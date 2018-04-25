package com.htc.libfeedframework.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.List;

public abstract class HtcFeedService extends Service {
    protected final String LOG_TAG = getClass().getSimpleName();
    private boolean m_bOnUnbindInvoked = false;

    public static final String INTENT_EXTRA_BOOLEAN_KEEPALIVE = "intent_extra_boolean_keepalive";

    public static final int COMMAND_GET_PROPERTY = -101;
    public static final int COMMAND_DISABLED = -110;
    public static final String KEY_PROPERTY_FILTERABLE = "key_property_filterable";
//    public static final int COMMAND_GET_PROPERTY = FeedProvider.COMMAND_GET_PROPERTY;
//    public static final int COMMAND_DISABLED = FeedProvider.COMMAND_DISABLED;
//    public static final String KEY_PROPERTY_FILTERABLE = FeedProvider.KEY_PROPERTY_FILTERABLE;

    protected boolean onUnbind(Intent intent, boolean bDying) {
//        Logger.d(LOG_TAG, "onUnbind(%b) not overrided %s", bDying, intent);
        return false;
    }

    @Override
    public final boolean onUnbind(Intent intent) {
        if (m_bOnUnbindInvoked) {
            return false;
        }
        m_bOnUnbindInvoked = true;

        boolean bKeepalive = intent.getBooleanExtra(INTENT_EXTRA_BOOLEAN_KEEPALIVE, false);
        boolean bResult = onUnbind(intent, bKeepalive);
        boolean bIsReadyTodie = isReadyToDie();

//        Logger.d(LOG_TAG, "keepalive:%b, isReadyTodie:%b", bKeepalive, bIsReadyTodie);

        if (!bKeepalive || bIsReadyTodie) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    killMyProcess();
                }
            });
        }

        return (!bKeepalive && bResult);
    }

    private boolean isReadyToDie() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            int myPid = android.os.Process.myPid();
            List<RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
            if (processes != null) {
                for (RunningAppProcessInfo runningAppProcessInfo : processes) {
                    if (runningAppProcessInfo == null) {
                        continue;
                    }

                    if (myPid == runningAppProcessInfo.pid) {
                        switch (runningAppProcessInfo.importance) {
                            case (RunningAppProcessInfo.IMPORTANCE_BACKGROUND):
                            case (RunningAppProcessInfo.IMPORTANCE_EMPTY):
                            case (RunningAppProcessInfo.IMPORTANCE_SERVICE):
                                return true;

                            default:
                                return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    private final void killMyProcess() {
        int nMyPid = android.os.Process.myPid();
//        Logger.i(LOG_TAG, "killProcess(%d)", nMyPid);
        android.os.Process.killProcess(nMyPid);
    }

}
