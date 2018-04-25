
package com.htc.lib1.cs.workflow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;

/**
 * Class to record current running workflow tasks.
 * 
 * @author samael_wang@htc.com
 */
public class RunningWorkflowTasks {
    private static RunningWorkflowTasks sInstance;
    private List<WeakReference<AsyncWorkflowTask<?>>> mRunningTasks;

    /**
     * Get the singleton instance.
     * 
     * @return {@link RunningWorkflowTasks}
     */
    public static synchronized RunningWorkflowTasks get() {
        if (sInstance == null)
            sInstance = new RunningWorkflowTasks();

        return sInstance;
    }

    // Private constructor.
    private RunningWorkflowTasks() {
        mRunningTasks = new ArrayList<WeakReference<AsyncWorkflowTask<?>>>();
    }

    /**
     * Add a task to the running queue.
     * 
     * @param task
     */
    /* package */synchronized void add(AsyncWorkflowTask<?> task) {
        mRunningTasks.add(new WeakReference<AsyncWorkflowTask<?>>(task));
    }

    /**
     * Remove a task from the running queue.
     * 
     * @param task
     */
    /* package */synchronized void remove(AsyncWorkflowTask<?> task) {
        Iterator<WeakReference<AsyncWorkflowTask<?>>> iter = mRunningTasks.iterator();
        while (iter.hasNext()) {
            if (iter.next().get() == task)
                iter.remove();
        }
    }

    /**
     * Check if an activity has running tasks.
     * 
     * @param activity Activity to look up with.
     * @return {@code true} if there exists running tasks associating with the
     *         activity.
     */
    public synchronized boolean hasRunningTasks(Activity activity) {
        Iterator<WeakReference<AsyncWorkflowTask<?>>> iter = mRunningTasks.iterator();
        while (iter.hasNext()) {
            AsyncWorkflowTask<?> task = iter.next().get();
            if (task != null && task.getActivity() == activity)
                return true;
        }

        return false;
    }
}
