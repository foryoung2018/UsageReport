package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 8/22/14.
 */
public class DMException extends Exception {

    public DMException() {
    }

    public DMException(String detailMessage) {
        super(detailMessage);
    }

    public DMException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMException(Throwable throwable) {
        super(throwable);
    }

}
