package com.htc.lib1.settings.provider;

import android.util.Log;

public class SettingsWrapperLog {
	private static final boolean DEBUG_FLAG = true;	// TODO: use real debug flag
	private static final String CLASS_NAME = SettingsWrapperLog.class.getSimpleName();
	
	public static void logV(Object... msgs) {
		if(DEBUG_FLAG) {
			Log.v(CLASS_NAME, concatStrings(msgs));
		}
	}
	public static void logD(Object... msgs) {
		if(DEBUG_FLAG) {
			Log.d(CLASS_NAME, concatStrings(msgs));
		}
	}
	
	public static void logW(String tag, Object... msgs) {
		Log.w(CLASS_NAME, concatStrings(msgs));
	}

	public static String concatStrings(Object... strings) {
    	if(null==strings) {
    		return null;
    	}
    	String result = "";
    	for(Object s : strings) {
    		result = result + s;
    	}
    	return result;
	}    
}
