package com.htc.lib1.cs.account;

import android.content.Context;

import net.grandcentrix.tray.TrayPreferences;

class AppSignInPreference extends TrayPreferences {
    private static final String PREF_NAME_POSTFIX = "_htc_account";
    private static final String KEY_IS_APP_SIGN_IN = "isAppSignIn";
    private static final String KEY_ACCOUNT_TAG = "accountTag";

    AppSignInPreference(final Context context, String moduleName) {
        super(context, moduleName + PREF_NAME_POSTFIX, 1);
    }

    void setSignedIn(boolean signIn) {
        put(KEY_IS_APP_SIGN_IN, signIn);
    }

    boolean isSignedIn(boolean defaultValue) {
        return getBoolean(KEY_IS_APP_SIGN_IN, defaultValue);
    }

    void setTag(String tag) {
        put(KEY_ACCOUNT_TAG, tag);
    }

    String getTag() {
        return getString(KEY_ACCOUNT_TAG, null);
    }
}
