
package com.htc.lib1.cs.app;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Helper class to get application info.
 * 
 * @author samael_wang@htc.com
 */
public class ApplicationInfoHelper {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private HashMap<String, Bundle> mMetaData = new HashMap<String, Bundle>();

    /**
     * Constructor for self-package.
     * 
     * @param context Context used to retrieve the application context.
     * @return {@link ApplicationInfoHelper}
     */
    public ApplicationInfoHelper(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null or empty.");

        mContext = context.getApplicationContext();
    }

    /**
     * Get application metadata with given package name.
     * 
     * @param packageName Package name of application to get metadata from.
     * @return Application metadata.
     */
    public Bundle getApplicationMetaData(String packageName) {
        /*
         * Metadata is not supposed to be change at runtime so we can use
         * in-memory cache.
         */
        if (!mMetaData.containsKey(packageName)) {
            try {
                /*
                 * Do not use Context.getApplicationInfo(). See
                 * http://code.google.com/p/android/issues/detail?id=37968.
                 * Scroogle.
                 */
                Bundle metaData = mContext.getPackageManager().getApplicationInfo(
                        packageName, PackageManager.GET_META_DATA).metaData;

                // Create empty bundle when metaData is null;
                if (metaData == null)
                    metaData = new Bundle();

                mLogger.debugS(metaData);
                mMetaData.put(packageName, metaData);
            } catch (NameNotFoundException e) {
                // It should never happen.
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        return mMetaData.get(packageName);
    }

    /**
     * Get application metadata of current working context.
     * 
     * @return Application metadata.
     */
    public Bundle getApplicationMetaData() {
        return getApplicationMetaData(mContext.getPackageName());
    }

    /**
     * Check if current application works as system app.
     * 
     * @return {@code true} if the application is a system app or an update of a
     *         system app.
     */
    public boolean isSystemApp() {
        int flags = mContext.getApplicationInfo().flags;
        boolean system = (flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||
                (flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
        mLogger.debugS("Is system app: ", system);
        return system;
    }

    /**
     * Check if the application is privileged if in K44 or later; otherwise
     * check if it's system app.
     * 
     * @return {@code true} if it is.
     */
    public boolean isPrivilegedApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mLogger.debug("The system is K44+. Check privileged flag.");
            int flags = mContext.getApplicationInfo().flags;
            boolean privileged = (flags & ApplicationInfoUtils.FLAG_PRIVILEGED) != 0;
            mLogger.debugS("Is privileged app: ", privileged);
            return privileged;
        } else {
            mLogger.debug("The system is prior to K44. Check system flag.");
            return isSystemApp();
        }
    }

    /**
     * Check if current package has the same signatures as given package.
     * 
     * @param packageName Package to compare with.
     * @return {@code true} if the signatures are the same.
     */
    public boolean hasSameSignaturesAs(String packageName) {
        PackageInfo myPkg, otherPkg;

        // Get package info of this app.
        try {
            myPkg = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            // It should never happen.
            throw new IllegalStateException(e.getMessage(), e);
        }

        // Get package info of given package name.
        try {
            otherPkg = mContext.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            // Happens when the package is updating or removing.
            mLogger.warning(e.getMessage());
            mLogger.debug("Package to compare not found.");
            return false;
        }

        /*
         * The # of signatures must be the same. In fact the length should
         * always be 1.
         */
        if (myPkg.signatures.length != otherPkg.signatures.length) {
            mLogger.debug("Number of signatures mismatch.");
            return false;
        }

        /*
         * If any of the signatures are different then return false.
         */
        for (int i = 0; i < myPkg.signatures.length; i++) {
            if (!myPkg.signatures[i].equals(otherPkg.signatures[i])) {
                mLogger.debug("Signatures mismatch.");
                return false;
            }
        }

        mLogger.debug("Signatures are the same.");
        return true;
    }

    /**
     * Check if the package has given permission.
     * 
     * @param permission Permission to check.
     * @return {@code true} if it has the permission.
     */
    public boolean hasPermission(String permission) {
        boolean result = (PackageManager.PERMISSION_GRANTED ==
                mContext.getPackageManager().checkPermission(permission, mContext.getPackageName()));
        mLogger.debug("Has permission '", permission, "': ", result);
        return result;
    }

}
