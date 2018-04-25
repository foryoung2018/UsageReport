
package com.htc.lib1.cs.account;

import android.content.Intent;

/**
 * Indicating user authentication fails and it needs to initiate UI flows to
 * recover the error.
 */
public class RecoverableAuthenticationFailureException extends Exception {
    private static final long serialVersionUID = 1L;
    private Intent mIntent;

    public RecoverableAuthenticationFailureException(Intent intent) {
        super("User interaction required to recover.");
        mIntent = intent;
    }

    public RecoverableAuthenticationFailureException(Intent intent, Throwable cause) {
        super("User interaction required to recover.", cause);
        mIntent = intent;
    }

    /**
     * Get the intent to start UI flows to recover the error.
     * 
     * @return Intent.
     */
    public Intent getIntent() {
        return mIntent;
    }
}
