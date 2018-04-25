package com.htc.lib1.cc.widget.reminder.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityManager;

import com.htc.lib1.cc.widget.reminder.debug.MyLog;
import com.htc.lib1.cc.widget.reminder.drag.DraggableView;

/** @hide */
public class MyUtil {

    private static final String TAG = "MyUtil";

    public static void sendMessage(Handler handler, int what) {
        sendMessage(handler, what, 0);
    }

    public static void sendMessage(Handler handler, int what, long delay) {
        if (handler == null) {
            return;
        }
        if (delay > 0) {
            handler.sendEmptyMessageDelayed(what, delay);
        }
        else {
            handler.sendEmptyMessage(what);
        }
    }

    public static void sendMessage(Handler handler, Message msg) {
        sendMessage(handler, msg, 0);
    }

    public static void sendMessage(Handler handler, Message msg, long delay) {
        if (handler == null) {
            return;
        }
        if (delay > 0) {
            handler.sendMessageDelayed(msg, delay);
        }
        else {
            handler.sendMessage(msg);
        }
    }

    public static void removeMessage(Handler handler, int what) {
        if (handler == null) {
            return;
        }
        handler.removeMessages(what);
    }

    public static int getStatusbarHeight(Context context) {
        int statusY = 0;
        // com.android.internal.R.dimen.status_bar_height
        if (context != null) {
            Resources res = context.getResources();
            if (res != null) {
                int status_bar_height = ResourceWrap.getID(res, ResourceWrap.STATUS_BAR_HEIGHT, 0);
                if (status_bar_height > 0) {
                    statusY = res.getDimensionPixelSize(status_bar_height);
                } else {
                    MyLog.w(TAG, "getSBHeight id<=0");
                }
            } else {
                MyLog.w(TAG, "getSBHeight fail: res");
            }
        } else {
            MyLog.w(TAG, "getSBHeight fail: context");
        }
        return statusY;
    }

    /** Get the id from APP: res, type, name  */
//    public static int getIdFromRes(Context context, int resName) {
//        int id = 0;
//        try {
//            id = ReminderResWrap.getID(context, resName, 0);
//            if (id <= 0) {
//                MyLog.w(TAG, "getIdFromRes Invalid:" + resName);
//            }
//        } catch (Exception e) {
//            MyLog.e(TAG, "getIdFromRes " + resName + ", E: " + e);
//        }
//        return id;
//    }

    public static Resources getResourceFormResApp(Context context) {
        Resources res = null;
        try {
//            PackageManager pm = null;
//            if (context != null) {
//                pm = context.getPackageManager();
//                if (pm != null) {
//                    res = pm.getResourcesForApplication(ReminderResWrap.REMINDER_VIEW_RES_PACKAGE);
//                }
//            }
            if (context != null) {
                res = context.getResources();
            }
        } catch (Exception e) {
            MyLog.e(TAG, "getResourceFormApp e:" + e.getMessage());
        }
        return res;
    }

    public static LayoutInflater getLayoutInflaterFromResApp(Context context) {
        LayoutInflater inflater = null;
        try {
//            inflater = LayoutInflater.from(context.createPackageContext(ReminderResWrap.REMINDER_VIEW_RES_PACKAGE,
//                    Context.CONTEXT_IGNORE_SECURITY));
            inflater = LayoutInflater.from(context);
        } catch (Exception e) {
            MyLog.e(TAG, "LayoutInflater e:" + e.getMessage());
        }
        return inflater;
    }

    public static boolean isVerticalDragType(DraggableView view) {
        int type = DraggableView.DRAG_TYPE_VERTICAL_HORIZONTAL;
        if (view != null) {
            type = view.getDragType();
        }
        return (type & DraggableView.DRAG_TYPE_ONLY_HORIZONTAL) == 0;
    }

    private static AccessibilityManager sAccessibility = null;
    private static boolean sAccessibilityEnable = false;
    public static boolean getAccessibilityEnable(Context context) {
        if (sAccessibility == null && context != null) {
            sAccessibility =
                    (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        }
        if (sAccessibility != null) {
            boolean isTouchExploration = sAccessibility.isTouchExplorationEnabled();
            boolean isEnable = sAccessibility.isEnabled();
            MyLog.i(TAG, "getAccessibilityEnable isTouchExploration:" + isTouchExploration
                    + " isEnable:" + isEnable);
            return isTouchExploration && isEnable;
        }
        return false;
    }

    public static void checkAccessibilityEnable(Context context) {
        sAccessibilityEnable = getAccessibilityEnable(context);
    }

    public static boolean isAccessibilityEnable() {
        return sAccessibilityEnable;
    }

    /** Navigation Bar */
//    private static final int OVERRIDE_NONE = -1;
//    private static final int OVERRIDE_SHOW_NAVIGATION_BAR = 0;
//    private static final int OVERRIDE_HIDE_NAVIGATION_BAR = 1;
//    // Cache the result to make hasNavigationBar() more efficiency
//    // if it is called many times.
//    private static Boolean sHasNavigationBar = null;
    /**
     * Check whether the system has navigation bar.
     * If you need to know whether it is visible,
     * you should also check the system UI visibility.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc. and MUST be blong to the subclass of ContextThemeWrapper.
     * @return boolean true: the navigation bar show, false:not show
     */
    public static boolean showHtcNavigationBarWrap(Context context) {
        return true;
        /*
        if (sHasNavigationBar == null) {
            // read hasNavigationBar from
            // com.android.internal.R.bool.config_showNavigationBar
            Class<?>[] classes;
            try {
                classes = Class.forName("com.android.internal.R").getClasses();
                Class<?> boolClass = null;
                for (Class<?> clazz : classes) {
                    if ("bool".equals(clazz.getSimpleName())) {
                        boolClass = clazz;
                    }
                }
                Field configShowNavigationBar = boolClass.getField("config_showNavigationBar");
                int configShowNavigationBarId = configShowNavigationBar.getInt(null);
                boolean hasNavigationBar = context.getResources().getBoolean(configShowNavigationBarId);
                // com.android.internal.R.bool.config_showNavigationBar
                // may be overrode by system property: qemu.hw.mainkeys
                int navigationBarOverride =
                        (Integer) Class.forName("android.os.SystemProperties")
                        .getMethod("getInt", String.class, int.class)
                        .invoke(null, "qemu.hw.mainkeys", OVERRIDE_NONE);
                switch (navigationBarOverride) {
                    case OVERRIDE_SHOW_NAVIGATION_BAR: {
                        hasNavigationBar = true;
                    } break;
                    case OVERRIDE_HIDE_NAVIGATION_BAR: {
                        hasNavigationBar = false;
                    } break;
                }
                sHasNavigationBar = hasNavigationBar;
            } catch (Exception e) {
                MyLog.w(TAG, "showHtcNavigationBarWrap: " + e);
            }
        }
        return sHasNavigationBar;
        */
    }

    public static int getNavigationBarHeight(Context context) {
        int nbY = 0;
        // com.android.internal.R.dimen.navigation_bar_height
        if (context != null) {
            Resources res = context.getResources();
            if (res != null) {
                int navigation_bar_height = ResourceWrap.getID(res, ResourceWrap.NAVIGATION_BAR_HEIGHT, 0);
                if (navigation_bar_height > 0) {
                    nbY = res.getDimensionPixelSize(navigation_bar_height);
                } else {
                    MyLog.w(TAG, "getNBHeight id<=0");
                }
            } else {
                MyLog.w(TAG, "getNBHeight fail: res");
            }
        } else {
            MyLog.w(TAG, "getNBHeight fail: context");
        }
        return nbY;
    }


}
