package com.htc.lib2.opensense.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.Manifest;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.Manifest.permission;

import com.htc.lib2.opensense.internal.SystemWrapper.Environment;
import com.htc.lib2.opensense.internal.SystemWrapper.SWLog;
import com.htc.lib2.opensense.internal.SystemWrapper.SystemProperties;

/**
 * @hide
 */
public class StorageManager {

    private static final boolean IMAGE_SHOULE_BE_ENCRYPTED = true;
    private static final int CIPHER_STRENGTH = 128;
    private static final long CACHE_SIZE_BF = SystemProperties.getLong("com.htc.opensense.CchSzBF", 20L * 1000 * 1000);
    private static final long CACHE_SIZE_LC = SystemProperties.getLong("com.htc.opensense.CchSzLC", 10L * 1000 * 1000);
    private static final long CACHE_SIZE_EMMC = SystemProperties.getLong("com.htc.opensense.CchSzEMMC", 50L * 1000 * 1000);
    private static final long CACHE_SIZE_EXT = SystemProperties.getLong("com.htc.opensense.CchSzEXT", 200L * 1000 * 1000);
    private static final long SYNC_RANGE = 1000L * 5;
    private static final File BLINKFEED_CACHE_DIR = new File("/blinkfeed");
    private static final File DUMMY_LOCAL_CACHE_DIR = android.os.Environment.getExternalStorageDirectory();
    private static final boolean IS_BLINKFEED_CACHE_DIR_EXISTED = BLINKFEED_CACHE_DIR.exists();
    private static final ConcurrentMap<String, byte[]> RAWKEY_MAP = new ConcurrentHashMap<String, byte[]>();
    private static final ConcurrentMap<String, Long> BLOCKSIZE_MAP = new ConcurrentHashMap<String, Long>();
    private static final ConcurrentMap<String, Boolean> PATH_MAP = new ConcurrentHashMap<String, Boolean>();
    private static final Object LOCK_KEY = new Object();
    private static final String ENCRYPTION_KEY_PROVIDER_AUTHORITY = Download.AUTHORITY /* "com.htc.cachemanager" */;
    private static final String CIPHER_ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String CIPHER_PROVIDER = "BC";
    private static final String CIPHER_IV_STR = "2648171190913351";
    private static final String LOG_TAG = StorageManager.class.getSimpleName();
    private static final String NOMEDIA = ".nomedia";
    private static final String TEMP = "temp";

    private static boolean sIsEncrypted = IMAGE_SHOULE_BE_ENCRYPTED;
    private static long sLastSyncTimestamp = 0L;
    private static List<StorageInfo> sLastStorageInfos = null;
    private static String sEncryptionKey = null;

    /**
     * @hide
     */
    public static void setFileLastModified(Uri uri, long lastModifiedTime) {
        if ( uri == null || !"file".equals(uri.getScheme()) ) {
            return;
        }
        File targetFile = new File(uri.getPath());
        boolean isSuccess = false;
        String parentPath = null;
        File targetParentDir = targetFile.getParentFile();
        if ( targetParentDir != null ) {
            parentPath = targetParentDir.getAbsolutePath();
        }
        if ( !shouldUseTimestampFile(parentPath) ) {
            isSuccess = targetFile.setLastModified(lastModifiedTime);
        }
        if ( !isSuccess ) {
            setUseTimestampFile(parentPath, true);
            if ( !targetFile.exists() ) {
                return;
            }
            File timestampFile = getTimestampFile(targetFile);
            if ( timestampFile == null ) {
                return;
            }
            File timestampParentDir = timestampFile.getParentFile();
            if ( timestampParentDir == null ) {
                return;
            }
            if ( !timestampParentDir.exists() ) {
                timestampParentDir.mkdirs();
            }
            if ( timestampFile.exists() ) {
                timestampFile.delete();
            }
            try {
                timestampFile.createNewFile();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private static boolean shouldUseTimestampFile(String path) {
        if ( TextUtils.isEmpty(path) ) {
            return false;
        }
        Boolean result = PATH_MAP.get(path);
        if ( result == null ) {
            return false;
        }
        return result.booleanValue();
    }

    private static void setUseTimestampFile(String path, boolean value) {
        if ( TextUtils.isEmpty(path) ) {
            return;
        }
        PATH_MAP.put(path, value);
    }

    /**
     * @hide
     */
    public static long getFileLastModified(Uri uri) {
        if ( uri == null || !"file".equals(uri.getScheme()) ) {
            return 0L;
        }
        return getFileLastModified(new File(uri.getPath()));
    }

    /**
     * @hide
     */
    public static long getFileLastModified(File file) {
        long result = 0L;
        if ( file == null ) {
            return result;
        }
        File timestampFile = getTimestampFile(file);
        if ( timestampFile != null && timestampFile.isFile() ) {
            result = Math.max(result, timestampFile.lastModified());
        }
        if ( result <= 0L && file.exists() ) {
            result = Math.max(result, file.lastModified());
        }
        return result;
    }

    private static File getTimestampFile(File file) {
        if ( file == null ) {
            return null;
        }
        return new File(file.getAbsolutePath() + ".t"); // filename.t
    }

    // File I/O access
    /**
     * @hide
     */
    public static long getBlockSizeLong(String path) {
        final long defaultBlockSize = 4096L; // 4k
        if ( path == null ) {
            return defaultBlockSize;
        }
        if ( BLOCKSIZE_MAP.containsKey(path) ) {
            Long blockSize = BLOCKSIZE_MAP.get(path);
            if ( blockSize == null ) {
                return defaultBlockSize;
            }
            return blockSize.longValue();
        }
        StatFs statFs = new StatFs(path);
        long blockSize = 1L * statFs.getBlockSize(); // API 18 and after.
        if ( blockSize == 0L ) {
            blockSize = defaultBlockSize;
        }
        BLOCKSIZE_MAP.put(path, blockSize);
        return blockSize;
    }

    /**
     * @hide
     */
    public static boolean prepareCacheDir(List<StorageInfo> storageInfos) {
        if ( storageInfos == null || storageInfos.isEmpty() ) {
            Log.w(LOG_TAG, "No available storage exist!");
            return false;
        }
        StorageInfo storageInfo = storageInfos.get(0);
        if ( storageInfo == null ) {
            Log.w(LOG_TAG, "No available storage exist!");
            return false;
        }
        String currentCacheDir = getCacheDir(storageInfo);
        File target = new File(currentCacheDir);
        if ( !target.exists() ) {
            Log.i(LOG_TAG, "" + currentCacheDir + " is not prepared, try to re-create it.");
            try {
                target.mkdirs();
                File f = new File(currentCacheDir + File.separatorChar, NOMEDIA);
                if ( !f.exists() ) {
                    f.createNewFile();
                }
            } catch (Exception e) {
                Log.w(LOG_TAG, "Exception happened when preparing " + currentCacheDir + " !", e);
            }
        }
        if ( !target.exists() ) { // double check
            Log.w(LOG_TAG, "" + currentCacheDir + " is still not prepared!");
            return false;
        } else if ( !target.canWrite() ) {
            Log.w(LOG_TAG, "" + currentCacheDir + " is not writable");
            return false;
        }
        return true;
    }

    /**
     * @hide
     */
    public static String getCacheDir(Context context) {
        String currentCacheDir = null;
        List<StorageInfo> storageInfos = getAllStorages(context, Environment.MEDIA_MOUNTED);
        if ( storageInfos != null && !storageInfos.isEmpty() ) {
            currentCacheDir = getCacheDir(storageInfos.get(0));
        }
        return currentCacheDir;
    }

    /**
     * @hide
     */
    public static String getCacheDir(StorageInfo storageInfo) {
        if ( storageInfo == null ) {
            return null;
        }
        return storageInfo.getAbsolutePath() + File.separatorChar + ".data" + File.separatorChar + "CacheManager";
    }

    /**
     * @hide
     */
    public static String getTempDir(StorageInfo storageInfo) {
        if ( storageInfo == null ) {
            return null;
        }
        return getCacheDir(storageInfo) + File.separatorChar + TEMP;
    }

    /**
     * @hide
     */
    public static String getSharedFilePathFromUri(StorageInfo storageInfo, String uri) {
        if ( storageInfo == null || uri == null ) {
            return null;
        }
        String dir = getCacheDir(storageInfo);
        if ( dir == null ) {
            return null;
        }
        return dir + File.separatorChar + uri.hashCode();
    }

    /**
     * @hide
     */
    public static String getTempFilePathFromUri(StorageInfo storageInfo, String uri, int instance, long time) {
        if ( storageInfo == null || uri == null ) {
            return null;
        }
        String dir = getTempDir(storageInfo);
        if ( dir == null ) {
            return null;
        }
        return dir + File.separatorChar +  uri.hashCode() + "_" + instance + "_" + time;
    }

    /**
     * @hide
     */
    @Deprecated
    public static String getTempDir(String currentCacheDir) {
        if ( currentCacheDir == null ) {
            return null;
        }
        return currentCacheDir + File.separatorChar + TEMP;
    }

    /**
     * @hide
     */
    @Deprecated
    public static void checkFolderExist(String dir) {
        if ( dir != null && !new File(dir).exists() ) {
            SWLog.d(LOG_TAG, "Target path is not exist, recreate : " + dir);
            try {
                File dirFile = new File(dir);
                dirFile.mkdirs();
                File f = new File(dir + File.separatorChar, NOMEDIA);
                if ( !f.exists() ) {
                    f.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @hide
     */
    public static InputStream getInputStream(Context context, Uri uri) throws IOException {
        if ( uri == null ) {
            return null;
        }
        InputStream is = new FileInputStream(uri.getPath());
        if ( isEncrypted() ) {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, getEncryptionKey(context), CIPHER_IV_STR);
            if ( cipher != null ) {
                is = new CipherInputStream(is, cipher);
            } else {
                setEncrypted(false); // fall back to unencrypted mode
            }
        }
        return is;
    }

    /**
     * @hide
     */
    public static OutputStream getFileOutputStream(Context context, int cipherMode, File file) throws FileNotFoundException {
        if ( context == null || file == null ) {
            return null;
        }
        OutputStream output = new FileOutputStream(file);
        if ( isEncrypted() ) {
            Cipher cipher = getCipher(cipherMode, getEncryptionKey(context), CIPHER_IV_STR);
            if ( cipher != null ) {
                output = new CipherOutputStream(output, cipher);
            } else {
                setEncrypted(false); // fall back to unencrypted mode
            }
        }
        return output;
    }

    /**
     * @hide
     */
    public static InputStream getFileInputStream(int cpiherMode, Uri uri, String key) throws IOException {
        if ( uri == null ) {
            return null;
        }
        String path = uri.getPath();
        if ( TextUtils.isEmpty(path) ) {
            return null;
        }
        InputStream is = new FileInputStream(path);
        if ( isEncrypted() ) {
            Cipher cipher = getCipher(cpiherMode, key, CIPHER_IV_STR);
            if ( cipher != null ) {
                is = new CipherInputStream(is, cipher);
            } else {
                setEncrypted(false); // fall back to unencrypted mode
            }
        }
        return is;
    }

    /**
     * @hide
     */
    public static String getCipherDigest(Context context) {
        final String cipherStringSuffix = "-" + CIPHER_ALGORITHM + "-" + CIPHER_TRANSFORMATION
                + "-" + CIPHER_PROVIDER + "-" + CIPHER_STRENGTH + "-" + CIPHER_IV_STR;
        final String cipherString = "" + getEncryptionKey(context) + cipherStringSuffix;
        final String cipherDigest = "" + cipherString.hashCode();
        return cipherDigest;
    }

    /**
     * @hide
     */
    public static void setEncrypted(boolean encrypted) {
        if ( encrypted ^ sIsEncrypted ) { // change encryption status
            Log.w(LOG_TAG, "Avalon is from " + sIsEncrypted + " to " + encrypted);
        }
        sIsEncrypted = encrypted;
    }

    /**
     * @hide
     */
    public static boolean isEncrypted() {
        return sIsEncrypted;
    }

    private static byte[] getRawKey(String key) {
        if ( TextUtils.isEmpty(key) ) {
            return null;
        }
        if ( RAWKEY_MAP.containsKey(key) ) {
            return RAWKEY_MAP.get(key);
        }
        String base = null;
        for (String s : getSplittedStrings(key, CIPHER_STRENGTH)) {
            if ( base == null ) {
                base = s;
            } else {
                base = getXorString(base, s);
            }
        }
        byte[] value = null;
        if ( base != null ) {
            value = base.getBytes();
            RAWKEY_MAP.put(key, value);
        }
        return value;
    }

    private static List<String> getSplittedStrings(String key, int bitStrength) {
        List<String> list = new ArrayList<String>();
        if ( TextUtils.isEmpty(key) || bitStrength <= 0 ) {
            return list;
        }
        int length = key.length(); // 64
        int byteStrength = bitStrength / 8; // 16 (from 128bit)
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i += byteStrength) {
            builder.append(key.substring(i, Math.min(length, i + byteStrength)));
            int offset = byteStrength - builder.length();
            if ( offset > 0 ) {
                for (int j = 0; j < offset; j++) {
                    builder.append(" ");
                }
            }
            list.add(builder.toString());
            builder.setLength(0); // clear
        }
        return list;
    }

    private static String getXorString(String base, String xor) {
        if ( TextUtils.isEmpty(base) ) {
            return null;
        }
        if ( TextUtils.isEmpty(xor) ) {
            return base;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < xor.length(); i++) {
            if ( i < base.length() ) {
                char a = (char) (base.charAt(i) ^ xor.charAt(i));
                builder.append(a);
            }
        }
        return builder.toString();
    }

    private static Cipher getCipher(int mode, String key, String ivString) {
        if ( key == null || ivString == null ) {
            return null;
        }
        Cipher cipher = null;
        try {
            SecretKeySpec spec = new SecretKeySpec(getRawKey(key), CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
            cipher = Cipher.getInstance(CIPHER_TRANSFORMATION, CIPHER_PROVIDER);
            if ( cipher == null ) {
                return null;
            }
            cipher.init(mode, spec, iv);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return cipher;
    }

    /**
     * @hide
     */
    public static String getEncryptionKey(Context context) {
        if ( context == null ) {
            return null;
        }
        if ( sEncryptionKey == null ) {
            synchronized (LOCK_KEY) {
                if ( sEncryptionKey == null ) {
                    ContentProviderClient providerClient = null;
                    Cursor c = null;
                    providerClient = context.getContentResolver()
                            .acquireUnstableContentProviderClient(ENCRYPTION_KEY_PROVIDER_AUTHORITY);
                    if ( providerClient != null ) {
                        try {
                            c = providerClient.query(
                                    Download.ENCRYPTION_KEY_URI,
                                    null,
                                    null,
                                    null,
                                    null
                            );
                            if ( c != null && c.moveToNext() ) {
                                if ( "encryption_key".equals(c.getString(0)) ) {
                                    String key = c.getString(1);
                                    sEncryptionKey = key;
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } finally {
                            if ( c != null ) {
                                c.close();
                            }
                            if ( providerClient != null ) {
                                providerClient.release();
                            }
                        }
                    }
                }
            }
        }
        return sEncryptionKey;
    }

    /**
     * @hide
     */
    public static void deleteOldFiles(String currentCacheDir, long timestampToDelete) {
        final String methodName = "deleteOldFiles";
        if ( TextUtils.isEmpty(currentCacheDir) ) {
            return;
        }

        long workingTimestampStart = System.currentTimeMillis();
        long workingPeriodThreshold = 1000L * 5; // 5 sec
        File targetFolder = new File(currentCacheDir);
        if ( targetFolder.isDirectory() ) {
            int cachedFileCount = 0;
            String cachedFileNames = "";
            for (File f : targetFolder.listFiles()) {
                if ( f == null ) {
                    continue;
                }
                long workingTimestampEnd = System.currentTimeMillis();
                long workingPeriod = workingTimestampEnd - workingTimestampStart;
                if ( workingPeriod < 0L ) {
                    break;
                }
                if ( workingPeriod > workingPeriodThreshold ) {
                    SWLog.w(LOG_TAG, "[" + methodName + "] longer than " + workingPeriodThreshold + " ms." );
                    break;
                }
                if ( (timestampToDelete >= 0L) && (getFileLastModified(f) <= timestampToDelete) ) {
                    if ( cachedFileCount == 0 ) {
                        cachedFileNames = cachedFileNames + f.getName();
                    } else {
                        cachedFileNames = cachedFileNames + ", " + f.getName();
                    }
                    f.delete();
                    cachedFileCount++;
                }
            }
            Log.w(LOG_TAG, "[" + methodName + "] Cached: " + cachedFileNames);
        }

        deleteOldTempFiles(currentCacheDir);
    }

    /**
     * @hide
     */
    public static void deleteOldFiles(String currentCacheDir) {
        final String methodName = "deleteOldFiles";
        if ( TextUtils.isEmpty(currentCacheDir) ) {
            return;
        }

        File oldestCachedFile = null;
        long oldestCachedFileTimestamp = System.currentTimeMillis();
        File cacheDir = new File(currentCacheDir);
        if ( cacheDir.isDirectory() ) {
            File[] files = cacheDir.listFiles();
            int cachedFilesSizeThreshold = 60;
            int cachedFilesSize = files.length;
            int oldestCachedFileIndex = 0;
            int oldestCachedFileSentinel = (int) ((Math.random() + 2) * cachedFilesSize / 3); // 66% ~ 99%
            for (File f : files) {
                if ( f == null ) {
                    continue;
                }
                long currentTimestamp = getFileLastModified(f);
                if ( f.isFile() && currentTimestamp < oldestCachedFileTimestamp ) {
                    oldestCachedFileTimestamp = currentTimestamp;
                    oldestCachedFile = f;
                }
                oldestCachedFileIndex++;
                if ( oldestCachedFileIndex >= oldestCachedFileSentinel && cachedFilesSize >= cachedFilesSizeThreshold ) {
                    break;
                }
            }
        }
        if ( oldestCachedFile != null ) {
            Log.w(LOG_TAG, "[" + methodName + "] Cached: " + oldestCachedFile.getName());
            oldestCachedFile.delete();
        }

        deleteOldTempFiles(currentCacheDir);
    }

    private static void deleteOldTempFiles(String currentCacheDir) {
        final String methodName = "deleteOldFiles";
        if ( TextUtils.isEmpty(currentCacheDir) ) {
            return;
        }

        long tempFileTimestamp = System.currentTimeMillis() - 1000L * 60 * 60; // 1 hour ago
        File tempDir = new File(currentCacheDir + File.separator + TEMP);
        if ( tempDir.isDirectory() ) {
            int tempFileCount = 0;
            String tempFileNames = "";
            for (File f : tempDir.listFiles()) {
                if ( f == null ) {
                    continue;
                }
                if ( f.isFile() && getFileLastModified(f) < tempFileTimestamp ) {
                    if ( tempFileCount == 0 ) {
                        tempFileNames = tempFileNames + f.getName();
                    } else {
                        tempFileNames = tempFileNames + ", " + f.getName();
                    }
                    f.delete();
                    tempFileCount++;
                }
                if ( tempFileCount >= 5 ) { // delete 5 old files once
                    break;
                }
            }
            Log.w(LOG_TAG, "[" + methodName + "] Temp: " + tempFileNames);
        }
    }

    /**
     * @hide
     */
    public static boolean deleteFileFromUri(Uri uri) {
        if ( uri == null ) {
            return true;
        }
        String path = uri.getPath();
        if ( TextUtils.isEmpty(path) ) {
            return true;
        }
        File file = new File(path);
        if ( file.exists() ) {
            return file.delete();
        } else {
            return true;
        }
    }

    /**
     * @hide
     */
    public static List<StorageInfo> getAllStorages(Context context, String storageState) {
        long currentTime = System.currentTimeMillis();
        if ( sLastSyncTimestamp + SYNC_RANGE > currentTime ) {
            if ( sLastSyncTimestamp > currentTime ) {
                sLastSyncTimestamp = currentTime; // time correction
            }
            if ( sLastStorageInfos != null ) {
                return sLastStorageInfos;
            }
        }
        List<StorageInfo> storageInfos = new ArrayList<StorageInfo>();

        boolean writeExtStorageGranted = true;
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                writeExtStorageGranted = false;
            }
        }

       if (writeExtStorageGranted) {
            if (Environment.hasRemovableStorageSlot()) {
                // Fuse storage is primary storage, SD is secondary storage
                String firstStorageState = Environment.getExternalStorageState();
                if (storageState == null || storageState.equals(firstStorageState)) {
                    storageInfos.add(new StorageInfo(
                            Environment.getExternalStorageDirectory(),
                            StorageInfo.TYPE.EXT,
                            firstStorageState,
                            CACHE_SIZE_EXT
                    ));
                }
//            String secondStorageState = Environment.getRemovableStorageState();
//            if ( storageState == null || storageState.equals(secondStorageState) ) {
//                storageInfos.add(new StorageInfo(
//                        Environment.getRemovableStorageDirectory(),
//                        StorageInfo.TYPE.SDCARD,
//                        secondStorageState,
//                        CACHE_SIZE_SDCARD
//                ));
//            }
                if (Environment.hasPhoneStorage()) {
                    String thirdStorageState = Environment.getPhoneStorageState();
                    if (storageState == null || storageState.equals(thirdStorageState)) {
                        storageInfos.add(new StorageInfo(
                                Environment.getPhoneStorageDirectory(),
                                StorageInfo.TYPE.EMMC,
                                thirdStorageState,
                                CACHE_SIZE_EMMC
                        ));
                    }
                }
            } else {
                if (Environment.hasPhoneStorage()) {
                    // SD is primary storage, eMMC is secondary storage
                    String firstStorageState = Environment.getExternalStorageState();
                    if (storageState == null || storageState.equals(firstStorageState)) {
                        storageInfos.add(new StorageInfo(
                                Environment.getExternalStorageDirectory(),
                                StorageInfo.TYPE.EXT,
                                firstStorageState,
                                CACHE_SIZE_EXT
                        ));
                    }
                    String secondStorageState = Environment.getPhoneStorageState();
                    if (storageState == null || storageState.equals(secondStorageState)) {
                        storageInfos.add(new StorageInfo(
                                Environment.getPhoneStorageDirectory(),
                                StorageInfo.TYPE.EMMC,
                                secondStorageState,
                                CACHE_SIZE_EMMC
                        ));
                    }
                } else {
                    try {
                        if (Environment.isExternalStorageEmulated()) {
                            // Only Fuse
                            String firstStorageState = Environment.getExternalStorageState();
                            if (storageState == null || storageState.equals(firstStorageState)) {
                                storageInfos.add(new StorageInfo(
                                        Environment.getExternalStorageDirectory(),
                                        StorageInfo.TYPE.EXT,
                                        firstStorageState,
                                        CACHE_SIZE_EXT
                                ));
                            }
                        } else {
                            // Only SD
                            String firstStorageState = Environment.getExternalStorageState();
                            if (storageState == null || storageState.equals(firstStorageState)) {
                                storageInfos.add(new StorageInfo(
                                        Environment.getExternalStorageDirectory(),
                                        StorageInfo.TYPE.EXT,
                                        firstStorageState,
                                        CACHE_SIZE_EXT
                                ));
                            }
                        }
                    }catch(IllegalArgumentException ex)
                    {
                        Log.d(LOG_TAG, "android.os.Environment exception : " + ex);
                    }
                }
            }
        }

        String blinkfeedStorageState = getBlinkfeedStorageState();
        if ( storageState == null || storageState.equals(blinkfeedStorageState) ) {
            storageInfos.add(new StorageInfo(
                    getBlinkfeedStorageDirectory(),
                    StorageInfo.TYPE.BF,
                    blinkfeedStorageState,
                    CACHE_SIZE_BF
            ));
        }

        String localCacheStorageState = getLocalCacheStorageState(context);
        if ( storageState == null || storageState.equals(localCacheStorageState) ) {
            storageInfos.add(new StorageInfo(
                    getLocalCacheStorageDirectory(context),
                    StorageInfo.TYPE.LC,
                    localCacheStorageState,
                    CACHE_SIZE_LC
            ));
        }

        sLastSyncTimestamp = currentTime;
        sLastStorageInfos = storageInfos;
        return storageInfos;
    }

    private static File getBlinkfeedStorageDirectory() {
        return BLINKFEED_CACHE_DIR;
    }

    private static File getLocalCacheStorageDirectory(Context context) {
        if ( context != null ) {
            return context.getCacheDir();
        } else {
            return DUMMY_LOCAL_CACHE_DIR;
        }
    }

    // Not safe in UI thread
    private static String getBlinkfeedStorageState() {
        if ( IS_BLINKFEED_CACHE_DIR_EXISTED ) {
            return Environment.MEDIA_MOUNTED;
        } else {
            return Environment.MEDIA_REMOVED;
        }
    }

    private static String getLocalCacheStorageState(Context context) {
        if ( context != null ) {
            return Environment.MEDIA_MOUNTED;
        } else {
            return Environment.MEDIA_REMOVED;
        }
    }

    /**
     * @hide
     */
    public static class StorageInfo {

        /**
         * @hide
         */
        public enum TYPE {
            NONE,
            BF,
            EMMC,
            EXT,
            LC
        }

        private File mFile = null;
        private TYPE mType = TYPE.NONE;
        private String mState = "";
        private long mLimitSize = -1L;
        private long mRemainingUpperBound = 0;
        private long mRemainingLowerBound = 0;

        private StorageInfo(File file, TYPE type, String state, long sizeLimit) {
            mFile = file;
            if ( mFile == null ) {
                mType = TYPE.NONE;
            } else {
                mType = type;
            }
            if ( state != null ) {
                mState = state;
            }
            if ( sizeLimit > 0L ) {
                mLimitSize = sizeLimit;
            }

            if ( mLimitSize > 0 ) {
                mRemainingUpperBound = (long) (mLimitSize * 0.6);
            }
            if ( mLimitSize > 0 ) {
                mRemainingLowerBound = (long) (mLimitSize * 0.2);
            }
        }

        /**
         * Get the absolute path of this storage info
         * 
         * @return the absolute path of this storage info
         * 
         * @hide
         */
        public String getAbsolutePath() {
            if ( mFile == null ) {
                return "";
            } else {
                return mFile.getAbsolutePath();
            }
        }

        /**
         * @hide
         */
        public TYPE getType() {
            return mType;
        }

        /**
         * Get the state of this storage info
         * 
         * @return the state of this storage info
         * 
         * @hide
         */
        public String getState() {
            return mState;
        }

        /**
         * @hide
         */
        public long getLimitSize() {
            return mLimitSize;
        }

        /**
         * @hide
         */
        public long getRemainingUpperBound() {
            return mRemainingUpperBound;
        }

        /**
         * @hide
         */
        public long getRemainingLowerBound() {
            return mRemainingLowerBound;
        }
    }
}
