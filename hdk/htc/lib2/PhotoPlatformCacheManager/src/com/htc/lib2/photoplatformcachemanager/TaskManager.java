package com.htc.lib2.photoplatformcachemanager;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.htc.lib2.photoplatformcachemanager.PhotoPlatformCacheManager.DownloadFutureTask;

/**
 * @hide
 */
public class TaskManager {

	/**
	 * @hide
	 */
	public static class TaskExecutor {
		private static final String TAG = PhotoPlatformCacheManager.class
				.getSimpleName();

		public static final int MIN_THREADS = 1; // the number of threads to
													// keep in the pool
		public static final int DEFAULT_THREADS = 2;
		public static final int MAX_THREADS = 4; // the maximum number of
													// threads to allow in the
													// pool
		private static final int KEEP_ALIVE_TIME = 10; // the maximum time that
														// excess idle threads
														// will wait for new
														// tasks before
														// terminating

		private final PriorityBlockingQueue<Runnable> mQueue = new PriorityBlockingQueue<Runnable>();

		private ThreadPoolExecutor mExecutor = null;

		/**
		 * A thread factory that creates threads with a given thread priority.
		 */
		public class WorkerThreadFactory implements ThreadFactory {
			private final int mPriority;
			private final AtomicInteger mNumber = new AtomicInteger();
			private final String mName;

			public WorkerThreadFactory(String name, int priority) {
				mName = name;
				mPriority = priority;
			}

			public Thread newThread(Runnable r) {
				return new Thread(r, mName + '-' + mNumber.getAndIncrement()) {
					@Override
					public void run() {
						android.os.Process.setThreadPriority(mPriority);
						super.run();
					}
				};
			}
		}

		/**
		 * @hide
		 */
		public TaskExecutor(int coreThread, String taskName) {
			synchronized (this) {
				mExecutor = new ThreadPoolExecutor(getProperSize(coreThread),
						MAX_THREADS, KEEP_ALIVE_TIME, TimeUnit.SECONDS, mQueue,
						new WorkerThreadFactory(taskName,
								android.os.Process.THREAD_PRIORITY_BACKGROUND)) {
					@Override
					protected void afterExecute(Runnable r, Throwable t) {
						super.afterExecute(r, t);
						if (r instanceof DownloadFutureTask) {
							try {
								DownloadFutureTask tmpTask = (DownloadFutureTask) r;
								if (tmpTask.downloadTaskData.getBoolean(PhotoPlatformCacheManager.KEY_STATUS_PROGRESS_TO_SUCCESS, false)) {
									CLog.d(TAG, "[afterExecute] KEY_STATUS_PROGRESS_TO_SUCCESS is true", false);
									PhotoPlatformCacheManager	.forceTriggerSuccessCallback(tmpTask.downloadTaskUri, tmpTask.downloadTaskData);
								}
							}
							catch (Exception e) {
								CLog.w(TAG, "[afterExecute] Unable to do afterExecute() properly due to exception: " + e.toString(), false);
							}
						}
					}
				};

				mExecutor.allowCoreThreadTimeOut(true);
			}
		}

		/**
		 * @hide
		 */
		public void execute(Runnable runnable) {
			mExecutor.execute(runnable);
		}

		/**
		 * @hide
		 */
		public boolean isShutDown() {
			synchronized (this) {
				return mExecutor == null || mExecutor.isShutdown();
			}
		}

		public void shutdownNow() {
			mExecutor.shutdownNow();
		}

		public int size() {
			return mQueue.size();
		}

		public void purge() {
			mExecutor.purge();
		}

		public void remove(Runnable runnable) {
			mExecutor.remove(runnable);
		}

		public void setCorePoolSize(int corePoolSize) {
			try {
				mExecutor.setCorePoolSize(getProperSize(corePoolSize));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		public void setMaximumPoolSize(int maximumPoolSize) {
			try {
				mExecutor.setMaximumPoolSize(getProperSize(maximumPoolSize));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		private int getProperSize(int threadSize) {
			int nCoreThread = MIN_THREADS;
			nCoreThread = (threadSize > MIN_THREADS) ? threadSize : MIN_THREADS; // corePoolSize
																					// cannot
																					// less
																					// than
																					// MIN_THREADS
			nCoreThread = (nCoreThread > MAX_THREADS)
					? MAX_THREADS
					: nCoreThread; // maxThread cannot greater than MAX_THREADS

			return nCoreThread;
		}
	}

	/**
	 * @hide
	 */
	public static class TaskInfo {

		/**
		 * @hide
		 */
		public static enum Status {
			PROGRESS(0), SUCCESS(1), FAIL(2), ERROR(3);

			@SuppressWarnings("unused")
			private int v;

			private Status(int value) {
				v = value;
			}
		}

		private Status mStatus = Status.FAIL;

		/**
		 * @hide
		 */
		public TaskInfo(Status status) {
			mStatus = status;
		}

		/**
		 * @hide
		 */
		public TaskInfo applyStatus(Status status) {
			if (mStatus != null) {
				mStatus = status;
			}
			return this;
		}

		/**
		 * @hide
		 */
		public Status getStatus() {
			return mStatus;
		}
	}
}
