
package com.htc.lib1.cs;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * {@link AbstractIntentServiceBroadcastReceiver} uses an {@link IntentService}
 * to handle the broadcast intent, and hence it avoids ANRs in
 * {@link #onReceive(Context, Intent)}.
 */
public abstract class AbstractIntentServiceBroadcastReceiver extends BroadcastReceiver {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private static final String KEY_BROADCAST_INTENT = "broadcastIntent";

    /**
     * Interface for handling intent actions.
     */
    public interface BroadcastHandler {
        /**
         * Handle the event on given context with given action.
         * 
         * @param context Context to operate on.
         * @param action Action of the broadcast intent.
         * @param data Data of the broadcast intent.
         * @param extras Extras of the broadcast intent.
         */
        void handle(Context context, String action, Uri data, Bundle extras);
    }

    @Override
    public final void onReceive(final Context context, final Intent intent) {
        mLogger.verboseS(intent);

        // Start service to handle the broadcast intent.
        Intent serviceIntent = new Intent(context, getServiceClass());
        serviceIntent.putExtra(KEY_BROADCAST_INTENT, intent);
        if (context.startService(serviceIntent) == null) {
            throw new IllegalStateException(
                    "Unable to start service "
                            + serviceIntent.toString()
                            + ". Have you forgot to declared it in AndroidManifest.xml, "
                            + "or set 'manifestmerger.enabled=true' "
                            + "in your project.properties?");
        }

        /*
         * Set success result if it's an ordered broadcast. Surely it indicates
         * no error result can be returned to the caller but since the broadcast
         * handling happens in an intent service and the receiver could finish
         * before the service finishes, the execution result can not be returned
         * through the receiver anyway.
         */
        if (isOrderedBroadcast())
            setResultCode(Activity.RESULT_OK);
    }

    /**
     * Get the service class to handle broadcast.
     * 
     * @return Class extending {@link HandleBroadcastService}. Must not be
     *         {@code null}.
     */
    protected abstract Class<? extends HandleBroadcastService> getServiceClass();

    /**
     * {@link IntentService} to handle the broadcast intent.
     * 
     * @author samael_wang@htc.com
     */
    public static class HandleBroadcastService extends IntentService {
        protected HtcLogger mmLogger;

        public HandleBroadcastService(String name) {
            super(name);
            mmLogger = new CommLoggerFactory(name).create();
        }

        @Override
        protected final void onHandleIntent(Intent intent) {
            Intent broadcastIntent = intent.getParcelableExtra(KEY_BROADCAST_INTENT);
            if (broadcastIntent == null)
                throw new IllegalStateException("'" + KEY_BROADCAST_INTENT + "' is null.");

            mmLogger.verboseS(broadcastIntent);
            handleBroadcast(this, broadcastIntent);
        }

        /**
         * Get the list of {@link BroadcastHandler} to handle the broadcast. The
         * method is expected to be invoked once on each
         * {@link #onReceive(Context, Intent)} call happens unless subclass
         * overrides {@link #handleBroadcast(Context, Intent)}. The default
         * implementation returns {@code null}.
         * 
         * @return An array of {@link BroadcastHandler} or {@code null}.
         */
        protected BroadcastHandler[] getHandlers() {
            return null;
        }

        /**
         * Handle the broadcast intent in the background thread of
         * {@link IntentService}. The default implementation runs all handlers
         * retrieved from {@link #getHandlers()}. Subclasses can optionally
         * override this behavior.
         * 
         * @param context Context to operate on.
         * @param intent The broadcast intent.
         */
        protected void handleBroadcast(final Context context, final Intent intent) {
            BroadcastHandler[] handlers = getHandlers();
            if (handlers != null) {
                for (BroadcastHandler handler : handlers) {
                    handler.handle(context,
                            intent.getAction(), intent.getData(), intent.getExtras());
                }
            } else {
                mmLogger.error("No handlers were given to handle the intent.");
            }
        }

    }
}
