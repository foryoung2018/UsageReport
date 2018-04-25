package com.htc.lib1.upm;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.htc.lib1.upm.ulog.ULogAdapter;
import com.htc.lib2.up.HtcUPServiceClient;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*package*/class HtcUPServiceProxy implements HtcUPServiceClient.OnConnectedListener, HtcUPServiceClient.OnFailedConnectedListener, ComponentCallbacks2{
       
    private static final String TAG = "HtcUPServiceProxy";
    private static final long IDLE_TIMEOUT = 180000;
    private static final long WAIT_FOR_RECONNECT = 5000;
    private static final int MAX_RETRY = 2;
    private Context mContext;
    private Handler mHandler;
    private HtcUPServiceClient mUPServiceClient;
    private HtcUPLocalStore mLocalStore;
    private HtcUPUploadScheduler mUploadScheduler;
    private final Queue<Bundle> mDataQueue = new ConcurrentLinkedQueue<Bundle>();
    private final Queue<Bundle> mControlQueue = new ConcurrentLinkedQueue<Bundle>();
    private long mLastRequestTime;
    private boolean mUploadSelf;
    private String mPackageName;
    private final boolean mIsHtcDevice;
    private volatile ConnectState mState;
    private volatile int mRetry;
     
    private static enum ConnectState {
    	DISCONNECTED,
    	CONNECTING,
        CONNECTED_SERVICE,
        CONNECTED_LOCAL,
        PENDING_CONNECTION, /*for failing to connect to service case(ex. there is no UP service)*/
        PENDING_DISCONNECT,
        BACKWARD_COMPATIBLE,
        IGNORE;
    }
    
    public HtcUPServiceProxy(Context context, Handler handler, boolean uploadSelf) {
        mContext = context;
        mHandler = handler;
        mState = ConnectState.DISCONNECTED;
        mUploadScheduler = new HtcUPUploadScheduler(mContext, mHandler);
        mRetry = 0;
        mUploadSelf = uploadSelf;
        mIsHtcDevice = HtcUPDataUtils.isHtcDevice(mContext);
        try {
        	mUPServiceClient = new HtcUPServiceClient(mContext, this, this);
        } catch (IllegalArgumentException e) {
        	Log.e(TAG, "Cannot new instance of HtcUPServiceClient: " + e.getMessage(), e);
        }
    }
    
    public void init(boolean isUPEnable) {
        
        // In non-HTC device, we should check if HSP signature is same as caller app 
        // because the HSP probably is fake. If negative, just upload from app by itself.
        boolean hasSameSignatureAsHsp = false;
        if(!mIsHtcDevice) {
            hasSameSignatureAsHsp = HtcUPDataUtils.hasSameSignatureAsHsp(mContext);
            Log.d(TAG, "same signature: "+ hasSameSignatureAsHsp +", currernt state: " + mState);
        }
        
        // If application wants to upload data by itself, we force change
        // current state to CONNECTED_LOCAL and don't try to bind HSP.
        if (mUploadSelf || (!mIsHtcDevice && !hasSameSignatureAsHsp)) {
            Log.d(TAG, "Connect to local");
        	mLocalStore = HtcUPLocalStore.getInstance(mContext);
            mState = ConnectState.CONNECTED_LOCAL;
        } else if (isUPEnable){
        	// we connect to HSP when only user wants to send BI data.
            connectToService();
        }
        mPackageName = HtcUPDataUtils.getPackageName(mContext);
        Log.d(TAG, "[init] Package: " + mPackageName + " --> UP data enable: " + isUPEnable +  ", dipatch self: " + mUploadSelf + ", currernt state: " + mState);
                
        // Get last uploading time if we are in competitor device,
        // or calling application wants to upload data by itself.
        if (!mIsHtcDevice || mUploadSelf) {
            mUploadScheduler.init();
        }
    }
    
    public void connectToService() {
        if (mState != ConnectState.CONNECTED_LOCAL && mUPServiceClient != null) {
            Log.d(TAG,"Connecting to UP service...");
            mRetry ++;
            mState = ConnectState.CONNECTING;
            mUPServiceClient.connect();
        } else {
            useLocalStore();
        }
    }
    
    public void disconnectFromService(String reason) {
        if (mState == ConnectState.CONNECTED_SERVICE && mUPServiceClient != null) {
            Log.d(TAG, "Disconnect from  UP serivce: " + reason );
           mState = ConnectState.PENDING_DISCONNECT;
           mUPServiceClient.disconnect();
        }
    }
    
    public void checkServiceIdle() {
        if (mState == ConnectState.CONNECTED_SERVICE && mDataQueue.isEmpty() && (System.currentTimeMillis() - mLastRequestTime > IDLE_TIMEOUT)) {
            disconnectFromService("No interaction with app");
        } else {
            scheduleIdleChecking();
        }
    }
    
    public void upload() {
        mUploadScheduler.startUploadService(System.currentTimeMillis());
        mUploadScheduler.checkSchedule(System.currentTimeMillis());
    }
    
    public void putDataToQueue(Bundle data) {
    	data.putString(Common.PACKAGE_NAME, mPackageName);
    	mDataQueue.add(data);
        handleQueue();
    }
    
    public void controlSendingUPData(Bundle data) {
    	data.putString(Common.PACKAGE_CONTROL, mPackageName);
    	mControlQueue.add(data);
    	handleQueue();
    }
    
    private void handleQueue() {
        if (!Thread.currentThread().equals(mHandler.getLooper().getThread())) {
            //onConnected() and onDisconnected are called by main thread, 
            //but we need to handle queue with background thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        handleQueue();
                    } catch (Exception e) {
                        Log.e(TAG, "[Warning] Exception in handleQueue runnable!", e);
                    }
                }
            });
            return;
        }
        
        switch (mState) {
            case CONNECTED_SERVICE:
                int retry = 0;
                while (!mDataQueue.isEmpty() && mUPServiceClient != null) {
                    Log.d(TAG, "Send data to UP service.");
                    Bundle data  = mDataQueue.peek();
                    if (mUPServiceClient.sentToService(data)) {
                    	mDataQueue.poll();
                        retry = 0;
                    } else {
                        retry ++;
                        if (retry > MAX_RETRY) {
                            Log.d(TAG, "Failed to send data to service for 3 times, so ignore data.");
                            mDataQueue.poll();
                            retry = 0;
                        }
                    }
                }
                mLastRequestTime = System.currentTimeMillis();
                                
                
                if (!mIsHtcDevice) {
                	while (!mControlQueue.isEmpty() && mUPServiceClient != null) {
                		mUPServiceClient.controlUPSwitch(mControlQueue.poll());
                	}
                	// Consume old data directly.
                    if (HtcUPLocalStore.getInstance(mContext).existPendingData()) {
                        upload();
                    }
                }
                break;
                
            case BACKWARD_COMPATIBLE:
                while (!mDataQueue.isEmpty()) {
                    ULogAdapter.sendByULog(mDataQueue.poll());
                }
                break;
                
            case CONNECTED_LOCAL:
                while (!mDataQueue.isEmpty()) {
                    Bundle data = mDataQueue.poll();
                    mLocalStore.storeDataToFile(data, data.getBoolean(Common.IS_SECURE));
                }
                // Just ignore all control message
                if (!mIsHtcDevice) {
                    while (!mControlQueue.isEmpty()) {
                	    mControlQueue.poll();
                    }
                }
                mUploadScheduler.checkSchedule(System.currentTimeMillis());
                break;
                
            case DISCONNECTED:
                connectToService();
                break;
            
            case IGNORE:
            	// Remove all data from the queue.
            	while (!mDataQueue.isEmpty()) {
            		mDataQueue.poll();
            	}
            	break;
                
            default:
                Log.d(TAG, "Connection state: "+ mState);
                break;
        }
    }
    
    private void useLocalStore() {
        if (!mIsHtcDevice) {
            if (mState == ConnectState.CONNECTED_LOCAL)
                return;
            
            Log.d(TAG, "Use local store.");
            mLocalStore = HtcUPLocalStore.getInstance(mContext);
            mState = ConnectState.CONNECTED_LOCAL;
        } else {
            if (!HtcUPDataUtils.isKitKatOrBelow()) {
            	//If we cannot bind UP service in HTC device + L release, then ignore data directly.
                Log.d(TAG, "Ignore data due to there is no HtcUPService in HEP + L release.");
                mState = ConnectState.IGNORE;
            } else {
                //Consider backward compatible for Sense6 case.(Reflection ULog method)
                Log.d(TAG, "Backward compatible case: Use ULog APIs");
                mState = ConnectState.BACKWARD_COMPATIBLE;
            }
        }
        cancelDelayMessage();
        handleQueue();
    }
    
    private void scheduleIdleChecking() {
        Message message = Message.obtain();
        message.what = HtcUPHandler.CHECK_IDLE;
        mHandler.sendMessageDelayed(message, IDLE_TIMEOUT);
    }
    
    private void scheduleReConnect() {
        Message message = Message.obtain();
        message.what = HtcUPHandler.RECONNECT;
        mHandler.sendMessageDelayed(message, WAIT_FOR_RECONNECT);
    }
    
    private void cancelDelayMessage() {
        mHandler.removeMessages(HtcUPHandler.CHECK_IDLE);
        mHandler.removeMessages(HtcUPHandler.RECONNECT);
    }
    
    //Note: This is called by main thread, we need to handle queue with background thread.
    @Override
    public void onConnected() {
        mState = ConnectState.CONNECTED_SERVICE;
        Log.d(TAG, "Connect to service, change state to " + mState);
        mRetry = 0;
        scheduleIdleChecking();
        handleQueue();
    }

    //Note: This is called by main thread, we need to handle queue with background thread.
    @Override
    public void onDisconnected() {
        if (mState == ConnectState.PENDING_DISCONNECT) {
            Log.d(TAG, "Disconnect from UP service. Change state to: " + ConnectState.DISCONNECTED);
        } else {
            //mState = ConnectState.CONNECTING;
            Log.d(TAG, "Unexpected disconnect from service and wait system restart our service,"
                    + " change state from: " +mState + " to " + ConnectState.DISCONNECTED);
        }
        mState = ConnectState.DISCONNECTED;
    }

    @Override
    public void onFailedConnected(String reason) {
        mState = ConnectState.PENDING_CONNECTION;
        if (mRetry < MAX_RETRY) {
            Log.d(TAG, "Fail reson: " + reason + ", retry times: " + mRetry);
            scheduleReConnect();
        } else {
            Log.d(TAG, "Fail reson: " + reason + ", and use local store");
            useLocalStore();
        }
    }
    
    //Note: This is called by main thread, we need to handle queue with background thread.
  	@Override
  	public void onTrimMemory(int level) {
  		Log.d(TAG, "onTrimMemory() has been called. Memory Level: " + level);
  	    if (level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE || level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
  	        Message msg = Message.obtain();
  	        msg.what = HtcUPHandler.DISCONNECT;
  	        msg.obj = "Memory low.";
  	        mHandler.sendMessage(msg);
  	    }
  	}

    //Note: This is called by main thread, we need to handle queue with background thread.
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
	}

	//Note: This is called by main thread, we need to handle queue with background thread.
	@Override
	public void onLowMemory() {
		
	}
}
