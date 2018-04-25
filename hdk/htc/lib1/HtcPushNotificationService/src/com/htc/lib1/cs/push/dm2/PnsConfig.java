
package com.htc.lib1.cs.push.dm2;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.push.BuildConfig;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PushProvider;
import com.htc.lib1.cs.push.utils.SystemPropertiesProxy;

import android.content.Context;
import android.text.TextUtils;

/**
 * A value object to represent identity configs.
 * 
 * @author samael_wang@htc.com
 */
public class PnsConfig {
    /** PNS server base URI for GCM. */
    public String gcmBaseUri;

    /** PNS server base URI for Baidu Push. */
    public String baiduBaseUri;

    /** Preferred push provider. Could be {@code null}. */
    public String pushProvider;

    /** Distributed update period in minutes. */
    public Integer distributedUpdatePeriodInMinutes;

    /**
     * Get default (fallback) configurations.
     *
     * @param context Context to operate on.
     * @return Configurations
     */
    public static PnsConfig createDefault(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        PnsConfig config = new PnsConfig();
        config.gcmBaseUri = PnsInternalDefs.REGISTRATION_URI;
        config.baiduBaseUri = PnsInternalDefs.REGISTRATION_URI;
        config.distributedUpdatePeriodInMinutes = PnsInternalDefs.DISTRIBUTED_UPDATE_IN_MINUTES;

        return config;
    }

    /**
     * Create an {@link PnsConfig} from a given instance. For each field, if
     * {@code input} has a non-empty value, use it; If no, use the default value
     * from {@link #createDefault(Context)}.
     *
     * @param context Context to operate on.
     * @param input {@link PnsConfig} to create from.
     * @return {@link PnsConfig}
     */
    public static PnsConfig createWithDefaultFrom(Context context, PnsConfig input) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        PnsConfig config = new PnsConfig();

        config.gcmBaseUri = (input != null && !TextUtils.isEmpty(input.gcmBaseUri)) ? input.gcmBaseUri
                : PnsInternalDefs.REGISTRATION_URI;

        config.baiduBaseUri = (input != null && !TextUtils.isEmpty(input.baiduBaseUri)) ? input.baiduBaseUri
                : PnsInternalDefs.REGISTRATION_URI;

        config.distributedUpdatePeriodInMinutes = (input != null && input.distributedUpdatePeriodInMinutes != null) ? input.distributedUpdatePeriodInMinutes
                : PnsInternalDefs.DISTRIBUTED_UPDATE_IN_MINUTES;

        return config;
    }

    /**
     * Get configurations from system properties. Only works in debug mode.
     *
     * @param context Context to operate on.
     * @return Configurations
     */
    private static PnsConfig createFromSysProps(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        PnsConfig config = new PnsConfig();

        /*
         * Only fill values if in debug mode.
         */
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                || BuildConfig.DEBUG) {
            config.gcmBaseUri = SystemPropertiesProxy
                    .get(context, PnsInternalDefs.KEY_SYSTEM_PROP_GCM_BASE_URI);
            config.baiduBaseUri = SystemPropertiesProxy.get(context,
                    PnsInternalDefs.KEY_SYSTEM_PROP_BAIDU_BASE_URI);
            config.pushProvider = SystemPropertiesProxy.get(context,
                    PnsInternalDefs.KEY_SYSTEM_PROP_PUSH_PROVIDER);
            config.distributedUpdatePeriodInMinutes = SystemPropertiesProxy.getInt(context,
                    PnsInternalDefs.KEY_SYSTEM_PROP_DISTRIBUTED_UPDATE_PERIOD, 0);
        }

        return config;
    }

    /**
     * Create an instance with system properties. For each field, system
     * properties always take higher priority and only use {@code input} if no
     * system properties available.
     *
     * @param context Context to operate on.
     * @param input Input to use when system properties are not available.
     * @return Configurations
     */
    public static PnsConfig createWithSysPropsFrom(Context context, PnsConfig input) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        if (input == null)
            throw new IllegalArgumentException("'input' is null.");

        PnsConfig config = new PnsConfig();
        PnsConfig sysConfig = createFromSysProps(context);
        config.gcmBaseUri = TextUtils.isEmpty(sysConfig.gcmBaseUri) ? input.gcmBaseUri
                : sysConfig.gcmBaseUri;
        config.baiduBaseUri = TextUtils.isEmpty(sysConfig.baiduBaseUri) ? input.baiduBaseUri
                : sysConfig.baiduBaseUri;
        config.pushProvider = TextUtils.isEmpty(sysConfig.pushProvider) ? input.pushProvider
                : sysConfig.pushProvider;
        /*
         * Must check null pointer before check the value for Long unboxing
         * issue. See
         * http://stackoverflow.com/questions/12686718/nullpointerexception
         * -using-long-after-equality-check
         */
        config.distributedUpdatePeriodInMinutes = (sysConfig.distributedUpdatePeriodInMinutes == null || sysConfig.distributedUpdatePeriodInMinutes == 0) ?
                input.distributedUpdatePeriodInMinutes
                : sysConfig.distributedUpdatePeriodInMinutes;

        return config;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{gcmBaseUri=\"" + gcmBaseUri +
                "\", baiduBaseUri=\"" + baiduBaseUri +
                "\", pushProvider=\"" + pushProvider +
                "\", distributedUpdatePeriod=\"" + distributedUpdatePeriodInMinutes +
                "\"}";
    }

    /**
     * Check if the instance has valid values.
     *
     * @return {@code true} if the values are valid.
     */
    public boolean isValid() {
        return !TextUtils.isEmpty(gcmBaseUri) && !TextUtils.isEmpty(baiduBaseUri);
    }

    /**
     * Get the base URI corresponding to given provider.
     * 
     * @param provider Provider to look up with.
     * @return Base URI.
     */
    public String getBaseUri(PushProvider provider) {
        if (provider == PushProvider.GCM)
            return gcmBaseUri;
        else if (provider == PushProvider.BAIDU)
            return baiduBaseUri;
        else
            throw new IllegalStateException("Unrecongnized provider type: '" + provider.toString()
                    + "'");
    }

}
