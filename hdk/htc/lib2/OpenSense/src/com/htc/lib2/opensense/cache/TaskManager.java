package com.htc.lib2.opensense.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import com.htc.lib2.opensense.cache.CacheManager.Scheme;
import com.htc.lib2.opensense.cache.StorageManager.StorageInfo;
import com.htc.lib2.opensense.facedetect.FaceDetectTask;
import com.htc.lib2.opensense.facedetect.FaceDetectTaskHelper;
import com.htc.lib2.opensense.internal.SystemWrapper.Environment;
import com.htc.lib2.opensense.internal.SystemWrapper.HttpLinkConverter;
import com.htc.lib2.opensense.internal.SystemWrapper.SWLog;

/**
 * @hide
 */
public class TaskManager {

    /**
     * @hide
     */
    public static final int DLENGTH = 800;

    /**
     * @hide
     */
    public static final int HTTP_MAX_RETRY_GENERAL = 1;

    /**
     * @hide
     */
    public static final int HTTP_MAX_REDIRECT = 5;

    /**
     * @hide
     */
    public static final int HTTP_TIMEOUT = 1000 * 60; // 1 min

    /**
     * @hide
     */
    public static final String CACHEMANAGER_WAKELOCK = "CacheManager_0";

    /**
     * @hide
     */
    public static final String CACHE_PROVIDER_AUTHORITY = Download.AUTHORITY /* "com.htc.cachemanager" */;

    /**
     * @hide
     */
    public static final String HTTP_HEADER_KEY_AUTHORIZATION = "Authorization";

    private static final String LOG_TAG = TaskManager.class.getSimpleName();
    private static final String NOMEDIA = ".nomedia";
    private static final String URI_EMPTY = "empty://uri";
    private static final Object LOCK_DATABASE_MODIFICATION = new Object();
    private static final Object LOCK_TASK_HOLD = new Object();
    private static final Object LOCK_TASK_RELEASE = new Object();

    private static TaskManager sTaskManager = null;
    private static FaceDetectTaskHelper sFaceDetectTaskHelper = null;

    private AtomicInteger mTaskIdGen = new AtomicInteger(1);
    private Context mContext = null;
    private TaskExecutor mTaskExecutor = null;
    private TaskList mTaskList = null;

    /**
     * @hide
     */
    public static TaskManager init(Context context) {
        if ( sTaskManager == null ) {
            synchronized (TaskManager.class) {
                if ( sTaskManager == null ) { // double check
                    sTaskManager = new TaskManager(context);
                }
            }
        }
        return sTaskManager;
    }

    /**
     * @hide
     */
    public TaskManager(Context context) {
        mContext = context;
        mTaskList = new TaskList();
        mTaskExecutor = new TaskExecutor();
    }

    /**
     * @hide
     */
    public Task generateNewTask(String url, TaskCallback taskCallback) {
        Bundle data = new Bundle();
        return generateNewTask(url, taskCallback, data);
    }

    /**
     * @hide
     */
    public Task generateNewTask(String url, TaskCallback taskCallback, Bundle data) {
        Task task = null;
        if ( mTaskList == null ) {
            return task;
        }
        int taskId = mTaskIdGen.getAndIncrement();
        synchronized (mTaskList) {
            task = new Task(
                    mContext,
                    this,
                    taskId,
                    url,
                    taskCallback,
                    getFaceDetectTaskHelper(),
                    data
            );
            mTaskList.putById(taskId, task);
        }
        return task;
    }

    /**
     * @hide
     */
    public Task removeTask(int taskId) {
        Task task = null;
        if ( mTaskList == null ) {
            return task;
        }
        synchronized (mTaskList) {
            task = mTaskList.getById(taskId);
            mTaskList.deleteById(taskId);
        }
        return task;
    }

    /**
     * @hide
     */
    public int executeTask(Task task) {
        if ( mTaskExecutor == null || task == null ) {
            return 0;
        }
        synchronized ( mTaskExecutor ) {
            if ( !mTaskExecutor.isShutDown() ) {
                mTaskExecutor.execute(task);
            } else {
                throw new IllegalArgumentException("Can't use a ThreadPoolExecutor which has been shutdown");
            }
        }
        return 0;
    }

    /**
     * @hide
     */
    public boolean cancelTask(int taskId) {
        Task task = null;
        boolean canBeCanceled = false;
        if ( mTaskList == null ) {
            return canBeCanceled;
        }
        synchronized (mTaskList) {
            task = mTaskList.getById(taskId);
        }
        if ( task != null ) {
            canBeCanceled = task.cancel(true);
        }
        return canBeCanceled;
    }

    /**
     * @hide
     */
    public int getCurrentTaskSize() {
        int result = 0;
        synchronized (mTaskList) {
            result = mTaskList.size();
        }
        return result;
    }

    /**
     * @hide
     */
    public static class UriPrefix {

        private static final String ENCODE_PREFIX = "ext:";
        private static final String FILE_SCHEME_PREFIX = "file://";

        /**
         * @hide
         */
        public static Uri decode(Context context, Uri uri) {
            List<StorageInfo> storageInfos = StorageManager.getAllStorages(context, Environment.MEDIA_MOUNTED);
            if ( storageInfos == null || storageInfos.isEmpty() ) {
                return null;
            }
            return decode(storageInfos.get(0), uri);
        }

        /**
         * @hide
         */
        public static Uri decode(StorageInfo storageInfo, Uri uri) {
            if ( storageInfo == null || uri == null ) {
                return null;
            }
            Uri result = uri;
            String uriString = uri.toString();
            String originalPrefix = storageInfo.getAbsolutePath();
            if ( !TextUtils.isEmpty(originalPrefix) ) {
                String oldPrefix = FILE_SCHEME_PREFIX + ENCODE_PREFIX;
                String newPrefix = FILE_SCHEME_PREFIX + originalPrefix;
                if ( uriString.startsWith(oldPrefix) ) {
                    /*
                     * from file://ext:/.data/CacheManager/1228698067
                     *   to file:///storage/sdcard0/.data/CacheManager/1228698067
                     */
                    // uriString = uriString.replace(ENCODE_PREFIX, originalPrefix);
                    uriString = newPrefix + uriString.substring(oldPrefix.length());
                }
            }
            result =  Uri.parse(uriString);
            return result;
        }

        /**
         * @hide
         */
        public static Uri encode(Context context, Uri uri) {
            List<StorageInfo> storageInfos = StorageManager.getAllStorages(context, Environment.MEDIA_MOUNTED);
            if ( storageInfos == null || storageInfos.isEmpty() ) {
                return null;
            }
            return encode(storageInfos.get(0), uri);
        }

        /**
         * @hide
         */
        public static Uri encode(StorageInfo storageInfo, Uri uri) {
            if ( storageInfo == null || uri == null ) {
                return null;
            }
            Uri result = uri;
            String uriString = uri.toString();
            String originalPrefix = storageInfo.getAbsolutePath();
            if ( !TextUtils.isEmpty(originalPrefix) ) {
                String oldPrefix = FILE_SCHEME_PREFIX + originalPrefix;
                String newPrefix = FILE_SCHEME_PREFIX + ENCODE_PREFIX;
                if ( uriString.startsWith(oldPrefix) ) {
                    /*
                     * from file:///storage/sdcard0/.data/CacheManager/1228698067
                     *   to file://ext:/.data/CacheManager/1228698067
                     */
                    // uriString = uriString.replace(originalPrefix, ENCODE_PREFIX);
                    uriString = newPrefix + uriString.substring(oldPrefix.length());
                }
            }
            result =  Uri.parse(uriString);
            return result;
        }
    }

    /**
     * @hide
     */
    public static interface TaskCallback {

        /**
         * @hide
         */
        public void onSuccess(Uri uri, Bundle data);

        /**
         * @hide
         */
        public void onError(Exception e, Bundle data);
    }

    /**
     * @hide
     */
    public static class TaskList {

        private SparseArray<Task> mTaskListById = null;

        /**
         * @hide
         */
        public TaskList() {
            mTaskListById = new SparseArray<Task>();
        }

        /**
         * @hide
         */
        public synchronized Task getById(int id) {
            if ( mTaskListById == null ) {
                return null;
            }
            return mTaskListById.get(id);
        }

        /**
         * @hide
         */
        public synchronized void putById(int id, Task value) {
            if ( mTaskListById == null || value == null ) {
                return;
            }
            mTaskListById.put(id, value);
        }

        /**
         * @hide
         */
        public synchronized void deleteById(int id) {
            if ( mTaskListById == null ) {
                return;
            }
            Task task = mTaskListById.get(id);
            if ( task != null ) {
                mTaskListById.delete(id);
            }
        }

        /**
         * @hide
         */
        public synchronized int size() {
            if ( mTaskListById == null ) {
                return 0;
            }
            return mTaskListById.size();
        }
    }

    /**
     * @hide
     */
    public static class TaskExecutor {

        private static final int MIN_THREADS = 8;
        private static final int MAX_THREADS = 8;
        private static final int KEEP_ALIVE_TIME = 1;

        private final PriorityBlockingQueue<Runnable> mQueue = new PriorityBlockingQueue<Runnable>();

        private ThreadPoolExecutor mExecutor = null;

        /**
         * @hide
         */
        public TaskExecutor() {
            synchronized (this) {
                mExecutor = new ThreadPoolExecutor(MIN_THREADS, MAX_THREADS,
                        KEEP_ALIVE_TIME, TimeUnit.SECONDS, mQueue);
            }
        }

        /**
         * @hide
         */
        public void execute(Runnable runnable) {
            mExecutor.execute(runnable);
        }

        /**
         * @hide
         */
        public boolean isShutDown() {
            synchronized (this) {
                return mExecutor == null || mExecutor.isShutdown();
            }
        }
    }

    /**
     * @hide
     */
    public static class Task extends FutureTask<TaskInfo> implements Comparable<Task> {

        private int mTaskId = 0;
        private Bundle mData = null;
        private Context mContext = null;
        private TaskCallback mTaskCallback = null;
        private TaskManager mTaskManager = null;

        /**
         * @hide
         */
        public Task(Context context, TaskManager taskManager, int taskId, String uriString,
                TaskCallback taskCallback, FaceDetectTaskHelper faceDetectTaskHelper, Bundle data) {
            super(new TaskCallable(context, uriString, faceDetectTaskHelper, data));
            mContext = context;
            mData = data;
            mTaskCallback = taskCallback;
            mTaskManager = taskManager;
            mTaskId = taskId;
        }

        /**
         * @hide
         */
        @Override
        public int compareTo(Task another) {
            return getId() < another.getId() ? -1 : 1;
        }

        /**
         * @hide
         */
        @Override
        protected void done() {
            if ( mTaskManager != null ) {
                mTaskManager.removeTask(mTaskId);
            }
            mTaskManager = null;

            TaskInfo taskInfo = null;
            Exception taskException = null;
            try {
                taskInfo = get();
            } catch (InterruptedException e) {
                taskException = e;
            } catch (ExecutionException e) {
                taskException = e;
            } catch (CancellationException e) {
                // be cancelled
                taskException = e;
            }

            if ( mData == null ) {
                mData = new Bundle();
            }
            if ( mTaskCallback != null ) {
                if ( taskInfo == null ) {
                    mTaskCallback.onError(taskException, mData);
                    mData = null;
                    return;
                }
                TaskInfo.Error error = taskInfo.getError();
                if ( error != TaskInfo.Error.NONE ) {
                    TaskInfo.updateToDb(
                            mContext,
                            taskInfo.getId(),
                            Download.STATUS, TaskInfo.Status.FAIL.toString()
                    );
                    Exception exception = taskInfo.getException();
                    mTaskCallback.onError(exception, mData);
                    mData = null;
                    return;
                }
                Uri fullResultUri = taskInfo.getFullResultUri();
                if ( fullResultUri == null ) {
                    TaskInfo.updateToDb(
                            mContext,
                            taskInfo.getId(),
                            Download.STATUS, TaskInfo.Status.FAIL.toString()
                    );
                    mTaskCallback.onError(new Exception("resultUri == null"), mData);
                    mData = null;
                    return;
                } else {
                    mTaskCallback.onSuccess(fullResultUri, mData);
                    mData = null;
                    return;
                }
            }
        }

        /**
         * @hide
         */
        public int getId() {
            return mTaskId;
        }
    }

    /**
     * @hide
     */
    public static class TaskCallable implements Callable<TaskInfo> {

        private static final long QUERY_RANGE = 1000L * 5;

        private static long sLastUsedSizeQueryTimestamp = 0L;
        private static long sLastUsedSizeQueryResult = 0L;

        private int mUrlHash = 0;
        private Bundle mData = null;
        private Context mContext = null;
        private FaceDetectTaskHelper mFaceDetectTaskHelper = null;
        private String mUrl = null;

        /**
         * @hide
         */
        public TaskCallable(Context context, String url, FaceDetectTaskHelper faceDetectTaskHelper, Bundle data) {
            mContext = context;
            if ( data != null ) {
                mData = data;
            } else {
                mData = new Bundle();
            }
            if ( url != null ) {
                mUrl = url;
            } else {
                mUrl = "";
            }
            mFaceDetectTaskHelper = faceDetectTaskHelper;
            mUrlHash = mUrl.hashCode();
        }

        /**
         * @hide
         */
        @Override
        public TaskInfo call() throws Exception {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);

            TaskInfo taskInfo = null;
            Uri uri = Uri.parse(mUrl);
            List<StorageInfo> storageInfos = StorageManager.getAllStorages(mContext, Environment.MEDIA_MOUNTED);

            if ( !StorageManager.prepareCacheDir(storageInfos) ) {
                Log.e(LOG_TAG, "Can't create cache folder.");
                taskInfo = new TaskInfo(TaskInfo.Status.FAIL);
                return taskInfo.applyError(TaskInfo.Error.CANNOT_CREATE_SHARED_FOLDER);
            }

            taskInfo = TaskInfo.getCurrentTaskInfo(mContext, mUrlHash, mUrl, storageInfos);
            if ( taskInfo.getStatus() != TaskInfo.Status.SUCCESS ) {
                boolean isCheckOnly = mData.getBoolean(Download.CHECK_ONLY);
                if ( isCheckOnly ) {
                    taskInfo.applyError(TaskInfo.Error.CHECKED_THEN_NOT_CACHED);
                } else {
                    taskInfo = downloadTargetFile(mContext, mUrl, mData, taskInfo, storageInfos);
                }
            } else {
                Uri resultUri = taskInfo.getResultUri();
                StorageManager.setFileLastModified(resultUri, System.currentTimeMillis());
                taskInfo.applyFinishTime(StorageManager.getFileLastModified(resultUri));
            }

            if ( TaskInfo.Error.NONE == taskInfo.getError() ) {
                if ( URI_EMPTY.equals(taskInfo.getResultUri().toString()) ) {
                    taskInfo.applyError(TaskInfo.Error.EMPTY_URI);
                }
            }

            if ( TaskInfo.Error.NONE == taskInfo.getError() ) {
                String aoiParam = taskInfo.getResultUriParam("AOI");
                if ( TextUtils.isEmpty(aoiParam) ) {
                    aoiParam = getParamAoi(uri);
                    if ( TextUtils.isEmpty(aoiParam) ) {
                        taskInfo = applyFaceDetectionIntoTaskInfo(
                                mContext,
                                taskInfo,
                                mFaceDetectTaskHelper
                        );
                    } else {
                        taskInfo.applyResultUriParam("AOI", aoiParam);
                    }
                }
                taskInfo = applyDurationIntoTaskInfo(taskInfo, taskInfo.getDuration());

                taskInfo.applyStatus(TaskInfo.Status.SUCCESS);
                Uri encodedUri = UriPrefix.encode(mContext, taskInfo.getFullResultUri());
                TaskInfo.updateToDb(
                        mContext,
                        taskInfo.getId(),
                        Download.CONTENT_URI, encodedUri.toString(),
                        Download.STATUS, TaskInfo.Status.SUCCESS.toString(),
                        Download.FILE_SIZE, String.valueOf(taskInfo.getFileSize()),
                        Download.LAST_MODIFIED_TIME, String.valueOf(taskInfo.getFinishTime())
                );
            }
            return taskInfo;
        }

        private static TaskInfo downloadTargetFile(Context context, String url, Bundle data, TaskInfo taskInfo,
                List<StorageInfo> storageInfos) {
            if ( isSameScheme(url, Scheme.FILE) ) {
                taskInfo = getFileSchemeTargetFile(context, taskInfo, storageInfos, url, false);
            } else if ( isSameScheme(url, Scheme.FILE_ENCRYPTED) ) {
                taskInfo = getFileSchemeTargetFile(context, taskInfo, storageInfos, url, true);
            } else if ( isSameScheme(url, Scheme.HTTP) || isSameScheme(url, Scheme.HTTPS) ) {
                taskInfo = getHttpSchemeTargetFile(context, taskInfo, storageInfos, url, data);
            } else {
                // TODO non-supported-scheme logic
            }
            return taskInfo;
        }

        private static TaskInfo getFileSchemeTargetFile(Context context, TaskInfo taskInfo,
                List<StorageInfo> storageInfos, String uri, boolean preencrypted) {
            final String methodName = "getFileSchemeTargetFile";
            if ( taskInfo == null ) {
                Log.w(LOG_TAG, "[" + methodName + "] taskInfo is null, re-create it.");
                taskInfo = new TaskInfo(TaskInfo.Status.FAIL);
            }
            Pair<TaskInfo.Error, Exception> result = null;
            TaskInfo.Error error = TaskInfo.Error.NONE;
            Exception exception = null;
            if ( isRemainingSizeInsufficient(context, storageInfos) ) {
                result = deleteUnusedUriFromDb(context, storageInfos);
                if ( TaskInfo.Error.NONE != result.first ) {
                    return taskInfo.applyError(result.first).applyException(result.second);
                }
                result = deleteUnusedOlderFile(context, storageInfos);
                if ( TaskInfo.Error.NONE != result.first ) {
                    return taskInfo.applyError(result.first).applyException(result.second);
                }
            }

            if ( TextUtils.isEmpty(uri) ) {
                return taskInfo.applyError(TaskInfo.Error.INVALID_URI)
                        .applyException(new Exception("[" + methodName + "] converted uri is invalid"));
            }

            if ( preencrypted ) {
                if ( uri.startsWith(Scheme.FILE_ENCRYPTED.toString()) ) {
                    // uri = uri.replace("file+enc:/", "file:/");
                    uri = Scheme.FILE.toString() + uri.substring(Scheme.FILE_ENCRYPTED.toString().length());
                }
            }

            String currentCacheDir = StorageManager.getCacheDir(storageInfos.get(0));
            if ( TextUtils.isEmpty(currentCacheDir) ) {
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCATE_SHARED_FOLDER);
            }

            result = createSharedDir(StorageManager.getTempDir(storageInfos.get(0)));
            if ( TaskInfo.Error.NONE != result.first ) {
                return taskInfo.applyError(result.first).applyException(result.second);
            }

            String sharedFilePath = StorageManager.getSharedFilePathFromUri(storageInfos.get(0), uri);
            if ( TextUtils.isEmpty(sharedFilePath) ) {
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCALE_SHARED_FILE)
                        .applyException(new Exception("[" + methodName + "] sharedFilePath is empty"));
            }

            String tempFilePath = StorageManager.getTempFilePathFromUri(storageInfos.get(0), uri, taskInfo.hashCode(), System.currentTimeMillis());
            if ( TextUtils.isEmpty(tempFilePath) ) {
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCALE_TEMP_FILE)
                        .applyException(new Exception("[" + methodName + "] tempFilePath is empty"));
            }

            File tempFile = new File(tempFilePath);
            if ( !tempFile.exists() ) {
                try {
                    tempFile.createNewFile();
                } catch (Exception e) {
                    exception = e;
                }
            }
            if ( exception != null ) {
                String message = exception.getMessage();
                if ( message != null && message.contains("ENOSPC") ) { // get "No space left" exception
                    StorageManager.deleteOldFiles(currentCacheDir);
                }
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCALE_TEMP_FILE).applyException(exception);
            }

            float duration = 0.0f;
            File imageFile = null;
            try {
                imageFile = new File(new URI(uri));
                long startTime = 0L;
                if ( !imageFile.exists() ) {
                    error = TaskInfo.Error.CANNOT_FIND_TARGET;
                } else {
                    InputStream input = null;
                    OutputStream output = null;
                    startTime = System.currentTimeMillis();
                    try {
                        input = new FileInputStream(imageFile);
                        if ( preencrypted ) {
                            output = getFileOutputStream(tempFile);
                        } else {
                            output = getFileOutputStream(context, Cipher.ENCRYPT_MODE, tempFile);
                        }
                        result = copyInputToOutput(input, output);
                        if ( result != null ) {
                            error = result.first;
                            exception = result.second;
                        }
                        if ( error == TaskInfo.Error.NONE ) {
                            duration = ( (float)(System.currentTimeMillis() - startTime) ) / 1000;
                        }
                    } catch (Exception e) {
                        error = TaskInfo.Error.CANNOT_SAVE_TARGET_TO_TEMP_FILE;
                        exception = e;
                    } finally {
                        if ( input != null ) {
                            try {
                                input.close();
                            } catch (Exception e) {
                            }
                        }
                        if ( output != null ) {
                            try {
                                output.close();
                            } catch (Exception e) {
                            }
                        }
                    }
                    if ( exception != null ) {
                        if ( tempFile.exists() ) {
                            tempFile.delete(); // delete gabage file
                        }
                        String message = exception.getMessage();
                        if ( message != null && message.contains("ENOSPC") ) { // get "No space left" exception
                            StorageManager.deleteOldFiles(currentCacheDir);
                        }
                    }
                }
            } catch (Exception e) {
                error = TaskInfo.Error.CANNOT_FIND_TARGET;
                exception = e;
            }
            if ( error != TaskInfo.Error.NONE ) {
                return taskInfo.applyError(error).applyException(exception);
            }

            if ( !isValidImage(context, Uri.fromFile(tempFile)) ) {
                tempFile.delete();
                return taskInfo.applyError(TaskInfo.Error.INVALID_IMAGE);
            }

            File sharedFile = new File(sharedFilePath);
            if ( sharedFile.exists() ) {
                tempFile.delete();
            } else {
                boolean isSuccessRenaming = tempFile.renameTo(sharedFile);
                if ( !isSuccessRenaming ) {
                    return taskInfo.applyError(TaskInfo.Error.CANNOT_MOVE_TEMP_FILE_TO_SHARED_FILE);
                }
            }

            long sharedFileSize = sharedFile.length();
            long sharedFileFinishTime = StorageManager.getFileLastModified(sharedFile);
            long blockSize = StorageManager.getBlockSizeLong(currentCacheDir);
            sharedFileSize = (( sharedFileSize / blockSize ) + 1) * blockSize;
            return taskInfo.applyResultUri(Uri.fromFile(sharedFile)).applyFileSize(sharedFileSize)
                    .applyDuration(duration).applyFinishTime(sharedFileFinishTime);
        }

        private static TaskInfo getHttpSchemeTargetFile(Context context, TaskInfo taskInfo,
                List<StorageInfo> storageInfos, String uri, Bundle data) {
            final String methodName = "getHttpSchemeTargetFile";
            if ( taskInfo == null ) {
                Log.w(LOG_TAG, "[" + methodName + "] taskInfo is null, re-create it.");
                taskInfo = new TaskInfo(TaskInfo.Status.FAIL);
            }
            Pair<TaskInfo.Error, Exception> result = null;
            TaskInfo.Error error = TaskInfo.Error.NONE;
            Exception exception = null;
            if ( isRemainingSizeInsufficient(context, storageInfos) ) {
                result = deleteUnusedUriFromDb(context, storageInfos);
                if ( TaskInfo.Error.NONE != result.first ) {
                    return taskInfo.applyError(result.first).applyException(result.second);
                }
                result = deleteUnusedOlderFile(context, storageInfos);
                if ( TaskInfo.Error.NONE != result.first ) {
                    return taskInfo.applyError(result.first).applyException(result.second);
                }
            }

            uri = HttpLinkConverter.getAsciiLink(uri);
            if ( TextUtils.isEmpty(uri) ) {
                return taskInfo.applyError(TaskInfo.Error.INVALID_URI)
                        .applyException(new Exception("[" + methodName + "] converted uri is invalid"));
            }

            String currentCacheDir = StorageManager.getCacheDir(storageInfos.get(0));
            if ( TextUtils.isEmpty(currentCacheDir) ) {
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCATE_SHARED_FOLDER);
            }

            result = createSharedDir(StorageManager.getTempDir(storageInfos.get(0)));
            if ( TaskInfo.Error.NONE != result.first ) {
                return taskInfo.applyError(result.first).applyException(result.second);
            }

            String sharedFilePath = StorageManager.getSharedFilePathFromUri(storageInfos.get(0), uri);
            if ( TextUtils.isEmpty(sharedFilePath) ) {
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCALE_SHARED_FILE)
                        .applyException(new Exception("[" + methodName + "] sharedFilePath is empty"));
            }

            String tempFilePath = StorageManager.getTempFilePathFromUri(storageInfos.get(0), uri, taskInfo.hashCode(), System.currentTimeMillis());
            if ( TextUtils.isEmpty(tempFilePath) ) {
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCALE_TEMP_FILE)
                        .applyException(new Exception("[" + methodName + "] tempFilePath is empty"));
            }

            File tempFile = new File(tempFilePath);
            if ( !tempFile.exists() ) {
                try {
                    tempFile.createNewFile();
                } catch (Exception e) {
                    exception = e;
                }
            }
            if ( exception != null ) {
                String message = exception.getMessage();
                if ( message != null && message.contains("ENOSPC") ) { // get "No space left" exception
                    StorageManager.deleteOldFiles(currentCacheDir);
                }
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCALE_TEMP_FILE).applyException(exception);
            }

            float duration = 0.0f;
            URL imageUrl = null;
            PowerManager powerManager = null;
            PowerManager.WakeLock wakeLock = null;
            try {
                imageUrl = new URI(uri).toURL();
                long startTime = 0L;
                powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if ( powerManager != null ) {
                    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CACHEMANAGER_WAKELOCK);
                }
                if ( wakeLock != null ) {
                    wakeLock.acquire();
                }
                for (int i = 0; i <= HTTP_MAX_RETRY_GENERAL; i++) {
                    HttpURLConnection connection = null;
                    InputStream input = null;
                    OutputStream output = null;
                    boolean isDone = false;
                    startTime = System.currentTimeMillis();
                    try {
                        connection = getConnectedHttpURLConnection(imageUrl, data, HTTP_MAX_REDIRECT);
                        input = connection.getInputStream();
                        output = getFileOutputStream(context, Cipher.ENCRYPT_MODE, tempFile);
                        result = copyInputToOutput(input, output);
                        if ( result != null ) {
                            error = result.first;
                            exception = result.second;
                        }
                        if ( error == TaskInfo.Error.NONE ) {
                            duration = ( (float)(System.currentTimeMillis() - startTime) ) / 1000;
                            isDone = true;
                        }
                    } catch (Exception e) {
                        error = TaskInfo.Error.CANNOT_SAVE_TARGET_TO_TEMP_FILE;
                        exception = e;
                    } finally {
                        if ( input != null ) {
                            try {
                                input.close();
                            } catch (Exception e) {
                            }
                        }
                        if ( output != null ) {
                            try {
                                output.close();
                            } catch (Exception e) {
                            }
                        }
                        if ( connection != null ) {
                            connection.disconnect();
                        }
                    }
                    if ( exception != null ) {
                        if ( tempFile.exists() ) {
                            tempFile.delete(); // delete garbage file
                        }
                        String message = exception.getMessage();
                        if ( message != null && message.contains("ENOSPC") ) { // get "No space left" exception
                            StorageManager.deleteOldFiles(currentCacheDir);
                        }
                        if ( exception instanceof UnknownHostException ) {
                            isDone = true;
                        }
                    }
                    if ( isDone ) {
                        break;
                    }
                }
            } catch (Exception e) {
                error = TaskInfo.Error.CANNOT_FIND_TARGET;
                exception = e;
            } finally {
                if ( wakeLock != null && wakeLock.isHeld() ) {
                    wakeLock.release();
                }
            }
            if ( error != TaskInfo.Error.NONE ) {
                return taskInfo.applyError(error).applyException(exception);
            }

            if ( !isValidImage(context, Uri.fromFile(tempFile)) ) {
                tempFile.delete();
                return taskInfo.applyError(TaskInfo.Error.INVALID_IMAGE);
            }

            File sharedFile = new File(sharedFilePath);
            if ( sharedFile.exists() ) {
                tempFile.delete();
            } else {
                boolean isSuccessRenaming = tempFile.renameTo(sharedFile);
                if ( !isSuccessRenaming ) {
                    return taskInfo.applyError(TaskInfo.Error.CANNOT_MOVE_TEMP_FILE_TO_SHARED_FILE);
                }
            }

            long sharedFileSize = sharedFile.length();
            long sharedFileFinishTime = StorageManager.getFileLastModified(sharedFile);
            long blockSize = StorageManager.getBlockSizeLong(currentCacheDir);
            sharedFileSize = (( sharedFileSize / blockSize ) + 1) * blockSize;
            return taskInfo.applyResultUri(Uri.fromFile(sharedFile)).applyFileSize(sharedFileSize)
                    .applyDuration(duration).applyFinishTime(sharedFileFinishTime);
        }

        private static Pair<TaskInfo.Error, Exception> copyInputToOutput(InputStream input, OutputStream output) {
            Exception exception = null;
            if ( input == null || output == null ) {
                return new Pair<TaskInfo.Error, Exception>(
                        TaskInfo.Error.CANNOT_SAVE_TARGET_TO_TEMP_FILE,
                        exception
                );
            }
            final byte[] buf = new byte[2048];
            int readSize = -1;
            try {
                while ( (readSize = input.read(buf)) >= 0 ) {
                    output.write(buf, 0, readSize);
                }
                output.flush();
            } catch (IOException e) {
                exception = e;
            }
            if ( exception != null ) {
                return new Pair<TaskInfo.Error, Exception>(
                        TaskInfo.Error.CANNOT_SAVE_TARGET_TO_TEMP_FILE,
                        exception
                );
            }
            return new Pair<TaskInfo.Error, Exception>(
                    TaskInfo.Error.NONE,
                    exception
            );
        }

        private static Pair<TaskInfo.Error, Exception> createSharedDir(String dir) {
            if ( dir == null ) {
                return new Pair<TaskInfo.Error, Exception>(
                        TaskInfo.Error.CANNOT_CREATE_SHARED_FOLDER,
                        new Exception("[createSharedDir] dir == null")
                );
            }
            File sharedFolder = new File(dir);
            Exception exception = null;
            if ( !sharedFolder.exists() ) {
                SWLog.d(LOG_TAG, "Target path is not exist, re-create : " + dir);
                sharedFolder.mkdirs();
            }
            try {
                File noMediaMarker = new File(dir + File.separatorChar, NOMEDIA);
                if ( !noMediaMarker.exists() ) {
                    noMediaMarker.createNewFile();
                }
            } catch (IOException e) {
                exception = e;
            }
            return new Pair<TaskInfo.Error, Exception>(TaskInfo.Error.NONE, exception);
        }

        private static boolean isRemainingSizeInsufficient(Context context, List<StorageInfo> storageInfos) {
            final String methodName = "isRemainingSizeInsufficient";
            if ( context == null ) {
                SWLog.w(LOG_TAG, "[" + methodName + "] context is empty");
                return false;
            }
            if ( storageInfos == null || storageInfos.isEmpty() ) {
                SWLog.w(LOG_TAG, "[" + methodName + "] storageInfos is empty");
                return false;
            }
            StorageInfo storageInfo = storageInfos.get(0);
            long limitSize = storageInfo.getLimitSize();
            long remainingLowerBound = storageInfo.getRemainingLowerBound();
            String currentCacheDir = StorageManager.getCacheDir(storageInfos.get(0));
            if ( TextUtils.isEmpty(currentCacheDir) ) {
                SWLog.w(LOG_TAG, "[" + methodName + "] currentCacheDir is empty");
                return false;
            }

            long usedSize = getUsedSizeInDirFromDb(context, storageInfos);
            if ( limitSize - usedSize < remainingLowerBound ) {
                return true;
            }
            return false;
        }

        private static Pair<TaskInfo.Error, Exception> deleteUnusedUriFromDb(Context context,
                List<StorageInfo> storageInfos) {
            final String methodName = "deleteUnusedUriFromDb";
            if ( context == null || storageInfos == null || storageInfos.isEmpty() ) {
                return new Pair<TaskInfo.Error, Exception>(
                        TaskInfo.Error.CANNOT_LOCATE_SHARED_FOLDER,
                        new Exception("[" + methodName + "] context == null || storageInfos == null || storageInfos.isEmpty()")
                );
            }

            Log.i(LOG_TAG, "[" + methodName + "] remain size not enough, start check database uri is exist in local folder");
            ContentProviderClient providerClient = null;
            Cursor c = null;
            Exception exception = null;
            try {
                providerClient = context.getContentResolver().acquireUnstableContentProviderClient(CACHE_PROVIDER_AUTHORITY);
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
                                    StorageManager.getCacheDir(storageInfos.get(0))
                            },
                            Download.LAST_MODIFIED_TIME + " ASC"
                    );
                    if ( c != null ) {
                        StringBuilder deleteStr = new StringBuilder();
                        boolean isFirst = true;
                        while ( c.moveToNext() ) {
                            Uri uri = Uri.parse(c.getString(c.getColumnIndexOrThrow(Download.CONTENT_URI)));
                            uri = UriPrefix.decode(context, uri);
                            boolean isExist = isFileExisted(uri);
                            if ( uri.toString().startsWith(Scheme.FILE.toString()) && !isExist ) {
                                if ( !isFirst ) {
                                    deleteStr.append(" OR ");
                                } else {
                                    isFirst = false;
                                }
                                deleteStr.append(Download._ID);
                                deleteStr.append("=");
                                deleteStr.append(c.getString(c.getColumnIndexOrThrow(Download._ID)));
                                if ( deleteStr.length() > DLENGTH ) {
                                    Log.i(LOG_TAG, "[" + methodName + "] delete " + deleteStr.toString());
                                    synchronized (LOCK_DATABASE_MODIFICATION) {
                                        providerClient.delete(
                                                Download.DOWNLOAD_CONTENT_URI,
                                                deleteStr.toString(),
                                                null
                                        );
                                    }
                                    deleteStr.setLength(0);
                                    isFirst = true;
                                }
                            }
                        }
                        if ( deleteStr.length() > 0 ) {
                            Log.i(LOG_TAG, "[" + methodName + "] out of while delete " + deleteStr.toString());
                            synchronized (LOCK_DATABASE_MODIFICATION) {
                                providerClient.delete(
                                        Download.DOWNLOAD_CONTENT_URI,
                                        deleteStr.toString(),
                                        null
                                );
                            }
                        }
                    }
                } else {
                    exception = new Exception("[" + methodName + "] ContentProviderClient is null for uri: "
                            + Download.DOWNLOAD_CONTENT_URI.toString());
                }
            } catch (Exception e) {
                exception = e;
            } finally {
                if ( c != null ) {
                    c.close();
                }
                if ( providerClient != null ) {
                    providerClient.release();
                }
            }
            return new Pair<TaskInfo.Error, Exception>(TaskInfo.Error.NONE, exception);
        }

        private static Pair<TaskInfo.Error, Exception> deleteUnusedOlderFile(Context context,
                List<StorageInfo> storageInfos) {
            final String methodName = "deleteUnusedOlderFile";
            if ( context == null || storageInfos == null || storageInfos.isEmpty() ) {
                return new Pair<TaskInfo.Error, Exception>(
                        TaskInfo.Error.CANNOT_LOCATE_SHARED_FOLDER,
                        new Exception("[" + methodName + "] context == null || storageInfos == null || storageInfos.isEmpty()")
                );
            }

            StorageInfo storageInfo = storageInfos.get(0);
            long limitSize = storageInfo.getLimitSize();
            long remainingUpperBound = storageInfo.getRemainingUpperBound();
            long usedSize = getUsedSizeInDirFromDb(context, storageInfos);
            long remainSize = limitSize - usedSize;

            String currentCacheDir = StorageManager.getCacheDir(storageInfo);
            if ( TextUtils.isEmpty(currentCacheDir) ) {
                return new Pair<TaskInfo.Error, Exception>(TaskInfo.Error.CANNOT_LOCATE_SHARED_FOLDER, null);
            }

            Log.i(LOG_TAG, "[" + methodName + "] remain size not enough, start delete");
            ContentProviderClient providerClient = null;
            Cursor c = null;
            Exception exception = null;
            try {
                providerClient = context.getContentResolver().acquireUnstableContentProviderClient(CACHE_PROVIDER_AUTHORITY);
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
                                    currentCacheDir
                            },
                            Download.LAST_MODIFIED_TIME + " ASC"
                    );
                    // the deletion operation should not be interrupted
                    if ( c != null ) {
                        StringBuilder deleteStr = new StringBuilder();
                        boolean isFirst = true;
                        long timestampToDelete = 0L;
                        while ( c.moveToNext() ) {
                            int currentSize = c.getInt(c.getColumnIndexOrThrow(Download.FILE_SIZE));
                            Uri currentUri = Uri.parse(c.getString(c.getColumnIndexOrThrow(Download.CONTENT_URI)));
                            currentUri = UriPrefix.decode(context, currentUri);
                            timestampToDelete = Math.max(timestampToDelete, StorageManager.getFileLastModified(currentUri));
                            if ( currentUri.toString().startsWith(Scheme.FILE.toString()) && !StorageManager.deleteFileFromUri(currentUri) ) {
                                continue;
                            }
                            remainSize += currentSize;
                            if ( remainSize < remainingUpperBound ) {
                                if ( !isFirst ) {
                                    deleteStr.append(" OR ");
                                } else {
                                    isFirst = false;
                                }
                                deleteStr.append(Download._ID);
                                deleteStr.append("=");
                                deleteStr.append(c.getString(c.getColumnIndexOrThrow(Download._ID)));
                                if ( deleteStr.length() > DLENGTH ) {
                                    Log.i(LOG_TAG, "[" + methodName + "] delete " + deleteStr.toString());
                                    synchronized (LOCK_DATABASE_MODIFICATION) {
                                        providerClient.delete(
                                                Download.DOWNLOAD_CONTENT_URI,
                                                deleteStr.toString(),
                                                null
                                        );
                                    }
                                    deleteStr.setLength(0);
                                    isFirst = true;
                                }
                            } else {
                                Log.i(LOG_TAG, "[" + methodName + "] remain size " + remainSize + " break");
                                break;
                            }
                            Log.i(LOG_TAG, "[" + methodName + "] remain size " + remainSize);
                        }
                        StorageManager.deleteOldFiles(currentCacheDir, timestampToDelete);
                        Log.d(LOG_TAG, "[deleteUnusedOlderFile] out of while delete " + deleteStr.toString());
                        if ( deleteStr.length() > 0 ) {
                            synchronized (LOCK_DATABASE_MODIFICATION) {
                                providerClient.delete(
                                        Download.DOWNLOAD_CONTENT_URI,
                                        deleteStr.toString(),
                                        null
                                );
                            }
                        }
                    }
                } else {
                    exception = new Exception("[" + methodName + "] ContentProviderClient is null for uri: "
                            + Download.DOWNLOAD_CONTENT_URI.toString());
                }
            } catch (Exception e) {
                exception = e;
            } finally {
                if ( c != null ) {
                    c.close();
                }
                if ( providerClient != null ) {
                    providerClient.release();
                }
            }
            return new Pair<TaskInfo.Error, Exception>(TaskInfo.Error.NONE, exception);
        }

        private static long getUsedSizeInDirFromDb(Context context, List<StorageInfo> storageInfos) {
            final String methodName = "getUsedSizeInDirFromDb";
            long result = 0L;
            if ( context == null ) {
                SWLog.w(LOG_TAG, "[" + methodName + "] context is empty");
                return result;
            }
            if ( storageInfos == null || storageInfos.isEmpty() ) {
                SWLog.w(LOG_TAG, "[" + methodName + "] storageInfos is empty");
                return result;
            }
            String currentCacheDir = StorageManager.getCacheDir(storageInfos.get(0));
            if ( TextUtils.isEmpty(currentCacheDir) ) {
                SWLog.w(LOG_TAG, "[" + methodName + "] currentCacheDir is empty");
                return result;
            }

            long currentTime = System.currentTimeMillis();
            if ( sLastUsedSizeQueryTimestamp + QUERY_RANGE > currentTime ) {
                if ( sLastUsedSizeQueryTimestamp > currentTime ) {
                    sLastUsedSizeQueryTimestamp = currentTime;
                }
                return sLastUsedSizeQueryResult;
            }

            SWLog.d(
                    LOG_TAG,
                    "select str = " + "select sum(" + Download.FILE_SIZE + ") from " + Download.DOWNLOAD_TB
                            + " where " + Download.STORE_FOLDER + " ='" + currentCacheDir + "'"
            );
            ContentProviderClient providerClient = null;
            Cursor c = null;
            try {
                providerClient = context.getContentResolver().acquireUnstableContentProviderClient(CACHE_PROVIDER_AUTHORITY);
                if ( providerClient != null ) {
                    c = providerClient.query(
                            Download.RAWQUERY_URI,
                            null,
                            "select sum(" + Download.FILE_SIZE + ") from " + Download.DOWNLOAD_TB
                                    + " where " + Download.STORE_FOLDER + " ='" + currentCacheDir + "'",
                            null,
                            null
                    );
                } else {
                    Log.w(LOG_TAG, "[" + methodName + "] ContentProviderClient is null for uri: " + Download.RAWQUERY_URI.toString());
                }
                if ( c != null && c.moveToNext() ) {
                    result = c.getInt(0);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Exception in [" + methodName + "] : " + e);
            } finally {
                if ( c != null ) {
                    c.close();
                }
                if ( providerClient != null ) {
                    providerClient.release();
                }
            }
            if ( result < 0 ) { // size check
                result = 0;
            }
            sLastUsedSizeQueryTimestamp = currentTime;
            sLastUsedSizeQueryResult = result;
            return result;
        }
    }

    /**
     * @hide
     */
    public static class TaskInfo {

        /**
         * @hide
         */
        public static enum Status {
            PROGRESS(0),
            SUCCESS(1),
            FAIL(2);

            private int v;

            private Status(int value) {
                v = value;
            }

            /**
             * @hide
             */
            public int toValue() {
                return v;
            }

            /**
             * @hide
             */
            @Override
            public String toString() {
                return "" + v;
            }

            /**
             * @hide
             */
            public static Status intToEnum(int value) {
                if ( value == 0 ) {
                    return PROGRESS;
                } else if ( value == 1 ) {
                    return SUCCESS;
                } else if ( value == 2 ) {
                    return FAIL;
                }
                return FAIL;
            }
        }

        private static enum Error {
            NONE,
            CANNOT_LOCATE_SHARED_FOLDER,
            CANNOT_LOCALE_SHARED_FILE,
            CANNOT_LOCALE_TEMP_FILE,
            CANNOT_CREATE_SHARED_FOLDER,
            CANNOT_CREATE_TEMP_FOLDER,
            CANNOT_CREATE_TEMP_FILE,
            CANNOT_FIND_TARGET,
            CANNOT_SAVE_TARGET_TO_TEMP_FILE,
            CANNOT_MOVE_TEMP_FILE_TO_SHARED_FILE,
            CHECKED_THEN_NOT_CACHED,
            EMPTY_URI,
            INVALID_IMAGE,
            INVALID_URI;
        }

        private static final int WARNING_CANNOT_EXECUTE_FACE_DETECTION = 1;

        private int mWarningBits = 0;
        private float mDuration = 0.0f;
        private long mFileSize = 0L;
        private long mFinishTime = 0L;
        private Status mStatus = Status.FAIL;
        private Uri mResultUri = Uri.parse(URI_EMPTY);
        private Error mError = Error.NONE;
        private String mId = null;
        private Exception mException = null;
        private HashMap<String, String> mResultUriParams = null;

        /**
         * @hide
         */
        public TaskInfo(Status status) {
            mStatus = status;
        }

        /**
         * @hide
         */
        public TaskInfo applyDuration(float duration) {
            if ( duration > 0.0 ) {
                mDuration = duration;
            }
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyError(Error error) {
            mError = error;
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyId(String id) {
            mId = id;
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyFileSize(long fileSize) {
            mFileSize = fileSize;
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyFinishTime(long finishTime) {
            mFinishTime = finishTime;
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyResultUri(Uri resultUri) {
            if ( resultUri == null ) {
                mResultUri = resultUri;
                return this;
            }

            String uriString = resultUri.toString();
            String query = resultUri.getQuery();
            if ( TextUtils.isEmpty(uriString) || TextUtils.isEmpty(query) ) {
                mResultUri = resultUri;
                return this;
            }

            String[] pairs = query.split("&");
            if ( pairs == null ) {
                mResultUri = resultUri;
                return this;
            }

            for ( String pair : pairs ) {
                if ( pair == null ) {
                    continue;
                }
                String[] params = pair.split("=");
                if ( params != null && params.length >= 2 ) {
                    String key = params[0];
                    String value = params[1];
                    if ( !TextUtils.isEmpty(key) && !TextUtils.isEmpty(value) ) {
                        if ( mResultUriParams == null ) {
                            mResultUriParams = new HashMap<String, String>();
                        }
                        mResultUriParams.put(key, value);
                    }
                }
            }
            uriString = uriString.replace("?" + query, "");
            mResultUri = Uri.parse(uriString);
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyResultUriParam(String key, String value) {
            if ( TextUtils.isEmpty(key) || TextUtils.isEmpty(value) ) {
                return this;
            }
            if ( mResultUriParams == null ) {
                mResultUriParams = new HashMap<String, String>();
            }
            if ( !mResultUriParams.containsKey(key) ) {
                mResultUriParams.put(key, value);
            }
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyStatus(int status) {
            mStatus = Status.intToEnum(status);
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyStatus(Status status) {
            if ( mStatus != null ) {
                mStatus = status;
            }
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyException(Exception exception) {
            mException = exception;
            return this;
        }

        /**
         * @hide
         */
        public TaskInfo applyWarning(int warningBit) {
            mWarningBits = mWarningBits | warningBit;
            return this;
        }

        /**
         * @hide
         */
        public float getDuration() {
            return mDuration;
        }

        /**
         * @hide
         */
        public Error getError() {
            return mError;
        }

        /**
         * @hide
         */
        public long getFileSize() {
            return mFileSize;
        }

        /**
         * @hide
         */
        public long getFinishTime() {
            return mFinishTime;
        }

        /**
         * @hide
         */
        public String getId() {
            return mId;
        }

        /**
         * @hide
         */
        public Uri getFullResultUri() {
            if ( mResultUriParams == null ) {
                return mResultUri;
            }
            String fullResultUriString = mResultUri.toString();
            boolean isFirst = true;
            for ( String key : mResultUriParams.keySet() ) {
                if ( key == null ) {
                    continue;
                }
                if ( isFirst ) {
                    fullResultUriString += "?";
                    isFirst = false;
                } else {
                    fullResultUriString += "&";
                }
                fullResultUriString += key + "=" + mResultUriParams.get(key);
            }
            return Uri.parse(fullResultUriString);
        }

        /**
         * @hide
         */
        public Uri getResultUri() {
            return mResultUri;
        }

        /**
         * @hide
         */
        public String getResultUriParam(String key) {
            String result = null;
            if ( TextUtils.isEmpty(key) || (mResultUriParams == null) ) {
                return result;
            }
            result = mResultUriParams.get(key);
            return result;
        }

        /**
         * @hide
         */
        public Status getStatus() {
            return mStatus;
        }

        /**
         * @hide
         */
        public Exception getException() {
            return mException;
        }

        /**
         * @hide
         */
        public static TaskInfo getFromDb(Context context, int urlHash, StorageInfo storageInfo, TaskInfo initValue) {
            final String methodName = "getFromDb";
            TaskInfo taskInfo = initValue;
            if ( taskInfo == null ) {
                taskInfo = new TaskInfo(TaskInfo.Status.FAIL);
            }
            if ( context == null || storageInfo == null ) {
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCATE_SHARED_FOLDER);
            }
            String currentCacheDir = StorageManager.getCacheDir(storageInfo);
            if ( TextUtils.isEmpty(currentCacheDir) ) {
                return taskInfo.applyError(TaskInfo.Error.CANNOT_LOCATE_SHARED_FOLDER);
            }

            int resultStatus = TaskInfo.Status.FAIL.toValue();
            int resultSize = 0;
            Uri resultUri = null;
            String resultId = null;
            boolean isQueryDone = false;
            StringBuilder query = new StringBuilder();
            query.append(Download.IMG_URL_HASH).append("=").append(urlHash)
                    .append(" AND (").append(Download.STORE_FOLDER).append("='")
                    .append(currentCacheDir).append("'").append(")");
            ContentProviderClient providerClient = null;
            Cursor c = null;
            try {
                providerClient = context.getContentResolver().acquireUnstableContentProviderClient(CACHE_PROVIDER_AUTHORITY);
                if ( providerClient != null ) {
                    c = providerClient.query(
                            Download.DOWNLOAD_CONTENT_URI,
                            new String[] {
                                    Download._ID,
                                    Download.CONTENT_URI,
                                    Download.FILE_SIZE,
                                    Download.STATUS
                            },
                            query.toString(),
                            null,
                            null
                    );
                } else {
                    Log.w(LOG_TAG, "[" + methodName + "] ContentProviderClient is null for query: " + Download.DOWNLOAD_CONTENT_URI.toString());
                }
                if ( c != null && c.moveToNext() ) {
                    resultStatus = c.getInt(c.getColumnIndexOrThrow(Download.STATUS));
                    resultSize = c.getInt(c.getColumnIndexOrThrow(Download.FILE_SIZE));
                    String resultUriString = c.getString(c.getColumnIndexOrThrow(Download.CONTENT_URI));
                    if ( !TextUtils.isEmpty(resultUriString) ) {
                        resultUri = UriPrefix.decode(
                                storageInfo,
                                Uri.parse(resultUriString)
                        );
                    }
                    resultId = c.getString(c.getColumnIndexOrThrow(Download._ID));
                    isQueryDone = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if ( c != null ) {
                    c.close();
                }
                if ( providerClient != null ) {
                    providerClient.release();
                }
            }
            if ( isQueryDone ) {
                taskInfo.applyStatus(resultStatus).applyFileSize(resultSize).applyResultUri(resultUri).applyId(resultId);
            }

            if ( taskInfo.getStatus() == TaskInfo.Status.SUCCESS ) {
                if ( !isUriExisted(context, taskInfo.getResultUri()) || !isValidImage(context, taskInfo.getResultUri()) ) {
                    StorageManager.deleteFileFromUri(taskInfo.getResultUri()); // may cause native crash
                    deleteTaskInfoFromDb(context, taskInfo.getId());
                    taskInfo.applyStatus(TaskInfo.Status.FAIL);
                }
            }

            return taskInfo;
        }

        /**
         * @hide
         */
        public static void updateToDb(Context context, String id, String... update) {
            final String methodName = "updateToDb";
            if ( context == null || update == null ) {
                return;
            }
            int size = update.length;
            if ( size % 2 != 0 ) {
                // CLog.d(TAG, "[updateToDb] size = " + size, true);
                throw new RuntimeException();
            }
            ContentValues values = new ContentValues();
            for (int i = 0; i < size; i += 2) {
                values.put(update[i], update[i + 1]);
            }
            Uri uri = Uri.withAppendedPath(Download.DOWNLOAD_CONTENT_URI, id);
            ContentProviderClient providerClient = null;
            try {
                providerClient = context.getContentResolver().acquireUnstableContentProviderClient(CACHE_PROVIDER_AUTHORITY);
                if ( providerClient != null ) {
                    synchronized (LOCK_DATABASE_MODIFICATION) {
                        providerClient.update(uri, values, null, null);
                    }
                    context.getContentResolver().notifyChange(Download.DOWNLOAD_CONTENT_URI, null);
                } else {
                    Log.w(LOG_TAG, "[" + methodName + "] ContentProviderClient is null for update: " + uri.toString());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception in [" + methodName + "] : " + e);
            } finally {
                if ( providerClient != null ) {
                    providerClient.release();
                }
            }
        }

        /**
         * @hide
         */
        public static TaskInfo getCurrentTaskInfo(Context context, int urlHash, String url, List<StorageInfo> storageInfos) {
            StorageInfo storageInfo = null;
            if ( storageInfos != null && !storageInfos.isEmpty() ) {
                storageInfo = storageInfos.get(0);
            }
            TaskInfo taskInfo = getFromDb(context, urlHash, storageInfo, null);
            if ( taskInfo.getStatus() == TaskInfo.Status.FAIL ) {
                taskInfo.applyId(insertStatusProgressToDbIfNotExist(context, urlHash, url, storageInfo));
            }
            return taskInfo;
        }

        private static String insertStatusProgressToDbIfNotExist(Context context, int taskId, String url,
                StorageInfo storageInfo) {
            String result = null;
            if ( context == null || url == null || storageInfo == null ) {
                return result;
            }
            String currentCacheDir = StorageManager.getCacheDir(storageInfo);
            if ( TextUtils.isEmpty(currentCacheDir) ) {
                return result;
            }

            ContentProviderClient providerClient = null;
            Cursor c = null;
            try {
                providerClient = context.getContentResolver().acquireUnstableContentProviderClient(CACHE_PROVIDER_AUTHORITY);
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
                    }
                    if ( TextUtils.isEmpty(result) ) {
                        ContentValues values = new ContentValues();
                        values.put(Download.IMG_URL_HASH, taskId);
                        values.put(Download.IMG_URL, url);
                        values.put(Download.STATUS, TaskInfo.Status.PROGRESS.toValue());
                        values.put(Download.STORE_FOLDER, currentCacheDir);
                        synchronized (LOCK_DATABASE_MODIFICATION) {
                            result = providerClient.insert(Download.DOWNLOAD_CONTENT_URI, values).getLastPathSegment();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if ( c != null ) {
                    c.close();
                }
                if ( providerClient != null ) {
                    providerClient.release();
                }
            }
            return result;
        }
    }

    /**
     * @hide
     */
    public static boolean isValidImage(Context context, Uri uri) {
        int [] outSize = getImageBounds(context, uri);
        int width = outSize[0];
        int height = outSize[1];
        if ( width == -1 || height == -1 ) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @hide
     */
    public static int[] getImageBounds (Context context, Uri uri) {
        final String methodName = "getImageBounds";
        int [] outSize = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        if ( uri == null ) {
            return new int[] {-1, -1};
        }
        String path = uri.getPath();
        if ( TextUtils.isEmpty(path) ) {
            return new int[] {-1, -1};
        }
        File file = new File(path);
        if ( file.length() <= 64L ) { // currently size lower bound is 65 (a magic number)
            return new int[] {-1, -1};
        }

        InputStream is = null;
        try {
            is = StorageManager.getInputStream(context, uri);
            if ( is != null ) {
                BitmapFactory.decodeStream(is, null, options);
            } else {
                options.outWidth = -1;
                options.outHeight = -1;
            }
        } catch (Exception e) {
            options.outWidth = -1;
            options.outHeight = -1;
            Log.w(LOG_TAG, "[" + methodName + "] exception for uri: " + uri);
        } finally {
            if ( is != null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.w(LOG_TAG, "[" + methodName + "] exception for uri: " + uri);
                }
            }
        }

        outSize[0] = options.outWidth;
        outSize[1] = options.outHeight;
        return outSize;
    }

    private static boolean isUriExisted(Context context, Uri uri) {
        if ( context == null || uri == null ) {
            return false;
        }
        boolean result = false;
        if ( "file".equals(uri.getScheme()) ) {
            File targetFile = new File(uri.getPath());
            result =  targetFile.exists();
        }
        return result;
    }

    private static void deleteTaskInfoFromDb(Context context, String id) {
        final String methodName = "deleteTaskInfoFromDb";
        if ( context == null ) {
            return;
        }
        Uri uri = Uri.withAppendedPath(Download.DOWNLOAD_CONTENT_URI, id);
        ContentProviderClient providerClient = null;
        try {
            providerClient = context.getContentResolver().acquireUnstableContentProviderClient(CACHE_PROVIDER_AUTHORITY);
            if ( providerClient != null ) {
                synchronized (LOCK_DATABASE_MODIFICATION) {
                    providerClient.delete(uri, null, null);
                }
            } else {
                Log.w(LOG_TAG, "[" + methodName + "] ContentProviderClient is null for delete: " + uri.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Exception in [" + methodName + "] : " + e);
        } finally {
            if ( providerClient != null ) {
                providerClient.release();
            }
        }
    }

    /**
     * @hide
     */
    @SuppressLint("DefaultLocale")
    public static boolean isSameScheme(String uriString, Scheme scheme) {
        if ( uriString == null ) {
            return false;
        }
        Uri uri = Uri.parse(uriString);
        String schemeString = uri.getScheme();
        if ( schemeString != null ) {
            schemeString = schemeString.toLowerCase();
            if ( scheme.toString().equals(schemeString) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @hide
     */
    public static OutputStream getFileOutputStream(File file) throws FileNotFoundException {
        if ( file == null ) {
            return null;
        }
        OutputStream out = new FileOutputStream(file);
        return out;
    }

    /**
     * @hide
     */
    public static OutputStream getFileOutputStream(Context context, int cipherMode, File file) throws IOException {
        return StorageManager.getFileOutputStream(context, cipherMode, file);
    }

    private static boolean isFileExisted(Uri uri) {
        if ( uri != null ) {
            File file = new File(uri.getPath());
            return file.exists();
        } else {
            return false;
        }
    }

    /**
     * @hide
     */
    public static String getParamAoi(Uri uri) {
        if ( uri == null ) {
            return null;
        }
        return getUriQueryParameter(uri, "AOI");
    }

    /**
     * @hide
     */
    public static String getParamWidthHeight(Uri uri) {
        if ( uri == null ) {
            return null;
        }
        String width = getUriQueryParameter(uri, "width");
        String paramWidthHeight = null;
        if ( !TextUtils.isEmpty(width) ) {
            paramWidthHeight = "width=" + width;
            String height = getUriQueryParameter(uri, "height");
            if ( !TextUtils.isEmpty(height) ) {
                paramWidthHeight += "&height=" + height;
            }
        }
        return paramWidthHeight;
    }

    /**
     * @hide
     */
    public static String getUriQueryParameter(Uri uri, String key) {
        if ( uri == null || TextUtils.isEmpty(key) ) {
            return null;
        }
        String result = null;
        try {
            result = uri.getQueryParameter(key);
        } catch (UnsupportedOperationException e) {
        }
        return result;
    }

    private static TaskInfo applyFaceDetectionIntoTaskInfo(Context context, TaskInfo initValue,
            FaceDetectTaskHelper faceDetectTaskHelper) {
        final String methodName = "applyFaceDetectionIntoTaskInfo";
        if ( initValue == null || faceDetectTaskHelper == null ) {
            return initValue;
        }
        TaskInfo taskInfo = initValue;
        int[] faceRect = null;
        int[] outSize = new int[2];

//        FaceDetectTask faceDetectTask;
        FaceDetectTask faceDetectTask = null;                                   
        if(FaceDetectTask.IsOmronEnable)
        {        	        	
        	faceDetectTask = FaceDetectTask.new_task(0);           
        	if(faceDetectTask == null)        	   
        		faceDetectTask = FaceDetectTask.new_task_google(0);
        }
        else         	
        	faceDetectTask = FaceDetectTask.new_task_google(0);        
//        synchronized (LOCK_TASK_HOLD) {
//            faceDetectTask = faceDetectTaskHelper.lockTask();
//        }
        if ( faceDetectTask != null ) {
            if ( StorageManager.isEncrypted() ) {
                faceDetectTask.setEncryptionKey(StorageManager.getEncryptionKey(context));
            }
            faceRect = faceDetectTask.fd_wait(taskInfo.getResultUri(), outSize);
            faceDetectTask.stop();
//            synchronized (LOCK_TASK_RELEASE) {
//                faceDetectTaskHelper.unlockTask(faceDetectTask);
//            }
            //check the picture is invalid image
            if ( outSize[0] == -1 || outSize[1] == -1 ) {
                StorageManager.deleteFileFromUri(taskInfo.getResultUri());
                return taskInfo.applyError(TaskInfo.Error.INVALID_IMAGE)
                        .applyException(new Exception("[" + methodName + "] image bounds is -1"));
            } else if ( outSize[0] == 0 || outSize[1] == 0 ) {
                outSize = getImageBounds(context, taskInfo.getResultUri());
                if ( outSize[0] == -1 || outSize[1] == -1 ) {
                    StorageManager.deleteFileFromUri(taskInfo.getResultUri());
                    return taskInfo.applyError(TaskInfo.Error.INVALID_IMAGE)
                            .applyException(new Exception("[" + methodName + "] image bounds is 0"));
                }
            }
        } else {
            faceRect = null;
            outSize = null;
            taskInfo.applyWarning(TaskInfo.WARNING_CANNOT_EXECUTE_FACE_DETECTION);
        }

        taskInfo.applyResultUriParam("AOI", getAoiValueString(faceRect));
        if ( outSize != null && outSize.length == 2 ) {
            taskInfo.applyResultUriParam("width", "" + outSize[0])
                    .applyResultUriParam("height", "" + outSize[1]);
        }

        return taskInfo;
    }

    /**
     * @hide
     */
    public static String getAoiValueString(int[] aoi) {
        String value = "";
        if ( aoi == null ) {
            return value;
        }
        for (int i = 0; i < aoi.length; i++) {
            if ( i % 4 == 3 && (i + 1) == aoi.length ) {
                value += aoi[i];
            } else if ( i % 4 == 3 ) {
                value += aoi[i] + "|";
            } else {
                value += aoi[i] + ",";
            }
        }
        return value;
    }

    /**
     * @hide
     */
    public static FaceDetectTaskHelper getFaceDetectTaskHelper() {
        if ( sFaceDetectTaskHelper == null ) {
            synchronized (TaskManager.class) {
                if ( sFaceDetectTaskHelper == null ) {
                    sFaceDetectTaskHelper = new FaceDetectTaskHelper();
                }
            }
        }
        return sFaceDetectTaskHelper;
    }

    /**
     * @hide
     */
    public static TaskInfo applyDurationIntoTaskInfo(TaskInfo taskInfo, float duration) {
        if ( taskInfo == null ) {
            return taskInfo;
        }
        return taskInfo.applyResultUriParam("DOWNLOADTIME", "" + duration);
    }

    /**
     * @hide
     */
    public static Bundle retrieveHttpHeaders(Bundle data) {
        Bundle result = null;
        String headerAuthorization = null;
        if ( data != null ) {
            result = data.getBundle(Download.HTTP_HEADERS);
            headerAuthorization = data.getString(Download.HTTP_HEADER_AUTHORIZATION);
        }
        if ( result == null ) {
            result = new Bundle();
        }
        if ( !TextUtils.isEmpty(headerAuthorization) ) {
            result.putString(HTTP_HEADER_KEY_AUTHORIZATION, headerAuthorization);
        }
        return result;
    }

    /**
     * @hide
     */
    public static HttpURLConnection getConnectedHttpURLConnection(URL url, Bundle data, int maxRedirectCount)
            throws IOException {
        if ( maxRedirectCount < 0 ) {
            maxRedirectCount = 0;
        }
        // HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection connection = null;
        URL currentUrl = url;
        for (int i = 0; i <= maxRedirectCount; i++) {
            connection = getHttpURLConnection(currentUrl, data);
            int respondeCode = 0;
            try {
                connection.connect();
                respondeCode = connection.getResponseCode();
            } catch (IOException e) {
                connection.disconnect();
                throw e;
            }
            if ( respondeCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    respondeCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    respondeCode == HttpURLConnection.HTTP_SEE_OTHER ||
                    respondeCode == 307 /* currently not built-in */ ) {
                String location = connection.getHeaderField("Location");
                location = HttpLinkConverter.getAsciiLink(location);
                Exception exception = null;
                try {
                    if ( !TextUtils.isEmpty(location) ) {
                        currentUrl = new URI(location).toURL();
                    } else {
                        currentUrl = null;
                    }
                } catch (MalformedURLException e) {
                    exception = e;
                } catch (URISyntaxException e) {
                    exception = e;
                }
                if ( exception != null ) {
                    currentUrl = null;
                    exception.printStackTrace();
                }
                if ( currentUrl != null && i < maxRedirectCount ) {
                    connection.disconnect();
                    SWLog.i(LOG_TAG, "Try to redirect to: " + currentUrl);
                    continue;
                }
            }
            break;
        }
        return connection;
    }

    private static HttpURLConnection getHttpURLConnection(URL url, Bundle data) throws IOException {
        HttpURLConnection connection = null;
        if ( url == null ) {
            throw new IOException("url should not be null");
        }
        connection = (HttpURLConnection) url.openConnection();
        if ( data != null ) {
            Bundle headers = retrieveHttpHeaders(data);
            for (String headerKey : headers.keySet()) {
                if ( TextUtils.isEmpty(headerKey) ) {
                    continue;
                }
                String headerValue = headers.getString(headerKey);
                if ( headerValue == null ) {
                    headerValue = "";
                }
                connection.addRequestProperty(headerKey, headerValue);
                SWLog.i(LOG_TAG, "" + headerKey + ": " + headerValue);
            }
        }
        connection.setConnectTimeout(TaskManager.HTTP_TIMEOUT);
        connection.setReadTimeout(TaskManager.HTTP_TIMEOUT);
        connection.setUseCaches(true);
        return connection;
    }
}
