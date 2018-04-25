
package com.htc.lib1.cs.workflow;

/**
 * The root of all unexpected exceptions that the model doesn't know how to
 * handle with. It's usually unrecoverable and in most case will cause an error
 * message then stop.
 */
public class UnexpectedException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnexpectedException(String message) {
        super(message);
    }

    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

}
