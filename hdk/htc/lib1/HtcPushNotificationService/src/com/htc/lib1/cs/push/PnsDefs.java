
package com.htc.lib1.cs.push;

/**
 * Constants related to push notification service.
 *
 * @author samael_wang@htc.com
 */
public class PnsDefs {

    /**
     * The package name of PNS client HMS implementation since Sense 6.0.
     */
    public static final String PKG_NAME_PNS_CLIENT = "com.htc.cs.pns";

    /**
     * The package name of PNS client preloaded on devices prior and include
     * Sense 7.0.
     */
    //public static final String PKG_NAME_LEGACY_PNS_CLIENT = "com.htc.cs.pushclient";

    /**
     * The default PNS server assigned relative retry value in second is 2 days
     */
    public static final int DEFAULT_RETRY_AFTER_VALUE_IN_SEC = 0;

    /** Header field of retry-after value */
    public static final String HEADER_RETRY_AFTER = "Retry-After";
}
