
package com.htc.lib1.cs.push.httputils;

import com.htc.lib1.cs.httpclient.HttpException;

/**
 * Represents a Sense service related exception with HTTP status code, service
 * error code and service error message. Only errors that needs to provide extra
 * information to who handles the exception will be specialized (subclassed),
 * such as InvalidAuthKeyException which indicates the auth-key may be expired..
 * 
 * @author samael_wang@htc.com
 */
public class PnsRestServiceException extends HttpException {
    private static final long serialVersionUID = 1L;
    private PnsServiceErrorCode mErrorCode;

    public PnsRestServiceException(int statusCode, PnsServiceErrorCode errorCode) {
        super(statusCode);
        mErrorCode = errorCode;
    }

    public PnsRestServiceException(int statusCode, PnsServiceErrorCode errorCode, String message) {
        super(statusCode, message);
        mErrorCode = errorCode;
    }

    public PnsRestServiceException(int statusCode, PnsServiceErrorCode errorCode, String message,
                                   Throwable cause) {
        super(statusCode, message, cause);
        mErrorCode = errorCode;
    }

    public PnsServiceErrorCode getErrorCode() {
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
     * Specialized {@code ServiceErrorException} which indicates the auth-key
     * may be expired.
     *
     * @author samael_wang@htc.com
     */
    public static class InvalidAuthKeyException extends PnsRestServiceException {
        private static final long serialVersionUID = 1L;

        public InvalidAuthKeyException(int statusCode, String errorMessage) {
            super(statusCode, PnsServiceErrorCode.InvalidAuthKey, errorMessage);
        }
    }

    /**
     * Specialized {@code ServiceErrorException} which indicates registration
     * not found.
     *
     * @author autosun_li@htc.com
     */
    public static class TargetNotFoundException extends PnsRestServiceException {
        private static final long serialVersionUID = 1L;

        public TargetNotFoundException(int statusCode, String errorMessage) {
            super(statusCode, PnsServiceErrorCode.TargetNotFound, errorMessage);
        }
    }
}
