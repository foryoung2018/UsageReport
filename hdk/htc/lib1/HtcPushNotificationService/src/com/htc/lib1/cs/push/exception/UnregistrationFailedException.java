
package com.htc.lib1.cs.push.exception;

import com.htc.lib1.cs.workflow.UnexpectedException;

/**
 * Represents the unregistration (either to push provider or to htc server)
 * failed.
 */
public class UnregistrationFailedException extends UnexpectedException {
    private static final long serialVersionUID = 1L;

    public UnregistrationFailedException(String message) {
        super(message);
    }

    public UnregistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
