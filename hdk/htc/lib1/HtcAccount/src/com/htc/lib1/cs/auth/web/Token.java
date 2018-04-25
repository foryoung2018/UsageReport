
package com.htc.lib1.cs.auth.web;

import android.text.TextUtils;

import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * The class of access token and refresh token.
 * 
 * @author samael_wang@htc.com
 */
/* package */class Token {
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private String mAccessToken;
    private String mRefreshToken;

    public Token(OAuth2TokenResponse response) {
        if (response == null)
            throw new IllegalArgumentException("'response' is null.");
        if (TextUtils.isEmpty(response.accessToken))
            throw new IllegalArgumentException("'access_token' is null or empty.");

        mAccessToken = response.accessToken;
        if (!TextUtils.isEmpty(response.refreshToken)) {
            mRefreshToken = response.refreshToken;
        } else {
            mLogger.warning("'refresh_token' is null or empty. User will be asked to re-login after access token expires.");
            mRefreshToken = null;
        }
    }

    /**
     * Get the access token.
     * 
     * @return The access token.
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * Get the refresh token.
     * 
     * @return The refresh token or {@code null} if not available for some
     *         reasons.
     */
    public String getRefreshToken() {
        return mRefreshToken;
    }

    @Override
    public String toString() {
        return new StringBuilder("Token{ ").append("accessToken=").append(mAccessToken)
                .append(", refreshToken=").append(mRefreshToken).append(" }").toString();
    }
}
