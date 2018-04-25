
package com.htc.lib1.cs.account.restservice;

import com.htc.lib1.cs.account.HtcAccountRestServiceException;
import com.htc.lib1.cs.account.HtcAccountServiceErrorCode;

/**
 * Indicates user to choose request password approach.
 */
public class ChooseAndConfirmException extends HtcAccountRestServiceException {
    /**
     * 
     */
    private static final long serialVersionUID = 111671771177099613L;
    
    private String mCountryCode = null;
    private String mNationalNumber = null;
    private String mBackUpEmailAddress = null;
    private String mCaptchaNonce = null;
    
    public ChooseAndConfirmException(int statusCode, String countryCode, String nationalNumber,
                                     String backUpEmailAddress, String captchaNonce, String errMessage) {
        super(statusCode, HtcAccountServiceErrorCode.NeedChooseAndConfirm, errMessage);
        
        mCountryCode = countryCode;
        mNationalNumber = nationalNumber;
        mBackUpEmailAddress = backUpEmailAddress;
        mCaptchaNonce = captchaNonce;
    }
    
    public String getCountryCode() {
        return mCountryCode;
    }

    public String getNationalNumber() {
        return mNationalNumber;
    }

    public String getBackUpEmailAddress() {
        return mBackUpEmailAddress;
    }

    public String getCaptchaNonce() {
        return mCaptchaNonce;
    }
}
