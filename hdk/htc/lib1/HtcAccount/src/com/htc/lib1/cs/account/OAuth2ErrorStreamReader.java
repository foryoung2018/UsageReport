
package com.htc.lib1.cs.account;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.htc.lib1.cs.account.OAuth2RestServiceException.OAuth2InvalidAuthKeyException;
import com.htc.lib1.cs.account.OAuth2RestServiceException.OAuth2InvalidClientException;
import com.htc.lib1.cs.account.OAuth2RestServiceException.OAuth2InvalidRequestException;
import com.htc.lib1.cs.account.OAuth2RestServiceException.OAuth2InvalidScopesException;
import com.htc.lib1.cs.account.OAuth2RestServiceException.OAuth2UnverifiedUserException;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpErrorStreamReader;
import com.htc.lib1.cs.httpclient.HttpException;
import com.htc.lib1.cs.httpclient.JSONObjectInputStreamReader;

/**
 * The reader of identity server OAuth 2.0 exceptions.
 */
public class OAuth2ErrorStreamReader implements HttpErrorStreamReader {

    private JSONObjectInputStreamReader mJSONReader = new JSONObjectInputStreamReader();

    @Override
    public HttpException readFrom(int statusCode, Map<String, List<String>> responseHeader,
            BufferedInputStream estream) {
        try {
            JSONObject jsonObj = mJSONReader.readFrom(statusCode, responseHeader, estream);
            String error = jsonObj.getString("error");

            // Convert the error to a concrete exception.
            return convertErrorToException(statusCode, error, jsonObj.toString());
        } catch (JSONException e) {
            return new HttpException(statusCode, e.getMessage(), e);
        } catch (IOException e) {
            return new HttpException(statusCode, e.getMessage(), e);
        }
    }

    /**
     * Convert error to a concrete {@link OAuth2RestServiceException} instance.
     * 
     * @param statusCode
     * @param error
     * @param message
     * @return
     */
    private OAuth2RestServiceException convertErrorToException(int statusCode, String error,
            String message) {
        if (OAuth2RestServiceException.INVALID_AUTHKEY.equals(error)) {
            return new OAuth2InvalidAuthKeyException(statusCode, message);
        } else if (OAuth2RestServiceException.INVALID_CLIENT.equals(error)) {
            return new OAuth2InvalidClientException(statusCode, message);
        } else if (OAuth2RestServiceException.INVALID_REQUEST.equals(error)) {
            return new OAuth2InvalidRequestException(statusCode, message);
        } else if (OAuth2RestServiceException.INVALID_SCOPES.equals(error)) {
            return new OAuth2InvalidScopesException(statusCode, message);
        } else if (OAuth2RestServiceException.UNVERIFIED_USER.equals(error)) {
            return new OAuth2UnverifiedUserException(statusCode, message);
        } else {
            return new OAuth2RestServiceException(statusCode, error, message);
        }
    }
}
