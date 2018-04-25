package com.htc.lib1.hfmclient;

import java.lang.reflect.Method;
import java.util.Locale;

import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcAlertDialog.Builder;

import dalvik.system.PathClassLoader;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;

/**
 * This class provides download manager for supported language.
 */
public class HfmDownloadClient
{
    private static final String TAG = "HfmDownloadClient";
    
    private static final String HTCSPEAK_PACKAGE_NAME = "com.htc.HTCSpeaker";
    private static final String MSG_DOWNLOAD_LANGPACK_FINISHED = "com.htc.HTCSpeaker.DOWNLOAD_COMPLETE";
    
    /** Checking the given locale is supported */
    public static final int RESULT_SUPPORT = 0;
    /** Checking the given locale input the wrong argument */
    public static final int RESULT_WRONG_ARGUMENT = -1;
    /** Checking the given locale can not find the engine */
    public static final int RESULT_NOT_INSTALL_ENGINE = -2;
    /** Status for no need dialog */
    public static final int RESULT_NO_NEED_DIALOG = -3;
    //{ Simon_Wu (Check dangerous permissions for Android M) start
    /** Status for permission need request*/
    public static final int RESULT_ERROR_PERMISSION_NEED_REQUEST = -4;
    /** Status for permission cannot not be granted*/
    public static final int RESULT_ERROR_PERMISSION_GRANT_FAIL = -5;
    //} Simon_Wu (Check dangerous permissions for Android M) end
    
    /** Status code for successful method call. */
    public static final int SUCCESS = 0;
    /** Status code for download fail */
    public static final int ERROR_DOWNLOAD_FAILD = -1;
    /** Status code for unknown error */
    public static final int ERROR_UNKOWN = -99;
    
    /** String index of Download Dialog Title */
    public static final int TEXT_DOWNLOAD_DIALOG_TITLE = 0;
    /** String index of Download Dialog Message */
    public static final int TEXT_DOWNLOAD_DIALOG_MESSAGE = 1;
    /** String index of Download Dialog Positive button */
    public static final int TEXT_DOWNLOAD_DIALOG_POSITIVE_BUTTON = 2;
    /** String index of Download Dialog Negative button */
    public static final int TEXT_DOWNLOAD_DIALOG_NEGATIVE_BUTTON = 3;
    /** String index of Update Dialog Title */
    public static final int TEXT_UPDATE_DIALOG_TITLE = 4;
    /** String index of Update Dialog Message */
    public static final int TEXT_UPDATE_DIALOG_MESSAGE = 5;
    /** String index of Update Dialog Positive button */
    public static final int TEXT_UPDATE_DIALOG_POSITIVE_BUTTON = 6;
    /** String index of Update Dialog Negative button */
    public static final int TEXT_UPDATE_DIALOG_NEGATIVE_BUTTON = 7;
    //SelyLan 20141210 begin
    public static final int TEXT_NETWORK_NOT_AVAIL_TITLE = 8;
    public static final int TEXT_NETWORK_NOT_AVAIL_MESSAGE = 9;
    public static final int TEXT_NETWORK_NOT_AVAIL_YES_BTN = 10;
    public static final int TEXT_NETWORK_NOT_AVAIL_NO_BTN = 11;
    public static final int TEXT_LANGPACK_DOWNLOADING = 12;
    //SelyLan 20141210 end
    
    private static final String AUTOMOTIVE_ACTION_MODE_CHANGE = "com.htc.AutoMotive.Service.ModeChange";
    private static final String AUTOMOTIVE_CURRENT_MODE = "AutoMotive_Current_Mode";
    
    private static final int AUTOMOTIVE_ENABLED = 0;
    private static final int AUTOMOTIVE_DISABLED = 1;
    
    private Context mContext;
    private BroadcastReceiver mLangDMReceiver;
    private static PathClassLoader mPathClassLoader = null;
    
    //{ Simon_Wu (Check dangerous permissions for Android M) begin
    private static final String[] DOWNLOAD_DANGEROUS_PERMISSION_LIST = new String[]
    {
        Manifest.permission.READ_EXTERNAL_STORAGE, //Manifest.permission_group.STORAGE
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    //} Simon_Wu (Check dangerous permissions for Android M) end
    
    /**
     * Constructs an instance.
     * 
     * @param context
     *            context for download callback
     * @param downloadcallback
     *            downloadcallback methods to receives responses
     */
    public HfmDownloadClient(Context context, final DownloadCallback downloadcallback)
    {
        mContext = context;
        mLangDMReceiver = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent)
            {
                Log.d(TAG, "onReceive= " + intent.getAction());
                if (intent.getAction().equals(HfmDownloadClient.MSG_DOWNLOAD_LANGPACK_FINISHED))
                {
                    int status = intent.getIntExtra("status", ERROR_UNKOWN);
                    String reason = intent.getStringExtra("reason");
                    if (reason == null)
                    {
                        reason = "";
                    }
                    
                    if (downloadcallback != null)
                    {
                        downloadcallback.onDownloadFinish(status, reason);
                    }
                }
            }
        };
    }
    
    private static Class<?> getNGFServiceClass(Context context)
    {
        Log.d(TAG, "getNGFServiceClass");
        String packagePath = HTCSPEAK_PACKAGE_NAME;
        String classPath = "com.htc.HTCSpeaker.NGFService";
        
        try
        {
            String apkName = context.getPackageManager().getApplicationInfo(packagePath, 0).sourceDir;
            // { 2013/10/14 Simon_Wu (PathClassLoader keeps one instance) begin
            if (mPathClassLoader == null)
            {
                Log.d(TAG, "new PathClassLoader");
                mPathClassLoader = new PathClassLoader(apkName, ClassLoader.getSystemClassLoader());
            }
            Class<?> clazz = Class.forName(classPath, true, mPathClassLoader);
            // } 2013/10/14 Simon_Wu (PathClassLoader keeps one instance) end
            return clazz;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Returns integer status code whether the given locale is supported or not.
     * 
     * @param context
     *            context for Locale information
     * @return RESULT_SUPPORT if the given locale is supported
     *                  RESULT_WRONG_ARGUMENT if input argument is wrong
     *                  RESULT_NOT_INSTALL_ENGINE if it can not find the engine
     */
    public int startDownloadSystemLangPack(Context context)
    {
        return startDownloadLangPack(context, Locale.getDefault());
    }
    
    /**
     * Returns integer status code whether the given locale is supported or not.
     * 
     * @param context
     *            context for Locale information
     * @param locale
     *            check the given locale
     * @return RESULT_SUPPORT if the given locale is supported
     *                  RESULT_WRONG_ARGUMENT if input argument is wrong
     *                  RESULT_NOT_INSTALL_ENGINE if it can not find the engine
     *                  RESULT_ERROR_PERMISSION_NEED_REQUEST if permission need request.
     *                  RESULT_ERROR_PERMISSION_GRANT_FAIL if permission cannot not be granted.
     */
    public int startDownloadLangPack(Context context, Locale locale)
    {
        Log.d(TAG, "startDownloadLangPack");
        if (context == null || locale == null)
        {
            Log.d(TAG, "startDownloadLangPack: argument is wrong");
            return RESULT_WRONG_ARGUMENT;
        }
        
        //{ Simon_Wu (Check dangerous permissions for Android M) begin
        PermissionManager permissionMgr = new PermissionManager(mContext);
        String[] deniedList = permissionMgr.getLostPermissions(DOWNLOAD_DANGEROUS_PERMISSION_LIST);
        if (deniedList != null && deniedList.length > 0)
        {
            int retGrant = permissionMgr.grantPermissions(deniedList);
            Log.d(TAG, "retGrant = " + retGrant);
            
            if (retGrant == PermissionManager.RESULT_ERR_PERMISSION_NEED_REQUEST)
            {
                Log.e(TAG, "permission need request");
                return RESULT_ERROR_PERMISSION_NEED_REQUEST;
            }
            else if (retGrant == PermissionManager.RESULT_ERR_PERMISSION_GRANT_FAIL)
            {
                Log.e(TAG, "permission cannot not be granted");
                return RESULT_ERROR_PERMISSION_GRANT_FAIL;
            }
        }
        //} Simon_Wu (Check dangerous permissions for Android M) end
        
        try
        {
            Class<?> clazz = getNGFServiceClass(context);
            if (clazz == null)
            {
                Log.d(TAG, "Can not find NGFService");
                return RESULT_NOT_INSTALL_ENGINE;
            }
            
            Class<?>[] param = {Context.class, Locale.class, ResultReceiver.class};
            Method method = clazz.getMethod("startDownloadLangPack", param);
            if (method == null)
            {
                Log.d(TAG, "Can not find startDownloadLangPack");
                return RESULT_NOT_INSTALL_ENGINE;
            }
            
            Object[] paramObj = {context, locale, null};
            method.invoke(null, paramObj);
            return RESULT_SUPPORT;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "not install engine");
            return RESULT_NOT_INSTALL_ENGINE;
        }
    }
    
    //SelyLan 20141210 begin
    private static boolean isDataAvailable(Context context)
    {
    	ConnectivityManager conxMgr = (ConnectivityManager) context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);

    	NetworkInfo mobileNwInfo = conxMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    	NetworkInfo wifiNwInfo   = conxMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    	return ((mobileNwInfo== null? false : mobileNwInfo.isConnected() )|| (wifiNwInfo == null? false : wifiNwInfo.isConnected()));
    }
    //SelyLan 20141210 end
    
    /**
     * Returns integer status code whether showing Download Dialog.
     * 
     * @param context
     *            context for DownloadDialog. It should be activity's context
     * @param app_name
     *            AP name
     * @param negative
     *            click listener of Negative Button
     * @param positive
     *            click listener of Positive Button
     * @param cancel
     *            onCancelListener of Dialog
     * @return SUCCESS if Download Dialog could be shown
     *                  RESULT_WRONG_ARGUMENT if input argument is wrong
     *                  RESULT_NO_NEED_DIALOG if it does not need dialog
     *                  RESULT_ERROR_PERMISSION_NEED_REQUEST if permission need request.
     *                  RESULT_ERROR_PERMISSION_GRANT_FAIL if permission cannot not be granted.
     */
    public int showDownloadDialog(final Context context, String app_name, final OnClickListener negative, OnClickListener positive, OnCancelListener cancel)
    {
        Log.d(TAG, "showDownloadDialog: " + app_name);
        if (context == null || app_name == null || negative == null ||positive == null || cancel == null)
        {
            Log.d(TAG, "showDownloadDialog: argument is wrong");
            return RESULT_WRONG_ARGUMENT;
        }
        
        String titleText = null;
        String messgageText = null;
        String positiveText = null;
        String negativeText = null;
        
        int status = HfmClient.isSupportedLocaleEx(context, Locale.getDefault());
        Log.d(TAG, "status = " + status);

        //SelyLan 20141210 begin
        if ((status == HfmClient.RESULT_NOT_INSTALL) || (status == HfmClient.RESULT_GOT_NEW_VERSION))
        {
    		if (isDataAvailable(context) == false)
    		{
				Builder dialog = new HtcAlertDialog.Builder(context);
				dialog.setTitle(getTextFromResId(context, app_name, TEXT_NETWORK_NOT_AVAIL_TITLE));
				dialog.setMessage(getTextFromResId(context, app_name, TEXT_NETWORK_NOT_AVAIL_MESSAGE));

				dialog.setNegativeButton(getTextFromResId(context, null, TEXT_NETWORK_NOT_AVAIL_NO_BTN), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						Log.d(TAG, "press cancel");
						negative.onClick(dialog, which);
					}
				});
				dialog.setPositiveButton(getTextFromResId(context, null, TEXT_NETWORK_NOT_AVAIL_YES_BTN), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						Log.d(TAG, "press setting");
						negative.onClick(dialog, which);
						context.startActivity(new Intent(Settings.ACTION_SETTINGS));
					}
				});
				dialog.setOnCancelListener(cancel);
				dialog.setIsAutoMotive(isCarMode());
				dialog.show();
				return SUCCESS;
    		}
        }
        //SelyLan 20141210 end
        
        if (status == HfmClient.RESULT_NOT_INSTALL)
        {
            titleText = getTextFromResId(context, null, TEXT_DOWNLOAD_DIALOG_TITLE);
            messgageText = getTextFromResId(context, app_name, TEXT_DOWNLOAD_DIALOG_MESSAGE);
            positiveText = getTextFromResId(context, null, TEXT_DOWNLOAD_DIALOG_POSITIVE_BUTTON);
            negativeText = getTextFromResId(context, null, TEXT_DOWNLOAD_DIALOG_NEGATIVE_BUTTON);
        }
        else if (status == HfmClient.RESULT_GOT_NEW_VERSION)
        {
            titleText = getTextFromResId(context, null, TEXT_UPDATE_DIALOG_TITLE);
            messgageText = getTextFromResId(context, app_name, TEXT_UPDATE_DIALOG_MESSAGE);
            positiveText = getTextFromResId(context, null, TEXT_UPDATE_DIALOG_POSITIVE_BUTTON);
            negativeText = getTextFromResId(context, null, TEXT_UPDATE_DIALOG_NEGATIVE_BUTTON);
        }
        
        if (titleText != null && messgageText != null && positiveText != null && negativeText != null)
        {
            HtcAlertDialog.Builder builder = new HtcAlertDialog.Builder(context);
            builder.setTitle(titleText);
            builder.setMessage(messgageText);
            builder.setPositiveButton(positiveText, positive);
            builder.setNegativeButton(negativeText, negative);
            builder.setOnCancelListener(cancel);
            builder.setIsAutoMotive(isCarMode());
            builder.show();
            return SUCCESS;
        }
        else
        {
            Log.d(TAG, "get text fail.");
        }
        
        return RESULT_NO_NEED_DIALOG;
    }    
    
    public static String getTextFromId(Context context, int stringId)
    {
    	return getTextFromResId(context, null, stringId);
    }
    
    public static String getTextFromId(Context context, int stringId, String app_title)
    {
    	return getTextFromResId(context, app_title, stringId);
    }
    
    private static String getTextFromResId(Context context, String app_title, int stringId)
    {
        Log.d(TAG, "getDialogTextFromResourceId: " + app_title + ", " + stringId);
        String dialogText = null;
        
        try
        {
            String defPackage = "com.htc.HTCSpeaker";
            PackageManager manager = context.getPackageManager();
            Resources res = manager.getResourcesForApplication(defPackage);
            
            switch (stringId)
            {
            case TEXT_DOWNLOAD_DIALOG_TITLE:
                dialogText = res.getString(res.getIdentifier("download_db_title", "string", defPackage), Locale.getDefault().getDisplayLanguage()) + "?";
                break;
            case TEXT_DOWNLOAD_DIALOG_MESSAGE:
                dialogText = res.getString(res.getIdentifier("download_db_content_outside", "string", defPackage), app_title, Locale.getDefault().getDisplayLanguage());
                break;
            case TEXT_DOWNLOAD_DIALOG_POSITIVE_BUTTON:
                dialogText = res.getString(res.getIdentifier("download_db_btn", "string", defPackage));
                break;
            case TEXT_DOWNLOAD_DIALOG_NEGATIVE_BUTTON:
                dialogText = res.getString(res.getIdentifier("no_db_btn", "string", defPackage));
                break;
            case TEXT_UPDATE_DIALOG_TITLE:
                dialogText = res.getString(res.getIdentifier("update_db_title", "string", defPackage), Locale.getDefault().getDisplayLanguage()) + "?";
                break;
            case TEXT_UPDATE_DIALOG_MESSAGE:
                dialogText = res.getString(res.getIdentifier("update_db_content_outside", "string", defPackage), app_title, Locale.getDefault().getDisplayLanguage());
                break;
            case TEXT_UPDATE_DIALOG_POSITIVE_BUTTON:
                dialogText = res.getString(res.getIdentifier("update_db_btn", "string", defPackage));
                break;
            case TEXT_UPDATE_DIALOG_NEGATIVE_BUTTON:
                dialogText = res.getString(res.getIdentifier("no_db_btn", "string", defPackage));
                break;
            //SelyLan 20141210 begin
            case TEXT_NETWORK_NOT_AVAIL_TITLE:
            	dialogText = res.getString(res.getIdentifier("network_not_available_title", "string", defPackage));
            	break;
            case TEXT_NETWORK_NOT_AVAIL_MESSAGE:
            	dialogText = res.getString(res.getIdentifier("network_not_available_content", "string", defPackage));
            	break;
            case TEXT_NETWORK_NOT_AVAIL_YES_BTN:
            	dialogText = res.getString(res.getIdentifier("network_not_available_Yes", "string", defPackage));
            	break;
            case TEXT_NETWORK_NOT_AVAIL_NO_BTN:
            	dialogText = res.getString(res.getIdentifier("network_not_available_No", "string", defPackage));
            	break;
            case TEXT_LANGPACK_DOWNLOADING:
            	dialogText = res.getString(res.getIdentifier("downloading_language_pack", "string", defPackage), Locale.getDefault().getDisplayLanguage());
            	break;
            default:
            	break;
            //SelyLan 20141210 end
            }
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        
        return dialogText;
    }
    
    private interface IDownloadCallback
    {
        void onDownloadFinish(int status, String reason);
    }
    
    /**
     * This class defines interface methods that must be registered in HfmClient.
     */
    public static class DownloadCallback implements IDownloadCallback
    {
        /**
         * Invokes when download finish.
         * @param status {@link #SUCCESS},
         *                   {@link #ERROR_DOWNLOAD_FAILD},
         *                   {@link #ERROR_UNKOWN},
         * @param reason the command identified
         */
        @Override
        public void onDownloadFinish(int status, String reason)
        {
            
        }
    }
    
    /**
     * Register download callback.
     */
    public void startDownloadCallback()
    {
        Log.d(TAG, "startDownloadCallback");
        IntentFilter counterActionFilter = new IntentFilter(HfmDownloadClient.MSG_DOWNLOAD_LANGPACK_FINISHED);
        mContext.registerReceiver(mLangDMReceiver, counterActionFilter, "com.htc.permission.APP_DEFAULT", null);
    }
    
    /**
     * Unregister download callback.
     */
    public void stopDownloadCallback()
    {
        Log.d(TAG, "stopDownloadCallback");
        mContext.unregisterReceiver(mLangDMReceiver);
    }
    
    private synchronized boolean isCarMode()
    {
        boolean ret = false;
        IntentFilter filter = new IntentFilter(AUTOMOTIVE_ACTION_MODE_CHANGE);
        Intent intent = mContext.registerReceiver(null, filter);
        if (intent != null)
        {
            int mode = intent.getIntExtra(AUTOMOTIVE_CURRENT_MODE, -1);
            if (mode == AUTOMOTIVE_ENABLED)
            {
                ret = true;
            }
        }
        else
        {
            Log.w(TAG, "isCarMode: intent is null");
        }
        Log.d(TAG, "isCarMode: ret = " + ret);
        return ret;
    }
}
