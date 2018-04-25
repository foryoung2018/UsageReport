package com.htc.lib1.home;

import android.annotation.TargetApi;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

/**
 * The helper function to start Launcher Bar item for applications/shortcuts/app shortcuts/folders
 *
 * @exthide
 */
public class LauncherBarHelper {

    private static final String TAG = LauncherBarHelper.class.getSimpleName();

    public static final int NO_ID = -1;

    /**
     * method name for remote content provider.
     */
    public static final String METHOD_START_LAUNCHER_BAR_ITEM = "StartLauncherBarItem";

    /**
     * extra name for remote content provider.
     */
    public static final String EXTRA_ITEM_ID = "com.htc.launcher.extra.ITEM_ID";

    /**
     * extra name for remote content provider.
     */
    public static final String EXTRA_ITEM_TYPE = "com.htc.launcher.extra.ITEM_TYPE";

    /**
     * extra name for remote content provider.
     */
    public static final String EXTRA_INTENT = "com.htc.launcher.extra.INTENT";

    /**
     * extra name for remote content provider.
     */
    public static final String EXTRA_PROFILE_ID = "com.htc.launcher.extra.PROFILE_ID";

    /**
     * required permission for caller.
     */
    private static final String PERMISSION_APP_HSP = "com.htc.sense.permission.APP_HSP";

    /**
     * URI of remote content provider.
     */
    private static final Uri URI = Uri.parse("content://com.htc.launcher.settings");

    /**
     * Call this function to start Launcher Bar item for the specified application.
     *
     * @param context       The context of calling application.
     * @param nItemId       The id of Launcher Bar item.
     * @param nItemType     The type of Launcher Bar item.
     * @param strIntent     The intent uri of Launcher Bar item.
     * @param lProfileId    The profile id of Launcher Bar item.
     */
    public static boolean startLauncherBarItem(Context context, int nItemId, int nItemType, String strIntent, long lProfileId) {

        if (nItemId <= NO_ID) {
            Log.e(TAG, "Item Id is invalid");
            return false;
        }

        return startLauncherBarItemByProvider(context, nItemId, nItemType, strIntent, lProfileId);
    }

    public static boolean startLauncherBarItem(Context context, int nItemId) {

        return startLauncherBarItem(context, nItemId, 0, null, 0l);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static boolean startLauncherBarItemByProvider(Context context, int nItemId, int nItemType, String strIntent, long lProfileId) {
        // ContentProviderClient.call() API added from API 17
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        }

        ContentProviderClient client = null;
        try {
            client = context.getContentResolver().acquireUnstableContentProviderClient(URI);

            if (client != null) {
                Bundle bundle = new Bundle();
                bundle.putInt(EXTRA_ITEM_ID, nItemId);
                if (strIntent != null && !strIntent.isEmpty()) {
                    bundle.putInt(EXTRA_ITEM_TYPE, nItemType);
                    bundle.putString(EXTRA_INTENT, strIntent);
                    bundle.putLong(EXTRA_PROFILE_ID, lProfileId);
                }

                Bundle ret = client.call(METHOD_START_LAUNCHER_BAR_ITEM, null, bundle);
                if (ret != null) {
                    return ret.getBoolean(METHOD_START_LAUNCHER_BAR_ITEM, false);
                }
            }
        } catch (DeadObjectException e) {
            Log.w(TAG, "acquireUnstableContentProviderClient with exception! uri:" + URI);
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

    public static int getItemId(Context context, Bundle bundle, boolean bCheckPermission, String strCaller) {
        if (bCheckPermission && !TextUtils.isEmpty(strCaller)) {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null &&
                    PackageManager.PERMISSION_GRANTED != packageManager.checkPermission(
                            PERMISSION_APP_HSP, strCaller)) {
                Log.e(TAG, "no permission " + PERMISSION_APP_HSP + ": caller=" + strCaller);
                return NO_ID;
            }
        }

        int nId = bundle.getInt(EXTRA_ITEM_ID, NO_ID);
        return nId;
    }

    public static Bundle buildResult(boolean bRet) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(METHOD_START_LAUNCHER_BAR_ITEM, bRet);
        return bundle;
    }
}
