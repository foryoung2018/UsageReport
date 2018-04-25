
package com.htc.lib1.cs.account.restobj;

import android.accounts.AbstractAccountAuthenticator;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

import java.util.List;
import java.util.UUID;

/**
 * Account adding response.
 * 
 * @author autosun_li@htc.com
 */
public class AuthenticationToken extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * The device access token for HTCAccount's
     * {@link AbstractAccountAuthenticator}.
     */
    @SerializedName("accessToken")
    public String accessToken;

    /**
     * The account detail.
     */
    @SerializedName("account")
    public AccountDetail account;

    public static class AccountDetail extends RestObject {
        private static final long serialVersionUID = 1L;

        /**
         * The account UUID.
         */
        @SerializedName("accountId")
        public UUID accountId;

        /**
         * The country code this account created.
         */
        @SerializedName("countryCode")
        public String countryCode;

        /**
         * The email of this account verified or not.
         */
        @SerializedName("isVerified")
        public boolean isVerified;

        /**
         * The docs of legal. Note: Unused on client side.
         */
        @SerializedName("legalDocsToSign")
        public Boolean legalDocsToSign;

        /**
         * The account type/provider.
         */
        @SerializedName("provider")
        public String provider;

        @Override
        public boolean isValid() {
            return (accountId != null);
        }
    }

    /**
     * The access token's expiration time.
     */
    @SerializedName("expiresIn")
    public Integer expiresIn;

    /**
     * The refresh token for access token.
     */
    @SerializedName("refreshToken")
    public String refreshToken;

    /**
     * The request scopes.
     */
    @SerializedName("scopes")
    public List<String> scopes;

    /**
     * The URI of service.
     */
    @SerializedName("serviceUri")
    public String serviceUri;

    /**
     * The URI of web service.
     */
    @SerializedName("webServiceUri")
    public String webServiceUri;

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(accessToken)
                && (account != null)
                && account.isValid()
                && (expiresIn != null)
                && !TextUtils.isEmpty(serviceUri)
                && !TextUtils.isEmpty(webServiceUri);
    }
}
