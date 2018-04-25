
package com.htc.lib1.cs.auth.web;

import android.content.Context;
import android.text.TextUtils;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.StringUtils;
import com.htc.lib1.cs.SystemPropertiesProxy;
import com.htc.lib1.cs.account.OAuth2ConfigHelper;
import com.htc.lib1.cs.account.OAuth2ConfigHelper.AuthClient;
import com.htc.lib1.cs.auth.BuildConfig;

/**
 * Web auth configs.
 * 
 * @author samael_wang@htc.com
 */
public class WebAuthConfig {
    /** Fallback URI to use. */
    private static final String DEFAULT_BASE_URI = "https://www.htcsense.com/$SS$/";

    /**
     * Key of system properties to override the URI. The key must not exceed 31
     * characters.
     */
    private static final String KEY_BASE_URI = "debug.myhtc.uri.webauth";

    private static WebAuthConfig sInstance;
    private Context mContext;
    private AuthClient mClient;

    /**
     * Get the singleton instance.
     * 
     * @param context Context used to retrieve application context.
     * @return {@link WebAuthConfig}
     */
    public static synchronized WebAuthConfig get(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        if (sInstance == null)
            sInstance = new WebAuthConfig(context.getApplicationContext());

        return sInstance;
    }

    // Private constructor.
    private WebAuthConfig(Context context) {
        mContext = context;
        mClient = new OAuth2ConfigHelper(context).getDefaultAuthClient();
    }

    @SuppressWarnings("unused")
    public String getBaseUri() {
        if (BuildConfig.DEBUG || HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag) {
            String overrideUri = SystemPropertiesProxy.get(mContext, KEY_BASE_URI);
            if (!TextUtils.isEmpty(overrideUri))
                return StringUtils.ensureTrailingSlash(overrideUri);
        }
        
        return StringUtils.ensureTrailingSlash(DEFAULT_BASE_URI);
    }

    /**
     * Get the identity OAuth 2.0 client.
     * 
     * @return {@link AuthClient}
     */
    public AuthClient getAuthClient() {
        return mClient;
    }

}
