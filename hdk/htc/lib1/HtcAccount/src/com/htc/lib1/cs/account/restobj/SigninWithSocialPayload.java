
package com.htc.lib1.cs.account.restobj;

import android.accounts.AbstractAccountAuthenticator;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

import java.util.List;
import java.util.UUID;

public class SigninWithSocialPayload extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * Account detail of this account operation.
     */
    @SerializedName("account")
    public AccountDetail account;

    public static class AccountDetail extends RestObject {
        private static final long serialVersionUID = 1L;

        /**
         * The language code for user's location.
         */
        @SerializedName("languageCode")
        public String languageCode;

        /**
         * The region id of user's location.
         */
        @SerializedName("regionId")
        public UUID regionId;

        /**
         * User's second email.
         */
        @SerializedName("secondEmail")
        public String secondEmail;

        /**
         * Is this account check mail me about products.
         */
        @SerializedName("sendEmailAboutProducts")
        public boolean sendEmailAboutProducts;

        @Override
        public boolean isValid() {
            return (regionId != null);
        }
    }

    /**
     * The client id of HTCAccount's {@link AbstractAccountAuthenticator}
     */
    @SerializedName("clientId")
    public String clientId;

    /**
     * Indicate that user confirm to create account or not.
     */
    @SerializedName("confirm")
    public Boolean confirm;

    /**
     * Dry run or not. Note: Unused on client side.
     */
    @SerializedName("dryRun")
    public Boolean dryRun;

    /**
     * The access token of social account.
     */
    @SerializedName("socialAccessToken")
    public String socialAccessToken;

    /**
     * The request scopes.
     */
    @SerializedName("scopes")
    public List<String> scopes;

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(clientId)
                && !TextUtils.isEmpty(socialAccessToken)
                && (scopes != null);
    }

}
