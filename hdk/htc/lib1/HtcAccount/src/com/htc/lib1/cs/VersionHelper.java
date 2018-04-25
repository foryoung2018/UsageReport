
package com.htc.lib1.cs;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Helper class to get version info.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 */
public class VersionHelper {
    private static VersionHelper mInstance;
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private String mVersionName;
    private String mVersionNameStrings[];
    private String mPath;
    private int mVersionCode;
    private Context mContext;

    /**
     * Get an instance of {@link VersionHelper}
     * 
     * @param context Context used to retrieve application context.
     * @return {@link VersionHelper} instance.
     */
    public static synchronized VersionHelper get(Context context) {
        if (mInstance == null) {
            mInstance = new VersionHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Construct a {@link VersionHelper}.
     * 
     * @param context Context to operate on.
     */
    private VersionHelper(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            mVersionName = info.versionName;
            if (mVersionName != null) {
                mVersionNameStrings = mVersionName.split("\\.");
            }
            mPath = info.applicationInfo.sourceDir;
            mVersionCode = info.versionCode;
        } catch (NameNotFoundException e) {
            // This should never happen since an application must be able to get
            // its own package name. If it happens it must be a bug.
            throw new IllegalStateException(e.getMessage(), e);
        }
        mContext = context;
    }

    /**
     * Check if the version name satisfies HTC format:<br>
     * Major.Minor.Timestamp.SrcCL.LocCL
     * 
     * @return True if the version string is generated from HTC prebuild system.
     */
    public boolean isHtcVersionFormat() {
        return (mVersionNameStrings != null && mVersionNameStrings.length == 5);
    }

    /**
     * Get the version in mojor.minor format.
     * 
     * @return Version or {@code null} if an error occurs.
     */
    public String getVersion() {
        try {
            return mVersionNameStrings[0] + "." + mVersionNameStrings[1];
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Get the source change list number from the version name.
     * 
     * @return CL or {@code null} if it's not HTC version format.
     */
    public String getChangeList() {
        try {
            return mVersionNameStrings[3];
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Get Git commit.
     * 
     * @return Git commit.
     */
    public String getCommit() {
        try {
            Class<?> clazz = Class.forName(mContext.getPackageName() + ".BuildInfo");
            Object obj = clazz.getMethod("get", Context.class).invoke(null, mContext);
            String commit = (String) clazz.getMethod("getGitCommit", (Class<?>[]) null).invoke(obj,
                    (Object[]) null);
            return commit.substring(0, 7);
        } catch (Exception e) {
            mLogger.info(e.getMessage());
            return null;
        }
    }

    /**
     * Get the version code.
     * 
     * @return Version code.
     */
    public int getVersionCode() {
        return mVersionCode;
    }

    /**
     * Get the full version name.
     * 
     * @return Version name.
     */
    public String getVersionName() {
        return mVersionName;
    }

    /**
     * Get application installed path.
     * 
     * @return Application path.
     */
    public String getApplicationPath() {
        return mPath;
    }
}
