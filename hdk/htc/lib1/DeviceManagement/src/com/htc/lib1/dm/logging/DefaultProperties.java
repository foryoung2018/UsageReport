package com.htc.lib1.dm.logging;

import com.htc.lib1.dm.solo.ConfigManager;



/**
 * Default logging properties.
 */
public class DefaultProperties implements Properties {
	private static final String TAG = ConfigManager.class.getSimpleName();
	private static final String TAG_SENSITIVE = ConfigManager.class.getSimpleName() + "_S";

    @Override
    public String tag() {
        return TAG;
    }

    @Override
    public String senstiveTag() {
        return TAG_SENSITIVE;
    }

    @Override
    public Boolean enableMethodLog() {
        return false;
    }

    @Override
    public Boolean enableFileInfoLog() {
        return false;
    }

    @Override
    public Boolean enableThrowableFormatter() {
        return false;
    }

    @Override
    public Boolean enableBundleFormatter() {
        return false;
    }

}
