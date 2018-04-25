
package com.htc.lib1.cs.auth.web;

/**
 * Constants related to webview authorization.
 * 
 * @author samael_wang@htc.com
 */
/* package */class WebAuthDefs {
    /**
     * Query string parameter name of grant type.
     */
    public static final String PARAM_GRANT_TYPE = "grant_type";

    /**
     * Grant type for the sign-in flow.
     */
    public static final String GRANT_TYPE_AUTH_CODE = "authorization_code";

    /**
     * Grant type for the refresh token flow.
     */
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    /**
     * Query string parameter name of authorization code.
     */
    public static final String PARAM_AUTH_CODE = "code";

    /**
     * Query string parameter name of client id.
     */
    public static final String PARAM_CLIENT_ID = "client_id";

    /**
     * Query string parameter name of response type.
     */
    public static final String PARAM_RESPONSE_TYPE = "response_type";

    /**
     * Response type for authorization code.
     */
    public static final String RESPONSE_TYPE_CODE = "code";

    /**
     * Query string parameter name of client secret.
     */
    public static final String PARAM_CLIENT_SECRET = "client_secret";

    /**
     * Query string parameter name of scopes.
     */
    public static final String PARAM_SCOPES = "scope";

    /**
     * Query string parameter name of redirect uri.
     */
    public static final String PARAM_REDIRECT_URL = "redirection_url";

    /**
     * Query string parameter name of refresh token.
     */
    public static final String PARAM_REFRESH_TOKEN = "refresh_token";
}
