
package com.htc.lib1.cs.httpclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;

import com.htc.lib1.cs.DeviceProfileHelper;
import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.VersionHelper;
import com.htc.lib1.cs.googleads.GoogleAdvertiseUtils;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper class to build request properties for HTC REST services. It builds the
 * common header used by HTC services and generate meaningful user agent.
 * 
 * @author samael_wang@htc.com
 */
public class HtcRestRequestPropertiesBuilder {
    private Context mContext;
    private Map<String, String> mRequestProperties;

    public HtcRestRequestPropertiesBuilder(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        mContext = context;

        /*
         * Put common request properties shared among HTC REST services. It can
         * be override later if the caller wants.
         */
        mRequestProperties = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        mRequestProperties.put("Accept", "application/json");
        mRequestProperties.put("Content-Type", "application/json;charset=utf-8");
        mRequestProperties.put("X-Request", "JSON");
        mRequestProperties.put("X-HTC-DEVICE-SN", Build.SERIAL);
        mRequestProperties.put("X-HTC-MODEL", Build.MODEL);
        mRequestProperties.put("X-HTC-Operator-PLMN",
                DeviceProfileHelper.get(mContext).getSimOperator());
        mRequestProperties.put("X-HTC-Network-PLMN",
                DeviceProfileHelper.get(mContext).getNetworkOperaotr());
        mRequestProperties.put("X-HTC-App-Version", getAppVersionString());
        mRequestProperties.put("X-HTC-GA-ID", GoogleAdvertiseUtils.getAdvertisingId(mContext));

        /*
         * Get the trimmed English string of application label, and setup
         * user-agent if the application label is not empty.
         */
        String appLabel = getAppLabel();
        if (!TextUtils.isEmpty(appLabel))
            mRequestProperties.put("User-Agent", getUserAgentString(appLabel));
    }

    /**
     * Add a request property. It overrides existing value, if any.
     * 
     * @param key Key of the property. Must not be {@code null}.
     * @param value Value of the property.
     * @return {@link HtcRestRequestPropertiesBuilder}
     */
    public HtcRestRequestPropertiesBuilder addRequestProperty(String key, String value) {
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("'key' is null or empty.");
        mRequestProperties.put(key, value);

        return this;
    }

    /**
     * Set the authkey.
     * 
     * @param authkey Identity service authkey. An empty or {@code null} string
     *            removes the authkey from request properties.
     * @return {@link HtcRestRequestPropertiesBuilder}
     */
    public HtcRestRequestPropertiesBuilder setAuthKey(String authkey) {
        if (TextUtils.isEmpty(authkey))
            mRequestProperties.remove("AuthKey");
        else
            mRequestProperties.put("AuthKey", authkey);
        return this;
    }

    /**
     * Build request properties.
     * 
     * @return {@link Map<String, String>}
     */
    public Map<String, String> build() {
        return mRequestProperties;
    }

    /**
     * Get the trimmed English string of application label.
     * 
     * @return App level or {@code null} if not available.
     */
    @SuppressLint("NewApi")
    private String getAppLabel() {
        Configuration engConf = new Configuration(mContext.getResources().getConfiguration());
        engConf.locale = Locale.ENGLISH;

        int appLabelRes = mContext.getApplicationInfo().labelRes;
        String appLabel = null;

        /*
         * Prefer to use English name if createConfigurationContext is available
         * (i.e. api17+), otherwise use localized string.
         */
        if (appLabelRes != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                appLabel = mContext.createConfigurationContext(engConf).getString(appLabelRes)
                        .replaceAll("\\s+", "");
            } else {
                appLabel = mContext.getString(appLabelRes).replaceAll("\\s+", "");
            }
        }

        return appLabel;
    }

    /**
     * Generate app version string.
     * 
     * @return App version string.
     */
    private String getAppVersionString() {
        return mContext.getPackageName() + ";" + VersionHelper.get(mContext).getVersionName()
                + ";" + VersionHelper.get(mContext).getVersionCode();
    }

    /**
     * Compose user agent.
     * 
     * @param appname Trimmed application name without spaces inside.
     * @return User-agent string.
     */
    private String getUserAgentString(String appname) {
        String sense = DeviceProfileHelper.get(mContext).getSenseVersion();
        String rom = DeviceProfileHelper.get(mContext).getRomVersion();

        StringBuilder builder = new StringBuilder(appname)
                .append("/")
                .append(VersionHelper.get(mContext).getVersionName())
                .append(" (Linux; U; Android ")
                .append(Build.VERSION.RELEASE);

        builder.append("; ")
                .append(StringUtils.getIETFLangaugeTag(Locale.getDefault()))
                .append("; ")
                .append(Build.MODEL)
                .append(" Build/")
                .append(Build.ID)
                .append(")");

        // Append ROM version if available.
        builder.append(" ROM");
        if (!TextUtils.isEmpty(rom))
            builder.append("/").append(rom);

        // Append Manufacturer and serial number.
        builder.append(" (")
                .append(Build.MANUFACTURER)
                .append("; ")
                .append(Build.SERIAL)
                .append(")");

        // Append Sense version if available.
        if (!TextUtils.isEmpty(sense))
            builder.append(" Sense/").append(sense);

        return builder.toString();
    }
}
