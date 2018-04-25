
package com.htc.lib1.cs.account;

import com.htc.lib1.cs.httpclient.HttpException;

/**
 * Represents HTC Account REST service related exception with HTTP status code,
 * service error code and service error message. Only errors that needs to
 * provide extra information to who handles the exception will be specialized
 * (subclassed), such as WrongDataCenterException which provides correct data
 * center address.
 * 
 * @author samael_wang@htc.com
 */
public class HtcAccountRestServiceException extends HttpException {

    private static final long serialVersionUID = 1L;
    private HtcAccountServiceErrorCode mErrorCode;

    public HtcAccountRestServiceException(int statusCode, HtcAccountServiceErrorCode errorCode) {
        super(statusCode);
        mErrorCode = errorCode;
    }

    public HtcAccountRestServiceException(int statusCode, HtcAccountServiceErrorCode errorCode,
            String message) {
        super(statusCode, message);
        mErrorCode = errorCode;
    }

    public HtcAccountRestServiceException(int statusCode, HtcAccountServiceErrorCode errorCode,
            String message,
            Throwable cause) {
        super(statusCode, message, cause);
        mErrorCode = errorCode;
    }

    public HtcAccountServiceErrorCode getErrorCode() {
        return mErrorCode;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                .append(" {Code=\"").append(mErrorCode.toString())
                .append("(").append(mErrorCode.getValue())
                .append(")\", ErrorString=\"").append(getMessage())
                .append("\"}").toString();
    }

    /**
     * Specialized {@link HtcAccountRestServiceException} which provides correct
     * data center address.
     * 
     * @author samael_wang@htc.com
     */
    public static class WrongDataCenterException extends HtcAccountRestServiceException {
        private static final long serialVersionUID = 1L;

        public WrongDataCenterException(int statusCode, String dataCenter) {
            super(statusCode, HtcAccountServiceErrorCode.WrongDataCenter, dataCenter);
        }

        /**
         * Get the correct data center address.
         * 
         * @return Data center URI.
         */
        public String getDataCenterAddress() {
            return getMessage();
        }
    }

    /**
     * Specialized {@link HtcAccountRestServiceException} for
     * {@link HtcAccountServiceErrorCode#TokenExpired}, as it's the most common
     * error integrated apps need to catch.
     * 
     * @author samael_wang@htc.com
     */
    public static class TokenExpiredException extends HtcAccountRestServiceException {
        private static final long serialVersionUID = 1L;

        public TokenExpiredException(int statusCode, String errorMsg) {
            super(statusCode, HtcAccountServiceErrorCode.TokenExpired, errorMsg);
        }
    }
}
