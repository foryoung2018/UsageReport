package com.htc.lib2.opensense.facedetect;

import android.util.Log;

/**
 * @author hugh
 * 
 * @hide
 */
public class FaceDetectTaskHelper {
	
	private String TAG = FaceDetectTaskHelper.class.getSimpleName();
	final static int MAXTASK = 8;
	
	private boolean bTaskCache[] = new boolean[MAXTASK];
	private FaceDetectTask mTaskCache[] = new FaceDetectTask[MAXTASK];
	
	public FaceDetectTaskHelper() {
		//InitTask
		createTask();		
	}
	
	public FaceDetectTask lockTask() {
		int i;
		FaceDetectTask result = null;
		
		//re check mTaskCache
		if (mTaskCache[0] == null) {
			createTask();
		}
				
		for (i=0;i<MAXTASK;i++) {
			if (bTaskCache[i] == false) {
				bTaskCache[i] = true;
				result = mTaskCache[i];
				break;
			}
		}

		if (result == null) {
			Log.e(TAG, "Impossible Task did not match Thread numbers");
		}

		return result;
	}
	
	public boolean unlockTask(FaceDetectTask target) {
		int i ;
		FaceDetectTask result = null;			
			
		for (i=0;i<MAXTASK;i++) {
			if (mTaskCache[i] == target) {
				if (!bTaskCache[i])
					Log.e(TAG, "Impossible2");
				result = mTaskCache[i];
				bTaskCache[i] = false;					
				break;
			}
		}

		if (result == null) {
			Log.e(TAG, "Impossible cant found task" + target);
			return false;
		}

		return true;
	}

	public void createTask() {
		int i;

		//if (PhotoEffect.IsSupported()) {
		if(FaceDetectTask.IsOmronEnable) { // check Omron is enabled
			for (i=0;i<MAXTASK;i++) {
				FaceDetectTask task = FaceDetectTask.new_task(0); //each Task can allow 100 jobs
				if(task != null) {					
					mTaskCache[i] = task; //each Task can allow 100 jobs	
				} else {
					// if create omron task fail
					mTaskCache[i] = FaceDetectTask.new_task_google(0);
				}
				bTaskCache[i] = false;		
			}
		} else {
			for (i=0;i<MAXTASK;i++) {
				mTaskCache[i] = FaceDetectTask.new_task_google(0); //each Task can allow 100 jobs
				bTaskCache[i] = false;		
			}
		}
	}

	public void destroyTask() {
		int i;			

		for (i=0;i<MAXTASK;i++) {
			if (mTaskCache[i] != null)
				mTaskCache[i].stop();
				
			mTaskCache[i] = null;
			bTaskCache[i] = false;
		}
	}	

}