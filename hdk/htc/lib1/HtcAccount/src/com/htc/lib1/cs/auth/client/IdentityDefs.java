
package com.htc.lib1.cs.auth.client;

import com.htc.lib1.cs.account.HtcAccountDefs;

import java.util.Arrays;
import java.util.List;

/**
 * Constant values of identity client related information in addition to common
 * constants defined in {@link HtcAccountDefs}.
 */
public class IdentityDefs {

    /** The http timeout in seconds used in all identity client REST requests. */
    public static final long HTTP_TIMEOUT_SECONDS = 30;

    /****************************************************************************************
     * ConfirmCredentials options.
     ****************************************************************************************/

    /****************************************************************************************
     * OAuth 2 options.
     ****************************************************************************************/

    /** The issue token scopes */
    public static final String OAUTH_SCOPE_ISSUETOKEN = "issuetoken";

    /** The email scopes */
    public static final String OAUTH_SCOPE_EMAIL = "email";

    /** The scopes identity needed */
    public static final String OAUTH_IDENTITY_SCOPE = OAUTH_SCOPE_ISSUETOKEN + " "
            + OAUTH_SCOPE_EMAIL;

    /** The scopes identity needed */
    public static final List<String> OAUTH_IDENTITY_SCOPE_LIST = Arrays.asList(
            OAUTH_SCOPE_ISSUETOKEN, OAUTH_SCOPE_EMAIL);

    /**
     * Query string parameter name of grant type.
     */
    public static final String PARAM_GRANT_TYPE = "grant_type";

    /**
     * Query string parameter name of response type.
     */
    public static final String PARAM_RESPONSE_TYPE = "response_type";

    /**
     * Query string parameter name of client id.
     */
    public static final String PARAM_CLIENT_ID = "client_id";

    /**
     * Query string parameter name of client id.
     */
    public static final String PARAM_APP_ID = "app_id";

    /**
     * Query string parameter name of client id.
     */
    public static final String PARAM_SIGN_KEY = "sign_key";

    /**
     * Query string parameter name of scopes.
     */
    public static final String PARAM_SCOPES = "scope";

    /**
     * Query string parameter name of refresh token.
     */
    public static final String PARAM_REFRESH_TOKEN = "refresh_token";

    /** The signature grant type */
    public static final String OAUTH_GRANT_TYPE_SIGNKEY = "android_signkey";

    /** The token response type */
    public static final String OAUTH_RESPONSE_TYPE_TOKEN = "token";

    /** The refresh token grant type */
    public static final String OAUTH_GRANT_TYPE_REFRESHTOKEN = "refresh_token";

}
