package com.htc.lib2.weather;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Constants for Weather Content Provider
 */
public class WeatherConsts {
    private static final String LOG_TAG = "WeatherConsts";

    // For integration into Prism Non-Htc version.
    public static final String PRISM_CUSTOM_AUTHORITY_PREFIX = com.htc.lib2.weather.BuildConfig.PRISM_CUSTOM_AUTHORITY_PREFIX;
    private static final String EXTRA_AUTHORITY_PREFIX = PRISM_CUSTOM_AUTHORITY_PREFIX != null ? PRISM_CUSTOM_AUTHORITY_PREFIX + "." : "";
    public static final String PRISM_PREF_NAME = "weather_settings";
    public static final String WEATHER_APP_PACKAGE = "com.htc.Weather";
    public static final String TIMEZONE_UPDATOR_PACKAGE = "com.htc.provider.citytimezoneweatherprovider";
    public static final String HSP_PACKAGE = "com.htc.sense.hsp";
    public static final String PERMISSION_HSP = "com.htc.sense.permission.APP_HSP";

    // Weather provider
    private static final String _AUTHORITY_ = "com.htc.provider.weather";
    /** AUTHORITY of Weather provider */
    private static final String AUTHORITY;
    /** AUTHORITY of Weather App provider */
    public static final String AUTHORITY_WEATHER_APP = WEATHER_APP_PACKAGE + "." + _AUTHORITY_;
    /** AUTHORITY of TimezoneUpdater provider */
    private static final String AUTHORITY_TIMEZONE_UPDATOR = TIMEZONE_UPDATOR_PACKAGE + "." + _AUTHORITY_;

    /** LOCATION_LIST_PATH of Weather provider*/
    public static final String LOCATION_LIST_PATH = "locationlist";
    /** LOCATION_LIST_PATH_WITH_LANGUAGE  of Weather provider*/
    public static final String LOCATION_LIST_PATH_WITH_LANGUAGE = LOCATION_LIST_PATH + "/lang";
    /** SETTING_PATH of Weather provider*/
    public static final String SETTING_PATH = "setting";
    /** LOCATION_PATH of Weather provider*/
    public static final String LOCATION_PATH = "location";

    /** METHOD LOCATION_LIST_EXIST */
    public static final String METHOD_LOCATION_LIST_EXIST = "METHOD_LOCATION_LIST_EXIST";
    public static final String METHOD_ENABLE_DOWNLOAD_LOCATION_LIST = "METHOD_ENABLE_DOWNLOAD_LOCATION_LIST";
    public static final String METHOD_DOWNLOAD_LOCATION_LIST = "METHOD_DOWNLOAD_LOCATION_LIST";
    public static final String METHOD_REGISTER_LISTENER = "METHOD_REGISTER_LISTENER";
    public static final String METHOD_UNREGISTER_LISTENER = "METHOD_UNREGISTER_LISTENER";
    public static final String KEY_MESSENGER = "KEY_MESSENGER";
    public static final String KEY_LOCATION_LIST_EXIST = "KEY_LOCATION_LIST_EXIST";
    

    /** Download result */
    public static final int DOWNLOAD_RESULT_SUCCESS = 1;
    public static final int DOWNLOAD_RESULT_FAIL = 2;

    /** Download result code */
    public static final int DOWNLOAD_SUCCESS_CODE_DOWNLOADED = 1;
    public static final int DOWNLOAD_SUCCESS_CODE_ALREADY_DOWNLOAD = 2;

    public static final int DOWNLOAD_FAIL_CODE_FLAG_NOT_SET = 101;
    public static final int DOWNLOAD_FAIL_CODE_NO_NETWORK = 102;
    public static final int DOWNLOAD_FAIL_CODE_NOT_SUPPORT_LANGUAGE = 103;
    public static final int DOWNLOAD_FAIL_CODE_NO_URL = 104;
    public static final int DOWNLOAD_FAIL_CODE_IO_ERROR = 105;


    // Sense 5.5 feature: common city list between clock and weather
    /**
     * Weather ap category used in database.
     */
    public static final String APP_WEATHER = "com.htc.elroy.Weather";
    /**
     * Clock ap category used in database.
     */
    protected static final String APP_WORLDCLOCK = "com.htc.android.worldclock";
    
    /**
     * LcationSyncService ap category used in database.
     */
    public static final String APP_LOCATIONSERVICE = "com.htc.htclocationservice";
    
    /** Consts of city type 1: current location city */
    public static final int TYPE_CURRENT_LOCATION = 1;
    /** Consts of city type 2: city code city */
    public static final int TYPE_LOC_CODE = 2;
    /** Consts of city type 3: latitude and longitude */
    public static final int TYPE_LATITUDE = 3;
    
    /** sync service intent category name */
    public static final String SYNC_SERVICE_TRIGGER_INTENT_EXTRA_CATEGORY_NAME = "categoryName";
    /** sync service trigger intent extra request */
    public static final String SYNC_SERVICE_TRIGGER_INTENT_EXTRA_REQUESTS = "requests";
    /** sync service trigger intent extra source */
    public static final String SYNC_SERVICE_TRIGGER_INTENT_EXTRA_SOURCE = "source";

    /** sync service trigger source request 1 : app calls request() */
    public static final int SYNC_SERVICE_TRIGGER_SOURCE_REQUEST = 1; 
    /** sync service trigger source request 2 : app calls trigger() */
    public static final int SYNC_SERVICE_TRIGGER_SOURCE_FORCE_UPDATE = 2;
    /** sync service trigger source request 3 : receive a auto sync notification */
    public static final int SYNC_SERVICE_TRIGGER_SOURCE_AUTO_SYNC = 3;
    
  ///~*

    /** notify intent from WSPPUtility */
    public static final String WSP_UTILITY_NOTIFICATION_INTENT_ACTION_NAME = "com.htc.util.weather.WSPUtility";

    ///*~ Sync service result intent
    /** Sync service result intent action name */
    public static final String SYNC_SERVICE_RESULT_INTENT_ACTION_NAME = "com.htc.sync.provider.weather.result";
    /** Sync service  result - boolean: true / false */
    public static final String SYNC_SERVICE_RESULT_INTENT_EXTRA_STATUS = "status";
    /** Sync service result data intent */
    public static final String SYNC_SERVICE_RESULT_INTENT_EXTRA_DATA = "data";
    ///~*

    /** notify location list database is downloaded */
    public static final String INTENT_ACTION_LOCATION_LIST_DATABASE_DOWNLOADED = "com.htc.sync.provider.weather.location_list_downloaded";
    
    ///*~~:setting notify intent
    /**  setting intent action name */
    public static final String SETTING_INTENT_ACTION_NAME = "com.htc.sync.provider.weather.SETTINGS_UPDATED";
    /**  setting intent extra setting data */
    public static final String SETTING_INTENT_EXTRA_NAME_SETTING_DATA = "settingData";
    /**  setting key about update when open*/
    public static final String SETTING_KEY_UPDATE_WHENOPEN = "com.htc.sync.provider.weather.setting.updatewhenopen";
    /**  setting key for weather sync on/off*/
    public static final String SETTING_KEY_AUTO_SYNC_SWITCH = "com.htc.sync.provider.weather.setting.autosyncswitch";
    /**  setting key about auto sync frequency*/
    public static final String SETTING_KEY_AUTO_SYNC_FREQUENCY = "com.htc.sync.provider.weather.setting.autosyncfrequency";
    /**  setting key about temperature unit */
    public static final String SETTING_KEY_TEMPERATURE_UNIT = "com.htc.sync.provider.weather.setting.temperatureunit";
    /**  setting key about sound effect */
    public static final String SETTING_KEY_SOUND_EFFECT = "com.htc.sync.provider.weather.setting.soundeffect";
    //private~~*
    
    /** WeatherSyncProvider Weather News setting key*/
    public static final String SETTING_KEY_WEATHERNEWS = "com.htc.sync.provider.weather.setting.weathernews";

    private static final String _SYNC_AUTHORITY_ = "com.htc.sync.provider.weather";
    /** WeatherSyncProvider AUTHORITY */
    public static final String SYNC_AUTHORITY;
    /** Weather APP AUTHORITY */
    public static final String SYNC_AUTHORITY_WEATHER_APP = WEATHER_APP_PACKAGE + "." + _SYNC_AUTHORITY_;
    /** TimezoneUpdator AUTHORITY */
    private static final String SYNC_AUTHORITY_TIMEZONE_UPDATOR = TIMEZONE_UPDATOR_PACKAGE + "." + _SYNC_AUTHORITY_;

    /** target package name **/
    static final String TARGET_PACKAGE_NAME;

    static {
        if (PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // prism
            Log.d(LOG_TAG, "use Prism");
            AUTHORITY = EXTRA_AUTHORITY_PREFIX + _AUTHORITY_;
            SYNC_AUTHORITY = EXTRA_AUTHORITY_PREFIX + _SYNC_AUTHORITY_;
            TARGET_PACKAGE_NAME = PRISM_CUSTOM_AUTHORITY_PREFIX;
        } else {
            if (isSystemApp(TIMEZONE_UPDATOR_PACKAGE)) {
                // timezone updator
                Log.d(LOG_TAG, "use TimeZoneUpdator");
                AUTHORITY = AUTHORITY_TIMEZONE_UPDATOR;
                SYNC_AUTHORITY = SYNC_AUTHORITY_TIMEZONE_UPDATOR;
                TARGET_PACKAGE_NAME = TIMEZONE_UPDATOR_PACKAGE;
            } else {
                if (android.os.Build.VERSION.SDK_INT >= 24) {
                    // weather
                    Log.d(LOG_TAG, "use Weather");
                    AUTHORITY = AUTHORITY_WEATHER_APP;
                    SYNC_AUTHORITY = SYNC_AUTHORITY_WEATHER_APP;
                    TARGET_PACKAGE_NAME = WEATHER_APP_PACKAGE;
                } else {
                    // hsp
                    Log.d(LOG_TAG, "use HSP");
                    AUTHORITY = _AUTHORITY_;
                    SYNC_AUTHORITY = _SYNC_AUTHORITY_;
                    TARGET_PACKAGE_NAME = HSP_PACKAGE;
                }
            }
        }
    }

    /** CONTENT_URI of Weather provider */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    /** WeatherSyncProvider path data */
    public static final String PATH_DATA = "data";
    /** WeatherSyncProvider URI_DATA */
    public static final Uri URI_DATA = Uri.parse("content://" + SYNC_AUTHORITY + "/" + PATH_DATA);
    
    /** Weather Account Type */
    public static final String ACCOUNT_TYPE = "com.htc.sync.provider.weather";
    /** Weather Account Authority **/
    public static final String ACCOUNT_AUTHORITY = EXTRA_AUTHORITY_PREFIX + _SYNC_AUTHORITY_;

    /** Location list column name of Weather Content Provider*/
    public static enum LOCATION_LIST_COLUMN_NAME {
        /**_id*/
        _id, 
        /**code*/
        code, 
        /**name*/
        name, 
        /**state*/
        state, 
        /**country*/
        country, 
        /**latitude*/
        latitude, 
        /**longitude*/
        longitude, 
        /**timezone*/
        timezone, 
        /**timezoneId*/
        timezoneId
    };

    /** setting table column name */
    public static enum SETTING_COLUMN_NAME {
        /**_id*/
        _id, 
        /**app*/
        app, 
        /**key*/
        key, 
        /**value*/
        value
    };

    /** Location table column name */
    public static enum LOCATION_COLUMN_NAME {
        /**_id*/
        _id, 
        /**app*/
        app, 
        /**type*/
        type, 
        /**code*/
        code, 
        /**name*/
        name, 
        /**state*/
        state, 
        /**country*/
        country, 
        /**latitude*/
        latitude, 
        /**longitude*/
        longitude, 
        /**timezone*/
        timezone, 
        /**timezoneId*/
        timezoneId
    };
    
    /** WeatherSyncProvider data table column type */
    public static enum TABLE_DATA_COLUMNS {
        /**TABLE_DATA_COLUMNS*/
        _id, type, param1, param2, lastRequest, lastUpdate,
        /**TABLE_DATA_COLUMNS*/
        curTempC, curTempF, curConditionId,
        /**TABLE_DATA_COLUMNS*/
        fstName, fstDate, fstConditionId, fstHighTempC, fstHighTempF, fstLowTempC, fstLowTempF,
        /**TABLE_DATA_COLUMNS*/
        curLocLat, curLocLng, curLocLatTrim, curLocLngTrim,
        /**TABLE_DATA_COLUMNS*/
        curLocName, curLocState, curLocCountry, curLocTimezoneId,
        //Add columns for Tablet
        /**TABLE_DATA_COLUMNS*/
        cityLocalTime,cityLatitude,cityLongitude,cityTimeZone,cityWebURL,
        /**TABLE_DATA_COLUMNS*/
        dayLightFlag,
        /**TABLE_DATA_COLUMNS*/
        curFeelTempC,curFeelTempF,curHumidity,curWinddirection,curWindspeed,curVisibility,
        /**TABLE_DATA_COLUMNS*/
        fstSunrise,fstSunset,
        /**TABLE_DATA_COLUMNS*/
        fstFeelHighTempC,fstFeelHighTempF,fstFeelLowTempC,fstFeelLowTempF,
        /**TABLE_DATA_COLUMNS*/
        fstNightFeelHighTempC,fstNightFeelHighTempF,fstNightFeelLowTempC,fstNightFeelLowTempF,
        /**TABLE_DATA_COLUMNS*/
        fstNightConditionId,fstNightHighTempC,fstNightHighTempF,fstNightLowTempC,fstNightLowTempF,
        /**TABLE_DATA_COLUMNS*/
        fstPrecip,fstNightPrecip,
        /**TABLE_DATA_COLUMNS*/
        hourName,hourConditionId,hourTempC,hourTempF,hourFeelTempC,hourFeelTempF,hourPrecip,

        //Add for initializing time zone
        /**TABLE_DATA_COLUMNS*/
        timeZoneAbbreviation ,currentSetTimezone,
        
        //Add for hourly date time, web link and daily web link
        /**TABLE_DATA_COLUMNS*/
        hourEpochDateTime, hourWebURL, fstWebURL,

        // Add for PM 2.5
        pm25,
        // Add for AirQualityDaily
        airQualityDaily,
    };
    
    /** SEARCH COLUMN name */
   	public enum SEARCH_COLUMN {
   	    /*** CITY_AND_COUNTRY */
   		CITY_AND_COUNTRY, 
   		/** CITY */
   		CITY, 
   		/** COUNTRY */
   		COUNTRY, 
   		/** STATE */
   		STATE, 
   		/** CITY_STATE_AND_COUNTRY */
   		CITY_STATE_AND_COUNTRY
   	};
   	
   	/** Search type */
	public enum SEARCH_TYPE {
	    /** START_WITH */
		START_WITH, 
		/** END_WITH */
		END_WITH, 
		/** CONTAIN */
		CONTAIN, 
		/** MATCH_IGONE_CASE */
		MATCH_IGONE_CASE
	};

    /** LOCATION_TYPE_CODE */
    public static final int LOCATION_TYPE_CODE = 1;
    /** LOCATION_TYPE_CUSTOM*/
    public static final int LOCATION_TYPE_CUSTOM = 2;

    // Languages
    /**LANG_ENGLISH_UNITED_STATES*/
    public static final String LANG_ENGLISH_UNITED_STATES = "0409WWE";
    
    
 // TODO
    /* ****************************** *
     * For Current Data:              *
     * - Current Condition ID         *
     * - Current Temperature (C/F)    *
     * - High/Low Temperature (C/F)   *
     * ****************************** */
    /** Weather data bundle key: KEY_OUT_CURR_COND_ID.*/
    public static final String KEY_OUT_CURR_COND_ID  = "currConditionID";
    /** Weather data bundle key: KEY_OUT_CURR_TEMP_C.*/
    public static final String KEY_OUT_CURR_TEMP_C   = "currTempC";
    /** Weather data bundle key: KEY_OUT_CURR_TEMP_F.*/
    public static final String KEY_OUT_CURR_TEMP_F   = "currTempF";
    /** Weather data bundle key: KEY_OUT_HIGH_TEMP_C.*/
    public static final String KEY_OUT_HIGH_TEMP_C   = "highTempC";
    /** Weather data bundle key: KEY_OUT_HIGH_TEMP_F.*/
    public static final String KEY_OUT_HIGH_TEMP_F   = "highTempF";
    /** Weather data bundle key: KEY_OUT_LOW_TEMP_C.*/
    public static final String KEY_OUT_LOW_TEMP_C    = "lowTempC";
    /** Weather data bundle key: KEY_OUT_LOW_TEMP_F.*/
    public static final String KEY_OUT_LOW_TEMP_F    = "lowTempF";

    /* ****************************** *
     * For Weather APP:               *
     * - Current Feel Temperature     *
     * - Humidity                     *
     * - Wind Direction               *
     * - Wind Speed (MI/KM)           *
     * - Visibility (MI/KM)           *
     * - Sunrise/Sunset               *
     * - Forcast date                  *
     * - Forcast day                  *
     * - Forcast condition id         *
     * ****************************** */
    /** Weather data bundle key: KEY_OUT_FEEL_TEMP_C.*/
    public static final String KEY_OUT_FEEL_TEMP_C   = "feelTempC";
    /** Weather data bundle key: KEY_OUT_FEEL_TEMP_F.*/
    public static final String KEY_OUT_FEEL_TEMP_F   = "feelTempF";
    /** Weather data bundle key: KEY_OUT_HUMIDITY.*/
    public static final String KEY_OUT_HUMIDITY      = "humidity";
    /** Weather data bundle key: KEY_OUT_WINDDIRECTION.*/
    public static final String KEY_OUT_WINDDIRECTION = "windDirection";
    /** Weather data bundle key: KEY_OUT_WINDSPEED_MI.*/
    public static final String KEY_OUT_WINDSPEED_MI  = "windSpeedMI";
    /** Weather data bundle key: KEY_OUT_WINDSPEED_KM.*/
    public static final String KEY_OUT_WINDSPEED_KM  = "windSpeedKM";
    /** Weather data bundle key: KEY_OUT_VISIBILITY_MI.*/
    public static final String KEY_OUT_VISIBILITY_MI = "visibilityMI";
    /** Weather data bundle key: KEY_OUT_VISIBILITY_KM.*/
    public static final String KEY_OUT_VISIBILITY_KM = "visibilityKM";
    /** Weather data bundle key: KEY_OUT_SUNRISE.*/
    public static final String KEY_OUT_SUNRISE       = "sunrise";
    /** Weather data bundle key: KEY_OUT_SUNSET.*/
    public static final String KEY_OUT_SUNSET        = "sunset";
    /** Weather data bundle key: KEY_OUT_FSTDATE.*/
    public static final String KEY_OUT_FSTDATE       = "fstDate";
    /** Weather data bundle key: KEY_OUT_FSTNAME.*/
    public static final String KEY_OUT_FSTNAME        = "fstName";
    /** Weather data bundle key: KEY_OUT_FSTCONDITIONID.*/
    public static final String KEY_OUT_FSTCONDITIONID = "fstCondId";
    /** Weather data bundle key: KEY_OUT_DAYLIGHT.*/
    public static final String KEY_OUT_DAYLIGHT = "daylight";
    /** Weather data bundle key: KEY_OUT_FSTNIGHTCONDITIONID.*/
    public static final String KEY_OUT_FSTNIGHTCONDITIONID = "fstnightCondId";
    /** Weather data bundle key: KEY_OUT_NIGHT_HIGH_TEMP_C.*/
    public static final String KEY_OUT_NIGHT_HIGH_TEMP_C   = "nighthighTempC";
    /** Weather data bundle key: KEY_OUT_NIGHT_HIGH_TEMP_F.*/
    public static final String KEY_OUT_NIGHT_HIGH_TEMP_F   = "nighthighTempF";
    /** Weather data bundle key: KEY_OUT_NIGHT_LOW_TEMP_C.*/
    public static final String KEY_OUT_NIGHT_LOW_TEMP_C    = "nightlowTempC";
    /** Weather data bundle key: KEY_OUT_NIGHT_LOW_TEMP_F.*/
    public static final String KEY_OUT_NIGHT_LOW_TEMP_F    = "nightlowTempF";
    /**Weather data bundle key:KEY_OUT_FST_WEB_URL.*/
    public static final String KEY_OUT_FST_WEB_URL = "fstWebUrl";
    /**Weather data bundle key:KEY_OUT_CITY_WEB_URL.*/
    public static final String KEY_OUT_CITY_WEB_URL    = "cityWebUrl";
    /**Weather data bundle key:KEY_OUT_LAST_UPDATE.*/
    public static final String KEY_OUT_LAST_UPDATE    = "lastUpdate";
    /**Weather data bundle key:KEY_OUT_CITY_LOCALTIME.*/
    public static final String KEY_OUT_CITY_LOCALTIME    = "cityLocalTime";
    /**Weather data bundle key:KEY_OUT_HOUR_NAME.*/
    public static final String KEY_OUT_HOUR_NAME    = "hourName";
    /**Weather data bundle key:KEY_OUT_HOUR_CONDITIONID.*/
    public static final String KEY_OUT_HOUR_CONDITIONID    = "hourConditionId";
    /**Weather data bundle key:KEY_OUT_HOURPRECIP.*/
    public static final String KEY_OUT_HOUR_PRECIP    = "hourPrecip";
    /**Weather data bundle key:KEY_OUT_HOURTEMPC.*/
    public static final String KEY_OUT_HOUR_TEMPC    = "hourTempC";
    /**Weather data bundle key:KEY_OUT_HOURTEMPF.*/
    public static final String KEY_OUT_HOUR_TEMPF    = "hourTempF";
    /**Weather data bundle key:KEY_OUT_HOUR_WEB_URL.*/
    public static final String KEY_OUT_HOUR_WEB_URL = "hourWebUrl";
    /**Weather data bundle key:KEY_OUT_HOUR_EPOCH_DATE_TIME.*/
    public static final String KEY_OUT_HOUR_EPOCH_DATE_TIME = "hourEpochDateTime";

    public static final String KEY_OUT_PM25 = "pm25";
    public static final String KEY_OUT_AIRQUALITY_DAILY = "airQualityDaily";

    /**Weather data bundle key:KEY_OUT_CurWeatherData.*/
    public static final String KEY_OUT_CURWEATHER_DATA = "curWeatherData";
    /**Weather data bundle key:KEY_OUT_FstWeatherData.*/
    public static final String KEY_OUT_FSTWEATHER_DATA = "fstWeatherData";
    /**Weather data bundle key:KEY_OUT_HourWeatherData.*/
    public static final String KEY_OUT_HOURWEATHER_DATA = "hourWeatherData";
    /**Weather data bundle key:KEY_OUT_CATEGORYNAME.*/
    public static final String KEY_OUT_CATEGORYNAME = "categoryName";

    /** Weather data bundle key: KEY_OUT_TRIGGER_TIME.*/
    public static final String KEY_OUT_TRIGGER_TIME    = "triggerTime";

    /** Weather data bundle key: key for */
    public static final String KEY_OUT_TIMEZONE_ID = "cityTimezoneID";

    /* ****************************** *
     * For GPS Sync failed code (Device only)     *
     * ****************************** */

    @Deprecated
    public static final int GPS_ERROR_CODE_NODE = 0;

    public static final int GPS_ERROR_CODE_NONE = 0;

    public static final int GPS_ERROR_CODE_NOT_SUPPORT = 1;

    public static final int GPS_ERROR_CODE_SIGNAL_NOT_AVAILABLE = 2;

    public static final int GPS_ERROR_CODE_LOCATION_SERVICE_OFF = 3;

    private static Context getContext() {
        try {
            Class<?> clz = Class.forName("android.app.ActivityThread");
            Method method = clz.getMethod("currentApplication");
            Context ctx = (Context) method.invoke(method, new Object[]{});
            if (ctx == null) {
                Log.w(LOG_TAG, "getContext() is null");
            }
            return ctx;
        } catch (Exception e) {
            Log.e(LOG_TAG, "getContext() exception: " + e.getMessage());
        }
        return null;
    }

    private static boolean isSystemApp(String pkg) {
        final Context context = getContext();
        if (context != null) {
            try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(pkg, PackageManager.GET_META_DATA);
                return (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            } catch (NameNotFoundException e) {
            } catch (Exception e) {
                Log.e(LOG_TAG, "isSystemApp() exception: " + e);
            }
        }
        return false;
    }
}
