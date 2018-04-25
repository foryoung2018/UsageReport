
package com.htc.lib1.cs.account;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;

/**
 * Helper class to check if a system authenticator exists or if the package
 * itself works as a system authenticator.
 * 
 * @author samael_wang@htc.com
 */
public class SystemAuthenticatorHelper {
    private static SystemAuthenticatorHelper sInstance;
    private Context mContext;

    /**
     * Get the instance of {@link SystemAuthenticatorHelper}.
     * 
     * @param context Context used to retrieve application context.
     * @return {@link SystemAuthenticatorHelper}
     */
    public static synchronized SystemAuthenticatorHelper get(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        if (sInstance == null)
            sInstance = new SystemAuthenticatorHelper(context.getApplicationContext());
        return sInstance;
    }

    private SystemAuthenticatorHelper(Context context) {
        mContext = context;
    }

    /**
     * Check if current package works as a system authenticator of given
     * account type.
     * 
     * @param accountType Account type to look for.
     * @return True if it's a system authenticator, false otherwise.
     */
    public boolean isSystemAuthenticator(String accountType) {
        final String packageName = mContext.getPackageName();
        return (packageName != null) && packageName.equals(getSystemAuthenticator(accountType));
    }

    /**
     * Check if the authenticator for the given account type exists in te
     * system.
     * 
     * @param accountType Account type to look for.
     * @return True if exists, false otherwise.
     */
    public boolean systemAuthenticatorExists(String accountType) {
        /* Check the existence of system authenticator. */
        AuthenticatorDescription[] authenticators =
                AccountManager.get(mContext).getAuthenticatorTypes();
        for (AuthenticatorDescription authenticator : authenticators) {
            if (authenticator.type.equals(accountType))
                return true;
        }
        return false;
    }

    /**
     * Get the package name of the system authenticator for given
     * {@code accountType}.
     * 
     * @param accountType Account type to look up with.
     * @return The package name of the authenticator or {@code null} if no
     *         system authenticator for the given type exists.
     */
    public String getSystemAuthenticator(String accountType) {
        /* Check the existence of system authenticator. */
        AuthenticatorDescription[] authenticators =
                AccountManager.get(mContext).getAuthenticatorTypes();
        for (AuthenticatorDescription authenticator : authenticators) {
            if (authenticator.type.equals(accountType))
                return authenticator.packageName;
        }

        return null;
    }
}
