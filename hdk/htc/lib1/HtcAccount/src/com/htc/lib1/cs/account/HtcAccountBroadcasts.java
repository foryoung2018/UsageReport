
package com.htc.lib1.cs.account;

import android.content.Context;

/**
 * HTC Account broadcasts. System HTC Account authenticator sends those
 * broadcasts through {@link Context#sendBroadcast(android.content.Intent)},
 * while if WebView implementation sends the same broadcasts through
 * {@code LocalBroadcastManager}.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountBroadcasts {

    /**
     * An add account flow has been completed. The source package name which
     * initiates the flow could be found with
     * {@link HtcAccountDefs#KEY_SOURCE_SERVICE}.
     */
    public static final String ACTION_ADD_ACCOUNT_COMPLETED =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".ADD_ACCOUNT_COMPLETED";

    /**
     * An add account flow has been canceled.The source package name which
     * initiates the flow could be found with
     * {@link HtcAccountDefs#KEY_SOURCE_SERVICE}.
     */
    public static final String ACTION_ADD_ACCOUNT_CANCELED =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".ADD_ACCOUNT_CANCELED";

    /**
     * HTC Account has been removed from the system.
     */
    public static final String ACTION_REMOVE_ACCOUNT_COMPLETED =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".REMOVE_ACCOUNT_COMPLETED";

    /**
     * HTC Account has been removed from the system, this action will need integrated app to extends
     *  {@link SignedInAccountAppProvider}, ane implements
     *  {@link SignedInAccountAppProvider#getRemoveAccountReceiverName()} to receive the force
     *  sign-out intent.
     */
    public static final String ACTION_ACCOUNT_FORCE_SIGNED_OUT =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".ACCOUNT_FORCE_SIGNED_OUT";

    /**
     * HTC Account authtoken has been renewed.
     */
    public static final String ACTION_AUTH_TOKEN_RENEWED =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".AUTH_TOKEN_RENEWED";

    /**
     * Extra which resolves to a {@link String}, indicating the package name of
     * the app which triggers the add account flow.
     */
    public static final String KEY_SOURCE_PACKAGE = HtcAccountDefs.KEY_SOURCE_PACKAGE;

    /**
     * Extra which resolves to a {@code boolean} to indicate if the account add
     * / remove event was caused by that user has signed in a different account.
     */
    public static final String KEY_ACCOUNT_CHANGED =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".ACCOUNT_CHANGED";

    /**
     * Extra which resolves to a {@link String}, indicating the type of
     * authtoken renewed.
     */
    public static final String KEY_AUTHTOKEN_TYPE =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".AUTHTOKEN_TYPE";

    /**
     * The permission which the broadcaster holds if it's sent as a system
     * broadcast but not local broadcast.
     */
    public static final String PERMISSION_SEND =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".permission.SEND_BROADCAST";

    /**
     * The permission the receiver must holds if it's sent as a system broadcast
     * but not local broadcast.
     */
    public static final String PERMISSION_RECEIVE =
            HtcAccountDefs.GENERAL_PREFIX_IDENTITY + ".permission.RECEIVE_BROADCAST";
}
