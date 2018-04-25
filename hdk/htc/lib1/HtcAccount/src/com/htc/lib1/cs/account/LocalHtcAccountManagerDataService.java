
package com.htc.lib1.cs.account;

import android.accounts.Account;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Service to operate on {@link HtcAccountManagerDataSource}.
 * 
 * @author samael_wang@htc.com
 */
public class LocalHtcAccountManagerDataService extends Service {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private Transport mTransport;

    @Override
    public IBinder onBind(Intent intent) {
        mLogger.verboseS(intent);
        return mTransport.asBinder();
    }

    @Override
    public void onCreate() {
        mLogger.verbose();

        mTransport = new Transport();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLogger.verboseS(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mLogger.verbose();
        super.onDestroy();

        mTransport = null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mLogger.verboseS(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mLogger.verbose();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRebind(Intent intent) {
        mLogger.verboseS(intent);
        super.onRebind(intent);
    }

    /**
     * Implementation of the AIDL interface.
     * 
     * @author samael_wang@htc.com
     */
    private class Transport extends IHtcAccountManagerDataSource.Stub {

        @Override
        public synchronized boolean addAccount(HtcAccount account) throws RemoteException {
            mLogger.verbose();
            return HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .addAccount(account.toAccount());
        }

        @Override
        public synchronized void addUserData(HtcAccount account, String key, String value)
                throws RemoteException {
            mLogger.verbose();
            HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .addUserData(account.toAccount(), key, value);
        }

        @Override
        public synchronized HtcAccount[] getAccounts(String typeToQuery) throws RemoteException {
            mLogger.verbose();
            Account[] accounts = HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .getAccounts(typeToQuery);
            HtcAccount[] htcAccounts = new HtcAccount[accounts.length];
            for (int i = 0; i < accounts.length; i++) {
                htcAccounts[i] = new HtcAccount(accounts[i]);
            }
            return htcAccounts;
        }

        @Override
        public synchronized String getAuthToken(HtcAccount account, String authTokenType)
                throws RemoteException {
            mLogger.verbose();
            return HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .getAuthToken(account.toAccount(), authTokenType);
        }

        @Override
        public synchronized long getId(HtcAccount account) throws RemoteException {
            mLogger.verbose();
            return HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .getId(account.toAccount());
        }

        @Override
        public synchronized String getPassword(HtcAccount account) throws RemoteException {
            mLogger.verbose();
            return HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .getPassword(account.toAccount());
        }

        @Override
        public synchronized String getUserData(HtcAccount account, String key)
                throws RemoteException {
            mLogger.verbose();
            return HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .getUserData(account.toAccount(), key);
        }

        @Override
        public synchronized void removeAccount(HtcAccount account) throws RemoteException {
            mLogger.verbose();
            HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .removeAccount(account.toAccount());
        }

        @Override
        public synchronized void removeAllAuthTokens(long id) throws RemoteException {
            mLogger.verbose();
            HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .removeAllAuthTokens(id);
        }

        @Override
        public synchronized void removeAuthToken(long id, String authToken) throws RemoteException {
            mLogger.verbose();
            HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .removeAuthToken(id, authToken);
        }

        @Override
        public synchronized void removeUserData(long id, String key) throws RemoteException {
            mLogger.verbose();
            HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .removeUserData(id, key);
        }

        @Override
        public synchronized void setAuthToken(HtcAccount account, String authTokenType,
                String authToken) throws RemoteException {
            mLogger.verbose();
            HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .setAuthToken(account.toAccount(), authTokenType, authToken);
        }

        @Override
        public synchronized void setPassword(HtcAccount account, String password)
                throws RemoteException {
            mLogger.verbose();
            HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .setPassword(account.toAccount(), password);
        }

        @Override
        public String getGuid(HtcAccount account, String authToken) throws RemoteException {
            mLogger.verbose();
            return HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .getGuid(account.toAccount(), authToken);
        }

        @Override
        public void setGuid(HtcAccount account, String guid, String authToken)
                throws RemoteException {
            mLogger.verbose();
            HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this)
                    .setGuid(account.toAccount(), guid, authToken);
        }

        @Override
        public synchronized void clear() throws RemoteException {
            mLogger.verbose();
            HtcAccountManagerDataSource.get(LocalHtcAccountManagerDataService.this).clear();
        }

    }

    /**
     * Service connection to {@link LocalHtcAccountManagerDataService}.
     * 
     * @author samael_wang@htc.com
     */
    public static class DataServiceConnection implements ServiceConnection {
        private HtcLogger mmLogger = new CommLoggerFactory(this).create();
        private Context mmContext;
        private boolean mmServiceConnected;
        private IHtcAccountManagerDataSource mRemoteDataSource;

        public DataServiceConnection(Context context) {
            if (context == null)
                throw new IllegalArgumentException("'context' is null.");

            mmContext = context;
        }

        /**
         * Bind to the data service.
         * 
         * @return {@link DataServiceConnection}
         * @throws RemoteException If binding failed.
         */
        public synchronized DataServiceConnection bind() throws RemoteException {
            Intent intent = new Intent(mmContext, LocalHtcAccountManagerDataService.class);
            if (mmContext.bindService(intent, this, Context.BIND_AUTO_CREATE)) {
                /*
                 * Context.bindService() call returns immediately before the
                 * service connection finishes hence we need to wait until
                 * ServiceConnection.onServiceConnected() being invoked.
                 */
                try {
                    mmLogger.debug("Waiting for service being connected...");
                    wait();
                    mmLogger.debug("Service bound.");
                } catch (InterruptedException e) {
                    throw new RemoteException(e.getMessage());
                }

                return this;
            }

            throw new RemoteException("Failed to bind to '"
                    + LocalHtcAccountManagerDataService.class.getSimpleName() + "'");
        }

        /**
         * Unbind the service connection. Take no effect if the service is not
         * connected.
         */
        public synchronized void unbind() {
            if (mmServiceConnected) {
                mmServiceConnected = false;
                mRemoteDataSource = null;
                try {
                    mmContext.unbindService(this);
                } catch (RuntimeException e) {
                    /*
                     * In most cases it's because the caller didn't wait for the
                     * response before finishing the activity, but sometimes
                     * might caused by configuration changes and activity's
                     * automatically destroyed / re-create.
                     */
                    mmLogger.error("Failed to unbind service connection. ",
                            "It's most likely the Context (",
                            mmContext.getClass().getSimpleName(),
                            ") which the session is operating on has been destroyed ",
                            "and a service leak occurs: ", e);
                }
            }
        }

        /**
         * Get the remote data source. Must be invoked after {@link #bind()}
         * successes.
         * 
         * @return {@link IHtcAccountManagerDataSource}
         */
        public IHtcAccountManagerDataSource getRemoteDataSource() {
            if (!mmServiceConnected)
                throw new IllegalStateException(
                        "Service is not connected yet. Call 'bind()' first.");

            return mRemoteDataSource;
        }

        @Override
        public synchronized void onServiceConnected(ComponentName name, IBinder service) {
            mmLogger.debug(name, " (", service, ") connected.");
            mmServiceConnected = true;
            mRemoteDataSource = IHtcAccountManagerDataSource.Stub.asInterface(service);

            mmLogger.debug("Notify all who's waiting for service connected.");
            notifyAll();
        }

        @Override
        public synchronized void onServiceDisconnected(ComponentName name) {
            mmLogger.error(name, " disconnected accidentally.");
            mmServiceConnected = false;
        }
    }
}
