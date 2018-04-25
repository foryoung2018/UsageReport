package com.htc.lib1.upm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class HtcUPHandler extends Handler{
	private static final String TAG = "HtcUPHandler";
	/*package*/static final int INIT = 1;
    /*package*/static final int SEND = 2;
    /*package*/static final int DISPATCH = 3;
    /*package*/static final int CHECK_IDLE = 4;
    /*package*/static final int RECONNECT = 5;
    /*package*/static final int DISCONNECT = 6;
    /*package*/static final int UP_SWITCH = 7;
    /*package*/static final int UP_OFF = 0;
    /*package*/static final int UP_ON = 1;
    private Context mContext;
    private HtcUPServiceProxy mServiceProxy;
    private boolean mIsUPDataEnable;
    
    public HtcUPHandler(Looper looper, Context context, boolean uploadSelf){
        super(looper);
        mContext = context;
        mServiceProxy = new HtcUPServiceProxy(mContext, this, uploadSelf);
        mContext.registerComponentCallbacks(mServiceProxy);
    }
    
    @Override
    public void handleMessage(Message msg) {
    	 try {
             handleMessageInternal(msg);
         } catch (Exception e) {
             Log.e(TAG, "[Warning] Exception in handleMessage!", e);
         }
    }
    
    private void handleMessageInternal(Message msg) {
    	if (msg == null)
    		return;
    	
    	switch(msg.what) {
	        case INIT:
	            mIsUPDataEnable = HtcUPEnableChecker.isUPEnable(mContext);            	
	            mServiceProxy.init(mIsUPDataEnable);
	            break;
	        case SEND:
	            if (mIsUPDataEnable && msg.obj != null && msg.obj instanceof Bundle) {
	                Bundle data = (Bundle)msg.obj;
	                mServiceProxy.putDataToQueue(data);
	            } else {
	            	Log.d(TAG, "UP data has been disabled by user");
	            }
	            break;
	        case DISPATCH:
	            if (mIsUPDataEnable)
	                mServiceProxy.upload();
	            break;
	        case CHECK_IDLE:
	                mServiceProxy.checkServiceIdle();
	            break;
	        case RECONNECT:
	            if (mIsUPDataEnable)
	                mServiceProxy.connectToService();
	            break;
	        case DISCONNECT:
	        	String reason = null;
	        	if (msg.obj != null && msg.obj instanceof String)
	                reason = (String) msg.obj;
	            mServiceProxy.disconnectFromService(reason);
	            break;
	        case UP_SWITCH:
	            // only works on competitor device
	            if (!HtcUPDataUtils.isHtcDevice(mContext)) {
	                Bundle control = new Bundle();
	                if (msg.arg1 == UP_ON) {
	                    mIsUPDataEnable = true;
	                    HtcUPEnableChecker.setUPEnable(mContext, true);
	                    control.putBoolean(Common.PACKAGE_UP_ENABLE, true);
	                } else if (msg.arg1 == UP_OFF) {
	                    mIsUPDataEnable = false;
	                    HtcUPEnableChecker.setUPEnable(mContext, false);
	                    // clear all data when UP data has been disabled.
	                    HtcUPLocalStore.getInstance(mContext).clearDataStore();
	                    control.putBoolean(Common.PACKAGE_UP_ENABLE, false);
	                }
	        		mServiceProxy.controlSendingUPData(control);
	        	}
	        	break;
	        default:
	            Log.d(Common.TAG, "[HtcUPHander] message error!");
	    }
    }
    
    private static class HtcUPEnableChecker {
    	private static final String TAG = "HtcUPEnableChecker";
        private static final String preferenceName = Common.UP_PREFERENCE_NAME;
        private static final String IS_ENABLE = "is_enable";
        
        public static boolean isUPEnable(Context context) {
        	// In HTC device, we always return true here due to there are global settings to disable UP data.
        	if (HtcUPDataUtils.isHtcDevice(context)) {
        		Log.d(TAG, "In HTC device, ignore UP data switch.");
        	    return true;
        	}
        	
            boolean isEnable;
            SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
            isEnable = preferences.getBoolean(IS_ENABLE, false);
            Log.d(TAG, "Is UP data enable: " + isEnable);
            return isEnable;
        }
        
        public static void setUPEnable(Context context, boolean isEnable) {
        	SharedPreferences preferences = context.getSharedPreferences(preferenceName, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(IS_ENABLE, isEnable);
            editor.commit();//It is OK due to we are in background thread.
        }
    }
     
}
