
package com.htc.lib1.cs.httpclient;

/**
 * Indicates an error occurs when trying to make a HTTP/HTTPS request to the
 * remote server.
 */
public class ConnectionException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
