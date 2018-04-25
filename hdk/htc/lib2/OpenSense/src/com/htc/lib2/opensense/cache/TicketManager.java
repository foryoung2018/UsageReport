package com.htc.lib2.opensense.cache;

import java.util.concurrent.atomic.AtomicInteger;

import com.htc.lib2.opensense.internal.SystemWrapper.SWLog;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;

/**
 * @hide
 */
public class TicketManager {

    private static String LOG_TAG = TicketManager.class.getSimpleName();
    private static TicketManager sTicketManager = null;

    private final Object mTaskCallbackListLock = new Object();

    private AtomicInteger mTicketIdGen = new AtomicInteger(1);
    private Context mContext = null;
    private TaskManager mTaskManager = null;
    private SparseArray<TaskCallback> mTaskCallbackListByHash = null;
    private SparseArray<TaskCallback> mTaskCallbackListByTicketId = null;

    /**
     * @hide
     */
    public static TicketManager init(Context context) {
        if ( sTicketManager == null ) {
            synchronized (TicketManager.class) {
                if ( sTicketManager == null ) { // double check
                    sTicketManager = new TicketManager(context);
                }
            }
        }
        return sTicketManager;
    }

    /**
     * @hide
     */
    public TicketManager(Context context) {
        mContext = context;
        mTaskCallbackListByHash = new SparseArray<TaskCallback>();
        mTaskCallbackListByTicketId = new SparseArray<TaskCallback>();
        mTaskManager = TaskManager.init(mContext);
    }

    /**
     * @hide
     */
    public int generateNewTicket(String url, DownloadCallback callback, Bundle data) {
        int ticketId = mTicketIdGen.getAndIncrement();
        TicketInfo ticketInfo = new TicketInfo(ticketId, url, callback, data);
        int hash = 0;
        String preHashString = url;
        if ( !TextUtils.isEmpty(preHashString) ) {
            if ( data != null ) {
                preHashString += "-checkonly=" + data.getBoolean(Download.CHECK_ONLY);
            }
            hash = preHashString.hashCode();
        }
        TaskManager.Task task = null;
        TaskCallback taskCallback = null;
        synchronized (mTaskCallbackListLock) {
            taskCallback = mTaskCallbackListByHash.get(hash);
            if ( taskCallback == null ) {
                taskCallback = new TaskCallback(this, hash);
                mTaskCallbackListByHash.put(hash, taskCallback);
                task = mTaskManager.generateNewTask(url, taskCallback, data);
                if ( task != null ) {
                    int taskId = task.getId();
                    SWLog.i(LOG_TAG, "[add] ticket: " + ticketId + " with task: " + taskId + ", target: " + getEncodedString(url));
                    taskCallback.setTaskId(taskId);
                }
            } else {
                SWLog.i(LOG_TAG, "[add] ticket: " + ticketId + " with target: " + getEncodedString(url));
            }
            taskCallback.add(ticketInfo);
            mTaskCallbackListByTicketId.put(ticketId, taskCallback);
        }
        mTaskManager.executeTask(task);
        return ticketId;
    }

    /**
     * @hide
     */
    public TicketInfo removeTicket(int ticketId) {
        TicketInfo ticketInfo = null;
        TaskCallback taskCallback = null;
        synchronized (mTaskCallbackListLock) {
            taskCallback = mTaskCallbackListByTicketId.get(ticketId);
            if ( taskCallback != null ) {
                mTaskCallbackListByTicketId.remove(ticketId);
                ticketInfo = taskCallback.remove(ticketId);
            }
        }
        if ( ticketInfo != null ) {
            final int currentTicketId = ticketId;
            final TicketInfo currentTicketInfo = ticketInfo;
            new Thread() {
                @Override
                public void run() {
                    currentTicketInfo.onError(new Exception("ticket " + currentTicketId + " is removed by user."));
                }
            }.start();
        }
        if ( taskCallback != null && taskCallback.getCurrentTicketSize() <= 0 ) {
            int taskId = taskCallback.getTaskId();
            SWLog.i(LOG_TAG, "[remove] ticket: " + ticketId + " with canceling task: " + taskId);
            mTaskManager.cancelTask(taskId);
        } else {
            SWLog.i(LOG_TAG, "[remove] ticket: " + ticketId);
        }
        return ticketInfo;
    }

    /**
     * @hide
     */
    public int getCurrentTicketSize() {
        int sizeByHash = 0;
        int sizeByTicketId = 0;
        synchronized (mTaskCallbackListLock) {
            int size = mTaskCallbackListByHash.size();
            for (int i = 0; i < size; i++) {
                int key = mTaskCallbackListByHash.keyAt(i);
                TaskCallback taskCallback = mTaskCallbackListByHash.get(key);
                if ( taskCallback != null ) {
                    sizeByHash += taskCallback.getCurrentTicketSize();
                }
            }
            sizeByTicketId = mTaskCallbackListByTicketId.size();
        }
        if ( sizeByHash != sizeByTicketId ) {
            Log.w(LOG_TAG, "ticket size is inconsistant: " + sizeByHash + "-" + sizeByTicketId);
        }
        return Math.max(sizeByHash, sizeByTicketId);
    }

    private void removeTaskCallbackByHash(TaskCallback taskCallback, int hash) {
        if ( mTaskCallbackListByHash == null || taskCallback == null ) {
            return;
        }
        synchronized (mTaskCallbackListLock) {
            mTaskCallbackListByHash.remove(hash);
        }
    }

    private void removeTaskCallbackByTicketId(TaskCallback taskCallback, int ticketId) {
        if ( mTaskCallbackListByTicketId == null || taskCallback == null ) {
            return;
        }
        synchronized (mTaskCallbackListLock) {
            mTaskCallbackListByTicketId.remove(ticketId);
        }
    }

    /**
     * @hide
     */
    public static String getEncodedString(String string) {
        if ( TextUtils.isEmpty(string) ) {
            return "[empty]";
        }
        return new String(Base64.encode(string.getBytes(), Base64.DEFAULT));
    }

    /**
     * @hide
     */
    public static class TicketInfo {

        private int mId = 0;
        private int mHash = 0;
        private Bundle mData = null;
        private DownloadCallback mCallback = null;
        private String mUrl = null;

        /**
         * @hide
         */
        public TicketInfo(int id, String url, DownloadCallback callback, Bundle data) {
            mId = id;
            mData = data;
            mCallback = callback;
            mUrl = url;
            if ( !TextUtils.isEmpty(mUrl) ) {
                mHash = mUrl.hashCode();
            }
        }

        /**
         * @hide
         */
        public int getId() {
            return mId;
        }

        /**
         * @hide
         */
        public int getHash() {
            return mHash;
        }

        /**
         * @hide
         */
        public void onSuccess(Uri uri) {
            if ( mCallback == null ) {
                return;
            }
            mCallback.onDownloadSuccess(uri, mData);
        }

        /**
         * @hide
         */
        public void onError(Exception e) {
            if ( mCallback == null ) {
                return;
            }
            mCallback.onDownloadError(e, mData);
        }
    }

    /**
     * @hide
     */
    public static class TaskCallback implements com.htc.lib2.opensense.cache.TaskManager.TaskCallback {

        private int mHash = 0;
        private int mTaskId = 0;
        private SparseArray<TicketInfo> mTicketList = null;
        private TicketManager mTicketManager = null;

        /**
         * @hide
         */
        public TaskCallback(TicketManager ticketManager, int hash) {
            mHash = hash;
            mTicketList = new SparseArray<TicketInfo>();
            mTicketManager = ticketManager;
        }

        /**
         * @hide
         */
        public void add(TicketInfo ticketInfo) {
            if ( mTicketList == null || ticketInfo == null ) {
                return;
            }
            int ticketId = ticketInfo.getId();
            synchronized (mTicketList) {
                mTicketList.put(ticketId, ticketInfo);
            }
        }

        /**
         * @hide
         */
        public TicketInfo remove(int ticketId) {
            TicketInfo ticketInfo = null;
            if ( mTicketList == null ) {
                return ticketInfo;
            }
            synchronized (mTicketList) {
                ticketInfo = mTicketList.get(ticketId);
                mTicketList.remove(ticketId);
            }
            return ticketInfo;
        }

        /**
         * @hide
         */
        public int getHash() {
            return mHash;
        }

        /**
         * @hide
         */
        public int getTaskId() {
            return mTaskId;
        }

        /**
         * @hide
         */
        public int getCurrentTicketSize() {
            int size = 0;
            if ( mTicketList == null ) {
                return size;
            }
            synchronized (mTicketList) {
                size = mTicketList.size();
            }
            return size;
        }

        /**
         * @hide
         */
        public void setTaskId(int taskId) {
            mTaskId = taskId;
        }

        /**
         * @hide
         */
        @Override
        public void onSuccess(Uri uri, Bundle data) {
            final String methodName = "onSuccess";
            if ( mTicketList == null ) {
                return;
            }
            if ( mTicketManager != null ) {
                mTicketManager.removeTaskCallbackByHash(this, mHash);
            }
            int size = mTicketList.size();
            for (int i = 0; i < size; i++) {
                int key = mTicketList.keyAt(i);
                TicketInfo ticketInfo = mTicketList.get(key);
                if ( ticketInfo != null ) {
                    if ( mTicketManager != null ) {
                        mTicketManager.removeTaskCallbackByTicketId(this, ticketInfo.getId());
                    }
                    ticketInfo.onSuccess(uri);
                }
            }
            dumpQueueSize(methodName);
        }

        /**
         * @hide
         */
        @Override
        public void onError(Exception e, Bundle data) {
            final String methodName = "onError";
            if ( mTicketList == null ) {
                return;
            }
            if ( mTicketManager != null ) {
                mTicketManager.removeTaskCallbackByHash(this, mHash);
            }
            int size = mTicketList.size();
            for (int i = 0; i < size; i++) {
                int key = mTicketList.keyAt(i);
                TicketInfo ticketInfo = mTicketList.get(key);
                if ( ticketInfo != null ) {
                    if ( mTicketManager != null ) {
                        mTicketManager.removeTaskCallbackByTicketId(this, ticketInfo.getId());
                    }
                    ticketInfo.onError(e);
                }
            }
            dumpQueueSize(methodName);
        }

        private void dumpQueueSize(String methodName) {
            if ( mTicketManager != null && methodName != null ) {
                SWLog.d(LOG_TAG, "[" + methodName + "] queueing ticket: " + mTicketManager.getCurrentTicketSize());
            }
        }
    }
}
