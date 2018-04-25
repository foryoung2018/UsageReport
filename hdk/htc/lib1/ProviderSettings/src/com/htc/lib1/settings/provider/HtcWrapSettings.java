/**
 * The content is from the difference between 
 *     //packages_new/modules/wrapper/HtcWrapandroidprovider/1.0/com/htc/wrap/android/provider/Settings.java
 *     //honeycomb/3_2/frameworks/base/core/java/android/provider/Settings.java
 *     
 * @author TJ Tsai (128135)
 */

package com.htc.lib1.settings.provider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;


/**
 * 
 * The Settings provider contains global system-level device preferences.
 * 
 * @author TJ Tsai (128135)
 */
public final class HtcWrapSettings {
	
	private static final String TAG = "HtcWrapSettings";
	
	private static final boolean DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
	
	/**
	 * Combines two arrays into one.
	 * @param strArray1
	 * @param strArray2
	 * @return
	 * @author TJ Tsai (128135)
	 */
	private static String [] combineStringArrays(String [] strArray1, 
			String [] strArray2) {
		final int size = strArray1.length + strArray2.length;
		String [] strArray = new String[size];
		
		java.lang.System.arraycopy(strArray1, 0, 
				strArray, 0, strArray1.length);
		java.lang.System.arraycopy(strArray2, 0, 
				strArray, strArray1.length, strArray2.length);
		return strArray;
	}
	
    /**
     * default value type for resolution picker in #ACTION_RESOLUTION_PICKER.
     * The integer extra field #EXTRA_RESOLUTION_TYPE hold the default select resolution
     * the definition is DISPLAY_RESOLUTION_TYPE_AUTO to 480P
     * 
     */
     public static final int DISPLAY_RESOLUTION_TYPE_START = -1;
     public static final int DISPLAY_RESOLUTION_TYPE_AUTO = DISPLAY_RESOLUTION_TYPE_START + 1;
     public static final int DISPLAY_RESOLUTION_TYPE_720P = DISPLAY_RESOLUTION_TYPE_START + 2;
     public static final int DISPLAY_RESOLUTION_TYPE_480P = DISPLAY_RESOLUTION_TYPE_START + 3;

    /**
     *
     * Activity Intent Action:  Invoke the display resolution dialog to set resolution.
     * The int extra field #EXTRA_RESOLUTION_TYPE hold the default select resolution
     * 
     */
     public static final String ACTION_RESOLUTION_PICKER = "com.htc.content.intent.action.Resolution_dialog";

    /**
     * Extra key: Apply default resolution type for picker in intent action.
     * The integer extra field #EXTRA_RESOLUTION_TYPE hold the default resolution
     * in #ACTION_RESOLUTION_PICKER for the resolution picker
     * the definition is DISPLAY_RESOLUTION_TYPE_AUTO to 480P
     * 
     */
     public static final String EXTRA_RESOLUTION_TYPE = "com.htc.content.intent.extra.Extra_DEF_TYPE";

    /**
     *
     * Intent Action:  User assigned h.w. key action.
     * The int extra field #EXTRA_UAK_KEY_TYPE holds the User assigned h.w. key 1 based index
     * The boolean extra field #EXTRA_UAK_KEY_EVEN_LONGPRESS holds h.w. key event type, true : long press, false : push
     * 
     */
     public static final String ACTION_UAK_KEY_HAPPEN = "ACTION_UAK_TRIGGLE";

    /**
     * Extra key: Apply user defined h.w. key 1 base index in intent action.
     * The integer extra field #EXTRA_UAK_KEY_TYPE hold the 1 base index
     * in #ACTION_UAK_KEY_HAPPEN for the user defined h.w. key event
     * 
     */
     public static final String EXTRA_UAK_KEY_TYPE = "EXTRA_UAK_KEY_TYPE";

    /**
     * Extra key: Apply user defined h.w. key event type in intent action.
     * The boolean extra field #EXTRA_UAK_KEY_EVENT_LONGPRESS hold the boolean
     * in #ACTION_UAK_KEY_HAPPEN for the user defined h.w. key event type, true means long press, false means normal push
     * 
     */
     public static final String EXTRA_UAK_KEY_EVENT_LONGPRESS = "EXTRA_UAK_KEY_EVENT_LONGPRESS";

    // End of Intent actions for Settings

     
     
//	//===============================================================
//    // static classe: NameValueTable
//    //===============================================================
//	/**
//	 * @see {@link android.provider.Settings.NameValueTable}
//	 * @author TJ Tsai (128135)
//	 */
//	protected static class NameValueTable extends Settings.NameValueTable {
//		
//	}
	
	
	
	//===============================================================
    // static classe: System
    //===============================================================
	/**
	 * @see {@link android.provider.Settings.System}
	 * @author TJ Tsai (128135)
	 */
	public static class System extends HtcPublicSettings.System {
	        //[START] KengFu Chang, 2012.02.06
	        public static final String MODE_WCDMA = "mode_wcdma";
	        public static final String MODE_CDMA = "mode_cdma";
	        public static final String MODE_GSM = "mode_gsm";
	        //[END] KengFu Chang, 2012.02.06        

        // + Mark.sl_Wu@20111031: add API for show quick tip +
        private static ConcurrentHashMap<String, Boolean> sQuickTipMap;

        private static boolean sGlobalQuickTipFlag = true;

        public static boolean getQuickTipFlag(ContentResolver resolver, String name) {
            loadQuickTipState(resolver);

            Boolean appQuickTipFlag = sQuickTipMap.get(name);
            // app does not write intent-filter in AndroidManifest.xml
            // supposely, the app will show its quick tip every time
            if (appQuickTipFlag == null) {
                appQuickTipFlag = true;
            }

            if (DEBUG) {
                Log("package name: " + name);
                Log("appQuickTipFlag: " + appQuickTipFlag);
                printQuickTipState(resolver);
            }

            return sGlobalQuickTipFlag && appQuickTipFlag;
        }

        public static void disableQuickTipFlag(ContentResolver resolver, String name) {
            loadQuickTipState(resolver);

            // +[HTC] TJ Tsai, 2013.03.27, No need to check the existence. Just 
            // directly puts the disabled package in the collections.
            // if (sQuickTipMap.containsKey(name)) {
                sQuickTipMap.put(name, false);
            // }
            // -[HTC] TJ Tsai, 2013.03.27
            
            saveQuickTipState(resolver);

            if (DEBUG) {
                printQuickTipState(resolver);
            }
        }

        /**
         * It will call {@link #setShowQuickTip(ContentResolver, 
         * boolean)} and also send a broadcast.
         * @author TJ Tsai, 2012.06.21
         * @param context
         * @param bool
         */
        public static void setShowQuickTip(Context context, boolean bool) {
        	setShowQuickTip(context.getContentResolver(), bool);
        	
            Intent intent = new Intent("htc.intent.action.QUICK_TIPS_CHANGED");
            intent.putExtra("state", bool);
            context.sendBroadcast(intent);
        }
        
        public static void setShowQuickTip(ContentResolver resolver, boolean bool) {
            loadQuickTipState(resolver);

            sGlobalQuickTipFlag = bool;
            Set<String> set = sQuickTipMap.keySet();
            for (String packageName : set) {
                sQuickTipMap.put(packageName, bool);
            }
            
            saveQuickTipState(resolver);

            if (DEBUG) {
                printQuickTipState(resolver);
            }
        }
        
        /**
         * 
         * @author TJ Tsai, 2012.06.21
         * @param context
         * @return
         */
        public static boolean getShowQuickTip(Context context) {
            return getShowQuickTip(context.getContentResolver());
        }
        
        public static boolean getShowQuickTip(ContentResolver resolver) {
            loadQuickTipState(resolver);
            
            if (DEBUG) {
                printQuickTipState(resolver);
            }
            
            return sGlobalQuickTipFlag;
        }
        
        private static void saveQuickTipState(ContentResolver resolver) {
            if (DEBUG) {
                Log("saveQuickTipState()");
            }
            
            StringBuilder builder = new StringBuilder();

            // sGlobalQuickTipFlag need to set false if all packages are set to off
            sGlobalQuickTipFlag = sGlobalQuickTipFlag && sQuickTipMap.containsValue(true);
            builder.append(sGlobalQuickTipFlag);
            builder.append(';');
            
            for (Map.Entry<String, Boolean> entry : sQuickTipMap.entrySet()) {
                builder.append(entry.getKey()).append(':').append(entry.getValue()).append(';');
            }
            
            android.provider.Settings.System.putString(resolver, SHOW_ALL_QUICK_TIPS, builder.toString());
        }

        private static void loadQuickTipState(ContentResolver resolver) {
            if (DEBUG) {
                Log("loadQuickTipState()");
            }
            sQuickTipMap = new ConcurrentHashMap<String, Boolean>();

            // prevent null pointer exception
            
            String value = android.provider.Settings.System.getString(resolver, SHOW_ALL_QUICK_TIPS);
            if (value == null) {
                return;
            }
            
            String[] segments = value.split(";");
            for (int index = 0; index < segments.length; index++) {
                if (DEBUG) {
                    Log(new StringBuilder()
                            .append("QuickTip[").append(index).append("]=")
                            .append(segments[index])
                            .toString());
                }
                
                switch (index) {
                case 0:
                    sGlobalQuickTipFlag = Boolean.valueOf(segments[0]); 
                    break;
                default:
                    String[] pairs = segments[index].split(":");
                    sQuickTipMap.put(pairs[0], Boolean.valueOf(pairs[1]));
                    break;
                }
            }
            
            if (DEBUG) {
                printQuickTipState(resolver);
            }
        }

        private static void printQuickTipState(ContentResolver resolver) {
            Log(new StringBuilder()
                    .append("quick-tip-set: ").append(sQuickTipMap)
                    .append(", ")
                    .append(SHOW_ALL_QUICK_TIPS).append(": ")
                    .append(android.provider.Settings.System.getString(resolver, SHOW_ALL_QUICK_TIPS))
                    .toString());
        }
        // - Mark.sl_Wu@20111031 -        
        
        /**
         * The mapping of stream type (integer) to its setting.
         */
        public static final String[] VOLUME_SETTINGS = 
        	combineStringArrays(Settings.System.VOLUME_SETTINGS, 
        			new String[] {VOLUME_SYSTEM, VOLUME_DTMF});
        
         /**
          * Settings to backup. This is here so that it's in the same place as the settings
          * keys and easy to update.
          * 
          */
         public static final String[] SETTINGS_TO_BACKUP = 
        	 	combineStringArrays(Settings.System.SETTINGS_TO_BACKUP, 
        	 new String[] {
             WEATHER_WALLPAPER_ANIMATION_ALLOWED,
             WIFI_SLEEP_POLICY,
             BLUETOOTH_FTP_SETTINGS,
             BLUETOOTH_MAP_SETTINGS,
             INACTIVITY_TIME,
             NOTIFICATIONS_USE_RING_VOLUME,
             VOLUME_DTMF,

             // 2012-08-31 Denny - Begin - Added a new volume for all devices
            VOLUME_VOICE + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_RING + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_MUSIC + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_ALARM + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_EARPIECE_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_EARPIECE_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_RING + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_MUSIC + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_ALARM + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_SPEAKER_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_SPEAKER_NAME,

            VOLUME_VOICE + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_RING + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_MUSIC + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_ALARM + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADSET_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADSET_NAME,
            
            
            VOLUME_VOICE + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_RING + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_MUSIC + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_ALARM + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_WIRED_HEADPHONE_NAME,
            
            
            VOLUME_VOICE + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_RING + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_MUSIC + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_ALARM + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_NAME,
            
            
            
            VOLUME_VOICE + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_RING + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_MUSIC + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_ALARM + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME,
            
            
            VOLUME_VOICE + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_RING + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_MUSIC + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_ALARM + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME,
            
            
            VOLUME_VOICE + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_RING + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_MUSIC + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_ALARM + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_NAME,
            
            
            VOLUME_VOICE + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_RING + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_MUSIC + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_ALARM + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_RING + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_MUSIC + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_ALARM + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_RING + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_MUSIC + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_ALARM + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_AUX_DIGITAL_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_AUX_DIGITAL_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_RING + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_MUSIC + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_ALARM + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_ANLG_DOCK_HEADSET_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_RING + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_MUSIC + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_ALARM + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DGTL_DOCK_HEADSET_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_RING + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_MUSIC + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_ALARM + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_ACCESSORY_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_ACCESSORY_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_RING + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_MUSIC + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_ALARM + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_DEVICE_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_USB_DEVICE_NAME,
            // 2012-08-31 Denny - End - Added a new volume for all devices
            // 2012-11-09 Denny - Begin - Added a new volume for headset
            VOLUME_VOICE + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_RING + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_MUSIC + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_ALARM + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_DEVICE_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_DEVICE_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_RING + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_MUSIC + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_ALARM + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_TX_DEVICE_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_FM_TX_DEVICE_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_RING + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_MUSIC + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_ALARM + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_DIRECT_OUTPUT_NAME,
            
            VOLUME_VOICE + DEVICE_OUT_HDMI_NAME,
            VOLUME_SYSTEM + DEVICE_OUT_HDMI_NAME,
            VOLUME_RING + DEVICE_OUT_HDMI_NAME,
            VOLUME_MUSIC + DEVICE_OUT_HDMI_NAME,
            VOLUME_ALARM + DEVICE_OUT_HDMI_NAME,
            VOLUME_NOTIFICATION + DEVICE_OUT_HDMI_NAME,
            VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_HDMI_NAME,
            VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_HDMI_NAME,
            VOLUME_RING + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_HDMI_NAME,
            VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_HDMI_NAME,
            VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_HDMI_NAME,
            VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE + DEVICE_OUT_HDMI_NAME,
            
            // [Android-4.2][Porting] TJ Tsai, 2012.12.21, move from framework to wrapper
            // Lib3 porting by Alan Lee 20131120
            com.htc.lib1.settings.provider.Settings.System.VOLUME_FM, 
             
            PROFILE_PREFIX[0] + VOLUME_VOICE,
            PROFILE_PREFIX[0] + VOLUME_SYSTEM,
            PROFILE_PREFIX[0] + VOLUME_RING,
            PROFILE_PREFIX[0] + VOLUME_MUSIC,
            PROFILE_PREFIX[0] + VOLUME_ALARM,
            PROFILE_PREFIX[0] + VOLUME_NOTIFICATION,
            PROFILE_PREFIX[0] + VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[0] + VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[0] + VOLUME_RING + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[0] + VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[0] + VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[0] + VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[0] + Settings.System.MODE_RINGER,
            
            
            PROFILE_PREFIX[1] + VOLUME_VOICE,
            PROFILE_PREFIX[1] + VOLUME_SYSTEM,
            PROFILE_PREFIX[1] + VOLUME_RING,
            PROFILE_PREFIX[1] + VOLUME_MUSIC,
            PROFILE_PREFIX[1] + VOLUME_ALARM,
            PROFILE_PREFIX[1] + VOLUME_NOTIFICATION,
            PROFILE_PREFIX[1] + VOLUME_VOICE + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[1] + VOLUME_SYSTEM + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[1] + VOLUME_RING + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[1] + VOLUME_MUSIC + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[1] + VOLUME_ALARM + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[1] + VOLUME_NOTIFICATION + APPEND_FOR_LAST_AUDIBLE,
            PROFILE_PREFIX[1] + Settings.System.MODE_RINGER,
            // 2012-11-09 Denny - End - Added a new volume for headset
             VOLUME_DTMF + APPEND_FOR_LAST_AUDIBLE,
             
             BE_POLITE,					//sound settings / quiet ring on pickup
             POCKET_MODE,				//sound settings / pocket mode
             FLIP_TO_SPEAKER,                          //sound settings / flip to speaker, htc shawn
             WINDOW_ANIMATION_SCALE,     //display settings / animation
             TRANSITION_ANIMATION_SCALE, //display settings / animation

             // NOTIFICATION_SOUND,          //sound settings notification ringtone, Notification sound doesn't need backup&restore anymore
             SILENT_MODE,                 //sound settings silent mode
             FOTA_PERIODIC_CHECKIN,
             SETTING_ROAMING_RINGTONE,
             SETTING_SD_CARD_NOTIFICATIONS,
             PSAVER_ENABLE,
             PSAVER_SCHEDULE,

             HTC_APPLICATION_AUTOMATIC_STARTUP, //Settings / Application / Automatic startup
             HTC_APPLICATION_NOTIFICATION,      //Settings / Application / Notification
//             HTC_3D_HOME_SCREEN,                // Settings / Display / 3D Home Screen
             HTC_WIRELESS_SLEEP_MODE_ENABLED,  //Settings / Power
             HTC_WIRELESS_SLEEP_START_TIME,    //Settings / Power
             HTC_WIRELESS_SLEEP_END_TIME,      //Settings / Power
             
            // Charm settings
            CHARM_MESSAGE_NOTIFICATION,
            CHARM_PHONE_NOTIFICATION,
            CHARM_VOICE_MAIL_NOTIFICATION,
            CHARM_MESSAGE_RECEIVED_NOTIFICATION,
            CHARM_INDICATOR_SUPPORTED,
            
			// setting modify inside framework for Htc speak       
       	    HTCSPEAK_DEFALUT_LANG,
       	    //setting Power
       	    ENABLE_POWER_EFFICIENCY,
       	    SET_POWER_MODE,
       	    POWERSAVER_ENABLE,

       	    HTC_APP_STORAGE_LOCATION_BACKUP, //Settings / storage / App storage locations settings / *
            // + Mark.sl_Wu@20120222: new key for Sense4.0 feature +
       	    HTC_GESTURES_ENABLED,
       	    HTC_ANIMATION_ENABLED,
       	    HTC_WINDOW_ANIMATION_SCALE,
       	    HTC_TRANSITION_ANIMATION_SCALE,
       	    // - Mark.sl_Wu@20120222: new key for Sense4.0 feature -

       	    // marked out by Alan Lee since it's device dependent - 20130705
       	    //HTC_BEATS_AUDIO,	

       	    // Mark.SL_Wu@20120712: Backup/Restore of settings for Boomerang
       	    HTC_FONT_SIZE,
       	    COMPASS_WARNING,
       	    DATE_FORMAT_SHORT,
       	    ACCELEROMETER_ROTATION,

            HTC_MAGNIFIER_SETTING,
            
            //+[Android-4.2][Porting] TJ Tsai, 2012.12.21, move from framework to wrapper
            BLUETOOTH_NAME, 
            BLUETOOTH_FTP_SETTINGS,
            BLUETOOTH_MAP_SETTINGS,
            //-[Android-4.2][Porting] TJ Tsai, 2012.12.21, move from framework to wrapper
             //Denny.cy@20121221:Backup/Restore for restore ICS vibrate key to JB
            VIBRATE_ON,
            //Denny.cy@20121227:Backup/Restore pull to refsh
            HTC_PULL_TO_FRESH_SOUND_ENABLED,
            //Denny.cy@20121227:Backup/Restore EMERGENCY_TONE, the key was move to
            //* global table but need add white list in system table for restore backup 
            //* from old version
            EMERGENCY_TONE,                           // moved to global
            
            // +[HTC] TJ Tsai, 2013.05.17, [Sound] Do not disturb
            // No need to backup for HTC_DND_FEATURE_ENABLED
            // (1) There exists the time gap between the current 
            //     device and the target going-to-restore device.
            // (2) The current device enables the DND, but it doesn't 
            //     mean another device will enable the DND. It depends
            //     on a SIM card.
            // HTC_DND_FEATURE_ENABLED,
            HTC_DND_AUTO_TURN_OFF_ENABLED,
            HTC_DND_AUTO_TURN_OFF_MINUTES,
            HTC_DND_PLAY_SOUND_ENABLED,
            // -[HTC] TJ Tsai, 2013.05.17, [Sound] Do not disturb
            NOTIFICATIONS_USE_RING_VOLUME,//[HTC] Denny.cy, 2013.07.19
         });
         
	}
	
	
	
	//===============================================================
    // static classe: Secure
    //===============================================================
	/**
	 * @see {@link android.provider.Settings.Secure}
	 * @author TJ Tsai (128135)
	 */
	public static class Secure extends HtcPublicSettings.Secure {
		/*************************************************************
		 * Adds Secure keys in {@link HtcISettingsSecure}!
		 ************************************************************/
		/*************************************************************
		 * DO NOT add keys here!
		 ************************************************************/
		/*************************************************************
		 * Adds Secure keys in {@link HtcISettingsSecure}!
		 ************************************************************/
		
        /**
         * [Override] See {@link android.provider.Settings.Secure#
         * setLocationProviderEnabled(ContentResolver, String, boolean)}
         * 
         * @param cr the content resolver to use
         * @param provider the location provider to enable or disable
         * @param enabled true if the provider should be enabled
         * @see {@link android.provider.Settings.Secure#
         * setLocationProviderEnabled(ContentResolver, String, boolean)
         * @author TJ Tsai (128135) 2011.09.15
         */
        public static final void setLocationProviderEnabled(
        		ContentResolver cr, String provider, boolean enabled) {
        	//We will monitor the LOCATION_PROVIDERS_ALLOWED if the 
        	//ROM is under debugging.
        	if (DEBUG) {
        		traceCallingStack();
        	}
        	
        	//redirects to the original method
        	android.provider.Settings.Secure.setLocationProviderEnabled(cr, provider, 
        			enabled);
        }
        
        /**
         * 
         */
        public static final String[] SETTINGS_TO_BACKUP = 
        		combineStringArrays(Settings.Secure.SETTINGS_TO_BACKUP,
	    		new String[] {
			            TTS_DEFAULT_VARIANT, // +Htc, need backup "tts_default_variant" or "the text-to-speech / language" can't be backuped completely

			            WIFI_NUM_ALLOWED_CHANNELS,
                        //HTC_WIFI_START
                        WIFI_SECURE_NETWORKS_AVAILABLE_NOTIFICATION_ON,
                        //HTC_WIFI_END
			            LOCATION_PROVIDERS_ALLOWED,
			            // BH_Lin@20110309	----------------------------------------------->
			            // purpose: for Sprint Data Roaming Enhancement using.
			            DATA_ROAMING_ALLOWED,
			            DATA_ROAMING_BLOCKED,
			            DATA_ROAMING_GUARD_ALLOWED,
			            DATA_ROAMING_GUARD_BLOCKED,
			            VOICE_ROAMING_ALLOWED,
			            VOICE_ROAMING_BLOCKED,
			            VOICE_ROAMING_GUARD_ALLOWED,
			            VOICE_ROAMING_GUARD_BLOCKED,
			            SMS_ROAMING_GUARD_ALLOWED,
			            // BH_Lin@20110309	-----------------------------------------------<
			            HTC_LOCATE_ALLOWED,
			            INTEGRATE_GOOGLE_NAVIGATION,
			//+htc: Pen event
			            PEN_ATTR_FOR_EACH_APP,
			            PEN_ENABLE_SKETCH,
			            PEN_AS_TOUCH,
			            PEN_UPPER_BUTTON_ID,
			            PEN_LOWER_BUTTON_ID,
			//+htc: Pen event
			            UI_NIGHT_MODE,

			          //Settings / Privacy / Display message text on lock screen
			            HTC_NEW_MESSAGE_NOTIFICATION,
			            //@+Ausmus 20110516
			            VZW_GLOBAL_ROAMING_OPTIONS,
			            //@-Ausmus 20110516

			            ENABLE_HTC_FASTBOOT,
			            HTC_MUSIC_BYPASS_ENABLED, // + Mark.sl_Wu@20120222: new key for Sense4.0 feature -
			            // Mark.SL_Wu@20120712: Backup/Restore of settings for Boomerang
			            HTC_PHONE_NOTIFICATION_PREVIEW,
			            HTC_MESSAGE_NOTIFICATION_PREVIEW,
			            // + Add Lockscreen settings to backup/restore list by Alan 20130808
			            // ref mail title: [sense55 lockscreen backup restore]
			            HTC_LOCKSCREEN_PRODUCTIVITY_VOICE_MAIL,
			            HTC_LOCKSCREEN_PRODUCTIVITY_MAIL,
			            HTC_LOCKSCREEN_PRODUCTIVITY_CALENDAR_EVENTS,
			            // - Add Lockscreen settings to backup/restore list by Alan 20130808 
			            
			            INCALL_POWER_BUTTON_BEHAVIOR,
			            LONG_PRESS_TIMEOUT,
			            SPELL_CHECKER_ENABLED,
			            //[+]Add to back up & restore tty mode Ryan@20121106
			            PREFERRED_TTY_MODE,
			            TTY_MODE_ENABLED,
			            //[-]Add to back up & restore tty mode Ryan@20121106
			            
			            //+Add by Alan by Allen Hsu's request 20130716
			            ROAMING_SOUND_ON,
			            //-Add by Alan by Allen Hsu's request 20130716
			            
			            // + Add by Alan for "Phone incoming call" feature
			            // ref mail title: "M8_UL_K44_SENSE60, #671"
			            HTC_LOCKSCREEN_SHOW_PRIVATE_CONTACTS,
			            // - Add by Alan for "Phone incoming call" feature
	        	});
        
        
        // BH_Lin@20110309	--------------------------------------------------->
        // -------------------------------------------------------------------->
        /**
         * Helper method for determining if a data roaming is allowed.
         * @param cr the content resolver to use
         * @param provider the data roaming guard to query
         * @return true if the data roaming guard is enabled
         */
        public static final boolean isDataRoamingAllowed(ContentResolver cr, String provider) {
        	Log.d(TAG, "HtcWrapSettings.Secure.isDataRoamingAllowed");
        	
//        	return android.provider.Settings.Secure.Agent.isDataRoamingAllowed(cr, provider);
        	return (Boolean) SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"isDataRoamingAllowed", 
        			new Class[]{ContentResolver.class, String.class}, 
        			cr, provider);
        }

        /**
         * Thread-safe method for enabling or disabling a single data roaming allowed.
         * @param cr the content resolver to use
         * @param provider the data roaming guard to enable or disable
         * @param enabled true if the provider should be enabled
         */
        public static final void setDataRoamingAllowed(ContentResolver cr, String provider, boolean enabled) {
            // to ensure thread safety, we write the provider name with a '+' or '-'
            // and let the SettingsProvider handle it rather than reading and modifying
            // the list of enabled providers.
        	Log.d(TAG, "HtcWrapSettings.Secure.setDataRoamingAllowed");
        	
//        	android.provider.Settings.Secure.Agent.setDataRoamingAllowed(cr, provider, enabled);
        	SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"setDataRoamingAllowed", 
        			new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, 
        			cr, provider, enabled);
        }
        
        /**
         * Helper method for determining if a data roaming is blocked.
         * @param cr the content resolver to use
         * @param provider the data roaming guard to query
         * @return true if the data roaming guard is blocked
         */
        public static final boolean isDataRoamingBlocked(ContentResolver cr, String provider) {
        	Log.d(TAG, "HtcWrapSettings.Secure.isDataRoamingBlocked");

//        	return android.provider.Settings.Secure.Agent.isDataRoamingBlocked(cr, provider);
        	return (Boolean) SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"isDataRoamingBlocked", 
        			new Class[]{ContentResolver.class, String.class}, 
        			cr, provider);
        }

        /**
         * Thread-safe method for enabling or disabling a single data roaming.
         * @param cr the content resolver to use
         * @param provider the data roaming to enable or disable
         * @param enabled true if the provider should be enabled
         */
        public static final void setDataRoamingBlocked(ContentResolver cr, String provider, boolean enabled) {
            // to ensure thread safety, we write the provider name with a '+' or '-'
            // and let the SettingsProvider handle it rather than reading and modifying
            // the list of enabled providers.
        	Log.d(TAG, "HtcWrapSettings.Secure.setDataRoamingBlocked");
        	
//        	android.provider.Settings.Secure.Agent.setDataRoamingBlocked(cr, provider, enabled);
        	SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"setDataRoamingBlocked", 
        			new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, 
        			cr, provider, enabled);
        }
        

        /**
         * Helper method for determining if a data roaming guard is allowed.
         * @param cr the content resolver to use
         * @param provider the data roaming guard to query
         * @return true if the data roaming guard is enabled
         */
        public static final boolean isDataRoamingGuardAllowed(ContentResolver cr, String provider) {
        	Log.d(TAG, "HtcWrapSettings.Secure.isDataRoamingGuardAllowed");
        	
//        	return android.provider.Settings.Secure.Agent.isDataRoamingGuardAllowed(cr, provider);
        	return (Boolean) SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"isDataRoamingGuardAllowed", 
        			new Class[]{ContentResolver.class, String.class}, 
        			cr, provider);
        }

        /**
         * Thread-safe method for enabling or disabling a single data roaming guard.
         * @param cr the content resolver to use
         * @param provider the data roaming guard to enable or disable
         * @param enabled true if the provider should be enabled
         */
        public static final void setDataRoamingGuardAllowed(ContentResolver cr, String provider, boolean enabled) {
        	Log.d(TAG, "HtcWrapSettings.Secure.setDataRoamingGuardAllowed");
        	
//        	android.provider.Settings.Secure.Agent.setDataRoamingGuardAllowed(cr, provider, enabled);
        	SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"setDataRoamingGuardAllowed", 
        			new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, 
        			cr, provider, enabled);
        }
        
        /**
         * Helper method for determining if a data roaming guard is blocked.
         * @param cr the content resolver to use
         * @param provider the data roaming guard to query
         * @return true if the data roaming guard is blocked
         */
        public static final boolean isDataRoamingGuardBlocked(ContentResolver cr, String provider) {
        	Log.d(TAG, "HtcWrapSettings.Secure.isDataRoamingGuardBlocked");
        	
//        	return android.provider.Settings.Secure.Agent.isDataRoamingGuardBlocked(cr, provider);
        	return (Boolean) SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"isDataRoamingGuardBlocked", 
        			new Class[]{ContentResolver.class, String.class}, 
        			cr, provider);
        }

        /**
         * Thread-safe method for enabling or disabling a single data roaming guard Blocker.
         * @param cr the content resolver to use
         * @param provider the data roaming guard to enable or disable
         * @param enabled true if the provider should be enabled
         */
        public static final void setDataRoamingGuardBlocked(ContentResolver cr, String provider, boolean enabled) {
        	Log.d(TAG, "HtcWrapSettings.Secure.setDataRoamingGuardBlocked");
        	
//        	android.provider.Settings.Secure.Agent.setDataRoamingGuardBlocked(cr, provider, enabled);
        	SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"setDataRoamingGuardBlocked", 
        			new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, 
        			cr, provider, enabled);
        }
        
        /**
         * Helper method for determining if a voice roaming is allowed.
         * @param cr the content resolver to use
         * @param provider the voice roaming enabler to query
         * @return true if the voice roaming enabler is enabled
         */
        public static final boolean isVoiceRoamingAllowed(ContentResolver cr, String provider) {
        	Log.d(TAG, "HtcWrapSettings.Secure.isVoiceRoamingAllowed");
        	
//        	return android.provider.Settings.Secure.Agent.isVoiceRoamingAllowed(cr, provider);
        	return (Boolean) SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"isVoiceRoamingAllowed", 
        			new Class[]{ContentResolver.class, String.class}, 
        			cr, provider);
        }

        /**
         * Thread-safe method for enabling or disabling a single voice roaming enabler.
         * @param cr the content resolver to use
         * @param provider the voice roaming enabler to enable or disable
         * @param enabled true if the provider should be enabled
         */
        public static final void setVoiceRoamingAllowed(ContentResolver cr, String provider, boolean enabled) {
        	Log.d(TAG, "HtcWrapSettings.Secure.setVoiceRoamingAllowed");
        	
//        	android.provider.Settings.Secure.Agent.setVoiceRoamingAllowed(cr, provider, enabled);
        	SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"setVoiceRoamingAllowed", 
        			new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, 
        			cr, provider, enabled);
        }
        
        /**
         * Helper method for determining if a voice roaming is blocked.
         * @param cr the content resolver to use
         * @param provider the voice roaming blocker to query
         * @return true if the voice roaming blcoker is enabled
         */
        public static final boolean isVoiceRoamingBlocked(ContentResolver cr, String provider) {
        	Log.d(TAG, "HtcWrapSettings.Secure.isVoiceRoamingBlocked");
        	
//        	return android.provider.Settings.Secure.Agent.isVoiceRoamingBlocked(cr, provider);
        	return (Boolean) SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"isVoiceRoamingBlocked", 
        			new Class[]{ContentResolver.class, String.class}, 
        			cr, provider);
        }

        /**
         * Thread-safe method for enabling or disabling a single voice roaming blocker.
         * @param cr the content resolver to use
         * @param provider the voice roaming blocker to enable or disable
         * @param enabled true if the provider should be enabled
         */
        public static final void setVoiceRoamingBlocked(ContentResolver cr, String provider, boolean enabled) {
        	Log.d(TAG, "HtcWrapSettings.Secure.setVoiceRoamingBlocked");
        	
//        	android.provider.Settings.Secure.Agent.setVoiceRoamingBlocked(cr, provider, enabled);
        	SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"setVoiceRoamingBlocked", 
        			new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, 
        			cr, provider, enabled);
        }
        
        /**
         * Helper method for determining if a voice roaming guard is allowed.
         * @param cr the content resolver to use
         * @param provider the voice roaming guard to query
         * @return true if the voice roaming guard is enabled
         */
        public static final boolean isVoiceRoamingGuardAllowed(ContentResolver cr, String provider) {
        	Log.d(TAG, "HtcWrapSettings.Secure.isVoiceRoamingGuardAllowed");
        	
//        	return android.provider.Settings.Secure.Agent.isVoiceRoamingGuardAllowed(cr, provider);
        	return (Boolean) SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"isVoiceRoamingGuardAllowed", 
        			new Class[]{ContentResolver.class, String.class}, 
        			cr, provider);
        }

        /**
         * Thread-safe method for enabling or disabling a single voice roaming guard.
         * @param cr the content resolver to use
         * @param provider the voice roaming guard to enable or disable
         * @param enabled true if the provider should be enabled
         */
        public static final void setVoiceRoamingGuardAllowed(ContentResolver cr, String provider, boolean enabled) {
        	Log.d(TAG, "HtcWrapSettings.Secure.setVoiceRoamingGuardAllowed");
        	
//        	android.provider.Settings.Secure.Agent.setVoiceRoamingGuardAllowed(cr, provider, enabled);
        	SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"setVoiceRoamingGuardAllowed", 
        			new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, 
        			cr, provider, enabled);
        }
        
        /**
         * Helper method for determining if a voice roaming guard is blocked.
         * @param cr the content resolver to use
         * @param provider the voice roaming guard to query
         * @return true if the voice roaming guard is enabled
         */
        public static final boolean isVoiceRoamingGuardBlocked(ContentResolver cr, String provider) {
        	Log.d(TAG, "HtcWrapSettings.Secure.isVoiceRoamingGuardBlocked");
        	
//        	return android.provider.Settings.Secure.Agent.isVoiceRoamingGuardBlocked(cr, provider);
        	return (Boolean) SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"isVoiceRoamingGuardBlocked", 
        			new Class[]{ContentResolver.class, String.class}, 
        			cr, provider);
        }

        /**
         * Thread-safe method for enabling or disabling a single voice roaming guard blocker.
         * @param cr the content resolver to use
         * @param provider the voice roaming guard to enable or disable
         * @param enabled true if the provider should be enabled
         */
        public static final void setVoiceRoamingGuardBlocked(ContentResolver cr, String provider, boolean enabled) {
        	Log.d(TAG, "HtcWrapSettings.Secure.setVoiceRoamingGuardBlocked");
        	
//        	android.provider.Settings.Secure.Agent.setVoiceRoamingGuardBlocked(cr, provider, enabled);
        	SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"setVoiceRoamingGuardBlocked", 
        			new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, 
        			cr, provider, enabled);
        }
        
        /**
         * Helper method for determining if a sms roaming is allowed.
         * @param cr the content resolver to use
         * @param provider the sms roaming guard to query
         * @return true if the sms roaming guard is enabled
         */
        public static final boolean isSmsRoamingGuardAllowed(ContentResolver cr, String provider) {
        	Log.d(TAG, "HtcWrapSettings.Secure.isSmsRoamingGuardAllowed");

//        	return android.provider.Settings.Secure.Agent.isSmsRoamingGuardAllowed(cr, provider);
        	return (Boolean) SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"isSmsRoamingGuardAllowed", 
        			new Class[]{ContentResolver.class, String.class}, 
        			cr, provider);
        }

        /**
         * Thread-safe method for enabling or disabling a single sms roaming.
         * @param cr the content resolver to use
         * @param provider the sms roaming to enable or disable
         * @param enabled true if the provider should be enabled
         */
        public static final void setSmsRoamingGuardAllowed(ContentResolver cr, String provider, boolean enabled) {
        	Log.d(TAG, "HtcWrapSettings.Secure.setSmsRoamingGuardAllowed");
        	
//        	android.provider.Settings.Secure.Agent.setSmsRoamingGuardAllowed(cr, provider, enabled);
        	SettingsReflectionUtil.invokeMethod(
        			"android.provider.HtcISettingsSecure$Agent", 
        			"setSmsRoamingGuardAllowed", 
        			new Class[]{ContentResolver.class, String.class, Boolean.TYPE}, 
        			cr, provider, enabled);
        }
        
        // --------------------------------------------------------------------<
        // BH_Lin@20110309	---------------------------------------------------<

    }

	//===============================================================
    // static classe: Global
    //===============================================================
	/**
	 * @see {@link android.provider.Settings.System}
	 * @author TJ Tsai (128135)
	 */
	public static class Global extends HtcPublicSettings.Global {

		/*************************************************************
		 * Adds Global keys in {@link HtcISettingsGlobal}!
		 ************************************************************/
		/*************************************************************
		 * DO NOT add keys here!
		 ************************************************************/

		/**
         * 
         * Backup list extension for Global table</br></br>
         * added by Alan Lee 20130401
         */
        public static final String[] SETTINGS_TO_BACKUP = 
        		combineStringArrays(Settings.Global.SETTINGS_TO_BACKUP,
	    		new String[] {
        				INSTALL_NON_MARKET_APPS
	        	});
        
		/*************************************************************
		 * Adds Global keys in {@link Settings$HtcIGlobal}!
		 ************************************************************/
		/*************************************************************
		 * DO NOT add keys here!
		 ************************************************************/
	}
	

    
    /**
     * <P>Tries to trace the stacks from an invocation for a specific 
     * purpose.</P>
     *  
     * @author TJ Tsai (128135) 2011.09.15
     */
    private static void traceCallingStack() {
    	if (!DEBUG) {
    		return; //do not dump aynthing.
    	}
    	
    	final boolean SUPPORT_PS_COMMAND = false;
    	long startTime = 0, endTime = 0, elapsedTime;
    	
    	startTime = SystemClock.elapsedRealtime();
    	
    	Log(">> traceCallingStack()");
    	Log("Process.myPid(): " + Process.myPid());
    	Log("Process.myTid(): " + Process.myTid());
    	Log("Process.myUid(): " + Process.myUid());
    	Log("\n");
    	
    	//Prints to the standard error stream a text representation of
    	//the current stack for this Thread.
    	Log("\n");
    	Thread.dumpStack();
    	Log("\n");
    	
    	endTime = SystemClock.elapsedRealtime();
		elapsedTime = endTime - startTime;
    	Log("<< traceCallingStack(): " + elapsedTime + "(ms)");
    }
    
    /**
     * Dumps the message.
     * @param message
     * @author TJ Tsai (128135) 2011.09.15
     */
    private static void Log(String message) {
    	Log.d(TAG, message);
    }
}

