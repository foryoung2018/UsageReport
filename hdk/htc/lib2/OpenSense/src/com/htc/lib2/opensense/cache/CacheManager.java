package com.htc.lib2.opensense.cache;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

import javax.crypto.Cipher;

import com.htc.lib2.opensense.cache.StorageManager.StorageInfo;
import com.htc.lib2.opensense.cache.TaskManager.TaskExecutor;
import com.htc.lib2.opensense.cache.TaskManager.TaskInfo;
import com.htc.lib2.opensense.cache.TaskManager.UriPrefix;
import com.htc.lib2.opensense.facedetect.FaceDetectTask;
import com.htc.lib2.opensense.facedetect.FaceDetectTaskHelper;
import com.htc.lib2.opensense.internal.SystemWrapper.Environment;
import com.htc.lib2.opensense.internal.SystemWrapper.HtcBuildFlag;
import com.htc.lib2.opensense.internal.SystemWrapper.HttpLinkConverter;
import com.htc.lib2.opensense.internal.SystemWrapper.SWLog;
import com.htc.lib2.opensense.internal.SystemWrapper.SystemProperties;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

/**
 * Help to cache and maintain files downloaded from remote service. CacheManager
 * uses a FIFO (First in First out) queue to execute the task. The newer the task
 * is added, it will be executed early in the same CacheManager instance.
 * Application should specify a writable directory for Download.
 * 
 * @hide
 */
public class CacheManager {

    public static enum Scheme {
        FILE("file"),
        FILE_ENCRYPTED("file+enc"),
        HTTP("http"),
        HTTPS("https");

        private String v;

        private Scheme(String value) {
            v = value;
        }

        @Override
        public String toString() {
            return v;
        }
    }

    private static final boolean TICKET_MODE = false;
    // private static final boolean TICKET_MODE = true;
    private static final boolean TIME_SHOULD_BE_APPENDED = SystemProperties.getBoolean("com.htc.opensense.DownloadTime", false); // Flag to use add download time in uri.
    private static final long LOCAL_FILES_KEEP_TIME = 7 * 24 * 60 * 60 * 1000L; // 7 days
    private static final Object LOCK_DATABASE = new Object(); // For synchronized write and read database
    private static final Object LOCK_TASK_HOLD = new Object();
    private static final Object LOCK_TASK_RELEASE = new Object();
    private static final String TAG = CacheManager.class.getSimpleName();

    private static CacheManager sCacheManager = null;

    private final ArrayList<Integer> mDownloadList = new ArrayList<Integer>();
    private final AtomicInteger mTaskId = new AtomicInteger(1);
    private final ConcurrentMap<Integer, Object> mUrlPool = new ConcurrentHashMap<Integer, Object>();
    private final ConcurrentMap<Integer, DownloadFutureTask> mTaskMap = new ConcurrentHashMap<Integer, DownloadFutureTask>();
    private final SparseArray<HashMap<Integer, DownloadCallback>> mCallbackListSparseArray = new SparseArray<HashMap<Integer, DownloadCallback>>();
    private final TaskExecutor mExecutorHelper;

    private Context mContext = null;
    private FaceDetectTaskHelper mFDTaskHelper = null;
    private PowerManager mPowerManager = null;
    private TicketManager mTicketManager = null;

    /**
     * Get an instance of {@link CacheManager} when setting folder, if
     * {@link CacheManager} has been initiate, it will return the instance.
     * 
     * Please add below permission :
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * <uses-permission android:name="android.permission.WAKE_LOCK" />
     * 
     * @param context
     *            through which it can access the current theme, resources, etc.
     * @return the CacheManager instance
     * 
     * @hide
     */
    public static CacheManager init(Context context) {
        if ( sCacheManager == null ) {
            synchronized (CacheManager.class) {
                if ( sCacheManager == null ) { // double check
                    sCacheManager = new CacheManager(context);
                }
            }
        }
        return sCacheManager;
    }

    private CacheManager(Context context) {
        if ( mPowerManager == null ) {
            mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        }
        if ( context != null ) {
            mContext = context.getApplicationContext();
        }
        mExecutorHelper = new TaskExecutor();

        if ( TICKET_MODE ) {
            mTicketManager = TicketManager.init(mContext);
        } else {
            mFDTaskHelper = TaskManager.getFaceDetectTaskHelper();
        }
    }

    private static int getTotalFileSizeInDirFromDb(ContentProviderClient client, String dir) {
        SWLog.d(
                TAG,
                "select str = " + "select sum(" + Download.FILE_SIZE + ") from " + Download.DOWNLOAD_TB
                        + " where " + Download.STORE_FOLDER + " ='" + dir + "'"
        );
        Cursor c = null;
        int result = 0;
        try {
            if ( client != null ) {
                c = client.query(
                        Download.RAWQUERY_URI,
                        null,
                        "select sum(" + Download.FILE_SIZE + ") from " + Download.DOWNLOAD_TB
                                + " where " + Download.STORE_FOLDER + " ='" + dir + "'",
                        null,
                        null
                );
            } else {
                Log.w(TAG, "[getTotalFileSizeInDirFromDb] ContentProviderClient is null for uri: " + Download.RAWQUERY_URI.toString());
            }
            if ( c != null && c.moveToNext() ) {
                result = c.getInt(0);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in [getTotalFileSizeInDirFromDb] : " + e);
        } finally {
            if ( c != null ) {
                c.close();
            }
        }
        if ( result < 0 ) { // size check
            result = 0;
        }
        return result;
    }

    private static String getFileNameFromUri(String dir, String uri) {
        if ( dir == null || uri == null ) {
            return null;
        }
        return dir + "/" + uri.hashCode();
    }

    /**
     * Input a url to start downlaod, if download,
     * {@link DownloadCallback#onDownloadSuccess(Uri, Bundle)} will be executed,
     * otherwise, {@link DownloadCallback#onDownloadError(Exception, Bundle)}
     * will be executed After the task in add to the executor, task id will be return
     * 
     * @param url the will be download url
     * @param callback the callback that will be call when download success or fail
     * @param data  the bundle will be return when download success or fail
     * @return this download task id
     * 
     * @hide
     */
    public int downloadPhotoByUrl(String url, DownloadCallback callback, Bundle data) {
        return downloadPhotoByUrl(url, null, callback, data);
    }

    /**
     * Input a url and auth to start downlaod, if download,
     * {@link DownloadCallback#onDownloadSuccess(Uri, Bundle)} will be executed,
     * otherwise, {@link DownloadCallback#onDownloadError(Exception, Bundle)}
     * will be executed After the task in add to the executor, task id will be return
     * 
     * @param url the will be download url
     * @param authHeader use to set URLConnection request property
     * @param callback the callback that will be call when download success or fail
     * @param data the bundle will be return when download success or fail
     * @return this download task id
     * 
     * @hide
     */
    public int downloadPhotoByUrl(String url, String authHeader, DownloadCallback callback, Bundle data) {
        if ( data == null ) {
            data = new Bundle();
        }
        if ( !TextUtils.isEmpty(authHeader) ) {
            data.putString(Download.HTTP_HEADER_AUTHORIZATION, authHeader);
        }
        if ( TICKET_MODE ) {
            int ticketId = 0;
            if ( mTicketManager != null ) {
                ticketId = mTicketManager.generateNewTicket(url, callback, data);
            }
            return ticketId;
        } else {
            synchronized (mExecutorHelper) {
                int taskId = mTaskId.getAndIncrement();
                DownloadFutureTask task = new DownloadFutureTask(mContext, taskId, url, callback, data);
                if ( !mExecutorHelper.isShutDown() ) {
                    mExecutorHelper.execute(task);
                } else {
                    throw new IllegalArgumentException(
                            "Can't use a ThreadPoolExecutor which has been shutdown");
                }
                mTaskMap.putIfAbsent(task.downloadTaskId, task);
                return task.downloadTaskId;
            }
        }
    }

    /**
     * Stop the download task which
     * 
     * @param taskId
     *            : The id of task which is returned by
     *            {@link #downloadPhotoByUrl(String, DownloadCallback, Bundle)}
     * 
     * @hide
     */
    public void stopDownloadPhotoByTaskId(final int taskId) {
        if ( TICKET_MODE ) {
            if ( mTicketManager != null ) {
                mTicketManager.removeTicket(taskId);
            }
        } else {
            final DownloadFutureTask task = mTaskMap.remove(taskId);
            if ( task != null ) {
                CLog.d(TAG, "[Stop download] Url : " + TicketManager.getEncodedString(task.downloadTaskUrl) + ", hash url : " + task.downloadTaskUrlHash, true);
                task.cancel(true);
                new Thread() {
                        @Override
                        public void run() {
                            Bundle data = new Bundle();
                            Exception e = new Exception("stopDownloadPhotoByTaskId");
                            handleError(task.downloadTaskUrlHash, taskId, "stopDownloadPhotoByTaskId", data, e);
                        }
                }.start();
            }
        }
    }

    private static synchronized void checkDatabaseUriIsExist(Context context, StorageInfo storageInfo) throws RemoteException {
        ContentProviderClient providerClient = context.getContentResolver().acquireUnstableContentProviderClient(TaskManager.CACHE_PROVIDER_AUTHORITY);
        long maxSizeAllocated = storageInfo.getLimitSize();
        long mBottomBound = storageInfo.getRemainingLowerBound();
        String dir = StorageManager.getCacheDir(storageInfo);
        long remainSize = maxSizeAllocated - getTotalFileSizeInDirFromDb(providerClient, dir);
        CLog.d(TAG, "[checkDatabaseUriIsExist] remain size " + remainSize, false);
        if ( remainSize > mBottomBound ) {
            if ( providerClient != null ) {
                providerClient.release();
            }
            return;
        }
        CLog.d(TAG, "[checkDatabaseUriIsExist] remain size not enough, start check database uri is exist in local folder", true);
        Cursor c = null;
        try {
            if ( providerClient != null ) {
                c = providerClient.query(
                        Download.DOWNLOAD_CONTENT_URI,
                        new String[] {
                                Download._ID,
                                Download.FILE_SIZE,
                                Download.CONTENT_URI,
                                Download.IMG_URL_HASH
                        },
                        Download.STATUS + "=? AND " + Download.STORE_FOLDER + "=?",
                        new String[] {
                                TaskInfo.Status.SUCCESS.toString(),
                                dir
                        },
                        Download.LAST_MODIFIED_TIME + " ASC"
                );
                deleteCachedFileUriFromDb(context, c, providerClient);
            } else {
                CLog.w(TAG, "[checkDatabaseUriIsExist] ContentProviderClient is null for uri: " + Download.DOWNLOAD_CONTENT_URI.toString(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CLog.e(TAG, "Exception in [checkDatabaseUriIsExist] : " + e, true);
        } finally {
            if ( c != null ) {
                c.close();
            }
            if ( providerClient != null ) {
                providerClient.release();
            }
        }
    }

    private static synchronized void checkShouldDeleteData(Context context, StorageInfo storageInfo) throws RemoteException {
        ContentProviderClient providerClient = context.getContentResolver().acquireUnstableContentProviderClient(TaskManager.CACHE_PROVIDER_AUTHORITY);
        long maxSizeAllocated = storageInfo.getLimitSize();
        long mBottomBound = storageInfo.getRemainingLowerBound();
        long mUpperBound = storageInfo.getRemainingUpperBound();
        String dir = StorageManager.getCacheDir(storageInfo);
        long remainSize = maxSizeAllocated - getTotalFileSizeInDirFromDb(providerClient, dir);
        CLog.d(TAG, "[checkShouldDeleteData] remain size " + remainSize, false);
        if ( remainSize > mBottomBound ) {
            if ( providerClient != null ) {
                providerClient.release();
            }
            return;
        }
        CLog.d(TAG, "[checkShouldDeleteData] remain size not enough, start delete", true);
        Cursor c = null;
        try {
            if ( providerClient != null ) {
                c = providerClient.query(
                        Download.DOWNLOAD_CONTENT_URI,
                        new String[] {
                                Download._ID,
                                Download.FILE_SIZE,
                                Download.CONTENT_URI,
                                Download.IMG_URL_HASH
                        },
                        Download.STATUS + "=? AND " + Download.STORE_FOLDER + "=?",
                        new String[] {
                                TaskInfo.Status.SUCCESS.toString(),
                                dir
                        },
                        Download.LAST_MODIFIED_TIME + " ASC"
                );
                // the deletion operation should not be interrupted
                deleteCachedFileData(context, c, providerClient, remainSize, mUpperBound);
                // remove 7 days local db catch
                long currentTime = System.currentTimeMillis();
                long keepTime = currentTime - LOCAL_FILES_KEEP_TIME;
                providerClient.delete(
                        Download.DOWNLOAD_CONTENT_URI,
                        Download.STORE_FOLDER + " != ? and " +  Download.LAST_MODIFIED_TIME + " < ? ",
                        new String[] {
                                dir,
                                String.valueOf(keepTime)
                        }
                );
            } else {
                CLog.w(TAG, "[checkShouldDeleteData] ContentProviderClient is null for uri: " + Download.DOWNLOAD_CONTENT_URI.toString(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CLog.e(TAG, "Exception in [checkShouldDeleteData] : " + e, true);
        } finally {
            if ( c != null ) {
                c.close();
            }
            if ( providerClient != null ) {
                providerClient.release();
            }
        }
    }

    private static void deleteCachedFileUriFromDb(Context context, Cursor c, ContentProviderClient client) {
        if ( c == null ) {
            return;
        }
        try {
            StringBuilder deleteStr = new StringBuilder();
            boolean first = true;
            while ( c.moveToNext() ) {
                Uri uri = Uri.parse(c.getString(c.getColumnIndexOrThrow(Download.CONTENT_URI)));
                uri = UriPrefix.decode(context, uri);
                boolean isExist = checkUriIsExist(uri);
                CLog.d(TAG, "[deleteCachedFileUriFromDb] The uri is exist in local folder : " + isExist, false);
                if ( uri.toString().startsWith(ContentResolver.SCHEME_FILE) && !isExist ) {
                    if ( !first ) {
                        deleteStr.append(" OR ");
                    } else {
                        first = false;
                    }
                    deleteStr.append(Download._ID);
                    deleteStr.append("=");
                    deleteStr.append(c.getString(c.getColumnIndexOrThrow(Download._ID)));
                    if ( deleteStr.length() > TaskManager.DLENGTH ) {
                        CLog.d(TAG, "[deleteLocalUri] delete " + deleteStr.toString(), true);
                        if ( client != null ) {
                            client.delete(
                                    Download.DOWNLOAD_CONTENT_URI,
                                    deleteStr.toString(),
                                    null
                            );
                        } else {
                            CLog.w(TAG, "[deleteCachedFileUriFromDb] ContentProviderClient is null for uri: " + Download.DOWNLOAD_CONTENT_URI.toString(), true);
                        }
                        deleteStr.setLength(0);
                        first = true;
                    }
                }
            }
            if ( deleteStr.length() > 0 ) {
                CLog.d(TAG, "[deleteCachedFileUriFromDb] out of while delete " + deleteStr.toString(), true);
                if ( client != null ) {
                    client.delete(
                            Download.DOWNLOAD_CONTENT_URI,
                            deleteStr.toString(),
                            null
                    );
                } else {
                    CLog.w(TAG, "[deleteCachedFileUriFromDb] ContentProviderClient is null for uri: " + Download.DOWNLOAD_CONTENT_URI.toString(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CLog.e(TAG, "Exception in [deleteCachedFileUriFromDb] : " + e, true);
        }
    }

    private static void deleteCachedFileData(Context context, Cursor c, ContentProviderClient client,
            long remainSize, final long upperBound) throws RemoteException {
        if ( c == null ) {
            return;
        }
        try {
            StringBuilder deleteStr = new StringBuilder();
            boolean first = true;
            long lastTimestampToDelete = 0L;
            Uri lastUriToDelete = null;
            while ( c.moveToNext() ) {
                int size = c.getInt(c.getColumnIndexOrThrow(Download.FILE_SIZE));
                Uri uri = Uri.parse(c.getString(c.getColumnIndexOrThrow(Download.CONTENT_URI)));
                uri = UriPrefix.decode(context, uri);
                lastUriToDelete = uri;
                lastTimestampToDelete = Math.max(lastTimestampToDelete, StorageManager.getFileLastModified(lastUriToDelete));
                if ( uri.toString().startsWith(ContentResolver.SCHEME_FILE) && !deleteFileFromUri(uri) ) {
                    continue;
                }
                if ( upperBound < 0
                        || (remainSize = remainSize + size) < upperBound ) {
                    if ( !first ) {
                        deleteStr.append(" OR ");
                    } else {
                        first = false;
                    }
                    deleteStr.append(Download._ID);
                    deleteStr.append("=");
                    deleteStr.append(c.getString(c.getColumnIndexOrThrow(Download._ID)));
                    if ( deleteStr.length() > TaskManager.DLENGTH ) {
                        CLog.d(TAG, "[deleteData] delete " + deleteStr.toString(), true);
                        if ( client != null ) {
                            client.delete(
                                    Download.DOWNLOAD_CONTENT_URI,
                                    deleteStr.toString(),
                                    null
                            );
                        } else {
                            CLog.w(TAG, "[deleteData] ContentProviderClient is null for uri: " + Download.DOWNLOAD_CONTENT_URI.toString(), true);
                        }
                        deleteStr.setLength(0);
                        first = true;
                    }
                } else {
                    CLog.d(TAG, "[deleteData] remain size " + remainSize + " break", true);
                    break;
                }
                CLog.d(TAG, "[deleteData] remain size " + remainSize, true);
            }
            deleteFileAndOlderFilesByUriAndTimestamp(lastUriToDelete, lastTimestampToDelete);
            CLog.d(TAG, "[deleteData] out of while delete " + deleteStr.toString(), true);
            if ( deleteStr.length() > 0 ) {
                if ( client != null ) {
                    client.delete(
                            Download.DOWNLOAD_CONTENT_URI,
                            deleteStr.toString(),
                            null
                    );
                } else {
                    CLog.w(TAG, "[deleteData] ContentProviderClient is null for uri: " + Download.DOWNLOAD_CONTENT_URI.toString(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CLog.e(TAG, "Exception in [deleteData] : " + e, true);
        }
    }

    private static boolean deleteFileFromUri(Uri uri) {
        if ( uri == null ) {
            return true;
        }
        File file = new File(uri.getPath());
        if ( file.exists() ) {
            CLog.d(TAG, "[deleteFileFromUri] uri : " + uri.toString() + ", file exist " + file.getPath(), false);
            return file.delete();
        } else {
            return true;
        }
    }

    private static void deleteFileAndOlderFilesByUriAndTimestamp(Uri uri, long timestampToDelete) {
        if ( uri == null ) {
            return;
        }
        File file = new File(uri.getPath());
        long startTime = System.currentTimeMillis();
        long workingPeriod = 1000L * 5; // 5 sec
        File folder = file.getParentFile();
        if ( folder.isDirectory() ) {
            for (File f : folder.listFiles()) {
                if ( f == null ) {
                    continue;
                }
                long endTime = System.currentTimeMillis();
                long result = endTime - startTime;
                if ( result > workingPeriod || result < 0L ) {
                    SWLog.w(TAG, "[deleteFileAndOlderFilesByUri]: longer than " + workingPeriod + " ms." );
                    break;
                }
                if ( (timestampToDelete >= 0L) && (StorageManager.getFileLastModified(f) <= timestampToDelete) ) {
                    Log.w(TAG, "[deleteFileAndOlderFilesByUri]: " + f.getName());
                    f.delete();
                }
            }
        }
    }

    private static boolean checkUriIsExist(Uri uri) {
        if ( uri != null ) {
            File file = new File(uri.getPath());
            return file.exists();
        } else {
            return false;
        }
    }

    private static boolean isUriExist(Uri uri) throws RemoteException {
        boolean result = false;
        if ( uri == null || !"file".equals(uri.getScheme()) ) {
            return result;
        }
        CLog.d(TAG, "[isUriExist] check Uri " + uri.toString(), false);
        result = new File(uri.getPath()).exists();
        return result;
    }

    private static void closeStream(Closeable stream) {
        if ( stream != null ) {
            try {
                stream.close();
            } catch (IOException e) {
                CLog.e(TAG, "Could not close stream", e, true);
            }
        }
    }

    private static Pair<Uri, Long> saveToFileSystemAndCloseInputStream(Context context,
            InputStream is, String uri, File file, String currentCacheDir, boolean preencrypted) throws IOException {
        String fileName = getFileNameFromUri(currentCacheDir, uri);
        if ( fileName == null || is == null ) {
            closeStream(is);
            return null;
        }
        InputStream in = null;
        OutputStream out = null;
        Uri resultUri = null;
        long resultSize = 0L;
        String digestString = null;
        boolean isDone = false;
        IOException exp = null;
        try {
            if ( preencrypted ) {
                out = TaskManager.getFileOutputStream(file);
            } else {
                out = getFileOutputStream(context, Cipher.ENCRYPT_MODE, file);
            }
            if ( out == null ) {
                isDone = true;
            }

            if ( !isDone ) {
                in = new BufferedInputStream(is, 2048);
                CRC32 digester = new CRC32();
                final byte[] buf = new byte[2048];
                int readSize = -1;
                while ( (readSize = in.read(buf)) >= 0 ) {
                    digester.update(buf, 0, readSize);
                    out.write(buf, 0, readSize);
                }
                out.flush();
                digestString = "" + digester.getValue();
                CLog.d(TAG, "Download file size : " + file.length(), true);
                //File size check, if is 0, return null 
                if ( file.length() == 0 ) {
                    file.delete();
                    isDone = true;
                }
                CLog.d(TAG, "[Digest] url : " + TicketManager.getEncodedString(uri) + ", digest : " + digestString, true);
            }
        } catch (IOException e) {
            exp = e;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(is);
            closeStream(in);
            closeStream(out);
        }

        if ( exp != null ) {
            throw exp;
        }

        if ( !isDone ) {
            if ( !TaskManager.isValidImage(context, Uri.fromFile(file))) {
                file.delete();
                isDone = true;
            }
        }

        if ( !isDone ) {
            File targetFile = new File(fileName);
            if ( targetFile.exists() ) {
                file.delete();
            } else {
                boolean isSuccessRenaming = file.renameTo(targetFile);
                if ( !isSuccessRenaming ) {
                    CLog.e(TAG, "File rename fail.", true);
                }
            }
            resultUri = Uri.fromFile(targetFile);
            resultSize = targetFile.length();
            long blockSize = StorageManager.getBlockSizeLong(currentCacheDir);
            resultSize = (( resultSize / blockSize ) + 1) * blockSize;
        }

        return new Pair<Uri, Long>(resultUri, resultSize);
    }

    private static String insertStatusProgressToDbIfNotExist(Context context, int taskId, String url, String currentCacheDir) throws RemoteException, Exception{
        if ( context == null || url == null || currentCacheDir == null ) {
            return "";
        }
        ContentValues values = new ContentValues();
        values.put(Download.IMG_URL_HASH, taskId);
        values.put(Download.IMG_URL, url);
        values.put(Download.STATUS, TaskInfo.Status.PROGRESS.toValue());
        values.put(Download.STORE_FOLDER, currentCacheDir);

        ContentProviderClient providerClient = null;
        Cursor c = null;
        String result = "";
        boolean isDone = false;
        try {
            providerClient = context.getContentResolver().acquireUnstableContentProviderClient(TaskManager.CACHE_PROVIDER_AUTHORITY);
            if ( providerClient != null ) {
                c = providerClient.query(
                        Download.DOWNLOAD_CONTENT_URI,
                        new String[] {
                                Download._ID
                        },
                        Download.IMG_URL_HASH + "=? AND " + Download.STORE_FOLDER + "=?",
                        new String[] {
                                "" + taskId,
                                currentCacheDir
                        },
                        null
                );
                if ( c != null && c.moveToNext() ) {
                    result = c.getString(c.getColumnIndexOrThrow(Download._ID));
                    isDone = true;
                }
                if ( !isDone ) {
                    Uri resultUri = providerClient.insert(Download.DOWNLOAD_CONTENT_URI, values);
                    result = resultUri.getLastPathSegment();
                }
            } else {
                CLog.w(TAG, "[insertToDb] ContentProviderClient is null for insert : " + Download.DOWNLOAD_CONTENT_URI.toString(), true);
            }
        } finally {
            if ( c != null ) {
                c.close();
            }
            if ( providerClient != null ) {
                providerClient.release();
            }
        }
        if ( result != null ) {
            return result;
        }
        return "";
    }

    private static void deleteFromDb(Context context, String id) {
        if ( context == null ) {
            return;
        }
        Uri downloadUri = Uri.withAppendedPath(Download.DOWNLOAD_CONTENT_URI, id);
        ContentProviderClient providerClient = null;
        try {
            providerClient = context.getContentResolver().acquireUnstableContentProviderClient(TaskManager.CACHE_PROVIDER_AUTHORITY);
            if ( providerClient != null ) {
                providerClient.delete(downloadUri, null, null);
            } else {
                CLog.w(TAG, "[deleteFromDb] ContentProviderClient is null for delete: " + downloadUri.toString(), true);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            CLog.e(TAG, "Exception in [deleteFromDb] : " + e, true);
        } finally {
            if ( providerClient != null ) {
                providerClient.release();
            }
        }
    }

    private static void updateToDb(Context context, String id, String... update) {
        if ( context == null || update == null ) {
            return;
        }
        int size = update.length;
        if ( size % 2 != 0 ) {
            CLog.d(TAG, "[updateToDb] size = " + size, true);
            throw new RuntimeException();
        }
        ContentValues values = new ContentValues();
        for (int i = 0; i < size; i += 2) {
            values.put(update[i], update[i + 1]);
        }
        Uri downloadUri = Uri.withAppendedPath(Download.DOWNLOAD_CONTENT_URI, id);
        ContentProviderClient providerClient = null;
        try {
            providerClient = context.getContentResolver().acquireUnstableContentProviderClient(TaskManager.CACHE_PROVIDER_AUTHORITY);
            if ( providerClient != null ) {
                providerClient.update(downloadUri, values, null, null);
                context.getContentResolver().notifyChange(Download.DOWNLOAD_CONTENT_URI, null);
            } else {
                CLog.w(TAG, "[updateToDb] ContentProviderClient is null for update: " + downloadUri.toString(), true);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            CLog.e(TAG, "Exception in [updateToDb] : " + e, true);
        } finally {
            if ( providerClient != null ) {
                providerClient.release();
            }
        }
    }

    private TaskInfo getTaskInfoFromDb(int urlHash, String currentCacheDir, TaskInfo info, boolean isUpdateMode) {
        return getTaskInfoFromDb(mContext, urlHash, currentCacheDir, info, isUpdateMode);
    }

    private static TaskInfo getTaskInfoFromDb(Context context, int urlHash, String currentCacheDir, TaskInfo info, boolean isUpdateMode) {
        if ( info == null ) {
            info = new TaskInfo(TaskInfo.Status.FAIL);
        }
        if ( context == null || currentCacheDir == null ) {
            return info;
        }
        StringBuilder query = new StringBuilder();
        query.append(Download.IMG_URL_HASH).append("=").append(urlHash)
                .append(" AND (").append(Download.STORE_FOLDER).append("='")
                .append(currentCacheDir).append("'").append(")");
        ContentProviderClient providerClient = context.getContentResolver()
                .acquireUnstableContentProviderClient(TaskManager.CACHE_PROVIDER_AUTHORITY);
        Cursor c = null;
        try {
            if ( providerClient != null ) {
            c = providerClient.query(Download.DOWNLOAD_CONTENT_URI,
                    new String[] { Download._ID, Download.CONTENT_URI,
                            Download.STATUS }, query.toString(), null, null);
            } else {
                CLog.w(TAG, "[getTaskInfoFromDb] ContentProviderClient is null for query: " + Download.DOWNLOAD_CONTENT_URI.toString(), true);
            }
            if ( c != null ) {
                if ( c.moveToNext() ) {
                    info.applyStatus(c.getInt(c.getColumnIndexOrThrow(Download.STATUS)));

                    String uri = c.getString(c
                            .getColumnIndexOrThrow(Download.CONTENT_URI));
                    if ( !TextUtils.isEmpty(uri) ) {
                        uri = UriPrefix.decode(context, Uri.parse(uri)).toString();
                        info.applyResultUri(Uri.parse(uri));
                    }
                    info.applyId(c.getString(c.getColumnIndexOrThrow(Download._ID)));
                }
            }
            if ( info.getStatus() == TaskInfo.Status.SUCCESS ) {
                if ( isUpdateMode ) {
                    if ( !isUriExist(info.getResultUri()) ) {
                        info.applyStatus(TaskInfo.Status.PROGRESS); 
                    }
                } else {
                    if ( !isUriExist(info.getResultUri()) || !TaskManager.isValidImage(context, info.getResultUri()) ) {
                        deleteFileFromUri(info.getResultUri());
                        deleteFromDb(context, info.getId());
                        info.applyStatus(TaskInfo.Status.FAIL);
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) { 
            e.printStackTrace();
            CLog.e(TAG, "Exception in [getTaskInfoFromDb] : " + e, true);
        } finally {
            if ( c != null ) {
               c.close();
            }
            if ( providerClient != null ) {
                providerClient.release();
            }
        }

        return info;
    }

    @SuppressLint("UseSparseArrays")
    private void addCallBack(int taskId, int targetId, DownloadCallback callback) {
        synchronized (mCallbackListSparseArray) {
            HashMap<Integer, DownloadCallback> callbackList = getCallbackListWithLock(targetId);
            if ( callbackList == null ) {
                callbackList = new HashMap<Integer, DownloadCallback>();
                mCallbackListSparseArray.put(targetId, callbackList);
            }
            callbackList.put(taskId, callback);
        }
    }

    private HashMap<Integer, DownloadCallback> removeCallbackListWithLock(int targetId) {
        HashMap<Integer, DownloadCallback> callbackList = mCallbackListSparseArray.get(targetId);
        mCallbackListSparseArray.remove(targetId);
        return callbackList;
    }

    private HashMap<Integer, DownloadCallback> getCallbackListWithLock(int targetId) {
        return mCallbackListSparseArray.get(targetId);
    }

    private class DownloadFutureTask extends FutureTask<TaskInfo> implements Comparable<DownloadFutureTask> {
        int downloadTaskId;
        int downloadTaskUrlHash;
        String downloadTaskUrl;
        Bundle downloadTaskData;

        /**
         * @hide
         */
        public DownloadFutureTask(Context context, int id, String url, DownloadCallback callback, Bundle data) {
            super(new ImageCallable(context, url, data, id));
            downloadTaskUrl = url;
            if ( downloadTaskUrl == null ) {
                downloadTaskUrl = "";
            }
            downloadTaskUrlHash = downloadTaskUrl.hashCode();
            downloadTaskId = id;
            CLog.d(TAG, "add callback : " + downloadTaskUrlHash + ", url : " + TicketManager.getEncodedString(downloadTaskUrl), true);
            addCallBack(downloadTaskId, downloadTaskUrlHash, callback);
            downloadTaskData = data;
        }

        /**
         * @hide
         */
        @Override
        protected void done() {
            CLog.d(TAG,"In done : " + downloadTaskUrlHash, true);
            try {
                mTaskMap.remove(downloadTaskId);
                boolean isFacedetect = false;
                if ( downloadTaskData != null ) {
                    isFacedetect = downloadTaskData.getBoolean(Download.FACE_DETECT, false);
                }
                TaskInfo taskInfo = get();
                if ( taskInfo != null ) {
                    if ( taskInfo.getStatus() == TaskInfo.Status.PROGRESS ) {
                        CLog.d(TAG,"in done check status is progress", false);
                        CLog.d(TAG, "before query db info : " + taskInfo.getStatus().toValue(), false);
                        taskInfo = getTaskInfoFromDb(downloadTaskUrlHash, StorageManager.getCacheDir(mContext), taskInfo, true);
                        CLog.d(TAG, "after query db info : " + taskInfo.getStatus().toValue(), false);
                    }
                }
                if ( taskInfo != null ) {
                    if ( taskInfo.getStatus() == TaskInfo.Status.SUCCESS ) {
                        if ( !TICKET_MODE ) {
                            String AOI = TaskManager.getUriQueryParameter(taskInfo.getFullResultUri(), "AOI");
                            if ( AOI == null && isFacedetect ) {
                                int [] faceRect = null;
                                int [] outSize = new int [2];
                                try {
                                    CLog.d(TAG, "[done] HashUrl : " + downloadTaskUrlHash + ", isFacedetect : " + isFacedetect, true);
                                    //Facedetect muti thread, task stop in app call release()
                                    //Because EagleEye catch thread leak, so follow Wj Lee, 
                                    //use FaceDetectTask task = FaceDetectTask.new_task(0) and don't need to lockTask(). 
                                    FaceDetectTask task = null;                                   
                                    if(FaceDetectTask.IsOmronEnable)
                                    {
                                       task = FaceDetectTask.new_task(0);
                                       if(task == null)
                                    	   task = FaceDetectTask.new_task_google(0);
                                    }
                                    else 
                                    	task = FaceDetectTask.new_task_google(0);
                                    
//                                    synchronized (LOCK_TASK_HOLD) {
//                                        if ( mFDTaskHelper != null ) {
//                                            task = mFDTaskHelper.lockTask();
//                                        }
//                                    }
                                    if ( task != null ) {
                                        if ( StorageManager.isEncrypted() ) {
                                            task.setEncryptionKey(StorageManager.getEncryptionKey(mContext));
                                        }
                                        faceRect = task.fd_wait(taskInfo.getResultUri(), outSize);
                                        task.stop();                                       
//                                        synchronized (LOCK_TASK_RELEASE) {
//                                            if ( mFDTaskHelper != null ) {
//                                                mFDTaskHelper.unlockTask(task);
//                                            }
//                                        }
                                        //check the picture is invalid image
                                        if ( outSize[0] == -1 || outSize[1] == -1 ) {
                                            Exception e = new Exception("Invalid image.");
                                            handleError(downloadTaskUrlHash, downloadTaskId, "InvalidImageCheck", downloadTaskData, e);
                                            deleteFileFromUri(taskInfo.getResultUri());
                                            CLog.e(TAG, "Image bounds is -1", true);
                                            return;
                                        } else if ( outSize[0] == 0 || outSize[1] == 0 ) {
                                            outSize = TaskManager.getImageBounds(mContext, taskInfo.getResultUri());
                                            if ( outSize[0] == -1 || outSize[1] == -1 ) {
                                                Exception e = new Exception("Invalid image.");
                                                handleError(downloadTaskUrlHash, downloadTaskId, "InvalidImageCheck", downloadTaskData, e);
                                                deleteFileFromUri(taskInfo.getResultUri());
                                                CLog.e(TAG, "Image bounds is 0", true);
                                                return;
                                            }
                                        }
                                    }
                                    else {
                                        Log.e(TAG, "Error No Task");
                                        faceRect = null;
                                        outSize = null;
                                    }
                                } catch (Exception e) {
                                    CLog.d(TAG,"[done] Face detect exception.", true);
                                    outSize = null;
                                    e.printStackTrace();
                                }
                                taskInfo.applyResultUriParam("AOI", TaskManager.getAoiValueString(faceRect));
                                if ( outSize != null && outSize.length == 2 ) {
                                    taskInfo.applyResultUriParam("width", "" + outSize[0]).applyResultUriParam("height", "" + outSize[1]);
                                }
                                Uri fullResultUri = taskInfo.getFullResultUri();
                                CLog.d(TAG, "[done] update to db, " + fullResultUri, false);
                                Uri uri = UriPrefix.encode(mContext, fullResultUri);
                                updateToDb(mContext, taskInfo.getId(), Download.CONTENT_URI, uri.toString());
                            }
                        }

                        /*
                         * This for solved dead lock issue.
                         * If APP synchronized their onDownloadsuccess, will have a little rate to block CacheManager thread.
                         * So new another thread to send download success callback to avoid dead lock block all download thread.
                         */
                        HashMap<Integer, DownloadCallback> callbackTempList = null;
                        synchronized (mCallbackListSparseArray) {
                            callbackTempList = removeCallbackListWithLock(downloadTaskUrlHash);
                        }
                        final HashMap<Integer, DownloadCallback> callbackList = callbackTempList;
                        final Uri threadUri = taskInfo.getFullResultUri();
                        new Thread() {
                                @Override
                                public void run() {
                                    if ( callbackList != null ) {
                                        Collection<DownloadCallback> collection = callbackList.values();
                                        Iterator<DownloadCallback> itrs = collection.iterator();
                                        while ( itrs.hasNext() ) {
                                            DownloadCallback callback = itrs.next();
                                            CLog.d(TAG,
                                                    "on Download Success callback : "
                                                            + threadUri, true);
                                            callback.onDownloadSuccess(threadUri, downloadTaskData);
                                        }
                                        callbackList.clear();
                                    }
                                }
                        }.start();

                        long fileUpdateTime = System.currentTimeMillis();
                        StorageManager.setFileLastModified(taskInfo.getResultUri(), fileUpdateTime);
                        synchronized (LOCK_DATABASE) {
                            updateToDb(mContext, taskInfo.getId(), Download.LAST_MODIFIED_TIME,
                                    String.valueOf(fileUpdateTime));
                        }
                        synchronized (mDownloadList) {
                            if ( mDownloadList.contains(downloadTaskUrlHash) ) {
                                //is a trick
                                CLog.d(TAG, "Remove download success : "+downloadTaskUrlHash, false);
                                mDownloadList.remove((Object)downloadTaskUrlHash);
                            }
                        }
                    } else if ( taskInfo.getStatus() == TaskInfo.Status.FAIL ) {
                        CLog.d(TAG,"Download fail in task end.", true);
                        throw new ExecutionException("Download fail",
                                new Throwable());
                    }
                }

            } catch (InterruptedException e) {
                CLog.d(TAG, "User interrupt task " + downloadTaskUrlHash, true);
                handleError(downloadTaskUrlHash, downloadTaskId, "InterruptedException", downloadTaskData, e);
            } catch (ExecutionException e) {
                CLog.d(TAG,"ExecutionException : " + e +" ExecutionException hash : " + downloadTaskUrlHash, true);
                e.printStackTrace();
                handleError(downloadTaskUrlHash, downloadTaskId, "InterruptedException", downloadTaskData, e);
            } catch (CancellationException e) {
                CLog.d(TAG, "User CancellationException " + e +" CancellationException hash : " + downloadTaskUrlHash, true);
                handleError(downloadTaskUrlHash, downloadTaskId, "CancellationException", downloadTaskData, e);
            } finally {
                synchronized (mUrlPool) {
                    mUrlPool.remove(downloadTaskUrlHash);
                }
            }
        }

        /**
         * @hide
         */
        @Override
        public int compareTo(DownloadFutureTask other) {
            return downloadTaskId < other.downloadTaskId ? -1 : 1;
        }

    }

    private class ImageCallable implements Callable<TaskInfo> {
        String imageUrl = null;
        int imageUrlHash;
        boolean imageIsUsingFaceDetect;
        //If url has AOI information, use this AOI and didn't execute facedetect.
        boolean imageHasAOI;
        String imageParamAOI ;
        Context imageContext = null;
        String imageWidth = null;
        String imageHeight = null;
        int imageTaskId;
        Bundle imageBundleData;

        /**
         * @param id 
         * @hide
         */
        public ImageCallable(Context context, String url, Bundle data, int id) {
            imageContext = context;
            imageUrl = url;
            if ( imageUrl == null ) {
                imageUrl = "";
            }
            imageTaskId = id;
            imageBundleData = data;
            if ( imageBundleData == null ) {
                imageBundleData = new Bundle();
            }
            Uri uri = Uri.parse(imageUrl);
            imageParamAOI = TaskManager.getParamAoi(uri);
            imageHasAOI = imageParamAOI != null;
            imageWidth = TaskManager.getUriQueryParameter(uri, "width");
            imageHeight = TaskManager.getUriQueryParameter(uri, "height");
            imageUrlHash = imageUrl.hashCode();
            if ( data != null ) {
                imageIsUsingFaceDetect = data.getBoolean(Download.FACE_DETECT, false);
            } else {
                imageIsUsingFaceDetect = false;
            }
        }

        /**
         * @hide
         */
        @Override
        public TaskInfo call() throws Exception {
            CLog.d(TAG,"in Info call start : " + imageUrlHash, false);
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
            TaskInfo taskInfo = null;
            String currentCacheDir = StorageManager.getCacheDir(mContext);
            List<StorageInfo> storageInfos = StorageManager.getAllStorages(imageContext, Environment.MEDIA_MOUNTED);
            boolean hasHashUrl = true;
            synchronized (mUrlPool) {
                if ( mUrlPool.containsKey(imageUrlHash) ) {
                    CLog.d(TAG,"in mPoolUrls contain imageUrlHash ", false);
                    taskInfo = new TaskInfo(TaskInfo.Status.PROGRESS);
                    return taskInfo;
                } else {
                    mUrlPool.putIfAbsent(imageUrlHash, imageUrlHash);
                }
            }
            if ( !StorageManager.prepareCacheDir(storageInfos) ) {
                Exception e = new Exception("Can't create cache folder.");
                handleError(imageUrl.hashCode(), imageTaskId, "checkPathExist", imageBundleData, e);
                return taskInfo;
            }

            synchronized(LOCK_DATABASE){
                CLog.d(TAG,"in DATABASE_LOCK ", false);
                try {
                    CLog.d(TAG, "before get Info", false);
                    taskInfo = getTaskInfoFromDb(imageUrlHash, currentCacheDir, null, false);
                    CLog.d(TAG, "after get Info", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if ( taskInfo.getStatus() == TaskInfo.Status.FAIL ) {
                    try {
                        CLog.d(TAG, "before insert to DB", false);
                        taskInfo.applyId(insertStatusProgressToDbIfNotExist(imageContext, imageUrlHash, imageUrl, currentCacheDir));
                        CLog.d(TAG, "after insert to DB", false);
                    } catch (Exception e) {
                        CLog.d(TAG,"insert db exception : " + e, true);
                    }
                }
            }

            boolean isCheckOnly = imageBundleData.getBoolean(Download.CHECK_ONLY);
            if ( isCheckOnly ) {
                if ( taskInfo.getStatus() != TaskInfo.Status.SUCCESS ) {
                    Exception e = new Exception("The image is not cached for check-only mode.");
                    throw e;
                }
            }

            /* if ( info.status == Download.STATUS_PROGRESS ) {
                deleteFromDb(info.id);
                info.status = Download.STATUS_FAIL;
            } */

            synchronized(mDownloadList) {
                if ( taskInfo.getStatus() == TaskInfo.Status.FAIL ) {
                    CLog.d(TAG, "add to on downloadlist : " + imageUrlHash + ", url : " + "...", false);
                    mDownloadList.add(imageUrlHash);
                } else if ( taskInfo.getStatus() == TaskInfo.Status.PROGRESS ) {
                    hasHashUrl = mDownloadList.contains(imageUrlHash);
                }
            }

            if ( taskInfo.getStatus() == TaskInfo.Status.FAIL ) {
                taskInfo = downloadTargetFile(taskInfo, storageInfos, false);
            } else if ( taskInfo.getStatus() == TaskInfo.Status.PROGRESS && !hasHashUrl ) {
                taskInfo = downloadTargetFile(taskInfo, storageInfos, true);
            }
            CLog.d(TAG,"in Info call end ", false);
            return taskInfo;
        }

        private TaskInfo downloadTargetFile(TaskInfo taskInfo, List<StorageInfo> storageInfos,
                boolean isMultiDownload) throws Exception {
            StorageInfo storageInfo = storageInfos.get(0);
            if ( TaskManager.isSameScheme(imageUrl, Scheme.FILE) ) {
                taskInfo = getFileSchemeTargetFile(imageContext, taskInfo, imageUrl, imageTaskId, imageBundleData, storageInfo, false);
            } else if ( TaskManager.isSameScheme(imageUrl, Scheme.FILE_ENCRYPTED) ) {
                taskInfo = getFileSchemeTargetFile(imageContext, taskInfo, imageUrl, imageTaskId, imageBundleData, storageInfo, true);
            } else {
                taskInfo = getHttpSchemeTargetFile(imageContext, taskInfo, imageUrl, imageTaskId, imageBundleData, storageInfo);
            }
            //this array have x,y,width,height
            int [] faceRect = null;
            int [] outSize = null;
            try {
                if ( !TICKET_MODE && imageIsUsingFaceDetect && !imageHasAOI ) { //For check doing face detect
                    if ( isMultiDownload ) {
                        CLog.d(TAG, "[muti download FaceDetect] url " + "..." + ", HashUrl : " + imageUrlHash + ", isFacedetect : " + imageIsUsingFaceDetect, true);
                    } else {
                        CLog.d(TAG, "[FaceDetect] url " + "..." + ", HashUrl : " + imageUrlHash + ", isFacedetect : " + imageIsUsingFaceDetect, true);
                    }
                    //Facedetect muti thread, task stop in app call release()
                    outSize = new int[2];
                    FaceDetectTask task = null;                                   
                    if(FaceDetectTask.IsOmronEnable)
                    {
                       task = FaceDetectTask.new_task(0);
                       if(task == null)
                    	   task = FaceDetectTask.new_task_google(0);
                    }
                    else 
                    	task = FaceDetectTask.new_task_google(0);
//                    synchronized (LOCK_TASK_HOLD) {
//                        if ( mFDTaskHelper != null ) {
//                            task = mFDTaskHelper.lockTask();
//                        }
//                    }
                    if ( task != null ) {
                        if ( StorageManager.isEncrypted() ) {
                            task.setEncryptionKey(StorageManager.getEncryptionKey(mContext));
                        }
                        faceRect = task.fd_wait(taskInfo.getResultUri(), outSize);
                        task.stop();
//                        synchronized (LOCK_TASK_RELEASE) {
//                            if ( mFDTaskHelper != null ) {
//                                mFDTaskHelper.unlockTask(task);
//                            }
//                        }
                        //check the picture is invalid image
                        if ( outSize[0] == -1 || outSize[1] == -1 ) {
                            Exception e = new Exception("Invalid image.");
                            handleError(imageUrlHash, imageTaskId, "InvalidImageCheck", imageBundleData, e);
                            deleteFileFromUri(taskInfo.getResultUri());
                            CLog.e(TAG, "Image bounds is -1", true);
                            return null;
                        } else if ( outSize[0] == 0 || outSize[1] == 0 ) {
                            outSize = TaskManager.getImageBounds(imageContext, taskInfo.getResultUri());
                            if ( outSize[0] <= 0 || outSize[1] <= 0 ) {
                                Exception e = new Exception("Invalid image.");
                                handleError(imageUrlHash, imageTaskId, "InvalidImageCheck", imageBundleData, e);
                                deleteFileFromUri(taskInfo.getResultUri());
                                CLog.e(TAG, "Image bounds is 0", true);
                                return null;
                            }
                        }
                    }
                    else {
                        Log.e(TAG, "Error No Task");
                        faceRect = null;
                        outSize = null;
                    }
                    if ( faceRect == null ) {
                        outSize = null;
                        CLog.d(TAG, "faceRect is null", true);
                    }
                    taskInfo.applyResultUriParam("AOI", TaskManager.getAoiValueString(faceRect));
                    if ( outSize != null && outSize.length == 2 ) {
                        taskInfo.applyResultUriParam("width", "" + outSize[0]).applyResultUriParam("height", "" + outSize[1]);
                    }
                } else {
                    if ( !TaskManager.isValidImage(imageContext, taskInfo.getResultUri()) ) {
                        Exception e = new Exception("Invalid image.");
                        handleError(imageUrlHash, imageTaskId, "InvalidImageCheck", imageBundleData, e);
                        deleteFileFromUri(taskInfo.getResultUri());
                        CLog.e(TAG, "Image bounds is -1", true);
                        return null;
                    }
                }
            } catch (Exception e) {
                if ( isMultiDownload ) {
                    CLog.d(TAG, "[multi download] Face detect exception.", true);
                } else {
                    CLog.d(TAG, "Face detect exception.", true);
                }
                handleError(imageUrlHash, imageTaskId, "InvalidImageCheck", imageBundleData, e);
                outSize = null;
                e.printStackTrace();
            }
            taskInfo.applyStatus(TaskInfo.Status.SUCCESS);
            //If url has AOI information, then did not execute face detect
            if ( imageHasAOI ) {
                taskInfo.applyResultUriParam("AOI", imageParamAOI)
                        .applyResultUriParam("width", imageWidth).applyResultUriParam("height", imageHeight);
            }
            if ( isMultiDownload ) {
                Uri fullResultUri = taskInfo.getFullResultUri();
                CLog.d(TAG, "[multi download] update to db, " + fullResultUri, false);
                CLog.d(TAG, "[multi download Download Time] uri : " + fullResultUri +  ", url : " + "..." + ", download time : " + taskInfo.getDuration() + "s", true);
            } else {
                Uri fullResultUri = taskInfo.getFullResultUri();
                CLog.d(TAG, "update to db, " + fullResultUri, false);
                CLog.d(TAG, "[Download Time] uri : " + fullResultUri +  ", url : " + "..." + ", download time : " + taskInfo.getDuration() + "s", true);
            }
            if ( TIME_SHOULD_BE_APPENDED ) {
                taskInfo = TaskManager.applyDurationIntoTaskInfo(taskInfo, taskInfo.getDuration());
            }
            Uri uri = UriPrefix.encode(mContext, taskInfo.getFullResultUri());
            updateToDb(imageContext, taskInfo.getId(), Download.CONTENT_URI, uri.toString(),
                    Download.STATUS,
                    TaskInfo.Status.SUCCESS.toString(),
                    Download.FILE_SIZE, String.valueOf(taskInfo.getFileSize()));
            if ( isMultiDownload ) {
                CLog.d(TAG, "[multi download] return " + taskInfo.getFullResultUri(), false);
            } else {
                CLog.d(TAG, "return " + taskInfo.getFullResultUri(), false);
            }
            return taskInfo;
        }

        private TaskInfo getFileSchemeTargetFile(Context context, TaskInfo taskInfo, String uri, int taskId, Bundle data, StorageInfo storageInfo,
                boolean preencrypted) throws IOException, URISyntaxException, RemoteException {
            if ( context == null || uri == null || storageInfo == null ) {
                Log.e(TAG, "(context == null): " + (context == null) + ", (uri == null): " + (uri == null) + ", (storageInfo == null): " + (storageInfo == null));
                return null;
            }
            if ( preencrypted ) {
                if ( uri.startsWith(Scheme.FILE_ENCRYPTED.toString()) ) {
                    // uri = uri.replace("file+enc:/", "file:/");
                    uri = Scheme.FILE.toString() + uri.substring(Scheme.FILE_ENCRYPTED.toString().length());
                }
            }
            File localUriFile = new File(new URI(uri));
            if ( !localUriFile.exists() ) {
                String path = null;
                path = TicketManager.getEncodedString(uri);
                FileNotFoundException e = new FileNotFoundException(path + " not found");
                CLog.e(TAG, "Exception when download file : " + e, true);
                handleError(uri.hashCode(), taskId, "Exception", data, e);
                throw e;
            }

            checkDatabaseUriIsExist(context, storageInfo);
            checkShouldDeleteData(context, storageInfo);
            CLog.d(TAG, "[Start check local file] , url " + "..." + ", hashcode : " +  uri.hashCode(), false);
            String currentCacheDir = StorageManager.getCacheDir(storageInfo);
            long startTime = System.currentTimeMillis();
            String tempFile = StorageManager.getTempFilePathFromUri(storageInfo, uri, this.hashCode(), System.currentTimeMillis());
            try {
                StorageManager.checkFolderExist(StorageManager.getTempDir(currentCacheDir));
                File file = new File(tempFile);
                if ( !file.exists() ) {
                    if ( !file.createNewFile() ) {
                        throw new IOException("Cannot create file");
                    }
                }

                InputStream is = new FileInputStream(localUriFile);
                Pair<Uri, Long> result = saveToFileSystemAndCloseInputStream(mContext, is, uri, file, currentCacheDir, preencrypted);
                Uri saveTo = null;
                long photoSize = 0L;
                if ( result != null ) {
                    saveTo = result.first;
                    photoSize = result.second;
                }
                float duration = ( (float)(System.currentTimeMillis() - startTime) ) / 1000;
                return taskInfo.applyResultUri(saveTo).applyFileSize(photoSize).applyDuration(duration);
            } catch (IOException e) {
                File file = new File(tempFile);
                if ( file.exists() ) {
                    file.delete();
                }
                String message = e.getMessage();
                if ( message != null && message.contains("ENOSPC") ) { // get "No space left" exception
                    StorageManager.deleteOldFiles(currentCacheDir);
                }
                throw e;
            }
        }

        private TaskInfo getHttpSchemeTargetFile(Context context, TaskInfo taskInfo, String uri, int taskId, Bundle data,
                StorageInfo storageInfo) throws IOException, URISyntaxException, RemoteException, Exception {
            if ( context == null || uri == null || storageInfo == null ) {
                Log.e(TAG, "(context == null): " + (context == null) + ", (uri == null): " + (uri == null) + ", (storageInfo == null): " + (storageInfo == null));
                return null;
            }
            checkDatabaseUriIsExist(context, storageInfo);
            checkShouldDeleteData(context, storageInfo);
            CLog.d(TAG, "[Start download file] url " + "..." + ", hashcode : " +  uri.hashCode(), false);

            uri = HttpLinkConverter.getAsciiLink(uri);
            if ( uri == null ) {
                Log.e(TAG, "uri is invalid");
                return null;
            }

            final URL imageUrl = new URI(uri).toURL();
            int fail_count = 0;
            String currentCacheDir = StorageManager.getCacheDir(storageInfo);

            if ( mPowerManager == null ) {
                mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            }
            PowerManager.WakeLock wakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TaskManager.CACHEMANAGER_WAKELOCK);
            if ( wakeLock != null ) {
                wakeLock.acquire();
            }
            try {
                while ( true ) {
                    HttpURLConnection connection = null;
                    long startTime = System.currentTimeMillis();
                    //Download file in temp file, after download success move temp file to target folder, then delete temp file
                    String tempFile = StorageManager.getTempFilePathFromUri(storageInfo, uri, this.hashCode(), System.currentTimeMillis());
                    try {
                        StorageManager.checkFolderExist(StorageManager.getTempDir(currentCacheDir));
                        File file = new File(tempFile);
                        if ( !file.exists() ) {
                            if ( !file.createNewFile() ) {
                                throw new IOException("Cannot create file");
                            }
                        }
                        connection = TaskManager.getConnectedHttpURLConnection(imageUrl, data, TaskManager.HTTP_MAX_REDIRECT);
                        InputStream is = connection.getInputStream();
                        Pair<Uri, Long> result = saveToFileSystemAndCloseInputStream(mContext, is, uri, file, currentCacheDir, false);
                        Uri saveTo = null;
                        long photoSize = 0L;
                        if ( result != null ) {
                            saveTo = result.first;
                            photoSize = result.second;
                        }
                        float duration = ( (float)(System.currentTimeMillis() - startTime) ) / 1000;
                        return taskInfo.applyResultUri(saveTo).applyFileSize(photoSize).applyDuration(duration);
                    } catch (IOException e) {
                        File file = new File(tempFile);
                        if ( file.exists() ) {
                            file.delete();
                        }
                        String message = e.getMessage();
                        if ( message != null && message.contains("ENOSPC") ) { // get "No space left" exception
                            StorageManager.deleteOldFiles(currentCacheDir);
                        }
                        fail_count++;
                        if ( fail_count > TaskManager.HTTP_MAX_RETRY_GENERAL ) {
                            handleError(uri.hashCode(), taskId, "IOException",
                                    data, e);
                            throw e;
                        }
                        CLog.e(TAG,
                                "IOException when download file : "
                                        + uri.hashCode() + " e : " + e, true);
                        if ( connection != null ) {
                            CLog.e(TAG, "Response code: "
                                    + connection.getResponseCode()
                                    + ", hash uri : " + uri.hashCode(), true);
                        }

                    } catch (Exception e) {
                        CLog.e(TAG, "Exception when download file : " + e, true);
                        handleError(uri.hashCode(), taskId, "Exception", data, e);
                        throw e;
                    } finally {
                        if ( connection != null ) {
                            connection.disconnect();
                        }
                    }
                }
            } finally {
                if ( wakeLock != null && wakeLock.isHeld() ) {
                    wakeLock.release();
                }
            }
        }
    }

    /**
     * stop all task and no longer to use this instance, if the
     * {@link CacheManager} is release, the next time the application want to
     * use this instance of the same directory, it will re-initialize it
     * 
     * @deprecated
     * @hide
     */
    @Deprecated
    public void release() {
    }

    private static class CLog {
        private static final boolean LOG_ENABLED = HtcBuildFlag.Htc_DEBUG_flag;
        private static final boolean CDebug = SystemProperties.getBoolean("com.htc.opensense.CDebug", false);

        @SuppressWarnings("unused")
        public static void v(String tag, String msg, boolean isAlways) {
            if ( isAlways ) {
                Log.v(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.v(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            }
        }

        @SuppressWarnings("unused")
        public static void v(String tag, String msg, Throwable tr, boolean isAlways) {
            if ( isAlways ) {
                Log.v(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.v(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            }
        }

        public static void d(String tag, String msg, boolean isAlways) {
            if ( isAlways ) {
                Log.d(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.d(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            }
        }

        @SuppressWarnings("unused")
        public static void d(String tag, String msg, Throwable tr, boolean isAlways) {
            if ( isAlways ) {
                Log.d(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.d(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            }
        }

        @SuppressWarnings("unused")
        public static void i(String tag, String msg, boolean isAlways) {
            if ( isAlways ) {
                Log.i(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.i(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            }
        }

        @SuppressWarnings("unused")
        public static void i(String tag, String msg, Throwable tr, boolean isAlways) {
            if ( isAlways ) {
                Log.i(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.i(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            }
        }

        public static void w(String tag, String msg, boolean isAlways) {
            if ( isAlways ) {
                Log.w(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.w(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            }
        }

        @SuppressWarnings("unused")
        public static void w(String tag, String msg, Throwable tr, boolean isAlways) {
            if ( isAlways ) {
                Log.w(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.w(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            }
        }

        @SuppressWarnings("unused")
        public static void w(String tag, Throwable tr, boolean isAlways) {
            if ( isAlways ) {
                Log.w(tag, "Thread name : " + Thread.currentThread().getName(), tr);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.w(tag, "Thread name : " + Thread.currentThread().getName(), tr);
            }
        }

        public static void e(String tag, String msg, boolean isAlways) {
            if ( isAlways ) {
                Log.e(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.e(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg);
            }
        }

        public static void e(String tag, String msg, Throwable tr, boolean isAlways) {
            if ( isAlways ) {
                Log.e(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            } else if ( LOG_ENABLED || CDebug ) {
                Log.e(tag, "Thread name : " + Thread.currentThread().getName() + " , " + msg, tr);
            }
        }
    }

    private void handleError(final int errorUrlHash, int errorTaskId, String errorFunctionName, final Bundle errorBundleData, final Exception e) {
        TaskInfo taskInfo = getTaskInfoFromDb(errorUrlHash, StorageManager.getCacheDir(mContext), null, false);
        if ( taskInfo != null && taskInfo.getId() != null
                && taskInfo.getStatus() != TaskInfo.Status.SUCCESS ) {
            if ( errorFunctionName.equals("CancellationException") || errorFunctionName.equals("stopDownloadPhotoByTaskId") ) {
                CLog.d(TAG, "["+errorFunctionName+"] change download status to fail : "+taskInfo.getId(), false);
                updateToDb(mContext, taskInfo.getId(), Download.STATUS, TaskInfo.Status.FAIL.toString());
            }
        }
        HashMap<Integer, DownloadCallback> callbackTempList = null;
        synchronized (mCallbackListSparseArray) {
            callbackTempList = removeCallbackListWithLock(errorUrlHash);
        }
        /*
         * This for solved dead lock issue.
         * If APP synchronized their onDownloadsuccess, will have a little rate to block CacheManager thread.
         * So new another thread to send download success callback to avoid dead lock block all download thread.
         */
        final HashMap<Integer, DownloadCallback> callbackList = callbackTempList;
        final Uri threadUri = taskInfo.getFullResultUri();
        new Thread() {
                @Override
                public void run() {
                    if ( callbackList != null ) {
                        Collection<DownloadCallback> collection = callbackList.values();
                        Iterator<DownloadCallback> itrs = collection.iterator();
                        while ( itrs.hasNext() ) {
                            DownloadCallback callback = itrs.next();
                            CLog.d(TAG, "on Download Error callback : " + threadUri
                                    + ", hash : " + errorUrlHash, true);
                            callback.onDownloadError(e, errorBundleData);
                        }
                        callbackList.clear();
                    }
                }
        }.start();
        synchronized (mDownloadList) {
            if ( mDownloadList.contains(errorUrlHash) ) {
                //is a trick
                CLog.d(TAG, "["+errorFunctionName+"] Remove download success : "+errorUrlHash, false);
                mDownloadList.remove((Object)errorUrlHash);
            }
        }
    }

    /**
     * @hide
     */
    public InputStream getInputStream(Uri uri) throws IOException {
        return StorageManager.getInputStream(mContext, uri);
    }

    public String getCipherDigest() {
        return StorageManager.getCipherDigest(mContext);
    }

    public static OutputStream getFileOutputStream(Context context, int cipherMode, File file) throws FileNotFoundException {
        return StorageManager.getFileOutputStream(context, cipherMode, file);
    }
}
