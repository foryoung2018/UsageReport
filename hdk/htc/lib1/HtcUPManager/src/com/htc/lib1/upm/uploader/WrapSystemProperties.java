package com.htc.lib1.upm.uploader;

import java.lang.reflect.Method;

import android.text.TextUtils;

import com.htc.lib1.upm.Log;

public class WrapSystemProperties {
    private static final String TAG = "WrapSystemProperties";
    private static final String CLASS = "android.os.SystemProperties";
    private static Class<?> sSystemPropertiesClass;
    private static Method sGet;
    private static Method sGetBoolean;
    private volatile static boolean sIsInit;
    
    private WrapSystemProperties() {
    
    }
    
    private static void init() throws ClassNotFoundException, NoSuchMethodException {
        if (!sIsInit) {
            synchronized(WrapSystemProperties.class) {
                if (!sIsInit) {
                    if (sSystemPropertiesClass == null)
                        sSystemPropertiesClass =  Class.forName(CLASS);
                    
                    if (sGet == null && sSystemPropertiesClass != null)
                        sGet = sSystemPropertiesClass.getMethod("get", String.class);
                    
                    if (sGetBoolean == null && sSystemPropertiesClass != null)
                        sGetBoolean = sSystemPropertiesClass.getMethod("getBoolean", String.class, boolean.class);
                    
                    sIsInit = true;
                }
            }
        }
    }
    
    public static String get(String key, String defValue) {
    	String result = defValue;
    	try {
    	    init();        
            Object obj = sGet.invoke(null, key);
            if (obj != null && obj instanceof String)
                result = (String) obj;
    	} catch(Exception e) {
    		Log.e(TAG, "Failed to get system properties: " + e.getMessage(), e);
    	}
    	result = TextUtils.isEmpty(result) ? defValue : result;
        return result;
    }
    
    public static boolean getBoolean(String key, boolean defValue) {
    	boolean result = defValue;
    	try {
    	    init();
            Object obj = sGetBoolean.invoke(null, key, defValue);
            if (obj != null && obj instanceof Boolean)
                result = ((Boolean)obj).booleanValue();	
    	} catch (Exception e) {
    		Log.e(TAG, "Failed to get system properties: " + e.getMessage(), e);
    	}
        return result;
    }

}
