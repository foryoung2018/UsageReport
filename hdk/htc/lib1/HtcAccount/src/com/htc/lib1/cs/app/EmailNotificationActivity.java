
package com.htc.lib1.cs.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.HtcAccountManager;
import com.htc.lib1.cs.account.HtcAccountManagerCreator;
import com.htc.lib1.cs.account.server.HtcAccountServerHelper;
import com.htc.lib1.cs.auth.R;
import com.htc.lib1.cs.auth.web.TokenDefs;
import com.htc.lib1.cs.dialog.EmailNotificationDialog;

/**
 * The activity to launch when user presses email unverified notification.
 */
public class EmailNotificationActivity extends AccountAuthenticatorActivity {
    private boolean mInitialized = false;

    private HtcAccountManager mAccntManager;
    private Account mTargetAccount;
    private String mEmailAddress;

    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccntManager = HtcAccountManagerCreator.get().createAsAuthenticator(getApplicationContext());
        Account accounts[] = mAccntManager.getAccountsByType(HtcAccountDefs.TYPE_HTC_ACCOUNT);
        if (accounts == null || accounts.length == 0) {
            mLogger.warning("No HTC Account found!");
            finish();
            return;
        }

        mTargetAccount = accounts[0];
        mEmailAddress = mAccntManager.getUserData(mTargetAccount, HtcAccountDefs.KEY_SOCIAL_ACCOUNT_NAME);
        if (TextUtils.isEmpty(mEmailAddress)) {
            mEmailAddress = mTargetAccount.name;
        }
        mLogger.debugS("EmailAddress=", mEmailAddress);

        // Init progress dialog.
        mProgressDialog = ProgressDialog.newInstance(this,
                getString(R.string.dialog_msg_please_wait));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!mInitialized) {
            mInitialized = true;
            mProgressDialog.show();

            new CheckEmailVerificationStatusTask()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class CheckEmailVerificationStatusTask extends AsyncTask<Void, Void, Boolean> {
        String mServerUri = null;
        String mAuthKey = null;
        @Override
        protected Boolean doInBackground(Void... objects) {
            mServerUri = mAccntManager.getUserData(
                    mTargetAccount, HtcAccountDefs.KEY_SERVER_URI);
            if (TextUtils.isEmpty(mServerUri)) {
                String countryCode = mAccntManager.getUserData(mTargetAccount, HtcAccountDefs.KEY_COUNTRY_CODE);
                HtcAccountServerHelper.RegionConfig regionConfig =
                        HtcAccountServerHelper.getRegionConfig(getApplicationContext(), countryCode);
                if (regionConfig != null) {
                    mServerUri = regionConfig.serviceUri;
                }
                if (TextUtils.isEmpty(mServerUri)) {
                    mServerUri = HtcAccountServerHelper.getDefaultServiceUri(
                            EmailNotificationActivity.this);
                }
            }

            try {
                Bundle tokenResult = mAccntManager.getAuthToken(
                        mTargetAccount,
                        TokenDefs.TYPE_LOCAL_ACCOUNT_TOKEN,
                        null,
                        EmailNotificationActivity.this,
                        null,
                        null)
                        .getResult();
                mAuthKey = tokenResult.getString(AccountManager.KEY_AUTHTOKEN);
            } catch (OperationCanceledException e) {
                mLogger.debug("User cancelled");
                cancel(false);
                return Boolean.FALSE;
            } catch (Exception e) {
                mLogger.warning("Failed to get master token", e);
                return Boolean.FALSE;
            }

            return Boolean.TRUE;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (isFinishing() || isDestroyedCompact()) {
                return;
            }

            if (result) {
                mProgressDialog.dismiss();
                DialogFragmentUtils.showDialog(EmailNotificationActivity.this,
                        EmailNotificationDialog.newInstance(mEmailAddress, mAuthKey, mTargetAccount, mServerUri));
            } else {
                Toast.makeText(EmailNotificationActivity.this,
                        R.string.toast_txt_error_access_social_networks_failed,
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            if (isFinishing() || isDestroyedCompact()) {
                return;
            }
            finish();
        }
    }
}
