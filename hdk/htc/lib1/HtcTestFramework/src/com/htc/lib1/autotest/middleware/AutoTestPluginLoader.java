package com.htc.lib1.autotest.middleware;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import dalvik.system.PathClassLoader;

class AutoTestPluginLoader
{
	private static final String TAG = "AutoTestPluginLoader";

	private static final String INTENT_CIBUNDLE = "com.htc.autotest.command.worker.CIBundle";
	private static final String BUNDLE_CLASSPATH = "ClassPath";
	
	private static final String AT_CLASS = "com.htc.autotest.server.CIServerExt";
	private static final String AT_METHOD = "dispatchCommand";

	private static AutoTestPluginLoader sInstance = null;
	
	private boolean mInited = false;

	private PathClassLoader mLoader = null;
	private Class<?> mClazz = null;
	private Method mMethod = null;
	
	public static AutoTestPluginLoader getInstance()
	{
		if( sInstance == null )
			sInstance = new AutoTestPluginLoader();

		return sInstance;
	}

	private boolean ensureInit(Intent intent)
	{
		if( mInited )
			return true;

		try
		{
			Bundle ciBundle = intent.getBundleExtra( INTENT_CIBUNDLE );
			String classPath = ciBundle.getString( BUNDLE_CLASSPATH );
			
			Log.d( TAG, "AutoTestPluginLoader.ensureInit(), cp_loader: " + classPath );
			
			mLoader = new PathClassLoader( classPath, ClassLoader.getSystemClassLoader() );
			mClazz  = Class.forName( AT_CLASS, true, mLoader );
			mMethod = mClazz.getMethod( AT_METHOD, Context.class, Intent.class );
			
			mInited = mLoader != null && mClazz != null && mMethod != null;
		} 
		catch( Exception e )
		{
			Log.d( TAG, e.getMessage(), e );
		} 
		finally
		{
			if( !mInited )
			{
				mLoader = null;
				mClazz = null;
				mMethod = null;

				Log.i( TAG, "AutoTestPluginLoader.ensureInit() = false" );
			}
			else
			{
				Log.i( TAG, "AutoTestPluginLoader.ensureInit() = true" );
			}
		}

		return mInited;
	}

	public boolean dispatchCommand(Context context, Intent intent)
	{
		if( !ensureInit( intent ) )
			return false;

		boolean bIsHandled = false;
		
		try
		{
			Object objResult = mMethod.invoke( null, context, intent );
			
			if( objResult instanceof Boolean )
				bIsHandled = ((Boolean) objResult).booleanValue();
		} 
		catch( Exception e )
		{
			Log.w( TAG, e.getMessage(), e );
		}

		return bIsHandled;
	}
}