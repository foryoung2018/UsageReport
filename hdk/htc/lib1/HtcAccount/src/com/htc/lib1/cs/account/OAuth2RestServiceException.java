
package com.htc.lib1.cs.account;

import com.htc.lib1.cs.httpclient.HttpException;

/**
 * General representation of identity server OAuth 2.0 exceptions.
 * 
 * @author samael_wang
 */
public class OAuth2RestServiceException extends HttpException {
    /**
     * The AuthKey is invalid, resource owner or authorization server denied
     * request.
     */
    public static final String INVALID_AUTHKEY = "access_denied";

    /**
     * client authentication failed.
     */
    public static final String INVALID_CLIENT = "invalid_client";

    /**
     * Missing required parameter or invalid parameter value.
     */
    public static final String INVALID_REQUEST = "invalid_request";

    /**
     * The scopes are invalid, unknown or malformed.
     */
    public static final String INVALID_SCOPES = "invalid_scope";

    /**
     * User email is unverified.
     */
    public static final String UNVERIFIED_USER = "unverified_user";

    private static final long serialVersionUID = 2L;
    private String mErrorString;

    public OAuth2RestServiceException(int statusCode, String error, String message) {
        super(statusCode, message);
        mErrorString = error;
    }

    public String getErrorString() {
        return mErrorString;
    }

    /**
     * Specialized {@link OAuth2RestServiceException} for
     * {@link OAuth2RestServiceException#INVALID_AUTHKEY}.
     * 
     * @author samael_wang
     */
    public static class OAuth2InvalidAuthKeyException extends OAuth2RestServiceException {
        private static final long serialVersionUID = 2L;

        public OAuth2InvalidAuthKeyException(int statusCode, String message) {
            super(statusCode, INVALID_AUTHKEY, message);
        }
    }

    /**
     * Specialized {@link OAuth2RestServiceException} for
     * {@link OAuth2RestServiceException#INVALID_CLIENT}.
     * 
     * @author samael_wang
     */
    public static class OAuth2InvalidClientException extends OAuth2RestServiceException {
        private static final long serialVersionUID = 2L;

        public OAuth2InvalidClientException(int statusCode, String message) {
            super(statusCode, INVALID_CLIENT, message);
        }
    }

    /**
     * Specialized {@link OAuth2RestServiceException} for
     * {@link OAuth2RestServiceException#INVALID_REQUEST}.
     * 
     * @author samael_wang
     */
    public static class OAuth2InvalidRequestException extends OAuth2RestServiceException {
        private static final long serialVersionUID = 2L;

        public OAuth2InvalidRequestException(int statusCode, String message) {
            super(statusCode, INVALID_REQUEST, message);
        }
    }

    /**
     * Specialized {@link OAuth2RestServiceException} for
     * {@link OAuth2RestServiceException#INVALID_SCOPES}.
     * 
     * @author samael_wang
     */
    public static class OAuth2InvalidScopesException extends OAuth2RestServiceException {
        private static final long serialVersionUID = 2L;

        public OAuth2InvalidScopesException(int statusCode, String message) {
            super(statusCode, INVALID_SCOPES, message);
        }
    }

    /**
     * Specialized {@link OAuth2RestServiceException} for
     * {@link OAuth2RestServiceException#UNVERIFIED_USER}.
     * 
     * @author autosun_li
     */
    public static class OAuth2UnverifiedUserException extends OAuth2RestServiceException {
        private static final long serialVersionUID = 2L;

        public OAuth2UnverifiedUserException(int statusCode, String message) {
            super(statusCode, UNVERIFIED_USER, message);
        }
    }
}
