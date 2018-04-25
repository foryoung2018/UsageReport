package com.htc.lib1.dm.env;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import com.htc.lib1.dm.exception.DMException;
import com.htc.lib1.dm.exception.DMUnexpectedException;
import com.htc.lib1.dm.logging.Logger;

/**
 * Useful information about the APP.
 *
 * @author brian_anderson
 */
public class AppEnv {

    private static final Logger LOGGER = Logger.getLogger("[DM]", AppEnv.class);

    // --------------------------------------------------

    // Singleton instance...
    private static AppEnv sInstance = null;

    // --------------------------------------------------

    private Context context;

    // --------------------------------------------------

    private AppEnv(Context context) {
        this.context = context;
    }

    public static AppEnv get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }

        synchronized (AppEnv.class) {

            if (sInstance == null) {
                sInstance = new AppEnv(context.getApplicationContext());
                LOGGER.debug("Created new instance: {}", sInstance);
            }

            return sInstance;
        }
    }

    // --------------------------------------------------

    /**
     * The application version to report to the server.
     */
    public String getAppVersion() throws DMException {
        String packageName = context.getPackageName();
        int packageVersionCode = getAppVersionCode();
        String packageVersionName = getAppVersionName();
        return String.format("%s;%s;%s", packageName, packageVersionName, packageVersionCode);
    }

    /**
     * The application version code.
     * <p/>
     * Obtained from the application's Android manifest.
     *
     * @return the application version code
     */
    public int getAppVersionCode() throws DMException {
        return getMyPackageInfo().versionCode;
    }

    /**
     * The application version name.
     * <p/>
     * Obtained from the application's Android manifest.
     *
     * @return the application version name of "<unknown>" if not specified.
     */
    public String getAppVersionName() throws DMException {
        String versionName = getMyPackageInfo().versionName;

        if (TextUtils.isEmpty(versionName)) {
            versionName = "<unknown>";
        }

        return versionName;
    }

    public String getAppID() {
        return context.getPackageName();
    }

    private PackageInfo getMyPackageInfo() throws DMException {
        String myPackageName = context.getPackageName();

        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(myPackageName, PackageManager.GET_META_DATA);
        } catch (NameNotFoundException ex) {
            throw new DMUnexpectedException("Couldn't find my package info: packageName=" + myPackageName);
        } catch (Exception ex) {
            // Maybe android.os.TransactionTooLargeException or java.lang.RuntimeException: Package manager has died
            LOGGER.warning(ex);
            throw new DMUnexpectedException(ex);
        }
    }

}
