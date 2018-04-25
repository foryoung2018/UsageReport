package com.htc.lib1.upm;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/**
 * @hide
 */
final class UPManagerImpl implements UPManager {

    private Context mContext;
    private Handler mHandler;
    private volatile boolean mIsDebugging;
    public UPManagerImpl(Context context, boolean uploadSelf) {
        // Need to get application context in order to prevent memory leak.
        if (context == null)
            throw new IllegalArgumentException("Context cannot be null!");

        mContext = context.getApplicationContext();
        if (mContext == null)
            throw new IllegalArgumentException("Application context is null!");

        mHandler =  new HtcUPHandler(HandlerThreadUtils.getInstance().getLooper(), mContext, uploadSelf);
        mHandler.sendEmptyMessage(HtcUPHandler.INIT);
        mIsDebugging = false;
    }

    public void write(String appID, String category, String action , /*label*/ String stringValue, /*value*/int intValue) {

        if (TextUtils.isEmpty(appID))
            throw new IllegalArgumentException("AppID cannot be null!");

        if (TextUtils.isEmpty(category))
            throw new IllegalArgumentException("category cannot be null!");

        send(HtcUPDataUtils.createBundleForUPData(appID, action, category, stringValue, intValue, null, null, 0, false, mIsDebugging));
    }

    public void write(String appID, String category, String action, /*attribute labels*/String[] labels, /*attribute values*/String[] values) {

        if (TextUtils.isEmpty(appID))
            throw new IllegalArgumentException("AppID cannot be null!");

        if (TextUtils.isEmpty(category))
            throw new IllegalArgumentException("category cannot be null!");

        HtcUPDataUtils.checkAttribute(labels, values);

        send(HtcUPDataUtils.createBundleForUPData(appID, action, category, null, -1, labels, values, 0, false, mIsDebugging));
    }

    public void secureWrite(String appID, String category, String action , /*label*/ String stringValue, /*value*/int intValue) {

        if (TextUtils.isEmpty(appID))
            throw new IllegalArgumentException("AppID cannot be null!");

        if (TextUtils.isEmpty(category))
            throw new IllegalArgumentException("category cannot be null!");

        send(HtcUPDataUtils.createBundleForUPData(appID, action, category, stringValue, intValue, null, null, 0, true, mIsDebugging));
    }

    public void enableSendingOnNonHtcDevice(boolean isEnable) {
        Message msg = Message.obtain();
        msg.what = HtcUPHandler.UP_SWITCH;
        msg.arg1 = isEnable ? HtcUPHandler.UP_ON : HtcUPHandler.UP_OFF;
        mHandler.sendMessage(msg);
    }

    public void enableDebugLog(boolean enable) {
        mIsDebugging = enable;
    }

    private void send(Bundle data){
        Message msg = mHandler.obtainMessage(HtcUPHandler.SEND, data);
        mHandler.sendMessage(msg);
    }

    /*
     * Only for debugging
     */
    /*public void delivery() {
        Log.d(Common.TAG, "delivery() had been called!");
        Message msg = mHandler.obtainMessage(HtcUPHandler.DISPATCH);
        mHandler.sendMessage(msg);
    }*/

}
