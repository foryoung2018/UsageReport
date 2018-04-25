package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 3/13/15.
 */
public class DMFatalException extends DMException {
    public DMFatalException() {
    }

    public DMFatalException(String detailMessage) {
        super(detailMessage);
    }

    public DMFatalException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMFatalException(Throwable throwable) {
        super(throwable);
    }
}
