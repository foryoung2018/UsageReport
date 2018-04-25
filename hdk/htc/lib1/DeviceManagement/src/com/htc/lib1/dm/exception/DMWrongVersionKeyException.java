package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 8/27/14.
 */
public class DMWrongVersionKeyException extends DMException {
    public DMWrongVersionKeyException() {
    }

    public DMWrongVersionKeyException(String detailMessage) {
        super(detailMessage);
    }

    public DMWrongVersionKeyException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMWrongVersionKeyException(Throwable throwable) {
        super(throwable);
    }
}
