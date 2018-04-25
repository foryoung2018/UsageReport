package com.htc.lib2.activeservice.exception;

/**
 * This exception is thrown when a call to {@link com.htc.lib2.activeservice.HtcActiveManager#connect(ServiceConnectionListener listener)}
 * fails because the active service can not be found.
 */

public class ActiveNotFoundException extends Exception {
    private static final long serialVersionUID = 2L;
    private final static String MSG_SERVICE_NOT_FOUND = "Active service cannot be found";
    public ActiveNotFoundException() {
        super(MSG_SERVICE_NOT_FOUND);
    }
}
