
package com.htc.lib1.cs.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import com.htc.lib1.cs.ConnectivityHelper;
import com.htc.lib1.cs.account.ConfigurationResource;
import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.IdentityRegion;
import com.htc.lib1.cs.account.server.HtcAccountServerHelper;
import com.htc.lib1.cs.auth.R;
import com.htc.lib1.cs.dialog.NetworkUnavailableDialog;
import com.htc.lib1.cs.dialog.TermDialog;
import com.htc.lib1.cs.workflow.Workflow.ResultHandler;

import java.util.List;
import java.util.Locale;

/**
 * Activity to show terms and conditions or privacy policy.
 * 
 * @author samael_wang@htc.com
 */
public class TermActivity extends SelfLogActivity implements OnDismissListener {
    private static final String ACTION_SHOW_TOS = "com.htc.lib1.cs.action.SHOW_TOS";
    private static final String ACTION_SHOW_PRIVACY_POLICY = "com.htc.lib1.cs.action.SHOW_PRIVACY_POLICY";

    public static void launchTosActivity(Activity activity) {
        Intent intent = new Intent(ACTION_SHOW_TOS);
        intent.setClass(activity, TermActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void launchPrivacyPolicyActivity(Activity activity) {
        Intent intent = new Intent(ACTION_SHOW_PRIVACY_POLICY);
        intent.setClass(activity, TermActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    private TermDialog mTermDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // Cancel sign-in flow if process has been killed
            mLogger.info("Cancel sign-in flow since process has been killed");
            finish();
            return;
        }

        setup();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (mTermDialog != null) {
            DialogFragmentUtils.dismissDialog(this, mTermDialog, true /* singleInstance */);
        }

        setup();
    }

    private void setup() {
        if (ConnectivityHelper.get(this).isConnected()) {
            showTos();
        } else {
            NetworkUnavailableDialog dialog = NetworkUnavailableDialog.newInstance();
            dialog.setOnClickListener(new OnNetworkUnavailableDialogClickListener());
            DialogFragmentUtils
                    .showDialog(this, dialog, true /* singleInstance */);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Show legal docs dialog.
     */
    private void showTos() {
        mLogger.verbose();

        /*
         * If caller has passed a country code, try to use it. Otherwise, find a
         * suggested country code.
         */
        Locale systemLocale = Locale.getDefault();
        StringBuilder sb = new StringBuilder().append(systemLocale.getLanguage());
        if (!TextUtils.isEmpty(systemLocale.getCountry())) {
            sb.append("_").append(systemLocale.getCountry());
        }
        String countryCode = sb.toString();

        // Generate legal docs URL.
        String legalDocsUrl;
        int titleResId;
        if (ACTION_SHOW_PRIVACY_POLICY.equals(getIntent().getAction())) {
            legalDocsUrl = new ConfigurationResource(this)
                    .getPrivacyPolicyUrl(countryCode);
            titleResId = R.string.dialog_title_privacy_policy;
        } else { /* ACTION_SHOW_TOS */
            // Convert full language code to country code. e.g. "en_US" to "US"
            // and make ensure its in supported country list.
            if (!TextUtils.isEmpty(countryCode) && countryCode.contains("_")) {
                countryCode = countryCode.substring(countryCode.indexOf('_') + 1);
                countryCode = HtcAccountServerHelper
                        .getRegionConfigWithoutUpdate(this, countryCode)
                        .countryCode;
            }
            legalDocsUrl = new ConfigurationResource(this)
                    .getPendingLegalDocsUrl(countryCode, null);
            titleResId = R.string.dialog_title_term_of_service;
        }

        // Append URL fragment identifier if caller specified.
        String fragIdentifier = getIntent().getStringExtra(
                HtcAccountDefs.KEY_URL_FRAGMENT_IDENTIFIER);
        if (!TextUtils.isEmpty(fragIdentifier) && fragIdentifier.startsWith("#"))
            legalDocsUrl += fragIdentifier;

        mLogger.debugS("URL: ", legalDocsUrl);
        mTermDialog = TermDialog.newInstance(titleResId, legalDocsUrl);
        mTermDialog.setOnDismissListener(this);
        DialogFragmentUtils.showDialog(this, mTermDialog, true /* singleInstance */);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        finish();
        /*
         * This TermActivity will have fade out and slide out to right in OOBE,
         * so we override the transition after finish it.
         */
        overridePendingTransition(0, 0);
    }

    private class OnNetworkUnavailableDialogClickListener implements
            NetworkUnavailableDialog.OnClickListener {

        @Override
        public void onClick(NetworkUnavailableDialog dialog, int which) {
            finish();
            /*
             * This TermActivity will have fade out and slide out to right in
             * OOBE, so we override the transition after finish it.
             */
            overridePendingTransition(0, 0);
        }

    }

    /**
     * Show legal docs after get regions done.
     */
    private class LegalTipsTosHandler implements ResultHandler<List<IdentityRegion>> {

        @Override
        public boolean onResult(Activity activity, List<IdentityRegion> result) {
            showTos();
            return true;
        }

    }
}
