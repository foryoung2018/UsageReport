
package com.htc.lib1.cs.push;

import java.util.List;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.htc.lib1.cs.RestObject;

/**
 * The base class of {@link RegisterPayload} and {@link UpdateRegisterPayload}.
 * 
 * @author samael_wang
 */
public class BaseRegistrationPayload extends RestObject {
    private static final long serialVersionUID = 1L;

    /**
     * Protocol version of PNS.
     */
    @SerializedName("protocol_version")
    public int protocolVersion;

    /**
     * Optional OS type.
     */
    @SerializedName("os")
    public String os;

    /**
     * Optional OS version.
     */
    @SerializedName("os_version")
    public String osVersion;

    /**
     * Android version for backward compatibility.
     */
    @SerializedName("android_version")
    public String androidVersion;

    /**
     * The identifier to distinguish different clients on the same device. For
     * android the value should be the package name.
     */
    @SerializedName("client_id")
    public String clientId;

    /**
     * The version of the client. For android the value should be
     * {@code versionName}.
     */
    @SerializedName("client_version")
    public String clientVersion;

    /**
     * Push provider to use.
     */
    @SerializedName("push_provider")
    public String pushProvider;

    /**
     * Optional HTC Sense version.
     */
    @SerializedName("sense_version")
    public String senseVersion;

    /**
     * Optional HTC Sense version.
     */
    @SerializedName("china_sense_version")
    public String chinaSenseVersion;
    
    
    /**
     * Optional Product name (Unrecognized field)
     */
    @SerializedName("product")
    public String product;
    
    /**
     * Optional ROM version.
     */
    @SerializedName("rom_version")
    public String romVersion;

    /**
     * Optional ROM version.
     */
    @SerializedName("sim_mccmnc")
    public List<String> sim_mccmnc;
    
    /**
     * GCM sender id. Mandatory if {@link #pushProvider} is GCM.
     */
    @SerializedName("gcm_sender_id")
    public String gcmSenderId;

    /**
     * GCM regId. Mandatory if {@link #pushProvider} is GCM.
     */
    @SerializedName("gcm_reg_id")
    public String gcmRegId;

    /**
     * Baidu app key. Mandatory if {@link #pushProvider} is Baidu.
     */
    @SerializedName("baidu_app_key")
    public String baiduAppKey;

    /**
     * Baidu channel id. Mandatory if {@link #pushProvider} is Baidu.
     */
    @SerializedName("baidu_channel_id")
    public String baiduChannelId;

    /**
     * Baidu user id. Mandatory if {@link #pushProvider} is Baidu.
     */
    @SerializedName("baidu_user_id")
    public String baiduUserId;

    @Override
    public boolean isValid() {
        return protocolVersion != 0
                && !TextUtils.isEmpty(clientId) && !TextUtils.isEmpty(clientVersion)
                && !TextUtils.isEmpty(os) && !TextUtils.isEmpty(osVersion)
                && !TextUtils.isEmpty(pushProvider)
                && PushProvider.GCM.equals(pushProvider) ?
                (!TextUtils.isEmpty(gcmRegId) && !TextUtils.isEmpty(gcmSenderId))
                : (!TextUtils.isEmpty(baiduAppKey) && !TextUtils.isEmpty(baiduChannelId)
                && !TextUtils.isEmpty(baiduUserId));
    }
}
