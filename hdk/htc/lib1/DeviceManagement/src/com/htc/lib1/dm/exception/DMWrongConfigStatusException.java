package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 8/24/14.
 */
public class DMWrongConfigStatusException extends DMException {
    public DMWrongConfigStatusException() {
    }

    public DMWrongConfigStatusException(String detailMessage) {
        super(detailMessage);
    }

    public DMWrongConfigStatusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMWrongConfigStatusException(Throwable throwable) {
        super(throwable);
    }
}
