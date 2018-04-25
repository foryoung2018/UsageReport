
package com.htc.lib1.cs.httpclient;

import java.io.IOException;

/**
 * Represents exceptions when parsing response string into objects. REST
 * services raises {@code ParseResponseException} when the REST call successed
 * (indicated by HTTP status code) but not able to parse the response string. It
 * could be caught and ignored in case the response is not important.
 */
public class ParseResponseException extends IOException {

    private static final long serialVersionUID = 1L;

    public ParseResponseException(String message) {
        super(message);
    }

    public ParseResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
