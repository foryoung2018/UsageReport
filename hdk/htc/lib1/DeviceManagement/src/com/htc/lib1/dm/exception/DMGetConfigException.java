package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 8/24/14.
 */
public class DMGetConfigException extends DMException {
    public DMGetConfigException() {
    }

    public DMGetConfigException(String detailMessage) {
        super(detailMessage);
    }

    public DMGetConfigException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMGetConfigException(Throwable throwable) {
        super(throwable);
    }
}
