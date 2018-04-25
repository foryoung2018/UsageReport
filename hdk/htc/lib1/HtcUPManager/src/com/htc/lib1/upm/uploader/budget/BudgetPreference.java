package com.htc.lib1.upm.uploader.budget;

import android.content.Context;
import android.content.SharedPreferences;

import com.htc.lib1.upm.Common;
import com.htc.lib1.upm.Log;

public class BudgetPreference {
	
	private final static String TAG = "BudgetPreference";
	private final static boolean _DEBUG = Common._DEBUG;
	
	public final static String KEY_PERIOD_BASELINE = "PeriodBaseline";
	
	public final static String KEY_MOBILE_DL_APP_USAGE = "MobileDlAppUsage";
	public final static String KEY_MOBILE_UL_APP_USAGE = "MobileUlAppUsage";
	public final static String KEY_MOBILE_TOTAL_APP_USAGE = "MobileTotalAppUsage";

	public final static String KEY_OTHER_DL_APP_USAGE = "OtherDlAppUsage";
	public final static String KEY_OTHER_UL_APP_USAGE = "OtherUlAppUsage";
	public final static String KEY_OTHER_TOTAL_APP_USAGE = "OtherTotalAppUsage";
	
	public final static String KEY_ALL_DL_APP_USAGE = "AllDlAppUsage";
	public final static String KEY_ALL_UL_APP_USAGE = "AllUlAppUsage";
	public final static String KEY_ALL_TOTAL_APP_USAGE = "AllTotalAppUsage";

	private final static String[] KEYS = {
		KEY_MOBILE_DL_APP_USAGE,
		KEY_MOBILE_UL_APP_USAGE,
		KEY_MOBILE_TOTAL_APP_USAGE,
		KEY_OTHER_DL_APP_USAGE,
		KEY_OTHER_UL_APP_USAGE,
		KEY_OTHER_TOTAL_APP_USAGE,
		KEY_ALL_DL_APP_USAGE,
		KEY_ALL_UL_APP_USAGE,
		KEY_ALL_TOTAL_APP_USAGE,
	};
	
	private long[] mValues;
	
	private final static String preferenceName = "Budget";
	
	private Context mContext;
	
	BudgetPreference(Context context) {
		
		mContext = context;
		mValues = new long[KEYS.length];
		
		SharedPreferences preferences = mContext.getSharedPreferences(preferenceName, 0);
		for( int i=0; i<KEYS.length; ++i )
			mValues[i] = preferences.getLong(KEYS[i], 0L);
	}

	public void removeAll(){
        SharedPreferences preferences = mContext.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
	}
	
	public void flush() {
        SharedPreferences preferences = mContext.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = preferences.edit();
		for( int i=0; i<KEYS.length; ++i )
			editor.putLong(KEYS[i], mValues[i]);
		editor.commit();
	}

	public boolean set(String key, long value)
	{
		for( int i=0; i<KEYS.length; ++i )
			if ( KEYS[i].equals(key) ) {
				mValues[i] = value;
				return true;
			}
		return false;
	}
	
	public long get(String key) {
		for( int i=0; i<KEYS.length; ++i )
			if ( KEYS[i].equals(key) )
				return mValues[i];
		return 0L;
	}
	
	public String toString() {
	    SharedPreferences p = mContext.getSharedPreferences(preferenceName, 0);
	    return getKeyValueString(p,KEY_PERIOD_BASELINE)+", "+getKeyValueString(p,KEY_MOBILE_DL_APP_USAGE)
	            +", "+getKeyValueString(p, KEY_MOBILE_UL_APP_USAGE)+", "+getKeyValueString(p, KEY_MOBILE_TOTAL_APP_USAGE)+", "
	            +getKeyValueString(p, KEY_OTHER_DL_APP_USAGE)+", "+getKeyValueString(p, KEY_OTHER_UL_APP_USAGE)
	            +", "+getKeyValueString(p, KEY_OTHER_TOTAL_APP_USAGE)+", "+getKeyValueString(p, KEY_ALL_DL_APP_USAGE)+", "
	            +getKeyValueString(p, KEY_ALL_UL_APP_USAGE)+", "+getKeyValueString(p, KEY_ALL_TOTAL_APP_USAGE);
	}
	
	private String getKeyValueString(SharedPreferences preferences, String key) {
	    if(preferences != null)
	        return key+"="+preferences.getLong(key, -100L);
	    else
	        return "";
	}
	
	static long getPeriodBaseline(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
		long value = preferences.getLong(KEY_PERIOD_BASELINE, -1L);
		return value;
	}
	
	static void setPeriodBaseline(Context context, long value)
	{
		if (_DEBUG) Log.i(TAG, "setPeriodBaseline()", "Set period baseline as " + value );

        SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(KEY_PERIOD_BASELINE, value);       
        editor.commit();
	}
}