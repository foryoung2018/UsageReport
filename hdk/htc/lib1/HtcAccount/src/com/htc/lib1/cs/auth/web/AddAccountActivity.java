
package com.htc.lib1.cs.auth.web;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.htc.lib1.cs.ConnectivityHelper;
import com.htc.lib1.cs.account.OAuth2ConfigHelper.AuthClient;
import com.htc.lib1.cs.app.DialogFragmentUtils;
import com.htc.lib1.cs.app.ProgressDialog;
import com.htc.lib1.cs.app.SelfLogActivity;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.R;
import com.htc.lib1.cs.dialog.NetworkUnavailableDialog;
import com.htc.lib1.cs.auth.googleplus.GoogleAuthTask;
import com.htc.lib1.cs.httpclient.URLEncodedUtils;
import com.htc.lib1.cs.httpclient.URLEncodedUtils.NameValuePair;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.workflow.AsyncWorkflowTask;
import com.htc.lib1.cs.workflow.RunningWorkflowTasks;
import com.htc.lib1.cs.workflow.UnexpectedException;
import com.htc.lib1.cs.workflow.Workflow.ResultHandler;
import com.htc.lib1.cs.workflow.Workflow.UnexpectedExceptionHandler;

/**
 * The activity used to sign-in an account.
 * 
 * @author samael_wang@htc.com
 */
public class AddAccountActivity extends SelfLogActivity {
    private static final int ACTIVITY_REQ_CODE_GOOGLE_START = 100;

    public static final int RESULT_RESTART_ADD_ACCOUNT = RESULT_FIRST_USER;

    private enum SignInFlowType {
        UNKNOWN,
        MAIN_FLOW,
        SIGN_IN_GOOGLE,
    }

    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private OnInitNetworkUnavailableClickListener mOnInitNetUnavailableClickListener = new OnInitNetworkUnavailableClickListener();
    private Dialog mProgressDialog;
    private boolean mInitialized;
    private WebView mWebView;
    private boolean mLoadingFirstPage = true;

    private String mAuthTokenType = null;

    private GoogleAuthTask mGoogleAuthTask = null;
    private SignInFlowType mSignInFlowType = SignInFlowType.UNKNOWN;

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // Cancel sign-in flow if process has been killed
            mLogger.info("Restart sign-in flow");
            setResult(RESULT_RESTART_ADD_ACCOUNT);
            finish();
            return;
        }

        Intent intent = getIntent();
        if (intent != null) {
            mAuthTokenType = intent.getStringExtra(AddAccountAuthenticatorActivity.EXTRA_KEY_AUTH_TOKEN_TYPE);
        }

        // Set the layout.
        setContentView(R.layout.specific_activity_add_account);

        // Initialize WebView.
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(false);
        mWebView.setWebViewClient(new WebAuthorizationClient());

        /**
         * Security enhancement for CVE-2014-1939 and CVE-2014-7224. Refer
         * 
         * <pre>
         * http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2014-1939
         * http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2014-7224
         * https://daoyuan14.github.io/news/newattackvector.html
         * </pre>
         */
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");

        // Init progress dialog.
        mProgressDialog = ProgressDialog.newInstance(this,
                getString(R.string.dialog_msg_please_wait));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // Check network.
        if (hasConnectivityOrShowWarningDialog() && !mInitialized) {
            // Show progress dialog.
            mProgressDialog.show();

            // Clear cookie and start authorization.
            CookieManager.getInstance().removeAllCookie();
            switchSignInFlowType(SignInFlowType.MAIN_FLOW);

            mInitialized = true;
        }
    }

    @Override
    protected void onDestroy() {
        /*
         * To prevent WindowLeaked of progress dialog, we try to dismiss it
         * when Activity destroyed.
         */
        dismissProgressDialog();

        super.onDestroy();

        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }

        if (mGoogleAuthTask != null) {
            mGoogleAuthTask.destroy();
            mGoogleAuthTask = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mGoogleAuthTask != null &&
                mGoogleAuthTask.handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (mGoogleAuthTask != null &&
                mGoogleAuthTask.handleRequestPermissionResult(
                        requestCode, permissions, grantResults)) {
            return;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            setResult(RESULT_CANCELED);
            onAddAccountFlowCancel();
            super.onBackPressed();
        }
    }

    private void switchSignInFlowType(SignInFlowType type) {
        if (mSignInFlowType != type) {
            mSignInFlowType = type;
            switch (mSignInFlowType) {
                case MAIN_FLOW: {
                    mLogger.info("Start main signing-in flow");
                    mProgressDialog.show();

                    AuthClient client = WebAuthConfig.get(this).getAuthClient();
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new NameValuePair(WebAuthDefs.PARAM_CLIENT_ID, client.id));
                    params.add(new NameValuePair(WebAuthDefs.PARAM_RESPONSE_TYPE,
                            WebAuthDefs.RESPONSE_TYPE_CODE));
                    params.add(new NameValuePair(WebAuthDefs.PARAM_REDIRECT_URL,
                            WebAuthConfig.get(AddAccountActivity.this).getAuthClient().redirectUrl));
                    if (!TextUtils.isEmpty(client.scopes))
                        params.add(new NameValuePair(WebAuthDefs.PARAM_SCOPES, client.scopes));
                    //String url = new StringBuilder("https://www.htcsense.com/$SS$/")
                    String url = new StringBuilder(WebAuthConfig.get(this).getBaseUri())
                            .append("Services/OAuth/Authorize?")
                            .append(URLEncodedUtils.format(params)).toString();
                    mLogger.debugS("Loading ", url);
                    mLoadingFirstPage = true;
                    mWebView.loadUrl(url);
                    break;
                }
                case SIGN_IN_GOOGLE: {
                    mLogger.info("Start Google signing-in flow");
                    mProgressDialog.show();
                    mWebView.loadUrl("file:///android_res/raw/htc_account_auth_blank.html");
                    if (mGoogleAuthTask != null) {
                        mGoogleAuthTask.destroy();
                    }
                    mGoogleAuthTask = new GoogleAuthTask(
                            this,
                            new GoogleAuthCallback(),
                            mAuthTokenType,
                            ACTIVITY_REQ_CODE_GOOGLE_START,
                            GoogleAuthTask.REQUIRED_ACTIVITY_REQ_CODES);
                    mGoogleAuthTask.start();
                    break;
                }
            }
        }
    }

    /**
     * Overloaded method to set dialog behavior.
     * 
     * @return True if data network is available, false otherwise.
     */
    private boolean hasConnectivityOrShowWarningDialog() {
        if (ConnectivityHelper.get(this).isConnected())
            return true;

        /* else */
        mLogger.debug("No connectivity presents.");

        /*
         * The onDetach() method which removes the listener might be invoked
         * right after setOnClickListener if we reuse the existing instance.
         * Further the concurrency nature of FragmentManager operations might
         * encounter problems when operating on the same instance. Hence we
         * create new instance on each call.
         */
        NetworkUnavailableDialog dialog = NetworkUnavailableDialog.newInstance();
        dialog.setOnClickListener(mOnInitNetUnavailableClickListener);
        DialogFragmentUtils.showDialog(this, dialog);
        return false;
    }

    /**
     * Show general error toast message.
     */
    private void showErrorToast() {
        if (!isDestroyedCompact()) {
            Toast.makeText(AddAccountActivity.this, R.string.toast_txt_error_general_failure,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Dismiss progress dialog.
     */
    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mLogger.verbose("Dismiss progress dialog.");
            mProgressDialog.dismiss();
        }
    }

    /**
     * Notify integrated apps that the add account flow has been completed.
     * 
     * @param accnt Added account.
     */
    private void onAddAccountFlowComplete(Account accnt) {
        BroadcastUtils.addAccountCompleted(this);

        Intent data = new Intent();
        data.putExtra(AccountManager.KEY_ACCOUNT_NAME, accnt.name);
        data.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accnt.type);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    /**
     * Notify integrated apps that the add account flow has been canceled.
     */
    private void onAddAccountFlowCancel() {
        BroadcastUtils.addAccountCanceled(this);
        finish();
    }

    /**
     * Listener for {@link NetworkUnavailableDialog} during initialization.
     * 
     * @author samael_wang@htc.com
     */
    private class OnInitNetworkUnavailableClickListener implements
            NetworkUnavailableDialog.OnClickListener {

        @Override
        public void onClick(NetworkUnavailableDialog dialog, int which) {
            if (which == NetworkUnavailableDialog.BUTTON_CANCEL) {
                /*
                 * If network is not available and user chooses to cancel, then
                 * cancel the whole flow.
                 */
                onBackPressed();
            }
        }

    }

    /**
     * WebViewClient to perform authorization.
     * 
     * @author samael_wang@htc.com
     */
    private class WebAuthorizationClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mLogger.verboseS(url);

            boolean override = false;

            if (url.startsWith(WebAuthConfig.get(AddAccountActivity.this).getAuthClient().redirectUrl)) {
                view.setVisibility(View.GONE);

                String authorizationCode = Uri.parse(url).getQueryParameter(
                        WebAuthDefs.PARAM_AUTH_CODE);
                mLogger.debugS("Got authorization code: ", authorizationCode);

                new AsyncWorkflowTask.Builder<Account>(
                        AddAccountActivity.this,
                        new AuthorizationWorkflow(
                                AddAccountActivity.this,
                                WebAuthConfig.get(AddAccountActivity.this).getAuthClient().id,
                                WebAuthConfig.get(AddAccountActivity.this).getAuthClient().secret,
                                authorizationCode,
                                WebAuthConfig.get(AddAccountActivity.this).getAuthClient().redirectUrl))
                        .setProgressDialog(mProgressDialog)
                        .addResultHandler(new AuthorizationResultHandler())
                        .addUnexpectedExpHandler(new GeneralErrorHandler())
                        .build().executeOnThreadPool();

                override = true;
            } else if (GoogleAuthTask.isGoogleSign(url)) {
                switchSignInFlowType(SignInFlowType.SIGN_IN_GOOGLE);
                override = true;
            }

            return override;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mLogger.verboseS(url);

            if (mSignInFlowType == SignInFlowType.MAIN_FLOW) {
                /*
                 * Show progress dialog when the Activity is running.
                 */
                if (!AddAccountActivity.this.isDestroyedCompact() &&
                        !AddAccountActivity.this.isFinishing()) {
                    mProgressDialog.show();
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mLogger.verboseS(url);

            if (mSignInFlowType == SignInFlowType.MAIN_FLOW) {
                if (mLoadingFirstPage) {
                    mWebView.clearHistory();
                    mLoadingFirstPage = false;
                }
                /*
                 * Dismiss the dialog if there's no running tasks associating with
                 * this activity.
                 */
                if (!RunningWorkflowTasks.get().hasRunningTasks(AddAccountActivity.this)) {
                    dismissProgressDialog();
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            if (mSignInFlowType == SignInFlowType.MAIN_FLOW) {
                mLogger.verboseS(failingUrl);
                mLogger.warning("errorCode=", errorCode, ", description=", description);

                // Dismiss progress dialog and show error toast.
                dismissProgressDialog();
                showErrorToast();

                /*
                 * If there's nothing to show, stop the flow immediately. Otherwise
                 * restore to the previous step.
                 */
                if (view.canGoBack()) {
                    view.goBack();
                } else {
                    onAddAccountFlowCancel();
                }
            }
        }
    }

    /**
     * Result handler for {@link AsyncWorkflowTask}.
     * 
     * @author samael_wang@htc.com
     */
    private class AuthorizationResultHandler implements ResultHandler<Account> {

        @Override
        public boolean onResult(Activity activity, Account account) {
            onAddAccountFlowComplete(account);
            return true;
        }

    }

    /**
     * General error handler for sign-in failures.
     * 
     * @author samael_wang@htc.com
     */
    private class GeneralErrorHandler implements UnexpectedExceptionHandler {

        @Override
        public boolean onException(Activity activity, UnexpectedException exception) {
            Toast.makeText(AddAccountActivity.this, R.string.toast_txt_error_general_failure,
                    Toast.LENGTH_SHORT).show();
            onAddAccountFlowCancel();
            return true;
        }
    }

    private class GoogleAuthCallback implements GoogleAuthTask.Callback {
        @Override
        public void onFinish(GoogleAuthTask.ResultCode resultCode, Account account) {
            mLogger.info("Google auth finished: " + resultCode.name());
            switch (resultCode) {
                case OK:
                    onAddAccountFlowComplete(account);
                    break;
                case USER_CANCELED:
                    mLogger.info("Cancelled signing-in Google account");
                    switchSignInFlowType(SignInFlowType.MAIN_FLOW);
                    break;
                case NO_AVAILABLE_ACCOUNT:
                    mLogger.info("No Google account available");
                    Toast.makeText(AddAccountActivity.this,
                            R.string.toast_txt_no_google_account,
                            Toast.LENGTH_SHORT)
                            .show();
                    switchSignInFlowType(SignInFlowType.MAIN_FLOW);
                    break;
                case GOOGLE_API_ERROR:
                    mLogger.info("Unable to connect to Google service");
                    Toast.makeText(AddAccountActivity.this,
                            R.string.toast_txt_error_access_social_networks_failed,
                            Toast.LENGTH_SHORT)
                            .show();
                    switchSignInFlowType(SignInFlowType.MAIN_FLOW);
                    break;
                case OTHERS:
                    mLogger.info("Failed to sign in with Google account");
                    Toast.makeText(AddAccountActivity.this,
                            R.string.toast_txt_error_signing_in_google_account,
                            Toast.LENGTH_SHORT)
                            .show();
                    switchSignInFlowType(SignInFlowType.MAIN_FLOW);
                    break;
                default:
                    mLogger.warning("Undefined error: " + resultCode.name());
                    switchSignInFlowType(SignInFlowType.MAIN_FLOW);
                    break;
            }
        }
    }
}
