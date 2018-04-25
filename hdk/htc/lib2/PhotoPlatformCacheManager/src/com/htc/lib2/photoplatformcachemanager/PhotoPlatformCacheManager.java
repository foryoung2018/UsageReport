package com.htc.lib2.photoplatformcachemanager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.aiqidii.mercury.provider.PhotoPlatformException;
import com.htc.lib2.photoplatformcachemanager.TaskManager.TaskExecutor;
import com.htc.lib2.photoplatformcachemanager.TaskManager.TaskInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;

/**
 * PhotoPlatformCacheManager uses a queue to execute the task which helps to cache and maintain files read from Photo Platform
 * PhotoPlatformCacheManager makes no attempt to cooperate with other processes that may be writing to the same cache directory.
 *
 * @hide
 */
public class PhotoPlatformCacheManager {
	/**
	 * Constant to retrieve task ID which was appended with download Uri
	 */
    private static final String PPCM_URI_PARAM_TASK_ID = "taskId";
    
	/**
	 * The key to set and get service policy
	 * 
	 *  @hide
	 */    
    public static final String PPCM_SERVICE_POLICY = "SERVICE_POLICY";        
    
	/**
	 * Default service policy - FCFS
	 * 
	 *  @hide
	 */    
    public static final int PPCM_SERVICE_POLICY_FCFS = 0;        
    
	/**
	 * Optional service policy - LCFS
	 * 
	 *  @hide
	 */    
    public static final int PPCM_SERVICE_POLICY_LCFS = 1;
    
	/**
	 * Default disk cache size
	 * 
	 *  @hide
	 */
    public static final long PPCM_DISK_CACHE_DEFAULT_SIZE = PPCMDiskLruCache.DISK_CACHE_DEFAULT_SIZE;    
    
	/**
	 * The key to set and  get exception error code
	 */    
    public static final String PPCM_ERROR_KEY = "PPCM_ERROR_CODE";
    
	/**
	 * Error caused by access token expired
	 */
    public static final int PPCM_ERROR_ACCESS_TOKEN_EXPIRED = PhotoPlatformException.ERROR_ACCESS_TOKEN_EXPIRED;
    
	/**
	 * An API is called before the PhotoPlatformClient is connected to the PhotoPlatform service
	 */
    public static final int PPCM_ERROR_CLIENT_DISCONNECTED = PhotoPlatformException.ERROR_CLIENT_DISCONNECTED;    
    
	/**
	 * An illegal argument was supplied
	 */
    public static final int PPCM_ERROR_ILLEGAL_ARGUMENT = PhotoPlatformException.ERROR_ILLEGAL_ARGUMENT;
    
	/**
	 * A exception that is thrown from a remote process wrapped inside a .mercury.provider.PhotoPlatformException
	 */
    public static final int PPCM_ERROR_REMOTE = PhotoPlatformException.ERROR_REMOTE;
    
	/**
	 * Error caused by the underlying SQLite database
	 */
    public static final int PPCM_ERROR_SQLITE = PhotoPlatformException.ERROR_SQLITE;
    
	/**
	 * Problem writing to the local server and hence document synchronization
	 */
    public static final int PPCM_ERROR_SYNCHRONIZATION = PhotoPlatformException.ERROR_SYNCHRONIZATION;    
    
	/**
	 * The user is not currently logged in.
	 */
    public static final int PPCM_ERROR_NOT_LOGGED_IN = PhotoPlatformException.ERROR_NOT_LOGGED_IN;    
    
	/**
	 * Error caused by session token expired.
	 */
    public static final int PPCM_ERROR_SESSION_TOKEN_EXPIRED = PhotoPlatformException.ERROR_SESSION_TOKEN_EXPIRED;            
    
	/**
	 * Unknown error type
	 */
    public static final int PPCM_ERROR_UNKNOWN = PhotoPlatformException.ERROR_UNKNOWN;
    
	/**
	 * Illegal access to an API
	 */
    public static final int PPCM_ILLEGAL_ACCESS = PhotoPlatformException.ILLEGAL_ACCESS;    
    
    public final static String KEY_STATUS_PROGRESS_TO_SUCCESS = "key_status_progress_to_success";
    
    private Context mContext = null;       
    private static PhotoPlatformCacheManager sCacheManager = null;    
    private static final String TAG = PhotoPlatformCacheManager.class.getSimpleName();    
    
    private static TaskExecutor mExecutorHelper; //download thread pool
    private static TaskExecutor mSuccessCallbackHelper; //success callback thread pool
    private static TaskExecutor mErrorCallbackHelper; //error callback thread pool
    
    private static int mServicePolicy = PPCM_SERVICE_POLICY_FCFS;
    
    private final AtomicInteger mTaskId = new AtomicInteger(1);        
        
    private final ConcurrentHashMap<Integer, DownloadCallback> mCallbackList = new ConcurrentHashMap<Integer, DownloadCallback>();    
    private final ConcurrentHashMap<Integer, DownloadFutureTask> mDownloadFutureTaskMap = new ConcurrentHashMap<Integer, DownloadFutureTask>();
    private static SparseArray<HashMap<Integer, DownloadCallback>> mProgressCallbackListSparseArray = new SparseArray<HashMap<Integer, DownloadCallback>>();
    
    /**
     * Get an instance of {@link PhotoPlatformCacheManager}, if
     * {@link PhotoPlatformCacheManager} has been initiate, it will return the instance.
     * 
     * @param context
     *            through which it can access the current theme, resources, etc.
     * @return the PhotoPlatformCacheManager instance
     * 
     * @hide
     */
    public static PhotoPlatformCacheManager init(Context context) {
    	return init(context, TaskExecutor.MAX_THREADS, PPCM_SERVICE_POLICY_FCFS);
    }
    
    /**
     * Get an instance of {@link PhotoPlatformCacheManager}, if
     * {@link PhotoPlatformCacheManager} has been initiate, it will return the instance.
     * 
     * @param context
     *            through which it can access the current theme, resources, etc.
     * @param corePoolSize
     *            the number of threads to keep in the download thread pool; default MIN_THREADS = 2 and MAX_THREADS = 4
     * @param servicePolicy
     *            the type service policy and default type is {@link PPCM_SERVICE_POLICY_FCFS} 
     * @return the PhotoPlatformCacheManager instance
     * 
     * @hide
     */
    public static PhotoPlatformCacheManager init(Context context, int corePoolSize, int servicePolicy) {
        if (sCacheManager == null) {
            synchronized (PhotoPlatformCacheManager.class) {
                if (sCacheManager == null) { // double check
                    sCacheManager = new PhotoPlatformCacheManager(context, corePoolSize);
                    mServicePolicy = servicePolicy; // cannot be changed
                }
            }
        }
        return sCacheManager;
    }    
    
    private PhotoPlatformCacheManager(Context context, int corePoolSize) {
        if ( context != null ) {
            mContext = context.getApplicationContext();
        }
        mExecutorHelper = new TaskExecutor(corePoolSize, "[DownloadExecutor]");
        mSuccessCallbackHelper = new TaskExecutor(corePoolSize, "[SuccessCallback]");
        mErrorCallbackHelper = new TaskExecutor(TaskExecutor.MIN_THREADS, "[ErrorCallback]");
    }
    
    /**
     * Input a url to start downlaod, if download,
     * {@link DownloadCallback#onDownloadSuccess(Uri, Bundle)} will be executed,
     * otherwise, {@link DownloadCallback#onDownloadError(Exception, Bundle)}
     * will be executed After the task in add to the executor, task id will be return
     * 
     * @param url the will be download url
     * @param callback the callback that will be call when download success or fail
     * @param data  the bundle will be return when download success or fail
     * @return this download task id
     * 
     * @hide
     */
    @Deprecated
    public int downloadPhotoByUrl(String url, DownloadCallback callback, Bundle data) {
        return downloadPhotoByUrl(url, null, callback, data);
    }

    /**
     * Input a url and auth to start downlaod, if download,
     * {@link DownloadCallback#onDownloadSuccess(Uri, Bundle)} will be executed,
     * otherwise, {@link DownloadCallback#onDownloadError(Exception, Bundle)}
     * will be executed After the task in add to the executor, task id will be return
     * 
     * @param url the will be download url
     * @param authHeader use to set URLConnection request property
     * @param callback the callback that will be call when download success or fail
     * @param data the bundle will be return when download success or fail
     * @return this download task id
     * 
     * @hide
     */
    @Deprecated
    public int downloadPhotoByUrl (String url, String authHeader, DownloadCallback callback, Bundle data) throws IllegalArgumentException{
    	if (url == null) {
            throw new IllegalArgumentException(
                    "url is null object");
    	}
        
        Uri thumbnailUri = Uri.parse(url);
        return downloadPhotoByUrl(thumbnailUri, authHeader, callback, data);
    }
    
    /**
     * Input a url to start downlaod, if download,
     * {@link DownloadCallback#onDownloadSuccess(Uri, Bundle)} will be executed,
     * otherwise, {@link DownloadCallback#onDownloadError(Exception, Bundle)}
     * will be executed After the task in add to the executor, task id will be return
     * 
     * @param url the will be download url
     * @param callback the callback that will be call when download success or fail
     * @param data  the bundle will be return when download success or fail
     * @return this download task id
     * 
     * @hide
     */
    public int downloadPhotoByUrl(Uri url, DownloadCallback callback, Bundle data) {
        return downloadPhotoByUrl(url, null, callback, data);
    }    
    
    /**
     * Input a url and auth to start downlaod, if download,
     * {@link DownloadCallback#onDownloadSuccess(Uri, Bundle)} will be executed,
     * otherwise, {@link DownloadCallback#onDownloadError(Exception, Bundle)}
     * will be executed After the task in add to the executor, task id will be return
     * 
     * @param url the will be download url
     * @param authHeader use to set URLConnection request property
     * @param callback the callback that will be call when download success or fail
     * @param data the bundle will be return when download success or fail
     * @return this download task id
     * 
     * @hide
     */
    public int downloadPhotoByUrl (Uri url, String authHeader, DownloadCallback callback, Bundle data) throws IllegalArgumentException{
    	if (url == null) {
            throw new IllegalArgumentException(
                    "url is null object");
    	}
    	
        if (data == null) {
            data = new Bundle();
        }

        synchronized (mExecutorHelper) {
            int taskId = mTaskId.getAndIncrement();
            DownloadFutureTask task = new DownloadFutureTask(mContext, taskId, url, callback, data);
            if (!mExecutorHelper.isShutDown()) {
                mExecutorHelper.execute(task);
            } else {
                throw new IllegalArgumentException(
                        "Can't use a ThreadPoolExecutor which has been shutdown");
            }
            
            mDownloadFutureTaskMap.putIfAbsent(task.downloadTaskId, task);
            return task.downloadTaskId;
        }
    }    
    
    /**
     * Stop the download task
     * 
     * @param taskId
     *            : The id of task which is returned by
     *            {@link #downloadPhotoByUrl(String, DownloadCallback, Bundle)}
     * 
     * @hide
     */
    public void stopDownloadPhotoByTaskId(final int taskId) {    	
		final DownloadFutureTask task = removeDownloadFutureTask(taskId);
		if (task != null)
			 task.cancel(true);
    }    
    
    /**
     * Input a thumbnailUri to get related InputStream
     * 
     * @param thumbnailUri from Photo Platform
     * @return InputStream that related to thumbnailUri
     * 
     * @hide
     */
    public InputStream getInputStream(final Uri thumbnailUri) {
    	return PPCMDiskLruCache.readFromDiskCache(mContext, thumbnailUri.toString());
    }
    
    /**
     * Input a thumbnailUri to get cache file path
     * 
     * @param thumbnailUri from Photo Platform
     * @return cache file path
     * 
     * @hide
     */
    public String getFilePath(final Uri thumbnailUri) {
    	return PPCMDiskLruCache.getFilePathFromDiskCache(mContext, thumbnailUri.toString());
    }
        
    /**
     * @hide
     */
    @Deprecated
    public void release() {
    }             
    
    /**
     * Changes the maximum number of bytes the cache can store and queues a job
     * to trim the existing store, if necessary.
     * 
     * @param size
     *            : maximum number of bytes the cache can store
     * 
     * @hide
     */
    public void setMaxDiskCacheSize(long size) {        
    	if (mContext == null)
    		return;
    	
    	PPCMDiskLruCache.setMaxSize(mContext, size);
    }              
    
    /**
     * Returns the number of bytes currently being used to store the values in
     * this cache. This may be greater than the max size if a background
     * deletion is pending.
     * 
     * @hide
     */
    public long size() {        
    	if (mContext == null)
    		return 0;
    	
    	return PPCMDiskLruCache.size(mContext);
    }            
    
    /**
     * Closes the cache and deletes all of its stored values. This will delete
     * all files in the cache directory including files that weren't created by
     * the cache.
     * 
     * @hide
     */
    public void delete() {        
    	if (mContext == null)
    		return;
    	
    	PPCMDiskLruCache.delete(mContext);
    }
    
    private DownloadFutureTask removeDownloadFutureTask (int taskId) {
			return mDownloadFutureTaskMap.remove(taskId);
    }
    
    private void addCallBack(int taskId, int targetId, DownloadCallback callback) {
    	mCallbackList.putIfAbsent(targetId, callback);
    }      
    
    private DownloadCallback removeCallback(int targetId) {
    	return mCallbackList.remove(targetId);
    }    
    
    private class CallbackFutureTask extends FutureTask<TaskInfo> implements Comparable<CallbackFutureTask> {
    	int downloadTaskId;
		public CallbackFutureTask(Runnable runnable, int taskID, TaskInfo result) {
			super(runnable, result);
			downloadTaskId = taskID;
		}

		@Override
		public int compareTo(CallbackFutureTask another) {
			return downloadTaskId < another.downloadTaskId ? -1 : 1;
		}    
    }
    
    // append task ID to original Uri, so that each download request has an unique Uri    
    private String convertURIWithTaskID(Uri originalURI, int taskID) {  
    	if (originalURI == null) {
    		CLog.w(TAG, "[convertURIWithTaskID] original Uri is null, return original one.", false);
    		return null;
    	}
    		
    	return originalURI.toString() + "?" + PPCM_URI_PARAM_TASK_ID + "=" + taskID;
    }    
    
    private HashMap<Integer, DownloadCallback> getProgressCallbackListWithLock(int targetId) {
    	synchronized (mProgressCallbackListSparseArray) {
    		return mProgressCallbackListSparseArray.get(targetId);
    	}
    }    

    @SuppressLint("UseSparseArrays")
	private void addProgressCallBack(int taskId, int targetId, DownloadCallback callback) {
        synchronized (mProgressCallbackListSparseArray) {
            HashMap<Integer, DownloadCallback> callbackList = getProgressCallbackListWithLock(targetId);
            if ( callbackList == null ) {
                callbackList = new HashMap<Integer, DownloadCallback>();
                mProgressCallbackListSparseArray.put(targetId, callbackList);
            }
            callbackList.put(taskId, callback);
        }
    }
    
    private static HashMap<Integer, DownloadCallback> removeProgressCallbackListWithLock(int targetId) {
    	synchronized (mProgressCallbackListSparseArray) {
    		HashMap<Integer, DownloadCallback> callbackList = mProgressCallbackListSparseArray.get(targetId);
    		mProgressCallbackListSparseArray.remove(targetId);
    		return callbackList;
    	}
    }    
    
	protected static void forceTriggerSuccessCallback(	final Uri downloadTaskUri, final Bundle data) {
		CLog.d(TAG, "[forceTriggerSuccessCallback] downloadTaskUri: " + downloadTaskUri, false);
		new Thread() {
			@Override
			public void run() {
				HashMap<Integer, DownloadCallback> callbackList = removeProgressCallbackListWithLock(downloadTaskUri.hashCode());
                if ( callbackList != null ) {
                    Collection<DownloadCallback> collection = callbackList.values();
                    Iterator<DownloadCallback> itrs = collection.iterator();
                    while ( itrs.hasNext() ) {
                        DownloadCallback callback = itrs.next();
    					CLog.d(TAG,
    							"[forceTriggerSuccessCallback]on Download Success callback : "
    									+ downloadTaskUri, false);
                        callback.onDownloadSuccess(downloadTaskUri, data);
                    }
                    callbackList.clear();
                }
                else
                	CLog.d(TAG, "[forceTriggerSuccessCallback] callbackList is empty", false);
			}
		}.start();
	}
	
	private void forceTriggerErrorCallback(final Exception e, final int errorUrlHash, final Bundle data) {
		if (getProgressCallbackListWithLock(errorUrlHash) == null)
			return;
		
		new Thread() {
			@Override
			public void run() {
				HashMap<Integer, DownloadCallback> callbackList = removeProgressCallbackListWithLock(errorUrlHash);
				if (callbackList != null) {
					CLog.d(TAG, "[forceTriggerErrorCallback] downloadTaskUri: "
							+ errorUrlHash, false);
					Collection<DownloadCallback> collection = callbackList
							.values();
					Iterator<DownloadCallback> itrs = collection.iterator();
					while (itrs.hasNext()) {
						DownloadCallback callback = itrs.next();
						CLog.d(TAG,
								"[forceTriggerErrorCallback]on Download Error callback : "
										+ errorUrlHash, false);
						callback.onDownloadError(e, data);
					}
					callbackList.clear();
				} else
					CLog.d(TAG,
							"[forceTriggerErrorCallback] callbackList is empty",
							false);
			}
		}.start();
	}
    
    public class DownloadFutureTask extends FutureTask<TaskInfo> implements Comparable<DownloadFutureTask> {
    	Context downloadContext = null;
        int downloadTaskId;
        Uri downloadTaskUri;
        int downloadTaskUriWithTaskIDHash;
        String downloadTaskUriWithTaskID;
        Bundle downloadTaskData;        

        /**
         * @hide
         */
        public DownloadFutureTask(Context context, int id, Uri thumbnailUri, DownloadCallback callback, Bundle data) {        	
            super(new ImageCallable(context, thumbnailUri, data, id));
            downloadContext = context;
            downloadTaskId = id;
            downloadTaskUri = thumbnailUri;
            downloadTaskUriWithTaskID = convertURIWithTaskID(thumbnailUri, id);
            downloadTaskUriWithTaskIDHash = downloadTaskUriWithTaskID.hashCode();
            addCallBack(downloadTaskId, downloadTaskUriWithTaskIDHash, callback);
            downloadTaskData = data;
        }

        /**
         * @hide
         */
        @Override
        protected void done() {        	        	
            CLog.d(TAG,"[done] In done : " + downloadTaskUriWithTaskID, false);
            try {            	
            	removeDownloadFutureTask(downloadTaskId);
            	
                final TaskInfo taskInfo = get();
                if (taskInfo != null) {
                    if (taskInfo.getStatus() == TaskInfo.Status.SUCCESS) {
                    	final DownloadCallback callback = removeCallback(downloadTaskUriWithTaskIDHash);
                    	if (callback != null) {
                    		Runnable doneCallbackRunnable = new Runnable() {
                    			@Override
                    			public void run() {
                    				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
									 CLog.d(TAG,
						                    "onDownloadSuccess : "
						                            + downloadTaskUriWithTaskID, false);
									 callback.onDownloadSuccess(downloadTaskUri, downloadTaskData);
                    			}
                    		};
                    		CallbackFutureTask callbackTask = new CallbackFutureTask(doneCallbackRunnable, downloadTaskId, taskInfo);
                    		mSuccessCallbackHelper.execute(callbackTask);
                    	}
                    } else if (taskInfo.getStatus() == TaskInfo.Status.FAIL) {
                        throw new ExecutionException("Download fail", new Throwable());
                    }
                    else if (taskInfo.getStatus() == TaskInfo.Status.PROGRESS) {
                    	CLog.d(TAG, "[done] addProgressCallBack for PROGRESS task:" + downloadTaskUriWithTaskID, false);
                    	addProgressCallBack(downloadTaskId, downloadTaskUri.hashCode(), removeCallback(downloadTaskUriWithTaskIDHash));

                    	// check if the cache file is download successfully again based on clean file path
                    	if (PPCMDiskLruCache.getFilePathFromDiskCache(downloadContext, downloadTaskUri.toString()) != null) {
                    		CLog.d(TAG, "[done] able to get clean file path and call back directly: " + downloadTaskUriWithTaskID, false);
                    		forceTriggerSuccessCallback(downloadTaskUri, downloadTaskData);
                    	}
                    }
                } 
                else {
                	CLog.w(TAG, "[done] taskInfo is null", false);
                }
            } catch (InterruptedException e) {
                CLog.d(TAG,"InterruptedException  : " + downloadTaskUriWithTaskID, false);
                handleError(downloadTaskUriWithTaskIDHash, downloadTaskId, "InterruptedException", downloadTaskData, e);
            } catch (ExecutionException e) {
                CLog.d(TAG,"ExecutionException : " +  downloadTaskUriWithTaskID, false);
                handleError(downloadTaskUriWithTaskIDHash, downloadTaskId, "ExecutionException", downloadTaskData, e);
            } catch (CancellationException e) {
                CLog.d(TAG, "CancellationException : " + downloadTaskUriWithTaskID, false);
                handleError(downloadTaskUriWithTaskIDHash, downloadTaskId, "CancellationException", downloadTaskData, e);
            }
        }

        /**
         * @hide
         */
        @Override
        public int compareTo(DownloadFutureTask other) {
        	if (mServicePolicy == PPCM_SERVICE_POLICY_LCFS)
        		return downloadTaskId > other.downloadTaskId ? -1 : 1;
        	
        	return downloadTaskId < other.downloadTaskId ? -1 : 1;        		
        }
    }
    
    private class ImageCallable implements Callable<TaskInfo> {
        Uri imageUri = null;
        String imageUriWithTaskID = null;        
        Context imageContext = null;
        int imageTaskId;
        Bundle imageBundleData;

        /**
         * @param id 
         * @hide
         */
        public ImageCallable(Context context, Uri thumbnailUri, Bundle data, int id) {
            imageContext = context;
            imageUri = thumbnailUri;
            imageUriWithTaskID = convertURIWithTaskID(thumbnailUri, id);
            imageTaskId = id;
            imageBundleData = data;
            if (imageBundleData == null) {
                imageBundleData = new Bundle();
            }
        }

        /**
         * @hide
         */
        @Override
        public TaskInfo call() throws Exception {      
        	CLog.d(TAG, "[call] " + imageUriWithTaskID, true);
        	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);               	
            TaskInfo taskInfo = new TaskInfo(TaskInfo.Status.FAIL);
            
    		if (Thread.currentThread().isInterrupted())
    			return taskInfo;
            
    		taskInfo = downloadTargetFile(imageContext, imageTaskId, taskInfo, imageUri, imageBundleData);
            
            if (Thread.currentThread().isInterrupted()) {
            	CLog.d(TAG, "[call] the task was finished after interrupted", true);
            	if (taskInfo.getStatus() == TaskInfo.Status.SUCCESS) {
            		imageBundleData.putBoolean(KEY_STATUS_PROGRESS_TO_SUCCESS, true);
            	}
            }
            	
            return taskInfo;
        }
    }
    
	private TaskInfo downloadTargetFile(Context context, int id, TaskInfo taskInfo, Uri thumbnailUri,
            Bundle data) throws Exception {
		long taskStart = System.currentTimeMillis();
		int downloadTaskUriWithTaskIDHash = convertURIWithTaskID(thumbnailUri, id).hashCode();
    	
    	try {
            int streamBytes = PPCMDiskLruCache.writeToDiskCache(context, thumbnailUri);

        	// check disk cache to make sure it is valid            	
        	boolean nVliadDiskInputStream = (streamBytes > 0) ? true : false ;
        	if (nVliadDiskInputStream)
        		taskInfo.applyStatus(TaskInfo.Status.SUCCESS);
        	else {
        		if (streamBytes == -1) {
        			taskInfo.applyStatus(TaskInfo.Status.PROGRESS);
        			CLog.w(TAG, "[downloadTargetFile][" + id + "] Download in progress: " + thumbnailUri, false);
        			return taskInfo;
        		}
        		taskInfo.applyStatus(TaskInfo.Status.ERROR);
        		if (!nVliadDiskInputStream)
        			CLog.e(TAG, "[downloadTargetFile][" + id + "] InputStream size is invalid: " + streamBytes, true);
        		data.putInt(PPCM_ERROR_KEY, PPCM_ERROR_UNKNOWN);
        		handleError(downloadTaskUriWithTaskIDHash, id, "PhotoPlatformException", data, new PhotoPlatformException(PhotoPlatformException.ERROR_UNKNOWN)); 
        	}
        } catch (Exception e) {
        	taskInfo.applyStatus(TaskInfo.Status.ERROR);
        	PhotoPlatformException exception = PhotoPlatformException.cast(e);
        	int errorCode = exception.getCode();
        	CLog.e(TAG, "[downloadTargetFile][" + id + "] PhotoPlatformException: error code " + errorCode, true);
    		data.putInt(PPCM_ERROR_KEY, errorCode);
    		handleError(downloadTaskUriWithTaskIDHash, id, "PhotoPlatformException", data, new Exception("PhotoPlatformException"));
        } finally {
        	CLog.d(TAG, "[downloadTargetFile][" + id + 	"] takes [" + (System.currentTimeMillis() - taskStart) + "ms]", false);
        }

        return taskInfo;
    }

	private void handleError(int errorUrlHash, final int errorTaskId, final String errorFunctionName, final Bundle errorBundleData, final Exception e) {
		forceTriggerErrorCallback(e, errorUrlHash, errorBundleData);
		final DownloadCallback callback = removeCallback(errorUrlHash);
		if (callback != null) {
			Runnable errorCallbackRunnable = new Runnable() {
				@Override
				public void run() {
					android.os.Process
							.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
					callback.onDownloadError(e, errorBundleData);
				}
			};
			CallbackFutureTask callbackTask = new CallbackFutureTask(
					errorCallbackRunnable, errorTaskId, null);
			mErrorCallbackHelper.execute(callbackTask);
		}	    	
    }
}
