package com.htc.lib2.up;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class HtcUPServiceClient {

    private final static String TAG = "HtcUPServiceClient";
    static final String APP_PACKAGE_NAME_HTCBIDHANDLER = "com.htc.bidhandler";
    static final String APP_PACKAGE_NAME_HSP = "com.htc.sense.hsp";
    static final String SERVICE_PACKAGE_NAME_HTCBIDHANDLER = "com.htc.bidhandler.HtcUPService";
    static final String SERVICE_PACKAGE_NAME_HSP = "com.htc.sense.hsp.upservice.HtcUPService";
    static final String SERVICE_ACTION_HTCBIDHANDLER = "com.htc.bidhandler.START";
    static final String SERVICE_ACTION_HSP =  "com.htc.sense.hsp.upservice.START";
    private static final int SEND_DATA_TO_SERVICE = 1;
    private static final int SEND_CONTROL_TO_SERVICE = 3;
    private Messenger mService;
    private OnConnectedListener mOnConnectedListener;
    private OnFailedConnectedListener mOnFailedConnectedListener;
    private ServiceConnection mConnection;
    private Context mContext;
    private boolean mBound;

    public HtcUPServiceClient(Context context, OnConnectedListener listener, OnFailedConnectedListener failedListener) {
        if (context == null || listener == null || failedListener == null)
            throw new IllegalArgumentException("[HtcUPServiceClient] context or listener cannot be null! ");
        mContext = context;
        mOnConnectedListener = listener;
        mOnFailedConnectedListener = failedListener;
        mConnection = new HtcUPServiceConnection();
        mBound = false;
    }

    public static abstract interface OnConnectedListener {
        public abstract void onConnected();

        public abstract void onDisconnected();
    }

    public static abstract interface OnFailedConnectedListener {
        public abstract void onFailedConnected(String reason);
    }

    public boolean sentToService(Bundle data) {
        Messenger service = mService;
        if (mBound && service != null) {
            Message msg = Message.obtain();
            msg.what = SEND_DATA_TO_SERVICE;
            msg.obj = data;
            try {
                service.send(msg);
                return true;
            } catch (RemoteException re) {
                Log.e(TAG, "Failed to send data to UP service", re);
                return false;
            }
        }
        return false;
    }

    public void controlUPSwitch(Bundle control) {
        Messenger service = mService;
        if (mBound && service != null) {
            Message msg = Message.obtain();
            msg.what = SEND_CONTROL_TO_SERVICE;
            msg.obj = control;
            try {
                service.send(msg);
            } catch (RemoteException re) {
                Log.e(TAG, "Failed to control UP  switch", re);
            }
        }
    }

    public void connect() {
        if (isAppInstalled(mContext, APP_PACKAGE_NAME_HTCBIDHANDLER)){
            connectSetting(APP_PACKAGE_NAME_HTCBIDHANDLER, SERVICE_PACKAGE_NAME_HTCBIDHANDLER, SERVICE_ACTION_HTCBIDHANDLER);
        } else {
            Log.d(TAG, "HtcBIDHandler not exists, use HSP");
            connectSetting(APP_PACKAGE_NAME_HSP, SERVICE_PACKAGE_NAME_HSP, SERVICE_ACTION_HSP);
        }
    }

    private boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void connectSetting(String packageName, String packageNameUPService, String serviceAction) {
        Intent intent = new Intent(serviceAction);
        intent.setClassName(packageName, packageNameUPService);
        if (mBound) {
            Log.d(TAG, "Calling connect() while still connected, ignore this conncet");
            return;
        }

        boolean result = mContext.bindService(intent, mConnection,
                Context.BIND_NOT_FOREGROUND | Context.BIND_AUTO_CREATE);
        if (!result) {
            Log.d(TAG, "Cannot connect to UP service!");
            mOnFailedConnectedListener.onFailedConnected("Failed to bind service");
            // mConnection = null;
        }
    }

    public void disconnect() {
        if (mBound) {
            try {
                mContext.unbindService(mConnection);
            } catch (Exception e) {
                Log.e(TAG, "Failed to unbind service.", e);
            }
            mService = null;
            mBound = false;
            mOnConnectedListener.onDisconnected();
        }
    }

    final class HtcUPServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Get UP service binder: " + service + ", name: " + name);
            mService = new Messenger(service);
            mBound = true;
            mOnConnectedListener.onConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected UP service");
            mBound = false;
            mService = null;
            mOnConnectedListener.onDisconnected();
        }
    };

}
