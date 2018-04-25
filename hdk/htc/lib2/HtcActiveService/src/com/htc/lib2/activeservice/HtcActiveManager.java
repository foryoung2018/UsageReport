package com.htc.lib2.activeservice;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.htc.lib2.activeservice.exception.ActiveNotConnectedException;
import com.htc.lib2.activeservice.exception.ActiveNotFoundException;
import com.htc.lib2.activeservice.exception.ActiveRemoteException;

/**
 * HtcAtiveManager let you connect to active service and query previous active data.
 */

public class HtcActiveManager {

    private final static String TAG = "HtcActiveManager";

    public final static String ACTION_BIND_ACTIVE_SERVICE = "com.htc.sense.hsp.activeservice.action_service_bind";
    private final static String NAME_ACTIVE_SERVICE_CLASS = "com.htc.sense.hsp.activeservice.HtcActiveService";
    private final static String NAME_HSP_PACKAGE = "com.htc.sense.hsp";

    private final static int MSG_TRANSPORT = 0;

    private IActiveService mService = null;
    private Context mContext = null;

    private ArrayList<TransportModeListener> mTransportModeListeners = new ArrayList<TransportModeListener>();
    private ServiceConnectionListener mConnectionListener;

    private boolean isITransportModeRegistered = false;
    private Handler mUiHandler;

    private boolean isConnected = false;

    /**
     * The HtcActiveManager constructor
     * @param context The context of application
     * */
    public HtcActiveManager(Context context) {
        mContext = context.getApplicationContext();
        mTransportModeListeners = new ArrayList<TransportModeListener>();

        mUiHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_TRANSPORT:
                        final int N = mTransportModeListeners.size();
                        if (N != 0) {
                            Log.d(TAG, "onTransportMode(), " + N + ", " + ((TransportModeRecord)msg.obj).getTimestamp());
                        }
                        for (int i = 0; i < N; i++) {
                            mTransportModeListeners.get(i).onTransportModeChanged((TransportModeRecord)msg.obj);
                        }
                        break;
                }
            }
        };
    }

    /**
     * Start to connect the active service.
     * ServiceConnectionListener will be called when the service is connected or disconnected. <br/>
     * <br/>
     * Need com.htc.permission.APP_DEFAULT permission.
     * @param listener the listener will be called when active service is connected/disconnected.
     * @throws throw {@link com.htc.lib2.activeservice.exception.ActiveNotFoundRemoteException} while
     * the active service cannot be found.
     */

    public void connect(ServiceConnectionListener listener) throws ActiveNotFoundException {
        Log.d(TAG, "connect() time = " + System.currentTimeMillis());
        mConnectionListener = listener;

        bindActiveService();
    };

    /**
     * Start to disconnect the active service.
     * ServiceConnectionListener will be called when the service is connected or disconnected.
     * @return
     */
    public void disconnect() {
        Log.d(TAG, "disconnect()");
        unbindActiveService();
    };


    /**
     * Active service connection. Will be called when active service is connected/disconnected.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.d(TAG, name + " connected time = " + System.currentTimeMillis());

            isConnected = true;

            mService = IActiveService.Stub.asInterface(service);

            if (mConnectionListener != null) {
                mConnectionListener.onConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d(TAG, name + " disconnected");

            isConnected = false;

            if (mConnectionListener != null) {
                mConnectionListener.onDisconnected();
            }
        }
    };

    /**
     * Check whether the active service is connected.
     * @return true if it is connected
     */
    public boolean isServiceConnected() {
        return (isConnected && mService != null);
    }

    //ITransportMode listener will post message to main looper of Client(App)
    //And it will be handled on App's looper thread
    private ITransportModeListener mITransportModeListener = new ITransportModeListener.Stub() {

        @Override
        public void onTransportModeChanged(TransportModeRecord r)
        throws RemoteException {
            // TODO Auto-generated method stub
            Message msg = mUiHandler.obtainMessage(MSG_TRANSPORT, r);
            Log.d(TAG, "Post MSG_TRANSPORT(" + r.getTimestamp() + ") to handler");
            mUiHandler.sendMessage(msg);
        }
    };
    /**
     * Register {@link com.htc.lib2.activeservice.TransportModeListener} listener.
     * The listener will be called when the screen is
     * ON and a transport record is generated.
     * <br/>
     *
     * {@link com.htc.lib2.activeservice.exception.ActiveRemoteException}
     * or {@link com.htc.lib2.activeservice.exception.ActiveNotConnectedException}
     * may be thrown while remote exception occurs, service is not connected, respectively.
     * @param {@link com.htc.lib2.activeservice.TransportModeListener} object.
     * @return return true if succeed. return false if failed.
     */
    public boolean registerTransportModeListener(TransportModeListener l) {
        Log.d(TAG, "regTransportMode()");
        if (isServiceConnected()) {
            if (!mTransportModeListeners.contains(l)) {
                mTransportModeListeners.add(l);
                Log.d(TAG, "registered listeners(" + mTransportModeListeners.size() + ")");
            }
            else {
                Log.w(TAG, "The listener has registered before");
            }

            if (isITransportModeRegistered == false) {
                return registerITranportModeListener();
            }

            return true;
        } else {
            throw new ActiveNotConnectedException();
        }
    }


    /**
     * Unregister {@link com.htc.lib2.activeservice.TransportModeListener} listener.
     * <br/>
     * {@link com.htc.lib2.activeservice.exception.ActiveRemoteException}
     * or {@link com.htc.lib2.activeservice.exception.ActiveNotConnectedException}
     * may be thrown while remote exception occurs, service is not connected, respectively.
     *
     * @param {@link com.htc.lib2.activeservice.TransportModeListener} object.
     */
    public void unregisterTransportModeListener(TransportModeListener l) {
        Log.d(TAG, "unregTransportMode()");
        if (isServiceConnected()) {
            if (mTransportModeListeners.contains(l)) {
                mTransportModeListeners.remove(l);

                Log.d(TAG, "remaining listeners(" + mTransportModeListeners.size() + ")");

                if (mTransportModeListeners.size() == 0) {
                    unregisterITransportModeListener();
                }
            }
        } else {
            throw new ActiveNotConnectedException();
        }
    }

    private boolean registerITranportModeListener() {
        if (mService == null) {
            throw new ActiveNotConnectedException();
        }

        if (mTransportModeListeners.size() > 0
                && isITransportModeRegistered == false) {
            try {
                if (mService == null || mITransportModeListener == null) {
                    Log.e(TAG, "Service or ITransportModeListener shouldn't be null when register listener");
                    return false;
                }

                isITransportModeRegistered = mService.registerTransportModeListener(mITransportModeListener);
                return isITransportModeRegistered;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                isITransportModeRegistered = false;
                e.printStackTrace();
                throw new ActiveRemoteException();
            }
        } else {
            return true;
        }
    }

    private void unregisterITransportModeListener() {
        if (mService != null
                && isITransportModeRegistered == true) {
            try {
                mService.unregisterTransportModeListener(mITransportModeListener);
                isITransportModeRegistered = false;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new ActiveRemoteException();
            }
        }
    }

    /** Get the latest transport mode record.
    * <br/>
    * {@link com.htc.lib2.activeservice.exception.ActiveRemoteException}
    * or {@link com.htc.lib2.activeservice.exception.ActiveNotConnectedException}
    * may be thrown while remote exception occurs, service is not connected, respectively.
    * @return The latest TransportModeRecord object if succeed. Return null if failed or no current transport mode record
    */
    public TransportModeRecord getLatestTransportModeRecord() {
        Log.d(TAG, "getLatestTransportMode()");
        if (mService != null) {
            try {
                return mService.getLatestTransportMode();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new ActiveRemoteException();
            }
        } else {
            throw new ActiveNotConnectedException();
        }
    }

    private void bindActiveService() throws ActiveNotFoundException {
        if (mService == null && isServiceConnected() == false) {
            Intent i = new Intent(ACTION_BIND_ACTIVE_SERVICE);
            i.setClassName(NAME_HSP_PACKAGE, NAME_ACTIVE_SERVICE_CLASS);
            boolean result = mContext.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bindService, result = " + result);
            if (!result) {
                throw new ActiveNotFoundException();
            }
        }
    }

    private void unbindActiveService() {
        if (mService != null && isServiceConnected() == true) {
            Log.d(TAG, "unbindService");

            if (isITransportModeRegistered) {
                unregisterITransportModeListener();
            }

            mContext.unbindService(mConnection);
            isConnected = false;
            mService = null;
        }
    }

    /**Query transport records from startTime to endTime.
     * If the number of the queried records is larger
     * than {@link com.htc.lib2.activeservice.TransportRecordsQueryResult#MAX_NUMBER_QUERY_TRANSPORT_RECORDS},
     * the TransportRecordsQueryResult will return the incomplete status.
     * In this case, if you need the complete records, you may query
     * again from the latest records in the queried result to the
     * end time you want.  <br/>
     * <br/>
     * {@link com.htc.lib2.activeservice.exception.ActiveRemoteException}
     * or {@link com.htc.lib2.activeservice.exception.ActiveNotConnectedException}
     * may be thrown while remote exception occurs, service is not connected, respectively.
     *
     * @param startTime Unix time (epoch time) format.
     * @param endTime Unix time (epoch time) format.
     * @return the transport mode query result.
     */

    public TransportRecordsQueryResult queryTransportModeRecords(long startTime , long endTime) {
        Log.d(TAG, "queryTransportMode() startT = " + startTime + " endT = " + endTime);
        if (isServiceConnected()) {
            try {
                return mService.queryTransportModeRecords(startTime, endTime);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new ActiveRemoteException();
            }
        } else {
            throw new ActiveNotConnectedException();
        }
    }

    /**
     * Return whether active engine is enabled or disabled.
     * <br/>
     * {@link com.htc.lib2.activeservice.exception.ActiveRemoteException}
     * or {@link com.htc.lib2.activeservice.exception.ActiveNotConnectedException}
     * may be thrown while remote exception occurs, service is not connected, respectively.
     * @return true if active engine is enabled or false on disabled.
     */
    public boolean isEnabled() {
        if (isServiceConnected()) {
            try {
                boolean serviceEnabled = mService.isEnabled();
                Log.d(TAG, "HtcActiveService isEnabled = " + serviceEnabled);
                return serviceEnabled;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new ActiveRemoteException();
            }
        } else {
            throw new ActiveNotConnectedException();
        }
    }


    /** Check whether the active engine is supported.
    * <br/>
    * {@link com.htc.lib2.activeservice.exception.ActiveRemoteException}
    * or {@link com.htc.lib2.activeservice.exception.ActiveNotConnectedException}
    * may be thrown while remote exception occurs, service is not connected, respectively.
    * @return true if active engine is supported.
    */
    public boolean isSupported() {
        if (isServiceConnected()) {
            try {
                boolean serviceSupported = mService.isSupported();
                Log.d(TAG, "HtcActiveService isSupported = " + serviceSupported);
                return serviceSupported;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new ActiveRemoteException();
            }
        } else {
            throw new ActiveNotConnectedException();
        }
    }

    /** Try to enable the active engine with permission.
    * <br/>
    * {@link com.htc.lib2.activeservice.exception.ActiveRemoteException}
    * , {@link com.htc.lib2.activeservice.exception.ActiveNotConnectedException}
    * or {@link com.htc.lib2.activeservice.exception.ActiveSecurityException}
    * may be thrown while remote exception occurs, service is not connected,
    * or permission is not enough, respectively.
    *
    * @return true if enabling active engine is successful.
    */
    public boolean enableWithPermission() {
        Log.d(TAG, "enableWithPermission()");
        if (isServiceConnected()) {
            try {
                return mService.enableWithPermission();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new ActiveRemoteException();
            }
        } else {
            throw new ActiveNotConnectedException();
        }
    }

    /** Try to disable the active engine with permission.
     *
     * <br/>
     * {@link com.htc.lib2.activeservice.exception.ActiveRemoteException}
     * , {@link com.htc.lib2.activeservice.exception.ActiveNotConnectedException}
     * or {@link com.htc.lib2.activeservice.exception.ActiveSecurityException}
     * may be thrown while remote exception occurs, service is not connected,
     * or permission is not enough, respectively.
     *
     * @return true if disenabling active engine is successful.
     */
    public boolean disableWithPermission() {
        Log.d(TAG, "disableWithPermission()");
        if (isServiceConnected()) {
            try {
                return mService.disableWithPermission();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new ActiveRemoteException();
            }
        } else {
            throw new ActiveNotConnectedException();
        }
    }
}
