
package com.htc.lib1.cs.auth.client;

import java.util.regex.Pattern;

/**
 * Constants used in {@link EmailAccountUtils}.
 */
public class EmailAccountUtilsDefs {

    /** Account type of hotmail used by HTC mail app. */
    public static final String MAIL_HOTMAIL = "com.htc.android.windowslive";

    /** Account type of exchange active sync used by HTC mail app. */
    public static final String MAIL_EXCHANGE = "com.htc.android.mail.eas";

    /** Account type of generic mail accounts used by HTC mail app. */
    public static final String MAIL_GENERIC = "com.htc.android.mail";

    /** Account type of google / gmail accounts. */
    public static final String MAIL_GMAIL = "com.google";

    /** Regular expression pattern of an UUID. */
    public static final Pattern PATTERN_UUID = Pattern
            .compile("[a-fA-Z0-9]{8}-[a-fA-Z0-9]{4}-[a-fA-Z0-9]{4}-[a-fA-Z0-9]{4}-[a-fA-Z0-9]{12}");

    public static final String[] EMAIL_VERIFICATION_SENDERS = new String[] {
            "do-not-reply@htcsense.com", "do-not-reply@htctouch.com"
    };

}
