
package com.htc.lib1.cs;

import java.util.UUID;

import android.Manifest;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Helper class to get serial number, IMEI and handset device ID.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 */
public class DeviceProfileHelper {
    private static DeviceProfileHelper sInstance;
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private TelephonyManager mTelManager;
    private HtcTelephonyManagerProxy mHtcTelManagerProxy;

    /**
     * Get the singleton instance of {@link DeviceProfileHelper}.
     * 
     * @param context Context used to retrieve application context.
     * @return {@link DeviceProfileHelper}
     */
    public static synchronized DeviceProfileHelper get(Context context) {
        if (context == null)
            throw new IllegalStateException("'context' is null.");

        if (sInstance == null)
            sInstance = new DeviceProfileHelper(context.getApplicationContext());

        return sInstance;
    }

    /**
     * Create an instance.
     * 
     * @param context Context to operate on.
     */
    public DeviceProfileHelper(Context context) {
        mContext = context;
        mTelManager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        mHtcTelManagerProxy = new HtcTelephonyManagerProxy();
    }

    /**
     * Get IMEI (or MEID for CDMA phones). Requires permission
     * {@link Manifest.permission#READ_PHONE_STATE}.
     * 
     * @return IMEI for GSM phones or MEID for CDMA phones.
     */
    public String getIMEI() {
        String imei = null;
        try {
            if (mHtcTelManagerProxy.dualPhoneEnable() ||
                    mHtcTelManagerProxy.dualGSMPhoneEnable()) {
                String imei1 =
                        mHtcTelManagerProxy.getDeviceIdExt(HtcTelephonyManagerProxy.PHONE_SLOT1);
                String imei2 =
                        mHtcTelManagerProxy.getDeviceIdExt(HtcTelephonyManagerProxy.PHONE_SLOT2);
                mLogger.debugS("Detected dual SIM phone: \nIMEI1 = ", imei1,
                        "\nIMEI2 = ", imei2);

                if (!TextUtils.isEmpty(imei1))
                    imei = imei1;
                else
                    imei = imei2;
            } else {
                mLogger.debug("Detected single SIM phone.");
            }
        } catch (Exception e) {
            mLogger.debug(e);

        }

        // Fallback
        if (TextUtils.isEmpty(imei))
            imei = mTelManager.getDeviceId();

        mLogger.debugS("IMEI: ", imei);
        return imei;
    }

    /**
     * Get IMEI (or MEID for CDMA phones). Requires permission
     * {@link Manifest.permission#READ_PHONE_STATE}.
     * 
     * @param def Default value to use if IMEI / MEID is not available.
     * @return IMEI for GSM phones or MEID for CDMA phones or {@code def} value
     *         if IMEI / MEID is not available.
     */
    public String getIMEI(String def) {
        String imei = getIMEI();
        if (TextUtils.isEmpty(imei))
            imei = def;
        return imei;
    }

    /**
     * Get the subscriber id (IMSI for GSM phones). Requires permission
     * {@link Manifest.permission#READ_PHONE_STATE}.
     * 
     * @return IMSI or functional equivalent subscriber id or {@code null} if
     *         not available.
     */
    public String getIMSI() {
        String imsi = null;
        try {
            if (mHtcTelManagerProxy.dualPhoneEnable() ||
                    mHtcTelManagerProxy.dualGSMPhoneEnable()) {
                String imsi1 = mHtcTelManagerProxy
                        .getSubscriberIdExt(HtcTelephonyManagerProxy.PHONE_SLOT1);
                String imsi2 = mHtcTelManagerProxy
                        .getSubscriberIdExt(HtcTelephonyManagerProxy.PHONE_SLOT2);
                mLogger.debugS("Detected dual SIM phone: \nIMSI1 = ", imsi1,
                        "\nIMSI2 = ", imsi2);

                if (!TextUtils.isEmpty(imsi1))
                    imsi = imsi1;
                else
                    imsi = imsi2;
            } else {
                mLogger.debug("Detected single SIM phone.");
            }
        } catch (Exception e) {
            mLogger.debug(e);

        }

        // Fallback
        if (TextUtils.isEmpty(imsi))
            imsi = mTelManager.getSubscriberId();

        mLogger.debugS("IMSI: ", imsi);
        return imsi;
    }

    /**
     * Get the subscriber id (IMSI for GSM phones). Requires permission
     * {@link Manifest.permission#READ_PHONE_STATE}.
     * 
     * @param def Default value to use if IMSI is not available.
     * @return IMSI or functional equivalent subscriber id or {@code def} if not
     *         available.
     */
    public String getIMSI(String def) {
        String imsi = getIMSI();
        if (TextUtils.isEmpty(imsi))
            imsi = def;
        return imsi;
    }

    /**
     * Get the WiFi MAC address of the device, if any. Requires permission
     * {@link Manifest.permission#ACCESS_WIFI_STATE}.
     * 
     * @return MAC address or {@code null} if not available.
     */
    public String getMacAddress() {
        WifiManager wifiMananger = (WifiManager) mContext.getSystemService(
                Context.WIFI_SERVICE);
        String macAddress = wifiMananger.getConnectionInfo().getMacAddress();
        mLogger.debugS("WiFi Mac Address: ", macAddress);
        return macAddress;
    }

    /**
     * Get the WiFi MAC address of the device, if any. Requires permission
     * {@link Manifest.permission#ACCESS_WIFI_STATE}.
     * 
     * @param def Default value to use if MAC address is not available.
     * @return MAC address or {@code def} if not available.
     */
    public String getMacAddress(String def) {
        String macAddress = getMacAddress();
        if (TextUtils.isEmpty(macAddress))
            macAddress = def;
        return macAddress;
    }

    /**
     * Generate a variant 2, version 3 based device UUID based on its serial
     * number, IMEI and Android ID.
     * 
     * @return Handset device ID.
     */
    public UUID getHandsetDeviceId() {
        String input = String.format("%s.%s.%s", Build.SERIAL, getIMEI(Build.UNKNOWN),
                Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID));
        UUID uuid = UUID.nameUUIDFromBytes(input.getBytes());
        mLogger.debugS("UUID: ", uuid);
        return uuid;
    }

    /**
     * Get HTC device model ID, if any.
     * 
     * @return Model id on HTC devices or an empty string if running on a
     *         non-HTC device or the value is not available for any reason.
     */
    public String getModelId() {
        String mid = SystemPropertiesProxy.get(mContext, "ro.mid");
        mLogger.debugS("Model ID: ", mid);
        return mid;
    }

    /**
     * Get HTC device model ID, if any.
     * 
     * @param def Default value to use if model id is not available.
     * @return Model id on HTC devices or {@code def} if running on a non-HTC
     *         device or the value is not available for any reason.
     */
    public String getModelId(String def) {
        String mid = getModelId();
        if (TextUtils.isEmpty(mid))
            mid = def;
        return mid;
    }

    /**
     * Get HTC device customer ID, if any.
     * 
     * @return Customer id on HTC devices or an empty string if running on a
     *         non-HTC device or the value is not available for any reason.
     */
    public String getCustomerId() {
        String cid = SystemPropertiesProxy.get(mContext, "ro.cid");
        mLogger.debugS("Customer ID: ", cid);
        return cid;
    }

    /**
     * Get HTC device customer ID, if any.
     * 
     * @param def Default value to use if customer id is not available.
     * @return Customer id on HTC devices or {@code def} if running on a non-HTC
     *         device or the value is not available for any reason.
     */
    public String getCustomerId(String def) {
        String cid = getCustomerId();
        if (TextUtils.isEmpty(cid))
            cid = def;
        return cid;
    }

    /**
     * Get HTC Sense version.
     * 
     * @return HTC Sense version on HTC devices or an empty string if running on
     *         a non-HTC device or the value is not available for any reason.
     */
    public String getSenseVersion() {
        String senseVersion = SystemPropertiesProxy.get(mContext, "ro.build.sense.version");
        mLogger.debugS("Sense version: ", senseVersion);
        return senseVersion;
    }

    /**
     * Get HTC Sense version.
     * 
     * @param def Default value to use if not able to detect sense version.
     * @return HTC Sense version on HTC devices or {@code def} if running on a
     *         non-HTC device or the value is not available for any reason.
     */
    public String getSenseVersion(String def) {
        String senseVersion = getSenseVersion();
        if (TextUtils.isEmpty(senseVersion))
            senseVersion = def;
        return senseVersion;
    }

    /**
     * Get ROM version string.
     * 
     * @return ROM version or an empty string if not available.
     */
    public String getRomVersion() {
        String version = SystemPropertiesProxy.get(mContext, "ro.build.description");
        mLogger.debugS("Full ROM version: ", version);
        if (!TextUtils.isEmpty(version)) {
            /*
             * Get first token of version string. For example
             * "0.1.0.0 (20150131 #480890) test-keys", we just need "0.1.0.0"
             */
            version = version.trim();
            int spaceIdx = version.indexOf(" ");
            version = (spaceIdx == -1) ? version : version.substring(0, spaceIdx);
        }
        return version;
    }

    /**
     * Get ROM version string.
     * 
     * @param def Default value to use if not able to detect ROM version.
     * @return ROM version or {@code def} if not available.
     */
    public String getRomVersion(String def) {
        String version = getRomVersion();
        if (TextUtils.isEmpty(version))
            version = def;
        return version;
    }

    /**
     * Get SIM card operator PLMN.
     * 
     * @return SIM card operator PLMN.
     */
    public String getSimOperator() {
        String plmn = null;
        try {
            if (mHtcTelManagerProxy.dualPhoneEnable() ||
                    mHtcTelManagerProxy.dualGSMPhoneEnable()) {
                String plmn1 = mHtcTelManagerProxy
                        .getIccOperator(HtcTelephonyManagerProxy.PHONE_SLOT1);
                String plmn2 = mHtcTelManagerProxy
                        .getIccOperator(HtcTelephonyManagerProxy.PHONE_SLOT2);
                mLogger.debugS("Detected dual SIM phone: \nPLMN1 = ", plmn1, "\nPLMN2 = ", plmn2);

                if (!TextUtils.isEmpty(plmn1))
                    plmn = plmn1;
                else
                    plmn = plmn2;
            } else {
                mLogger.debug("Detected single SIM phone.");
            }
        } catch (Exception e) {
            mLogger.debug(e);

        }

        // Fallback
        if (TextUtils.isEmpty(plmn))
            plmn = mTelManager.getSimOperator();

        mLogger.debugS("Operator PLMN: ", plmn);
        return plmn;
    }

    /**
     * Get network operator PLMN
     * 
     * @return Network PLMN.
     */
    public String getNetworkOperaotr() {
        String plmn = null;
        try {
            if (mHtcTelManagerProxy.dualPhoneEnable() ||
                    mHtcTelManagerProxy.dualGSMPhoneEnable()) {
                String plmn1 = mHtcTelManagerProxy
                        .getNetworkOperatorExt(HtcTelephonyManagerProxy.PHONE_SLOT1);
                String plmn2 = mHtcTelManagerProxy
                        .getNetworkOperatorExt(HtcTelephonyManagerProxy.PHONE_SLOT2);
                mLogger.debugS("Detected dual SIM phone: \nPLMN1 = ", plmn1, "\nPLMN2 = ", plmn2);

                if (!TextUtils.isEmpty(plmn1))
                    plmn = plmn1;
                else
                    plmn = plmn2;
            } else {
                mLogger.debug("Detected single SIM phone.");
            }
        } catch (Exception e) {
            mLogger.debug(e);

        }

        // Fallback
        if (TextUtils.isEmpty(plmn))
            plmn = mTelManager.getNetworkOperator();

        mLogger.debugS("Network PLMN: ", plmn);
        return plmn;
    }

    /**
     * Get SIM card country code.
     * 
     * @return SIM card country code.
     */
    public String getSimCardISO() {
        String iso = mTelManager.getSimCountryIso();
        mLogger.debugS("SIM country code: ", iso);
        return iso;
    }

}
