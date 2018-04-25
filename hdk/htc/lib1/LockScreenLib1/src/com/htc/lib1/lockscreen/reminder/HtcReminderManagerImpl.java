package com.htc.lib1.lockscreen.reminder;

import com.htc.reminderview.service.IHtcReminderService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

/** @hide */
class HtcReminderManagerImpl {

    private static final String TAG = "RemiManagerImpl";

    private Object mLock = new Object();
    private Context mContext;
    private IHtcReminderService mHtcReminderService;
    private ServiceConnection mServiceConnection;
    private HtcReminderClientStub mHtcReminderClientStub;
    private HtcReminderManager mReminderManager;

    private boolean mHasReminderService;
    private int mCurrentViewMode = HtcReminderViewMode.INVALID_MODE;

    private static final String SERVICE_PACKAGENAME = "com.htc.lockscreen";
    private static final String SERVICE_COMPONENTNAME = "com.htc.lockscreen.service.HtcReminderService";

    private boolean mUnlockPending = false;
    private boolean mBackToLSPending = false;
    private static final int ACTION_NONE = 0;
    private static final int ACTION_GOTO_LOCK_SCREEN = 200;
    private static final int ACTION_GOTO_UNLOCK_SCREEN = 300;

    /**
     * HtcReminderManagerImpl
     * @param context context
     */
    public HtcReminderManagerImpl(Context context) {
        mContext = context;
        mHasReminderService = hasReminderService(mContext);
    }

    private boolean hasReminderService(Context context) {
        String packageName = SERVICE_PACKAGENAME;
        String lockScreenClass = SERVICE_COMPONENTNAME;
        boolean installed = false;
        try {
            Context pluginContext = context.createPackageContext(packageName,
                    Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            ClassLoader pClassLoader = pluginContext.getClassLoader();
            Class<?> clazz = Class.forName(lockScreenClass, true, pClassLoader);
            installed = true;
        } catch (Exception ex) {
            MyLog.w(TAG, "NO HtcReminderService");
        }
        return installed;
    }

    private void bindReminderService() {
        if (!mHasReminderService) {
            return;
        }
        synchronized (mLock) {
            if (mServiceConnection == null && mContext != null) {
                Intent serviceIntent = new Intent();
                serviceIntent.setClassName(SERVICE_PACKAGENAME, SERVICE_COMPONENTNAME);
                mServiceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        MyLog.d(TAG, "Disconnected: " + this);
                        mHtcReminderService = null;
                    }
    
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        MyLog.d(TAG, "Connected: " + this);
                        mHtcReminderService = IHtcReminderService.Stub.asInterface(service);
                        registerClient();
                        checkRegistedViewMode();
                        checkPendingAction();
                    }
                };
                try {
                    mContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);                    
                } catch (Exception e) {
                    MyLog.w(TAG, "bindService E: " + e);
                }
            }
        }
    }

    private void unBindReminderService() {
        synchronized (mLock) {
            unregisterClient();
            if (mServiceConnection != null && mContext != null) {
                try {
                    mContext.unbindService(mServiceConnection);
                } catch (Exception e) {
                    MyLog.w(TAG, "un-bindService E: " + e);
                }
                mServiceConnection = null;
                mHtcReminderService = null;
            }
            mCurrentViewMode = HtcReminderViewMode.INVALID_MODE;
        }
    }

    void setReminderManager(HtcReminderManager manager) {
        synchronized (mLock) {
            mReminderManager = manager;
            checkServiceStatus();
        }
    }

    boolean hasReminderService() {
        return mHasReminderService;
    }

    boolean serviceConnected() {
        return (mHtcReminderService != null);
    }

    int getcurViewMode() {
        return mCurrentViewMode;
    }

    private void registerClient() {
        try {
            if (mHtcReminderClientStub == null) {
                mHtcReminderClientStub = new HtcReminderClientStub(this);
            }
            if (mHtcReminderClientStub != null && mHtcReminderService != null) {
                notifyViewModeChange(mHtcReminderService.registerClient(mHtcReminderClientStub));
            }
        } catch (Exception e) {
            MyLog.w(TAG, "registerClient E: " + e);
        }
    }

    private void unregisterClient() {
        try {
            if (mHtcReminderClientStub != null && mHtcReminderService != null) {
                mHtcReminderService.unregisterClient(mHtcReminderClientStub);
                mHtcReminderClientStub.cleanUp();
                mHtcReminderClientStub = null;
            }
            mCurrentViewMode = HtcReminderViewMode.INVALID_MODE;
        } catch (Exception e) {
            MyLog.w(TAG, "registerClient E: " + e);
        }
    }

    /**
     * registerViewMode
     * @param viewMode int
     */
    void registerViewMode(int viewMode) {
        synchronized (mLock) {
            if (!mHasReminderService) {
                notifyViewModeChange(viewMode);
                return;
            }
            checkServiceStatus();
            addViewMode(viewMode);
        }
    }

    /**
     * unregisterViewMode
     * @param viewMode int
     */
    void unregisterViewMode(int viewMode) {
        synchronized (mLock) {
            if (!mHasReminderService) {
                if (mCurrentViewMode == viewMode) {
                    notifyViewModeChange(HtcReminderViewMode.INVALID_MODE);
                }
                return;
            }
            deleteViewMode(viewMode);
            checkServiceStatus();
        }
    }

    /**
     * get View Mode
     * @return viewMode int
     */
    int getViewMode() {
        int mode = HtcReminderViewMode.INVALID_MODE;
        try {
            if (mHtcReminderService != null) {
                mode = mHtcReminderService.getViewMode();
            }
        } catch (Exception e) {
            MyLog.w(TAG, "getVM E:" + e);
        }
        return mode;
    }

    private synchronized void checkServiceStatus() {
        if (mContext == null && !mHasReminderService) {
            return;
        }
        boolean disconnect = ((mReminderManager == null) && !mUnlockPending && !mBackToLSPending);
        if (disconnect) {
            if (mServiceConnection != null) {
                unBindReminderService();
            }
        } else {
            if (mServiceConnection == null) {
                bindReminderService();
            }
        }
    }

    private void checkRegistedViewMode() {
        if (mReminderManager != null) {
            mReminderManager.checkRegisteredViewMode();
        }
    }

    void addViewMode(int viewMode) {
        try {
            if (mHtcReminderService != null) {
                mHtcReminderService.registerViewMode(viewMode);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "addVM E:" + e);
        }
    }

    void deleteViewMode(int viewMode) {
        try {
            if (mHtcReminderService != null) {
                mHtcReminderService.unregisterViewMode(viewMode);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "deleteVM E:" + e);
        }
    }

    void unlock() {
         synchronized (mLock) {
            if (!mHasReminderService) {
                return;
            }
            MyLog.i(TAG, "unlock");
            if (mHtcReminderService == null) {
                checkServiceStatus();
            } else {
                doUnlock();
            }
            // updatePendingFlag() will update the pending flag to TRUE.
            // if ReminderService was existed but has not connected yet. 
            // That is, (mHasReminderService && mHtcReminderService == null)
            // Otherwise, it will clear flag if HtcReminderService is connected.
            // Therefore, we only need to update flag one time here.
            updatePendingFlag(ACTION_GOTO_UNLOCK_SCREEN);
        }
    }

    private boolean doUnlock() {
        boolean result = false;
        try {
            if (mHtcReminderService != null) {
                mHtcReminderService.unlock();
                result = true;
            }
        } catch (Exception e) {
            MyLog.e(TAG, "doUnlock E:" + e);
        }
        return result;
    }

    void sendCommand(String action, Bundle extras) {
        synchronized (mLock) {
            if (!mHasReminderService) {
                return;
            }
            if (extras == null) {
                MyLog.i(TAG, "sendCommand: " + action + ", null");
            } else {
                MyLog.i(TAG, "sendCommand: " + action);
            }
            if (mHtcReminderService == null) {
                checkServiceStatus();
            } else {
                doSendCommand(action, extras);
            }
            updatePendingFlag(action, extras);
        }
    }

    private boolean doSendCommand(String action, Bundle extras) {
        boolean result = false;
        try {
            if (mHtcReminderService != null) {
                mHtcReminderService.sendCommand(
                        action, 
                        extras);
                result = true;
            }
        } catch (Exception e) {
            MyLog.e(TAG, "doSendCommand E:" + e);
        }
        return result;
    }

    private static final String ACTION_BACK_TO_LOCKSCREEN = 
            HtcReminderManager.ACTION_BACK_TO_LOCKSCREEN;
    private void updatePendingFlag(String action, Bundle extras) {
        if (TextUtils.equals(action, ACTION_BACK_TO_LOCKSCREEN)) {
            updatePendingFlag(ACTION_GOTO_LOCK_SCREEN);
        }
    }

    private void updatePendingFlag(int pendingAction) {
        mBackToLSPending = false;
        mUnlockPending = false;
        boolean pending = (mHasReminderService && mHtcReminderService == null);
        switch(pendingAction) {
        case ACTION_GOTO_LOCK_SCREEN:
            mBackToLSPending = pending;
            break;
        case ACTION_GOTO_UNLOCK_SCREEN:
            mUnlockPending = pending;
            break;
        case ACTION_NONE:
            break;
        default:
            MyLog.i(TAG, "updatePendingFlag: unknown");
            break;
        }
    }

    private void checkPendingAction() {
        synchronized (mLock) {
            if (mBackToLSPending) {
                doSendCommand(
                        ACTION_BACK_TO_LOCKSCREEN,
                        null);
            }
            if (mUnlockPending) {
                doUnlock();
            }
            updatePendingFlag(ACTION_NONE);
            checkServiceStatus();
        }
    }

    void notifyViewModeChange(int viewMode) {
        synchronized (mLock) {
            mCurrentViewMode = viewMode;
            if (mReminderManager != null) {
                mReminderManager.notifyViewModeChange(viewMode);
            }
        }
    }

    void notifyUnlock() {
        synchronized (mLock) {
            if (mReminderManager != null) {
                mReminderManager.notifyUnlock();
            }
        }
    }

    void notifyCommand(String action, Bundle extras) {
        synchronized (mLock) {
            if (mReminderManager != null) {
                mReminderManager.notifyCommand(action, extras);
            }
        }
    }

}
