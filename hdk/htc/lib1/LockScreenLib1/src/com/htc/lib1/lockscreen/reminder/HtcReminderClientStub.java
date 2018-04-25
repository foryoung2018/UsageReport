package com.htc.lib1.lockscreen.reminder;

import android.os.Bundle;
import android.os.RemoteException;

import com.htc.reminderview.service.IHtcReminderClient;

/** @hide */
class HtcReminderClientStub extends IHtcReminderClient.Stub {

    HtcReminderManagerImpl mReminderManagerImpl;

    public HtcReminderClientStub(HtcReminderManagerImpl reminderService) {
        mReminderManagerImpl = reminderService;
    }

    public void cleanUp() {
        mReminderManagerImpl = null;
    }

    @Override
    public void onViewModeChange(int viewMode) throws RemoteException {
        if (mReminderManagerImpl != null) {
            mReminderManagerImpl.notifyViewModeChange(viewMode);
        }
    }

    @Override
    public void unlock() throws RemoteException {
        if (mReminderManagerImpl != null) {
            mReminderManagerImpl.notifyUnlock();
        }
    }

    @Override
    public Bundle sendCommand(String action, Bundle extras)
            throws RemoteException {
        if (mReminderManagerImpl != null) {
            mReminderManagerImpl.notifyCommand(action, extras);
        }
        return null;
    }

}
