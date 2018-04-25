package com.htc.lib1.cc.widget.reminder.util;


import com.htc.lib1.cc.widget.reminder.debug.MyLog;

import android.content.res.Resources;

/** @hide */
public class ResourceWrap {

    private static final String TAG = "ResWrap";

    // com.android.internal.R.dimen.status_bar_height
    public static final int STATUS_BAR_HEIGHT = 1;
    // com.android.internal.R.dimen.navigation_bar_height
    public static final int NAVIGATION_BAR_HEIGHT = 2;

    private static int ID_STATUS_BAR_HEIGHT = 0;
    private static int ID_NAVIGATION_BAR_HEIGHT = 0;

    private static boolean S_INIT = false;

    public static int getID(Resources res, int name, int defID) {
        if (!S_INIT) {
            init(res);
            S_INIT = true;
        }
        int id;
        switch (name) {
        case STATUS_BAR_HEIGHT:
            id = ID_STATUS_BAR_HEIGHT;
            break;
        case NAVIGATION_BAR_HEIGHT:
            id = ID_NAVIGATION_BAR_HEIGHT;
            break;
        default:
            id = defID;
            break;
        }
        if (id <= 0) {
            id = defID;
        }
        return id;
    }

    private static void init(Resources res) {
        if (res != null) {
            ID_STATUS_BAR_HEIGHT = res.getIdentifier("status_bar_height", "dimen", "android");
            ID_NAVIGATION_BAR_HEIGHT = res.getIdentifier("navigation_bar_height", "dimen", "android");
        } else {
            MyLog.w(TAG, "init: res NULL.");
        }
    }
}
