package com.htc.lib1.autotest.middleware;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class CoworkInterfaceListener extends BroadcastReceiver
{
	private static final String TAG = "CoworkInterfaceListener";
	
	private static final String SYSTEM_SECURE = "ro.secure";
	private static final String SYSTEM_DEBUGGABLE = "ro.debuggable";	
	private static final String mPermission = "com.htc.autotest.coworkinterface.TEST";
	private static final String mIntentFilterActionName = "AUTO_TEST_ENABLE";

	private static final String EXPECTED_PACKAGE_NAME = "com.htc.autotest.gsr.service";
	
	public static final String INTENT_CIBUNDLE = "com.htc.autotest.command.worker.CIBundle";
	public static final String BUNDLE_COMMAND = "Command";	
	
	public static final String SP_CLASS = "android.os.SystemProperties";
	public static final String SP_METHOD = "get";
	
	private final Context mContext;
	private final String mIntentFilterAction;
	private final CoworkInterfaceIntentFilter mIntentFilter;	
	
	private boolean mIsRegistered = false;
	private boolean mIsSystemTestable = false;
	
	private List<String> mCommandBlackList = new ArrayList<String>();
	
	public CoworkInterfaceListener(Context context)
	{			
		String pkgName = context.getPackageName();
		mContext = context;

		mIntentFilterAction = pkgName + "." + mIntentFilterActionName;
		mIntentFilter = new CoworkInterfaceIntentFilter();
					
		// mIsSystemTestable = isTestableROM() && isSignatureMatch(pkgName) && (!isSRPatchExist());
		mIsSystemTestable = isTestableROM() && isSignatureMatch(pkgName);

		// Log.d( TAG, "isSRPatchExist: " + isSRPatchExist());
		Log.d( TAG, "Enable CIListener: " + mIsSystemTestable);		
	}
	
	public void destory()
	{
		this.stop();
	}
	
    /**
     * Register a Receiver in the given context.
     */
	public void start()
	{
		if( !mIsRegistered && mIsSystemTestable )
		{
			mIsRegistered = true;
			mContext.registerReceiver( this, mIntentFilter, mPermission, null );
			
			Log.d( TAG, "Receiver is registered!" );
		}
	}

    /**
     * Unregister a Receiver in the given context.
     */
	public void stop()
	{
		if( mIsRegistered )
		{
			mIsRegistered = false;
			mContext.unregisterReceiver( this );
			
			Log.d( TAG, "Receiver is unregistered!" );			
		}
	}

	public void addToBlackList(String command)
	{
		mCommandBlackList.add( command.toLowerCase(Locale.getDefault()) );
		Log.d( TAG, "add a command to the black list: " + command );
	}	
	
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		Log.d( TAG, "Receiver active" );

		new Thread( new Runnable()
		{
			public void run()
			{
				String[] strCommand = intent.getBundleExtra(INTENT_CIBUNDLE).getString(BUNDLE_COMMAND).split(" ");
				
				if( strCommand.length > 0 )
				{				
					if( ! mCommandBlackList.contains( strCommand[0].toLowerCase(Locale.US) ) )				
					    AutoTestPluginLoader.getInstance().dispatchCommand(context, intent);
					else
						Log.w( TAG, "Command in the blacklist: " + strCommand[0] );
				}
				else
					Log.w( TAG, "Ignore an invalid command");				
			}

		} ).start();
	}

	private Method loadMethod(String className, String methodName)
	{
		Boolean IsLoad = false;
		
		Class<?> clazz = null;
		Method method = null;
		
		try
		{
			clazz = Class.forName( className );
			method = clazz.getDeclaredMethod(methodName, String.class, String.class);

			IsLoad = (clazz != null) && (method != null);			
		} 
		catch( Exception e )
		{
			Log.d( TAG, e.getMessage(), e );
		} 
		finally
		{
			if( !IsLoad )
			{
				clazz = null;
				method = null;

				Log.d( TAG, "loadMethod() = false" );
			}
			else
			{
				Log.d( TAG, "loadMethod() = true" );
			}
		}		
		
		return method;
		
	}
	
	private boolean isTestableROM()
	{
		Method method = null;		
		method = loadMethod( SP_CLASS, SP_METHOD );
						
		if( method == null )
			return false;
			
		boolean isTestable = false;
		
		String IsSecure = "1";
		String IsDebuggable = "0";		
		Object objResult = null;
		
		try
		{	
			objResult = method.invoke( null, SYSTEM_SECURE, "1" );			
			if( objResult instanceof String )
				IsSecure = ((String) objResult);
			
			objResult = method.invoke( null, SYSTEM_DEBUGGABLE, "0" );			
			if( objResult instanceof String )
				IsDebuggable = ((String) objResult);			
		} 
		catch( Exception e )
		{
			Log.w( TAG, e.getMessage(), e );
		}
		
		isTestable = "0".equals(IsSecure) && "1".equals(IsDebuggable);
		
		Log.d( TAG, "IsSecure: "   + IsSecure);
		Log.d( TAG, "IsDuggable: " + IsDebuggable);		
		Log.d( TAG, "isTestable: " + isTestable);
		
	    return isTestable;	
	}
	
	/*
	private static boolean isSRPatchExist()
	{
	    try
	    {
	        return (null != Class.forName("android.view.ATViewRoot"));
	    }
	    catch( ClassNotFoundException e )
	    {
	        return false;
	    }
	}
	*/
	
	private boolean isSignatureMatch(String pkgName)
	{
		Log.d( TAG, "pkgName: " + pkgName);		
		
		if( mContext.getPackageManager().checkSignatures(pkgName, EXPECTED_PACKAGE_NAME) >= 0 )
		{	
			Log.d( TAG, "isSignatureMatch: true");			
			return true;
		}				
		
		return false;
	}	
	
	private class CoworkInterfaceIntentFilter extends IntentFilter
	{
		public CoworkInterfaceIntentFilter()
		{
			addAction( mIntentFilterAction );
		}		
	}	
}
