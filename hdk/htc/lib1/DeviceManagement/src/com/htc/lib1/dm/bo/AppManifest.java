package com.htc.lib1.dm.bo;

import android.annotation.SuppressLint;

/**
 * Created by Joe_Wu on 8/25/14.
 */
public class AppManifest {


    // --------------------------------------------------

    // App ID.
    // The ID of the application on the device.
    protected String appID;

    // App version key.
    // An opaque identifier.
    // The version key of the application on the device.
    protected String versionKey;

    // Config ID
    // An opaque identifier.
    // The ID of the config that the DM client currently holds in its cache.
    protected String configID;

    // Android version code
    private int versionCode;

    // Android version name
    private String versionName;

    // --------------------------------------------------

    public AppManifest(String appID, String versionKey,
                            int versionCode, String versionName,
                            String configID) {

        this.appID = appID;
        this.versionKey = versionKey;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.configID = configID;
    }

    // --------------------------------------------------

    public String getAppID() {
        return appID;
    }

    public String getVersionKey() {
        return versionKey;
    }

    public String getConfigID() {
        return configID;
    }

    // --------------------------------------------------

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    // --------------------------------------------------

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("<%s: appID=%s, versionKey=%s, versionCode=%d, versionName=%s>", this.getClass().getSimpleName(), appID, versionKey, versionCode, versionName);
    }
}
