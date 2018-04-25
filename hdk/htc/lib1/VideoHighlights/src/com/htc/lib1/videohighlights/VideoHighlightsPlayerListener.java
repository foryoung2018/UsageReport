package com.htc.lib1.videohighlights;

import android.os.Bundle;

public interface VideoHighlightsPlayerListener {
	public static final int ERROR_NO_CONTEXT = 1;
	public static final int ERROR_PORJECT_LOADING_FAILURE = 2;
	public static final int ERROR_PORJECT_LOADING_EXCEPTION = 3;
	public static final int ERROR_PROJECT_NO_FOUND = 4;
	public static final int ERROR_START_PREVIEW_FAIL = 5;
	public static final int ERROR_NOT_CURRENT_THREAD_FAIL = 6;
	public static final int ERROR_WAIT_ON_START_TIMEOUT = 7;
	public static final int ERROR_PROJECT_LOADING_EXPIRE = 8;
	public static final int ERROR_SERVICE_MAIN_THREAD_RESTARTED = 9;
	public final static int ERROR_SOURCE_MEDIA_FILE_BROKEN = 10;
	public final static int ERROR_FILE_IO_ERROR = 11;
	
	public void onLoad();
	
	public void onError(int errorCode);
	public void onStart();
	public void onStop();
	public void onDurationFinished();
	public int onCallback(int type, Bundle args);
	public void onProgress(final long ms);
}
