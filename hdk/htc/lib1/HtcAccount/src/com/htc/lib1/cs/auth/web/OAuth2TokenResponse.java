
package com.htc.lib1.cs.auth.web;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

/**
 * Value object mapping of the response of {@code /$WS$/Services/OAuth/Token}
 * API.
 * 
 * @author samael_wang@htc.com
 */
public class OAuth2TokenResponse extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * OAuth 2.0 access token of HTC Account.
     */
    @SerializedName("access_token")
    public String accessToken;

    /**
     * OAuth 2.0 token types defined in RFC6749. It should be "Bearer" here.
     */
    @SerializedName("token_type")
    public String tokenType;

    /**
     * Token expiration time in seconds.
     */
    @SerializedName("expires_in")
    public long expiresIn;

    /**
     * OAuth 2.0 refresh token. It's consider mandatory but we tolerant the
     * temporary error if server didn't return refresh token.
     */
    @SerializedName("refresh_token")
    public String refreshToken;

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(tokenType);
    }

}
