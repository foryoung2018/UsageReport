
package com.htc.lib1.cs.push;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * @author samael_wang
 */
public class UpdateRegisterPayload extends BaseRegistrationPayload {
    private static final long serialVersionUID = 1L;

    /**
     * The secret key generated by server during client registration.
     */
    @SerializedName("reg_key")
    public String regKey;

    /**
     * Encrypt messages via Baidu push
     */
    @SerializedName("need_encryption")
    public boolean need_encryption;

    @Override
    public boolean isValid() {
        return super.isValid() && !TextUtils.isEmpty(regKey);
    }
}
