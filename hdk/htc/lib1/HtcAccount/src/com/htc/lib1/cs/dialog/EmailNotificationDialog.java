
package com.htc.lib1.cs.dialog;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.HtcAccountManager;
import com.htc.lib1.cs.account.HtcAccountManagerCreator;
import com.htc.lib1.cs.account.restservice.ProfileResource;
import com.htc.lib1.cs.account.server.HtcAccountServerHelper;
import com.htc.lib1.cs.app.ProgressDialog;
import com.htc.lib1.cs.app.SelfLogActivity;
import com.htc.lib1.cs.app.SelfLogDialog;
import com.htc.lib1.cs.auth.R;
import com.htc.lib1.cs.auth.client.EmailAccountUtils;
import com.htc.lib1.cs.httpclient.ConnectionException;
import com.htc.lib1.cs.httpclient.ConnectivityException;
import com.htc.lib1.cs.widget.LinkableTextView;

import static com.htc.lib1.cs.account.HtcAccountDefs.KEY_AUTH_SERVER_URI;
import static com.htc.lib1.cs.account.HtcAccountDefs.KEY_COUNTRY_CODE;

/**
 * Dialog to instruct user verifying their email address, shows when user
 * presses the grace period status bar notification.
 */
public class EmailNotificationDialog extends SelfLogDialog {
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AUTH_KEY = "authKey";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_SERVICE_URI = "serviceUri";

    private String mAuthKey;
    private String mEmailAddress;
    private Account mTargetAccount;
    private String mServiceUri;

    private ResendVerificationEmailTask mResendVerificationEmailTask = null;

    /**
     * @param emailAddress Email address.
     * @param authKey Could be {@code null}.
     * @return Dialog.
     */
    public static EmailNotificationDialog newInstance(String emailAddress,
                                                      String authKey,
                                                      Account account,
                                                      String serviceUri) {
        EmailNotificationDialog dialog = new EmailNotificationDialog();
        Bundle args = new Bundle();
        args.putString(KEY_EMAIL, emailAddress);
        args.putString(KEY_AUTH_KEY, authKey);
        args.putParcelable(KEY_ACCOUNT, account);
        args.putString(KEY_SERVICE_URI, serviceUri);
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mEmailAddress = args.getString(KEY_EMAIL);
            mAuthKey = args.getString(KEY_AUTH_KEY);
            mTargetAccount = args.getParcelable(KEY_ACCOUNT);
            mServiceUri = args.getString(KEY_SERVICE_URI);
        }

        // Compose dialog.
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_email_verification)
                .setView(getMessageView(mEmailAddress))
                .setPositiveButton(R.string.dialog_btn_open_mail, new OnOpenMailClickListener())
                .setNegativeButton(R.string.dialog_btn_later, new OnLaterClickListener())
                .create();
        return dialog;
    }

    private View getMessageView(String email) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.htcaccount_dialog_email_verify, null);

        LinkableTextView textMessage = (LinkableTextView) layout.findViewById(R.id.txt_verify_email);

        // Compose description message.
        if (textMessage != null) {
            textMessage.setLinkableText(R.string.dialog_msg_email_verification,
                    new LinkableTextView.LinkText(email, null),
                    new LinkableTextView.LinkText(getString(R.string.dialog_link_resend),
                            new OnResendClickListener()));
        }

        return layout;
    }

    /*
     * Handler for "Open mail" button.
     */
    private class OnOpenMailClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            /*
             * When user presses open mail, open the mailbox.
             */

            Intent mailIntent = EmailAccountUtils.getEmailActivityIntent(getActivity(),
                    mEmailAddress);

            try {
                getActivity().startActivity(mailIntent);
            } catch (Exception e) {
                /*
                 * On non-HTC devices the mail intent might be invalid.
                 */
                mLogger.error(e);
            } finally {
                /*
                 * In either case, dismiss the dialog and finish the activity if caller
                 * requested.
                 */
                dialog.dismiss();
                getActivity().finish();
            }
        }
    }

    /**
     * Handler for "Later" button.
     */
    private class OnLaterClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            /*
             * When user presses later, dismiss the dialog and, if caller
             * requested, finish the activity.
             */

            dismiss();
            getActivity().finish();
        }
    }

    /**
     * Handler for "Resend" link.
     */
    private class OnResendClickListener extends ClickableSpan {
        /**
         * Remove underline but ensure link color.
         */
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            mLogger.debug();
            if (mResendVerificationEmailTask == null) {
                mResendVerificationEmailTask = new ResendVerificationEmailTask();
                mResendVerificationEmailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    private class ResendVerificationEmailTask extends AsyncTask<Void, Void, Boolean> {
        Dialog mProgressDialog = null;
        boolean mIsNetworkError = false;

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.newInstance(getActivity(),
                    getString(R.string.dialog_msg_please_wait));
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mLogger.debug();
            Context context = getActivity().getApplicationContext();
            try {
                ProfileResource resource = new ProfileResource(
                        getActivity().getApplicationContext(), mServiceUri, mAuthKey,
                        context.getPackageName());
                resource.resendVerifyEmail(mEmailAddress);
            } catch (ConnectionException e) {
                mLogger.warning("Connection error: ", e);
                mIsNetworkError = true;
            } catch (ConnectivityException e) {
                mLogger.warning("Connectivity error: ", e);
                mIsNetworkError = true;
            } catch (Exception e) {
                mLogger.warning("Failed to send verification email: ", e);
                return Boolean.FALSE;
            }
            mLogger.debug("Success");
            return Boolean.TRUE;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mLogger.debug();
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            Activity activity = getActivity();
            if (isCancelled() || activity.isFinishing()) {
                return;
            }
            if (activity instanceof SelfLogActivity) {
                if (((SelfLogActivity) activity).isDestroyedCompact()) {
                    return;
                }
            }

            String message;
            if (result) {
                message = getResources().getString(R.string.toast_txt_email_sent_to_tip, mEmailAddress);
            } else if (mIsNetworkError) {
                message = getResources().getString(R.string.toast_txt_error_network_unavailable);
            } else {
                message = getResources().getString(R.string.toast_txt_error_general_failure);
            }
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

            mResendVerificationEmailTask = null;
        }
    }
}
