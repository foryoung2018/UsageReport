
package com.htc.lib1.cs.app;

import android.content.pm.ApplicationInfo;

/**
 * Static utility methods related to application / package info.
 * 
 * @author samael_wang@htc.com
 */
public class ApplicationInfoUtils {
    /** Privileged flag from Android source tree. */
    public static final int FLAG_PRIVILEGED = 1 << 30;
    /** Forward lock flag from Android source tree. */
    public static final int FLAG_FORWARD_LOCK = 1 << 29;
    /** Can't save state flag from Android source tree. */
    public static final int FLAG_CANT_SAVE_STATE = 1 << 28;
    /** Blocked flag from Android source tree. */
    public static final int FLAG_BLOCKED = 1 << 27;

    /**
     * Convert {@link ApplicationInfo#flags} to a {@link String}.
     * 
     * @param flags {@link ApplicationInfo#flags}
     * @param delimeter The delimeter to use between flags. One can simply pass
     *            " ".
     * @return {@link String}
     */
    public static final String packageFlagsToString(int flags, String delimeter) {
        StringBuilder builder = new StringBuilder();
        if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0)
            builder.append("SYSTEM").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0)
            builder.append("DEBUGGABLE").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_HAS_CODE) != 0)
            builder.append("HAS_CODE").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_PERSISTENT) != 0)
            builder.append("PERSISTENT").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_FACTORY_TEST) != 0)
            builder.append("FACTORY_TEST").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_ALLOW_TASK_REPARENTING) != 0)
            builder.append("ALLOW_TASK_REPARENTING").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA) != 0)
            builder.append("ALLOW_CLEAR_USER_DATA").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
            builder.append("UPDATED_SYSTEM_APP").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_TEST_ONLY) != 0)
            builder.append("TEST_ONLY").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_SUPPORTS_SMALL_SCREENS) != 0)
            builder.append("SUPPORTS_SMALL_SCREENS").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_SUPPORTS_NORMAL_SCREENS) != 0)
            builder.append("SUPPORTS_NORMAL_SCREENS").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_SUPPORTS_LARGE_SCREENS) != 0)
            builder.append("SUPPORTS_LARGE_SCREENS").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_RESIZEABLE_FOR_SCREENS) != 0)
            builder.append("RESIZEABLE_FOR_SCREENS").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES) != 0)
            builder.append("SUPPORTS_SCREEN_DENSITIES").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_VM_SAFE_MODE) != 0)
            builder.append("VM_SAFE_MODE").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0)
            builder.append("ALLOW_BACKUP").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_KILL_AFTER_RESTORE) != 0)
            builder.append("KILL_AFTER_RESTORE").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_RESTORE_ANY_VERSION) != 0)
            builder.append("RESTORE_ANY_VERSION").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0)
            builder.append("EXTERNAL_STORAGE").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_SUPPORTS_XLARGE_SCREENS) != 0)
            builder.append("SUPPORTS_XLARGE_SCREENS").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0)
            builder.append("LARGE_HEAP").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_STOPPED) != 0)
            builder.append("STOPPED").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_SUPPORTS_RTL) != 0)
            builder.append("SUPPORTS_RTL").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_INSTALLED) != 0)
            builder.append("INSTALLED").append(delimeter);
        if ((flags & ApplicationInfo.FLAG_IS_DATA_ONLY) != 0)
            builder.append("IS_DATA_ONLY").append(delimeter);
        if ((flags & FLAG_PRIVILEGED) != 0)
            builder.append("PRIVILEGED").append(delimeter);
        if ((flags & FLAG_FORWARD_LOCK) != 0)
            builder.append("FORWARD_LOCK").append(delimeter);
        if ((flags & FLAG_CANT_SAVE_STATE) != 0)
            builder.append("FLAG_CANT_SAVE_STATE").append(delimeter);
        if ((flags & FLAG_BLOCKED) != 0)
            builder.append("BLOCKED").append(delimeter);
        return builder.toString().trim();
    }
}
