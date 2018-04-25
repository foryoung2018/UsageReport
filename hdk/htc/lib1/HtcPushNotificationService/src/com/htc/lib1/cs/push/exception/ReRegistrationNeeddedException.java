
package com.htc.lib1.cs.push.exception;

import com.htc.lib1.cs.workflow.ModelException;

/**
 * Represents it needs re-registration.
 */
public class ReRegistrationNeeddedException extends ModelException {
    private static final long serialVersionUID = 1L;

    public ReRegistrationNeeddedException(String message) {
        super(message);
    }

    public ReRegistrationNeeddedException(String message, Throwable cause) {
        super(message, cause);
    }
}
