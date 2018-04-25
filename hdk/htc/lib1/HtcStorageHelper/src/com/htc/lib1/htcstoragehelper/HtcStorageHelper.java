/*
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2008 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of
 * HTC Corporation ("HTC"). Only the user who is legally
 * authorized by HTC ("Authorized User") has right to employ this work
 * within the scope of this statement. Nevertheless, the Authorized User
 * shall not use this work for any purpose other than the purpose agreed by HTC.
 * Any and all addition or modification to this work shall be unconditionally
 * granted back to HTC and such addition or modification shall be solely owned by HTC.
 * No right is granted under this statement, including but not limited to,
 * distribution, reproduction, and transmission, except as otherwise provided in this statement.
 * Any other usage of this work shall be subject to the further written consent of HTC.
 */
package com.htc.lib1.htcstoragehelper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.IllegalArgumentException;
import java.lang.System;
import java.util.ArrayList;

//import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

/**
 * Provide function to access various storages accordingly.
 *
 * @exthide {@exthide}
 */
public class HtcStorageHelper {

    final static String TAG = "Lib1-HtcStorageHelper";
    final static boolean DEBUG = false;//HtcWrapHtcDebugFlag.Htc_DEBUG_flag;

    public static File getPhoneStorageDir(Context context) throws IllegalArgumentException {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null!");
        }

        File[] storageList = context.getExternalFilesDirs(null);
        for (File path : storageList) {
            if (path == null) {
                if (DEBUG) Log.d(TAG, "encounter null File instance!");
                continue; // Context may replace File instance as null. Skipping it.
            } else {
                if (DEBUG) Log.d(TAG, "checking File: " + path.toString());
                // Returns non-removable storage path. Suppose it's unique.
                if (!Environment.isExternalStorageRemovable(path)) {
                    if (DEBUG) Log.d(TAG, "Get non-removable storage: " + path.getPath());
                    return getStorageRootDir(path);
                }
            }
        }

        // return null if no non-removable storage represents
        return null;
    }

    public static File[] getRemovableStorageDirs(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null!");
        }

        File[] storageList = context.getExternalFilesDirs(null);
        ArrayList<File> resultList = new ArrayList<File>();

        // Returns all removable storages.
        for (File path : storageList) {
            if (path == null) {
                if (DEBUG) Log.d(TAG, "encounter null File instance!");
                continue; // Context may replace File instance as null. Skipping it.
            } else {
                if (DEBUG) Log.d(TAG, "checking File: " + path.toString());
                if (Environment.isExternalStorageRemovable(path)) {
                    if (DEBUG) Log.d(TAG, "Get removable storage: " + path.getPath());
                    File storageRootDir = getStorageRootDir(path);
                    resultList.add(storageRootDir);
                }
            }
        }

        // At last, add USE storage path into the result
        File usbStorage = getUsbStorageDirectory();
        // Only when USB storage can be retrieved and it's in mounted state, the instance will be returned
        if (usbStorage != null && 
            Environment.MEDIA_MOUNTED.equals(Environment.getStorageState(usbStorage))) {
            resultList.add(usbStorage);
        }

        return resultList.toArray(new File[resultList.size()]);
    }

    private static File getStorageRootDir(File path) {
        File result = null;
        try {
            // remove Android/data/<package_name>/files and return storage root back. Catch NPE if any.
            result = path.getParentFile().getParentFile().getParentFile().getParentFile();
        } catch (NullPointerException e) {
            if (DEBUG) Log.w(TAG, "Got NPE while converting removable storage path! Will return null instead.");
        }

        return result;
    }

    private static File getUsbStorageDirectory() {
        String path = System.getenv("EXTERNAL_STORAGE4");
        if (DEBUG) Log.d(TAG, "USB storage path: " + path);
        return path == null ? null : new File(path);
    }
}

