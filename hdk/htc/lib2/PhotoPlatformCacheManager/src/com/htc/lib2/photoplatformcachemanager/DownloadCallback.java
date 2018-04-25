package com.htc.lib2.photoplatformcachemanager;

import android.net.Uri;
import android.os.Bundle;

/**
 * Used in PhotoPlatformCacheManager.
 * If download success, PhotoPlatformCacheManager will send notice download success by callback.
 * 
 * @hide
 */
public interface DownloadCallback {

    /**
     * Used on download success
     * 
     * @param uri the download success uri
     * @param data the origin data bundle.
     * 
     * @hide
     */
    public void onDownloadSuccess(Uri uri, Bundle data);

    /**
     * Used on download error
     *  
     * @param e the exception information
     * @param data the origin data bundle.
     * 
     * @hide
     */
    public void onDownloadError(Exception e, Bundle data);
}

