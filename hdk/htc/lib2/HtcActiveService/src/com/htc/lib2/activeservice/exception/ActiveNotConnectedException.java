package com.htc.lib2.activeservice.exception;


/**
 * An ActiveNotConnectedException is thrown when an attempt tries to
 * communicate with active service while the connection is lost.
 */

public class ActiveNotConnectedException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final static String MSG_SERVICE_NOT_CONNECTED = "Active service is disconnected.";
    public ActiveNotConnectedException() {
        super(MSG_SERVICE_NOT_CONNECTED);
    }
}
