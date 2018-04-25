// 
// ======
// Usage:
// ======
// When you publish your key(s), 
//
//    #Step 1: 
//       copy your key(s) from Settings.java (which in the same folder)
//
//    #Step 2:
//       change '@hide' to '' 
//       (extension hide, HTC: visible, 3rd party: invisible)
//
//    #Step 3:
//       please help add the '@author' field in the comment.
//
// =====
// Note:
// =====
//    (1) Please DO NOT modify Settings.java. 
//
//    (2) If you don't know how to do, please see the sample below 
//        (search the keyword: WIFI_SAVED_STATE).
//

package com.htc.lib1.settings.provider;

import android.os.Build;


/**
 * 
 * <H3>The class is the intermediate layer that is used to publish  
 * hidden APIs from {@link Settings}.</H3>
 * 
 * <H3>Hierarchy diagram:</H3>
 * <PRE>
 *   {@link HtcWrapSettings} extends {@link HtcPublicSettings}
 *   {@link HtcPublicSettings} extends {@link Settings}
 * </PRE>
 * 
 * @author TJ Tsai
 * @since {@link Build.VERSION_CODES#JELLY_BEAN_MR1}, 2013.03.13
 */
/* default */ class HtcPublicSettings extends Settings {
	
	public static class System extends Settings.System {
		
        /**
         * CDMA only settings
         * DTMF tone type played by the dialer when dialing.
         *                 0 = Normal
         *                 1 = Long
         * 
         */
        public static final String DTMF_TONE_TYPE_WHEN_DIALING = "dtmf_tone_type";
        
        /**
         * Whether the hearing aid is enabled. The value is
         * boolean (1 or 0).
         * 
         */
        public static final String HEARING_AID = "hearing_aid";
        
        /**
         * Whether to play sounds when the keyguard is shown and dismissed.
         * 
         */
        public static final String LOCKSCREEN_SOUNDS_ENABLED = "lockscreen_sounds_enabled";
        
        /**
         * Whether the notification LED should repeatedly flash when a notification is
         * pending. The value is boolean (1 or 0).
         * 
         */
        public static final String NOTIFICATION_LIGHT_PULSE = "notification_light_pulse";
        
        /**
         * Receive incoming SIP calls?
         * 0 = no
         * 1 = yes
         * 
         */
        public static final String SIP_RECEIVE_CALLS = "sip_receive_calls";
        
        /**
         * Call Preference String.
         * "SIP_ALWAYS" : Always use SIP with network access
         * "SIP_ADDRESS_ONLY" : Only if destination is a SIP address
         * "SIP_ASK_ME_EACH_TIME" : Always ask me each time
         * 
         */
        public static final String SIP_CALL_OPTIONS = "sip_call_options";
        
        /**
         * One of the sip call options: Always use SIP with network access.
         * 
         */
        public static final String SIP_ALWAYS = "SIP_ALWAYS";
        
        /**
         * One of the sip call options: Always ask me each time.
         * 
         */
        public static final String SIP_ASK_ME_EACH_TIME = "SIP_ASK_ME_EACH_TIME";
        
        /**
         * Whether silent mode should allow vibration feedback. This is used
         * internally in AudioService and the Sound settings activity to
         * coordinate decoupling of vibrate and silent modes. This setting
         * will likely be removed in a future release with support for
         * audio/vibe feedback profiles.
         *
         * Not used anymore. On devices with vibrator, the user explicitly selects
         * silent or vibrate mode.
         * Kept for use by legacy database upgrade code in DatabaseHelper.
         * 
         */
        public static final String VIBRATE_IN_SILENT = "vibrate_in_silent";
        
	} // System
	/////////////////////////////////////////////////////////////////
	
	
	
	public static class Secure extends Settings.Secure {
        /**
         * Controls whether settings backup is enabled.
         * Type: int ( 0 = disabled, 1 = enabled )
         * 
         */
        public static final String BACKUP_ENABLED = "backup_enabled";
        
        /**
         * Whether the enhanced voice privacy mode is enabled.
         * 0 = normal voice privacy
         * 1 = enhanced voice privacy
         * 
         */
        public static final String ENHANCED_VOICE_PRIVACY_ENABLED = "enhanced_voice_privacy_enabled";
        
	} // Secure
	/////////////////////////////////////////////////////////////////
	
	
	
	public static class Global extends Settings.Global {
		
		/**
		 * Used to save the Wifi_ON state prior to tethering.
		 * This state will be checked to restore Wifi after
		 * the user turns off tethering.
		 *
		 * 
		 * @author SKY.KH_LU
		 */
		public static final String WIFI_SAVED_STATE = "wifi_saved_state";
		
        /**
         * The CDMA roaming mode 0 = Home Networks, CDMA default
         *                       1 = Roaming on Affiliated networks
         *                       2 = Roaming on any networks
         * 
         */
        public static final String CDMA_ROAMING_MODE = "roaming_settings";
        
        /**
         * CDMA Cell Broadcast SMS
         *                            0 = CDMA Cell Broadcast SMS disabled
         *                            1 = CDMA Cell Broadcast SMS enabled
         * 
         */
        public static final String CDMA_CELL_BROADCAST_SMS =
                "cdma_cell_broadcast_sms";
        
        /**
         * The CDMA subscription mode 0 = RUIM/SIM (default)
         *                                1 = NV
         * 
         */
        public static final String CDMA_SUBSCRIPTION_MODE = "subscription_mode";
        
        /**
         * Default install location value.
         * 0 = auto, let system decide
         * 1 = internal
         * 2 = sdcard
         * 
         */
        public static final String DEFAULT_INSTALL_LOCATION = "default_install_location";
        
        /**
         * Whether mobile data connections are allowed by the user.  See
         * ConnectivityManager for more info.
         * 
         */
        public static final String MOBILE_DATA = "mobile_data";
        
        /**
         * The preferred network mode   7 = Global
         *                              6 = EvDo only
         *                              5 = CDMA w/o EvDo
         *                              4 = CDMA / EvDo auto
         *                              3 = GSM / WCDMA auto
         *                              2 = WCDMA only
         *                              1 = GSM only
         *                              0 = GSM / WCDMA preferred
         * 
         */
        public static final String PREFERRED_NETWORK_MODE =
                "preferred_network_mode";
        
        /**
         * The preferred TTY mode     0 = TTy Off, CDMA default
         *                            1 = TTY Full
         *                            2 = TTY HCO
         *                            3 = TTY VCO
         * 
         */
        public static final String PREFERRED_TTY_MODE =
                "preferred_tty_mode";
        
        /**
         * Let user pick default install location.
         *
         * 
         */
        public static final String SET_INSTALL_LOCATION = "set_install_location";
        
        /**
         * A comma separated list of radios that should to be disabled when airplane mode
         * is on, but can be manually reenabled by the user.  For example, if RADIO_WIFI is
         * added to both AIRPLANE_MODE_RADIOS and AIRPLANE_MODE_TOGGLEABLE_RADIOS, then Wifi
         * will be turned off when entering airplane mode, but the user will be able to reenable
         * Wifi in the Settings app.
         *
         * {}
         */
        public static final String AIRPLANE_MODE_TOGGLEABLE_RADIOS = "airplane_mode_toggleable_radios";
        
        /**
         * CDMA only settings
         * Whether the auto retry is enabled. The value is
         * boolean (1 or 0).
         * 
         */
        public static final String CALL_AUTO_RETRY = "call_auto_retry";
        
        /**
         * CDMA only settings
         * Emergency Tone  0 = Off
         *                 1 = Alert
         *                 2 = Vibrate
         * 
         */
        public static final String EMERGENCY_TONE = "emergency_tone";
        
	} // Global
	/////////////////////////////////////////////////////////////////
	
}
