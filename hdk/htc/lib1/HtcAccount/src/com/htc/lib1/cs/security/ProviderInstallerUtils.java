
package com.htc.lib1.cs.security;

import java.security.Provider;
import java.security.Security;

import android.content.Context;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Utils for trying install security providers.
 * 
 * @author autosun_li@htc.com
 */
public class ProviderInstallerUtils {
    private static HtcLogger sLogger = new CommLoggerFactory(ProviderInstallerUtils.class).create();

    /**
     * Install security provider and continue working if failed.
     * 
     * @param context Context
     */
    public static void tryInstallIfNeed(Context context) {
        sLogger.verbose();

        try {
            /*
             * In order to let library user have choose if use google library or not,
             * using reflection to try dynamic security provider installation.
             * e.g. ProviderInstaller.installIfNeeded(context);
             */
            Class<?> installer = Class.forName("com.google.android.gms.security.ProviderInstaller");
            Class<?>[] paramTypes = { Context.class };
            Object[] params = { context };
            installer.getMethod("installIfNeeded", paramTypes).invoke(null, params);
        } catch (Exception e) {
            sLogger.warningS("Install security provider failed due to ", e.toString());
        } finally {
            Provider[] providers = Security.getProviders();
            if (providers != null && providers.length > 0) {
                sLogger.debugS("Prefered provider: ", providers[0]);
            }
        }
    }
}
