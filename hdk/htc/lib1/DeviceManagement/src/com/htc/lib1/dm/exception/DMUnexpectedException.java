package com.htc.lib1.dm.exception;

public class DMUnexpectedException extends DMException {

    public DMUnexpectedException() {
    }

    public DMUnexpectedException(String detailMessage) {
        super(detailMessage);
    }

    public DMUnexpectedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMUnexpectedException(Throwable throwable) {
        super(throwable);
    }

}
