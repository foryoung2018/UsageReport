package com.htc.lib1.frisbee;

import android.net.Uri;

/**
 * @hide
 * @author vincent
 *
 */
public class Constants {
    public static final boolean DEBUG = false;
    
    public static final Uri SECURE_DOWNLOAD_URI = Uri.parse("content://com.htc.dnatransfer.public/securedownload");
    public static final Uri DOWNLOAD_URI = Uri.parse("content://com.htc.dnatransfer.public/download");
    public static final String KEY_PATH = "path";
    public static final String KEY_DELETE = "delete";

}
