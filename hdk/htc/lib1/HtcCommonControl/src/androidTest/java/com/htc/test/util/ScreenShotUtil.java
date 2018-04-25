package com.htc.test.util;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.htc.test.HtcActivityTestCaseBase;
import com.htc.test.IFBSnapshotService;
import com.robotium.solo.Solo;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ScreenShotUtil {
    private final static String KEY_RET_CODE = "return_code";
    private final static String KEY_RET_MESSAGE = "return_message";
    private final static int RET_OK = 0;
    private final static int RET_NOT_EXIST = -1;
    private final static int RET_NOT_SAME_WITH_RESOLUTION = -2;
    private final static int RET_NOT_SAME_WITH_DEVICE = -3;

    private final static String LOG_TAG = "ScreenShotUtil";
    private final static String EXPECT_SET = "/CommonControlExpectedUI/";
    private final static String WIDGET_SNAPSHOT = "/WidgetSnapShot/";
    private final static String Diff_SNAPSHOT = "/DiffSnapShot/";
    private final static String Diff_EXTENSION = "_diff";
    private final static String EXPECT_EXTENSION = "_expected";

    private static boolean orientationMark = true;
    private static boolean themeMask = true;

    public final static String FILETYPE_PNG = ".png";
    public final static String FILETYPE_TXT = ".txt";

    public static String EXTRA_INFO = null;

    private static DisplayMetrics mDisplayMetrics = null;

    private static Context getContext(Solo solo, View view) {
        Context c = null;
        if (null != view) {
            c = view.getContext();
        }

        if (null == c) {
            c = solo.getCurrentActivity();
        }

        return c;
    }

    public static DisplayMetrics setResolutionInformation(Solo solo, View view) {
        return setResolutionInformation(getContext(solo, view));
    }

    public static DisplayMetrics setResolutionInformation(Context context) {
        if (null == context)
            return null;

        if (null == mDisplayMetrics)
            mDisplayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);

        return mDisplayMetrics;
    }

    private static String getResolutionNormalization(
            DisplayMetrics displayMetrics) {
        if (null == displayMetrics)
            return null;

        if (displayMetrics.widthPixels < displayMetrics.heightPixels) {
            return String.format("%dx%d", displayMetrics.widthPixels,
                    displayMetrics.heightPixels);
        } else {
            return String.format("%dx%d", displayMetrics.heightPixels,
                    displayMetrics.widthPixels);
        }
    }

    private static String getBasePath(boolean bResolution, String baseName) {
        String resolution = null;
        if (true == bResolution) {
            resolution = getResolutionNormalization(mDisplayMetrics);
            if (null == resolution)
                return null;
        }

        StringBuilder sbDir = new StringBuilder();
        File directory = InstrumentationRegistry.getContext().getDataDir();
        sbDir.append(directory);
        sbDir.append(baseName);
        directory = new File(sbDir.toString());
        if (!directory.exists())
            if (!directory.mkdir())
                return null;
        if (bResolution)
            sbDir.append(resolution);
        else
            sbDir.append(Build.DEVICE);
        sbDir.append('/');
        directory = new File(sbDir.toString());
        if (!directory.exists())
            if (!directory.mkdir())
                return null;

        return sbDir.toString();
    }

    private static String getFilePath(boolean bResolution, String baseName,
            String name, String fileType) {
        String base = getBasePath(bResolution, baseName);
        if (null == base)
            return null;
        StringBuilder sb = new StringBuilder(base);
        sb.append(name).append(fileType);
        return sb.toString();
    }

    public static String getExpectSetPathBase() {
        return getBasePath(false, EXPECT_SET);
    }

    public static String getScreenShotPathBase() {
        return getBasePath(false, WIDGET_SNAPSHOT);
    }

    public static String getDiffSnapShotPathBase() {
        return getBasePath(false, Diff_SNAPSHOT);
    }

    public static String getExpectSetFilePath(String name) {
        return getExpectSetFilePath(false, name, FILETYPE_PNG);
    }

    public static String getExpectSetFilePath(boolean bResolution, String name, String fileType) {
        return getFilePath(bResolution, EXPECT_SET, name, fileType);
    }

    public static String getScreenShotFilePath(String name, String fileType) {
        return getFilePath(false, WIDGET_SNAPSHOT, name, fileType);
    }

    public static String getDiffSnapShotFilePath(String name) {
        return getFilePath(false, Diff_SNAPSHOT, name, FILETYPE_PNG);
    }

    public static String takeScreenShot(final Solo solo, final View view,
            final String name, final Rect[] excludeArea) {
        setResolutionInformation(solo, view);
        if (null == view || null == name)
            return null;

        final Lock lockObj= new Lock();

        final String sFileName = getScreenShotFilePath(name, FILETYPE_PNG);
        solo.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.destroyDrawingCache();
                    view.buildDrawingCache(false);
                    Bitmap b = view.getDrawingCache();

                    if (null != b ) {
                        if ( null != excludeArea) {
                            Bitmap excludeResult = Bitmap.createBitmap(
                                    b.getWidth(), b.getHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas c = new Canvas(excludeResult);
                            c.setMatrix(new Matrix());
                            c.save();
                            for (Rect r : excludeArea) {
                                if (null != r)
                                    c.clipRect(r, Region.Op.DIFFERENCE);
                            }
                            Paint p = new Paint();
                            c.drawBitmap(b, 0, 0, p);
                            c.restore();
                            b.recycle();
                            b = excludeResult;
                    }

                        saveBitmap(b, sFileName);
                        view.destroyDrawingCache();
                        if (!b.isRecycled())
                            b.recycle();
                    } else {
                        Log.e(LOG_TAG, "Can't get the snapshot \"" + sFileName
                                + "\".");
                    }
                    lockObj.unlockAndNotify();
                }
            }

        });
        lockObj.waitUnlock(10000);
        // solo.sleep(5000);
        return sFileName;
    }

    static private boolean sEnableSnapShotService = false;
    static private IFBSnapshotService mIRemoteService;
    static private Object mSnapshotLock = new Object();
    private static ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            synchronized (mSnapshotLock) {
                if (null == mIRemoteService) {
                    if ( sEnableSnapShotService ) {
                        mIRemoteService = IFBSnapshotService.Stub
                                .asInterface(service);
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            synchronized (mSnapshotLock) {
                if (null == mIRemoteService) {
                    mIRemoteService = null;
                }
            }
        }
    };

    public static void initSnapshotService(Context c) {
        if (null == c)
            return;

        if ( sEnableSnapShotService ) {
            Intent intent = new Intent(IFBSnapshotService.class.getName());
            c.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public static void freeSnapshotService(Context c) {
        if (null == c)
            return;

        if ( sEnableSnapShotService ) {
            c.unbindService(mConnection);
        }
    }

    private static Bitmap takeScreenShotBySurface(Context context,
            final String name, int minLayer, int maxLayer) {
        Assert.assertTrue("shotsnap service not exist",
                (null != mIRemoteService));
        if (null == context)
            return null;

        Bitmap b = null;
        try {
            if ( sEnableSnapShotService ) {
                b = mIRemoteService.takeSnapShotByBitmap(minLayer, maxLayer);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Assert.assertEquals("No snapshot", true, (null != b));
        return b;
        // String n = ScreenShotUtil.getScreenShotFilePath(name);
        // try {
        // n = mIRemoteService.takeSnapShot(name, minLayer, maxLayer);
        // } catch (RemoteException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // Assert.assertEquals("No snapshot", true, (null != n));
        // return decodeBitmap(true, name);
    }

    public static boolean assertSystemUI(final String name) {
        Assert.assertTrue(
                "shotsnap service not exist",
                (null != mIRemoteService) && (null != name)
                        && (0 <= name.length()));

        Map ret = null;
        try {
            if ( sEnableSnapShotService ) {
                ret = mIRemoteService.compareBitmapEqualBefore(name);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertMapResult(ret);

        return true;
    }

    public static boolean assertMultipleWindow(TestCase testcase) {
        Assert.assertTrue("shotsnap service not exist",
                (null != mIRemoteService) && (null != testcase));

        String name = getScreenShotName(testcase);

        Assert.assertTrue((null != name) && (0 < name.length()));
        Map ret = null;
        try {

            if ( sEnableSnapShotService ) {
                ret = mIRemoteService.compareBitmapEqualBefore(name);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertMapResult(ret);

        return true;
    }

    public static String takeScreenShotBySurface(final Solo solo,
            final View view, final String name, final Rect[] excludeArea) {
        setResolutionInformation(solo, view);

        Context c = getContext(solo, view);
        if (null == name || null == c)
            return null;

        final String sFileName = getScreenShotFilePath(name, FILETYPE_PNG);
        Bitmap b = takeScreenShotBySurface(c, sFileName, 0, 65536);
        if (null == b)
            return null;

        saveBitmap(b, sFileName);
        if (!b.isRecycled())
            b.recycle();

        return sFileName;
    }

    public static String takeScreenShotByDraw(final Solo solo, final View view,
            final String name, final Rect[] excludeArea) {
        setResolutionInformation(solo, view);
        if (null == view || null == name)
            return null;

        final String sFileName = getScreenShotFilePath(name, FILETYPE_PNG);
        Bitmap excludeResult = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(excludeResult);
        c.setMatrix(new Matrix());
        c.save();
        for (Rect r : excludeArea) {
            if (null != r)
                c.clipRect(r, Region.Op.DIFFERENCE);
        }
        view.draw(c);
        c.restore();
        saveBitmap(excludeResult, sFileName);
        if (!excludeResult.isRecycled())
            excludeResult.recycle();

        return sFileName;
    }

    public static boolean compareTo(Bitmap bitmap, Bitmap reference,
            int threshold) {
        if (bitmap.getConfig() != reference.getConfig()
                || bitmap.getWidth() != reference.getWidth()
                || bitmap.getHeight() != reference.getHeight()) {
            return false;
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if ( bitmap.getPixel(i, j) != reference.getPixel(i, j))
                    return false;
            }
        }

        return true;
    }

    public static Bitmap decodeBitmap(boolean bAssert, String file) {
        if (null == file || file.length() <= 0)
            return null;

        InputStream is = null;
        Bitmap b = null;
        try {
            is = new FileInputStream(file);
            if (bAssert)
                Assert.assertNotNull(file + " not exist", is);
            if (null != is) {
                b = BitmapFactory.decodeStream(is);
                if (bAssert)
                    Assert.assertNotNull(file + " can't be decoded", b);
                is.close();
            }
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "decode bitmap " + file, e);
        } catch (IOException e) {
            Log.d(LOG_TAG, "decode bitmap " + file, e);
        }

        return b;
    }

    public static boolean AssertViewEqualBefore(Solo solo, final View view,
            final String name, Rect[] excludeRect, boolean byDraw) {
        if (byDraw)
            takeScreenShotByDraw(solo, view, name, excludeRect);
        else
            takeScreenShot(solo, view, name, excludeRect);
        // Assert.assertFalse("Build.DEVICE can't be equal \"unknown\"",
        // Build.DEVICE.equals("unknown"));

        Map ret = compareExpectAndCurrent(name);
        assertMapResult(ret);

        return true;
    }

    private static void assertMapResult(Map ret) {
        Assert.assertTrue("ret can't be null", null != ret);
        Assert.assertTrue("ret{KEY_RET_CODE} can't be null",
                null != ret.get(KEY_RET_CODE));
        Assert.assertTrue("ret{KEY_RET_MESSAGE} can't be null",
                null != ret.get(KEY_RET_MESSAGE));
        int code = ((Integer) ret.get(KEY_RET_CODE));
        String msg = (String) ret.get(KEY_RET_MESSAGE);
        if (null == EXTRA_INFO) {
            Assert.assertEquals(code + ":" + msg, true, RET_OK == code);
        } else {
            Assert.assertEquals(code + ":" + msg + " , EXTRA_INFO:"
                    + EXTRA_INFO, true, RET_OK == code);
        }
    }

    private static void safeRecycleBitmap(Bitmap... bitmapList) {
        for (int i = 0, len=bitmapList.length; i < len; i++) {
            Bitmap b = bitmapList[i];
            if (null != b && !b.isRecycled()) {
                b.recycle();
            }
        }
    }

    public static Map compareExpectAndCurrent(String name) {
        String sExpect = getExpectSetFilePath(name);
        String sExpectResoltion = getExpectSetFilePath(true, name, FILETYPE_PNG);
        String sCurrent = getScreenShotFilePath(name, FILETYPE_PNG);

        Bitmap bExpect = decodeBitmap(false, sExpect);
        Bitmap bExpectResolution = decodeBitmap(false, sExpectResoltion);
        Bitmap bCurrent = decodeBitmap(true, sCurrent);

        HashMap ret = new HashMap();
        ret.put(KEY_RET_CODE, RET_OK);
        ret.put(KEY_RET_MESSAGE, "");

        if (null == bExpect && null == bExpectResolution) {
            ret.put(KEY_RET_CODE, RET_NOT_EXIST);
            ret.put(KEY_RET_MESSAGE, "Both \"" + sExpect + "\" and \""
                    + sExpectResoltion + "\" are null");

            safeRecycleBitmap(bExpect, bExpectResolution, bCurrent);
            return ret;
        }

        if (null != bExpect) {
            boolean b = compareTo(bCurrent, bExpect, 0);
            if (false == b) {
                saveDiffBitmap(bCurrent, bExpect, name);
                ret.put(KEY_RET_CODE, RET_NOT_SAME_WITH_DEVICE);
                ret.put(KEY_RET_MESSAGE, "\"" + sCurrent
                        + "\" is not the same with \"" + sExpect + "\"");
            }
        } else {
            boolean b = compareTo(bCurrent, bExpectResolution, 0);
            if (false == b) {
                saveDiffBitmap(bCurrent, bExpectResolution, name);
                ret.put(KEY_RET_CODE, RET_NOT_SAME_WITH_RESOLUTION);
                ret.put(KEY_RET_MESSAGE, "\"" + sCurrent
                        + "\" is not the same with \"" + sExpectResoltion
                        + "\"");
            }
        }

        safeRecycleBitmap(bExpect, bExpectResolution, bCurrent);

        return ret;
    }

    public static boolean AssertViewEqualBefore(Solo solo, final View view,
            final String name, Rect[] excludeRect) {
        return AssertViewEqualBefore(solo, view, name, excludeRect, false);
    }

    public static boolean AssertViewEqualBefore(Solo solo, final View view,
            final String name) {
        return AssertViewEqualBefore(solo, view, name, (Rect[]) null);
    }

    public static String getScreenShotName(TestCase testcase) {
        StringBuilder sb = new StringBuilder(testcase.getClass()
                .getSimpleName());
        sb.append("_").append(testcase.getName());
        if (testcase instanceof HtcActivityTestCaseBase) {
            if (testcase instanceof HtcActivityTestCaseBase) {
                if (orientationMark) {
                    int orientation = ((HtcActivityTestCaseBase) testcase)
                            .getOrientation();
                    if (orientation == Solo.PORTRAIT) {
                        sb.append("_Portrait");
                    } else if (orientation == Solo.LANDSCAPE) {
                        sb.append("_Landscape");
                    }
                }
                String fontStyle = ((HtcActivityTestCaseBase) testcase)
                        .getFontStyle();
                if (null != fontStyle && !fontStyle.equals("")) {
                    sb.append("_").append(fontStyle);
                }
            }

            if (themeMask) {
                String themeName = ((HtcActivityTestCaseBase) testcase)
                        .getThemeName();
                if (null == themeName) {
                    themeName = "HtcDeviceDefault";
                }
                sb.append("_").append(themeName);
            }
        }
        return sb.toString();
    }

    public static boolean AssertViewEqualBefore(Solo solo, final View view,
            TestCase testcase, Rect[] excludeRect, boolean byDraw) {
        return AssertViewEqualBefore(solo, view, getScreenShotName(testcase),
                excludeRect, byDraw);
    }

    public static boolean AssertViewEqualBefore(Solo solo, final View view,
            TestCase testcase) {
        return AssertViewEqualBefore(solo, view, testcase, null, false);
    }

    public static boolean AssertViewEqualBefore(Solo solo, final Bitmap bitmap, TestCase testcase) {
        return AssertViewEqualBefore(null, solo, bitmap, testcase);
    }

    public static boolean AssertViewEqualBefore(Context context, final Bitmap bitmap,
            TestCase testcase) {
        return AssertViewEqualBefore(context, null, bitmap, testcase);

    }

    public static boolean AssertViewEqualBefore(Context context, Solo solo, final Bitmap bitmap,
            TestCase testcase) {
        String name = getScreenShotName(testcase);
        if (null != context) {
            takeScreenShot(context, bitmap, name);
        } else if (null != solo) {
            takeScreenShot(solo, bitmap, name);
        }

        Map ret = compareExpectAndCurrent(name);
        assertMapResult(ret);
        return true;

    }

    public static String takeScreenShot(final Solo solo, final Bitmap bitmap, final String name) {
        return takeScreenShot(null, solo, bitmap, name);
    }

    public static String takeScreenShot(final Context context, final Bitmap bitmap,
            final String name) {
        return takeScreenShot(context, null, bitmap, name);
    }

    public static String takeScreenShot(final Context context, final Solo solo,
            final Bitmap bitmap,
            final String name) {
        if (null == bitmap || null == name) {
            return null;
        }

        if (null != context) {
            setResolutionInformation(context);
        } else if (null != solo) {
            setResolutionInformation(solo, null);
        }

        final String sFileName = getScreenShotFilePath(name, FILETYPE_PNG);
        if (null != bitmap) {
            saveBitmap(bitmap, sFileName);
        }
        return sFileName;
    }

    public static boolean AssertViewEqualBefore(Solo solo,
            final View container, final String name, View[] excludeChild) {
        if (null == excludeChild)
            return AssertViewEqualBefore(solo, container, name);

        int[] locationContainer = new int[2];
        int[] locationChild = new int[2];

        Rect[] rectList = new Rect[excludeChild.length];
        for (int i = 0, len = excludeChild.length; i < len; i++) {
            View childView = excludeChild[i];
            if (null == childView) {
                rectList[i] = null;
                continue;
            }

            container.getLocationInWindow(locationContainer);
            childView.getLocationInWindow(locationChild);

            int left = locationChild[0] - locationContainer[0];
            int top = locationChild[1] - locationContainer[1];
            rectList[i] = new Rect(left, top, left + childView.getWidth(), top
                    + childView.getHeight());
        }
        return AssertViewEqualBefore(solo, container, name, rectList);
    }

    private static String saveDiffBitmap(Bitmap bitmap1, Bitmap bitmap2,
            String name) {
        int width = Math.max(bitmap1.getWidth(), bitmap2.getWidth());
        int height = Math.max(bitmap1.getHeight(), bitmap2.getHeight());
        Bitmap diff = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        try {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    boolean inBounds1 = i < bitmap1.getWidth()
                            && j < bitmap1.getHeight();
                    boolean inBounds2 = i < bitmap2.getWidth()
                            && j < bitmap2.getHeight();
                    int color;

                    if (inBounds1 && inBounds2) {
                        int color1 = bitmap1.getPixel(i, j);
                        int color2 = bitmap2.getPixel(i, j);
                        color = color1 == color2 ? color1 : Color.RED;
                    } else if (inBounds1 && !inBounds2) {
                        color = Color.BLUE;
                    } else if (!inBounds1 && inBounds2) {
                        color = Color.GREEN;
                    } else {
                        color = Color.MAGENTA;
                    }
                    diff.setPixel(i, j, color);
                }
            }

            saveBitmap(bitmap1, getDiffSnapShotFilePath(name));
            saveBitmap(bitmap2,
                    getDiffSnapShotFilePath(name + EXPECT_EXTENSION));
            return saveBitmap(diff, getDiffSnapShotFilePath(name
                    + Diff_EXTENSION));
        } finally {
            diff.recycle();
        }
    }

    public static String saveBitmap(Bitmap bitmap, String name) {
        if (null == bitmap || null == name || 0 >= name.length())
            return null;

        File fileToSave = new File(name);
        try {
            FileOutputStream fos = new FileOutputStream(fileToSave);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos) == false)
                Log.d(LOG_TAG, "Compress/Write failed");
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.d(LOG_TAG,
                    "Can't save the screenshot! Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.");
            return null;
        }

        return name;
    }

    public static void setOrientationMark(boolean mark) {
        orientationMark = mark;
    }

    public static void setThemeMask(boolean mask) {
        themeMask = mask;
    }

    public static boolean AssertMultipleViewEqualBefore(final Solo solo, final View[] view,
            final TestCase testcase, final Instrumentation ins) {
        return AssertMultipleViewEqualBefore(solo, view, getScreenShotName(testcase), ins);
    }

    private static boolean AssertMultipleViewEqualBefore(final Solo solo, final View[] viewArray,
            final String name, final Instrumentation ins) {
        if (null == viewArray) {
            return false;
        }
        setResolutionInformation(solo, viewArray[0]);
        takeMultipleViewScreenShot(solo, viewArray, name, ins);
        final Map ret = compareExpectAndCurrent(name);
        assertMapResult(ret);
        return true;

    }

    private static String takeMultipleViewScreenShot(final Solo solo, final View[] viewArray,
            final String name, final Instrumentation ins) {
        final String sFileName = getScreenShotFilePath(name, FILETYPE_PNG);
        ins.runOnMainSync(new Runnable() {

            @Override
            public void run() {

                final Bitmap screenMap = Bitmap.createBitmap(mDisplayMetrics.widthPixels,
                        mDisplayMetrics.heightPixels, Bitmap.Config.ARGB_8888);

                final Canvas canvas = new Canvas(screenMap);

                final int length = viewArray.length;
                for (int i = 0; i < length; i++) {
                    final View v = viewArray[i];
                    if (null != v) {
                        canvas.save();
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        canvas.translate(location[0], location[1]);
                        v.draw(canvas);
                        canvas.restore();
                    }

                }

                if (null != screenMap) {
                    saveBitmap(screenMap, sFileName);
                    if (!screenMap.isRecycled())
                        screenMap.recycle();
                } else {
                    Log.e(LOG_TAG, "Can't get the snapshot \"" + sFileName
                            + "\".");
                }

            }
        });

        return sFileName;
    }

    public static void assertDrawable(Drawable drawable, Context context, TestCase testCase) {
        final Rect bounds = drawable.getBounds();
        testCase.assertFalse(bounds.isEmpty());

        final Bitmap bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(),
                Bitmap.Config.ARGB_8888
                );
        final Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        ScreenShotUtil.AssertViewEqualBefore(context, bitmap, testCase);

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
