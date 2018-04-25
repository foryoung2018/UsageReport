
package com.htc.lib1.cs.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.Dialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.WindowManager;

import com.htc.lib1.cs.logging.CommLoggerFactory;
import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.workflow.Workflow.ModelExceptionHandler;
import com.htc.lib1.cs.workflow.Workflow.ResultHandler;
import com.htc.lib1.cs.workflow.Workflow.UnexpectedExceptionHandler;

/**
 * {@link AsyncWorflowTask} is an {@link Activity} lifecycle-aware wrapper of
 * {@link AsyncTask} that provides a mechanism to control the Workflow. The
 * running task will be canceled when Activity is going to destroy. To use it,
 * implement the handler corresponding to returned value or throw exception. For
 * example:
 * 
 * <pre>
 * UnexpectedExceptionHandler exceptionHandler = new UnexpectedExceptionHandler() {
 * 
 *             public boolean onException(UnexpectedException exception) {
 *                 if (exception instanceof SomeKindOfException) {
 *                     // handle the exception
 *                     ...
 *                     
 *                     return true;
 *                 }
 * 
 *                 return false;
 *             }
 *             
 *         };
 * </pre>
 * 
 * Since {@link ResultHandler} is running in UI thread corresponding to given
 * {@link Activity} of the {@link AsyncWorkflowTask}, it can be used to update
 * UI components. Returning true means the result is already consumed and no
 * other handlers beyond should be executed.
 * 
 * <pre>
 * ResultHandler&lt;MyWortflow, ReturnType&gt; resultHandler =
 *         new ResultHandler&lt;MyWorkflow, ReturnType&gt;() {
 * 
 *             public boolean onResult(MyWorkflow workflow, ReturnType result) {
 *                 // handle the result
 *                 TextView textView = (TextView) findViewByID(R.id.my_text_view);
 *                 textView.setText(result.toString());
 *                 return true;
 *             }
 * 
 *         };
 * </pre>
 * 
 * Adding handlers to the task is necessary before task execution. The adding
 * order of result handlers has to be considered if there is a handler which
 * always returns true.
 * 
 * <pre>
 * asyntask.addUnexpectedExpHandler(exceptionHandler); // Handle
 * // SomeKindOfException
 * asyntask.addResultHandler(resultHandler); // Handle return value
 * </pre>
 * 
 * Finally, start the task by
 * 
 * <pre>
 * asyntask.execute();
 * </pre>
 * 
 * Once the {@link AsyncWorkflowTask} is started. It will register a life cycle
 * listener for associated {@link Activity}. If the {@link Activity} has been
 * paused, the handlers will not be invoked until {@link Activity} resumes. This
 * is used to avoid invalidate UI operations such as changing the state of a
 * {@link Fragment} after {@link Activity#onSaveInstanceState(Bundle)} is
 * called.
 * 
 * @param <Workflow>
 * @param <Result>
 */
public class AsyncWorkflowTask<Result> extends AsyncTask<Void, Void, Result> {

    /**
     * Builder class to build {@link AsyncWorkflowTask}.
     */
    public static class Builder<Result> {
        private Activity mmActivity;
        private Workflow<Result> mmWorkflow;
        private Dialog mmProgress;
        private boolean mmDebugLogs;
        private List<ResultHandler<Result>> mmResultHandlers;
        private List<ModelExceptionHandler> mmModelExceptionHandlers;
        private List<UnexpectedExceptionHandler> mmUnexpectedExceptionHandlers;

        /**
         * Construct a builder with given activity and workflow.
         * 
         * @param activity Activity to operate on. Must not be {@code null}.
         * @param workflow Workflow to execute. Must not be {@code null}.
         */
        public Builder(Activity activity, Workflow<Result> workflow) {
            if (activity == null)
                throw new IllegalArgumentException("'activity' is null.");
            if (workflow == null)
                throw new IllegalArgumentException("'workflow' is null.");

            mmActivity = activity;
            mmWorkflow = workflow;
            mmResultHandlers = new ArrayList<ResultHandler<Result>>();
            mmModelExceptionHandlers = new ArrayList<ModelExceptionHandler>();
            mmUnexpectedExceptionHandlers = new ArrayList<UnexpectedExceptionHandler>();
        }

        /**
         * Set the progress dialog. If not set or set to {@code null}, no
         * progress dialog will be shown.
         * 
         * @param message Message to use.
         * @return Builder instance.
         */
        public Builder<Result> setProgressDialog(Dialog dialog) {
            mmProgress = dialog;
            return this;
        }

        /**
         * Enable / disable debug logs. It logs the result in
         * {@link AsyncWorkflowTask#onPostExecute(Object)} and monitors
         * execution.
         * 
         * @param enable True to enable debug logs. Default off.
         * @return Builder instance.
         */
        public Builder<Result> enableDebugLogs(boolean enable) {
            mmDebugLogs = enable;
            return this;
        }

        /**
         * Add a result handler
         * 
         * @param resultHandler Non-{@code null} result handler.
         * @return Builder instance.
         */
        public Builder<Result> addResultHandler(ResultHandler<Result> resultHandler) {
            if (resultHandler == null)
                throw new IllegalArgumentException("'resultHandler' is null.");
            mmResultHandlers.add(resultHandler);
            return this;
        }

        /**
         * Add an exception handler.
         * 
         * @param expHandler Non-{@code null} Exception handler.
         * @return Builder instance.
         */
        public Builder<Result> addModelExpHandler(ModelExceptionHandler expHandler) {
            if (expHandler == null)
                throw new IllegalArgumentException("'expHandler' is null.");
            mmModelExceptionHandlers.add(expHandler);
            return this;
        }

        /**
         * Add an exception handler.
         * 
         * @param expHandler Non-{@code null} Exception handler.
         * @return Builder instance.
         */
        public Builder<Result> addUnexpectedExpHandler(UnexpectedExceptionHandler expHandler) {
            if (expHandler == null)
                throw new IllegalArgumentException("'expHandler' is null.");
            mmUnexpectedExceptionHandlers.add(expHandler);
            return this;
        }

        /**
         * Build the {@link AsyncWorkflowTask} instance.
         * 
         * @return
         */
        public AsyncWorkflowTask<Result> build() {
            return new AsyncWorkflowTask<Result>(mmActivity, mmWorkflow, mmResultHandlers,
                    mmModelExceptionHandlers, mmUnexpectedExceptionHandlers, mmProgress,
                    mmDebugLogs);
        }
    }

    /**
     * A thread-pool executor.
     */
    public static final ExecutorService THREAD_POOL_EXECUTOR = Executors.newCachedThreadPool();
    private HtcLogger mLogger;
    private List<ResultHandler<Result>> mResultHandlers;
    private List<ModelExceptionHandler> mModelExceptionHandlers;
    private List<UnexpectedExceptionHandler> mUnexpectedExceptionHandlers;
    private AsyncTask.Status mStatus = AsyncTask.Status.PENDING;
    private Workflow<Result> mWorkflow;
    private WorkflowMonitor mWorkflowMonitor;
    private Activity mActivity;
    private Dialog mProgressDialog;
    private ModelException mModelException;
    private UnexpectedException mUnexpectedException;
    private Runnable mPostExecuteRunnable;
    private boolean mCanRunPostExecute;
    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks;
    private boolean mDebugLogs;

    /**
     * Construct an {@link AsyncWorkflowTask}.
     * 
     * @param activity Activity to run on and show progress dialog on.
     * @param workflow Workflow object to run.
     * @param resultHandlers Handlers to handle results. It can be an empty list
     *            but must not be {@code null}.
     * @param modelExceptionHandlers Handlers to handle model exceptions. It can
     *            be an empty list but must not be {@code null}.
     * @param unexpectedExceptionHandlers Handlers to unexpected exceptions. It
     *            can be an empty list but must not be {@code null}.
     * @param progressDialog Progress dialog to show during task execution.
     *            {@code null} indicates not showing progress dialog.
     * @param enableDebugLogs Log the result in
     *            {@link AsyncWorkflowTask#onPostExecute(Object)} and monitor
     *            execution.
     */
    public AsyncWorkflowTask(Activity activity, Workflow<Result> workflow,
            List<ResultHandler<Result>> resultHandlers,
            List<ModelExceptionHandler> modelExceptionHandlers,
            List<UnexpectedExceptionHandler> unexpectedExceptionHandlers,
            Dialog progressDialog, boolean enableDebugLogs) {
        if (activity == null)
            throw new IllegalArgumentException("'activity' is null.");
        if (workflow == null)
            throw new IllegalArgumentException("'workflow' is null.");
        if (resultHandlers == null)
            throw new IllegalArgumentException("'resultHandlers' is null.");
        if (modelExceptionHandlers == null)
            throw new IllegalArgumentException("'modelExceptionHandlers' is null.");
        if (unexpectedExceptionHandlers == null)
            throw new IllegalArgumentException("'unexpectedExceptionHandlers' is null.");

        mLogger = new CommLoggerFactory(workflow).create();

        mActivity = activity;
        mWorkflow = workflow;
        mResultHandlers = resultHandlers;
        mModelExceptionHandlers = modelExceptionHandlers;
        mUnexpectedExceptionHandlers = unexpectedExceptionHandlers;
        mCanRunPostExecute = true;
        mDebugLogs = enableDebugLogs;

        // Initialize progress dialog parameters.
        mProgressDialog = progressDialog;

        mLogger.verbose("activity=", mActivity);
    }

    /**
     * Get the activity the task associates to.
     * 
     * @return {@link Activity}
     */
    public Activity getActivity() {
        return mActivity;
    }

    /**
     * Set a designed exception to indicate an alternative workflow need to be
     * executed.
     * 
     * @param e
     */
    protected void setModelException(ModelException e) {
        mModelException = e;
    }

    /**
     * Set an unexpected exception to indicate an error occurs.
     * 
     * @param e Exception.
     */
    protected void setUnexpectedException(UnexpectedException e) {
        mUnexpectedException = e;
    }

    @Override
    protected void onPreExecute() {
        mLogger.verbose();
        mStatus = AsyncTask.Status.RUNNING;

        showProgressDialog();

        mActivityLifecycleCallbacks = new OnExecutionLifecycleCallbacks();
        mActivity.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

        // Add this task to running queue.
        RunningWorkflowTasks.get().add(this);
    }

    @Override
    final protected Result doInBackground(Void... params) {
        mLogger.info("executing...");

        // Keep tracking execution if in debug mode.
        if (mDebugLogs) {
            mWorkflowMonitor = new WorkflowMonitor();
            THREAD_POOL_EXECUTOR.execute(mWorkflowMonitor);
        }

        Result result = null;
        try {
            result = mWorkflow.execute();
        } catch (ModelException e) {
            // Track exceptions.
            mLogger.info(e.getClass().getName(), ": ", e.getMessage());
            setModelException(e);
        } catch (UnexpectedException e) {
            // Track exceptions.
            mLogger.warning(e.getClass().getName(), ": ", e.getMessage());
            setUnexpectedException(e);
        } catch (Throwable e) {
            /*
             * For runtime exceptions or errors, convert it to
             * UnexpectedException.
             */
            mLogger.error(e);
            setUnexpectedException(new UnexpectedException(e.getMessage(), e));
        }

        // Stop monitor thread if any.
        if (mWorkflowMonitor != null)
            mWorkflowMonitor.setStop(true);

        mLogger.info("finish.");

        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        // Remove this task from the running queue.
        RunningWorkflowTasks.get().remove(this);

        if (mDebugLogs) {
            // This could possibly include sensitive information.
            mLogger.verboseS("result=", result);
        } else {
            mLogger.verbose();
        }

        if (mActivity != null) {
            mActivity.getApplication()
                    .unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            mPostExecuteRunnable = new PostExecuteRunnable(result);

            if (mCanRunPostExecute) {
                if (mProgressDialog != null) {
                    mLogger.debug("Dismissing progress dialog");
                    mProgressDialog.dismiss();
                }
                mPostExecuteRunnable.run();
            } else {
                mActivityLifecycleCallbacks = new PostExecutionLifecycleCallbacks();
                mActivity.getApplication().registerActivityLifecycleCallbacks(
                        mActivityLifecycleCallbacks);
                mLogger.debug(mActivity,
                        " is not at foreground. Postpond onPostExecute to onResume");
            }
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog != null) {
            mLogger.debug("Showing progress dialog");
            // In order to keep immersive mode, set dialog as not focusable before show.
            mProgressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            mProgressDialog.show();
            mProgressDialog.getWindow().getDecorView().setSystemUiVisibility(
                    getActivity().getWindow().getDecorView().getSystemUiVisibility());
            // To blocking background UI interaction, clear the focusable flag after show.
            mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    /**
     * Execute on {@link #THREAD_POOL_EXECUTOR}.
     * 
     * @return This instance of AsyncTask.
     */
    public final AsyncTask<Void, Void, Result> executeOnThreadPool() {
        return executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    /**
     * Runnable to run post execute actions.
     */
    private class PostExecuteRunnable implements Runnable {
        private Result mmResult;

        public PostExecuteRunnable(Result result) {
            this.mmResult = result;
        }

        @Override
        public void run() {
            if (mProgressDialog != null) {
                mLogger.debug("Dismissing progress dialog");
                mProgressDialog.dismiss();
            }

            boolean processed = false;

            if (mModelException != null) {
                // An designed exception occurs, let exception handlers to
                // handle it.
                for (ModelExceptionHandler handler : mModelExceptionHandlers) {
                    if (processed = handler.onException(mActivity, mModelException))
                        break;
                }

                // Found no handler to handle the exception.
                if (!processed) {
                    mLogger.error("No handler to handle the exception: ", mModelExceptionHandlers);

                    // Make the default uncaught exception handler to handle
                    // this exception.
                    Thread.currentThread().getUncaughtExceptionHandler()
                            .uncaughtException(Thread.currentThread(), mModelException);
                }
            } else if (mUnexpectedException != null) {
                // An error occurs, let exception handlers to handle it.
                for (UnexpectedExceptionHandler handler : mUnexpectedExceptionHandlers) {
                    if (processed = handler.onException(mActivity, mUnexpectedException))
                        break;
                }

                // Found no handler to handle the error.
                if (!processed) {
                    mLogger.error("No handler to handle the exception: ", mUnexpectedException);

                    // Make the default uncaught exception handler to handle
                    // this exception.
                    Thread.currentThread().getUncaughtExceptionHandler()
                            .uncaughtException(Thread.currentThread(), mUnexpectedException);
                }
            } else {
                // Find a result handler to handle the result.
                for (ResultHandler<Result> handler : mResultHandlers) {
                    if (processed = handler.onResult(mActivity, mmResult))
                        break;
                }

                // Found no handler to handle the result.
                if (!processed)
                    mLogger.warning("No handler to consume the result: ", mmResult);
            }

            mStatus = AsyncTask.Status.FINISHED;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        // Ensure monitor thread is stopped.
        if (mWorkflowMonitor != null)
            mWorkflowMonitor.setStop(true);
    }

    /**
     * Watch activity state changes to check if UI operations can be performed
     * or not.
     */
    private class OnExecutionLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (activity == mActivity) {
                mLogger.verbose();
                mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
                mActivity = null;
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle onSaveInstance) {
            if (activity == mActivity) {
                mLogger.verbose();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (activity == mActivity) {
                mLogger.verbose();

                /*
                 * When the activity destroys due to configuration changes, if
                 * the dialog is not dismissed, the dialog will be bring back on
                 * the screen when activity re-creates but the corresponding
                 * async task won't. This will cause the dialog never dismiss
                 * after configuration changes. However, dialog can not be
                 * dismissed after activity's onSaveInstance called, hence to
                 * avoid this problem, ensure progress dialog is dismissed when
                 * onPause.
                 */
                if (mProgressDialog != null) {
                    mLogger.debug("Dismissing progress dialog");
                    mProgressDialog.dismiss();
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (activity == mActivity) {
                mLogger.verbose();

                mCanRunPostExecute = true;
                showProgressDialog();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle onSaveInstance) {
            if (activity == mActivity) {
                mLogger.verbose();
                mCanRunPostExecute = false;
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (activity == mActivity) {
                mLogger.verbose();
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity == mActivity) {
                mLogger.verbose();

                mCanRunPostExecute = false;
                if (mActivity.isFinishing() && mStatus != AsyncTask.Status.FINISHED) {
                    final boolean ret = cancel(true);
                    mLogger.debug("Cancel the workflow ", mWorkflow, ", canceled = ", ret);
                    mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
                }
            }
        }

    };

    /**
     * Watch the activity state to execute postponed {@link PostExecuteRunnable}
     * .
     */
    private class PostExecutionLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (activity == mActivity) {
                mLogger.verbose();
                mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
                mActivity = null;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (activity == mActivity) {
                mLogger.verbose();
                if (mProgressDialog != null) {
                    mLogger.debug("Dismissing progress dialog");
                    mProgressDialog.dismiss();
                }
                mPostExecuteRunnable.run();
                mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

    };

    /**
     * Monitor thread used to periodically check running workflow.
     * 
     * @author samael_wang@htc.com
     */
    private class WorkflowMonitor implements Runnable {
        private boolean mmStop;

        public void setStop(boolean stop) {
            mmStop = stop;
        }

        @Override
        public void run() {
            while (!mmStop) {
                mLogger.debug("Workflow is running...");
                SystemClock.sleep(1000);
            }
        }

    };
}
