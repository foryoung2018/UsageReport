package com.htc.lib1.theme;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

public class ThemeWallpaper {
    private static String LOG_TAG = "ThemeType.ThemeWallpaper";

    private static final String WALLPAPER_SYNCBACK_AUTHORITY = "com.htc.themepicker.wallpapersyncback";
    private static final Uri MESSAGE_WALLPAPER_SYNCBACK_CONTENT_URI = Uri
            .parse("content://" + WALLPAPER_SYNCBACK_AUTHORITY
                    + "/message");
    private static final Uri LOCKSCREEN_WALLPAPER_SYNCBACK_CONTENT_URI = Uri
            .parse("content://" + WALLPAPER_SYNCBACK_AUTHORITY
                    + "/lockscreen");
    private static final Uri DOTVIEW_WALLPAPER_SYNCBACK_CONTENT_URI = Uri
            .parse("content://" + WALLPAPER_SYNCBACK_AUTHORITY
                    + "/dotview");


    public static boolean writeMessageWallpaperToThemePicker(Context context, Bitmap bitmap) throws IOException {
        return writeWallpaperToThemePicker(context, MESSAGE_WALLPAPER_SYNCBACK_CONTENT_URI, bitmap);
    }

    public static boolean writeLockScreenWallpaperToThemePicker(Context context, Bitmap bitmap) throws IOException {
        return writeWallpaperToThemePicker(context, LOCKSCREEN_WALLPAPER_SYNCBACK_CONTENT_URI, bitmap);
    }

    public static boolean writeDotViewWallpaperToThemePicker(Context context, Bitmap bitmap) throws IOException {
        return writeWallpaperToThemePicker(context, DOTVIEW_WALLPAPER_SYNCBACK_CONTENT_URI, bitmap);
    }


    private static int compressQuality = 100;
    private static CompressFormat format = CompressFormat.JPEG;

    private static boolean writeWallpaperToThemePicker(Context context, Uri uri, Bitmap bitmap) throws IOException {
        Log.d(LOG_TAG, "writeWallpaperToThemePicker+ bitmap = " + bitmap + " uri = " + uri);
        boolean bSuccess = false;
        String mode = bitmap == null ? "d" : "w";
        try {
            if (context == null) {
                Log.d(LOG_TAG, "bitmap = " + bitmap + " context = " + context);
                return bSuccess;
            }

            ContentResolver resolver = context.getContentResolver();
            ParcelFileDescriptor remoteFD = resolver.openFileDescriptor(uri, mode);

            if (remoteFD != null && bitmap != null) {
                ParcelFileDescriptor.AutoCloseOutputStream out = new ParcelFileDescriptor.AutoCloseOutputStream(remoteFD);

                try {
                    bitmap.compress(format, compressQuality, out);
                    bSuccess = true;
                } finally {
                    out.close();
                }

            } else {
                Log.w(LOG_TAG, "Get remoteFD is null");
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception at writeWallpaperToThemePicker", e);
        }


        Log.d(LOG_TAG, "writeWallpaperToThemePicker- bSuccess = " + bSuccess + ", mode = " + mode);
        return bSuccess;
    }
}
