
package com.htc.lib1.cs.account.restservice;

import com.htc.lib1.cs.account.HtcAccountRestServiceException;
import com.htc.lib1.cs.account.HtcAccountServiceErrorCode;

/**
 * Specialized {@link HtcAccountRestServiceException} which provides captcha
 * image key and url.
 * 
 * @author autosun_li@htc.com
 */
public class BadCaptchaException extends HtcAccountRestServiceException {
    private static final long serialVersionUID = 1L;

    private String mImageKey;
    private String mImageUrl;

    public BadCaptchaException(int statusCode, String captchaImageKey, String captchaImageUrl,
                               String errMessage) {
        super(statusCode, HtcAccountServiceErrorCode.BadCaptcha, errMessage);
        mImageKey = captchaImageKey;
        mImageUrl = captchaImageUrl;
    }

    public String getCaptchaImageKey() {
        return mImageKey;
    }

    public String getCaptchaImageUrl() {
        return mImageUrl;
    }
}
