package com.htc.lib1.upm.uploader.budget;


import java.util.HashMap;
import android.content.Context;
import android.net.ConnectivityManager;

import com.htc.lib1.upm.Common;
import com.htc.lib1.upm.Log;
import com.htc.lib1.upm.uploader.UploadUtils;
import com.htc.lib1.upm.uploader.budget.network.AllNetwork;
import com.htc.lib1.upm.uploader.budget.network.MobileNetwork;
import com.htc.lib1.upm.uploader.budget.network.Network;
import com.htc.lib1.upm.uploader.budget.network.OtherNetwork;


public class BudgetManager {
	
	public enum NetworkType {
		TYPE_MOBILE,
		TYPE_OTHER,
		TYPE_NONE;
		
		public static String toString(NetworkType type) {
			switch(type) {
			case TYPE_MOBILE: return "MOBILE";
			case TYPE_OTHER:  return "OTHER";
			case TYPE_NONE:   return "NONE";
			}
			return null;
		}
	}

	private final static String TAG = "BudgetManager";
	private final static boolean _DEBUG = Common._DEBUG;
	/**
	 * Reset budget per month.
	 */
	private final static long RESET_PERIOD = 720  * Common.HOUR_TO_MILLISECONDS;
	
	private Context mContext;
	private HashMap<String, String> mPolicyMap;
	private BudgetPreference mPref;
	private Network mMobileNetwork, mOtherNetwork, mTotalNetwork;
	
	public BudgetManager(Context context) {
		mContext = context;
		mPolicyMap = getBudgetPolicy();
		mPref = new BudgetPreference(mContext);
		mMobileNetwork = new MobileNetwork(mPref);
		mOtherNetwork = new OtherNetwork(mPref);
		mTotalNetwork = new AllNetwork(mPref);
	}

	public void updateAppUsage(long DLSize, long ULSize, String logcatTag) {
		
		updateAppUsage(getCurrentNetworkType(), DLSize, ULSize, logcatTag);
	}
	
	private void updateAppUsage(NetworkType type, long DLSize, long ULSize, String logcatTag) {
		
		if ( NetworkType.TYPE_NONE == type ) return;
		
		mTotalNetwork.appUsageUpdated(DLSize, ULSize);
		if ( NetworkType.TYPE_MOBILE == type ) {
			mMobileNetwork.appUsageUpdated(DLSize, ULSize);
		} else {
			mOtherNetwork.appUsageUpdated(DLSize, ULSize);
		}

		mPref.flush();
	}

	public boolean isAvailableByCurrentNetwork(long expectedDLSize, long expectedULSize) {

		NetworkType type = getCurrentNetworkType();
		
		if (_DEBUG) Log.i(TAG, "isAvailableByCurrentNetwork()", "is " + NetworkType.toString(type));
		
		return isAvailable(type, expectedDLSize, expectedULSize);
	}

	public boolean isAvailableByNoncurrentNetwork(long expectedDLSize, long expectedULSize) {
		
		NetworkType type = getCurrentNetworkType();
		
		if ( NetworkType.TYPE_NONE == type ) {
			boolean result = isAvailable(NetworkType.TYPE_MOBILE, expectedDLSize, expectedULSize);
			result |= isAvailable(NetworkType.TYPE_OTHER, expectedDLSize, expectedULSize);
			return result;
		}
		
		if ( NetworkType.TYPE_MOBILE == type )
			type = NetworkType.TYPE_OTHER;
		else
			type = NetworkType.TYPE_MOBILE;

		if (_DEBUG) Log.i(TAG, "isAvailableByNoncurrentNetwork()", "is " + NetworkType.toString(type));
		
		return isAvailable(type, expectedDLSize, expectedULSize);
	}
	
	private boolean isAvailable(NetworkType type, long expectedDLSize, long expectedULSize) {

	    // Reset base time to current time if current time is over period of budget (2014-05-08)
        resetAllNetworkBudgetInNeed();
        
		boolean result = true;

		if ( NetworkType.TYPE_MOBILE == type ) {
			result = isSpecficNetworkAvailable( Common.KEY_BUDGET_PREFIX_MOBILE, mMobileNetwork, expectedDLSize, expectedULSize );
		} else {
			result = isSpecficNetworkAvailable( Common.KEY_BUDGET_PREFIX_OTHER, mOtherNetwork, expectedDLSize, expectedULSize );
		}
		result &= isSpecficNetworkAvailable( Common.KEY_BUDGET_PREFIX_ALL, mTotalNetwork, expectedDLSize, expectedULSize );

		if (_DEBUG) Log.i(TAG, "isAvailable()", "type=" + NetworkType.toString(type) + " result=" + result + " DL=" + expectedDLSize + " UL=" + expectedULSize);
		
		return result;
		
	}
	
	private boolean isSpecficNetworkAvailable( String keyPrefix, Network network, long expectedDLSize, long expectedULSize) {
		
	    if (_DEBUG) Log.i(TAG, "isSpecficNetworkAvailable", "current usage: "+mPref.toString());
	    
		/*long period = getPolicyLongValue(null, Common.KEY_BUDGET_PERIOD, -1L);
		if ( 0L > period ) {
			if (_DEBUG) Log.i(TAG, "_isAvailable()", keyPrefix + Common.KEY_BUDGET_PERIOD + " unlimited, period is " + period);
			return true; // unlimited
		}*/

		long totalLimit = getPolicyLongValue(keyPrefix, Common.KEY_BUDGET_SUFFIX_TOTAL, -1L);
		long ULLimit = getPolicyLongValue(keyPrefix, Common.KEY_BUDGET_SUFFIX_UL, -1L);
		long DLLimit = getPolicyLongValue(keyPrefix, Common.KEY_BUDGET_SUFFIX_DL, -1L);
		
		boolean isAvailable = false;
		// default we assume it as Common.VALUE_BUDGET_TYPE_ABS_MB
        if ( -1L != totalLimit ) totalLimit *= Common.MEGABYTE_TO_BYTES;
        if ( -1L != ULLimit ) ULLimit *= Common.MEGABYTE_TO_BYTES;
        if ( -1L != DLLimit ) DLLimit *= Common.MEGABYTE_TO_BYTES;
        isAvailable = network.isAvailableByBytes(totalLimit, expectedDLSize, DLLimit, expectedULSize, ULLimit);
        if (_DEBUG) Log.i(TAG, "isAvailableByBytes", "type="+network.getTAG()+", isAvailable="+isAvailable+", totalLimit=" + totalLimit + ", DLLimit="
                + DLLimit + ", ULLimit=" + ULLimit + ", file DL size=" + expectedDLSize + ", file UL size=" + expectedULSize);
        return isAvailable;
	}
	
	private long getPolicyLongValue(String keyPrefix, String keySuffix, long defaultValue) {
		
		long value = defaultValue;
		String key = "";
		if ( null != keyPrefix && 0 != keyPrefix.length() ) key += keyPrefix;
		if ( null != keySuffix && 0 != keySuffix.length() ) key += keySuffix;
		
		//String str = mPolicy.getValue(Common.CATEGORY_BUDGET, key);
		String str = mPolicyMap.get(key);
		if ( null != str && 0 != str.length() ) {
			try {
				value = Long.parseLong(str);
			} catch (Exception e) {
				Log.e(TAG, "_getPolicyLongValue()", "exception catched during transfer " + keyPrefix + key + " from string to int");
				e.printStackTrace();
			}
		}
		
		return value;
	}

    /**
     * If current time exceeds the evaluation period, reset all network budget
     * and set current time as base time for next period
     */
	private void resetAllNetworkBudgetInNeed() {
	    
	    // Set current time as base time if it isn't initialized
	    long baseTime = BudgetPreference.getPeriodBaseline(mContext);
	    if(baseTime < 0) {
	        long firstBaseTime = System.currentTimeMillis();
	        BudgetPreference.setPeriodBaseline(mContext, firstBaseTime);
	        baseTime = firstBaseTime;
	        Log.i(TAG, "resetAllNetworkBudgetInNeed", "Set first time baseline of budget period : "+baseTime);
	    }
	    
	    //hard code reset period
	    long period = RESET_PERIOD;
        if ( period < 0 )
            return;

        long currentTime = System.currentTimeMillis();
        if(period == 0 || (currentTime - baseTime >= period)) { // period == 0 means check this time only so reset all to make following checking behavior correct
            resetAllNetworkBudget();
            BudgetPreference.setPeriodBaseline(mContext, currentTime);
            Log.i(TAG, "resetAllNetworkBudgetInNeed", "Set budget time baseline of period from "+baseTime+" to "+currentTime);
        }
	}

	private void resetAllNetworkBudget() {
        mMobileNetwork.reset();
        mOtherNetwork.reset();
        mTotalNetwork.reset();
        mPref.flush();
	}
	
	public NetworkType getCurrentNetworkType() {
		
		int networkType = UploadUtils.getCurrentNetworkType(mContext);
		
		if ( -1 == networkType )
			return NetworkType.TYPE_NONE;
		
		if ( ConnectivityManager.TYPE_MOBILE == networkType )
			return NetworkType.TYPE_MOBILE;
		
		return NetworkType.TYPE_OTHER;
    }

	// Budget configuration
	private static final String [][] BUDGET_POLICY = {
        {"mobile_UL",        "1"}, // 1MB
        {"mobile_DL",        "1"}, // 1MB
        {"mobile_total",     "2"}, // 2MB        
        {"other_UL",         "-1"}, // unlimited
        {"other_DL",         "-1"}, // unlimited
        {"other_total",      "-1"}, // unlimited
        {"all_UL",           "-1"}, // unlimited
        {"all_DL",           "-1"}, // unlimited
        {"all_total",        "-1"} // unlimited
	};
	
	private static HashMap<String, String> getBudgetPolicy() {
		HashMap<String, String> map = new HashMap<String, String>();
		for (String[] policy : BUDGET_POLICY) {
			map.put(policy[0], policy[1]);
		}
		return map;
	}

}