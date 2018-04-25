
package com.htc.lib1.cs.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.htc.lib1.cs.app.ProgressDialog;
import com.htc.lib1.cs.app.SelfLogDialog;
import com.htc.lib1.cs.auth.AuthLoggerFactory;
import com.htc.lib1.cs.auth.R;
import com.htc.lib1.cs.logging.HtcLogger;

/**
 * The dialog is used to present the legal document by given URL.
 */
public class TermDialog extends SelfLogDialog {
    private static final String KEY_TOS_URL = "tosURLHost";
    private static final String KEY_TITLE_RES = "titleResId";
    private HtcLogger mLogger = new AuthLoggerFactory(this).create();
    private OnDismissListener mOnDismissListener;
    private Dialog mProgressDialog;

    /**
     * Create a term dialog with given URL and title.
     * 
     * @param titleResId String resource ID used as the dialog title.
     * @param tosUrl URL to show.
     * @return Instance.
     */
    public static TermDialog newInstance(int titleResId, String tosUrl) {
        if (TextUtils.isEmpty(tosUrl))
            throw new IllegalArgumentException("'tosUrl' is null or empty.");
        if (titleResId <= 0)
            throw new IllegalArgumentException("Invalid 'titleResId': " + titleResId);

        TermDialog dialog = new TermDialog();
        Bundle args = new Bundle();
        args.putString(KEY_TOS_URL, tosUrl);
        args.putInt(KEY_TITLE_RES, titleResId);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String tosUrl = args.getString(KEY_TOS_URL);
        int titleRes = args.getInt(KEY_TITLE_RES);

        // Create progress dialog.
        mProgressDialog = ProgressDialog.newInstance(getActivity(),
                getActivity().getString(R.string.dialog_msg_please_wait));

        // Generate webview.
        WebView webview = createWebView();

        /**
         * Security enhancement for CVE-2014-1939 and CVE-2014-7224. Refer
         * 
         * <pre>
         * http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2014-1939
         * http://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2014-7224
         * https://daoyuan14.github.io/news/newattackvector.html
         * </pre>
         */
        webview.removeJavascriptInterface("searchBoxJavaBridge_");
        webview.removeJavascriptInterface("accessibility");
        webview.removeJavascriptInterface("accessibilityTraversal");

        // Load the page.
        webview.loadUrl(tosUrl);

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(titleRes)
                .setView(webview)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        // Return dialog.
        return dialog;
    }

    /**
     * Set the OnCancelListener.
     * 
     * @param listener OnCancelListener to invoke when progress dialog is
     *            canceled..
     */
    public void setOnDismissListener(OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    @Override
    public void onPause() {
        mLogger.debug("Pause close dialog.");
        super.onPause();
        mProgressDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null) {
            final View view = getDialog().getWindow().getDecorView();
            if (view != null) {
                if (View.VISIBLE == view.getVisibility() && 0 == view.getHeight()) {
                    mLogger.debug("Resume progress dialog.");
                    mProgressDialog.show();
                    // Hide dialog to prevent overlap with progress dialog.
                    setDialogVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void setDialogVisibility(int visibility) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            final View view = getDialog().getWindow().getDecorView();
            if (view != null)
                view.setVisibility(visibility);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    /**
     * Create the customized webview instance which is visible only if the
     * content is ready.
     * 
     * @return {@link WebView}
     */
    private WebView createWebView() {
        final WebView webView = new WebView(getActivity());
        webView.getSettings().setUseWideViewPort(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                mLogger.verboseS(url);

                /*
                 * Dismiss progress dialog and show webview dialog on page
                 * finished.
                 */
                if (isAdded()) {
                    mProgressDialog.dismiss();
                    // Show dialog when progress dialog dismissed.
                    setDialogVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                mLogger.warning("errorCode=", errorCode, ", description=", description);
                mLogger.debugS("failingUrl=", failingUrl);

                /*
                 * Dismiss the progress dialog, show a toast error message and
                 * dismiss the whole webview dialog if opening web page fails.
                 */
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.toast_txt_error_general_failure, Toast.LENGTH_LONG)
                        .show();
                dismiss();
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler,
                    String host, String realm) {
                mLogger.verboseS("Host: ", host, ", Realm: ", realm);
                /*
                 * Handles authentication request if the credentials are
                 * available.
                 */
                String[] credentials = view.getHttpAuthUsernamePassword(host, realm);
                if (credentials != null && credentials.length == 2) {
                    mLogger.debug("Handle http auth request.");
                    handler.proceed(credentials[0], credentials[1]);
                } else {
                    mLogger.error("Could not find satisfied credentials.");
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*
                 * Try to use system browser to open links when user clicks on
                 * it. If failed then fallback to default webview behavior.
                 */
                if (isResumed()) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        startActivity(browserIntent);
                    } catch (ActivityNotFoundException e) {
                        mLogger.error(e);
                        return super.shouldOverrideUrlLoading(webView, url);
                    }
                }else {
                    // Assumed that user cancelled a click-on-link operation.
                    mLogger.warning("Fragment is not resumed when handling URL.");
                    // return true so that WebView won't goto the URL.
                    return true;
                }

                return true;
            }
        });

        return webView;
    }
}
