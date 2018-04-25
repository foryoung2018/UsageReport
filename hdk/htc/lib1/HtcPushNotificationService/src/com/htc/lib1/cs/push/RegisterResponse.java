
package com.htc.lib1.cs.push;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

/**
 * Response of PNS registration.
 * 
 * @author samael_wang
 */
public class RegisterResponse extends RestObject {
    private static final long serialVersionUID = 1L;

    @SerializedName("reg_id")
    public String regId;

    @SerializedName("reg_key")
    public String regKey;

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(regId) && !TextUtils.isEmpty(regKey);
    }
}
