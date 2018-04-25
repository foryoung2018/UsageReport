
package com.htc.lib1.cs.account.restservice;

import com.htc.lib1.cs.account.HtcAccountRestServiceException;
import com.htc.lib1.cs.account.HtcAccountServiceErrorCode;

/**
 * Indicates user to use trusted devices.
 */
public class SendToBackUpEmailException extends HtcAccountRestServiceException {
    private static final long serialVersionUID = 1L;

    private String mBackUpEmailAddress = null;

    public SendToBackUpEmailException(int statusCode, String backUpEmailAddress, String errMessage) {
        super(statusCode, HtcAccountServiceErrorCode.SendToBackupEmail, errMessage);

        mBackUpEmailAddress = backUpEmailAddress;
    }

    public String getBackUpEmailAddress() {
        return mBackUpEmailAddress;
    }
}
