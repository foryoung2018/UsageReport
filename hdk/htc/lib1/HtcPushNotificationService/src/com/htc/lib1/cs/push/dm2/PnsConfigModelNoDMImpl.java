
package com.htc.lib1.cs.push.dm2;

import java.util.concurrent.TimeUnit;

import android.content.Context;

/**
 * The underlying data binding model of PNS configs which doesn't use dm at all.
 * 
 * @author samael_wang@htc.com
 */
public class PnsConfigModelNoDMImpl extends AbstractPnsConfigModel {
    private static PnsConfigModelNoDMImpl sInstance;

    /**
     * Get the instance of {@link PnsConfigModelNoDMImpl}.
     * 
     * @param context Context used to retrieve application context.
     * @return {@link PnsConfigModelNoDMImpl}
     */
    public static synchronized PnsConfigModelNoDMImpl get(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        if (sInstance == null)
            sInstance = new PnsConfigModelNoDMImpl(context.getApplicationContext());

        return sInstance;
    }

    /**
     * Reset the instance;
     */
    public static void reset() {
        sInstance = null;
    }

    // Private constructor.
    private PnsConfigModelNoDMImpl(Context context) {
        super(context);
    }

    @Override
    public PnsConfig getConfig(long timeout, TimeUnit unit) {
        if (mBaseConfig.get() == null) {
            mBaseConfig.set(PnsConfig.createWithSysPropsFrom(mContext,
                    PnsConfig.createDefault(mContext)));
        }

        // Reply with system properties overridings.
        return PnsConfig.createWithSysPropsFrom(mContext, mBaseConfig.get());
    }

}
