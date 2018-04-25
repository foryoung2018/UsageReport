package com.htc.lib1.HtcCalendarFramework.util.calendar;

import android.content.Context;
import android.util.Log;
import com.htc.lib1.HtcCalendarFramework.provider.HtcExCalendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

/**
  * The Connection Utils class
 * {@exthide}
 */
public class ConnectionUtils {
	private static final String TAG = "ConnectionUtils";

        /**
          * The ConnectionUtils constructor
          * @deprecated [Not use any longer]
          */
		/**@hide*/ 
        public ConnectionUtils() {
        }
        
        /**
          * check if network status is available or not
          * @param context Context
          * @return boolean true when network is available, false when network is available
          * @deprecated [Not use any longer]
          */
    /**@hide*/ 
	public static boolean isNetworkEnabled(Context context) {
        // use customized API instead of Htc build flag, gerald 2013 03 
	    //if (HtcBuildFlag.HTC_WIMAX_flag) {
        if(HtcWrapCustomization.readBoolean("System", "support_wimax",false)){
            return isNetworkIncludeWiMaxEnabled(context);
        }
        
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        
        //gerald to do
        NetworkInfo usbNet = cm.getNetworkInfo(TYPE_USBNET);
        boolean mOn,wOn,usbOn;
        mOn = mobile.isConnected() || (mobile.getState() == NetworkInfo.State.SUSPENDED);
        wOn = wifi.isConnected();
        usbOn = (usbNet == null) ? false : usbNet.isConnected();
        Log.d(TAG, "mobile connection:" + mOn + ", wifi connection:" + wOn );//+ ", usb connection:" + usbOn);
        if((wOn!=true)&&(mOn!=true)&&(usbOn!=true)) {
            return false;
        } else {
            return true;
        }
        
    }    
    static final int TYPE_USBNET      = 18;
    
	private static boolean isNetworkIncludeWiMaxEnabled(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //gerald to do
        NetworkInfo usbNet = cm.getNetworkInfo(TYPE_USBNET);
        NetworkInfo wimax = cm.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        
        boolean mOn = false, wOn = false, wimaxOn = false, usbOn = false;
        if (mobile != null) {
            mOn = mobile.isConnected() || (mobile.getState() == NetworkInfo.State.SUSPENDED);
        }
        if (wifi != null) {
            wOn = wifi.isConnected();
        }
        if (wimax != null) {
            wimaxOn = wimax.isConnected();
        }
        if (usbNet != null) {
        	usbOn = usbNet.isConnected();
        }
        
        Log.d(TAG, "mobile connection:" + mOn + ", wifi connection:" + wOn + ", wimax connection:" + wimaxOn + ", usb connection:" + usbOn);
        if((wOn != true) && (mOn != true) && (wimaxOn != true) && (usbOn != true))
            return false;
        else
            return true;
    }

}
