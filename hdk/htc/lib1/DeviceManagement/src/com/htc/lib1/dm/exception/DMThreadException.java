package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 8/26/14.
 */
public class DMThreadException extends DMException {
    public DMThreadException() {
    }

    public DMThreadException(String detailMessage) {
        super(detailMessage);
    }

    public DMThreadException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMThreadException(Throwable throwable) {
        super(throwable);
    }
}
