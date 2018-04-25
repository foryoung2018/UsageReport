
package com.htc.lib1.cs.push.exception;

import com.htc.lib1.cs.workflow.UnexpectedException;

/**
 * Represents the registration (either to push provider or to htc server)
 * failed. 
 */
public class RegistrationFailedException extends UnexpectedException {
    private static final long serialVersionUID = 1L;

    public RegistrationFailedException(String message) {
        super(message);
    }

    public RegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
