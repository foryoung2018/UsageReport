
package com.htc.lib1.cs.account;

import com.htc.lib1.cs.workflow.UnexpectedException;


/**
 * Indicating get regions failed for some reason.
 */
public class GetRegionsFailedException extends UnexpectedException {
    private static final long serialVersionUID = 2L;

    public GetRegionsFailedException(String message) {
        super(message);
    }

    public GetRegionsFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
