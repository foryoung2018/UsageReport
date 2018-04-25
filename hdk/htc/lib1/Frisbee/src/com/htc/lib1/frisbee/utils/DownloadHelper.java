package com.htc.lib1.frisbee.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.net.Uri;

import com.htc.lib1.frisbee.Constants;

/**
 * @hide
 * @author vincent
 *
 */
public class DownloadHelper {
    
    private static final String TAG = DownloadHelper.class.getSimpleName();
    
    public static InputStream openFile(Context context, String path, boolean isSecure) throws FileNotFoundException {
        Uri.Builder builder = (isSecure)? Constants.SECURE_DOWNLOAD_URI.buildUpon():Constants.DOWNLOAD_URI.buildUpon();
        builder.appendQueryParameter(Constants.KEY_PATH, path);
        return context.getContentResolver().openInputStream(builder.build());
    }

}
