package com.htc.lib1.HtcCalendarFramework.util.calendar;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class HtcWrapCustomization {

    private static HtcWrapCustomizationManager mManager = new HtcWrapCustomizationManager();
    private static final String TAG = "HtcWrapCustomization";
    private static Map<String, HtcWrapCustomizationReader> mAppSettings =  new HashMap<String, HtcWrapCustomizationReader>();

	/**
	  * Define the Calendar Settings constant value
	  */
	public static final String CALENDAR_START_WEEKDAY="calendar_start_weekday";
	/**
	  * Define the Calendar Settings constant value
	  */
	public static final String CALENDAR_SHOW_LUNAR="calendar_show_lunar";
	/**
	  * Define the Calendar Settings constant value
	  */
	public static final String CALENDAR_WEEKDAY_TYPE="calendar_weekday_type";
    
    private static HtcWrapCustomizationReader checkHtcCustomizationReader(String appName) {

        String tempName = appName.toLowerCase();
        
        HtcWrapCustomizationReader reader = mAppSettings.get(tempName);
        
        if (reader == null) {
            try {
               


                reader = mManager.getCustomizationReader(tempName,
                    HtcWrapCustomizationManager.READER_TYPE_XML, false);

                if (reader == null) {
                    Log.d(TAG, "reader = null");
                    return null;
                }
                
                mAppSettings.put(tempName, reader);

            }
            catch (Exception e) {
                Log.e(TAG, "Cannot get HtcCustomization, reason = " + e.toString());
                return null;
            }
        }

        return reader;
    }
    
    public static int readInteger(String appName, String key, int defaultValue){
        HtcWrapCustomizationReader reader = checkHtcCustomizationReader(appName);
        
        if( reader != null )
            return reader.readInteger(key, defaultValue);
            
        return defaultValue;
    
    }
    
    public static boolean readBoolean(String appName, String key, boolean defaultValue){
        HtcWrapCustomizationReader reader = checkHtcCustomizationReader(appName);
        
        if( reader != null )
            return reader.readBoolean(key, defaultValue);
            
        return defaultValue;
    
    }
    
    /** 
     * @deprecated [Module internal use]
     */
    /**@hide*/ 
    public static String readString(String appName, String key, String defaultValue){
        HtcWrapCustomizationReader reader = checkHtcCustomizationReader(appName);
        
        if( reader != null )
            return reader.readString(key, defaultValue);
            
        return defaultValue;
    
    }
    
    /** 
     * @deprecated [Not use any longer]
     */
    /**@hide*/ 
    public static byte readByte(String appName, String key, byte defaultValue){
        
        HtcWrapCustomizationReader reader = checkHtcCustomizationReader(appName);
        
        if( reader != null )
            return reader.readByte(key, defaultValue);
            
        return defaultValue;
    
    }
    
}

