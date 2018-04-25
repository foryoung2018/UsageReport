
package com.htc.lib1.cs.workflow;

import android.app.Activity;

/**
 * Interface for all workflows.
 */
public interface Workflow<Result> {
    /**
     * Perform workflow.
     * 
     * @return Computation results.
     * @throws ModelException If the workflow enters a designed branch scenario.
     *             Usually means some user interactions need to be performed to
     *             make the workflow continues.
     * @throws UnexpectedException If an error occurs that the workflow object
     *             doens't know how to handle.
     */
    public Result execute() throws ModelException, UnexpectedException;

    /**
     * Handle results of {@link AsyncWorkflowTask}.
     * 
     * @param <Result> Result type.
     */
    public static interface ResultHandler<Result> {
        /**
         * Executed when result returns and no previous handlers has consumed
         * the result. Run on UI thread.
         * 
         * @param activity Activity which the workflow executes on.
         * @param result Returned result, could be {@code null}.
         * @return True if the result has been consumed.
         */
        public boolean onResult(Activity activity, Result result);
    }

    /**
     * Handle model (designed) exceptions of {@link AsyncWorkflowTask}.
     */
    public static interface ModelExceptionHandler {
        /**
         * Executed when an exception occurs. Run on UI thread.
         * 
         * @param activity Activity which the workflow executes on.
         * @param exception Occurred error, will never be {@code null}.
         * @return True if the exception has been handled.
         */
        public boolean onException(Activity activity, ModelException exception);
    }

    /**
     * Handle unexpected exceptions of {@link AsyncWorkflowTask}.
     */
    public static interface UnexpectedExceptionHandler {
        /**
         * Executed when an exception occurs. Run on UI thread.
         * 
         * @param activity Activity which the workflow executes on.
         * @param exception Occurred error, will never be {@code null}.
         * @return True if the exception has been handled.
         */
        public boolean onException(Activity activity, UnexpectedException exception);
    }
}
