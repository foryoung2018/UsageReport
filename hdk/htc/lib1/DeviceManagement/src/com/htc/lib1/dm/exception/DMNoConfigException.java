package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 8/23/14.
 */
public class DMNoConfigException extends DMException {
    public DMNoConfigException() {
    }

    public DMNoConfigException(String detailMessage) {
        super(detailMessage);
    }

    public DMNoConfigException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMNoConfigException(Throwable throwable) {
        super(throwable);
    }
}
