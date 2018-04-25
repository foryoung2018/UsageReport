package com.htc.lib2.activeservice.exception;

/**
 * An ActiveRemoteException is thrown when an attempt tries to
 * communicate with active service and a remote exception occurs.
 */

public class ActiveRemoteException extends RuntimeException {
    private static final long serialVersionUID = 3L;
    private final static String MSG_SERVICE_NOT_FOUND = "Remote active service exception.";
    public ActiveRemoteException() {
        super(MSG_SERVICE_NOT_FOUND);
    }
}
