
package com.htc.lib1.cs.account;

import android.accounts.AccountsException;

/**
 * Indicates no HTC Account presents on the system while trying to operate on an
 * HTC Account.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountNotExistsException extends AccountsException {
    private static final long serialVersionUID = 1L;

    public HtcAccountNotExistsException() {
        super("No HTC Account presents on the system.");
    }

}
