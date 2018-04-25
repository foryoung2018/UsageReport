package com.htc.lib1.upm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import com.htc.lib0.HDKLib0Util;
import com.htc.lib1.upm.uploader.ReportConfig;
import com.htc.lib1.upm.Log;

public class HtcUPDataUtils {
    private static final int MAX_DATA_ITME = 64;
    private static final int VERSION_CODE = 1;
    
    public static boolean isShippingRom(Context context) {
    	if (!isHtcDevice(context))
    		return true;   //Consider all competitor device as shipping ROM
    	else
    		return ReportConfig.isShippingRom();
    }
    
    static void checkAttribute(String[] labels, String[] values) {
        if (labels == null || values == null)
            throw new IllegalArgumentException("string array can not be null!");
        
        if (labels.length != values.length)
            throw new IllegalArgumentException("Size of labels and values cannot be different!");
        
        if (labels.length > MAX_DATA_ITME || labels.length == 0)
            throw new IllegalArgumentException("Size of labels or values cannot exceed 64 or equal 0!");
        
        int N = labels.length;
        for (int i = 0 ; i < N ; i ++) {
            if (TextUtils.isEmpty(labels[i]) || TextUtils.isEmpty(values[i]))
                throw new IllegalArgumentException("Elements of labels and values cannot be null or empty!");
        }
    }
    
    static Bundle createBundleForUPData(String appID, String action, String category, String label, int value, String[] labels, String[] values, long timestamp, boolean isSecure, boolean isDebugging) {
        Bundle data = new Bundle();
        data.putString(Common.APP_ID, appID);
        if (timestamp != 0)
            data.putLong(Common.TIMESTAMP, timestamp);
        else
            data.putLong(Common.TIMESTAMP, System.currentTimeMillis());
        if (!TextUtils.isEmpty(action))
            data.putString(Common.EVENT_ACTION, action);
        if (!TextUtils.isEmpty(category))
            data.putString(Common.EVENT_CATEGORY, category);
        if (labels != null && labels.length > 0) {
            data.putStringArray(Common.ATTRIBUTE_LABLE, labels);
            data.putStringArray(Common.ATTRIBUTE_EXTRA, values);
        } else {
            if (!TextUtils.isEmpty(label))
                data.putString(Common.EVENT_LABEL, label);
            if (value >= 0)
                data.putInt(Common.EVENT_VALUE, value);
        }
        data.putBoolean(Common.IS_SECURE, isSecure);
        data.putInt(Common.VERSION_CODE, VERSION_CODE);
        data.putBoolean(Common.IS_DEBUGGING, isDebugging);
        return data;
    }
    
    public static String getPackageName(Context context) {
        String packageName = context.getPackageName();
        if (TextUtils.isEmpty(packageName))
        	packageName = Common.STR_UNKNOWN;
        return packageName;
    }
     
    public static void printDataForDebugging(Bundle data) {
        if (data == null)
            return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("appID: ").append(data.getString(Common.APP_ID)).append("\n");
        if (!TextUtils.isEmpty(data.getString(Common.EVENT_CATEGORY)))
        	sb.append("category: ").append(data.getString(Common.EVENT_CATEGORY)).append("\n");
        if (!TextUtils.isEmpty(data.getString(Common.EVENT_ACTION)))
        	sb.append("action: ").append(data.getString(Common.EVENT_ACTION)).append("\n");
        if (!TextUtils.isEmpty(data.getString(Common.EVENT_LABEL)))
        	sb.append("label: ").append(data.getString(Common.EVENT_LABEL)).append("\n");
        if (data.getInt(Common.EVENT_VALUE, -1) >= 0)
        	sb.append("value: ").append(data.getInt(Common.EVENT_VALUE)).append("\n");
        if (data.getStringArray(Common.ATTRIBUTE_LABLE) != null) {
        	sb.append("Attribute: \n");
        	String[] labels = data.getStringArray(Common.ATTRIBUTE_LABLE);
        	String[] extras = data.getStringArray(Common.ATTRIBUTE_EXTRA);
        	int N = data.getStringArray(Common.ATTRIBUTE_LABLE).length;
        	for (int i = 0 ; i < N ; i++)
        		sb.append("     {").append(labels[i]).append(" , ").append(extras[i]).append("} \n");
        }
        Log.v(sb.toString());
	}
    
    public static boolean isKitKatOrBelow() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
            return true;
        else
            return false;
    }
    
    /**
     * Returns whether the device is a HTC device.
     * For our view, HTC device includes HEP and OEM, but doesn't include Stock UI.
     * @param context
     * @return
     */
    public static boolean isHtcDevice(Context context) {
        //isHTCDevice(): check whether HtcCustomizationManager class exist or not. 
        return HDKLib0Util.isHTCDevice(); // && !HDKLib0Util.isStockUIDevice(context);
    }
    
    public static boolean hasSameSignatureAsHsp(Context ctx) {
        if(ctx == null)
            return false;
        
        PackageManager pm = ctx.getPackageManager();
        if(pm == null)
            return false;

        PackageInfo piHsp = null;
        PackageInfo piBIDHandler = null;
        PackageInfo piApp = null;
        String packageName = null;
        String appPackageName = ctx.getPackageName();
        if(!TextUtils.isEmpty(appPackageName)) {
            try {
                packageName = Common.HSP_PACKAGE_NAME;
                piHsp = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            } catch (Exception e) {
                String msg = e != null ? e.getMessage() : "";
                Log.d("hasSameSignatureAsHsp", "Fail to get info of "+packageName+", message: "+msg);
            }

            try {
                packageName = Common.APP_PACKAGE_NAME_HTCBIDHANDLER;
                piBIDHandler = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            } catch (Exception e) {
                String msg = e != null ? e.getMessage() : "";
                Log.d("hasSameSignatureAsHsp", "Fail to get info of "+packageName+", message: "+msg);
            }

            try {
                packageName = appPackageName;
                piApp = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            } catch (Exception e) {
                String msg = e != null ? e.getMessage() : "";
                Log.d("hasSameSignatureAsHsp", "Fail to get info of "+packageName+", message: "+msg);
            }
        }

        if(((piHsp == null || piHsp.signatures == null || piHsp.signatures[0] == null)
                && (piBIDHandler == null || piBIDHandler.signatures == null || piBIDHandler.signatures[0] == null)) ||
                piApp == null || piApp.signatures == null || piApp.signatures[0] == null)
            return false;

        if((piHsp != null && piHsp.signatures != null && piHsp.signatures[0] != null && piHsp.signatures[0].equals(piApp.signatures[0]))
        || (piBIDHandler != null && piBIDHandler.signatures != null && piBIDHandler.signatures[0] != null && piBIDHandler.signatures[0].equals(piApp.signatures[0]))) {
            return true;
        }

        return false;
    }
}
