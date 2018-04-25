package com.htc.lib1.dm.env;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.htc.lib1.dm.logging.Logger;

//import com.htc.wrap.android.net.HtcWrapConnectivityManager;

/**
 * Useful information about the network.
 * 
 * @author brian_anderson
 *
 */
public class NetworkEnv {

  private static final Logger LOGGER = Logger.getLogger("[DM]",NetworkEnv.class);
  
  // --------------------------------------------------

  // Singleton instance...
  private static NetworkEnv sInstance = null;
	  
  // --------------------------------------------------
	  
  private Context context;
  
  // --------------------------------------------------

  private NetworkEnv(Context context) {
    this.context = context;
  }

  public static NetworkEnv get(Context context) {
    if (context == null) {
      throw new IllegalArgumentException("context is null");
    }
    
    synchronized (NetworkEnv.class) {

		  if (sInstance == null) {
		    sInstance = new NetworkEnv(context.getApplicationContext());
		    LOGGER.debug("Created new instance: ", sInstance);
		  }

		  return sInstance;
    }
  }

  // --------------------------------------------------
  
  /**
   * The PLMN of the SIM.
   * <p>
   * The MCC/MNC of the active SIM card.  If no SIM card is active, returns NULL.
   * 
   * @return the PLMN of the active SIM card or <code>null</code> if no active SIM card is available.
   */
  public String getOperatorPLMN() {
    TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);      
    return tm.getSimOperator();
  }

  /**
   * The PLMN of the currently attached network.
   * <p>
   * The MCC/MNC of the currently attached network.
   * 
   * @return the PLMN of the currently attached network or <code>null</code> if no currently attached network.
   */
  public String getNetworkPLMN() {
    TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);      
    return tm.getNetworkOperator();
  }

  // --------------------------------------------------

  /**
   * Check if there is a connected Wifi network.
   * 
   * @return <code>true</code> iff there is a connected Wifi network
   */
  public boolean isWifiNetworkConnected() {
    return isNetworkConnected(ConnectivityManager.TYPE_WIFI);
  }

  /**
   * Check if there is a connected mobile network.
   * 
   * @return <code>true</code> iff there is a connected mobile network
   */
  public boolean isMobileNetworkConnected() {
    return isNetworkConnected(ConnectivityManager.TYPE_MOBILE);
  }

//  public boolean isUSBNetworkConnected() {
//    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//    NetworkInfo nInfo = cm.getNetworkInfo(HtcWrapConnectivityManager.TYPE_USBNET);
//    if(nInfo == null) {
//      return false;
//    }
//    
//    return nInfo.isConnected();
//  }
  
  /**
   * Check if a specific network type is connected.
   * <p>
   * Note that a given network type may be connected, but NOT be the active network (the default route for data).
   *  
   * @param type the type of network (e.g. ConnectivityManager.TYPE_WIFI)
   * @return <code>true</code> iff there is a connected network of the specified type
   */
  public boolean isNetworkConnected(int type) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo nInfo = cm.getNetworkInfo(type);
    return (nInfo == null ? false : nInfo.isConnected());
  }

  /**
   * Check if the Wifi network is the active network and it is connected.
   * 
   * @return <code>true</code> iff Wifi is the currently active network and it is connected
   */
  public boolean isWifiNetworkActive() {
    return isNetworkActive(ConnectivityManager.TYPE_WIFI);
  }

  /**
   * Check if the mobile network is the active network and it is connected.
   * 
   * @return <code>true</code> iff mobile is the currently active network and it is connected
   */
  public boolean isMobileNetworkActive() {
    return isNetworkActive(ConnectivityManager.TYPE_MOBILE);
  }

  /**
   * Check if a specific network type is the active network and whether it is connected.
   * 
   * @param type the type of network (e.g. ConnectivityManager.TYPE_WIFI)
   * @return <code>true</code> iff the active network is of the specified type and it is connected
   */
  public boolean isNetworkActive(int type) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo nInfo = cm.getActiveNetworkInfo();
    return (nInfo == null ? false : ((nInfo.getType() == type) && nInfo.isConnected()));
  }

  /**
   * The active network type.
   * 
   * @return the active network type or -1 if there is no active network.
   */
  public int getActiveNetworkType() {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo nInfo = cm.getActiveNetworkInfo();
    return (nInfo == null ? -1 : nInfo.getType());
  }

  /**
   * Check if there is an active network and it is connected.
   * <p>
   * Note that the active network may appear disconnected if background data is unavailable.
   * 
   * @return <code>true</code> iff there is an active network and it is connected
   */
  public boolean isNetworkConnected() {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo nInfo = cm.getActiveNetworkInfo();
    // nInfo.isConnected() is equivalent to nInfo.getState() == NetworkInfo.State.CONNECTED
    return (nInfo == null ? false : nInfo.isConnected());
  }

  /**
   * Check if the device is in airplane mode.
   * 
   * @return <code>true</code> iff the device is in airplane mode
   */
  @SuppressLint({ "NewApi" })
  @SuppressWarnings("deprecation")
  public boolean isAirplaneModeOn() {
    // As of API level 17, Settings.System.AIRPLANE_MODE_ON is deprecated in favor of Settings.Global.AIRPLANE_MODE_ON.
    //return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
    if (DeviceEnv.get(context).getAndroidApiLevel() < 17) {
      return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
    }
    else {
      return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    }
  }
    
  /**
   * Check if the device is considered to be roaming on the current network.
   * 
   * @return <code>true</code> iff the device is roaming on the current network.
   * @return
   */
  public boolean isRoaming() {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return tm.isNetworkRoaming();
  }
  
  /**
   * Check if data roaming is enabled.
   * 
   * @return <code>true</code> iff the data roaming is enabled.
   */
  @SuppressLint({ "InlinedApi", "NewApi" })
  @SuppressWarnings("deprecation")
  public boolean isDataRoamingEnabled() {
    boolean isDataRoaming;
    try {
      // As of API level 17, Settings.Secure.DATA_ROAMING is deprecated in favor of Settings.Global.DATA_ROAMING.
      // Data roaming can be enabled/disabled by the user, thus the move from Settings.Secure to Settings.Global.
      //isDataRoaming = Settings.System.getInt(context.getContentResolver(), Settings.Secure.DATA_ROAMING) > 0;
      if (DeviceEnv.get(context).getAndroidApiLevel() < 17) {
        isDataRoaming = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.DATA_ROAMING) > 0;
      }
      else {
        isDataRoaming = Settings.Global.getInt(context.getContentResolver(), Settings.Global.DATA_ROAMING) > 0;
      }
    }
    catch (Exception e) {
      isDataRoaming = false;
    }

    return isDataRoaming;
  }
}

