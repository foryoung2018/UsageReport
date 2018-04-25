package com.htc.lib1.upm.uploader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import android.content.Context;
import com.htc.lib1.upm.Common;
import com.htc.lib1.upm.Log;

/*package*/ interface FileChangdListener {
	public void onAdd(File file);
	public void onDelete(File file);
}

public class LogCacheManager {
	
	// == singleton ==
	private static LogCacheManager sLogCacheManager; 
	public static LogCacheManager getInstance() {
		if(sLogCacheManager == null) {
			sLogCacheManager = new LogCacheManager();
		}
		return sLogCacheManager;
	}	
	private LogCacheManager() {
		mListener = new MyFileChangedListener();
	}
	
	// == static functions / datum ==
//	private static final String Common.RELATIVE_LOG_FOLDER_PATH = "logs";
    public void onNewPolicyArrival(Context ctx) {
    	Log.d("onNewPolicyArrival");
    	init(ctx);
    	long cacheSize = LogCacheUtil.getCacheLimitFromPolicy();
		if(cacheSize != -1) {
			trimFiles(cacheSize);
		}
    }
	
	// == member functions / datum ==
	private ArrayList<File> mFiles;
	private long mSize;
	private MyFileChangedListener mListener; // Must make sure giving the lister to outer caller after init(Context)
	
	public void putFile(Context ctx, byte[] logBuf, String tag) {
		init(ctx);
		long limit = LogCacheUtil.getCacheLimitFromPolicy();
		if(ctx != null && logBuf != null && limit >= 0 && logBuf.length <= limit) {
			Log.d("[putFile] new file size: "+logBuf.length);
			// Because it is hard to calcuate file size after zip, use original byte array size to trim.
			// The influence should be small.
			trimFiles(limit - logBuf.length);
			writeNewFile(ctx, logBuf, tag);
		}
		else {
			//[CQG #95],Modified by Ricky,2012,09.25
			if(logBuf != null)
			Log.d("new file size "+logBuf.length+"is greater than total limited size !");
		}
	}
	
	/**
	 * get files from cache
	 */
	public EntryFile [] getFiles(Context ctx) {
		init(ctx);
		EntryFile [] entryFiles = new EntryFile [mFiles.size()];
		for(int i=0; i<mFiles.size(); i++) {
			try {
				entryFiles[i] = new EntryFile(mFiles.get(i), mListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return entryFiles;
	}

	private void init(Context ctx) {
		if(mFiles == null) {
			File [] files = getFilesFromFS(ctx);
			mFiles = new ArrayList<File>();
			// Add null judgment for CQG#246183, where the return value of getFilesFromFS may return null
			if(files!=null)
			Collections.addAll(mFiles, files);
			//mFiles = Arrays.asList(files);
			Collections.sort(mFiles);
			mSize = getFolderSize();
		}
	}
	
	private File [] getFilesFromFS(Context ctx) {
		File logFolder = ctx.getDir(Common.RELATIVE_LOG_FOLDER_PATH, Context.MODE_PRIVATE);
		if(logFolder != null && logFolder.isDirectory())
			return logFolder.listFiles();  // From CQG#246183, it may be null
		return new File[0];
	}
	
	private long getFolderSize() {
		long size = 0;
		for(File file : mFiles) {
			size += file.length();
		}		
		return size;
	}
	
	private void trimFiles(long limit) {
		for(int i=0; i< mFiles.size() && mSize > limit; i++) {
			File file = mFiles.get(0);
			mListener.onDelete(file); // maintain mFiles and mSize
			file.delete();
		}
	}
	
	private void writeNewFile(Context ctx, byte [] logBuf, String tag) {
        File file = EntryFile.writeNewFileEx(ctx, logBuf, tag);
        mListener.onAdd(file); // maintain mFiles and mSize
    }
	
	
	private class MyFileChangedListener implements FileChangdListener {
		/**
		 * only maintain mFiles and mSize
		 */
		public void onAdd(File file) {
			if(file != null && file.exists()) {
				for(File f : mFiles){
					if(f != null && f.compareTo(file) == 0){
						Log.d("[onAdd] file "+file.getAbsolutePath() + " is alread in the list !");
						return; 
					}
				}
				mFiles.add(file);
				Collections.sort(mFiles);
				mSize += file.length();
			}
		}
		
		/**
		 * only maintain mFiles and mSize
		 */
		public void onDelete(File file) {
			if(file != null && file.exists()) {
				File targetFileInList = null;
				for(File f : mFiles){
					if(f != null && f.compareTo(file) == 0){
						targetFileInList = f;
						break;
					}
				}
				if(targetFileInList != null) {
					mFiles.remove(targetFileInList);
					mSize -= file.length();
					Log.d("[onDelete] file "+file.getAbsolutePath() + " is removed in the list !");
					Log.d("[onDelete] total size = "+mSize+", removed file size = "+file.length());
				}
			}
		}
	}
}
