package com.htc.lib1.cs.account;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.htc.lib1.cs.app.ProcessUtils.ensureNotOnMainThread;

abstract class AsyncAccountTask<T> extends FutureTask<T>
        implements AccountManagerFuture<T> {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    static final int CODE_TRANSACTION_RESULT = 2;
    static final int CODE_TRANSACTION_ERROR = 3;
    static final String KEY_BINDER_PARCEL = "binder_parcel";
    private AccountBinder mBinder;
    private Activity mActivity;
    private Handler mHandler;
    private AccountManagerCallback<T> mCallback;

    public AsyncAccountTask(Activity activity, AccountManagerCallback<T> callback,
                            @NonNull Handler handler) {
        super(new Callable<T>() {
            @Override
            public T call() throws Exception {
                throw new IllegalStateException("this should never be called");
            }
        });
        mBinder = new AccountBinder();
        mActivity = activity;
        mCallback = callback;
        mHandler = handler;
    }

    public AccountManagerFuture<T> start(Intent intent) {
        if (mActivity != null && intent != null) {
            // send binder to intermediate activity
            intent.putExtra(KEY_BINDER_PARCEL, new ParcelableBinder(mBinder));
            mActivity.startActivity(intent);
        } else {
            Bundle bundle = new Bundle();
            if (intent != null) {
                bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            }
            T result = bundleToResult(bundle);
            set(result);
        }
        return this;
    }

    @Override
    public T getResult() throws OperationCanceledException, IOException,
            AuthenticatorException {
        return internalGetResult(null, null);
    }

    @Override
    public T getResult(long timeout, TimeUnit unit) throws OperationCanceledException,
            IOException, AuthenticatorException {
        return internalGetResult(timeout, unit);
    }

    @Override
    protected void done() {
        if (mCallback != null) {
            postToHandler(mHandler, mCallback, this);
        }
    }

    private void postToHandler(Handler handler, final AccountManagerCallback<T> callback,
                               final AccountManagerFuture<T> future) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.run(future);
            }
        });
    }

    private T internalGetResult(Long timeout, TimeUnit unit)
            throws OperationCanceledException, IOException, AuthenticatorException {
        if (!isDone()) {
            ensureNotOnMainThread();
        }
        try {
            if (timeout == null) {
                return get();
            } else {
                return get(timeout, unit);
            }
        } catch (CancellationException e) {
            throw new OperationCanceledException();
        } catch (TimeoutException|InterruptedException e) {
            // fall through and cancel
        } catch (ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof UnsupportedOperationException) {
                throw new AuthenticatorException(cause);
            } else if (cause instanceof AuthenticatorException) {
                throw (AuthenticatorException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new IllegalStateException(cause);
            }
        } finally {
            cancel(true /* interrupt if running */);
        }
        throw new OperationCanceledException();
    }

    protected abstract T bundleToResult(Bundle bundle);

    protected Throwable bundleToException(Bundle bundle) {
        mLogger.errorS(bundle);
        return new UnknownError();
    }

    private class AccountBinder extends Binder {
        @Override
        protected final boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            switch (code) {
                case CODE_TRANSACTION_RESULT: {
                    Bundle bundle = data.readBundle();
                    T result = bundleToResult(bundle);
                    if (result == null) {
                        // leave the Future running to wait for the real response to this request
                        return true;
                    }
                    set(result);
                    return true;
                }
                case CODE_TRANSACTION_ERROR: {
                    cancel(true /* mayInterruptIfRunning */);
                    Bundle bundle = data.readBundle();
                    setException(bundleToException(bundle));
                }
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }
}
