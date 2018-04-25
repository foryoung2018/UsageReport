package com.htc.lib1.lockscreen.reminder;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * HtcReminderManager
 * For Control HTC Alert View Priority.
 */
public class HtcReminderManager {

    private static final String TAG = "RemiManager";

    private HtcReminderManagerImpl mManagerImpl;
    private ArrayList<HtcReminderClient> mHtcReminderCallback;
    private ArrayList<Integer> mViewMode;
    private boolean mCleanUp = false;

    /** command action: back to lock screen */
    public static final String ACTION_BACK_TO_LOCKSCREEN  = "backToLockScreen";
    /** command action: notify service when App is in unlocking */
    public static final String ACTION_NOTIFY_IN_UNLOCKING = "notifyInUnlocking";

    /**
     * HtcReminderManager
     * @param context
     */
    public HtcReminderManager(Context context) {
        if (mManagerImpl == null) {
            mManagerImpl = new HtcReminderManagerImpl(context);
        }
        if (mManagerImpl != null) {
            mManagerImpl.setReminderManager(this);
        }
        mHtcReminderCallback = new ArrayList<HtcReminderClient>();
        mViewMode = new ArrayList<Integer>();
        mHandler = new MyHandler(Looper.getMainLooper());
    }

    /**
     * cleanUp
     */
    public void cleanUp() {
        MyLog.i(TAG, "cleanUp");
        mCleanUp = true;
        clearAllMessages();
        MyUtil.sendMessage(mHandler, WHAT_CLEANUP);
    }

    private void doCleanUp() {
        deleteAllClients();
        deleteAllViewMode();
        if (mManagerImpl != null) {
            mManagerImpl.setReminderManager(null);
        }
    }

    /**
     * registerClient
     * @param callback HtcReminderClient
     */
    public void registerCallback(HtcReminderClient callback) {
        if (mCleanUp || callback == null) {
            return;
        }
        Message msg = Message.obtain();
        if (msg != null) {
            msg.what = WHAT_REGISTER_CALLBACK;
            msg.obj = callback;
            MyUtil.sendMessage(mHandler, msg);
        }
    }

    private void doRegisterCallback(HtcReminderClient callback) {
        if (mCleanUp || callback == null) {
            return;
        }
        try {
            MyLog.i(TAG, "regiCb: " + callback);
            if (mHtcReminderCallback != null && !mHtcReminderCallback.contains(callback)) {
                mHtcReminderCallback.add(callback);
            }
            if (serviceConnected()) {
                callback.onViewModeChange(getCurViewMode());
            }
        } catch (Exception e) {
            MyLog.w(TAG, "regiCb E:" + e);
        }
        checkManagerStatus();
    }

    /**
     * unregisterClient
     * @param callback HtcReminderClient
     */
    public void unregisterCallback(HtcReminderClient callback) {
        if (mCleanUp || callback == null) {
            return;
        }
        Message msg = Message.obtain();
        if (msg != null) {
            msg.what = WHAT_UNREGISTER_CALLBACK;
            msg.obj = callback;
            MyUtil.sendMessage(mHandler, msg);
        }
    }

    private void doUnregisterCallback(HtcReminderClient callback) {
        if (mCleanUp || callback == null) {
            return;
        }
        MyLog.i(TAG, "un-regiCb: " + callback);
        try {
            if (mHtcReminderCallback != null && mHtcReminderCallback.contains(callback)) {
                mHtcReminderCallback.remove(callback);
            }
        } catch(Exception e) {
            MyLog.w(TAG, "un-regiCb E: " + e);
        }
        checkManagerStatus();
    }

    /**
     * registerViewMode
     * @param viewMode int
     */
    public void registerViewMode(int viewMode) {
        if (mCleanUp) {
            return;
        }
        Message msg = Message.obtain();
        if (msg != null) {
            msg.what = WHAT_REGISTER_VIEWMODE;
            msg.arg1 = viewMode;
            MyUtil.sendMessage(mHandler, msg);
        }
    }

    private void doRegisterViewMode(int viewMode) {
        if (mCleanUp) {
            return;
        }
        MyLog.i(TAG, "regiVM: " + viewMode);
        if (mViewMode != null && !mViewMode.contains(viewMode)) {
            mViewMode.add(viewMode);
        }
        checkManagerStatus();
        if (mManagerImpl != null) {
            mManagerImpl.registerViewMode(viewMode);
        }
    }

    /**
     * unregisterViewMode
     * @param viewMode int
     */
    public void unregisterViewMode(int viewMode) {
        if (mCleanUp) {
            return;
        }
        Message msg = Message.obtain();
        if (msg != null) {
            msg.what = WHAT_UNREGISTER_VIEWMODE;
            msg.arg1 = viewMode;
            MyUtil.sendMessage(mHandler, msg);
        }
    }

    private void doUnregisterViewMode(int viewMode) {
        if (mCleanUp) {
            return;
        }
        MyLog.i(TAG, "un-regiVM: " + viewMode);
        if (mViewMode != null && mViewMode.contains(viewMode)) {
            mViewMode.remove(new Integer(viewMode));
        }
        if (mManagerImpl != null) {
            mManagerImpl.unregisterViewMode(viewMode);
        }
        checkManagerStatus();
    }

    /**
     * Notify Lock Screen to show the locked screen.
     */
    public Bundle sendCommand(String action, Bundle extras) {
        if (mCleanUp) {
            MyLog.w(TAG, "sendCommand fail");
            return null;
        }
        MyLog.i(TAG, "sendCommand: " + action);
        if (action != null && ACTION_NOTIFY_IN_UNLOCKING.equals(action)) {
            Message msg = Message.obtain();
            if (msg != null) {
                msg.what = WHAT_APP_IN_UNLOCKING;
                MyUtil.sendMessage(mHandler, msg);
            }
        } else {
            if (mManagerImpl != null) {
                mManagerImpl.sendCommand(action, extras);
            }            
        }
        return null;
    }

    private boolean serviceConnected() {
        if (mManagerImpl != null) {
            return mManagerImpl.serviceConnected();
        }
        return false;
    }

    private int getCurViewMode() {
        if (mManagerImpl != null) {
            return mManagerImpl.getcurViewMode();
        }
        return HtcReminderViewMode.INVALID_MODE;
    }

    private void checkManagerStatus() {
        int numClien = (mHtcReminderCallback != null)? mHtcReminderCallback.size():0;
        int numMode = (mViewMode != null)? mViewMode.size():0;
        if (numClien <= 0 && numMode <= 0) {
            mManagerImpl.setReminderManager(null);
        } else {
            mManagerImpl.setReminderManager(this);
        }
    }

    void checkRegisteredViewMode() {
        MyUtil.sendMessage(mHandler, WHAT_CHECK_VIEWMODE);
    }

    private void doCheckRegisteredViewMode() {
        if (mViewMode != null && mManagerImpl != null) {
            int viewMode = 0;
            int size = mViewMode.size();
            for (int i=0; i<size; i++) {
                viewMode = mViewMode.get(i);
                mManagerImpl.addViewMode(viewMode);
            }
        }
    }

    private void deleteAllClients() {
        if (mHtcReminderCallback != null) {
            mHtcReminderCallback.clear();
        }
    }

    private void deleteAllViewMode() {
        if (mViewMode != null) {
            if (mManagerImpl != null) {
                int viewMode = 0;
                int size = mViewMode.size();
                for (int i=0; i<size; i++) {
                    viewMode = mViewMode.get(i);
                    mManagerImpl.deleteViewMode(viewMode);
                }
            }
            mViewMode.clear();
        }
    }

    void notifyViewModeChange(int viewMode) {
        Message msg = Message.obtain();
        if (msg != null) {
            msg.what = WHAT_NOTIFY_VIEWMODE;
            msg.arg1 = viewMode;
            MyUtil.sendMessage(mHandler, msg);
        }
    }

    void notifyUnlock() {
        MyUtil.sendMessage(mHandler, WHAT_NOTIFY_UNLOCK);
    }    

    void notifyCommand(String action, Bundle extras) {
        Message msg = Message.obtain();
        if (msg != null) {
            msg.what = WHAT_NOTIFY_COMMAND;
            msg.obj = new commandData(action, extras);
            MyUtil.sendMessage(mHandler, msg);
        }
    }

    private void doNotify(int type, int viewMode, String action, Bundle extras) {
        if (type == WHAT_NOTIFY_UNLOCK) {
            doForUnlocked();
        }
        if (mHtcReminderCallback != null) {
            int size = mHtcReminderCallback.size();
            try {
                for (int i=0; i<size; i++) {
                    doNotifyClient(mHtcReminderCallback.get(i), type, viewMode, action, extras);
                }
             } catch (Exception e) {
                MyLog.w(TAG, "notify " + type + " E: " + e);
            }
        }
    }

    private void doNotifyClient(HtcReminderClient client,
            int type, int viewMode, String action, Bundle extras) {
        try {
            if (client != null) {
                switch (type) {
                case WHAT_NOTIFY_UNLOCK:
                    client.unlock();
                    break;
                case WHAT_NOTIFY_VIEWMODE:
                    client.onViewModeChange(viewMode);
                    break;
                case WHAT_NOTIFY_COMMAND:
                    client.sendCommand(action, extras);
                    break;
                }
            }
        } catch (Exception e) {
            MyLog.w(TAG, "doNotiC: " + client + ", " + e);
        }
    }

    private void doForUnlocked() {
        MyLog.v(TAG, "unlock 2 clear ViewMode");
        if (mViewMode != null) {
            mViewMode.clear();
        }
        checkManagerStatus();
    }

    private MyHandler mHandler;
    private static final int WHAT_REGISTER_CALLBACK   = 3001;
    private static final int WHAT_UNREGISTER_CALLBACK = 3002;
    private static final int WHAT_REGISTER_VIEWMODE   = 3003;
    private static final int WHAT_UNREGISTER_VIEWMODE = 3004;    
    private static final int WHAT_CHECK_VIEWMODE      = 3005;
    private static final int WHAT_NOTIFY_VIEWMODE     = 3006;
    private static final int WHAT_NOTIFY_UNLOCK       = 3007;
    private static final int WHAT_NOTIFY_COMMAND      = 3008;
    private static final int WHAT_CLEANUP             = 3009;
    private static final int WHAT_APP_IN_UNLOCKING    = 3010;
    private class commandData {
        String mAction;
        Bundle mExtras;
        public commandData(String action, Bundle extras) {
            mAction = action;
            mExtras = extras;
        }
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            switch (msg.what) {
            case WHAT_REGISTER_CALLBACK:
                doRegisterCallback((HtcReminderClient) msg.obj);
                break;
            case WHAT_UNREGISTER_CALLBACK:
                doUnregisterCallback((HtcReminderClient) msg.obj);
                break;
            case WHAT_REGISTER_VIEWMODE:
                doRegisterViewMode(msg.arg1);
                break;
            case WHAT_UNREGISTER_VIEWMODE:
                doUnregisterViewMode(msg.arg1);
                break;
            case WHAT_CHECK_VIEWMODE:
                doCheckRegisteredViewMode();
                break;
            case WHAT_NOTIFY_VIEWMODE:
                doNotify(WHAT_NOTIFY_VIEWMODE,
                        msg.arg1,
                        null,
                        null);
                break;
            case WHAT_NOTIFY_UNLOCK:
                doNotify(WHAT_NOTIFY_UNLOCK,
                        HtcReminderViewMode.INVALID_MODE,
                        null,
                        null);
                break;
            case WHAT_NOTIFY_COMMAND:
                commandData data = (commandData) msg.obj;
                if (data != null) {
                    doNotify(WHAT_NOTIFY_COMMAND, 
                            HtcReminderViewMode.INVALID_MODE, 
                            data.mAction, 
                            data.mExtras);
                }
                break;
            case WHAT_CLEANUP:
                doCleanUp();
                break;
            case WHAT_APP_IN_UNLOCKING:
                doNotifyInUnlocking();
                break;
            }
        }
    }

    private void clearAllMessages() {
        MyUtil.removeMessage(mHandler, WHAT_REGISTER_CALLBACK);
        MyUtil.removeMessage(mHandler, WHAT_UNREGISTER_CALLBACK);
        MyUtil.removeMessage(mHandler, WHAT_REGISTER_VIEWMODE);
        MyUtil.removeMessage(mHandler, WHAT_UNREGISTER_VIEWMODE);
        MyUtil.removeMessage(mHandler, WHAT_CHECK_VIEWMODE);
        MyUtil.removeMessage(mHandler, WHAT_NOTIFY_VIEWMODE);
        MyUtil.removeMessage(mHandler, WHAT_NOTIFY_UNLOCK);
        MyUtil.removeMessage(mHandler, WHAT_NOTIFY_COMMAND);
        MyUtil.removeMessage(mHandler, WHAT_APP_IN_UNLOCKING);
    }

    /** 
     * Receive the unlocking notification from App side.
     * Need to feedback to Service side to clear the queue of view mode.
     */
    private void doNotifyInUnlocking() {
        // Notify ReminderService when App is in unlocking.
        if (mManagerImpl != null) {
            mManagerImpl.unlock();
        }
    }

}
