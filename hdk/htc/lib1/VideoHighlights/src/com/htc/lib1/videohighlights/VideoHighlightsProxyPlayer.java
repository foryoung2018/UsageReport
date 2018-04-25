/**
 * 
 */
package com.htc.lib1.videohighlights;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.view.TextureView;

/**
 * @author jiahan
 *
 */
public class VideoHighlightsProxyPlayer {
	private final Object mTargetInstance;
	private Method mLoadMethod;
	//private Method mLoadCollectionMethod;
	private Method mLoadNoBundleMethod;
	private Method mLoadWithBundleMethod;
	private Method mLoadShortMethod;
	private Method mStartPreviewMethod;
	private Method mStartPreviewShortMethod;
	private Method mCancelLoadingMethod;
	private Method mInitMethod;
	private Method mDeInitMethod;
	private Method mLockListenerTypeMethod;
	private Method mUnLockListenerTypeMethod;
	//private Method mGetVideoExportFoldersMethod;
	private Method mReleaseCurrentVideoMethod;
	private Method mSetDurationMethod;
	private Method mStopPreviewMethod;
	private Method mSetCallbackMethod;
	private Method mMakeCurrentThreadMethod;
	private Method mIsPlayerSupportedMethod;
	private Method mEnableMute;
	private Method mLockListenerTypeMethodV2;
	private Method mGetProjectInfoMethod;
	private final String mPackageName;
	private final int mPackageVersionCode;
	
	protected final static Logger LOG =Logger.getLogger(VideoHighlightsProxyPlayer.class.getName());
	
	public VideoHighlightsProxyPlayer(final Object instance, final String packageName, final int pacakgeVersionCode) throws NoSuchMethodException{
		this.mTargetInstance = instance;
		this.mPackageName = packageName;
		this.mPackageVersionCode = pacakgeVersionCode;
		this.initProxyMethods();
	}
	
	private void initProxyMethods() throws NoSuchMethodException{
		final Class<? extends Object> klass = this.mTargetInstance.getClass();
		
		mLoadMethod = klass.getDeclaredMethod("load", String.class, String[].class, int[].class, String.class,
				String.class, String.class, String.class, String.class, String[].class, int.class, Point.class, 
				long.class, long.class, String.class, Bundle.class);
		mLoadNoBundleMethod= klass.getDeclaredMethod("load", String.class, String[].class, int[].class, String.class,
				String.class, String.class, String.class, String.class, String[].class, int.class, Point.class, 
				long.class, long.class);
		mLoadWithBundleMethod = klass.getDeclaredMethod("load", Bundle.class);
		mLoadShortMethod = klass.getDeclaredMethod("load", String.class, String[].class, int[].class,
			String.class, String.class, Point.class);
		mStartPreviewMethod = klass.getDeclaredMethod("startPreview", TextureView.class, int.class,Bundle.class);
		mStartPreviewShortMethod= klass.getDeclaredMethod("startPreview", TextureView.class, int.class);
		mCancelLoadingMethod = klass.getDeclaredMethod("cancelLoading");
		mInitMethod = klass.getDeclaredMethod("init", Object.class, Context.class);
		mDeInitMethod = klass.getDeclaredMethod("deInit", Object.class);
		mLockListenerTypeMethod = klass.getDeclaredMethod("lockListenerType", Object.class, Object.class.getClass());
		mUnLockListenerTypeMethod = klass.getDeclaredMethod("unLockListenerType");
		//mGetVideoExportFoldersMethod = klass.getDeclaredMethod("getVideoExportFolders");
		mReleaseCurrentVideoMethod = klass.getDeclaredMethod("releaseCurrentVideo");
		mSetDurationMethod = klass.getDeclaredMethod("setDuration", double.class);
		mStopPreviewMethod = klass.getDeclaredMethod("stopPreview");
		mSetCallbackMethod = klass.getDeclaredMethod("setCallback", Callback.class);
		mMakeCurrentThreadMethod = klass.getDeclaredMethod("makeCurrentThread");
		
		mIsPlayerSupportedMethod = safeGetMethod(klass,"isPlayerSupported");
		mEnableMute = safeGetMethod(klass,"enableMute", boolean.class);
		mLockListenerTypeMethodV2 = safeGetMethod(klass,"lockListenerType", Object.class,Context.class, Object.class.getClass());
		mGetProjectInfoMethod = safeGetMethod(klass,"getProjectInfo");
	}
	
	private Method safeGetMethod(final Class<?> klass, final String methodName, Class<?>... parameterTypes){
		Method method = null;
		
		try{
			method = klass.getDeclaredMethod(methodName, parameterTypes); 
		}catch (Exception e) {
			LOG.log(Level.WARNING,null,e);
		}
		
		return method;
	}
	
	public Object getPlayerInstance(){
		return this.mTargetInstance;
	}
	
	public boolean isUpToDate(final Context context){
		boolean b = false;
		final PackageManager packageManager = context.getPackageManager();
		if(packageManager!=null){
			try {
				final PackageInfo packageInfo = packageManager.getPackageInfo(mPackageName, 0);
				
				if(packageInfo!=null && mPackageVersionCode==packageInfo.versionCode){
					b = true;
					
					LOG.log(Level.INFO, "isUpToDate: "+mPackageName+", "+mPackageVersionCode+", "
							+packageInfo.versionCode);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				LOG.log(Level.WARNING, null,e);
			}
		}
		
		return b;
	}
	
	public int load(Bundle bundle) {
		try {
			return (Integer)this.mLoadWithBundleMethod.invoke(mTargetInstance, bundle);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int load (
			final String where,
			final String[] whereArgs,
			final int[] coverIDs,
			final String eventId,
			final String projectName,
			final String projectDate,
			final String themeName,
			final String music,
			final String userSelectedUris[],
			final int actionID,
			final Point targetViewSize, 
			final long userSelectedStartTimeMs,
			final long userSelectedEndTimeMs,
			final String collectionType,
			final Bundle arg0){
		
		
		try {
			return (Integer)this.mLoadMethod.invoke(mTargetInstance, where,whereArgs,coverIDs,eventId,projectName,projectDate,themeName,
					music,userSelectedUris,actionID, targetViewSize, userSelectedStartTimeMs,userSelectedEndTimeMs,
				collectionType, arg0);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	 * General load function for editing client
	 */
	public int load(
			final String where,
			final String[] whereArgs,
			final int[] coverIDs,
			final String eventId,
			final String projectName,
			final String projectDate,
			final String themeName,
			final String music,
			final String userSelectedUris[],
			final int actionID,
			final Point targetViewSize, 
			final long userSeletedStartTimeMs, 
			final long userSeletedEndTimeMs){
		try {
			return (Integer)this.mLoadNoBundleMethod.invoke(mTargetInstance, where,whereArgs,coverIDs,eventId,projectName,projectDate,themeName,
					music,userSelectedUris,actionID, targetViewSize, userSeletedStartTimeMs,userSeletedEndTimeMs);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public int load(String where, String[] whereArgs, int[] coverIDs,
			String eventId, String projectName, final Point targetViewSize){
		try {
			return (Integer)this.mLoadShortMethod.invoke(mTargetInstance, where,whereArgs,coverIDs,
					eventId,projectName, targetViewSize);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.htc.zeroediting.ZeroEditingPreviewPlayerInterface#startPreview(android
	 * .view.TextureView, int, android.os.Bundle)
	 */
	public int startPreview(final TextureView view, final int startPosition, final Bundle bundle){
		try {
			return (Integer)this.mStartPreviewMethod.invoke(mTargetInstance,view, startPosition, bundle);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public int startPreview(TextureView view, int startPosition){
		try {
			return (Integer)this.mStartPreviewShortMethod.invoke(mTargetInstance,view, startPosition);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public void cancelLoading(){
		try {
			this.mCancelLoadingMethod.invoke(mTargetInstance);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public int init(final Object initializer, final Context context){
		try {
			return (Integer)this.mInitMethod.invoke(mTargetInstance,initializer, context);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	
	public void deInit(final Object initializer){
		try {
			this.mDeInitMethod.invoke(mTargetInstance,initializer);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	public void lockListenerType(final Object initializer,
			final Class<? extends VideoHighlightsPlayerListener> lockListenerType){
		try {
			this.mLockListenerTypeMethod.invoke(mTargetInstance,initializer, lockListenerType);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public void lockListenerType(final Object initializer,final Context context,
			final Class<? extends VideoHighlightsPlayerListener> lockListenerType){
		try {
			this.mLockListenerTypeMethodV2.invoke(mTargetInstance,initializer,context, lockListenerType);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			throw new RuntimeException("lockListenerType() is not supported");
		}
	}
	
	public void unLockListenerType(){
		try {
			this.mUnLockListenerTypeMethod.invoke(mTargetInstance);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	 * get Video Highlight export folder path (full path)
	 */
	/*
	public String[] getVideoExportFolders(){
		try {
			return (String[])this.mGetVideoExportFoldersMethod.invoke(mTargetInstance);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	} */

	
	public void releaseCurrentVideo(){
		try {
			this.mReleaseCurrentVideoMethod.invoke(mTargetInstance);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	

	
	public void setDuration(double seconds){
		
		try {
			this.mSetDurationMethod.invoke(mTargetInstance,seconds);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public int stopPreview(){
		try {
			return (Integer)this.mStopPreviewMethod.invoke(mTargetInstance);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public void makeCurrentThread(){
		try {
			this.mMakeCurrentThreadMethod.invoke(mTargetInstance);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public void setListener(VideoHighlightsPlayerListener listener){
		try {
			this.mSetCallbackMethod.invoke(mTargetInstance, new CallbackToListenerBridge(listener));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	 * 
	 */
	public boolean isPlayerSupported() {
		// new method, add null checking to protect first.
		if (mIsPlayerSupportedMethod == null) {
			return false;
		}
		try {
			return (Boolean) this.mIsPlayerSupportedMethod.invoke(mTargetInstance);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void enableMute(final boolean mute) {
		// new method, add null checking to protect first.
		try {
			this.mEnableMute.invoke(mTargetInstance, mute);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			throw new RuntimeException("enableMute() is not supported");
		}
	}
	
	public Bundle getProjectInfo() throws IllegalStateException{
		// new method, add null checking to protect first.
		try {
			return (Bundle)this.mGetProjectInfoMethod.invoke(mTargetInstance);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			throw new RuntimeException("getProjectInfo() is not supported");
		}
		
	}
}
