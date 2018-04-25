
package com.htc.lib1.cs.auth.web;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.htc.lib1.cs.account.HtcAccountAuthenticatorActivity;
import com.htc.lib1.cs.account.HtcAccountDefs;

/**
 * {@link AddAccountAuthenticatorActivity} plays the single entry of account
 * creation/sign-in flow no matter what activity actually being shown up. This
 * activity doesn't have any UI itself. It's only used to control initiate
 * activities according to options passed in and set {@link AccountManager}
 * responses.
 */
public class AddAccountAuthenticatorActivity extends HtcAccountAuthenticatorActivity {
    public static final String EXTRA_KEY_AUTH_TOKEN_TYPE = "com.htc.lib1.cs.KEY_AUTH_TOKEN_TYPE";

    private String mAuthTokenType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mAuthTokenType = savedInstanceState.getString(EXTRA_KEY_AUTH_TOKEN_TYPE);
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                mAuthTokenType = intent.getStringExtra(EXTRA_KEY_AUTH_TOKEN_TYPE);
            }
        }

        // Only support calling through AccountManager.
        if (getAuthenticatorResponse() != null) {
            if (savedInstanceState == null) {
                Intent intent = new Intent(this, AddAccountActivity.class);
                intent.putExtra(EXTRA_KEY_AUTH_TOKEN_TYPE, mAuthTokenType);
                startActivityForResult(intent, 0);
            } else {
                /*
                 * The activity has been re-created. It usually indicates a
                 * subsequent onActivityResult invocation. Do nothing here and
                 * wait for that.
                 */
            }
        } else {
            mLogger.error("It appears no authenticator response object has been passed in.");
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data == null)
                throw new IllegalStateException("'data' is null.");

            String type = HtcAccountDefs.TYPE_HTC_ACCOUNT;
            String name = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            if (TextUtils.isEmpty(name))
                throw new IllegalStateException("'name' is null or empty.");

            // Prepare for authenticator response.
            Bundle authenticatorResponse = new Bundle();
            authenticatorResponse.putString(AccountManager.KEY_ACCOUNT_TYPE, type);
            authenticatorResponse.putString(AccountManager.KEY_ACCOUNT_NAME, name);

            // Set activity result.
            Intent activityResultIntent = new Intent();
            activityResultIntent.putExtras(authenticatorResponse);
            setResult(RESULT_OK, activityResultIntent);

            // Set authenticator result.
            setAccountAuthenticatorResult(authenticatorResponse);
        } else if (resultCode == AddAccountActivity.RESULT_RESTART_ADD_ACCOUNT) {
            mLogger.info("Restart sign-in flow");
            Intent intent = new Intent(this, AddAccountActivity.class);
            intent.putExtra(EXTRA_KEY_AUTH_TOKEN_TYPE, mAuthTokenType);
            startActivityForResult(intent, 0);
        } else {
            setResult(resultCode);
        }

        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_KEY_AUTH_TOKEN_TYPE, mAuthTokenType);
    }
}
