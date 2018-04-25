
package com.htc.lib1.cs.push.dm2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.os.Looper;

import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.PnsInternalDefs;
import com.htc.lib1.cs.push.PushLoggerFactory;

/**
 * The abstract class of different implementations of pns config model.
 * 
 * @author samael_wang@htc.com
 */
public abstract class AbstractPnsConfigModel {
   protected HtcLogger mLogger = new PushLoggerFactory(this).create();
    protected AtomicReference<PnsConfig> mBaseConfig = new AtomicReference<>();
    protected Context mContext;
    
    protected AbstractPnsConfigModel(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        mContext = context;
    }

    /**
     * Check if the config is available.
     * 
     * @return True if it is.
     */
    public boolean isAvailable() {
        return mBaseConfig.get() != null;
    }

    /**
     * Block waiting for the config until retrieved or timeout.
     * 
     * @param timeout Timeout to wait. If passing 0 it essentially avoids block
     *            waiting but return whatever exists immediately.
     * @param unit Unit of {@code timeout}. Must not be {@code null}.
     * @return Non-{@code null} {@link PnsConfig}
     */
    public abstract PnsConfig getConfig(long timeout, TimeUnit unit);

    /**
     * Overloaded method of {@link #getConfig(long, TimeUnit)} which returns
     * immediately if running on main thread or block waiting for result
     * otherwise.
     * 
     * @return Non-{@code null} {@link PnsConfig}
     */
    public PnsConfig getConfig() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return getConfig(0, TimeUnit.MILLISECONDS);
        } else {
            return getConfig(PnsInternalDefs.DM_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }
    }
}
