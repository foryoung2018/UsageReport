package com.htc.lib2.activeservice.exception;

/**
 * An ActiveSecurityException is thrown when an attempt tries to call
 * {@link com.htc.lib2.activeservice.HtcActiveManager#enableWithPermission()} or
 * {@link com.htc.lib2.activeservice.HtcActiveManager#disableWithPermission()}
 * without granted permission.
 */

public class ActiveSecurityException extends RuntimeException {

    private static final long serialVersionUID = 4L;
    private final static String MSG_NOT_ENOUGH_PERMISSION = "Permission denial. Your permission is not granted";
    public ActiveSecurityException() {
        super(MSG_NOT_ENOUGH_PERMISSION);
    }
}
