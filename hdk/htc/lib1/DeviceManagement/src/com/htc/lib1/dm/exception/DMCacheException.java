package com.htc.lib1.dm.exception;

/**
 * Created by Joe_Wu on 8/31/14.
 */
public class DMCacheException extends DMException {
    public DMCacheException() {
    }

    public DMCacheException(String detailMessage) {
        super(detailMessage);
    }

    public DMCacheException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DMCacheException(Throwable throwable) {
        super(throwable);
    }
}
