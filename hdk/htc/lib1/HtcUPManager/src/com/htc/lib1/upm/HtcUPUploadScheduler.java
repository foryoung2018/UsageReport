package com.htc.lib1.upm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import com.htc.lib1.upm.uploader.ReportManager;

final class HtcUPUploadScheduler {
    private static final String TAG = "HtcUPUploadScheduler";
    private final static String preferenceName = Common.UP_PREFERENCE_NAME;
    private final static String LAST_REQUEST_UPLOAD_TIME = "LastReuestUploadTime";
    private static long UPLOAD_PERIOD = 24 * 60 * 60 * 1000;
    private Context mContext;
    private Handler mHandler;
    private long mLastRequestUploadTime;
    public HtcUPUploadScheduler(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        mLastRequestUploadTime = -1L;
    }
    
    private static void loadUploadPeriod(Context context) {
    	ParameterLoader parameterFetcher = new ParameterLoader(context);
    	UPLOAD_PERIOD = parameterFetcher.getInt("up_dispatch_period", (24 * 60 * 60)) * 1000;
    	Log.d(TAG, "Dispatch period: " + UPLOAD_PERIOD + " ms");
    }
    
    public void init() {
    	loadUploadPeriod(mContext);
        mLastRequestUploadTime = getLastRequestUploadTime();
        if (mLastRequestUploadTime < 0) {
            long now = System.currentTimeMillis();
            setLastRequestUploadTime(now);
            mLastRequestUploadTime = now;
        }
        Log.d(TAG, "Last upload time: " + mLastRequestUploadTime);
    }
    
    public void startUploadService(long now) {
        setLastRequestUploadTime(now);
        mLastRequestUploadTime = now;
        ReportManager.getInstance(mContext).onUpload();
    }
    
    public void checkSchedule(long currentRequestTime) {
        long diff = currentRequestTime - mLastRequestUploadTime;
        long now =  System.currentTimeMillis();
        long nextRequestTime = 0;
        if (diff >= UPLOAD_PERIOD || diff <= 0) {
            startUploadService(now);
            nextRequestTime = UPLOAD_PERIOD;
        } else {
            nextRequestTime = UPLOAD_PERIOD - diff;
        }
        Log.d(TAG, "Last upload time: " + mLastRequestUploadTime, " next request time is " + nextRequestTime + " ms later");
        mHandler.removeMessages(HtcUPHandler.DISPATCH);
        mHandler.sendEmptyMessageDelayed(HtcUPHandler.DISPATCH, nextRequestTime);
    }
    
    private long getLastRequestUploadTime() {
        long lastUploadTime;
        SharedPreferences preferences = mContext.getSharedPreferences(preferenceName, 0);
        lastUploadTime = preferences.getLong(LAST_REQUEST_UPLOAD_TIME, -1);
        Log.d(TAG, "Last request upload time: " + lastUploadTime);
        return lastUploadTime;
    }
    
    private void setLastRequestUploadTime(long lastRequestUploadTime) {
        SharedPreferences preferences = mContext.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LAST_REQUEST_UPLOAD_TIME, lastRequestUploadTime);
        editor.commit();//It is OK due to we are in background thread.
    }
    
}
