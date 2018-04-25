
package com.htc.lib1.cs;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Helper class for device management.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 * 
 * @author samael_wang@htc.com
 */
public class DeviceManagementUtils {
    /** Package name of dm-client. */
    public static final String PACKAGE_NAME_DM_CLIENT = "com.htc.cs.dm";

    /**
     * Check if dm-client is installed on current running device.
     * 
     * @param context Context to operate on.
     * @return True if it is, false otherwise.
     */
    public static final boolean isDMClientInstalled(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        try {
            context.getPackageManager().getPackageInfo(PACKAGE_NAME_DM_CLIENT, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
