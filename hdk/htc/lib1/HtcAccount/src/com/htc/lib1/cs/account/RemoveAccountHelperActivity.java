package com.htc.lib1.cs.account;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * A transparent activity to help handle account removal result with Identity Client before 8.40
 */
public class RemoveAccountHelperActivity extends Activity {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private static final int REQUEST_CODE_LAUNCH_ACCOUNT_SETTINGS = 0;
    public static final String KEY_ACCOUNT_REMOVED = "accountRemoved";

    private HtcAccountManager mAccountManager;

    private IBinder mBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParcelableBinder parcel = getIntent().getParcelableExtra(AsyncAccountTask.KEY_BINDER_PARCEL);
        if (parcel != null) {
            mBinder = parcel.getBinder();
        }

        mAccountManager = HtcAccountManagerCreator.get().create(this);
        Account accounts[] = mAccountManager.getAccounts();
        Bundle args = new Bundle();
        if (accounts.length > 0) {
            args.putParcelable("account", accounts[0]);
            Intent intent = new Intent("android.settings.ACCOUNT_SYNC_SETTINGS");
            intent.putExtras(args);
            startActivityForResult(intent, REQUEST_CODE_LAUNCH_ACCOUNT_SETTINGS);
        } else {
            Bundle result = new Bundle();
            result.putBoolean(KEY_ACCOUNT_REMOVED, true);
            notifyBinderResult(result);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LAUNCH_ACCOUNT_SETTINGS) {
            boolean accountRemoved = mAccountManager.getAccounts().length == 0;
            Bundle bundle = new Bundle();
            bundle.putBoolean(KEY_ACCOUNT_REMOVED, accountRemoved);
            notifyBinderResult(bundle);
            Intent resultData = new Intent();
            resultData.putExtras(bundle);
            if (accountRemoved) {
                setResult(RESULT_OK, resultData);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }

    private void notifyBinderResult(@NonNull Bundle result) {
        if (mBinder != null && mBinder.isBinderAlive()) {
            Parcel parcel = Parcel.obtain();
            parcel.writeBundle(result);
            try {
                mBinder.transact(AsyncAccountTask.CODE_TRANSACTION_RESULT, parcel, null,
                        Binder.FLAG_ONEWAY);
            } catch (RemoteException e) {
                mLogger.error(e.getMessage());
            }
        }
    }
}
