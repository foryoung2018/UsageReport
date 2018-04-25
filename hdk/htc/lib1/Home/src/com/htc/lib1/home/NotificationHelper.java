package com.htc.lib1.home;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

/**
 * The helper function to set notification count for applications/shortcuts
 *
 * @exthide
 */
public class NotificationHelper {

    private static final String TAG = NotificationHelper.class.getSimpleName();

    /**
     * Broadcast Action: A sticky broadcast when finishing loading items on workspace.
     */
    public static final String ACTION_ITEM_ADDED = "com.htc.launcher.action.ACTION_ITEM_ADDED";

    /**
     * Broadcast Action: A sticky broadcast to set notification count on workapce and all apps.
     */
    public static final String ACTION_SET_NOTIFICATION = "com.htc.launcher.action.SET_NOTIFICATION";

    private static final String PERMISSION_NOTIFICATION = "com.htc.sense.permission.APP_HSP";

    /**
     * Used as an string extra field in {@link #ACTION_SET_NOTIFICATION} to identify the component of applications.
     */
    public static final String EXTRA_COMPONENT = "com.htc.launcher.extra.COMPONENT";

    /**
     * Used as an string extra field in {@link #ACTION_SET_NOTIFICATION} to identify what URI of shortcut should be set.
     */
    public static final String EXTRA_DATA = "com.htc.launcher.extra.DATA";

    /**
     * Used as an int extra field in {@link #ACTION_SET_NOTIFICATION} for setting count value of notification.
     */
    public static final String EXTRA_COUNT = "com.htc.launcher.extra.COUNT";

    /**
     * method name for remote content provider.
     */
    public static final String METHOD_SET_BADGE_COUNT = "SetBadgeCount";

    /**
     * required permission for caller.
     */
    private static final String PERMISSION_APP_DEFAULT = "com.htc.permission.APP_DEFAULT";

    /**
     * URI of remote content provider.
     */
    private static final Uri uri = Uri.parse("content://com.htc.launcher.settings");

    /**
     * Call this function to set the notification count for the specified application.
     * The notification count will show on the application in workspace and in all apps.
     *
     * @param context       The context of calling application.
     * @param componentName The component name of the application which you would like to show notification count.
     * @param nCount        The value of notification count.
     */
    public static void setCount(Context context, ComponentName componentName, int nCount) {

        if (componentName == null) {
            Log.e(TAG, "Component name is null");
            return;
        }

        if (!setCountByProvider(context, componentName, null, nCount)) {
            setCountByBroadcast(context, componentName, null, nCount);
        }

    }

    /**
     * Call this function to set the notification count for shortcut.
     * The notification count will show on the specified shortcut in workspace.
     *
     * @param context The context of calling application.
     * @param strData Use this value to match the data filed of the shortcut intent. Normally it should be the encoded string of URI.
     * @param nCount  The value of notification count.
     */
    public static void setCount(Context context, String strData, int nCount) {

        if (TextUtils.isEmpty(strData)) {
            Log.e(TAG, "URI data is null or empty");
            return;
        }

        if (!setCountByProvider(context, null, strData, nCount)) {
            setCountByBroadcast(context, null, strData, nCount);
        }

    }

    private static void setCountByBroadcast(Context context,
                                            ComponentName componentName, String strData, int nCount) {
        Intent intent = new Intent(ACTION_SET_NOTIFICATION);
        if (componentName != null) {
            intent.putExtra(EXTRA_COMPONENT, componentName.flattenToShortString());
        }
        if (strData != null) {
            intent.putExtra(EXTRA_DATA, strData);
        }
        intent.putExtra(EXTRA_COUNT, nCount);
        context.sendBroadcast(intent,PERMISSION_NOTIFICATION);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static boolean setCountByProvider(Context context,
                                              ComponentName componentName, String strData, int nCount) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        }

        ContentProviderClient client = null;
        try {
            client = context.getContentResolver()
                    .acquireUnstableContentProviderClient(uri);

            if (client != null) {
                Bundle bundle = new Bundle();
                if (componentName != null) {
                    bundle.putString(EXTRA_COMPONENT, componentName.flattenToShortString());
                }
                if (strData != null) {
                    bundle.putString(EXTRA_DATA, strData);
                }
                bundle.putInt(EXTRA_COUNT, nCount);

                Bundle ret = client.call(METHOD_SET_BADGE_COUNT, null, bundle);
                if (ret != null) {
                    return ret.getBoolean(METHOD_SET_BADGE_COUNT, false);
                }
            }
        } catch (DeadObjectException e) {
            Log.w(TAG, "acquireUnstableContentProviderClient with exception! uri:" + uri);
        } catch (RemoteException e) {
            Log.w(TAG, "Exception when calling method");
        } catch (Exception e) {
            Log.w(TAG, "Exception : " + e.getMessage());
        } finally {
            if (client != null) {
                client.release();
            }
        }
        return false;
    }

    public static BadgeInfo createBadgeInfo(Context context, Bundle bundle,
                                            boolean bCheckPermission, String strCaller) {

        if (bCheckPermission && !TextUtils.isEmpty(strCaller)) {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null &&
                    PackageManager.PERMISSION_GRANTED != packageManager.checkPermission(
                            PERMISSION_APP_DEFAULT, strCaller)) {
                Log.e(TAG, "no permission " + PERMISSION_APP_DEFAULT + ": caller=" + strCaller);
                return null;
            }
        }

        String strComponentName = bundle.getString(EXTRA_COMPONENT);
        String strData = bundle.getString(EXTRA_DATA);
        int nCount = bundle.getInt(EXTRA_COUNT, 0);

        if ((TextUtils.isEmpty(strComponentName) && TextUtils.isEmpty(strData)) || nCount < 0) {
            Log.e(TAG, "invalid format ");
            return null;
        }

        return new BadgeInfo(strComponentName, strData, nCount);
    }

    public static BadgeInfo createBadgeInfo(Intent intent) {

        String strComponentName = intent.getStringExtra(EXTRA_COMPONENT);
        String strData = intent.getStringExtra(EXTRA_DATA);
        int nCount = intent.getIntExtra(EXTRA_COUNT, 0);

        if ((TextUtils.isEmpty(strComponentName) && TextUtils.isEmpty(strData)) || nCount < 0) {
            Log.e(TAG, "invalid format ");
            return null;
        }

        return new BadgeInfo(strComponentName, strData, nCount);
    }

    public static Bundle buildResult(boolean bRet) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(METHOD_SET_BADGE_COUNT, bRet);
        return bundle;
    }
}
