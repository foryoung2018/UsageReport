package com.htc.lib1.cs.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.lib1.cs.account.HtcAccountDefs;
import com.htc.lib1.cs.account.restobj.ConfirmAccountInfo;
import com.htc.lib1.cs.auth.R;
import com.htc.lib1.cs.widget.LinkableTextView;

import org.apache.commons.validator.routines.EmailValidator;

public class ConfirmAccountActivity extends SelfLogActivity {
    public static final String KEY_RESULT_SECOND_EMAIL = "com.htc.lib1.cs.KEY_RESULT_EMAIL";
    public static final String KEY_RESULT_NEWSLETTER_ON = "com.htc.lib1.cs.KEY_RESULT_NEWSLETTER_ON";

    private static final String KEY_ACCOUNT_TYPE = "com.htc.lib1.cs.ACCOUNT_TYPE";
    private static final String KEY_ASSOCIATED_ACCOUNT = "com.htc.lib1.cs.ASSOCIATED_ACCOUNT";
    private static final String KEY_ACCOUNT_NAME = "com.htc.lib1.cs.CONFIRM_ACCOUNT_NAME";
    private static final String KEY_ACCOUNT_SOCIAL_UID = "com.htc.lib1.cs.CONFIRM_ACCOUNT_SOCIAL_UID";
    private static final String KEY_NEWSLETTER_DEFAULT_ON = "com.htc.lib1.cs.NEWSLETTER_DEFAULT_ON";

    private static final String TYPE_SIGN_IN_GOOGLE = "signInGoogle";

    public static Intent createIntentForGoogleAccount(
            Context context,
            ConfirmAccountInfo accountInfo,
            String accountName,
            String googleUid,
            boolean newsletterDefaultOn) {
        Intent intent = new Intent(context, ConfirmAccountActivity.class);
        intent.putExtra(KEY_ACCOUNT_TYPE, TYPE_SIGN_IN_GOOGLE);
        intent.putExtra(KEY_ASSOCIATED_ACCOUNT, accountInfo);
        intent.putExtra(KEY_ACCOUNT_NAME, accountName);
        intent.putExtra(KEY_ACCOUNT_SOCIAL_UID, googleUid);
        intent.putExtra(KEY_NEWSLETTER_DEFAULT_ON, newsletterDefaultOn);
        return intent;
    }

    private String mAccountType = null;
    private ConfirmAccountInfo mAccountInfo = null;
    //private String mAccountName = null;
    //private String mSocialUID = null;
    private boolean mIsNewsLetterDefaultOn = false;
    private boolean mUseAccountEmail = false;
    private String mSecondEmail = null;

    private TextView mTextEmail;
    private TextView mTextEmailTips;
    private AutoCompleteTextView mEditTextEmail;
    private CheckBox mNewsOption;
    private View mItemOtherAccount;
    //private LinearLayout mOtherAccountlist;
    private LinkableTextView mLinkableTextLegal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htcaccount_activity_confirm_account);

        if (savedInstanceState != null) {
            mLogger.info("Cancel sign-in flow since process has been killed");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        Intent intent = getIntent();
        mAccountType = intent.getStringExtra(KEY_ACCOUNT_TYPE);
        mAccountInfo = (ConfirmAccountInfo) intent.getSerializableExtra(KEY_ASSOCIATED_ACCOUNT);
        //mAccountName = intent.getStringExtra(KEY_ACCOUNT_NAME);
        //mSocialUID = intent.getStringExtra(KEY_ACCOUNT_SOCIAL_UID);
        mIsNewsLetterDefaultOn = intent.getBooleanExtra(KEY_NEWSLETTER_DEFAULT_ON, false);

        mTextEmailTips = (TextView) findViewById(R.id.txt_social_email_tips);
        mTextEmail = (TextView) findViewById(R.id.txt_social_email);
        mEditTextEmail = (AutoCompleteTextView) findViewById(R.id.input_username);
        mNewsOption = (CheckBox) findViewById(R.id.checkbox_newsletter);
        mItemOtherAccount = findViewById(R.id.item_other_accounts);
        //mOtherAccountlist = (LinearLayout) findViewById(R.id.list_other_accounts);
        mLinkableTextLegal = (LinkableTextView) findViewById(R.id.txt_legal_tips);

        String tipsSocialEmail = String.format(getString(R.string.txt_social_email_tips),
                getSocialTypeString());
        mTextEmailTips.setText(tipsSocialEmail);
        String accountEmail = null;
        if (mAccountInfo != null && mAccountInfo.account != null) {
            accountEmail = mAccountInfo.account.email;
        }
        mTextEmail.setText(accountEmail);

        mUseAccountEmail = EmailValidator.getInstance().isValid(accountEmail);
        if (mUseAccountEmail) {
            // Set invisible when email is valid.
            mEditTextEmail.setVisibility(View.GONE);
        } else {
            // Set invisible when email is not valid.
            findViewById(R.id.item_social_email_tips).setVisibility(View.GONE);
        }

        mNewsOption.setChecked(mIsNewsLetterDefaultOn);
        if (HtcAccountDefs.TYPE_SIGN_IN.equals(mAccountType)) {
            findViewById(R.id.item_newsletter).setVisibility(View.GONE);
        }

        // Do not support account choosing for now. Always create new account.
        mItemOtherAccount.setVisibility(View.GONE);
        //if (mAccountInfo != null && mAccountInfo.associatedAccount != null) {
        //    for (ConfirmAccountInfo.AccountInfo acc : mAccountInfo.associatedAccount) {
        //        String accountType = acc.provider;
        //        String accountName = acc.email;
        //        mLogger.debugS("Associated account type=" + accountType + ", name=" + accountName);
        //
        //        View listitem = View.inflate(this, R.layout.htcaccount_layout_listitem1, null);
        //        ImageView iconView = (ImageView) listitem.findViewById(R.id.icon_view);
        //        iconView.setImageDrawable(getIcon(accountType));
        //        TextView txtView = (TextView) listitem.findViewById(R.id.text_line1);
        //        txtView.setText(accountName);
        //        listitem.setTag(R.id.htc_lib1_cs_associatedAccount, acc);
        //        listitem.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                ConfirmAccountInfo.AccountInfo acc = (ConfirmAccountInfo.AccountInfo)
        //                        view.getTag(R.id.htc_lib1_cs_associatedAccount);
        //                signInWithAnotherAccount(acc.provider, acc.email);
        //            }
        //        });
        //        mOtherAccountlist.addView(listitem);
        //    }
        //
        //    // Update create account button text.
        //    ((Button)findViewById(R.id.btn_create_account)).setText(R.string.btn_create_new_account);
        //} else {
        //    mItemOtherAccount.setVisibility(View.GONE);
        //}

        mLinkableTextLegal.setLinkableText(R.string.txt_sign_up_legal_tips,
                new LinkableTextView.LinkText(
                        getString(R.string.txt_sign_up_legal_tips_tos_link),
                        new LegalTipsTosClickableSpan(this)),
                new LinkableTextView.LinkText(
                        getString(R.string.txt_sign_up_legal_tips_privacy_link),
                        new LegalTipsPrivacyClickableSpan(this)));
    }

    /**
     * Newsletter text view click event.
     *
     * @param v Focused {@link View}.
     */
    public void onSubscribeNewsClick(View v) {
        mLogger.verbose();
        mNewsOption.setChecked(!mNewsOption.isChecked());
    }

    /**
     * btn_create.onClick
     *
     * @param v Focused {@link View}.
     */
    public void onBtnCreateAccountClick(View v) {
        // Hide IME before start creating account.
        mLogger.verbose();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        switch (mAccountType) {
            case HtcAccountDefs.TYPE_SIGN_IN_GOOGLE:
                if (socialAccountCommonCheck()) {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_RESULT_NEWSLETTER_ON, mNewsOption.isChecked());
                    intent.putExtra(KEY_RESULT_SECOND_EMAIL, mSecondEmail);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    private String getSocialTypeString() {
        switch (mAccountType) {
            case TYPE_SIGN_IN_GOOGLE:
                return "Google";
            default:
                return "";
        }
    }

    private boolean socialAccountCommonCheck() {
        if (!mUseAccountEmail) {
            String email = mEditTextEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, R.string.toast_txt_provide_email, Toast.LENGTH_LONG)
                        .show();
                return false;
            }
            if (!EmailValidator.getInstance().isValid(email)) {
                Toast.makeText(this, R.string.toast_txt_error_email_incorrect, Toast.LENGTH_LONG)
                        .show();
                return false;
            }
            mSecondEmail = email;
        }

        return true;
    }
}
