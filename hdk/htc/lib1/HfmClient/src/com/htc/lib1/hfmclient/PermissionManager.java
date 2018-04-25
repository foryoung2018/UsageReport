package com.htc.lib1.hfmclient;

import java.util.ArrayList;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * This class provides permission manager for request the dangerous permissions 
 */
public class PermissionManager
{
    private final String TAG = "PermissionManager";
    
    private final boolean DEBUG = false;

    private final String HTCSPEAK_PACKAGE_NAME = "com.htc.HTCSpeaker";
    private final String ACTIVITY_NAME_REQUEST_PERMISSION = "com.htc.HTCSpeaker.overlayui.RequestPermissionActivity";
    private final String ACTIVITY_EXTRA_DANGEROUS_PERMISSION_LIST = "Extra_Dangerous_Permission_List";

    private Context mContext;

    /** Status code for successful method call. */
    public static final int RESULT_SUCCESS = 0;
    /** Status for permission need request*/
    public static final int RESULT_ERR_PERMISSION_NEED_REQUEST = -1;
    /** Status for permission cannot not be granted*/
    public static final int RESULT_ERR_PERMISSION_GRANT_FAIL = -2;

    /** Broadcast intent of request permission result*/
    public static final String ACTION_SEND_PERMISSION_STATUS = "com.htc.HTCSpeaker.SEND_PERMISSION_STATUS";
    /** Extra name of request permission result*/
    public static final String EXTRA_STATUS = "Status";
    /** Protect permission of broadcast intent*/
    public static final String RECEIVER_PERMISSION = "com.htc.permission.APP_DEFAULT";
    /** Permission result: all permissions are granted*/
    public static final int STATUS_PERMISSION_GRANTED = 1;
    /** Permission result: At latest one permission is denied*/
    public static final int STATUS_PERMISSION_DENIED = 2;

    /**
     * Constructs an instance.
     * @param context context for getResources
     */
    public PermissionManager(Context context)
    {
        mContext = context;
    }

    /**
     * Returns true or false whether the storage permission is granted is  or not.
     * @return True if the storage permission is granted
     */
    public boolean isStoragePermissionGranted()
    {
        if (mContext == null)
        {
            Log.e(TAG, "isStoragePermissionGranted: context is null");
            return false;
        }

        int checkRead = mContext.getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, HTCSPEAK_PACKAGE_NAME);
        int checkWrite = mContext.getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, HTCSPEAK_PACKAGE_NAME);
        Log.d(TAG, "isStoragePermissionGranted: read = " + checkRead + ", write = " + checkWrite);
        
        if ((checkRead == PackageManager.PERMISSION_GRANTED) && (checkWrite == PackageManager.PERMISSION_GRANTED))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Returns the denied permission list
     * @param dangerousList the dangerous permission list
     * @return the denied permission list
     */
    public String[] getLostPermissions(String[] dangerousList)
    {
        Log.d(TAG, "getLostPermissions:");
        if (mContext == null)
        {
            Log.e(TAG, "getLostPermissions: context is null");
            return null;
        }
        
        PackageManager pkgManger = mContext.getPackageManager();
        ArrayList<String> deniedList = new ArrayList<String>();
        
        for (int i = 0, count = dangerousList.length; i < count; ++i)
        {
            int checkRet = pkgManger.checkPermission(dangerousList[i], HTCSPEAK_PACKAGE_NAME);
            if (DEBUG)
            {
                Log.d(TAG, "getLostPermissions: dangerousList[i] = " + dangerousList[i] + ", checkRet = " + checkRet);
            }
            
            if (checkRet != PackageManager.PERMISSION_GRANTED)
            {
                deniedList.add(dangerousList[i]);
            }
        }
        return deniedList.toArray(new String[deniedList.size()]);
    }

    /**
     * Start request permission dialog to allow or deny permissions
     * @param deniedList the denied permission list
     * @return RESULT_SUCCESS successful method call, 
     *                  RESULT_ERR_PERMISSION_NEED_REQUEST permission need request
     *                  RESULT_ERR_PERMISSION_GRANT_FAIL permission cannot not be granted
     */
    public int grantPermissions(String[] deniedList)
    {
        Log.d(TAG, "grantPermissions:");
        if (mContext == null)
        {
            Log.e(TAG, "getLostPermissions: context is null");
            return RESULT_ERR_PERMISSION_GRANT_FAIL;
        }
        
        int ret = RESULT_SUCCESS;
        
        if (deniedList != null && deniedList.length > 0)
        {
            Log.d(TAG, "deniedList.length = " + deniedList.length);
            
            if (startRequestPermissionActivity(deniedList))
            {
                Log.e(TAG, "call startRequestPermissionActivity: Success");
                ret = RESULT_ERR_PERMISSION_NEED_REQUEST;
            }
            else
            {
                Log.e(TAG, "call startRequestPermissionActivity: Fail");
                ret = RESULT_ERR_PERMISSION_GRANT_FAIL;
            }
        }
        else
        {
            Log.d(TAG, "Denied List is empty");
        }
        return ret;
    }
    
    private boolean startRequestPermissionActivity(String[] deniedList)
    {
        Log.d(TAG, "startRequestPermissionActivity:");
        
        boolean bRet = false;
        try
        {
            Intent intent = new Intent(ACTIVITY_NAME_REQUEST_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ACTIVITY_EXTRA_DANGEROUS_PERMISSION_LIST, deniedList);
            mContext.getApplicationContext().startActivity(intent);
            bRet = true;
        }
        catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
        return bRet;
    }
}
