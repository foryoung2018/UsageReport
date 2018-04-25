package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 8/26/14.
 */
public class DMJsonParseException extends DMException {
    public DMJsonParseException() {
    }

    public DMJsonParseException(String detailMessage) {
        super(detailMessage);
    }

    public DMJsonParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMJsonParseException(Throwable throwable) {
        super(throwable);
    }
}
