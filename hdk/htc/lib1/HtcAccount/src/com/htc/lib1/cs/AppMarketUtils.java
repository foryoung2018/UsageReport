
package com.htc.lib1.cs;

import android.net.Uri;

/**
 * Helper class to generate the URI to the app marketplace. Although it's
 * designed for Google Play store, it seems most China-specific Android
 * marketplaces also use the same {@code market://app.package.name} protocol and
 * can work with the same URI.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 */
public class AppMarketUtils {

    /**
     * Get the URI to show details / download page of given application.
     * 
     * @param packageName Package name of the application.
     * @return URI to initiate Google Play.
     */
    public static Uri getShowDetailsUri(String packageName) {
        return Uri.parse("market://details?id=" + packageName);
    }

    /**
     * Get the URI to show search page of given application.
     * 
     * @param packageName Package name of the application.
     * @return URI to initiate Google Play.
     */
    public static Uri getSearchUri(String packageName) {
        return Uri.parse("market://search?q=" + packageName);
    }

}
