
package com.htc.lib1.cs.pns;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * The client connection to push notification service agent. The agent might be
 * a stand alone client or library implementation, depending on the concrete
 * class implementation.
 * 
 * @author samael_wang@htc.com
 */
public class PnsAgentConnection implements ServiceConnection {
    /** Action to bind system pns agent. */
    protected static final String ACTION_PNS_AGENT_INTENT = "com.htc.cs.PnsAgent";
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Context mContext;
    private boolean mServiceConnected;
    private IPnsAgent mAgent;

    /**
     * Construct an instance.
     * 
     * @param context Context to operate on.
     */
    public PnsAgentConnection(Context context) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");

        mContext = context;
    }

    /**
     * Bind to push notification service.
     * 
     * @return {@link PnsAgentConnection}
     * @throws RemoteException If the binding failed.
     */
    public synchronized PnsAgentConnection bind() throws RemoteException {
        Intent intent = new Intent(ACTION_PNS_AGENT_INTENT);

        // Try local service first.
        intent.setPackage(mContext.getPackageName());
        if (!mContext.bindService(intent, this, Context.BIND_AUTO_CREATE)) {
            mLogger.warning("Failed to bind to '", intent.toString(), "'");

            // Try system service.
            intent.setPackage(PnsDefs.PKG_NAME_PNS_CLIENT);
            if (!mContext.bindService(intent, this, Context.BIND_AUTO_CREATE)) {
                throw new RemoteException(
                        "Failed to bind to local agent, nor system agent with action '"
                                + ACTION_PNS_AGENT_INTENT + "'");
            }
        }

        /*
         * Context.bindService() call returns immediately before the service
         * connection finishes hence we need to wait until
         * ServiceConnection.onServiceConnected() being invoked.
         */
        try {
            mLogger.debug("Waiting for service being connected...");
            wait();
            mLogger.debug("Service bound.");
        } catch (InterruptedException e) {
            throw new RemoteException(e.getMessage());
        }

        return this;
    }

    /**
     * Unbind the service connection. Take no effect if the service is not
     * connected.
     */
    public synchronized void unbind() {
        if (mServiceConnected) {
            mAgent = null;
            mServiceConnected = false;
            try {
                mContext.unbindService(this);
            } catch (RuntimeException e) {
                /*
                 * In most cases it's because the caller didn't wait for the
                 * response before finishing the activity, but sometimes might
                 * caused by configuration changes and activity's automatically
                 * destroyed / re-create.
                 */
                mLogger.error("Failed to unbind service connection. ",
                        "It's most likely the Context (",
                        mContext.getClass().getSimpleName(),
                        ") which the session is operating on has been destroyed ",
                        "and a service leak occurs: ", e);
            }
        }
    }

    /**
     * Get the push notification service agent. Must be invoked after
     * {@link #bind()} successes.
     * 
     * @return {@code IPnsAgent}
     */
    public IPnsAgent getAgent() {
        if (!mServiceConnected)
            throw new IllegalStateException("Service is not connected yet. Call 'bind()' first.");

        return mAgent;
    }

    /**
     * This method runs on main thread.
     */
    @Override
    public synchronized void onServiceConnected(ComponentName name, IBinder service) {
        mLogger.debug(name, " (", service, ") connected.");
        mServiceConnected = true;
        mAgent = IPnsAgent.Stub.asInterface(service);

        mLogger.debug("Notify all who's waiting for service connected.");
        notifyAll();
    }

    /**
     * This method runs on main thread.
     */
    @Override
    public synchronized void onServiceDisconnected(ComponentName name) {
        mLogger.error("IAccountAuthenticator ", name, " disconnected accidentally.");
        mServiceConnected = false;
    }

}
