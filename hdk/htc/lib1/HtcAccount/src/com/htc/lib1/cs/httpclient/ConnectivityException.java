
package com.htc.lib1.cs.httpclient;

/**
 * Indicates the device has no proper connectivity to perform network
 * operations.
 */
public class ConnectivityException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConnectivityException() {
        super("Data network is not available.");
    }

    public ConnectivityException(String message, Throwable cause) {
        super(message, cause);
    }

}
