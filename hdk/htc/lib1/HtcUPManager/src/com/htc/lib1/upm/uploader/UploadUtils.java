package com.htc.lib1.upm.uploader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.htc.lib1.upm.Common;
import com.htc.lib1.upm.HtcUPDataUtils;
import com.htc.lib1.upm.Log;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;

import java.util.Date;
import java.util.TimeZone;

public class UploadUtils {
    private final static String TAG = "UploadUtils";
    private static HtcWrapCustomizationManager mCustomizeManager = null;
    private static String SENSE_VERSION = Common.STR_UNKNOWN;
    
    public static int getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        boolean daylight = tz.inDaylightTime(new Date());
        return tz.getRawOffset() + (daylight ? tz.getDSTSavings() : 0);
    }
    
    public static String getSenseVersionByCustomizationManager(Context context) {
    	if (!HtcUPDataUtils.isHtcDevice(context))
    		return Common.STR_UNKNOWN;
    	
        if (mCustomizeManager == null)
            mCustomizeManager = new HtcWrapCustomizationManager();
        HtcWrapCustomizationReader reader = mCustomizeManager.getCustomizationReader("system", HtcWrapCustomizationManager.READER_TYPE_XML, false);
        if (reader != null)
            SENSE_VERSION = reader.readString("sense_version", Common.STR_UNKNOWN);
        if(HtcWrapHtcDebugFlag.Htc_DEBUG_flag) Log.d(TAG, "[getSenseVersionByCustomizationManager] Sense Version: " + SENSE_VERSION);
        return SENSE_VERSION;
    }
    
    /**
     * Return true if there is network can be used to upload data, but doesn't include 2G network and roaming. 
     * @param context
     * @return true if there is proper network to upload data.
     */
    public static boolean isNetworkAllowed(Context context) {        
        byte type = N_TYPE_ALL_NETWORK_WITHOUT_2G;  
        return isNetworkAllowed(context, type);         
    }
    
    //private final static byte N_MASK_USBNET = (byte) 0x80B;
    private final static byte N_MASK_WIFI = 0x40;
    private final static byte N_MASK_OTHERS = 0x20;
    private final static byte N_MASK_2G = 0x10;
    private final static byte N_TYPE_ALL_NETWORK_WITH_2G =  N_MASK_WIFI | N_MASK_OTHERS | N_MASK_2G;
    private final static byte N_TYPE_ALL_NETWORK_WITHOUT_2G =  N_MASK_WIFI | N_MASK_OTHERS;
    private final static byte N_TYPE_WIFI_ONLY =  N_MASK_WIFI;
    /**
     * This function always return false in roaming mode. For underneath network types, it return true 
     * if there is any enabled network type in allowedTypes.Currently, 4 types are defined below. The 
     * sequence is defined the highest bit as the fist one.
     * [1st bit(0x80)] 0: disabled in USBNET,   1: enabled in USBNET
     * [2nd bit(0x40)] 0: disabled in wifi,     1: enabled in wifi
     * [3rd bit(0x20]  0: disabled in others,   1: enabled in others( excpet USBNET, wifi, 2G)
     * [4th bit(0x10]  0: disabled in 2G,       1: enabled in 2G
     * 
     * @param acceptedTypes
     * @return
     */
    private static boolean isNetworkAllowed(Context context, byte acceptedTypes){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); 
        if (networkInfo == null){
            Log.d(TAG, "Upload block due to no active network.");
            return false;
        }
        
        if (networkInfo != null)
            Log.d(TAG, "getType(): "+networkInfo.getType()+", getSubtype: "+networkInfo.getSubtype());
        
        // don't upload in roaming network
        if (networkInfo.isRoaming()){
            Log.d(TAG, "Upload block due to roaming.");
            return false;
        }
        
        int type = networkInfo.getType();
        int subType = networkInfo.getSubtype();
        
        // get all status of network types
        //boolean isUSBNET = type == HtcWrapConnectivityManager.TYPE_USBNET;
        boolean isWifi = type == ConnectivityManager.TYPE_WIFI;
        boolean is2G = (type == ConnectivityManager.TYPE_MOBILE) 
                       && (subType == TelephonyManager.NETWORK_TYPE_EDGE || subType == TelephonyManager.NETWORK_TYPE_GPRS
                             || subType == TelephonyManager.NETWORK_TYPE_1xRTT || subType == TelephonyManager.NETWORK_TYPE_CDMA);
        boolean isOthers = !(isWifi || is2G);
        
        // get all accepted types
        // boolean isUSBNETTypeAccepted = (acceptedTypes & N_MASK_USBNET) == N_MASK_USBNET;
        boolean isTypeWifiAccepted = (acceptedTypes & N_MASK_WIFI) == N_MASK_WIFI;
        boolean isType2GAccepted = (acceptedTypes & N_MASK_2G) == N_MASK_2G;
        boolean isTypeOthersAccepted = (acceptedTypes & N_MASK_OTHERS) == N_MASK_OTHERS;
        
        // [isXXXAllowed] true means user agrees the XXX type network and XXX type network is available. Otherwise, false.
        //boolean isUSBNETAllowed = isUSBNET ? isUSBNETTypeAccepted : false ;
        boolean isWifiAllowed = isWifi ? isTypeWifiAccepted : false ;
        boolean is2GAllowed = is2G ? isType2GAccepted : false ;
        boolean isOthersAllowed = isOthers ? isTypeOthersAccepted : false ; 
        
        // the result
        boolean isNetworkAllowed = isWifiAllowed || is2GAllowed || isOthersAllowed;
        Log.d(TAG,"isNetworkAllowed: "+isNetworkAllowed
                    +", isTypeWifiAccepted: "+isTypeWifiAccepted+", isType2GAccepted: "+isType2GAccepted+", isTypeOthersAccepted: "+isTypeOthersAccepted
                    +", isWifiAllowed: "+isWifiAllowed+", is2GAllowed: "+is2GAllowed+", isOthersAllowed: "+isOthersAllowed);
        return isNetworkAllowed;
    }
    
    public static int getCurrentNetworkType(Context context) {
        
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //NetworkInfo usbNet = cm.getNetworkInfo(HtcWrapConnectivityManager.TYPE_USBNET);
        boolean mOn = false;
        boolean wOn = false;
        //boolean uOn = false;
        if ( null != mobile ) mOn=mobile.isConnected();
        if ( null != wifi ) wOn=wifi.isConnected();
        //if ( null != usbNet ) uOn = usbNet.isConnected();
        
        if ( mOn ) return ConnectivityManager.TYPE_MOBILE;
        if ( wOn ) return ConnectivityManager.TYPE_WIFI;
        //if ( uOn ) return HtcWrapConnectivityManager.TYPE_USBNET;
        return -1;
    }
    
    /**
     * Convert byte array to hex string.
     *
     * @param b byte array
     * @return hex string
     */
    public static String byteArrayToHexString(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n ++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                 hs += ("0" + stmp);
            else
                 hs += stmp;
        }
        return hs;
    }
    
}
