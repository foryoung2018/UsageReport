package com.htc.lib1.cs;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

import java.util.ArrayList;

/**
 * Created by leohsu on 2017/3/28.
 */

public class PermissionHelper {
    private Activity mActivity;
    private final PermissionStatus[] mPermissions;
    private final int mRequestCode;

    private HtcLogger mLogger = new AuthLoggerFactory(this).create();

    public enum ResultCode {
        // These values are ordered by priority. Review all ordinal() if need to change them.
        NOT_HANDLED,
        ALL_GRANTED,
        DENIED,
        SHOULD_GOTO_SETTINGS,
    }

    public PermissionHelper(Activity activity, String[] permissions, int requestCode) {
        mActivity = activity;
        mPermissions = new PermissionStatus[permissions.length];
        for (int i = 0; i < permissions.length; ++i) {
            mPermissions[i] = new PermissionStatus(permissions[i]);
        }
        mRequestCode = requestCode;
    }

    public void destroy() {
        mActivity = null;
    }

    public boolean checkPermissions() {
        boolean grantedAll = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (PermissionStatus ps : mPermissions) {
                ps.mGranted = mActivity.checkSelfPermission(ps.mPermission) ==
                        PackageManager.PERMISSION_GRANTED;
                if (!ps.mGranted) {
                    ps.mShouldShowRationale =
                            mActivity.shouldShowRequestPermissionRationale(ps.mPermission);
                    grantedAll = false;
                }
            }
        }
        return grantedAll;
    }

    public boolean shouldShowRequestPermissionRationale() {
        boolean shouldShow = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (PermissionStatus ps : mPermissions) {
                if (ps.mShouldShowRationale) {
                    shouldShow = true;
                }
            }
        }
        return shouldShow;
    }

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> reqPermissions = new ArrayList<>();
            for (PermissionStatus ps : mPermissions) {
                if (!ps.mGranted) {
                    reqPermissions.add(ps.mPermission);
                }
            }

            String[] permissions = new String[reqPermissions.size()];
            permissions = reqPermissions.toArray(permissions);
            mActivity.requestPermissions(permissions, mRequestCode);
        }
    }

    public ResultCode handleRequestPermissionResult(int requestCode,
                                                    String[] permissions,
                                                    int[] grantResults) {
        if (mRequestCode != requestCode) {
            return ResultCode.NOT_HANDLED;
        }

        ResultCode result = ResultCode.ALL_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; ++i) {
                PermissionStatus ps = getPermissionStatus(permissions[i]);
                if (ps == null) {
                    mLogger.warning("Requested permission " + permissions[i] + " is not required!");
                    continue;
                }

                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    ps.mGranted = true;
                } else {
                    boolean shouldShowRationale =
                            mActivity.shouldShowRequestPermissionRationale(ps.mPermission);
                    if (ps.mShouldShowRationale) {
                        // Has been shown before requesting, result in DENIED.
                        if (result.ordinal() < ResultCode.DENIED.ordinal()) {
                            result = ResultCode.DENIED;
                        }
                    } else if (shouldShowRationale) {
                        // Should not show -> should show, this is the first time requesting
                        // a permission, result in DENIED.
                        if (result.ordinal() < ResultCode.DENIED.ordinal()) {
                            result = ResultCode.DENIED;
                        }
                    } else {
                        // User selected "never ask again"
                        result = ResultCode.SHOULD_GOTO_SETTINGS;
                    }
                    ps.mShouldShowRationale = shouldShowRationale;
                }
            }
        }

        return result;
    }

    private PermissionStatus getPermissionStatus(String permission) {
        for (PermissionStatus ps : mPermissions) {
            if (ps.mPermission.equals(permission)) {
                return ps;
            }
        }
        return null;
    }

    private class PermissionStatus {
        final String mPermission;
        boolean mGranted = false;
        boolean mShouldShowRationale = false;

        PermissionStatus(String permission) {
            mPermission = permission;
        }
    }
}
