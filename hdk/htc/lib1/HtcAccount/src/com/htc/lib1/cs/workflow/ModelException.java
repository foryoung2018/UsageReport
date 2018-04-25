
package com.htc.lib1.cs.workflow;

/**
 * The root of model exceptions. It indicates a designed alternative flows of
 * the model.
 */
public class ModelException extends Exception {

    private static final long serialVersionUID = 1L;

    public ModelException(String message) {
        super(message);
    }

    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
