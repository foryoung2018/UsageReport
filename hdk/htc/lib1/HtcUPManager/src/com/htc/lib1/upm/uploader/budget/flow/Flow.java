package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.Common;
import com.htc.lib1.upm.Log;
import com.htc.lib1.upm.uploader.budget.BudgetPreference;

public abstract class Flow {
	
	private final static String TAG = "Flow";
	private final static boolean _DEBUG = Common._DEBUG;

	public final static int KEY_APP_USAGE = 0;
	private final static int MAX_KEY = KEY_APP_USAGE;
	
	private BudgetPreference mPref;
	private String mKeys[];
	
	public Flow( BudgetPreference pref, String keyUsage ) {
		mPref = pref;
		mKeys = new String[MAX_KEY+1];
		mKeys[KEY_APP_USAGE] = keyUsage;
	}
	
	protected boolean set(int key, long value) {
		
		if ( key < 0 || key > MAX_KEY ) return false;
		
		mPref.set(mKeys[key], value);
		return true;
	}

	protected long get(int key) {

		if ( key < 0 || key > MAX_KEY ) return 0L;
		
		return mPref.get(mKeys[key]);
	}
	
	public abstract String getTAG(); 

	public void reset() {
		set(KEY_APP_USAGE, 0L);
	}
	
	public void appUsageUpdated(long pkgSize) {

		long value = get(KEY_APP_USAGE);
		if (pkgSize<0L) {
			Log.e(getTAG(), "appUsageUpdated()", "value:" + value + " pkgSize:" + pkgSize);
			new Exception(new Throwable()).printStackTrace();
		}
		
		if(Long.MAX_VALUE - value > pkgSize)   // Check if value+pkgSize is going to be over max. of long integer
		    set(KEY_APP_USAGE, value+pkgSize); // It is still available for pkgSize
		else
		    set(KEY_APP_USAGE, Long.MAX_VALUE); // Use max. of long integer to prevent overflow since value+pkgSize is over upper bound of long integer
	}
	
	public boolean isAvailableBySize(long expectedSize, long limit) {
		
		if ( 0L > limit ) {
			if (_DEBUG)
				Log.i(getTAG(), "isAvailableByPercentage()", "true, 0 > limit");
			return true; // unlimited case
		}
		
		if ( 0L == limit ) {
			if (_DEBUG)
				Log.i(getTAG(), "isAvailableByPercentage()", "false, 0 == limit");
			return false; // do not allow case
		}
		
		long usage = get(KEY_APP_USAGE);
		long availSize = limit - usage;
		if ( availSize >= expectedSize ) {
			if (_DEBUG) 
				Log.i(getTAG(), "isAvailableBySize()", "true, limit(" + limit + ") > expected(" + expectedSize + ") + usage(" + usage + ")");
			return true;
		}

		if (_DEBUG) 
			Log.i(getTAG(), "isAvailableBySize()", "false, limit(" + limit + ") < expected(" + expectedSize + ") + usage(" + usage + ")");
		return false;
	}
	
	public boolean isAvailableByPercentage(long expectedSize, long percentage) {
        // This case is abandoned on KK443 (M8_MR) because it isn't realistic. Instead, always return true. (2014-05-08)
        return true;
	}
	
}