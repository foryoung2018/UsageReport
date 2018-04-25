package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 1/5/15.
 */
public class DMNoKeyInConfigException extends DMException {
    public DMNoKeyInConfigException() {
    }

    public DMNoKeyInConfigException(String detailMessage) {
        super(detailMessage);
    }

    public DMNoKeyInConfigException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMNoKeyInConfigException(Throwable throwable) {
        super(throwable);
    }
}
