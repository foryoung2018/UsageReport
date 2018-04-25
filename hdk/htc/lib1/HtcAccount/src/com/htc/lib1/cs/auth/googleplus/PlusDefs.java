package com.htc.lib1.cs.auth.googleplus;

import com.google.android.gms.common.Scopes;

/**
 * Created by leohsu on 2017/3/29.
 */

/**
 * Constant values for google plus.
 */
public class PlusDefs {

    /** Google account type. */
    public static final String TYPE_GOOGLE_ACCOUNT = "com.google";

    /** OAuth2 scopes prefix. */
    public static final String PREFIX_OAUTH2_SCOPES = "oauth2:";

    /** User email scope. */
    public static final String SCOPE_EMAIL = "email";

    /** OAuth2 format scopes for local authenticator version of identity client. */
    public static final String OAUTH2_SCOPES_AUTH = PREFIX_OAUTH2_SCOPES
            + Scopes.PROFILE + " "
            + SCOPE_EMAIL;
}
