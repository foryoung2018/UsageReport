
package com.htc.lib1.cs.account.restservice;

import com.htc.lib1.cs.account.restobj.ConfirmAccountInfo;
import com.htc.lib1.cs.account.HtcAccountRestServiceException;
import com.htc.lib1.cs.account.HtcAccountServiceErrorCode;

/**
 * Specialized {@link HtcAccountRestServiceException} which indicates that it
 * needs confirm for account creating.
 * 
 * @author autosun_li@htc.com
 */
public class ConfirmRequiredException extends HtcAccountRestServiceException {
    private static final long serialVersionUID = 1L;

    private ConfirmAccountInfo mConfirmAccount;

    public ConfirmRequiredException(int statusCode, ConfirmAccountInfo confirmAccount,
                                    String errMessage) {
        super(statusCode, HtcAccountServiceErrorCode.ConfirmRequired, errMessage);
        mConfirmAccount = confirmAccount;
    }

    public ConfirmAccountInfo getConfirmAccount() {
        return mConfirmAccount;
    }
}
