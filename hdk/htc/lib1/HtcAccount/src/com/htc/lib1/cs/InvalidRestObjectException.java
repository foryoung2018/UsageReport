
package com.htc.lib1.cs;

/**
 * Indicates the content of the {@link RestObject} instance is not valid.
 * 
 * @author samael_wang
 */
public class InvalidRestObjectException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidRestObjectException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidRestObjectException(Throwable throwable) {
        super(throwable);
    }

    public InvalidRestObjectException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
