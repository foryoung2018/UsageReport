
package com.htc.lib1.cs.account.restobj;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

import java.util.List;

public class ConfirmAccountInfo extends RestObject {
    private static final long serialVersionUID = 1L;

    public static class AccountInfo extends RestObject {
        private static final long serialVersionUID = 1L;

        /**
         * Account name/email.
         */
        @SerializedName("email")
        public String email;

        /**
         * Account type/provider.
         */
        @SerializedName("provider")
        public String provider;

        @Override
        public boolean isValid() {
            return !TextUtils.isEmpty(provider);
        }
    }

    /**
     * The account needed to be confirmed.
     */
    @SerializedName("account")
    public AccountInfo account;

    /**
     * The accounts which the user already have.
     */
    @SerializedName("associatedAccount")
    public List<AccountInfo> associatedAccount;

    /**
     * The captcha nonce for avoiding re-request user to input the captcha.
     */
    @SerializedName("captchaNonce")
    public String captchaNonce;

    @Override
    public boolean isValid() {
        return (account != null);
    }

}
