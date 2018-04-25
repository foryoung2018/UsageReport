
package com.htc.lib1.cs.account;

import java.io.IOException;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.cs.auth.BuildConfig;
import com.htc.lib1.cs.account.HtcAccountManagerFuture.HtcAccountManagerCallable;
import com.htc.lib1.cs.app.ClassLoaderUtils;
import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * Service connection for a local authenticator which accepts
 * {@link HtcAccountDefs#ACTION_IDENTITY_AUTHENTICATOR} and extends
 * {@link AbstractAccountAuthenticator}.
 * 
 * @author samael_wang@htc.com
 */
/* package */abstract class LocalHtcAccountAuthenticatorSession extends
        IHtcAccountAuthenticatorResponse.Stub implements ServiceConnection,
        HtcAccountManagerCallable<Bundle> {
    /**
     * Action to bind local HTC Account authenticator.
     */
    public static final String ACTION_LOCAL_HTC_ACCOUNT_AUTHENTICATOR_INTENT = "com.htc.cs.HtcAccountAuthenticator";

    protected HtcLogger mLogger = new CommLoggerFactory(this).create();
    protected IHtcAccountAuthenticator mAuthenticator;
    
    // Lock object for bind / unbind operations.
    private final Object mConnectionLock = new Object();
    // Lock object for waiting for authenticator responses.
    private final Object mAuthenticatorLock = new Object();
    private boolean mServiceConnected;
    private Context mContext;
    private boolean mLaunchActivity;
    private Exception mError;
    private Bundle mResult;

    /**
     * Construct an instance.
     * 
     * @param context Context to operate on.
     * @param launchActivity True to launch the activity if the result contains
     *            an intent.
     */
    public LocalHtcAccountAuthenticatorSession(Context context, boolean launchActivity) {
        if (context == null)
            throw new IllegalArgumentException("'context' is null.");
        mContext = context;
        mLaunchActivity = launchActivity;
    }

    @Override
    public Bundle call() throws IOException, AuthenticatorException, OperationCanceledException {
        try {
            bind();
        } catch (RemoteException e) {
            mLogger.error(e);
            onError(AccountManager.ERROR_CODE_REMOTE_EXCEPTION, e.getMessage());
        }

        if (mAuthenticator != null) {
            try {
                /*
                 * As AIDL document mentions, calls made from the local process
                 * are executed in the same thread that is making the call.
                 * Which means the invocation will be blocked until the result
                 * of the authenticator returns.
                 */
                doWork();
            } catch (RemoteException e) {
                mLogger.error(e);
                /*
                 * This exception could only happen if the authenticator process
                 * dies. Since local authenticator works in the main process of
                 * the calling app, it's unlikely to happen.
                 */
                onError(AccountManager.ERROR_CODE_REMOTE_EXCEPTION, e.getMessage());
            }
        }

        return internalGetResult();
    }

    /**
     * Bind the authenticator.
     */
    private void bind() throws RemoteException {
        Intent intent = new Intent();
        intent.setAction(ACTION_LOCAL_HTC_ACCOUNT_AUTHENTICATOR_INTENT);
        intent.setPackage(mContext.getPackageName());

        mLogger.debug("Binding authenticator...");
        synchronized (mConnectionLock) {
            if (mContext.bindService(intent, this, Context.BIND_AUTO_CREATE)) {
                /*
                 * Context.bindService() call returns immediately before the
                 * service connection finishes hence we need to wait until
                 * ServiceConnection.onServiceConnected() being invoked.
                 */
                try {
                    mLogger.debug("Waiting for service being connected...");
                    mConnectionLock.wait();
                    mLogger.debug("Service bound.");
                } catch (InterruptedException e) {
                    mLogger.error(e);
                    throw new RemoteException(e.getMessage());
                }
            } else {
                throw new RemoteException("Authenticator binding failed.");
            }
        }
    }

    /**
     * Close the authenticator session.
     */
    private void closeSession() {
        try {
            unbind();
        } finally {
            synchronized (mAuthenticatorLock) {
                mAuthenticatorLock.notifyAll();
            }
        }
    }

    /**
     * Unbind the service connection. Take no effect if the service is not
     * connected.
     */
    private void unbind() {
        synchronized (mConnectionLock) {
            if (mServiceConnected) {
                mServiceConnected = false;
                mAuthenticator = null;
                try {
                    mContext.unbindService(this);
                } catch (RuntimeException e) {
                    /*
                     * In most cases it's because the caller didn't wait for the
                     * response before finishing the activity, but sometimes
                     * might caused by configuration changes and activity's
                     * automatically destroyed / re-create.
                     */
                    mLogger.error("Failed to unbind service connection. ",
                            "It's most likely the Context (",
                            mContext.getClass().getSimpleName(),
                            ") which the session is operating on has been destroyed ",
                            "and a service leak occurs: ", e);
                }
            }
        }
    }

    /**
     * Subclasses should implement this method to invoke authenticator. This
     * method runs in a background thread.
     */
    public abstract void doWork() throws RemoteException;

    /**
     * This method runs on main thread.
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mLogger.debug("IHtcAccountAuthenticator ", name, " (", service, ") connected.");

        synchronized (mConnectionLock) {
            mServiceConnected = true;
            mAuthenticator = IHtcAccountAuthenticator.Stub.asInterface(service);

            mLogger.debug("Notify all who's waiting for service connected.");
            mConnectionLock.notifyAll();
        }
    }

    /**
     * This method runs on main thread.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        mLogger.error("IHtcAccountAuthenticator ", name, " disconnected accidentally.");

        synchronized (mConnectionLock) {
            mServiceConnected = false;
            mAuthenticator = null;
        }

        onError(AccountManager.ERROR_CODE_REMOTE_EXCEPTION, "Service disconnected accidentally.");
    }

    /**
     * Convert error code / message to exceptions.
     * 
     * @param code Error code defined in {@link AccountManager}.
     * @param message Authenticator defined message for the error.
     * @return Corresponding exception.
     */
    private Exception convertErrorToException(int code, String message) {
        if (code == AccountManager.ERROR_CODE_BAD_ARGUMENTS)
            return new IllegalArgumentException(message);

        if (code == AccountManager.ERROR_CODE_UNSUPPORTED_OPERATION)
            return new UnsupportedOperationException(message);

        if (code == AccountManager.ERROR_CODE_CANCELED)
            return new OperationCanceledException(message);

        if (code == AccountManager.ERROR_CODE_NETWORK_ERROR)
            return new IOException(message);

        if (code == AccountManager.ERROR_CODE_INVALID_RESPONSE)
            return new AuthenticatorException(message);

        return new AuthenticatorException(message);
    }

    /**
     * Block waiting for the result with given timeout.
     * 
     * @param timeout Timeout in milliseconds.
     * @return Response bundle from authenticator.
     * @throws IOException If the authenticator experienced an I/O problem
     *             creating a new auth token, usually because of network trouble
     * @throws AuthenticatorException If the authenticator failed to respond.
     * @throws OperationCanceledException If the operation was canceled for any
     *             reason, including the user canceling the creation process.
     */
    protected Bundle internalGetResult() throws IOException, AuthenticatorException,
            OperationCanceledException {

        synchronized (mAuthenticatorLock) {
            if (mError == null && mResult == null) {
                AccountAuthenticatorMonitorThread monitorThread = null;

                try {
                    // Initiate monitor thread if in debug mode.
                    if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag
                            || HtcWrapHtcDebugFlag.Htc_DEBUG_flag
                            || BuildConfig.DEBUG) {
                        monitorThread = new AccountAuthenticatorMonitorThread();
                        monitorThread.start();
                    }

                    mLogger.debug("Start working...");
                    mAuthenticatorLock.wait();
                    mLogger.debug("Work finishes.");
                } catch (InterruptedException e) {
                    mError = new OperationCanceledException(e.getMessage(), e);
                } finally {
                    // Stop the monitor thread.
                    if (monitorThread != null)
                        monitorThread.setStop(true);
                }
            }
        }

        /*
         * Authenticator returns an error. Rethrow it to the caller.
         */
        if (mError != null) {
            if (mError instanceof RuntimeException)
                throw (RuntimeException) mError;
            if (mError instanceof IOException)
                throw (IOException) mError;
            if (mError instanceof AuthenticatorException)
                throw (AuthenticatorException) mError;
            if (mError instanceof OperationCanceledException)
                throw (OperationCanceledException) mError;
            throw new AuthenticatorException(mError.getMessage(), mError);
        }

        /* Result couldn't be null. */
        if (mResult == null)
            throw new IllegalStateException("Both 'mError' and 'mResult' are null after execution.");

        /*
         * Return successful result.
         */
        return mResult;
    }

    @Override
    public void onResult(Bundle result) {
        /*
         * It would need to specify the class loader to application class
         * loader when unmarshalling in the subprocess or will cause
         * {@link ClassNotFoundException} if customed {@link Parcelable}
         * data existed.
         */
        ClassLoaderUtils.ensureWithAppClassLoader(result);

        mLogger.verbose("result: ", result);

        if (result == null) {
            /*
             * null result is unexpected and should be treated as an error.
             */
            onError(AccountManager.ERROR_CODE_INVALID_RESPONSE,
                    "Authenticator returns null result.");
        } else if (result.containsKey(AccountManager.KEY_ERROR_CODE)
                && result.containsKey(AccountManager.KEY_ERROR_MESSAGE)) {
            /*
             * Convert to exception if the returning value includes error code /
             * message.
             */
            int errorCode = result.getInt(AccountManager.KEY_ERROR_CODE);
            String errorMessage = result.getString(AccountManager.KEY_ERROR_MESSAGE);
            onError(errorCode, errorMessage);
        } else if (mLaunchActivity && result.containsKey(AccountManager.KEY_INTENT)) {
            /*
             * Launch the activity if caller request and wait for the real
             * result.
             */
            mLogger.debug("Start the authenticator activity.");
            mContext.startActivity((Intent) result.getParcelable(AccountManager.KEY_INTENT));
        } else {
            mLogger.debug("Pass the result and close the session.");
            mResult = result;
            closeSession();
        }
    }

    @Override
    public void onRequestContinued() {
        mLogger.verbose();
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        mLogger.error("errorCode: ", errorCode, ", errorMessage: ", errorMessage);
        mError = convertErrorToException(errorCode, errorMessage);
        closeSession();
    }

    /**
     * Monitor thread used to periodically check running authenticator request.
     * 
     * @author samael_wang@htc.com
     */
    private class AccountAuthenticatorMonitorThread extends Thread {
        private boolean mmStop;

        public void setStop(boolean stop) {
            mmStop = stop;
        }

        @Override
        public void run() {
            while (!mmStop) {
                mLogger.verbose("Waiting for authenticator response...");
                SystemClock.sleep(5000);
            }
        }

    };

}
