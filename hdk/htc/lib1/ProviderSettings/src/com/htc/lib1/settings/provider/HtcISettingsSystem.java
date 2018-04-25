package com.htc.lib1.settings.provider;

import android.net.Uri;
import android.os.Build;
import android.view.Surface;


/**
 * 
 * This interface is used to add HTC's system keys, and add some 
 * related APIs.
 * 
 * @since {@link Build.VERSION_CODES#JELLY_BEAN}, 2013.03.14
 * @author TJ Tsai
 */
/* default */ interface HtcISettingsSystem {

	/**
	 * Constant for use in AIRPLANE_MODE_RADIOS to specify Wimax radio.
	 * 
	 * 
	 */
	public static final String RADIO_WIMAX = "wimax";

	/**
	 * Constant for use in AIRPLANE_MODE_RADIOS to specify Usbnet radio.
	 * 
	 * 
	 */
	public static final String RADIO_USBNET = "usbnet";

	/**
	 * Value for {@link #WIFI_SLEEP_POLICY} to use the default Wi-Fi sleep
	 * policy, which is to sleep shortly after the turning off according to the
	 * {@link #STAY_ON_WHILE_PLUGGED_IN} setting.
	 */
	public static final int WIFI_SLEEP_POLICY_DEFAULT = 2;

	/**
	 * The number of radio channels that are allowed in the local 802.11
	 * regulatory domain.
	 * 
	 * 
	 */
	public static final String WIFI_NUM_ALLOWED_CHANNELS = "wifi_num_allowed_channels";

	/**
	 * Whether smart wifi is enabled (0 = false, 1 = true)
	 */
	public static final String SMART_WIFI_ENABLED = "smart_wifi_enabled";

	/**
	 * Whether the Hotspot should be auto on in dock mode.
	 * 
	 * 
	 */
	public static final String DOCK_WIFI_ON = "dock_wifi_on";

	/**
	 * Keep local bluetooth device name
	 * 
	 * 
	 */
	public static final String BLUETOOTH_NAME = "bluetooth_name";
	public static final String DEVICE_NAME = BLUETOOTH_NAME;

	/**
	 * backup/restore FTP,MAP state....
	 * 
	 * 
	 */
	public static final String BLUETOOTH_FTP_SETTINGS = "bt_ftp_enable";
	public static final String BLUETOOTH_MAP_SETTINGS = "bt_map_enable";

	/**
	 * Bluetooth discoverability end time. We need to sync discoverable timer
	 * between Settings application and BT Frameworks.
	 */
	public static final String BLUETOOTH_DISCOVERABILITY_END_TIMESTAMP = "bluetooth_discoverability_end_timestamp";

	/**
	 * Determine the behavior of the notification bubble for message, mail and
	 * so on.
	 */
	public static final String NOTIFICATION_BUBBLE = "notification_bubble";

	/**
	 * The inactivity time after the screen turns off. After the inactivity
	 * time, the phone will be really locked. The time unit is seconds.
	 */
	public static final String INACTIVITY_TIME = "inactivity_time";

	/**
	 * write the PLMN & version for Flexnet
	 */
	public static final String POWERSAVER_CONFIGP = "powersaver_configp";

	/**
	 * The Facebook event flash button. Whether flashing when there is an
	 * Facebook message event come in.
	 */
	public static final String FACEBOOK_EVENT_FLASH_BUTTON = "facebook_event_flash_button";

	/**
	 * The screen auto backlight. 0=no 1=yes
	 * 
	 * 
	 */
	public static final String SCREEN_AUTO = "screen_auto";

	// <charlie_lin@htc.com>
	// <ADD>
	/**
	 * The property name of default auto backlight setting.
	 * 
	 * 
	 */
	public static final String PREF_AUTOBACKLIGHT = "settings.display.autobacklight";

	/**
	 * The property name of default brightness.
	 * 
	 * 
	 */
	public static final String PREF_BRIGHTNESS = "settings.display.brightness";
	// </ADD>

	/**
	 * DTMF volume. This is used internally, changing this value will not change
	 * the volume. See AudioManager.
	 */
	public static final String VOLUME_DTMF = "volume_dtmf";

	// 2010-06-24 Sky Soo - Begin - Added a new volume group for wired
	// Headset/Headphone
	/**
	 * Appended to various volume related settings It is for Headset/Headphone
	 * case
	 */
	public static final String APPEND_FOR_HEADSET = "_headset";
	// 2010-06-24 Sky Soo - End - Added a new volume group for wired
	// Headset/Headphone

	/**
	 * Persistent store for the system-wide default calendar notification sound.
	 * 
	 * @see #RINGTONE
	 * @see #DEFAULT_CALENDAR_NOTIFICATION_URI
	 * 
	 */
	public static final String CALENDAR_NOTIFICATION_SOUND = "cal_notification";

	/**
	 * A {@link Uri} that will point to the current default calendar
	 * notification sound at any given time.
	 * 
	 * @see #DEFAULT_CALENDAR_NOTIFICATION_URI
	 * 
	 */
	public static final Uri DEFAULT_CALENDAR_NOTIFICATION_URI = android.provider.Settings.System
			.getUriFor(CALENDAR_NOTIFICATION_SOUND);

	/**
	 * Persistent store for the system-wide default message notification sound.
	 * 
	 * @see #RINGTONE
	 * @see #DEFAULT_MSG_NOTIFICATION_URI
	 * 
	 */
	public static final String MSG_NOTIFICATION_SOUND = "msg_notification";

	/**
	 * A {@link Uri} that will point to the current default message notification
	 * sound at any given time.
	 * 
	 * @see #DEFAULT_MSG_NOTIFICATION_URI
	 * 
	 */
	public static final Uri DEFAULT_MSG_NOTIFICATION_URI = android.provider.Settings.System
			.getUriFor(MSG_NOTIFICATION_SOUND);

	/**
	 * A {@link Uri} that will point to the current calendar start weekday at
	 * any given time. 1 = Sunday 2 = Monday
	 * 
	 * @see #CALENDAR_START_WEEKDAY
	 * 
	 */
	public static final String CALENDAR_START_WEEKDAY = "calendar_start_weekday";

	/**
	 * A {@link Uri} that will point to the current calendar whether show lunar
	 * string at any given time. 0 = Disable 1 = Enable
	 * 
	 * @see #CALENDAR_SHOW_LUNAR
	 * 
	 */
	public static final String CALENDAR_SHOW_LUNAR = "calendar_show_lunar";

	/**
	 * (Arabic trunk only) Setting to display digits in Native. 1 = On, 0 = Off
	 */
	public static final String NATIVE_DIGITS = "native_digits";

	/**
	 * Indicates the weather wallpaper animation is on/off.
	 * <UL>
	 * <LI>0 = disabled / off</LI>
	 * <LI>1 = enabled / on</LI>
	 * </UL>
	 * 
	 * 
	 */
	public static final String WEATHER_WALLPAPER_ANIMATION_ALLOWED = "weather_wallpaper_animation_allowed";

	/**
	 * Danny C.C. Hsu Date format short string for HTC mm/dd dd/mm mm-dd
	 * 
	 * 
	 */
	public static final String DATE_FORMAT_SHORT = "date_format_short";

	/**
	 * Silent mode string
	 * 
	 * 
	 */
	public static final String SILENT_MODE = "silent_mode";

	/**
	 * Indicates the "be polite" function is on/off. The value will be stored in
	 * the settings database.
	 * <UL>
	 * <LI>0 = disabled / off</LI>
	 * <LI>1 = enabled / on</LI>
	 * </UL>
	 * 
	 * 
	 */
	public static final String BE_POLITE = "be_polite";

	/**
	 * Indicates the "be polite" function is on/off. The value will be stored in
	 * the settings database.
	 * <UL>
	 * <LI>0 = disabled / off</LI>
	 * <LI>1 = enabled / on</LI>
	 * </UL>
	 * 
	 * 
	 */
	public static final String POCKET_MODE = "pocket_mode";

	// [HTC_PHONE] s: htc shawn
	/**
	 * Indicates the "flip to speaker" function is on/off. The value will be
	 * stored in the settings database.
	 * <UL>
	 * <LI>0 = disabled / off</LI>
	 * <LI>1 = enabled / on</LI>
	 * </UL>
	 * 
	 * 
	 */
	public static final String FLIP_TO_SPEAKER = "flip_to_speaker";
	// [HTC_PHONE] e: htc shawn

	/**
	 * Controls whether the custom accelerometer will be used to change screen
	 * orientation. If the value of {@link #ACCELEROMETER_ROTATION} is 0, the
	 * screen oriatation will be depended on the value.
	 * 
	 * <XMP>Framework display: 1. Update display in a particular orientation
	 * according CUSTOM_ORIENTATION, When "Auto-rotate Screen" is 0. 2.
	 * ROTATION_180 is reserved for future project/design. Framework could
	 * ignore 180 as default if device does not support. 3. AP will never set it
	 * to 180 if device does not support. 4. If a foreground AP does not support
	 * specified orientation, like Portrait only/landscape only, display as AP
	 * design. 5. Update display when "auto-rotate" or CUSTOM_ORIENTATION
	 * changed. 6. To lock in a orientation, test AP will a. set
	 * CUSTOM_ORIENTATION to a angle b. set auto-rotate = 0. 7. To unlock and
	 * recover auto-rotate, test AP will a. set CUSTOM_ORIENTATION = -1 b. set
	 * auto-rotate = 1. </XMP>
	 * 
	 * <P>
	 * The value list of the key Settings.System.CUSTOM_ORIENTATION:
	 * <UL>
	 * <LI>-1 (Default:work as HTC behavior)</LI>
	 * <LI>0 ROTATION_0</LI>
	 * <LI>1 ROTATION_90</LI>
	 * <LI>2 ROTATION_180 (reserved)</LI>
	 * <LI>3 ROTATION_270</LI>
	 * </UL>
	 * </P>
	 * 
	 * @request Sprint's request
	 * @path Settings/Display/
	 * @see #CUSTOM_ORIENTATION_ROTATION_DEFAULT
	 * @see #CUSTOM_ORIENTATION_ROTATION_0
	 * @see #CUSTOM_ORIENTATION_ROTATION_90
	 * @see #CUSTOM_ORIENTATION_ROTATION_180
	 * @see #CUSTOM_ORIENTATION_ROTATION_270
	 * @author TJ Tsai (128135)
	 * 
	 */
	public static final String CUSTOM_ORIENTATION = "custom_orientation";

	/**
	 * @request Sprint's request
	 * @path Settings/Display/
	 * @see #CUSTOM_ORIENTATION
	 * @see #CUSTOM_ORIENTATION_ROTATION_DEFAULT
	 * @see #CUSTOM_ORIENTATION_ROTATION_0
	 * @see #CUSTOM_ORIENTATION_ROTATION_90
	 * @see #CUSTOM_ORIENTATION_ROTATION_180
	 * @see #CUSTOM_ORIENTATION_ROTATION_270
	 * @author TJ Tsai (128135)
	 * 
	 */
	public static final int CUSTOM_ORIENTATION_ROTATION_DEFAULT = -1;
	public static final int CUSTOM_ORIENTATION_ROTATION_0 = Surface.ROTATION_0;
	public static final int CUSTOM_ORIENTATION_ROTATION_90 = Surface.ROTATION_90;
	public static final int CUSTOM_ORIENTATION_ROTATION_180 = Surface.ROTATION_180;
	public static final int CUSTOM_ORIENTATION_ROTATION_270 = Surface.ROTATION_270;

	/**
	 * Whether live web suggestions while the user types into search dialogs are
	 * enabled. Browsers and other search UIs should respect this, as it allows
	 * a user to avoid sending partial queries to a search engine, if it poses
	 * any privacy concern. The value is boolean (1 or 0).
	 * 
	 * @deprecated Each application that shows web suggestions should have its
	 *             own setting for this.
	 */
	// @Deprecated
	// public static final String SHOW_WEB_SUGGESTIONS = "show_web_suggestions";

	/**
	 * Danny C.C. Hsu @ hTC Supl setting for DM UNIQUE ID of the application
	 * 
	 * 
	 */
	public static final String DM_SUPL_IAPID = "dm_supl_iapid";

	/**
	 * Danny C.C. Hsu @ hTC Supl setting for DM the preferred connection(APN)
	 * used in AGPS
	 * 
	 * 
	 */
	public static final String DM_SUPL_PREFERRED_APN = "dm_supl_preferred_apn";

	/**
	 * Danny C.C. Hsu @ hTC Supl setting for DM other optional connection(APN)
	 * used in AGPS
	 * 
	 * 
	 */
	public static final String DM_SUPL_OPTIONAL_APN = "dm_supl_optional_apn";

	/**
	 * Danny C.C. Hsu @ hTC Supl setting for DM the name of AGPS server
	 * 
	 * 
	 */
	public static final String DM_SUPL_SERVER_NAME = "dm_supl_server_name";
	/**
	 * Danny C.C. Hsu @ hTC Supl setting for DM the IP address of AGPS server
	 * 
	 * 
	 */
	public static final String DM_SUPL_SERVER_IP = "dm_supoer_server_ip";

	/**
	 * Danny C.C. Hsu @ hTC Supl setting for DM the PORT number of AGPS server
	 * 
	 * 
	 */
	public static final String DM_SUPL_SERVER_PORT = "dm_supoer_server_port";

	/**
	 * Danny C.C. Hsu @ hTC AGps Settings for CMCC Lab test
	 * 
	 * 
	 */
	public static final String AGPS_ENABLED = "agps_enabled";
	/**
	 * //-added by Gilbert for MVNO operator string /**
	 * 
	 * 
	 */
	public static final String MVNO_OPERATOR = "mvno_operator";
	// -added by Gilbert for MVNO operator

	// ** [Start] Nicky Hung - 20130626 Add more MVNO field
	public static final String SUB_MVNO_OPERATOR = "sub_mvno_operator";
	public static final String CDMA_MVNO_OPERATOR = "cdma_mvno_operator";
	// ** [End] Nicky Hung

	// Added by jason liu #Need to sync google sync settings

	/**
	 * 
	 */
	public static final String GOOGLE_SYNC_CONTACTS = "google_sync_contact";

	/**
	 * 
	 */
	public static final String GOOGLE_SYNC_CALENDAR = "google_sync_calendar";

	/**
	 * 
	 */
	public static final String GOOGLE_SYNC_GMAIL = "google_sync_gmai";
	// end

	/**
	 * 
	 */
	@Deprecated
	public static final String FOTA_DOWNLOAD_OPTION = "FOTA_download_options";

	/**
	 * FOTA periodic checkin enable setting. Set 1 to schedule periodic checkin
	 * in FOTA service. Set 0 to disable all backgreoud FOTA checkin. Default
	 * value should be set to 1 (enable).
	 * 
	 * 
	 */
	public static final String FOTA_PERIODIC_CHECKIN = "FOTA_periodic_checkin";
	// [END] FOTA options.

	// [BEGIN] add for auto setting time, time zone, locale, weather current
	// city.
	/**
	 * The option for setting time, time zone, locale, and current city on
	 * weather automatically by my location. 'AUTO_UPDATE_BY_LOC' : Auto adjust
	 * the settings or not. 'AUTO_APPLY_UPDATE' : Do not ask user again.
	 * 
	 * 
	 */
	public static final String AUTO_UPDATE_BY_LOC = "auto_set_time_city_locale";

	/**
	 * 
	 */
	public static final String AUTO_APPLY_UPDATE = "auto_apply_set_time_city_locale";
	// [END]

	// BH_Lin@20091211 ------------------------------------------------------->
	// purpose : CDMA feature "Roaming Ring tone" for specific operator
	/**
	 * 
	 */
	public static final String SETTING_ROAMING_RINGTONE = "roaming_ringtone_on";
	// BH_Lin@20091211 ------------------------------------------------------<

	// [BEGIN] John Cheng 2010/2/2 for SD card notifications backup/restore
	/**
	 * 
	 */
	public static final String SETTING_SD_CARD_NOTIFICATIONS = "sd_card_notifications";
	// [END] John Cheng 2010/2/2

	// [Start] Allen Hsu 20100202 add for store DSA URL
	/**
	 * for store Sprint DSA Server URL
	 * 
	 * 
	 */
	public static final String DSA_SERVER_URL = "dsa_server_url";

	/**
	 * Default value for DSA_SERVER_URL
	 * 
	 * 
	 */
	public static final String DSA_SERVER_URL_DEFAULT = "https://dsa.spcsdns.net:443/dsa/";

	/**
	 * for store Sprint DSA_Proxy_URL
	 * 
	 * 
	 */
	public static final String DSA_PROXY_URL = "dsa_proxy_url";

	/**
	 * Default value for DSA_Proxy_URL
	 * 
	 * 
	 */
	public static final String DSA_PROXY_URL_DEFAULT = "144.226.247.31:80";
	// [End] Allen Hsu 20100202 add for store DSA URL

	/**
	 * For WifiRouter Setting Values start
	 */
	/**
	 * Whether the Hotspot should be on. Only the Hotspot service should touch
	 * this.
	 * 
	 * 
	 */
	public static final String HOTSPOT_ON = "hotspot_on";
	/**
	 * Whether the Hotspot should be auto on in dock mode.
	 * 
	 * 
	 */
	public static final String DOCK_HOTSPOT_ON = "dock_hotspot_on";

	/**
	 * 
	 */
	public static final String HOTSPOT_MAX_CONNECTION = "hotspot_max_connection";
	/**
	 * 
	 */
	public static final String HOTSPOT_SSID = "hotspot_ssid";
	/**
	 * 
	 */
	public static final String HOTSPOT_HIDDEN = "hotspot_hidden";
	/**
	 * 
	 */
	public static final String HOTSPOT_SECURITY_TYPE = "hotspot_security_type";
	/**
	 * 
	 */
	public static final String HOTSPOT_PASSWORD = "hotspot_password";
	/**
	 * 
	 */
	public static final String HOTSPOT_CHANNEL = "hotspot_channel";
	/**
	 * 
	 */
	public static final String HOTSPOT_POWERMODE = "hotspot_powermode";
	/**
	 * 
	 */
	public static final String HOTSPOT_IP_ADDRESS = "hotspot_id_address";
	/**
	 * 
	 */
	public static final String HOTSPOT_SUBNET_MASK = "hotspot_subnet_mask";
	/**
	 * 
	 */
	public static final String HOTSPOT_DHCP_ON = "hotspot_dhcp_on";
	/**
	 * 
	 */
	public static final String HOTSPOT_DHCP_STARTING_IP = "hotspot_dhcp_start_ip";
	/**
	 * 
	 */
	public static final String HOTSPOT_DHCP_MAX_CONNECTION = "hotspot_dhcp_max_connection";
	/**
	 * 
	 */
	public static final String HOTSPOT_MACFILTER_ON = "hotspot_macfilter_on";
	/**
	 * 
	 */
	public static final String HOTSPOT_WHITE_LIST = "hotspot_white_list";
	/**
	 * 
	 */
	public static final String HOTSPOT_BLACK_LIST = "hotspot_black_list";
	/**
	 * 
	 */
	public static final String HOTSPOT_BLOCK_LIST = "hotspot_block_list";
	/**
	 * 
	 */
	public static final String HOTSPOT_SLEEP_POLICY = "hotspot_sleep_policy";

	/**
	 * 
	 */
	public static final String HOTSPOT_REMIND_DIALOG = "hotspot_remind_dialog";
	/**
	 * 
	 */
	public static final String HOTSPOT_CONNECTION_ARRAY = "hotspot_connection_array";

	/**
	 * 
	 */
	public static final String HOTSPOT_POWER_MODE_REMIND_DIALOG = "hotspot_power_mode_remind";

	/*
	 * For WifiRouter Setting Values end
	 */

	/**
	 * Indicates Power saver is on/off.
	 * <UL>
	 * <LI>0 = disabled / off</LI>
	 * <LI>1 = enabled / on</LI>
	 * </UL>
	 * 
	 * 
	 */
	public static final String PSAVER_ENABLE = "psaver_enable";

	/**
	 * Indicates the schedule of Power saver.
	 * 
	 * 
	 */
	public static final String PSAVER_SCHEDULE = "psaver_schedule";

	// +[HTC_PHONE]: Evan Wu [Sense 2.1][Home Dialing]
	/**
	 * Indicates the enable state of home dialing feature.
	 * 
	 * @author Evan Wu@hTC 20101109.[Sense 2.1][Home Dialing]
	 * 
	 */
	public static final String HOME_COUNTRY_ENABLED = "home_country_enabled";

	/**
	 * Indicates the preferred country of home dialing feature.
	 * 
	 * @author Evan Wu@hTC 20101209.[Sense 2.1][Home Dialing]
	 * 
	 */
	public static final String HOME_DIALING_PREF = "home_dialing_pref";

	/**
	 * Indicates the preferred country code of home dialing feature.
	 * 
	 * @author Evan Wu@hTC 20101209.[Sense 2.1][Home Dialing]
	 * 
	 */
	public static final String HOME_DIALING_COUNTRY_CODE = "home_dialing_country_code";

	/**
	 * Indicates the preferred country trunk code of home dialing feature.
	 * 
	 * @author Evan Wu@hTC 20101209.[Sense 2.1][Home Dialing]
	 * 
	 */
	public static final String HOME_DIALING_TRUNK_CODE = "home_dialing_trunk_code";
	// -[HTC_PHONE]: Evan Wu [Sense 2.1][Home Dialing]

	/**
	 * Record the roaming state for home dialing.
	 * 
	 * @author Evan Wu@hTC 201110616.[Sense 3.5][Home Dialing Enhancement]
	 * 
	 */
	public static final String HOME_DIALING_ROAMING_STATE = "home_dialing_roaming_state";

	/**
	 * Indicates if need to popup dialog after first time roaming.
	 * 
	 * @author Evan Wu@hTC 201110616.[Sense 3.5][Home Dialing Enhancement]
	 * 
	 */
	public static final String HOME_DIALING_POPUP_DIALOG = "home_dialing_popup_dialog";

	/**
	 * A key to indicate that the titling behavior will be enabled or disabled.
	 * 
	 * @path Settings / Display / 3D Home Screen
	 * @purpose a user could disable the titling behavior from the path
	 * @author TJ Tsai (128135)
	 * @since HTC sense 3.0
	 * 
	 */
	public static final String HTC_3D_HOME_SCREEN = "htc_3d_home_screen";

	/**
	 * A key to indicate that whether the 'Automatic startup' is enabled(=1) or
	 * disabled(=0).
	 * 
	 * @path Settings / Application / Automatic startup
	 * @purpose auto launch a task manager after booting up the device.
	 * @author TJ Tsai (128135)
	 * @since HTC sense 3.0
	 * 
	 */
	public static final String HTC_APPLICATION_AUTOMATIC_STARTUP = "htc_application_automatic_startup";

	/**
	 * A key to indicate that what the threshold is. Then when the memory is
	 * lower than the threshold, the notification will be issued.
	 * 
	 * @path Settings / Application / Notification
	 * @purpose When the memory is lower than n MB, notification will be issued.
	 * @author TJ Tsai (128135)
	 * @since HTC sense 3.0
	 * 
	 */
	public static final String HTC_APPLICATION_NOTIFICATION = "htc_application_notification";

	/**
	 * A key to indicate that whether the wireless (mobile network and Wi-Fi)
	 * sleep mode is enabled or not.
	 * 
	 * @path Settings / Power
	 * @purpose Every time when device turns off the screen for 5 minutes and
	 *          during the sleeping time, the device will disconnect both mobile
	 *          network and Wi-Fi. Mobile network and Wi-Fi will be reconnected
	 *          when the user turns on the screen again.
	 * @author TJ Tsai (128135)
	 * @since HTC Sense tablet
	 * @see #HTC_WIRELESS_SLEEP_MODE_ENABLED
	 * @see #HTC_WIRELESS_SLEEP_START_TIME
	 * @see #HTC_WIRELESS_SLEEP_END_TIME
	 * 
	 */
	public static final String HTC_WIRELESS_SLEEP_MODE_ENABLED = "htc_wireless_sleep_mode_enabled";

	/**
	 * A key to indicate that what is the start time for the wireless (mobile
	 * network and Wi-Fi) sleep mode.
	 * 
	 * @path Settings / Power
	 * @purpose Every time when device turns off the screen for 5 minutes and
	 *          during the sleeping time, the device will disconnect both mobile
	 *          network and Wi-Fi. Mobile network and Wi-Fi will be reconnected
	 *          when the user turns on the screen again.
	 * @author TJ Tsai (128135)
	 * @since HTC Sense tablet
	 * @see #HTC_WIRELESS_SLEEP_MODE_ENABLED
	 * @see #HTC_WIRELESS_SLEEP_START_TIME
	 * @see #HTC_WIRELESS_SLEEP_END_TIME
	 * 
	 */
	public static final String HTC_WIRELESS_SLEEP_START_TIME = "htc_wireless_sleep_start_time";

	/**
	 * A key to indicate that what is the start time for the wireless (mobile
	 * network and Wi-Fi) sleep mode.
	 * 
	 * @path Settings / Power
	 * @purpose Every time when device turns off the screen for 5 minutes and
	 *          during the sleeping time, the device will disconnect both mobile
	 *          network and Wi-Fi. Mobile network and Wi-Fi will be reconnected
	 *          when the user turns on the screen again.
	 * @author TJ Tsai (128135)
	 * @since HTC Sense tablet
	 * @see #HTC_WIRELESS_SLEEP_MODE_ENABLED
	 * @see #HTC_WIRELESS_SLEEP_START_TIME
	 * @see #HTC_WIRELESS_SLEEP_END_TIME
	 * 
	 */
	public static final String HTC_WIRELESS_SLEEP_END_TIME = "htc_wireless_sleep_end_time";

	/**
	 * Charm settings Light up Charm when messages are received
	 */
	public static final String CHARM_MESSAGE_NOTIFICATION = "charm_message_notification";

	/**
	 * Charm settings Light up Charm for all phone calls and missed calls
	 */
	public static final String CHARM_PHONE_NOTIFICATION = "charm_phone_notification";

	/**
	 * Charm settings Light up Charm when voice mails are received
	 */
	public static final String CHARM_VOICE_MAIL_NOTIFICATION = "charm_voice_mail_notification";

	/**
	 * Charm settings The status of Received notification in Message settings
	 */
	public static final String CHARM_MESSAGE_RECEIVED_NOTIFICATION = "charm_message_received_notification";

	/**
	 * Charm settings Indicate that does device support charm indicator
	 */
	public static final String CHARM_INDICATOR_SUPPORTED = "charm_indicator_supported";

	// + Mark.sl_Wu@20111007: setting modify inside framework for Htc speak +
	/**
	 * @author Mark.sl_Wu
	 * @since HTC sense 4.0
	 */
	public static final String HTCSPEAK_DEFALUT_LANG = "htcspeak_default_lang";
	// - Mark.sl_Wu@20111007 -

	// + Mark.sl_Wu@20111019: define show all quick tips key for show me +
	/**
	 * @author Mark.sl_Wu
	 * @since HTC sense 4.0
	 */
	public static final String SHOW_ALL_QUICK_TIPS = "show_all_quick_tips";
	// - Mark.sl_Wu@20111019 -

	// + Jyunchen Huang@20111018: setting modify inside framework for Power +
	/**
	 * @author Jyunchen Huang
	 * @since HTC sense 4.0
	 */
	public static final String ENABLE_POWER_EFFICIENCY = "enable_power_efficiency";
	public static final String SET_POWER_MODE = "set_powermode";
	public static final String POWERSAVER_ENABLE = "powersaver_enable";
	// - Jyunchen Huang@20111018 -

	// +[HTC_PHONE]:BVS, setting of 3G power saving.
	/**
	 * Whether or not power saving 3g is enabled. (0 = false, 1 = true)
	 * 
	 * 
	 */
	public static final String POWER_SAVE_3G = "3g_power_save";
	// -[HTC_PHONE]:BVS, setting of 3G power saving.

	// +[HTC_PHONE]:Ethan 0930,modify network selection
	/**
	 * 
	 */
	public static final String NETWORK_SELECTION = "network_selection";
	// -[HTC_PHONE]:Ethan 0930,modify network selection

	/**
	 * Magnifier setting for text selection. Determine if the function is
	 * enabled or not. 0 = disabled 1 = enabled
	 * 
	 * @author Yachun Tsai
	 * @since HTC sense 5.0
	 */
	public static final String HTC_MAGNIFIER_SETTING = "htc_magnifier_setting";

	/**
	 * Application storage location settings
	 * 
	 * The prefix string of application database key
	 * 
	 * @author Yachun Tsai
	 * @since HTC sense 4.0
	 */
	public static final String HTC_PREFIX_APP_STORAGE_LOCATION = "htc_app_storage_loc_";

	/**
	 * Application storage location settings
	 * 
	 * The constant value of application storage location settings: indicate
	 * unknown storage
	 * 
	 * @author Yachun Tsai
	 * @since HTC sense 4.0
	 */
	public static final int HTC_APP_STORAGE_LOCATION_UNKNOWN_STORAGE = 0;

	/**
	 * Application storage location settings
	 * 
	 * The constant value of application storage location settings: indicate
	 * using phone storage
	 * 
	 * @author Yachun Tsai
	 * @since HTC sense 4.0
	 */
	public static final int HTC_APP_STORAGE_LOCATION_PHONE_STORAGE = 1;

	/**
	 * Application storage location settings
	 * 
	 * The constant value of application storage location settings: indicate
	 * using SD card storage
	 * 
	 * @author Yachun Tsai
	 * @since HTC sense 4.0
	 */
	public static final int HTC_APP_STORAGE_LOCATION_SD_CARD_STORAGE = 2;

	/**
	 * Application storage location settings
	 * 
	 * This key is for backup&restore of application storage location settings
	 * 
	 * @author Yachun Tsai
	 * @since HTC sense 4.0
	 */
	public static final String HTC_APP_STORAGE_LOCATION_BACKUP = "htc_app_storage_loc_backup";

	// + Mark.sl_Wu@20120222: new key for Sense4.0 feature +
	/**
	 * HTC Gestures
	 */
	public static final String HTC_GESTURES_ENABLED = "htc_gestures_enabled";

	/**
	 * HTC Animation
	 */
	public static final String HTC_ANIMATION_ENABLED = "htc_animation_enabled";
	public static final String HTC_WINDOW_ANIMATION_SCALE = "htc_window_animation_scale";
	public static final String HTC_TRANSITION_ANIMATION_SCALE = "htc_transition_animation_scale";
	// - Mark.sl_Wu@20120222: new key for Sense4.0 feature -

	// Mark.SL_Wu@20120629: backup/restore for Beats Audio
	public static final String HTC_BEATS_AUDIO = "htc_beats_audio";

	/**
	 * Font Size Add a dummy key for backup/restore
	 */
	public static final String HTC_FONT_SIZE = "htc_font_size";

	/**
	 * The settings of compass warning to show a notification when compass is
	 * unavailable.
	 */
	public static final String COMPASS_WARNING = "compass_warning";

	/**
	 * Indicates that whether the notification for the Sense TV mirror mode is
	 * enabled or not.
	 * <UL>
	 * <LI>0: means disabled
	 * <LI>1: means enabled
	 * </UL>
	 * 
	 * @author TJ Tsai, 2012.11.20
	 * @since Sense 5.0
	 */
	public static final String HTC_STV_MIRROR_MODE_NOTIFICATION_ON = "htc_stv_mirror_mode_notification_on";

	/**
	 * Indicates that whether the quick tip for the Sense TV trackpad is enabled
	 * or not.
	 * <UL>
	 * <LI>0: means disabled
	 * <LI>1: means enabled
	 * </UL>
	 * 
	 * @author TJ Tsai, 2012.11.30
	 * @since Sense 5.0
	 */
	public static final String HTC_STV_TRACKPAD_QUICK_TIPS_ON = "htc_stv_trackpad_quick_tips_on";

	/**
	 * Indicates that whether the flip to speaker is supported. for some sense50
	 * device we need to remove 'flip to speaker' feature because the device
	 * speaker is in front side, for the request we need to set 'flip to
	 * speaker' flag to flase and hide the ui item, to prevent check device flag
	 * in both provider and setting ap, I add new flag for ap to remove item.
	 * <UL>
	 * <LI>0: means not supported
	 * <LI>1: means supported
	 * </UL>
	 * the key value have no ui to change, no need to backup
	 * 
	 * @author U22:Denny.cy, 2012.12.12
	 * @since Sense 5.0
	 */
	public static final String HTC_SETTING_SOUND_FLIP_TO_SPEAKER_SUPPORTED = "htc_setting_sound_flip_to_speaker_supported";

	/**
	 * Indicates that whether the htc_pull_to_fresh_sound_enabled
	 * 
	 * <UL>
	 * <LI>0: means off
	 * <LI>1: means on
	 * </UL>
	 * the key value have no ui to change, no need to backup
	 * 
	 * @author U22:Denny.cy, 2012.12.27
	 * @since Sense 5.0
	 */
	public static final String HTC_PULL_TO_FRESH_SOUND_ENABLED = "htc_pull_to_fresh_sound_enabled";

	/**
	 * U22:Denny.cy copy from Settings.java for restore old DB backup CDMA only
	 * settings Emergency Tone 0 = Off 1 = Alert 2 = Vibrate
	 * 
	 * @hide
	 */
	public static final String EMERGENCY_TONE = "emergency_tone";

	public static final String DEVICE_OUT_EARPIECE_NAME = "_earpiece";
	public static final String DEVICE_OUT_SPEAKER_NAME = "_speaker";
	public static final String DEVICE_OUT_WIRED_HEADSET_NAME = "_headset";
	public static final String DEVICE_OUT_WIRED_HEADPHONE_NAME = "_headphone";
	public static final String DEVICE_OUT_BLUETOOTH_SCO_NAME = "_bt_sco";
	public static final String DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME = "_bt_sco_hs";
	public static final String DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME = "_bt_sco_carkit";
	public static final String DEVICE_OUT_BLUETOOTH_A2DP_NAME = "_bt_a2dp";
	public static final String DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME = "_bt_a2dp_hp";
	public static final String DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME = "_bt_a2dp_spk";
	public static final String DEVICE_OUT_AUX_DIGITAL_NAME = "_aux_digital";
	public static final String DEVICE_OUT_ANLG_DOCK_HEADSET_NAME = "_analog_dock";
	public static final String DEVICE_OUT_DGTL_DOCK_HEADSET_NAME = "_digital_dock";
	public static final String DEVICE_OUT_USB_ACCESSORY_NAME = "_usb_accessory";
	public static final String DEVICE_OUT_USB_DEVICE_NAME = "_usb_device";
	public static final String DEVICE_OUT_FM_DEVICE_NAME = "_fm_device";
	public static final String DEVICE_OUT_FM_TX_DEVICE_NAME = "_fm_tx_device";
	public static final String DEVICE_OUT_DIRECT_OUTPUT_NAME = "_direct_output";
	public static final String DEVICE_OUT_HDMI_NAME = "_hdmi";

	public static final String PROFILE_PREFIX[] = { "Handset_", "MutiMedia_" };

	/* For default notification sounds */
	public static final String RINGTONE = "ringtone";
	public static final String RINGTONE_MODE_CDMA = "ringtone_mode_cdma";
	public static final String RINGTONE_MODE_WCDMA = "ringtone_mode_wcdma";
	public static final String NOTIFICATION_SOUND = "notification_sound";
	public static final String ALARM_ALERT = "alarm_alert";
	public static final String CALENDAR_SOUND = "calendar_sound";
	public static final String MESSAGE_SOUND = "message_sound";
	public static final String EMAIL_SOUND = "email_sound";
	public static final String TASK_SOUND = "task_sound";

	/**
	 * <P>
	 * [Sound][Do-Not-Disturb] Indicates that wether the DND feature is enabled
	 * or not. When do-not-disturb is on, incoming calls are disabled and audio,
	 * vibration and LED notifications are off.
	 * </P>
	 * 
	 * <H3>Value:</H3>
	 * <UL>
	 * <LI>0 (turn off)</LI>
	 * <LI>1 (turn on)</LI>
	 * </UL>
	 * 
	 * @author TJ Tsai
	 * @since Sense 5.5, {@link Build.VERSION_CODES#JELLY_BEAN_MR1}, 2013.05.16
	 */
	public static final String HTC_DND_FEATURE_ENABLED = "htc_dnd_feature_enabled";

	/**
	 * <P>
	 * [Sound][Do-Not-Disturb] Indicates that wether the 'Auto turn off' of DND
	 * is turned on or not.
	 * <P>
	 * 
	 * <P>
	 * If 'Auto turn off' is turned off, Do-not-disturb will always enable until
	 * the user turns off it. If 'Auto turn off' is turned on, it will auto turn
	 * off the DND feature once the timer is reached.
	 * </P>
	 * 
	 * <H3>Value:</H3>
	 * <UL>
	 * <LI>0 (unchecked)</LI>
	 * <LI>1 (checked)</LI>
	 * </UL>
	 * 
	 * @author TJ Tsai
	 * @since Sense 5.5, {@link Build.VERSION_CODES#JELLY_BEAN_MR1}, 2013.05.16
	 * @see {@link #HTC_DND_AUTO_TURN_OFF_MINUTES}
	 */
	public static final String HTC_DND_AUTO_TURN_OFF_ENABLED = "htc_dnd_auto_turn_off_enabled";

	/**
	 * <P>
	 * [Sound][Do-Not-Disturb] The timeout before do-not-disturb turns off.
	 * </P>
	 * 
	 * <P>
	 * <B>Value:</B> integer (the time unit is minute)
	 * </P>
	 * 
	 * @author TJ Tsai
	 * @since Sense 5.5, {@link Build.VERSION_CODES#JELLY_BEAN_MR1}, 2013.05.16
	 * @see {@link #HTC_DND_AUTO_TURN_OFF_ENABLED}
	 */
	public static final String HTC_DND_AUTO_TURN_OFF_MINUTES = "htc_dnd_auto_turn_off_minutes";

	/**
	 * <P>
	 * [Sound][Do-Not-Disturb] Indicates that wether the sound of DND is enabled
	 * on or not.
	 * </P>
	 * 
	 * <P>
	 * If 'Alarm & timer' is turned off, it will not play sound for alarms and
	 * timers set by HTC Clock. If 'Alarm & timer' is turned on, it will play
	 * sound for alarms and timers set by HTC Clock.
	 * </P>
	 * 
	 * <H3>Value:</H3>
	 * <UL>
	 * <LI>0 (unchecked)</LI>
	 * <LI>1 (checked)</LI>
	 * </UL>
	 * 
	 * @author TJ Tsai
	 * @since Sense 5.5, {@link Build.VERSION_CODES#JELLY_BEAN_MR1}, 2013.05.16
	 */
	public static final String HTC_DND_PLAY_SOUND_ENABLED = "htc_dnd_play_sound_enabled";
	/**
	 * <P>
	 * [Sound][Use ringtone volume for notification] Indicates that wether use
	 * set volume value for ringtone and notifications
	 * </P>
	 * 
	 * 
	 * <H3>Value:</H3>
	 * <UL>
	 * <LI>0 (unchecked)</LI>
	 * <LI>1 (checked)</LI>
	 * </UL>
	 * 
	 * @author U22:Denny.cy, 2013.07.19
	 * @since Sense 5.5
	 * 
	 */

	public static final String NOTIFICATIONS_USE_RING_VOLUME = "notifications_use_ring_volume";

	/**
	 * @deprecated Use {@link android.provider.Settings.ecure#DENY_USER_CONTROL}
	 *             instead
	 * 
	 */
	@Deprecated
	public static final String DENY_USER_CONTROL = Settings.Secure.DENY_USER_CONTROL;

	// @+ Ausmus 20110516
	/**
	 * VZW global roaming opitons.
	 */
	public static final String VZW_GLOBAL_ROAMING_OPTIONS = Settings.Secure.VZW_GLOBAL_ROAMING_OPTIONS;
	// @- Ausmus 20110516

	// HTC_WIFI_START
	/**
	 * @deprecated Use
	 *             {@link android.provider.Settings.Secure#WIFI_SECURE_NETWORKS_AVAILABLE_NOTIFICATION_ON}
	 *             instead
	 */
	@Deprecated
	public static final String WIFI_SECURE_NETWORKS_AVAILABLE_NOTIFICATION_ON = Settings.Secure.WIFI_SECURE_NETWORKS_AVAILABLE_NOTIFICATION_ON;
	// HTC_WIFI_END

	// BH_Lin@20110309 --------------------------------------------------->
	// purpose:
	/**
	 * Comma-separated list of data roaming providers that activities may
	 * access.
	 */
	public static final String DATA_ROAMING_ALLOWED = Settings.Secure.DATA_ROAMING_ALLOWED;

	/**
	 * Comma-separated list of data roaming providers for oma that activities
	 * may access.
	 */
	public static final String DATA_ROAMING_BLOCKED = Settings.Secure.DATA_ROAMING_BLOCKED;

	/**
	 * Comma-separated list of data roaming guard providers that activities may
	 * access.
	 */
	public static final String DATA_ROAMING_GUARD_ALLOWED = Settings.Secure.DATA_ROAMING_GUARD_ALLOWED;

	/**
	 * Comma-separated list of data roaming guard providers for oma that
	 * activities may access.
	 */
	public static final String DATA_ROAMING_GUARD_BLOCKED = Settings.Secure.DATA_ROAMING_GUARD_BLOCKED;

	/**
	 * Comma-separated list of voice roaming providers that activities may
	 * access.
	 */
	public static final String VOICE_ROAMING_ALLOWED = Settings.Secure.VOICE_ROAMING_ALLOWED;

	/**
	 * Comma-separated list of voice roaming providers for oma that activities
	 * may access.
	 */
	public static final String VOICE_ROAMING_BLOCKED = Settings.Secure.VOICE_ROAMING_BLOCKED;

	/**
	 * Comma-separated list of voice roaming providers for oma that activities
	 * may access.
	 */
	public static final String VOICE_ROAMING_GUARD_ALLOWED = Settings.Secure.VOICE_ROAMING_GUARD_ALLOWED;

	/**
	 * Comma-separated list of voice roaming guard providers for oma that
	 * activities may access.
	 */
	public static final String VOICE_ROAMING_GUARD_BLOCKED = Settings.Secure.VOICE_ROAMING_GUARD_BLOCKED;

	/**
	 * Comma-separated list of sms roaming providers for outgoing sms that
	 * activities may access.
	 */
	public static final String SMS_ROAMING_GUARD_ALLOWED = Settings.Secure.SMS_ROAMING_GUARD_ALLOWED;

	// BH_Lin@20110309 ---------------------------------------------------<

    public static final String VOLUME_FM = "volume_fm";
}
