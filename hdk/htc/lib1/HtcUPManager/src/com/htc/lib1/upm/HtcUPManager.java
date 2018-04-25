package com.htc.lib1.upm;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;

public final class HtcUPManager {

    private static HtcUPManager sHtcBIManager;
    public static HtcUPManager getInstance(Context context, boolean forceUploadBySelf) {
        synchronized (HtcUPManager.class) {
            if (sHtcBIManager == null) {
                sHtcBIManager = new HtcUPManager(context, forceUploadBySelf);
            }
            return sHtcBIManager;
        }
    }

    public static HtcUPManager getInstance(Context context) {
        synchronized (HtcUPManager.class) {
            if (sHtcBIManager == null) {
                sHtcBIManager = new HtcUPManager(context, false);
            }
            return sHtcBIManager;
        }
    }

    private final UPManager mUPManager;

    private HtcUPManager(Context context, boolean uploadSelf) {
        if(isOwnerUser(context))
            mUPManager = new UPManagerImpl(context, uploadSelf);
        else
            mUPManager = new NullUPManager();
    }

    public void write(String appID, String category, String action , /*label*/ String stringValue, /*value*/int intValue) {
        mUPManager.write(appID, category, action, stringValue, intValue);
    }

    public void write(String appID, String category, String action, /*attribute labels*/String[] labels, /*attribute values*/String[] values) {
        mUPManager.write(appID, category, action, labels, values);
    }

    public void secureWrite(String appID, String category, String action , /*label*/ String stringValue, /*value*/int intValue) {
        mUPManager.secureWrite(appID, category, action, stringValue, intValue);
    }

    public void enableSendingOnNonHtcDevice(boolean isEnable) {
        mUPManager.enableSendingOnNonHtcDevice(isEnable);
    }

    public void enableDebugLog(boolean enable) {
        mUPManager.enableDebugLog(enable);
    }

    /**
     * @throws IllegalArgumentException if context is null
     */
    private boolean isOwnerUser(Context context) {
        if (context == null)
            throw new IllegalArgumentException("HtcUPManager's Context cannot be null!");

        boolean isOwner = false;
        UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
        if (um != null) {
            UserHandle userHandle = android.os.Process.myUserHandle();
            isOwner = um.getSerialNumberForUser(userHandle) == 0;
        }

        return isOwner;
    }

}
