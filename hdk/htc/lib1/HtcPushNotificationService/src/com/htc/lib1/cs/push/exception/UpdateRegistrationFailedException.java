
package com.htc.lib1.cs.push.exception;

import com.htc.lib1.cs.workflow.UnexpectedException;

/**
 * Represents the update registration (either to push provider or to htc server)
 * failed.
 */
public class UpdateRegistrationFailedException extends UnexpectedException {
    private static final long serialVersionUID = 1L;

    public UpdateRegistrationFailedException(String message) {
        super(message);
    }

    public UpdateRegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
