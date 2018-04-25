
package com.htc.lib1.cs.auth.web;

/**
 * Constants related to the tokens stored to account manager.
 * 
 * @author samael_wang@htc.com
 */
public class TokenDefs {

    /** The authtoken type used to store the refresh token */
    public static final String TYPE_REFRESH_TOKEN = "refreshToken";

    /** The authtoken type used to store the access token */
    public static final String TYPE_ACCESS_TOKEN = "accessToken";

    /** The authtoken type used to store the issue-token token of a local account */
    public static final String TYPE_LOCAL_ACCOUNT_TOKEN = "accessToken";
}
