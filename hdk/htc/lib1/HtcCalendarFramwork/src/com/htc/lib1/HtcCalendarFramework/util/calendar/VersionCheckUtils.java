package com.htc.lib1.HtcCalendarFramework.util.calendar;

import android.os.Build;

/**
  * The check utility for android level
  * 
  */
public class VersionCheckUtils {

    /**
      * The VersionCheckUtils constructor
      */
    public VersionCheckUtils() {
    }
    
    public static int getAndroidAPILevel() {
    	return Build.VERSION.SDK_INT;
    }
    
    public static boolean afterAPI21() {
        return getAndroidAPILevel() > 20;
    }
}
