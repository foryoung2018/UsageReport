package com.htc.lib1.cs.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;

import java.util.List;

/**
 * A transparent activity act as an intermediate activity, to handle the result of sign-in action.
 */
public class SignInHelperActivity extends Activity {
    private HtcLogger mLogger = new CommLoggerFactory(this).create();
    private static final int REQUEST_CODE_SIGN_IN_CONFIRM = 0;
    private static final int REQUEST_CODE_ACCOUNT_MANAGER_INTENT = 1;

    public static final String ACTION_SIGN_IN_INTERMEDIATE = "signInIntermediate";

    public static final String KEY_CALLING_MODULE = "callingModule";
    public static final String KEY_SIGNED_IN = "signedIn";

    private String mCallingModule;
    private IBinder mBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mCallingModule = intent.getStringExtra(KEY_CALLING_MODULE);

        if (!launchTarget(getIntent())) {
            // do nothing
            mLogger.error("Forbidden action: no intent to start.");
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private boolean launchTarget(Intent intent) {
        boolean success = false;
        if (ACTION_SIGN_IN_INTERMEDIATE.equals(intent.getAction())) {
            // sign-in intermediate
            ParcelableBinder parcel = intent.getParcelableExtra(AsyncAccountTask.KEY_BINDER_PARCEL);
            if (parcel != null) {
                mBinder = parcel.getBinder();
            }
            Intent targetIntent = getSignInWithAccountConfirmIntent();
            if (targetIntent != null) {
                startActivityForResult(targetIntent, REQUEST_CODE_SIGN_IN_CONFIRM);
                success = true;
            }
        } else {
            // general case, bring up authenticator's intent
            Intent accountManagerIntent = intent.getParcelableExtra(AccountManager.KEY_INTENT);
            if (accountManagerIntent != null) {
                startActivityForResult(accountManagerIntent, REQUEST_CODE_ACCOUNT_MANAGER_INTENT);
                success = true;
            }
        }
        return success;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN_CONFIRM) {
            boolean signedIn = resultCode == RESULT_OK;
            Bundle bundle = new Bundle();
            if (signedIn) {
                if (data != null) {
                    String tag = data.getStringExtra(HtcAccountDefs.KEY_ACCOUNT_TAG);

                    if (!TextUtils.isEmpty(mCallingModule)) {
                        AppSignInHelper helper = new AppSignInHelper(this, mCallingModule);
                        helper.setSignIn(true, tag);
                    }
                }
                Account[] accounts = HtcAccountManagerCreator.get().create(this).getAccounts();
                if (accounts.length > 0) {
                    bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, HtcAccountDefs.TYPE_HTC_ACCOUNT);
                    bundle.putString(AccountManager.KEY_ACCOUNT_NAME, accounts[0].name);
                }
            }
            bundle.putBoolean(KEY_SIGNED_IN, signedIn);
            notifyBinderResult(bundle);
            Intent resultData = new Intent();
            resultData.putExtras(bundle);
            setResult(resultCode, resultData);
            finish();
        } else if (requestCode == REQUEST_CODE_ACCOUNT_MANAGER_INTENT) {
            if (resultCode == RESULT_OK) {
                AppSignInHelper helper = new AppSignInHelper(this, mCallingModule);
                helper.setSignIn(true);
            }
            // bypass result to app
            setResult(resultCode, data);
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

    private Intent getSignInWithAccountConfirmIntent() {
        Intent intent = new Intent(HtcAccountDefs.ACTION_SIGN_IN_ACCOUNT_CONFIRM);
        intent.setPackage(HtcAccountDefs.PKG_NAME_IDENTITY_CLIENT);

        // check activity existence.
        List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (activities.isEmpty()) {
            mLogger.warning("Older identity client, return null");
            return null;
        }
        return intent;
    }

}
