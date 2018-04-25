
package com.htc.lib1.cs.app;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Simple service which sends broadcasts through {@link LocalBroadcastManager}
 * on main process. The broadcast intent to send should be put with the key
 * {@link #KEY_BROADCAST_INTENT} in the launch {@link Intent}.
 * 
 * @author samael_wang@htc.com
 */
public class LocalBroadcastService extends IntentService {
    public static final String KEY_BROADCAST_INTENT = LocalBroadcastService.class.getName()
            + ".BroadcastIntent";
    private HtcLogger mLogger = new CommLoggerFactory(this).create();

    public LocalBroadcastService() {
        super(LocalBroadcastService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent broadcast = intent.getParcelableExtra(KEY_BROADCAST_INTENT);
        mLogger.verboseS(broadcast);
        mLogger.verbose("broadcast delivered: ",
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast));
    }

}
