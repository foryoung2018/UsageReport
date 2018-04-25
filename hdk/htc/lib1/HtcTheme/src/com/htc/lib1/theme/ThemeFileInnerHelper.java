package com.htc.lib1.theme;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 6/27/16.
 */
public class ThemeFileInnerHelper {
    private static String LOG_TAG = "ThemeFileInnerHelper";
    private static final String CHECKSUM_ALGORITHM_NAME = "MD5";//SHA-256

    private static final int MAX_READBUFFER = 4096;

    public static void deleteFolderFile(String strPath, boolean deleteRoot) {
        ThemeSettingUtil.logd(LOG_TAG, "deleteFolderFile %s + ", strPath);

        File directory = new File(strPath);
        try {
            deleteDirectory(directory, deleteRoot);
            ThemeSettingUtil.logd(LOG_TAG, "deleteFolderFile success %b", deleteRoot);
        } catch (IOException e) {
            ThemeSettingUtil.logw(LOG_TAG, "deleteFolderFile %s", e);
        }
        ThemeSettingUtil.logw(LOG_TAG, "deleteFolderFile %s - ", strPath);
    }

    private static void deleteDirectory(final File directory, boolean deleteRoot) throws IOException {
        if (!directory.exists()) {
            return;
        }

        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }

        if (deleteRoot) {
            if (!directory.delete()) {
                final String message = "Unable to delete directory " + directory
                        + ".";
                throw new IOException(message);
            }
//            closeOpenFd(directory);
        }
    }

    private static void cleanDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) { // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    static void forceDelete(String dirPath, final String dstName) throws IOException {
        File dstf = new File(dirPath, dstName);
        forceDelete(dstf);
    }

    static void forceDelete(final File file) throws IOException {
        if (!file.exists()) {
            ThemeSettingUtil.logd(LOG_TAG, "forceDelete file %s not exist", file.getPath());
            return;
        }

        ThemeSettingUtil.logd(LOG_TAG, "forceDelete %s", file.getPath());

        if (file.isDirectory()) {
            deleteDirectory(file, true);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: "
                            + file);
                }
                final String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
//            closeOpenFd(file);
        }
    }

    private static boolean isSymlink(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }

        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            final File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }

        if (fileInCanonicalDir.getCanonicalFile().equals(
                fileInCanonicalDir.getAbsoluteFile())) {
            return false;
        } else {
            return true;
        }
    }

    static boolean copyToFile(File src, File dstTmp, File dstActual) {
        ThemeSettingUtil.logd(LOG_TAG, "copyToFile src %s, dstTmp %s, dst %s", src.getPath(), dstTmp.getPath(), dstActual.getPath());
        if (copyToFile(src, dstTmp)) {
            if (!ensureFileBeforeCopy(dstActual)) {
                return false;
            }

            if (dstTmp.renameTo(dstActual)) {
                ThemeSettingUtil.logd(LOG_TAG, "copyToFile- %s", dstActual.getPath());
                return true;
            }
        }
        return false;
    }

    static boolean copyToFile(File src, File dst) {
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            ThemeSettingUtil.logd(LOG_TAG, "copyToFile src %s, dst %s", src.getPath(), dst.getPath());

            if (!src.exists()) {
                ThemeSettingUtil.logw(LOG_TAG, "copyToFile but src not exist");
                return false;
            }

            if (!ensureFileBeforeCopy(dst)) {
                return false;
            }

            if (src.isDirectory() && dst.isDirectory()) {
                final File[] srcDirfiles = src.listFiles();
                boolean result = true;
                for (final File srcDirFile : srcDirfiles) {
                    File dstDirFile = new File(dst.getPath() + srcDirFile.getName());
                    result = result & copyToFile(srcDirFile, dstDirFile);
                }

                return result;
            }

            inStream = new FileInputStream(src);
            outStream = new FileOutputStream(dst);
            copyToFile(inStream, outStream);
            inStream.close();
            outStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            if (dst.exists()) {
                dst.delete();
            }
        } finally {

            try {
                if (inStream != null)
                    inStream.close();

                if (outStream != null)
                    outStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    static boolean copyToFile(InputStream inStream, String dirPath, String dstName) {
        return copyToFile(inStream, new File(dirPath, dstName));
    }

    static boolean copyToFile(InputStream inStream, File fDestTmp, File fDest) {
        if (copyToFile(inStream, fDestTmp)) {
            if (!ensureFileBeforeCopy(fDest)) {
                return false;
            }

            if (fDestTmp.renameTo(fDest)) {
                ThemeSettingUtil.logd(LOG_TAG, "copyToFile- %s", fDest.getPath());
                return true;
            }
        }

        return false;
    }

    static boolean copyToFile(InputStream inStream, File fDest) {
        if (fDest == null)
            return false;

        ThemeSettingUtil.logd(LOG_TAG, "copyToFile %s", fDest.getPath());

        if (!ensureFileBeforeCopy(fDest)) {
            return false;
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fDest);
            return copyToFile(inStream, fos);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    static boolean copyToFile(InputStream inStream, FileOutputStream fos) {
        try {
            try {
                byte[] buffer = new byte[MAX_READBUFFER];
                int nReadBytes;
                while ((nReadBytes = inStream.read(buffer)) >= 0) {
                    fos.write(buffer, 0, nReadBytes);
                }
            } finally {
                fos.flush();
                try {
                    fos.getFD().sync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fos.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean ensureFileBeforeCopy(File f) {
        if (f.exists()) {
            f.delete();
        } else {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                ThemeSettingUtil.logd(LOG_TAG, "%s create fail", f.getPath());
                return false;
            }
        }
        return true;
    }

    static String getChecksum(String dir) {
        ThemeSettingUtil.logd(LOG_TAG, "getChecksum %s", dir);
        String result = "";
        try {
            result = getChecksum(dir, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getChecksum(String dir, boolean getHash) throws Exception {
        String checksum = "";
        File folder = new File(dir);
        if (!folder.exists())
            return checksum;

        File[] files = folder.listFiles();
        if (files == null) {
            checksum += getChecksumInner(folder.toString());
        } else {
            for (File file : files) {
                checksum += getChecksum(file.toString(), false);
            }
        }
        if (getHash)
            checksum = getChecksumHashOfString(checksum);

        return checksum;
    }

    private static String getChecksumInner(String filePath) throws Exception {
        String returnVal = "";

            InputStream input = new FileInputStream(filePath);
            byte[] buffer = new byte[MAX_READBUFFER];
            MessageDigest checksumHash = MessageDigest.getInstance(CHECKSUM_ALGORITHM_NAME);
            int numRead = 0;
            while (numRead != -1) {
                numRead = input.read(buffer);
                if (numRead > 0) {
                    checksumHash.update(buffer, 0, numRead);
                }
            }
            input.close();

            byte[] checksumBytes = checksumHash.digest();
            for (int i = 0; i < checksumBytes.length; i++) {
                returnVal += Integer.toString((checksumBytes[i] & 0xff) + 0x100, 16)
                        .substring(1);
            }


        return returnVal.toUpperCase();
    }

    private static String getChecksumHashOfString(String str) throws Exception {
        MessageDigest checksum;
        StringBuffer hexString = new StringBuffer();

            checksum = MessageDigest.getInstance(CHECKSUM_ALGORITHM_NAME);
            checksum.reset();
            checksum.update(str.getBytes());
            byte messageDigest[] = checksum.digest();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer
                        .toHexString((0xF0 & messageDigest[i]) >> 4));
                hexString.append(Integer.toHexString(0x0F & messageDigest[i]));
            }

        return hexString.toString();
    }
}
