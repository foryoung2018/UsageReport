package com.htc.lib1.theme;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @hide Raise exception to AP.
 */
public class ScaleAssetUtil {

    private static final String TAG = "ScaleAssetUtil";

    private static final String ILLEGAL_SRCFILEPATH = "Current srcFilePath is null or srcFilePath.length = 0.";
    private static final String ILLEGAL_OUTPUTPATH = "Current outPutPath is null or outPutPath.length = 0.";
    private static final String ILLEGAL_PATH = "Current outPutPath or srcFilePath.do not meet the requirements.";
    private static final String ILLEGAL_DENSITY = "Please input great than or equal to 120 density.";
    private static final String DECODEFILE_FAIL = "BitmapFactory.decodeFile fail";
    private static final String RATIO = "Resources.getSystem().getDisplayMetrics() = null";
    private static final String OPENFILE_FAIL = "openFile fail";
    private static final String SPECIAL = "With the size of the incoming image processing into image are equal in size, not  scale.";
    private static final String CSBTMA_FAIL = "Bitmap.createScaledBitmap fail";
    private static final String SBTM_FAIL = "Scale Bitmap fail";
    private static final String WRITEFILE_FAIL = "Write asset file failure!";

    private static final boolean DEBUG = ThemeType.DEBUG;

    /**
     * Write the new scaled bitmap into the specified path.
     *
     * @param srcFilePath A set of image paths that you need to deal with.
     * @param outPutPath  The outPutPath that you want to save.
     * @param origDensity The density of the need to deal with pictures of the device.
     * @throws Exception 1. if srcFilePath is null. 2. outPutPath is null. 3.srcFilePath.length is 0.
     *                   4. outPutPath.length is 0. 5. srcFilePath.length is not equal to outPutPath.length.
     *                   6. origDensity less then 120. 7. Other operation has been done.
     */
    public static void scaleAsset(String[] srcFilePath, String[] outPutPath, int origDensity) throws Exception {
        if (null == srcFilePath || 0 == srcFilePath.length) {
            debugIllegal(ILLEGAL_SRCFILEPATH);
        }

        if (null == outPutPath || 0 == outPutPath.length) {
            debugIllegal(ILLEGAL_OUTPUTPATH);
        }

        if (srcFilePath.length != outPutPath.length) {
            debugIllegal(ILLEGAL_PATH);
        }

        if (origDensity < 120) {
            debugIllegal(ILLEGAL_DENSITY);
        }
        writeFile(srcFilePath, outPutPath, origDensity);

    }

    private static Bitmap openFile(String srcFilePath) throws IOException {
        File filePath = new File(srcFilePath);
        if (filePath.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath);
            if (null == bitmap) {
                debugNull(DECODEFILE_FAIL);
            }
            return bitmap;
        } else {
            debugIO("srcFilePath : " + srcFilePath + " does not exist.");
            return null;
        }
    }

    private static float getScaleRatio(int origDensity) {
        final DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        if (null == metrics) {
            debugNull(RATIO);
        }

        float ratio = ((float) metrics.densityDpi / (float) origDensity);
        return ratio;
    }

    private static Bitmap scaleBitmap(String srcFilePath, float ratio) throws Exception {
        Bitmap bitmap = openFile(srcFilePath);
        if (null == bitmap) {
            debugNull(OPENFILE_FAIL);
        }
        int dstWidth = (int) (bitmap.getWidth() * ratio);
        int dstHeight = (int) (bitmap.getHeight() * ratio);
        if (bitmap.getWidth() == dstWidth && bitmap.getHeight() == dstHeight) {
            debug(SPECIAL);
        }
        Bitmap newBmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
        bitmap.recycle();
        if (null == newBmp) {
            debugNull(CSBTMA_FAIL);
        }
        return newBmp;
    }

    private static boolean writeFile(String[] srcFilePath, String[] outPutPath, int origDensity) throws Exception {
        final float ratio = getScaleRatio(origDensity);
        for (int i = 0; i < srcFilePath.length; i++) {
            Bitmap bmp = scaleBitmap(srcFilePath[i], ratio);
            if (null == bmp) {
                debugNull(SBTM_FAIL);
            }
            FileOutputStream out = null;
            File outputpath = new File(outPutPath[i]);
            if (outputpath.getParentFile() != null && !outputpath.getParentFile().isDirectory()) {
                outputpath.getParentFile().mkdirs();
            }
            out = new FileOutputStream(outPutPath[i]);
            if (!bmp.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                debugIO(WRITEFILE_FAIL + "Out Path:" + out);
            }
            if (out != null) {
                out.close();
            }
            bmp.recycle();
        }
        return true;
    }

    private static void debugIllegal(String message) {
        if (DEBUG) {
            Log.e(TAG, message, new Exception());
        }
        throw new IllegalArgumentException(message);
    }

    private static void debugNull(String message) {
        if (DEBUG) {
            Log.e(TAG, message, new Exception());
        }
        throw new NullPointerException(message);
    }

    private static void debugIO(String message) throws IOException {
        if (DEBUG) {
            Log.e(TAG, message, new Exception());
        }
        throw new IOException(message);
    }

    private static void debug(String message) throws Exception {
        if (DEBUG) {
            Log.e(TAG, message, new Exception());
        }
        throw new Exception(message);
    }
}
