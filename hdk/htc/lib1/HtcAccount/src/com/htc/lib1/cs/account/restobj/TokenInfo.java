
package com.htc.lib1.cs.account.restobj;

import java.util.List;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

/**
 * represents the information of an access token of HTC Account.
 * 
 * @author samael_wang
 */
public class TokenInfo extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * Global Unique ID of the account.
     */
    @SerializedName("account_id")
    public String accountId;

    /**
     * The OAuth client id of the client that requested the token.
     */
    @SerializedName("client_id")
    public String clientId;

    /**
     * Token expiration time in seconds.
     */
    @SerializedName("expires_in")
    public long expiresIn;

    /**
     * List of scopes granted to the token.
     */
    @SerializedName("scope")
    public List<String> scopes;

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(clientId);
    }
}
