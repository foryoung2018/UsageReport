
package com.htc.lib1.cs.push.exception;

import com.htc.lib1.cs.workflow.ModelException;

/**
 * Indicates unregistration is required.
 * 
 * @author samael_wang@htc.com
 */
public class UnregistrationNeededException extends ModelException {
    private static final long serialVersionUID = 1L;

    public UnregistrationNeededException(String message) {
        super(message);
    }

    public UnregistrationNeededException(String message, Throwable cause) {
        super(message, cause);
    }

}
