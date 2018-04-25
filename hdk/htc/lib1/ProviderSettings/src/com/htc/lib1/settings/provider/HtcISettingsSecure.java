package com.htc.lib1.settings.provider;

import android.os.Build;


/**
 * 
 * This interface is used to add HTC's secure keys, and add some 
 * related APIs.
 * 
 * @since {@link Build.VERSION_CODES#JELLY_BEAN}, 2013.03.14
 * @author TJ Tsai
 */
/* default */ interface HtcISettingsSecure {

	/**
	 * MMS - URL to use for HTTP "x-wap-profile" header
	 */
	public static final String MMS_X_WAP_PROFILE_URL // Arc
	= "mms_x_wap_profile_url";

	/**
	 * OMADM limit user to control bt/wifi/camera... 0=allow. 1=deny.
	 * 
	 * 
	 */
	public static final String DENY_USER_CONTROL = "deny_user_control";

	// +HTC
	/**
	 * Whether or not playing roaming sound is enabled (0 = false, 1 = true) for
	 * connecting to data romaing netwrok is completed.
	 * 
	 * 
	 */
	public static final String ROAMING_SOUND_ON = "roaming_sound_on";
	// -HTC

	// ** [start] Bonian Chen - 20091215 modified for data roaming guard
	/**
	 * Whether or not block data roaming for different network condition. (0 =
	 * default - ask while external roaming, 1 = always ask while
	 * internal/external roaming, 2 = never bother user)
	 * 
	 * 
	 */
	public static final String DATA_ROAMING_GUARD = "data_roaming_guard";
	// ** [end] Bonian Chen

	// ++ Sam_Chao@20100312: For national roaming.

	/**
	 * Host name and port for a user-selected proxy.
	 */
	public static final String WIFI_HTTP_PROXY_ON = "wifi_http_proxy_on";

	/**
	 * Host name and port for a user-selected proxy.
	 */
	public static final String HTTP_PROXY_SERVER = "http_proxy_server";

	/**
	 * Host name and port for a user-selected proxy.
	 */
	public static final String HTTP_PROXY_PORT = "http_proxy_port";

	// BH_Lin@20110309 --------------------------------------------------->
	// purpose:
	/**
	 * Comma-separated list of data roaming providers that activities may
	 * access.
	 */
	public static final String DATA_ROAMING_ALLOWED = "data_roaming_allowed";

	/**
	 * Comma-separated list of data roaming providers for oma that activities
	 * may access.
	 */
	public static final String DATA_ROAMING_BLOCKED = "data_roaming_blocked";

	/**
	 * Comma-separated list of data roaming guard providers that activities may
	 * access.
	 */
	public static final String DATA_ROAMING_GUARD_ALLOWED = "data_roaming_guard_allowed";

	/**
	 * Comma-separated list of data roaming guard providers for oma that
	 * activities may access.
	 */
	public static final String DATA_ROAMING_GUARD_BLOCKED = "data_roaming_guard_blocked";

	/**
	 * Comma-separated list of voice roaming providers that activities may
	 * access.
	 */
	public static final String VOICE_ROAMING_ALLOWED = "voice_roaming_allowed";

	/**
	 * Comma-separated list of voice roaming providers for oma that activities
	 * may access.
	 */
	public static final String VOICE_ROAMING_BLOCKED = "voice_roaming_blocked";

	/**
	 * Comma-separated list of voice roaming providers for oma that activities
	 * may access.
	 */
	public static final String VOICE_ROAMING_GUARD_ALLOWED = "voice_roaming_guard_allowed";

	/**
	 * Comma-separated list of voice roaming guard providers for oma that
	 * activities may access.
	 */
	public static final String VOICE_ROAMING_GUARD_BLOCKED = "voice_roaming_guard_blocked";

	/**
	 * Comma-separated list of sms roaming providers for outgoing sms that
	 * activities may access.
	 */
	public static final String SMS_ROAMING_GUARD_ALLOWED = "sms_roaming_guard_allowed";

	// BH_Lin@20110309 ---------------------------------------------------<

	// BH_Lin@20100105 --------------------------------------------------->
	// purpose:
	/**
	 * Comma-separated list of gps one that activities may access.
	 * 
	 * 
	 */
	public static final String GPSONE_ALLOWED = "gpsone_allowed";
	// BH_Lin@20100105 ---------------------------------------------------<

	/**
	 * Allows a user to trace his/her phone when it is lost or stolen.
	 */
	public static final String HTC_LOCATE_ALLOWED = "htc_locate_allowed";

	// + Mark.sl_Wu@20110714: add key for collect location data +
	/**
	 * Allows HTC to collect depersonalized location data to improve location
	 * service.
	 */
	public static final String HTC_COLLECT_LOCATION_DATA = "htc_collect_location_data";
	// - Mark.sl_Wu@20110714 -

	/**
	 * Allows a user to integrate with both Google navigation and HTC Locations.
	 * It's a boolean option. (0 = false, 1 = true)
	 */
	public static final String INTEGRATE_GOOGLE_NAVIGATION = "integrate_google_navigation";

	// +htc: Pen event
	/**
	 * Remember the recent used pens for each application 1 = enable 0 = disable
	 * 
	 * 
	 */
	public static final String PEN_ATTR_FOR_EACH_APP = "pen_attr_for_each_app";

	/**
	 * The default value for PEN_ATTR_FOR_EACH_APP
	 * 
	 * 
	 */
	public static final int PEN_ATTR_FOR_EACH_APP_DEFAULT = 0;

	/**
	 * Enable showing sketch and Shoot (Scribble) when pen is close to panel 1 =
	 * enable 0 = disable
	 * 
	 * 
	 */
	public static final String PEN_ENABLE_SKETCH = "pen_enable_sketch";

	/**
	 * The default value for PEN_ENABLE_SKETCH
	 * 
	 * 
	 */
	public static final int PEN_ENABLE_SKETCH_DEFAULT = 0;

	/**
	 * Does user pen down on screen after SetupWizard has run 1 = pen down after
	 * SetupWizard has run 0 = pen never down after SetupWizard has run
	 * 
	 * 
	 */
	public static final String HAS_PEN_DOWN_AFTER_SETUP_WIZARD = "has_pen_down_after_setup_wizard";

	/**
	 * Allow target window recevice pen event and convert to touch event 1 =
	 * enable 0 = disable
	 * 
	 * 
	 */
	public static final String PEN_AS_TOUCH = "pen_as_touch";

	/**
	 * The functional id of pen upper button
	 * 
	 * @see MotionEvent#META_PEN_HIGHLIGHT_ON
	 * @see MotionEvent#META_PEN_ERASER_ON
	 * @see MotionEvent#META_PEN_TXT_SEL_ON
	 * 
	 */
	public static final String PEN_UPPER_BUTTON_ID = "pen_upper_button_id";

	/**
	 * The functional id of pen lower button
	 * 
	 * @see MotionEvent#META_PEN_HIGHLIGHT_ON
	 * @see MotionEvent#META_PEN_ERASER_ON
	 * @see MotionEvent#META_PEN_TXT_SEL_ON
	 * 
	 */
	public static final String PEN_LOWER_BUTTON_ID = "pen_lower_button_id";
	// -htc: Pen event

	// [framework] begblock_virtual_key, Joseph.th, 2011/07/22
	/**
	 * for blocking virual keys, two kind of modes Type : int value : 0 = not
	 * block VK, 1 = block VK
	 * 
	 * 
	 */
	public static final String HTC_BLOCK_VIRTUAL_KEY = "htc_block_virtual_key";
	// [framework] end by Joseph.th

	/**
	 * The Logging ID (a unique 64-bit value) as a hex string. Used as a
	 * pseudonymous identifier for logging.
	 * 
	 * 
	 */
	public static final String LOGGING_ID2 = "logging_id2";

	/**
	 * The number of radio channels that are allowed in the local 802.11
	 * regulatory domain.
	 * 
	 * 
	 */
	public static final String WIFI_NUM_ALLOWED_CHANNELS = "wifi_num_allowed_channels";

	/**
	 * Whether the Wi-Fi Auto-IP should be on. Only the Wi-Fi service should
	 * touch this.
	 */
	public static final String WIFI_AUTO_IP_ON = "wifi_auto_ip_on";

	/**
	 * In order to record wifi power active mode.
	 */
	public static final String WIFI_PWR_ACTIVE_MODE = "wifi_pwr_active_mode";// Enable
																				// Power
																				// Active
																				// Mode.
																				// zone_liu
																				// ++

	/**
	 * In order to record wifi offload feature should be enabled or not
	 */
	public static final String WIFI_OFFLOAD_ENABLED = "wifi_offload_enabled";

	/**
	 * Force DHCP for Roaming
	 * 
	 * 
	 */
	public static final String WIFI_DHCP_ROAMING = "wifi_dhcp_roaming";

	// HTC_WIFI_START
	public static final String WIFI_SECURE_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_secure_networks_available_notification_on";
	// HTC_WIFI_END

	/**
	 * Whether the Wimax should be on. Only the Wimax service should touch this.
	 * 
	 * 
	 */
	public static final String WIMAX_ON = "wimax_on";

	/**
	 * Whether to notify the user of open networks.
	 * 
	 * 
	 */
	public static final String WIMAX_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wimax_networks_available_notification_on";

	// added by garywang, start!
	/**
	 * Whether the Mobile Data Netwrok should be on. Only the Mobile Data
	 * Netwrok service should touch this.
	 * 
	 * 
	 */
	public static final String MOBILEDATA_ON = "mobiledata_on";

	/**
	 * Control mobile network features by Mobile network control (obile Data
	 * Netwrok)
	 * 
	 * 
	 */
	public static final String MOBILE_FEATURE_CONTROL = "mobile_feature_control";

	// end!

	/**
	 * Maximum amount of time in milliseconds to hold a wakelock while waiting
	 * for mobile data connectivity to be established after a disconnect from
	 * WiMax.
	 * 
	 *  pending API Council approval Added by Pilo Chen
	 */
	public static final String WIMAX_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wimax_mobile_data_transition_wakelock_timeout_ms";

	/**
	 * Whether the Usbnet should be on. Only the Usbnet service should touch
	 * this.
	 * 
	 * 
	 */
	public static final String USBNET_ON = "usbnet_on";

	/**
	 * The interval in milliseconds after which WiMax is considered idle. When
	 * idle, it is possible for the device to be switched from WiMax to the
	 * mobile data network.
	 * 
	 *  pending API Council approval Added by Pilo Chen
	 */
	public static final String WIMAX_IDLE_MS = "wimax_idle_ms";

	/**
	 * Address to ping as a last sanity check before attempting any recovery.
	 * Unset or set to "0.0.0.0" to skip this check.
	 * 
	 * 
	 */
	public static final String PDP_WATCHDOG_PING_ADDRESS = "pdp_watchdog_ping_address";

	/**
	 * The "-w deadline" parameter for the ping, ie, the max time in seconds to
	 * spend pinging.
	 * 
	 * 
	 */
	public static final String PDP_WATCHDOG_PING_DEADLINE = "pdp_watchdog_ping_deadline";

	// [HTC_PHONE] s: htc shawn
	/**
	 * for toggle "query CFU when camp-on" use
	 * 
	 * 
	 */
	public static final String CFU_QUERY_WHEN_CAMPON_MESSAGE = "cfu_query_when_campon";
	// [HTC_PHONE] e: htc shawn

	// [BEGIN] HTC feedback client
	/**
	 * Flag for allowing ActivityManagerService to send ACTION_APP_ERROR intents
	 * on application crashes and ANRs. If this is disabled, the crash/ANR
	 * dialog will never display the "Report" button. Type: int ( 0 = disallow,
	 * 1 = allow )
	 * 
	 * 
	 */
	public static final String SEND_HTC_ERROR_REPORT = "send_htc_error_report";

	/**
	 * Flag for allowing user to turn on/off HTC feedback client UI. Type: int (
	 * 0 = disallow, 1 = allow )
	 * 
	 * 
	 */
	public static final String HTC_ERROR_REPORT_SETTING = "htc_error_report_setting";

	/**
	 * Tell HTC privacy statement version Type: string
	 * 
	 * 
	 */
	public static final String TELL_HTC_PRIVACY_VERSION = "tell_htc_privacy_version";
	/**
	 * User Profile privacy statement version Type: string
	 * 
	 * 
	 */
	public static final String USER_PROFILE_PRIVACY_VERSION = "user_profile_privacy_version";
	/**
	 * Error Report privacy statement version Type: string
	 * 
	 * 
	 */
	public static final String ERROR_REPORT_PRIVACY_VERSION = "error_report_privacy_version";

	/**
	 * Setting for user to select prefer network to send error report. Type: int
	 * ( 0 = 3G or Wi-Fi, 1 = Wi-Fi only )
	 * 
	 * 
	 */
	public static final String HTC_ERROR_REPORT_PREFER_NETWORK = "htc_error_report_prefer_network";

	/**
	 * Allow user to send report automatically when error occurs Type: int ( 0 =
	 * manually send, 1 = automatically send )
	 * 
	 * 
	 */
	public static final String HTC_ERROR_REPORT_AUTO_SEND = "htc_error_report_auto_send";

	/**
	 * Allow user to send application log Type: int ( 0 = disallow, 1 = allow )
	 * 
	 * 
	 */
	public static final String SEND_HTC_APPLICATION_LOG = "send_htc_application_log";
	// [END] HTC feedback client

	// begin-tellhtc-enable-sense.com-logs, pitt_wu, 2011/06/30
	/**
	 * [sense 3.5] sense.com log is the second kind of usage log in Tell Htc.
	 * The value 1 means sense.com logs are allowed to send to Tell Htc server.
	 * Otherwise, value 0. Type : int value : 0 = disable sense.com logs, 1 =
	 * enable sense.com logs
	 * 
	 * 
	 */
	public static final String TELLHTC_ENABLE_SENSE_DOT_COM_LOG = "tellhtc_enable_sense_dot_com_log";
	// end-tellhtc-enable-sense.com-logs

	// Albal add for resolution type
	/**
	 * for selected resolution type, value : DISPLAY_RESOLUTION_TYPE_AUTO to
	 * 480P
	 * 
	 * 
	 */
	public static final String SELECT_RESOLUTION_TYPE = "select_resolution_type";
	// Albal add for resolution type

	// Albal add for fastboot enable flag
	/**
	 * for fast boot disable, value : 0, enable : 1, else integer reserved
	 * 
	 * 
	 */
	public static final String ENABLE_HTC_FASTBOOT = "enable_fastboot";
	// Albal add for fastboot enable flag

	// @+ Ausmus 20110516
	/**
	 * VZW global roaming option 0 = Deny roaming 1 = Allow All trip 2 = Allow
	 * this trip
	 * 
	 * 
	 */
	public static final String VZW_GLOBAL_ROAMING_OPTIONS = "vzw_global_roaming_options";
	// @- Ausmus 20110516

	// ++ Sam_Chao@20100312: For national roaming.
	/**
	 * Whether or not allow national roaming enabled (0 = false, 1 = true)
	 * 
	 * 
	 */
	public static final String NATIONAL_ROAMING_ON = "national_roaming_on";
	// -- Sam_Chao@20100312: For national roaming.

	/**
	 * A key to indicate that whether the 'Display message text on lock screen'
	 * is enabled(=1) or disabled(=0).
	 * 
	 * @path Settings / Privacy / Display message text on lock screen
	 * @purpose In case of it is disabled, it will only display the new message
	 *          notification icon in status bar without displaying the message
	 *          text in status bar
	 * @author TJ Tsai (128135)
	 * @since HTC sense 2.1
	 * 
	 */
	public static final String HTC_NEW_MESSAGE_NOTIFICATION = "htc_new_message_notification";

	// + Mark.sl_Wu@20110630: Sense3.5 feature +
	/**
	 * A key to indicate that whether the 'Phone notification preview' is
	 * enabled(=1) or disabled(=0).
	 * 
	 * @path Settings / Privacy / Phone notification preview
	 * @purpose In case of it is disabled, it will only display the missed calls
	 *          and voicemails notification icon in status bar without
	 *          displaying the message text in status bar
	 * @author Mark.sl_Wu
	 * @since HTC sense 3.5
	 * 
	 */
	public static final String HTC_PHONE_NOTIFICATION_PREVIEW = "htc_phone_notification_preview";

	/**
	 * A key to indicate that whether the 'Message notification preview' is
	 * enabled(=1) or disabled(=0).
	 * 
	 * @path Settings / Privacy / Message notification preview
	 * @purpose In case of it is disabled, it will only display the new message
	 *          notification icon in status bar without displaying the message
	 *          text in status bar
	 * @author Mark.sl_Wu
	 * @since HTC sense 3.5
	 * 
	 */
	public static final String HTC_MESSAGE_NOTIFICATION_PREVIEW = "htc_message_notification_preview";
	// - Mark.sl_Wu@20110630 -

	// + Mark.sl_Wu@20111007: [HTC Speak] Item to be add in security lock
	// settings +
	/**
	 * A key to indicate that whether the 'Allow notification announcements' is
	 * enabled(=1) or disabled(=0).
	 * 
	 * @path Settings / Security / Allow notification announcements
	 * @author Mark.sl_Wu
	 * @since HTC sense 4.0
	 * 
	 */
	public static final String HTC_SPEAK_ANNOUNCE = "htc_speak_announce";

	public static final String HTCSPEAK_DEFALUT_LANG = "htcspeak_default_lang";
	// - Mark.sl_Wu@20111007 -

	// [framework] begin add by jack_peng, 2010/01/24
	/**
	 * for check system 2D or 3D mode, which is read from a H/W switch Type :
	 * int value : 0 = 2D, 1 = 3D
	 * 
	 * 
	 */
	public static final String HTC_2D_3D_MODE = "htc_2d_3d_mode";
	// [framework] end add, 2010/01/24

	// [HTC_PHONE] s added by jason liu
	/**
	 * Whether the dtmf is enabled. 0 = disablel dtmf 1 = enable dtmf
	 * 
	 * 
	 */
	public static final String DTMF_ENABLED = "dtmf_enabled";
	// [HTC_PHONE] e end

	// + Mark.SL_Wu@2012.09.04: 3LM porting +
	/**
	 * Internal data encryption for /data partition 1 if data encryption is
	 * enabled, 0 if not
	 * 
	 * 
	 */
	public static final String DATA_ENCRYPTION = "data_encryption";

	/**
	 * SD card encryption 1 if sd card is encrypted, 0 otherwise
	 * 
	 * 
	 */
	public static final String SD_ENCRYPTION = "sd_encryption";

	/**
	 * Boot lock will disable all non-system apps from launching until unlocked
	 * and prevent install/uninstall of apps. This is used when a device has
	 * been provisioned and is rebooted. Device is locked until all the IT
	 * policies have been applied after boot. 1 is enabled, 0 disabled
	 * 
	 * 
	 */
	public static final String BOOT_LOCK = "boot_lock";

	/**
	 * Setting for blocking ADB via 3LM.
	 * 
	 * 
	 */
	public static final String ADB_BLOCKED = "adb_blocked";

	/**
	 * Setting for blocking USB via 3LM.
	 * 
	 * 
	 */
	public static final String USB_BLOCKED = "usb_blocked";

	/**
	 * Setting for locking admin via 3LM.
	 * 
	 * 
	 */
	public static final String ADMIN_LOCKED = "admin_locked";

	/**
	 * Setting for OTA policy via 3LM.
	 * 
	 * 
	 */
	public static final String OTA_DELAY = "ota_delay";

	/**
	 * Setting for emergency lock text
	 * 
	 * 
	 */
	public static final String EMERGENCY_LOCK_TEXT = "emergency_lock";

	/**
	 * Setting for tethering
	 * 
	 * 
	 */
	public static final String TETHERING_BLOCKED = "tethering_blocked";

	/**
	 * Setting for screenshot
	 * 
	 * 
	 */
	public static final String SCREENSHOT_BLOCKED = "screenshot_blocked";

	/**
	 * Setting for APN lock
	 * 
	 * 
	 */
	public static final String APN_LOCKED = "apn_locked";
	// - Mark.SL_Wu@2012.09.04: 3LM porting -

	// [framework] begin modified by rhed_jao 20110516, to merge 3LM feature
	/**
	 * Phone storage encryption when support internal SD card 1 if phone storage
	 * is encrypted, 0 otherwise
	 * 
	 */
	public static final String PHONE_ENCRYPTION = "phone_encryption";
	// [framework] end, 20110516

	// [framework] begin modified by rhed_jao 20110823, feature for DataControl,
	// Add "wifi-on" and "mobile connect on" settings for AutoSync
	/**
	 * Settings for SyncManager to perform auto sync when wifi connection is
	 * turn on. 1 is enabled, 0 disabled
	 * 
	 */
	public static final String AUTOSYNC_IN_MOBILE_ON = "autosync_in_mobile_on";

	/**
	 * Settings for SyncManager to perform auto sync when mobile connection is
	 * turn on. 1 is enabled, 0 disabled
	 * 
	 */
	public static final String AUTOSYNC_IN_WIFI_ON = "autosync_in_wifi_on";
	// [framework] end, 20110823

	// [framework] begin modified by rhed_jao 20110909, feature to replace
	// location provider, Add setting to replace NetworkLocationProvider and
	// GeocodeProvider
	/**
	 * Settings for weather clock to setup NewworkLocationProvider and
	 * GeocodeProvider. LocationManagerService will reload the new provider,
	 * after it changed. value: <NLP>,<GeocodeProvider>
	 * 
	 */
	public static final String CUSTOMIZED_LOCATION_PROVIDER = "customized_location_provider";
	// [framework] end, 20110909

	// [HTC_PHONE]s:Brian+@20120206, for dual mode system
	public static final String EXT_VOICE_CALL_PHONE_TYPE = "ext_voice_call_phone_type";
	// [HTC_PHONE]e:Brian-@20120206, for dual mode system

	// + Mark.sl_Wu@20120222: new key for Sense4.0 feature +
	/**
	 * Music bypass
	 */
	public static final String HTC_MUSIC_BYPASS_ENABLED = "htc_music_bypass_enabled";
	// - Mark.sl_Wu@20120222: new key for Sense4.0 feature -

	/**
	 * Preference of Deferred Actions for rejecting call.
	 * 
	 * This key is to enable/disable deferred actions for rejecting calls.
	 * 
	 * @author Bevis Tseng
	 * @since HTC sense 4.0
	 */
	public static final String HTC_ENABLE_DEFERRED_ACTION_FOR_REJECTING_CALLS = "htc_enable_deferred_action_for_rejecting_calls";

	/**
	 * The preferred TTY mode 0 = TTy Off, CDMA default 1 = TTY Full 2 = TTY HCO
	 * 3 = TTY VCO
	 */
	public static final String PREFERRED_TTY_MODE = "preferred_tty_mode";
	/**
	 * Whether the TTY mode mode is enabled. 0 = disabled 1 = enabled
	 * 
	 */

	public static final String TTY_MODE_ENABLED = "tty_mode_enabled";

	// [+] Add for Sprint Extension setgpsstate() Ryan@20121120
	/**
	 * This is only used in Sprint projects
	 * 
	 * 
	 * @author ryan_lin
	 */
	public static final String GPS_CHANGEABLE = "gps_changeable";

	/**
	 * Key of IIDA FN key checked state
	 */
	public static final String IIDA_FN_KEY_ENABLED = "iida_fn_key_enabled";

	// [-] Add for Sprint Extension setgpsstate() Ryan@20121120
	
	// + Add keys for Lockscreen by Alan 20130808
	public static final String HTC_LOCKSCREEN_PRODUCTIVITY_VOICE_MAIL = "htc_lockscreen_productivity_voice_mail";
	public static final String HTC_LOCKSCREEN_PRODUCTIVITY_MAIL = "htc_lockscreen_productivity_mail";
	public static final String HTC_LOCKSCREEN_PRODUCTIVITY_CALENDAR_EVENTS = "htc_lockscreen_productivity_calendar_events";
	// - Add keys for Lockscreen by Alan 20130808
	
    // + Add by Alan for "Phone incoming call" feature
	// ref mail title: "M8_UL_K44_SENSE60, #671"
	public static final String HTC_LOCKSCREEN_SHOW_PRIVATE_CONTACTS = "htc_lockscreen_show_private_contacts";
    // - Add by Alan for "Phone incoming call" feature
}
