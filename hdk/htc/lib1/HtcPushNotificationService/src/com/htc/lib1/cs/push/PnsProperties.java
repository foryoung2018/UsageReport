
package com.htc.lib1.cs.push;

import android.content.Context;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.utils.SystemPropertiesProxy;

/**
 * Properties for PNS.
 * 
 * @author samael_wang@htc.com
 */
public class PnsProperties {
    private static PnsProperties mInstance;
    private HtcLogger mLogger = new PushLoggerFactory(this).create();
    private Context mContext;

    /**
     * Get the singleton instance of {@link PnsProperties}.
     * 
     * @return {@link PnsProperties} instance.
     */
    public synchronized static PnsProperties get(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        if (mInstance == null)
            mInstance = new PnsProperties(context);

        return mInstance;
    }

    /**
     * Construct an instance.
     */
    private PnsProperties(Context context) {
        mContext = context;
    }

    /**
     * Get registration base URI.
     * 
     * @param provider
     * @return Base URI or {@code null} if not available.
     */
    public String getBaseUri(PushProvider provider) {
        if (PushProvider.BAIDU == provider)
            return getStringSystemProperty(PnsInternalDefs.KEY_SYSTEM_PROP_BAIDU_BASE_URI);
        return getStringSystemProperty(PnsInternalDefs.KEY_SYSTEM_PROP_GCM_BASE_URI);
    }

    /**
     * Get update period in milliseconds.
     * 
     * @return Update period or {@code null} if not available.
     */
    public Long getDistributedUpdatePeriod() {
        return getLongSystemProperty(PnsInternalDefs.KEY_SYSTEM_PROP_DISTRIBUTED_UPDATE_PERIOD);
    }

    /**
     * Get the name of preferred push provider to use.
     * 
     * @return Push provider name or {@code null} if not available.
     */
    public String getPreferredPushProvider() {
        return getStringSystemProperty(PnsInternalDefs.KEY_SYSTEM_PROP_PUSH_PROVIDER);
    }

    /**
     * Get a string type system property.
     * 
     * @param key Name of the property.
     * @return Value of the property or {@code null} / empty string if value not
     *         set or not running in debug mode.
     */
    @SuppressWarnings("unused")
    private String getStringSystemProperty(String key) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                || BuildConfig.DEBUG) {
            String value = SystemPropertiesProxy.get(mContext, key);
            mLogger.debugS(key, ": ", value);
            return value;
        }
        return null;
    }

    /**
     * Get a long type system property.
     * 
     * @param key Name of the property.
     * @return Value of the property or {@code null} if value not set or not
     *         running debug mode.
     */
    @SuppressWarnings("unused")
    private Long getLongSystemProperty(String key) {
        if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                || BuildConfig.DEBUG) {
            try {
                long value = Long.parseLong(SystemPropertiesProxy.get(mContext, key));
                mLogger.debugS(key, ": ", value);
                return value;
            } catch (NumberFormatException e) {
                mLogger.info("Not able to parse '", key, "'");
                return null;
            }
        }
        return null;
    }
}
