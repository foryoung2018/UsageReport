
package com.htc.lib1.cs.pns;

import com.htc.lib1.cs.httpclient.HttpException;

/**
 * Handle HTTP exceptions with service specific return value for client side of PNS.
 *
 * @author ted_hsu@htc.com
 */
public class PnsServiceUnavailableException extends HttpException {
    private static final long serialVersionUID = 1L;
    private PnsServiceErrorCode mErrorCode;
    private int mRetryAfterValueInSec = PnsDefs.DEFAULT_RETRY_AFTER_VALUE_IN_SEC;

    public PnsServiceUnavailableException(int statusCode, PnsServiceErrorCode errorCode, int retryAfterValueInSec) {
        super(statusCode);
        mErrorCode = errorCode;
        mRetryAfterValueInSec = retryAfterValueInSec;
    }

    public PnsServiceUnavailableException(int statusCode, PnsServiceErrorCode errorCode, String message, int retryAfterValueInSec) {
        super(statusCode, message);
        mErrorCode = errorCode;
        mRetryAfterValueInSec = retryAfterValueInSec;
    }

    public PnsServiceUnavailableException(int statusCode, PnsServiceErrorCode errorCode, String message,
                                          Throwable cause, int retryAfterValueInSec) {
        super(statusCode, message, cause);
        mErrorCode = errorCode;
        mRetryAfterValueInSec = retryAfterValueInSec;
    }

    public PnsServiceErrorCode getErrorCode() {
        return mErrorCode;
    }

    public int getRetryAfterValueInSec() {
        return mRetryAfterValueInSec;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                .append(" {Code=\"").append(mErrorCode.toString())
                .append("(").append(mErrorCode.getValue())
                .append(")\", ErrorString=\"").append(getMessage())
                .append("\" retryAfterValue=\"").append(mRetryAfterValueInSec)
                .append("\"}")
                .toString();
    }
}
