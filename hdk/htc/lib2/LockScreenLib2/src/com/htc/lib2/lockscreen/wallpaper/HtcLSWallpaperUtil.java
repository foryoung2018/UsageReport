package com.htc.lib2.lockscreen.wallpaper;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;


public class HtcLSWallpaperUtil {

    private static final String TAG = "LSWpUtil";
    private static final boolean localLOGI = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;

    // Wallpaper Provider
    private static final String AUTHORITY = "com.htc.lockscreen.wallpaper.provider";
    private static final String URI_WALLPAPER = "wallpaper";
    private static final String URI_WALLPAPER_TYPE = "wallpaper_type";
    private static final String KEY_WALLPAPER_TYPE = "wallpaper_type";
    // Wallpaper Type
    public static final int WALLPAPER_TYPE_HOME   = 0;
    public static final int WALLPAPER_TYPE_CUSTOM = 1;
    // Wallpaper Type Change Intent - Action
    public static final String INTENT_ACTION_WALLPAPER_CHANGED = 
            "com.htc.lockscreen.wallpaper.wallpaper_changed";
    // Wallpaper Type Change Intent - Extra Key
    private static final String INTENT_EXTRA_WALLPAPER_TYPE = "extra_wallpaper_type";
    // Lock Screen Wallpaper Change Intent - Action
    private static final String INTENT_ACTION_LS_WALLPAPER_CHANGED = 
            "com.htc.launcher.lockscreen.WallpaperChanged";

    /**
     * To check whether user has set the Wallpaper of Lock Screen.
     * @param context
     * @return boolean
     */
    public static boolean isLockScreenWallpaper(Context context) {
        return (getWallpaperType(context) == WALLPAPER_TYPE_CUSTOM);
    }

    public static int getWallpaperType(Context context) {
        ContentResolver resolver =
                (context != null)? context.getContentResolver():null;
        if (resolver == null) {
            Log.w(TAG, "getWpT Fail");
            return WALLPAPER_TYPE_HOME;
        }
        final Uri WpURI = Uri.parse("content://" + AUTHORITY + "/" + URI_WALLPAPER_TYPE);
        final String[] COLUMS = new String[]{"wallpaper_type"};
        Cursor cursor = null;
        int wallpaperType = WALLPAPER_TYPE_HOME;
        try {
            cursor = resolver.query(WpURI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(COLUMS[0]);
                if (index >= 0) {
                    wallpaperType = cursor.getInt(index);
                }
            }
            if (localLOGI) Log.i(TAG, "getWpT: " + wallpaperType);
        } catch (Exception e) {
            Log.w(TAG, "getWpT E: " + e);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    Log.w(TAG, "getWpT E: " + e);
                }
            }
        }
        return wallpaperType;
    }

    /**
     * Set Wallpaper Type
     * @param context
     * @param type
     */
    public static void setWallpaperType(Context context, int type) {
        if (context == null) {
            return;
        }
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            Log.w(TAG, "saveWT invalid CR");
            return;
        }
        final Uri WpURI = Uri.parse("content://" + AUTHORITY + "/" + URI_WALLPAPER_TYPE);
        ContentValues updateValues = new ContentValues();
        try {
            if (updateValues != null) {
                updateValues.put(KEY_WALLPAPER_TYPE, type);
            }
            if (resolver.update(WpURI, updateValues, null, null)<0) {
                Log.w(TAG, "saveWT update fail");
                return;
            }
        } catch (Exception e) {
            Log.w(TAG, "saveWT E:" + e);
            return;
        }
        Intent intent = new Intent(INTENT_ACTION_WALLPAPER_CHANGED);
        if (intent != null) {
            intent.putExtra(INTENT_EXTRA_WALLPAPER_TYPE, type);
            context.sendBroadcast(intent);
        }
    }

    /**
     * Set LockScreen Wallpaper
     * @param context context
     * @param bitmap bitmap
     * @return boolean true: setLockScreenWallpaper Success,false: setLockScreenWallpaper failed
     */
    public static boolean setLockScreenWallpaper(Context context, Bitmap bitmap) {
        boolean result = false;
        if (context == null) {
            return result;
        }
        final Uri WpURI = Uri.parse("content://" + AUTHORITY + "/" + URI_WALLPAPER);
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            return result;
        }
        ParcelFileDescriptor fd = null;
        FileOutputStream fos = null;
        try {
            fd = resolver.openFileDescriptor(WpURI, "");
            fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            result = true;
        } catch (Exception e) {
            Log.w(TAG, "setLockScreenWallpaper E: " + e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    result = false;
                    Log.w(TAG, "setLockScreenWallpaper E: " + e);
                }
            }
        }
        Intent intent = new Intent(INTENT_ACTION_LS_WALLPAPER_CHANGED);
        context.sendBroadcast(intent);
        return result;
    }

    /**
     * Get LockScreen Wallpaper
     * @param context
     * @return Bitmap Wallpaper
     */
    public static Bitmap getLockScreenWallpaper(Context context) {
        ContentResolver resolver =
                (context != null)? context.getContentResolver():null;
        if (resolver == null) {
            Log.w(TAG, "getLSWp Fail");
            return null;
        }
        final Uri WpURI = Uri.parse("content://" + AUTHORITY + "/" + URI_WALLPAPER);
        ParcelFileDescriptor fd = null;
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fd = resolver.openFileDescriptor(WpURI, "");
            fis = new ParcelFileDescriptor.AutoCloseInputStream(fd);
            bitmap = BitmapFactory.decodeStream(fis);
            if (localLOGI) Log.i(TAG, "getLSWp: " + bitmap);
        } catch (Exception e) {
            Log.w(TAG, "getLSWp E: " + e);
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "getLSWp OOM: " + e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.w(TAG, "getLSWp E: " + e);
                }
            }
        }
        return bitmap;
    }

}
