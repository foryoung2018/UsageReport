package com.htc.lib2.weather;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib2.weather.WeatherConsts.SEARCH_COLUMN;
import com.htc.lib2.weather.WeatherConsts.SEARCH_TYPE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Weather content provider utility
 * @author MASD_U59000
 */
public class WeatherUtility {
	 // htc log debug flag
    private static final boolean LOG_FLAG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    // htc log security debug flag
    private static final boolean LOG_FLAG_SECURITY = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag;
    /**Log tag*/
	private static final String LOG_TAG = "WeatherUtility";
	/** URI_SETTING */
	private static final Uri URI_SETTING = Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.SETTING_PATH);
	/** URI_LOCATION */
	private static final Uri URI_LOCATION = Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_PATH);
	/** URI_LOCATION_LIST */
	private static final Uri URI_LOCATION_LIST = Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_LIST_PATH);
    /** sync service trigger intent action name */
    private static final String SYNC_SERVICE_TRIGGER_INTENT_ACTION_NAME = "com.htc.sync.provider.weather.SyncService";
    
    /** intent to launch user agree dialog activity */
    private static final String INTENT_ACTION_LAUNCH_USER_AGREE_DIALOG = "com.htc.sense.hsp.weather.launch_useractivity";
    
    /**
     * category name for notification intent
     */
    private static final String WSP_UTILITY_FUNC_SET_SYNC_AUTOMATICALLY = "com.htc.util.weather.WSPUtility.setSyncAutomatically";
    
    private static final String TRIGGER_WEATHER_WIDGET_UPDATE_DATA = "com.htc.sync.provider.weather.trigger.weatherwidget";
    private static final String tri_category = "trigger_widget_update";
    
    /** DEFAULT AUTO SYNC FREQUENCY */
    private static final long DEFAULT_AUTO_SYNC_FREQUENCY = 3600000;
    
    /**
     */
    private static final String WSP_SETTING_APP_NAME = "com.htc.sync.provider.weather";
    /**
     */
    private static final String WSP_FLAG_CUR_LOC_KEY = "curLocFlag";
    /**
     */
    private static final String WSP_FLAG_CUR_LOC_ON = "on";
    /**
     */
    private static final String WSP_FLAG_CUR_LOC_OFF = "off";
    
    /**
     * Temperature unit [Celsius]
     */
    private static final String TEMPERATURE_UNIT_CELSIUS = "c";
    /**
     * Temperature unit [Fahrenheit]
     */
    private static final String TEMPERATURE_UNIT_FAHRENHEIT = "f";
    private static final String DEFAULT_TEMPERATURE_UNIT = TEMPERATURE_UNIT_CELSIUS;
    
    /** Weather Forecast day */
    private static int numberOfDay = 8;
    
    /**************************************************
	 * Location list helper
	 **************************************************/
	private static final Uri LOCATION_LIST_URI_IN_ENGLISH = Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_LIST_PATH_WITH_LANGUAGE + "/" + WeatherConsts.LANG_ENGLISH_UNITED_STATES);
	private static final Uri LOCATION_LIST_URI_DEFAULT = Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_LIST_PATH);
    
    private static final String CURSOR_EXTRA_KEY_HAS_EXTINFO = "_hasExtInfo";
	private static final String CURSOR_EXTRA_KEY_NAME = "_name";
	private static final String CURSOR_EXTRA_KEY_STATE = "_state";
	private static final String CURSOR_EXTRA_KEY_COUNTRY = "_country";
	
	
	// + eric : for English range
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int UBOUND_UPPERCASE_EN_US = 0x0041;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int LBOUND_UPPERCASE_EN_US = 0x005a;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int UBOUND_LOWERCASE_EN_US = 0x0061;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int LBOUND_LOWERCASE_EN_US = 0x007a;
	// - eric : for English range
	// + eric : for Greek range
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int UBOUND_UPPERCASE_EN_GR = 0x0391;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int LBOUND_UPPERCASE_EN_GR = 0x03a9;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int UBOUND_LOWERCASE_EN_GR = 0x03b1;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int LBOUND_LOWERCASE_EN_GR = 0x03c9;
	// - eric : for Greek range
	// + eric : for Russian range
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int UBOUND_UPPERCASE_RU_RU = 0x0410;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int LBOUND_UPPERCASE_RU_RU = 0x042f;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int UBOUND_LOWERCASE_RU_RU = 0x0430;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
	private static final int LBOUND_LOWERCASE_RU_RU = 0x044f;
	// - eric : for Russian range
    
    protected static WeatherLocation _getCurrentLocation(Context context) {
        WeatherLocation[] curLoc = WeatherUtility.loadLocations(context.getContentResolver(),
                "com.htc.htclocationservice");
        if (curLoc == null || curLoc.length < 1) {
            return null;
        } else if (curLoc.length > 1) {
            Log.w(LOG_TAG, "_getCurrentLocation: curLoc.length:" + curLoc.length);
        }

        WeatherLocation cl = curLoc[0];
        if (cl == null || cl.getLatitude() == null || cl.getLatitude().length() < 1
                || cl.getLongitude() == null || cl.getLongitude().length() < 1) {
            return null;
        }

        if (cl.getName() == null)
            cl.setName("");
        if (cl.getState() == null)
            cl.setState("");
        if (cl.getCountry() == null)
            cl.setCountry("");
        if (cl.getTimezoneId() == null)
            cl.setTimezoneId("");

        return cl;
    }
    
    /**
     * Get Current Location Cache Data 
     * @param context context
     * @param req weather request message
     * @return weather current location cache data
     */
    protected static WeatherData _getCacheData(Context context, WeatherRequest req, WeatherLocation sysCurLoc) {
//        if (LOG_FLAG) Log.d(LOG_TAG, "_getCacheData() - context=null?: " + (context == null) + ",req=null?: " + (req == null) );
        if(context == null || req == null) return null;

        // check if WeatherSyncProvider is installed and permission is valid
        if (!isWeatherSyncProviderInstalled(context.getContentResolver())) {
            return null;
        }

        WeatherData cacheData = null;
        
        // query data from cache database
        String where = WeatherRequest.generateWeatherRequestDbWhereCondition(req.getType(), req.getParam1(), req.getParam2());
        
        Cursor cursor = null;
        ContentProviderClient unstableContentProvider = null;
        try {
            unstableContentProvider = context.getContentResolver().acquireUnstableContentProviderClient(WeatherConsts.URI_DATA);
            cursor = unstableContentProvider.query(
                    WeatherConsts.URI_DATA,
//                    WeatherData.getProjection(),
                    null,
                    where,
                    null,
                    null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                cacheData = new WeatherData(cursor);
            }
        } catch (Exception e) {
            if(LOG_FLAG){
                Log.w(LOG_TAG, "_getCacheData() - Catch Exception: " , e);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if(unstableContentProvider != null) {
                unstableContentProvider.release();
            }
        }
        
        if (WeatherConsts.TYPE_CURRENT_LOCATION == req.getType()) {
//            if (LOG_FLAG_SECURITY) Log.d(LOG_TAG, "db cur: " + sysCurLoc.getName() + "_" + sysCurLoc.getState() + "_" + sysCurLoc.getCountry() + "_" + sysCurLoc.getLatitude() + "_" + sysCurLoc.getLongitude());
            
            String curLocLatTrim = trimLatitude(sysCurLoc.getLatitude());
            String curLocLngTrim = trimLatitude(sysCurLoc.getLongitude());
            
            // for current location type, we will compare the name/latitude to check whether cache data is existed or not
            if (cacheData != null) {
//                if (LOG_FLAG_SECURITY) Log.d(LOG_TAG, "check cache data, cur in cache: " + cacheData.getCurLocName() + "_" + cacheData.getCurLocState() + "_" + cacheData.getCurLocCountry() + "_" + cacheData.getCurLocLat() + "_" + cacheData.getCurLocLng());

                
                // Compare rule:
                // If one of sys and cache cur has address, then compare with address, or compare with latitude.
                if (cacheData.getCurLocName().length() > 0 || cacheData.getCurLocState().length() > 0 || cacheData.getCurLocCountry().length() > 0 ||
                    sysCurLoc.getName().length() > 0 || sysCurLoc.getState().length() > 0 || sysCurLoc.getCountry().length() > 0) {
                    // compare address
                    if (!sysCurLoc.getName().equals(cacheData.getCurLocName()) ||
                        !sysCurLoc.getState().equals(cacheData.getCurLocState()) ||
                        !sysCurLoc.getCountry().equals(cacheData.getCurLocCountry())) {
                        cacheData = null; // cur in cache != cur
                    }
                    
                } else {
                    // compare latitude
                    if (!curLocLatTrim.equals(cacheData.getCurLocLatTrim()) ||
                        !curLocLngTrim.equals(cacheData.getCurLocLngTrim())) {
                        cacheData = null; // cur in cache != cur
                    }                
                }
                
                // delete cur cache data if sys-loc is different from cache-loc
                                /*
                if (cacheData == null) {
                    cleanCurInCache(context, req);
                }*/

            }
            
//            if (LOG_FLAG) Log.d(LOG_TAG, "check cache data, has cache? " + (cacheData != null));
            
            // update sys cur to request obj
            req.setReqCurLoc(sysCurLoc.getLatitude(), sysCurLoc.getLongitude(), curLocLatTrim, curLocLngTrim, 
                sysCurLoc.getName(), sysCurLoc.getState(), sysCurLoc.getCountry(), sysCurLoc.getTimezoneId(), cacheData);
        } else {
            if (LOG_FLAG_SECURITY) Log.d(LOG_TAG, "check cache data, pam1: " + req.getParam1() + ", pam2: " + req.getParam2() + ", has cache? " + (cacheData != null));
        }        
        
        return cacheData;
    }
    
    /**
     * Update WeatherSyncProvider request time.
     * 
     * @param context context
     * @param req WSP request item
     */
    protected static void _addRequestInDatabase(Context context, WeatherRequest req) {
        if (context == null || req == null)
            return;

        if (!isWeatherSyncProviderInstalled(context.getContentResolver())) {
            return;
        }

        boolean added = false;

        String where = WeatherRequest.generateWeatherRequestDbWhereCondition(req.getType(), req.getParam1(),
                req.getParam2());

        ContentValues values = new ContentValues();
        values.put(WeatherConsts.TABLE_DATA_COLUMNS.lastRequest.name(), System.currentTimeMillis());
        ContentProviderClient unstableContentProvider = null;
        try {
            unstableContentProvider = context.getContentResolver()
                    .acquireUnstableContentProviderClient(WeatherConsts.URI_DATA);
            if (_hasRequested(context, where)) {
                // updateq
                int updated;
                updated = unstableContentProvider.update(WeatherConsts.URI_DATA, values, where, null);
                if (updated > 0) {
                    added = true;
                }
            } else {
                // insert
                values.put(WeatherConsts.TABLE_DATA_COLUMNS.type.name(), req.getType());
                values.put(WeatherConsts.TABLE_DATA_COLUMNS.param1.name(), req.getParam1());
                values.put(WeatherConsts.TABLE_DATA_COLUMNS.param2.name(), req.getParam2());

                Uri uri = unstableContentProvider.insert(WeatherConsts.URI_DATA, values);
                if (uri != null) {
                    added = true;
                }
            }
        } catch (Exception e) {
            if (LOG_FLAG) {
                Log.w(LOG_TAG, "_addRequestInDatabase() - Catch Exception: ", e);
            }
            added = false;
        } finally {
            if (unstableContentProvider != null) {
                unstableContentProvider.release();
            }
        }

        if (added) {
            // if (LOG_FLAG) Log.v(LOG_TAG, LOG_PREFIX +
            // "adding request is successful, request: " + req.toDebugInfo());
        } else {
            Log.w(LOG_TAG, "adding request is failed, request: " + req.toDebugInfo());
        }
    }

    /**
     * get SyncAutomatically status
     * 
     * @param context context
     * @return true: SyncAutomatically, false: not SyncAutomatically
     * this funtion require permission 'android.permission.READ_SYNC_SETTINGS'.
     */
    @SuppressLint("MissingPermission")
    public static boolean isSyncAutomatically(Context context) {
        // update from top request: only refer to weather account state, not master sync state
        // get weather account
        Account acc = getWeatherSyncAccount(context);
        if (acc != null) {
            try {
                return ContentResolver.getSyncAutomatically(acc, WeatherConsts.ACCOUNT_AUTHORITY);
            } catch (Exception e) {
                Log.d(LOG_TAG, "error to read account " + e.getMessage());
            }
        }
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, using shared preference
            return context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).getInt(WeatherConsts.SETTING_KEY_AUTO_SYNC_SWITCH, 1) != 0;
        }

        int value = Settings.System.getInt(context.getContentResolver(),
                WeatherConsts.SETTING_KEY_AUTO_SYNC_SWITCH, 1);
        Log.d(LOG_TAG, "Weather sync is " + (value != 0 ? "On" : "Off"));
        return (value != 0);
    }

    /**
     * send broadcast without exception
     * 
     * @param context context
     * @param Intent intent
     * @param String permission
     *
     */
    private static void sendBroadcast(Context context, Intent intent, String permission) {
        try {
            context.sendBroadcast(intent, permission);
        } catch (Exception e) {
            Log.d(LOG_TAG, "sendBroadcast failed, " + e.getMessage());
        }
    }
    /**
     * set Sync Automatically
     * 
     * @param context context
     * @param isSyncAutomatically true: SyncAutomatically, false: not
     *            SyncAutomatically
     * this funtion require permission 'android.permission.WRITE_SYNC_SETTINGS'.
     */
    @SuppressLint("MissingPermission")
    public static void setSyncAutomatically(Context context, boolean isSyncAutomatically) {
        Account acc = getWeatherSyncAccount(context);
        if (acc != null) {
            try {
                ContentResolver.setSyncAutomatically(acc, WeatherConsts.ACCOUNT_AUTHORITY,
                        isSyncAutomatically);
            } catch (Exception e) {
                Log.d(LOG_TAG, "error to modify account " + e.getMessage());
            }
        }

        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, using shared preference
            context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).edit().putInt(WeatherConsts.SETTING_KEY_AUTO_SYNC_SWITCH, isSyncAutomatically ? 1 : 0).apply();

        } else {
            try {
                Settings.System.putInt(context.getContentResolver(),
                    WeatherConsts.SETTING_KEY_AUTO_SYNC_SWITCH, isSyncAutomatically ? 1 : 0);
            }catch (Exception e) {}
            Log.d(LOG_TAG, "Set Weather sync " + (isSyncAutomatically ? "On" : "Off"));
        }

        // broadcast an intent to notify WSP receiver
        Intent intent = new Intent(WeatherConsts.WSP_UTILITY_NOTIFICATION_INTENT_ACTION_NAME);
        intent.addCategory(WSP_UTILITY_FUNC_SET_SYNC_AUTOMATICALLY);
        //Update for Android O broadcast policy
        sendBroadcast(context, intent, android.os.Build.VERSION.SDK_INT >= 26 ? WeatherConsts.PERMISSION_HSP : null);
    }

    /**
     * Get Weather Auto Sync Frequency
     * 
     * @param context context
     * @return long Weather Auto Sync Frequency
     */
    public static long getAutoSyncFrequency(Context context) {
        long ret;

        try {
            if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
                // Prism non-Htc, using shared preference
                ret = Long.parseLong(context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).getString(WeatherConsts.SETTING_KEY_AUTO_SYNC_FREQUENCY, String.valueOf(DEFAULT_AUTO_SYNC_FREQUENCY)));

            } else {
                ret = Long.parseLong(Settings.System.getString(context.getContentResolver(),
                        WeatherConsts.SETTING_KEY_AUTO_SYNC_FREQUENCY));
            }
        } catch (NumberFormatException e) {
            setDefaultAutoSyncFrequency(context);
            ret = DEFAULT_AUTO_SYNC_FREQUENCY;
        }

        return ret;
    }
    
    /**
     * get Update when open
     * 
     * @param context context
     * @param boolean updateWhenOpen
     */
    public static boolean getUpdateWhenOpen(Context context) {
        int updateWhenOpen = 0;
		try {
            if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
                // Prism non-Htc, using shared preference
                updateWhenOpen = context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).getInt(WeatherConsts.SETTING_KEY_UPDATE_WHENOPEN, 0);

            } else {
                updateWhenOpen = Settings.System.getInt(context.getContentResolver(), WeatherConsts.SETTING_KEY_UPDATE_WHENOPEN);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return updateWhenOpen == 1 ? true : false;
    }
    
    /**
     * check is WSPCurrentLocationFlag On or not
     * 
     * @param context context
     * @return boolean true:on, false: off
     */
    public static boolean isWSPCurrentLocationFlagOn(Context context) {
        String flag = WeatherUtility.loadSetting(context.getContentResolver(),
                WSP_SETTING_APP_NAME, WSP_FLAG_CUR_LOC_KEY);
        if (flag == null) {
            flag = WSP_FLAG_CUR_LOC_OFF;
        }
        return flag.equals(WSP_FLAG_CUR_LOC_ON);
    }
    
    /**
     * turn Off WSP CurrentLocation Flag
     * 
     * @param context context
     */
    public static void turnOffWSPCurrentLocationFlag(Context context) {
        WeatherUtility.insertOrUpdateSetting(context.getContentResolver(), WSP_SETTING_APP_NAME,
                WSP_FLAG_CUR_LOC_KEY, WSP_FLAG_CUR_LOC_OFF);
    }
    
    /**
     * broadcast weather Data Intent
     * @param context context
     * @param weather data bundle
     */
    public static void broadcastDataIntent(Context context, Bundle bundle) {
    	String categoryName = null;
    	if(bundle == null) {
    		Log.d(LOG_TAG, "bundle is null");
    		return;
    	}
    	categoryName = bundle.getString(WeatherConsts.KEY_OUT_CATEGORYNAME);
    	if(categoryName == null) {
    		Log.d(LOG_TAG, "categoryName is null");
    		return;
    	}
        //data.checkMaxAvailableIndex(); // check data Available index, before broadcasting data.
        //String categoryName = data.toString();
        Intent intent = new Intent(WeatherConsts.SYNC_SERVICE_RESULT_INTENT_ACTION_NAME);
        intent.addCategory(categoryName);
        intent.putExtra(WeatherConsts.SYNC_SERVICE_RESULT_INTENT_EXTRA_DATA, bundle);
        sendBroadcast(context, intent, WeatherConsts.PERMISSION_HSP);
        if (LOG_FLAG) Log.i(LOG_TAG, "broadcast data intent, category: " + categoryName /*+ ", WSPPData: " + data.toDebugInfo()*/); // turn off log, it's for dev

        /**Broadcast Intent to trigger Remote Widget[WeatherWidget, Clock Widget] update weather data**/
        Intent trigger_widget = new Intent(TRIGGER_WEATHER_WIDGET_UPDATE_DATA);
        trigger_widget.addCategory(tri_category);
        trigger_widget.putExtra(WeatherConsts.SYNC_SERVICE_RESULT_INTENT_EXTRA_DATA, categoryName);
        sendBroadcast(context, trigger_widget, WeatherConsts.PERMISSION_HSP);
        if (LOG_FLAG) Log.i(LOG_TAG, "broadcast trigger widget intent, category: " + tri_category + ", Extra: " + categoryName);
    }
    
    /**
     * method to check if user has agreed weather location and data usage, and
     * launch dialog box if needed.
     * 
     * @param context
     * @return state true if activity launched.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean checkOrLaunchUserAgreeDialog(Context context) {
        // query current state
        ContentProviderClient unstableClient = null;
        try {
            unstableClient = context.getContentResolver().acquireUnstableContentProviderClient(
                    WeatherConsts.URI_DATA);
            Bundle b = unstableClient.call("user_agreement_state", null, null);
            if (b != null) {
                boolean state = b.getBoolean("user_answer", false);
                Log.d(LOG_TAG, "user agreement state: " + state);

                if (state) {
                    // already agreed
                    return false;
                }
            } else {
                Log.d(LOG_TAG, "got null bundle, not support");

                return false;
            }
        } catch (Exception e) {
            if (LOG_FLAG) {
                Log.w(LOG_TAG, "checkOrLaunchUserAgreeDialog() - Catch Exception: ", e);
            }
        } finally {
            if (unstableClient != null) {
                unstableClient.release();
            }
        }

        // launch user agree dialog
        Log.d(LOG_TAG, "launch user agree activity");
        Intent launch = new Intent(INTENT_ACTION_LAUNCH_USER_AGREE_DIALOG);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launch.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        launch.setPackage(WeatherConsts.TARGET_PACKAGE_NAME);
        try {
            context.startActivity(launch);
        } catch (Exception e) {
            Log.d(LOG_TAG, "launch user agree activity failed");
            return false;
        }

        return true;
    }
    
    /**
     * trigger SyncService to sync Weather data
     * 
     * @param context context
     * @param resultIntentCategoryName result Intent Category Name
     * @param requests weather request message
     * @param triggerSource trigger sync service type
     */
    public static void triggerSyncService(Context context, String resultIntentCategoryName,
            WeatherRequest[] requests, int triggerSource) {

        if (WeatherConsts.SYNC_SERVICE_TRIGGER_SOURCE_FORCE_UPDATE == triggerSource
                && (requests == null || requests.length < 1)) {
            // do nothing, but trigger cur loc to update
        } else {
            Intent intent = new Intent(SYNC_SERVICE_TRIGGER_INTENT_ACTION_NAME);

            if (resultIntentCategoryName != null) {
                intent.putExtra(WeatherConsts.SYNC_SERVICE_TRIGGER_INTENT_EXTRA_CATEGORY_NAME,
                        resultIntentCategoryName);
            }

            if (requests != null) {
                intent.putExtra(WeatherConsts.SYNC_SERVICE_TRIGGER_INTENT_EXTRA_REQUESTS, requests);
            }

            intent.putExtra(WeatherConsts.SYNC_SERVICE_TRIGGER_INTENT_EXTRA_SOURCE, triggerSource);
            // L: start service should be explicit
            intent.setPackage(WeatherConsts.TARGET_PACKAGE_NAME);

            try {
                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    intent.setAction("com.htc.sync.provider.weather.TRIGGER_SYNC_SERVICE");
                    sendBroadcast(context, intent, WeatherConsts.PERMISSION_HSP);
                } else {
                    context.startService(intent);
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, "triggerSyncService failed, " + e, e);
            }
        }

        // Trigger HtcLocationService to update current location
        //
        // !! remove this when HtcLocationService has Account !!

        // trigger to get location only when force update
        // if (SYNC_SERVICE_TRIGGER_SOURCE_FORCE_UPDATE == triggerSource ||
        // SYNC_SERVICE_TRIGGER_SOURCE_AUTO_SYNC == triggerSource) {
        if (WeatherConsts.SYNC_SERVICE_TRIGGER_SOURCE_FORCE_UPDATE == triggerSource) {
            Intent htcLocationServiceIntent = new Intent("com.htc.app.autosetting.location");
            sendBroadcast(context, htcLocationServiceIntent, android.os.Build.VERSION.SDK_INT >= 26 ? WeatherConsts.PERMISSION_HSP : null);
        }
    }
    
    /** sync service **/
    /**
     * Trigger SyncService to sync Weather data [ This function is for App to
     * force update]
     * 
     * @param context context
     * @param resultIntentCategoryName result Intent Category Name
     * @param requests weather request message
     */
    public static void triggerSyncService(Context context, String resultIntentCategoryName,
    		WeatherRequest[] requests) {
        // it is ok if requests is null or empty. (for trigger cur loc)

        if (resultIntentCategoryName != null && resultIntentCategoryName.length() < 1) {
            throw new IllegalArgumentException("length of category name must > 1");
        }

        ArrayList<WeatherRequest> checkedRequests = new ArrayList<WeatherRequest>();

        // query cur loc info from db when type is current locaion
        if (requests != null) {
            int totalRequests = requests.length;
            for (int i = 0; i < totalRequests; i++) {
                if (requests[i].getType() == WeatherConsts.TYPE_CURRENT_LOCATION) {
                	WeatherRequest curReq = WeatherRequest.generateWeatherRequestForCurrentLocationWithCurCacheData(context);
                    if (curReq == null) {
                        if (LOG_FLAG)
                            Log.w(LOG_TAG,
                                    "Force update cur loc, but there is no cur loc in db. Maybe the cur loc in db was deleted because of a new cur loc.");
                        continue;
                    }
                    checkedRequests.add(curReq);
                } else {
                    checkedRequests.add(requests[i]);
                }
            }
        }

        if (LOG_FLAG)
            Log.d(LOG_TAG, "EVENT - FORCE UPDATE, total reqs: "
                    + (requests == null ? 0 : requests.length) + ", accepted reqs: "
                    + checkedRequests.size());

        triggerSyncService(context, resultIntentCategoryName,
                checkedRequests.toArray(new WeatherRequest[0]),
                WeatherConsts.SYNC_SERVICE_TRIGGER_SOURCE_FORCE_UPDATE);

    }

    private static Boolean sbDeviceChecked = false;
    private static Boolean sbDeviceSupport = false;
    private static boolean checkDeviceModeSupportedFromProvider(Context context) {
        if (sbDeviceChecked) return sbDeviceSupport;

        Boolean isSupport = false;
        try {
            //We enable device only mode since Android N, and we have move WeatherSyncProvider from HSP to Weather APP since Android N.
            String packageName = WeatherConsts.TARGET_PACKAGE_NAME;
            ApplicationInfo ai = context.getPackageManager()
                .getApplicationInfo(packageName, PackageManager.GET_META_DATA);

            Bundle metaData = ai.metaData;

            Object myApiValue = metaData.get("weather.device_only_mode");

            if (myApiValue instanceof Boolean) {
                isSupport = (Boolean) myApiValue;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "EVENT - checkDeviceModeSupportedFromProvider() failed " + e);
        }
        Log.d(LOG_TAG, "EVENT - checkDeviceModeSupportedFromProvider() " + isSupport);
        sbDeviceSupport = isSupport;
        sbDeviceChecked = true;

        return sbDeviceSupport;
    }
    /**
     * Get DeviceOnly mode supported or not
     *
     * @param context context
     * @return boolean
     */
    public static boolean isDeviceModeSupported(Context context) {
        if (android.os.Build.VERSION.SDK_INT > 23) {
            return checkDeviceModeSupportedFromProvider(context);
        } else {
            return false;
        }
    }
    /**
     * Get Weather DeviceOnly Error code
     *
     * @param context context
     * @return int
     */
    public static int getDevicModeSatausCode(Context context) {
        if (isDeviceModeSupported(context) == false) {
            //GPS_ERROR_CODE_NOT_SUPPORT:1
            if (LOG_FLAG) {
                Log.d(LOG_TAG, "EVENT - getDevicModeFailedCode() GPS_ERROR_CODE_NOT_SUPPORT");
            }
            return WeatherConsts.GPS_ERROR_CODE_NOT_SUPPORT;
        }
        int locationMode = Settings.Secure.LOCATION_MODE_OFF;
        int code = WeatherConsts.GPS_ERROR_CODE_NONE;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF);
            if (LOG_FLAG) {
                Log.d(LOG_TAG, "EVENT - getDevicModeFailedCode() LOCATION_MODE =  " + locationMode);
            }
        } catch (Exception e) {

        }

        if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {
            //GPS_ERROR_CODE_LOCATION_SERVICE_OFF:3
            Log.d(LOG_TAG, "EVENT - getDevicModeFailedCode() GPS_ERROR_CODE_LOCATION_SERVICE_OFF");
            return WeatherConsts.GPS_ERROR_CODE_LOCATION_SERVICE_OFF;
        }

        try {
            code = Settings.System.getInt(context.getContentResolver(), SETTING_KEY_GPS_ERROR_CODE, WeatherConsts.GPS_ERROR_CODE_NONE) ;

        } catch (Exception e) {

        }

        if (LOG_FLAG) {
            Log.d(LOG_TAG, "EVENT - getDevicModeFailedCode() code =  " + code);
        }
        return code;
    }

    /**
     * Set Weather DeviceOnly Error code
     *
     * @param context context
     * @param int
     */
    public static void setDevicModeStatusCode(Context context, int val) {
        if (LOG_FLAG) {
            Log.d(LOG_TAG, "EVENT - setDevicModeFailedCode(" + val + ")");
        }
        try {
            Settings.System.putInt(context.getContentResolver(), SETTING_KEY_GPS_ERROR_CODE, val) ;
        } catch (Exception e) {

        }
    }
    /**
     * To check if WeatherSyncProvider is installed or not
     * 
     * @param cr ContentResolver
     * @return true if WeatherSyncProvider is installed
     * 
     * @hide
     */
    protected static boolean isWeatherSyncProviderInstalled(ContentResolver cr) {
        ContentProviderClient client = cr.acquireUnstableContentProviderClient(WeatherConsts.URI_DATA);
        if (client != null) {
            client.release();

            return true;
        }

        Log.e(LOG_TAG, "WeatherSyncProvider is not installed!");
        return false;
    }
    
    /**
     * trim Latitude length
     * 
     * @param latitude latitude
     * @return latitude
     */
    public static String trimLatitude(String latitude) {
        String[] tmp = latitude.split("[.]");
        if (tmp.length != 2) {
            return latitude;
        }
        if (tmp[1].length() > 3) {
            tmp[1] = tmp[1].substring(0, 3);
        }
        return tmp[0] + "." + tmp[1];
    }
    
    private static boolean _hasRequested(Context context, final String where) {
        if (context == null) {
            return false;
        }

        // check if WeatherSyncProvider is installed and permission is valid
        if (!isWeatherSyncProviderInstalled(context.getContentResolver())) {
            return false;
        }

        boolean ret = false;

        Cursor cursor = null;
        ContentProviderClient unstableContentProvider = null;
        try {
            unstableContentProvider = context.getContentResolver()
                    .acquireUnstableContentProviderClient(WeatherConsts.URI_DATA);
            cursor = unstableContentProvider.query(WeatherConsts.URI_DATA, new String[] {
            		WeatherConsts.TABLE_DATA_COLUMNS._id.name()
            }, where, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                ret = true;
            }
        } catch (Exception e) {
            if (LOG_FLAG) {
                Log.w(LOG_TAG, "_hasRequested() - Catch Exception: ", e);
            }
            ret = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // release ContentProviderClient
            if (unstableContentProvider != null) {
                unstableContentProvider.release();
            }
        }

        return ret;
    }
    
    /**
     * get Weather Sync Account
     * 
     * @param context context
     * @return Account weather account
     * this funtion require permission 'android.permission.GET_ACCOUNTS'.
     */
    @SuppressLint("MissingPermission")
    public static Account getWeatherSyncAccount(Context context) {
        Account acc = null;

        try {
            Account[] accs = AccountManager.get(context).getAccountsByType(
                    WeatherConsts.ACCOUNT_TYPE);

            if (accs != null && accs.length == 1) {
                acc = accs[0];
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "error to get account: " + e.getMessage());
        }

        return acc;
    }
    
    /**
     * set Default Auto Sync Frequency
     * 
     * @param context context
     */
    public static void setDefaultAutoSyncFrequency(Context context) {
        Log.w(LOG_TAG, "no customization data - auto sync frequency, set default value");
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, using shared preference
            context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).edit().putString(WeatherConsts.SETTING_KEY_AUTO_SYNC_FREQUENCY, String.valueOf(DEFAULT_AUTO_SYNC_FREQUENCY)).apply();

        } else {
            try {
                Settings.System.putString(context.getContentResolver(), WeatherConsts.SETTING_KEY_AUTO_SYNC_FREQUENCY, ""
                    + DEFAULT_AUTO_SYNC_FREQUENCY);
            } catch (Exception e) {}
        }
    }

    /**
     * set Auto Sync Frequency
     *
     * @param context context
     * @param long syncFrequency
     */
    public static void setAutoSyncFrequency(Context context,long syncFrequency) {
    	if (LOG_FLAG)
        Log.d(LOG_TAG, "auto sync frequency, set syncFrequency:" + syncFrequency);
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, using shared preference
            context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).edit().putString(WeatherConsts.SETTING_KEY_AUTO_SYNC_FREQUENCY, String.valueOf(syncFrequency)).apply();

        } else {
            try {
                Settings.System.putString(context.getContentResolver(), WeatherConsts.SETTING_KEY_AUTO_SYNC_FREQUENCY, ""
                    + syncFrequency);
            } catch (Exception e) {}
        }
    }

    /**
     * set Update when open
     * 
     * @param context context
     * @param boolean updateWhenOpen
     */
    public static void setUpdateWhenOpen(Context context,boolean updateWhenOpen) {
    	if (LOG_FLAG)
        Log.d(LOG_TAG, "setUpdateWhenOpen:"+updateWhenOpen);
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, using shared preference
            context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).edit().putInt(WeatherConsts.SETTING_KEY_UPDATE_WHENOPEN, updateWhenOpen ? 1 : 0).apply();

        } else {
            try {
                Settings.System.putInt(context.getContentResolver(), WeatherConsts.SETTING_KEY_UPDATE_WHENOPEN, updateWhenOpen ? 1 : 0);
            } catch (Exception e) {}
        }
    }

	/**
	 * regulate the input string 
	 * @param str input search string 
	 * @return String result of search string
	 */
	private static String saftSearchCharacters(String str) {
		return str.replaceAll("'", "''").trim();
	}
	
	/********************************************************************
	 * Setting
	 ********************************************************************/
	
	/**
	 * Load setting
	 * @param cr ContentResolver
	 * @param app application name
	 * @param key key
	 * @return value
	 * 
	 * @deprecated [Not use any longer]
	 */
	/**@hide*/ 
    private static String loadSetting(ContentResolver cr, String app, String key) {
        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return null;
        }

        String value = null;
        Cursor cursor = null;

        ContentProviderClient unstableContentProvider = null;
        try {
            unstableContentProvider = cr.acquireUnstableContentProviderClient(Uri.withAppendedPath(
                    WeatherConsts.CONTENT_URI, WeatherConsts.SETTING_PATH));
            cursor = unstableContentProvider.query(
                    Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.SETTING_PATH),
                    new String[] {
                        WeatherConsts.SETTING_COLUMN_NAME.value.name()
                    }, WeatherConsts.SETTING_COLUMN_NAME.app.name() + "='" + app + "' and "
                            + WeatherConsts.SETTING_COLUMN_NAME.key.name() + "='" + key + "'",
                    null, null);
            if (cursor != null && cursor.getCount() == 1 && cursor.moveToFirst()) {
                value = cursor.getString(0);
            }
        } catch (Exception e) {
            if (LOG_FLAG) {
                Log.w(LOG_TAG, "loadSetting() - Catch Exception: ", e);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // release ContentProviderClient
            if (unstableContentProvider != null) {
                unstableContentProvider.release();
            }
        }
        return value;
    }
    
    /**
	 * load Multi-App Locations information Filter By App name
	 * @param cr ContentResolver
	 * @param apps AppLocations name
	 * @return WeatherLocation
	 */
	public static WeatherLocation[] loadMultiAppLocationsFilterByApp(ContentResolver cr, String[] apps) {
		ArrayList<WeatherLocation> locations = new ArrayList<WeatherLocation>();
		
        apps = replaceClockApp(apps);
		if (apps != null && apps.length > 0) {
			for (String app : apps) {
				locations.addAll(Arrays.asList(loadMultiAppLocations(cr, new String[] {app})));
			}
		}
		
		return locations.toArray(new WeatherLocation[0]);
	}
	
	/**
	 * Load multi-apps locations information 
	 * @param cr ContentResolver
	 * @param apps apps
	 * @return Weather locations
	 */
	public static WeatherLocation[] loadMultiAppLocations(ContentResolver cr, String[] apps) {
		ArrayList<WeatherLocation> locations = new ArrayList<WeatherLocation>();
		HashSet<String> loadedLocation = new HashSet<String>();
		
        apps = replaceClockApp(apps);
		if (apps != null && apps.length > 0) {
			for (String app : apps) {
				for (WeatherLocation wl : loadLocations(cr, app)) {
					String k = wl.getName() + ";" + wl.getState() + ";" + wl.getCountry();
					if (loadedLocation.contains(k)) {
						continue;
					}
					loadedLocation.add(k);
					locations.add(wl);
				}
			}
		}
		
		return locations.toArray(new WeatherLocation[0]);
	}
	
	/**
	 * Insert/Update settings
	 * @param cr ContentResolver
	 * @param app application name
	 * @param key key
	 * @param value value
	 * 
	 * @deprecated [Module internal use]
	 */
    /**@hide*/ 
	private static void insertOrUpdateSetting(ContentResolver cr, String app, String key, String value) {
        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return;
        }

		// delete
		cr.delete(
			URI_SETTING, 
			WeatherConsts.SETTING_COLUMN_NAME.app.name() + "='" + app + "' and " +
			WeatherConsts.SETTING_COLUMN_NAME.key.name() + "='" + key + "'", 
			null);
		
		// insert
		ContentValues values = new ContentValues();
        values.put(WeatherConsts.SETTING_COLUMN_NAME.app.name(), app);
        values.put(WeatherConsts.SETTING_COLUMN_NAME.key.name(), key);
        values.put(WeatherConsts.SETTING_COLUMN_NAME.value.name(), value);		
		cr.insert(URI_SETTING, values);
		
		cr.notifyChange(Uri.withAppendedPath(URI_SETTING, app), null);
	}
	
	/**
     * get Temperature Unit
     * 
     * @param context context
     * @return String temperature Unit
     */
    public static String getTemperatureUnit(Context context) {
        String unit = null;
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, using shared preference
            unit = context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).getString(WeatherConsts.SETTING_KEY_TEMPERATURE_UNIT, null);
        } else {
            unit = Settings.System.getString(context.getContentResolver(),WeatherConsts.SETTING_KEY_TEMPERATURE_UNIT);
        }

        if (unit == null || unit.length() == 0) {
            setDefaultTemperatureUnit(context);
            unit = DEFAULT_TEMPERATURE_UNIT;
        }

        return unit;
    }
    
    /**
     * set Default Temperature Unit
     * 
     * @param context context
     */
    public static void setDefaultTemperatureUnit(Context context) {
        Log.w(LOG_TAG, "no customization data - temperature unit, set default value");
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, using shared preference
            context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).edit().putString(WeatherConsts.SETTING_KEY_TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT).apply();

        } else {
            try {
                Settings.System.putString(context.getContentResolver(), WeatherConsts.SETTING_KEY_TEMPERATURE_UNIT,
                    DEFAULT_TEMPERATURE_UNIT);
            }catch (Exception e) {
            }
        }
    }

    /**
     * set  Temperature Unit
     *
     * @param context context
     */
    public static void setTemperatureUnit(Context context,String unit) {
    	if(LOG_FLAG)
        Log.d(LOG_TAG, "setTemperatureUnit:"+unit);
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, using shared preference
            context.getSharedPreferences(WeatherConsts.PRISM_PREF_NAME, Context.MODE_PRIVATE).edit().putString(WeatherConsts.SETTING_KEY_TEMPERATURE_UNIT, unit).apply();

        } else {
            try {
                Settings.System.putString(context.getContentResolver(), WeatherConsts.SETTING_KEY_TEMPERATURE_UNIT, unit);
            }catch (Exception e) {
            }
        }
    }

	/********************************************************************
	 * Location list
	 ********************************************************************/

    /**
	 * Hide Automatically by SDK Team [U12000]</br><br>
	 *
	 * Get location list
	 * @param cr ContentResolver
	 * @param sortOrder order by
	 * @return location list
	 * 
	 * @hide
	 */
	private static Cursor getLocationList(ContentResolver cr, String sortOrder) {
		return getLocationList(cr, sortOrder, -1);
	}
	
	/**
	 * Hide Automatically by SDK Team [U12000]</br></br>
	 * 
	 * Get location list with limit number
	 * @param cr ContentResolver
	 * @param sortOrder order by
	 * @param limit limit
	 * @return location list
	 * @hide
	 */
	private static Cursor getLocationList(ContentResolver cr, String sortOrder, int limit) {
        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return null;
        }

		Uri uri = URI_LOCATION_LIST;
		
		if (limit > -1) { 
			uri = Uri.withAppendedPath(uri, "" + limit);
		}
		
		return cr.query(
				uri, 
				null,
				null, 
				null, 
				sortOrder);	
	}
	
    /**
	 * Get location list by characters
	 * if searchCharacters = "a:b:c", then you will get location list with SEARCH_TYPE is:
	 *     (1) SEARCH_TYPE.START_WITH, abc% from SEARCH_COLUMN.*  
	 *     (2) SEARCH_TYPE.END_WITH, %abc from SEARCH_COLUMN.*
	 *     (3) SEARCH_TYPE.CONTAIN, %abc% from SEARCH_COLUMN.*
	 * , where % can be any character. 
	 * @param cr ContentResolver
	 * @param sortOrder order by
	 * @param searchCharacters characters, use ":" to split characters
	 * @param searchColumn search which column: SEARCH_COLUMN.CITY, SEARCH_COLUMN.COUNTRY, SEARCH_COLUMN.CITY_AND_COUNTRY
	 * @param searchType search type: SEARCH_TYPE.START_WITH, SEARCH_TYPE.END_WITH, SEARCH_TYPE.CONTAIN
	 * @return location list
	 */
	public static Cursor getLocaitonList(ContentResolver cr, String sortOrder, String searchCharacters, SEARCH_COLUMN searchColumn, SEARCH_TYPE searchType) {
        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return null;
        }

		if (searchCharacters != null) {
			if (isContainIllegalCharacters(null, searchCharacters)) {
				return generateEmptyResult(cr);
			}
		}
		
		String where = null;
		
		if (searchCharacters != null && !searchCharacters.equals("")) {
			searchCharacters = saftSearchCharacters(searchCharacters);
			
			//transfer to lower case
			searchCharacters = transformLowerCase(searchCharacters);

			// generate condition : %abc, abc% or %abc%
			String condition = null;
			if (SEARCH_TYPE.START_WITH == searchType) {
				condition = "'" + searchCharacters + "%'";
			} else if (SEARCH_TYPE.END_WITH == searchType) {
				condition = "'%" + searchCharacters + "'";
			} else if (SEARCH_TYPE.CONTAIN == searchType) {
				condition = "'%" + searchCharacters + "%'";
			} else if (SEARCH_TYPE.MATCH_IGONE_CASE == searchType) {
				condition = "'" + searchCharacters + "'";
			}
			
			// generate where 
			if (condition != null && !condition.equals("")) {
				if (SEARCH_COLUMN.CITY == searchColumn) {
					where = WeatherConsts.LOCATION_LIST_COLUMN_NAME.name.name() + " like " + condition;
				} else if (SEARCH_COLUMN.COUNTRY == searchColumn) {
					where = WeatherConsts.LOCATION_LIST_COLUMN_NAME.country.name() + " like " + condition;
				} else if (SEARCH_COLUMN.CITY_AND_COUNTRY == searchColumn) {
					where = WeatherConsts.LOCATION_LIST_COLUMN_NAME.name.name() + " like " + condition + " OR " +
					WeatherConsts.LOCATION_LIST_COLUMN_NAME.country.name() + " like " + condition;
				} else if (SEARCH_COLUMN.STATE == searchColumn) {
					where = WeatherConsts.LOCATION_LIST_COLUMN_NAME.state.name() + " like " + condition;
					
				} else if (SEARCH_COLUMN.CITY_STATE_AND_COUNTRY == searchColumn) {
					where = WeatherConsts.LOCATION_LIST_COLUMN_NAME.name.name() + " like " + condition + " OR " +
					WeatherConsts.LOCATION_LIST_COLUMN_NAME.state.name() + " like " + condition + " OR " +
					WeatherConsts.LOCATION_LIST_COLUMN_NAME.country.name() + " like " + condition;
				}
			}
		}
		
		return cr.query(
			Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_LIST_PATH), 
			null,
			where, 
			null, 
			sortOrder);	
	}
	
    /**
	 * Get location list by code
	 * @param cr ContentResolver
	 * @param code location code
	 * @return location
	 */
	public static Cursor getLocationListByCode(ContentResolver cr, String code) {
        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return null;
        }

		String where = WeatherConsts.LOCATION_LIST_COLUMN_NAME.code.name() + "='" + saftSearchCharacters(code) + "'";
		return cr.query(
			Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_LIST_PATH),
			null,
			where, 
			null,
			null);
	}

	/**
	 * Get location list by code[Data Type : WeatherLocation][Using acquireUnstableContentProviderClient() to query data.]
	 * @param cr ContentResolver
	 * @param code city
	 * @return WeatherLocation LocationList information
	 */
    public static WeatherLocation getLocationListByCodeUnstable(ContentResolver cr, String code)
    {
        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return null;
        }

        WeatherLocation weatherLocation = null;
        //Condition
        String where = WeatherConsts.LOCATION_LIST_COLUMN_NAME.code.name() + "='" + saftSearchCharacters(code) + "'";
        //Provider Uri
        Uri uri = Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_LIST_PATH);
        Cursor cursor = null;
        ContentProviderClient unstableContentProvider = cr.acquireUnstableContentProviderClient(uri);

        try {
            cursor = unstableContentProvider.query(uri, null, where, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 1 && cursor.moveToFirst()) {
                    weatherLocation = WeatherUtility.CursorToWeatherLocation(cursor);
                } else {
                    if(LOG_FLAG)
                        Log.w(LOG_TAG, (cursor.getCount() == 0 ? "no match data" : "data is incorrect") 
                                + ", cursor.getCount(): " + cursor.getCount());
                }
            } else {
                if(LOG_FLAG)
                    Log.w(LOG_TAG,"cursor is null");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if(LOG_FLAG){
                Log.d(LOG_TAG, "getLocationListByCodeUnstable() - Catch Exception");
                e.printStackTrace();
            }
            return null;
        } finally{
            //Close cursor
            if(cursor != null)
                cursor.close();
            //release ContentProviderClient
            if(unstableContentProvider != null)
                unstableContentProvider.release();
        }
        return weatherLocation;
    }

	
	/********************************************************************
	 * Common
	 ********************************************************************/	
	
	/**
	 * Convert cursor to WeatherLocation (for Location_List and Location table use)
	 * @param cursor cursor
	 * @return Weather location
	 */
	public static WeatherLocation CursorToWeatherLocation(Cursor cursor) {
		WeatherLocation w = new WeatherLocation();		
		int columnIndex = -1;
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME._id.name())) > -1) {
			w.setId(cursor.getInt(columnIndex));
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.type.name())) > -1) {
			w.setCustomLocation(cursor.getInt(columnIndex) == WeatherConsts.LOCATION_TYPE_CUSTOM);
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.code.name())) > -1) {
			w.setCode(cursor.getString(columnIndex));
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.name.name())) > -1) {
			w.setName(cursor.getString(columnIndex));
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.state.name())) > -1) {
			w.setState(cursor.getString(columnIndex));
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.country.name())) > -1) {
			w.setCountry(cursor.getString(columnIndex));
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.latitude.name())) > -1) {
			w.setLatitude(cursor.getString(columnIndex));
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.longitude.name())) > -1) {
			w.setLongitude(cursor.getString(columnIndex));
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.timezone.name())) > -1) {
			w.setTimezone(cursor.getString(columnIndex));
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.timezoneId.name())) > -1) {
			w.setTimezoneId(cursor.getString(columnIndex));
		}
		
		if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.app.name())) > -1) {
			w.setApp(cursor.getString(columnIndex));
		}		
		
		return w;
	}
	
	/**
	 * Convert Cursor To WeatherLocation For WorldClock
	 * @param cursor cursor
	 * @return Weather location For WorldClock
	 * 
	 * @deprecated [Alternative solution]
	 * Please use {@link #CursorToWeatherLocation(Cursor)}
	 */
	/**@hide*/ 
    private static WeatherLocation CursorToWeatherLocationForWorldClock(Cursor cursor) {
        WeatherLocation w = new WeatherLocation();      
        int columnIndex = -1;
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME._id.name())) > -1) {
            w.setId(cursor.getInt(columnIndex));
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.type.name())) > -1) {
            w.setCustomLocation(cursor.getInt(columnIndex) == WeatherConsts.LOCATION_TYPE_CUSTOM);
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.code.name())) > -1) {
            w.setCode(cursor.getString(columnIndex));
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.name.name())) > -1) {
            w.setName(cursor.getString(columnIndex));
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.state.name())) > -1) {
            w.setState(cursor.getString(columnIndex));
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.country.name())) > -1) {
            w.setCountry(cursor.getString(columnIndex));
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.latitude.name())) > -1) {
            w.setLatitude(cursor.getString(columnIndex));
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.longitude.name())) > -1) {
            w.setLongitude(cursor.getString(columnIndex));
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.timezone.name())) > -1) {
            w.setTimezone(cursor.getString(columnIndex));
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.timezoneId.name())) > -1) {
            w.setTimezoneId(cursor.getString(columnIndex));
        }
        
        if ((columnIndex = cursor.getColumnIndex(WeatherConsts.LOCATION_COLUMN_NAME.app.name())) > -1) {
            w.setApp(cursor.getString(columnIndex));
        }       
        
        return w;
    }
	
	/**
	 * Load location setting
	 * @param cr ContentResolver
	 * @param app Application name
	 * @return Weather locations
	 */
	public static WeatherLocation[] loadLocations(ContentResolver cr, String app) {
        app = replaceClockApp(app);
	    return _loadLocations(cr, app, false);
	}
	
	
	/**
	 * load Locations by app name
	 * @param cr ContentResolver
	 * @param app app name
	 * @param worldClockInfo: true : CursorToWeatherLocationForWorldClock , false: Cursor To WeatherLocation
	 * @return WeatherLocation list
	 */
	private static WeatherLocation[] _loadLocations(ContentResolver cr, String app, boolean worldClockInfo) {
		ArrayList<WeatherLocation> locations = new ArrayList<WeatherLocation>();

        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return locations.toArray(new WeatherLocation[0]);
        }

		Cursor cursor = null;
		Cursor locationListCursor = null;
		Uri uri = Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_PATH);
		ContentProviderClient unstableContentProvider = cr.acquireUnstableContentProviderClient(uri);
		
		try {
			cursor = unstableContentProvider.query(
			    uri, 
				null, 
				WeatherConsts.LOCATION_COLUMN_NAME.app.name() + "='" + app + "'", 
				null, 
				WeatherConsts.LOCATION_COLUMN_NAME._id.name());

			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {		
				do {
					WeatherLocation weatherLocation = (worldClockInfo) ? CursorToWeatherLocationForWorldClock(cursor) : CursorToWeatherLocation(cursor);
					
					String code = weatherLocation.getCode();
					if (code != null && !code.equals("")) {
						// since the URI is the same, we pass in unstable content provider client to query
						locationListCursor = getLocationListByCodeForLanguageTransfer(unstableContentProvider, code);
						if (locationListCursor != null && locationListCursor.getCount() > 0 && locationListCursor.moveToFirst()) {
							weatherLocation.setName(locationListCursor.getString(0));
							weatherLocation.setState(locationListCursor.getString(1));
							weatherLocation.setCountry(locationListCursor.getString(2));
						}
						if (locationListCursor != null) {
							locationListCursor.close();
						}
					}
					
					locations.add(weatherLocation);
				} while (cursor.moveToNext());
			}
		
		} catch (Exception e) {
		    if(LOG_FLAG)
		        Log.w(LOG_TAG, "_loadLocations exception was caught: " + e.getMessage(), e);
		} finally {
			if (locationListCursor != null && !locationListCursor.isClosed()) {
				locationListCursor.close();
			}
			if (cursor != null) {
				cursor.close();
			}
			//release ContentProviderClient
            if(unstableContentProvider != null)
                unstableContentProvider.release();
		}
		
		return locations.toArray(new WeatherLocation[0]);
	}
	
	/**
	 * Save location setting (original locations will be removed)
	 * @param cr ContentResolver
	 * @param app Application name
	 * @param locations Weather locations
	 */
	public static void saveLocations(ContentResolver cr, String app, WeatherLocation[] locations) {
        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return;
        }

        app = replaceClockApp(app);
		// delete first
		cr.delete(
			URI_LOCATION, 
			WeatherConsts.LOCATION_COLUMN_NAME.app.name() + "='" + app + "'", 
			null);
		
		// add new locations
		addLocation(cr, app, locations);
	}
	
	/********************************************************************
	 * Location
	 ********************************************************************/
	
	/**
	 * Add location setting
	 * @param cr ContentResolver
	 * @param app Application name
	 * @param locations Weather locations
	 */
	public static void addLocation(ContentResolver cr, String app, WeatherLocation[] locations) {	
		if (locations == null || locations.length < 1) {
			return;
		}
		
        app = replaceClockApp(app);
		int rowInserted = 0;
		try {
			rowInserted = cr.bulkInsert(URI_LOCATION, WeatherLocation2LocationContentValues(app, locations));
		} catch (Exception e) {
			Log.w(LOG_TAG, "exception was caught: " + e.getMessage(), e);
			return;
		}		
		if (rowInserted > 0) {
			cr.notifyChange(Uri.withAppendedPath(URI_LOCATION, app), null);
		}
	}
	
	 /**
     * convert WeatherLocation to location ContentValues
     * @param app app name
     * @param locs array of weather location
     * @return ContentValues
     */
	private static ContentValues[] WeatherLocation2LocationContentValues(String app, WeatherLocation[] locs) {
		final int totalWeatherLocation = locs.length;
		
		ContentValues[] vs = new ContentValues[totalWeatherLocation];
		for (int i=0; i<totalWeatherLocation; i++) {
			vs[i] = WeatherLocation2LocationContentValues(app, locs[i]);
		}
		
		return vs;
	}
	
	/**********************************************
	 * Private method
	 **********************************************/
	/** 
	 * convert WeatherLocation to location ContentValues
	 * @param app app name
	 * @param loc weather location
	 * @return ContentValues
	 */
	private static ContentValues WeatherLocation2LocationContentValues(String app, WeatherLocation loc) {
		ContentValues v = new ContentValues();
		
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.app.name(), app);
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.type.name(), loc.isCustomLocation() ? WeatherConsts.LOCATION_TYPE_CUSTOM : WeatherConsts.LOCATION_TYPE_CODE);
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.code.name(), loc.getCode());
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.name.name(), loc.getName());
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.state.name(), loc.getState());
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.country.name(), loc.getCountry());
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.latitude.name(), "" + loc.getLatitude());
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.longitude.name(), "" + loc.getLongitude());
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.timezone.name(), "" + loc.getTimezone());
		v.put(WeatherConsts.LOCATION_COLUMN_NAME.timezoneId.name(), "" + loc.getTimezoneId());
		
		return v;
	}

    /**
     * Replace clock category with weather.
     * 
     * @param app string to process
     * @return string
     */
    private static String replaceClockApp(String app) {
        if (WeatherConsts.APP_WORLDCLOCK.equals(app)) {
            if (LOG_FLAG)
                Log.d(LOG_TAG, "Query with Clock, replace with Weather");
            return WeatherConsts.APP_WEATHER;
        }

        return app;
    }
    
    /**
     * Replace clock category with weather. If weather also in the list, then
     * simply remove clock.
     * 
     * @param apps list to process
     * @return array
     */
    private static String[] replaceClockApp(String[] apps) {
        List<String> result = new LinkedList<String>();
        List<String> appsList = Arrays.asList(apps);
        for (String app : appsList) {
            if (WeatherConsts.APP_WORLDCLOCK.equals(app)) {
                if (!appsList.contains(WeatherConsts.APP_WEATHER)) {
                    if (LOG_FLAG)
                        Log.d(LOG_TAG, "Query with Clock in list, replace with Weather");
                    result.add(WeatherConsts.APP_WEATHER);
                } else {
                    if (LOG_FLAG)
                        Log.d(LOG_TAG, "Query with Clock in list, remove it");
                }
            } else {
                result.add(app);
            }
        }

        return result.toArray(new String[0]);
    }

    /** LANGUAGE_TRANSFER_PROJECT */
	private static final String[] LANGUAGE_TRANSFER_PROJECT = new String[] {
		WeatherConsts.LOCATION_LIST_COLUMN_NAME.name.name(), 
		WeatherConsts.LOCATION_LIST_COLUMN_NAME.state.name(), 
		WeatherConsts.LOCATION_LIST_COLUMN_NAME.country.name()
	};
	
	/**
	 * get LocationList by code for language transfer 
	 * @param unstableContentProvider ContentResolver
	 * @param code city code
	 * @return Location List with language transfer 
	 * @throws RemoteException 
	 */
	private static Cursor getLocationListByCodeForLanguageTransfer(ContentProviderClient unstableContentProvider, String code) throws RemoteException {
//checked by caller
//        // check if WeatherProvider is installed and permission is valid
//        if (!isWeatherProviderInstalled(unstableContentProvider)) {
//            return null;
//        }

		String where = WeatherConsts.LOCATION_LIST_COLUMN_NAME.code.name() + "='" + saftSearchCharacters(code) + "'";
		return unstableContentProvider.query(
			Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_LIST_PATH),
			LANGUAGE_TRANSFER_PROJECT,
			where, 
			null,
			null);
	}
	
	/**
     * To check if WeatherProvider is installed or not
     * (will check WeatherSyncProvider first due to permission declaration)
     * 
     * @param cr ContentResolver
     * @return true if WeatherProvider is installed
     * 
     * @hide
     */
    private static boolean isWeatherProviderInstalled(ContentResolver cr) {
        if (!isWeatherSyncProviderInstalled(cr)) {
            Log.e(LOG_TAG, "check WeatherProvider, but WeatherSyncProvider is not installed!");
            return false;
        }

        ContentProviderClient client = cr
                .acquireUnstableContentProviderClient(WeatherConsts.CONTENT_URI);
        if (client != null) {
            client.release();

            return true;
        }

        Log.e(LOG_TAG, "WeatherProvider is not installed!");
        return false;
    }
    
    /**
	 * Delete locations
	 * @param cr ContentResolver
	 * @param app Application name
	 * @param codes location codes
	 * @return the number of rows affected
	 */
	public static int deleteLocation(ContentResolver cr, String app, String[] codes) {
		if (app == null || codes == null || codes.length < 1) {
			return 0;
		}

        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return 0;
        }

        app = replaceClockApp(app);
		StringBuffer where = new StringBuffer("")
			.append(WeatherConsts.LOCATION_COLUMN_NAME.app.name()).append("='").append(app).append("' AND (");
		
		for (String code : codes) {
			where.append(WeatherConsts.LOCATION_COLUMN_NAME.code.name()).append("='").append(saftSearchCharacters(code)).append("' OR ");
		}
		where.append("0=1)");
		
		int rowDeleted = cr.delete(URI_LOCATION, where.toString(), null);
		
		if (rowDeleted > 0) {
			cr.notifyChange(Uri.withAppendedPath(URI_LOCATION, app), null);
		}
		
		return rowDeleted;
	}
	
	/**
	 * Delete location setting by latitude and longitude
	 * @param cr ContentResolver
	 * @param app Application name
	 * @param latitude latitude
	 * @param longitude longitude
	 * @return the number of rows affected
	 */
	public static int deleteLocation(ContentResolver cr, String app, double latitude, double longitude) {
		if (app == null) {
			return 0;
		}

        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return 0;
        }

        app = replaceClockApp(app);
		String where = WeatherConsts.LOCATION_COLUMN_NAME.app.name() + "='" + app + "' AND " +
					   WeatherConsts.LOCATION_COLUMN_NAME.latitude.name() + "='" + (""+latitude) + "' AND " +
					   WeatherConsts.LOCATION_COLUMN_NAME.longitude.name() + "='" + (""+longitude) + "'";
		
		int rowDeleted = cr.delete(URI_LOCATION, where, null); 
		
		if (rowDeleted > 0) {
			cr.notifyChange(Uri.withAppendedPath(URI_LOCATION, app), null);
		}
		
		return rowDeleted;	
	}
	
	/**
     * disable CurrentLocation
     * 
     * @param context context
     */
    public static void disableCurrentLocation(Context context) {
        if (LOG_FLAG)
            Log.d(LOG_TAG, "remove current location latitude and its weather cache data");
        WeatherUtility.saveLocations(context.getContentResolver(), "com.htc.htclocationservice",
                new WeatherLocation[] {});
        cleanCurInCache(context, WeatherRequest.generateWeatherRequestForCurrentLocation());

        turnOnWSPCurrentLocationFlag(context);
        Intent notifyIntent = new Intent("com.htc.htclocationservice.currentlocation.updated");
        sendBroadcast(context, notifyIntent, android.os.Build.VERSION.SDK_INT >= 26 ? WeatherConsts.PERMISSION_HSP : null);
    }
    
    private static void cleanCurInCache(Context context, WeatherRequest req) {
        // check if WeatherSyncProvider is installed and permission is valid
        if (!isWeatherSyncProviderInstalled(context.getContentResolver())) {
            return;
        }

        String where = WeatherConsts.TABLE_DATA_COLUMNS.type.name() + " = " + req.getType();
        int ret = context.getContentResolver().delete(WeatherConsts.URI_DATA, where, null);

        if (LOG_FLAG)
            Log.d(LOG_TAG, "clean cur in cache, rows: " + ret);
    }
    
    /**
     * turn On WSP CurrentLocation Flag
     * 
     * @param context context
     */
    private static void turnOnWSPCurrentLocationFlag(Context context) {
        WeatherUtility.insertOrUpdateSetting(context.getContentResolver(), WSP_SETTING_APP_NAME,
                WSP_FLAG_CUR_LOC_KEY, WSP_FLAG_CUR_LOC_ON);
    }
    
    /**
     * check temperature is Celsius ?
     * 
     * @param context context
     * @return true: Celsius ,false: not Celsius
     */
    public static boolean isTemperatureCelsius(Context context) {
        return TEMPERATURE_UNIT_CELSIUS.equals(getTemperatureUnit(context));
    }

    private static final String SETTING_KEY_USING_WCR = "com.htc.sync.provider.weather.setting.usingwcr";
    private static final String SETTING_KEY_WCRDATA_FINDCITYNAME = "com.htc.sync.provider.weather.setting.wcrdata.findcityname";
    private static final String SETTING_KEY_WCRDATA_GETVENDORLOGO = "com.htc.sync.provider.weather.setting.wcrdata.GetVendorLogo";
    private static final String SETTING_KEY_GPS_ERROR_CODE = "com.htc.sync.provider.weather.setting.gps.error.code";

    private static final String DEFAULT_findCityName = "https://htc2.accu-weather.com/widget/htc2/city-find.asp";

    /**
     * get Using WCR Flag
     *
     * @param context context
     * @return WCR Flag
     */
    public static boolean getUsingWCRFlag(Context context) {
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, no WCR
            return false;
        }

        boolean flag = Settings.System.getInt(context.getContentResolver(), SETTING_KEY_USING_WCR, 0) == 0 ? false : true;
        Log.w(LOG_TAG, "get UsingWCR flag is: " + flag);
        return flag;
    }

    /**
     * get WCR Find City Name
     * 
     * @param context context
     * @return address
     */
    public static String getWCRFindCityName(Context context) {
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, no WCR
            return DEFAULT_findCityName;
        }

        String address = Settings.System.getString(context.getContentResolver(), SETTING_KEY_WCRDATA_FINDCITYNAME);
        if (TextUtils.isEmpty(address)) {
            Log.w(LOG_TAG, "get WCR FindCityName: empty, using default address");
            address = DEFAULT_findCityName;
        }
        Log.w(LOG_TAG, "get WCR FindCityName: " + address);
        return address;
    }

    /**
     * get WCR Get Vendor Logo
     * 
     * @param context context
     * @return address
     */
    public static String getWCRGetVendorLogo(Context context) {
        if (WeatherConsts.PRISM_CUSTOM_AUTHORITY_PREFIX != null) {
            // Prism non-Htc, no WCR
            return "";
        }

        String address = Settings.System.getString(context.getContentResolver(), SETTING_KEY_WCRDATA_GETVENDORLOGO);
        Log.w(LOG_TAG, "get WCR GetVendorLogo: " + address);
        return address;
    }
    
    /**
	 * Generate display text for App. World Clock
	 * @param cursor cursor
	 * @return [0]: city text, [1]: country text
	 */
	public static String[] generateDisplayText(Cursor cursor) {
		WeatherLocationExtInfo extInfo = getWeatherLocationExtInfo(cursor);
		if (extInfo == null) {
			return new String[] {"", ""};
		}
		
		StringBuffer city = new StringBuffer("");
		if (!TextUtils.isEmpty(extInfo.systemLangName)) {
			city.append(extInfo.systemLangName);
		}
		if (!TextUtils.isEmpty(extInfo.systemLangState)) {
			if (city.length() > 0) {
				city.append(", ");
			}
			city.append(extInfo.systemLangState);
		}
		
		StringBuffer country = new StringBuffer("");
		if (!TextUtils.isEmpty(extInfo.systemLangCountry)) {
			country.append(extInfo.systemLangCountry);
		}
		
		if (extInfo.hasEngInfo) {
			if (!TextUtils.isEmpty(extInfo.engName)) {
				if (city.length() > 0) {
					city.append(" ");
				}
				city.append(extInfo.engName);
			}
			if (!TextUtils.isEmpty(extInfo.engCountry)) {
				if (country.length() > 0) {
					country.append(" ");
				}
				country.append(extInfo.engCountry);
			}
		}
		
		return new String[] {city.toString(), country.toString()};
	}
	
	/**
	 * check is contain illegal characters or not
	 * @param column database column name
	 * @param str input parameter
	 * @return true: contain illegal characters, false: not contain
	 */
	private static boolean isContainIllegalCharacters(String column, String str) {
		if (column != null && column.equals(WeatherConsts.LOCATION_LIST_COLUMN_NAME.timezoneId.name())) {
			return str.contains("%");
		} else {
			return str.contains("%") || str.contains("_");
		}
	}
	
	/**
	 * generate Empty Result
	 * @param cr ContentResolver
	 * @return Empty cursor Result
	 */
	private static Cursor generateEmptyResult(ContentResolver cr) {
        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(cr)) {
            return null;
        }

		return cr.query(
			Uri.withAppendedPath(WeatherConsts.CONTENT_URI, WeatherConsts.LOCATION_LIST_PATH), 
			null,
			WeatherConsts.LOCATION_LIST_COLUMN_NAME._id.name() + "<0", 
			null, 
			null);	
	}
	
	/**
     *  Hide Automatically by SDK Team [U12000] <br>
     *  <br>
     *  transform Lower Case
     *  @param string input string
     *  
     *  @hide
     */
    private static String transformLowerCase(String string) {
    	if (string != null && string.length() > 0) {
    		int length = string.length();
    		for (int i = 0 ; i < length ; i++) {
    			char c = string.charAt(i);
    			if ((UBOUND_UPPERCASE_EN_US <= c && c <= LBOUND_UPPERCASE_EN_US) ||
    					(UBOUND_UPPERCASE_EN_GR <= c && c <= LBOUND_UPPERCASE_EN_GR) ||
    					(UBOUND_UPPERCASE_RU_RU <= c && c <= LBOUND_UPPERCASE_RU_RU)) {
    				string = string.replace(c, (char) (c + 0x0020));
    			}
    		}
    	}
    	
    	return string;
    }
    
    /**
	 * search LocationList In English And Locale Language
	 * @param context context
	 * @param keyword search key word
	 * @param sortColumnName sort by which column
	 * @return Cursor search result with English and Locale Language
	 */
	public static Cursor searchLocationListInEnglishAndLocaleLanguage(Context context, String keyword, String sortColumnName) {
	    if (context == null) {
	        return null;
	    }

        // check if WeatherProvider is installed and permission is valid
        if (!isWeatherProviderInstalled(context.getContentResolver())) {
            return null;
        }

		if (keyword == null || keyword.length() < 1) {
			return getLocationList(context.getContentResolver(), sortColumnName);
		}
		
		if (isContainIllegalCharacters(null, keyword)) {
			return generateEmptyResult(context.getContentResolver());
		}
		
		// Only search english database when device language is non-english
		boolean isSystemLanguageEnglish = isSystemLanguageEnglish(context);
		
		if (isSystemLanguageEnglish) {
			return WeatherUtility.getLocaitonList(context.getContentResolver(), sortColumnName, keyword, WeatherConsts.SEARCH_COLUMN.CITY_STATE_AND_COUNTRY, WeatherConsts.SEARCH_TYPE.START_WITH);
		} else {
			Cursor cursorInEng = null;			
			boolean closeCursorInEng = false;			
			
			try {
				String _keyword = saftSearchCharacters(keyword); 
				String whereInEng = WeatherConsts.LOCATION_LIST_COLUMN_NAME.name.name() + " like '" + _keyword + "%' OR " + 
				   			   WeatherConsts.LOCATION_LIST_COLUMN_NAME.state.name() + " like '" + _keyword + "%' OR " +
				   			   WeatherConsts.LOCATION_LIST_COLUMN_NAME.country.name() + " like '" + _keyword + "%'";
				
				cursorInEng = context.getContentResolver().query(
						LOCATION_LIST_URI_IN_ENGLISH, 
						null, 
						whereInEng, 
						null, 
						sortColumnName);
				if (cursorInEng == null || cursorInEng.getCount() < 1) {
					// no result in english, return system language search result
					closeCursorInEng = true;										
					return WeatherUtility.getLocaitonList(context.getContentResolver(), sortColumnName, keyword, WeatherConsts.SEARCH_COLUMN.CITY_STATE_AND_COUNTRY, WeatherConsts.SEARCH_TYPE.START_WITH); 
				} else {
					// get result in english, get system language name/state/country
					closeCursorInEng = false;
					cursorInEng.moveToFirst();
					cursorInEng.getExtras().putString(CURSOR_EXTRA_KEY_HAS_EXTINFO, "true");
				}
				
				HashMap<Integer, StringBuffer> codeInEng = new HashMap<Integer, StringBuffer>();
				int count = 0;				
				
				do {
					count++;
					int group = count >> 9; // max depth 1000
					if (!codeInEng.containsKey(group)) {
						codeInEng.put(group, new StringBuffer(""));
					}
					String _code = cursorInEng.getString(cursorInEng.getColumnIndex(WeatherConsts.LOCATION_LIST_COLUMN_NAME.code.name()));
					String _where = WeatherConsts.LOCATION_LIST_COLUMN_NAME.code.name() + "='" + saftSearchCharacters(_code) + "' OR ";
					codeInEng.get(group).append(_where);					
				} while (cursorInEng.moveToNext());			
				
				for (StringBuffer whereInSystemLang : codeInEng.values()) {
					Cursor cursorInSystemLang = null;
					
					try {
						cursorInSystemLang = context.getContentResolver().query(
								LOCATION_LIST_URI_DEFAULT,
								new String[] {
									WeatherConsts.LOCATION_LIST_COLUMN_NAME.code.name(),
									WeatherConsts.LOCATION_LIST_COLUMN_NAME.name.name(),
									WeatherConsts.LOCATION_LIST_COLUMN_NAME.state.name(),
									WeatherConsts.LOCATION_LIST_COLUMN_NAME.country.name(),
								},
								whereInSystemLang.append(WeatherConsts.LOCATION_LIST_COLUMN_NAME.code.name()+"='xxxxx'").toString(),
								null,
								null);
							
							if (cursorInSystemLang != null && cursorInSystemLang.getCount() > 0 && cursorInSystemLang.moveToFirst()) {
								do {
									String code = cursorInSystemLang.getString(0); 
									cursorInEng.getExtras().putString(code + CURSOR_EXTRA_KEY_NAME, cursorInSystemLang.getString(1));
									cursorInEng.getExtras().putString(code + CURSOR_EXTRA_KEY_STATE, cursorInSystemLang.getString(2));
									cursorInEng.getExtras().putString(code + CURSOR_EXTRA_KEY_COUNTRY, cursorInSystemLang.getString(3));
								} while (cursorInSystemLang.moveToNext());
							}
					} finally {
						if (cursorInSystemLang != null) {
							cursorInSystemLang.close();
						}
					}
				}
				
				return cursorInEng;
				
			} finally {
				if (closeCursorInEng && cursorInEng != null) {
					cursorInEng.close();
				}
			}
		}
	}
	
	/**
	 * check System Language is English or not
	 * @param context context
	 * @return true: System Language is English, false: System Language isn't English
	 */
	private static boolean isSystemLanguageEnglish(Context context) {
		Locale systemLocale = context.getResources().getConfiguration().locale;
		String systemLanguage = systemLocale.getLanguage();
		
		return systemLanguage.equals("en");
	}	
	
	/**
     *  Hide Automatically by SDK Team [U12000]<br><br>
     *  
     *  Get WeatherLocation ExtInfo 
     *  @param cursor input parameter
     *  @return WeatherLocationExtInfo Weather location extra information
     *  
     *  @hide
     */
	private static WeatherLocationExtInfo getWeatherLocationExtInfo(Cursor cursor) {
		if (cursor == null) {
			return null;
		}
		
		boolean hasCursorExtra = cursor.getExtras() != null && cursor.getExtras() != Bundle.EMPTY && cursor.getExtras().containsKey(CURSOR_EXTRA_KEY_HAS_EXTINFO);
		WeatherLocationExtInfo extInfo = new WeatherLocationExtInfo();
		
		if (hasCursorExtra) {
			// cursor.name in English, extra.name in system language
			extInfo.hasEngInfo = true;
			
			String code = cursor.getString(cursor.getColumnIndex(WeatherConsts.LOCATION_LIST_COLUMN_NAME.code.name()));
			
			extInfo.systemLangName = cursor.getExtras().getString(code + CURSOR_EXTRA_KEY_NAME);
			extInfo.systemLangState = cursor.getExtras().getString(code + CURSOR_EXTRA_KEY_STATE);
			extInfo.systemLangCountry = cursor.getExtras().getString(code + CURSOR_EXTRA_KEY_COUNTRY);
			extInfo.engName = cursor.getString(cursor.getColumnIndex(WeatherConsts.LOCATION_LIST_COLUMN_NAME.name.name()));
			extInfo.engState = cursor.getString(cursor.getColumnIndex(WeatherConsts.LOCATION_LIST_COLUMN_NAME.state.name()));
			extInfo.engCountry = cursor.getString(cursor.getColumnIndex(WeatherConsts.LOCATION_LIST_COLUMN_NAME.country.name()));

		} else {
			// cursor.name in system language
			extInfo.hasEngInfo = false;
			
			extInfo.systemLangName = cursor.getString(cursor.getColumnIndex(WeatherConsts.LOCATION_LIST_COLUMN_NAME.name.name()));
			extInfo.systemLangState = cursor.getString(cursor.getColumnIndex(WeatherConsts.LOCATION_LIST_COLUMN_NAME.state.name()));
			extInfo.systemLangCountry = cursor.getString(cursor.getColumnIndex(WeatherConsts.LOCATION_LIST_COLUMN_NAME.country.name()));
		}
		
		return extInfo;
	}
	
	protected static Bundle getDataBundle(Context context,WeatherData data) {
    	Bundle bundle = null;
    	if(data != null) {
    		bundle = new Bundle();
    		data.checkMaxAvailableIndex();
            bundle.putParcelable(WeatherConsts.KEY_OUT_CURWEATHER_DATA, data.getCurWeatherDataInfo(context,null));
            bundle.putParcelableArrayList(WeatherConsts.KEY_OUT_FSTWEATHER_DATA, data.getFstWeatherDataInfo(context, numberOfDay));
            bundle.putParcelable(WeatherConsts.KEY_OUT_HOURWEATHER_DATA, data.getHourWeatherDataInfo(context, null));	
            bundle.putString(WeatherConsts.KEY_OUT_CATEGORYNAME, data.toString());
            bundle.putLong(WeatherConsts.KEY_OUT_LAST_UPDATE, data.lastUpdate);
            bundle.putString(WeatherConsts.KEY_OUT_CITY_LOCALTIME, data.cityLocalTime);
            bundle.putString(WeatherConsts.KEY_OUT_CITY_WEB_URL, data.cityWebURL);
            bundle.putString(WeatherConsts.KEY_OUT_TIMEZONE_ID, data.curLocTimezoneId);
    	}
    	return bundle;
    }
	
	protected static class WeatherData implements Parcelable{
		
		/** The symbol of separate */
	    private static final String SEPARATE = ";";

	    /**
	     * 
	     */
	    private int type = 0;

	    /**
	     * 
	     */
	    private String param1 = "";

	    /**
	     * 
	     */
	    private String param2 = "";

	    /**
	     * 
	     */
	    private long lastUpdate = 0;

	    /**
	     * 
	     */
	    private int curTempC = 0;

	    /**
	     * 
	     */
	    private int curTempF = 0;

	    /**
	     * 
	     */
	    private String curConditionId = "";

	    /**
	     * 
	     */
	    private ArrayList<String> fstName = new ArrayList<String>();

	    /**
	     * 
	     */
	    private ArrayList<String> fstDate = new ArrayList<String>();

	    /**
	     * 
	     */
	    private ArrayList<String> fstConditionId = new ArrayList<String>();

	    /**
	     * 
	     */
	    private ArrayList<String> fstHighTempC = new ArrayList<String>();

	    /**
	     * 
	     */
	    private ArrayList<String> fstHighTempF = new ArrayList<String>();

	    /**
	     * 
	     */
	    private ArrayList<String> fstLowTempC = new ArrayList<String>();

	    /**
	     * 
	     */
	    private ArrayList<String> fstLowTempF = new ArrayList<String>();

	    /**
	     * 
	     */
	    private String curLocLat = "";

	    /**
	     * 
	     */
	    private String curLocLng = "";

	    /**
	     * 
	     */
	    private String curLocLatTrim = "";

	    /**
	     * 
	     */
	    private String curLocLngTrim = "";

	    /**
	     * 
	     */
	    private String curLocName = "";

	    /**
	     * 
	     */
	    private String curLocState = "";

	    /**
	     * 
	     */
	    private String curLocCountry = "";

	    /**
	     * 
	     */
	    private String curLocTimezoneId = "";

	    /* Local city basic information { */
	    /** Local city time */
	    private String cityLocalTime = "";
	    /** Local city Latitude */ 
	    private String cityLatitude = "";
	    /** Local city Longitude */
	    private String cityLongitude = "";
	    /** Local city TimeZone */
	    private String cityTimeZone = "";
	    /** city web site URL */
	    private String cityWebURL = "";
	    /* } */

	    /* Current weather data information { */
	    /** Current is dayLight or nor */
	    private String dayLightFlag = ""; 
	    /** Current Celsius Temperature */
	    private int curFeelTempC = 0;
	    /** Current Fahrenheit Temperature */
	    private int curFeelTempF = 0;
	    /** Current Humidity */
	    private String curHumidity = "";
	    /** Current Wind direction */
	    private String curWinddirection = "";
	    /** Current Wind speed(Unit: mile) */
	    private String curWindspeedMI = "";
	    /** Current Visibility(Unit: mile) */
	    private String curVisibilityMI = "";
	    /** Current Wind speed(Unit: Kilometer) */
	    private String curWindspeedKM = "";
	    /** Current Visibility(Unit: Kilometer) */
	    private String curVisibilityKM = "";
	    /* } */

	    /* Forecast weather data information { */
	    /** Forecast Sunrise time */
	    private ArrayList<String> fstSunrise = new ArrayList<String>();
	    /** Forecast Sunset time */
	    private ArrayList<String> fstSunset = new ArrayList<String>();
	    /** Forecast daytime: Feel High Temperature(Celsius) */
	    private ArrayList<String> fstFeelHighTempC = new ArrayList<String>();
	    /** Forecast daytime: Feel High Temperature(Fahrenheit) */
	    private ArrayList<String> fstFeelHighTempF = new ArrayList<String>();
	    /** Forecast daytime: Feel Low Temperature(Celsius) */
	    private ArrayList<String> fstFeelLowTempC = new ArrayList<String>();
	    /** Forecast daytime: Feel Low Temperature(Fahrenheit) */
	    private ArrayList<String> fstFeelLowTempF = new ArrayList<String>();
	    /** Forecast night: Feel High Temperature(Celsius) */
	    private ArrayList<String> fstNightFeelHighTempC = new ArrayList<String>();
	    /** Forecast night: Feel High Temperature(Fahrenheit) */
	    private ArrayList<String> fstNightFeelHighTempF = new ArrayList<String>();
	    /** Forecast night: Feel Low Temperature(Celsius)  */
	    private ArrayList<String> fstNightFeelLowTempC = new ArrayList<String>();
	    /** Forecast night: Feel Low Temperature(Fahrenheit) */
	    private ArrayList<String> fstNightFeelLowTempF = new ArrayList<String>();
	    /** Forecast night: NightConditionId tells AP which of picture need to show(night) */
	    private ArrayList<String> fstNightConditionId = new ArrayList<String>();
	    /** Forecast night: High Celsius */
	    private ArrayList<String> fstNightHighTempC = new ArrayList<String>();
	    /** Forecast night: High Fahrenheit */
	    private ArrayList<String> fstNightHighTempF = new ArrayList<String>();
	    /** Forecast night: Low Celsius */
	    private ArrayList<String> fstNightLowTempC = new ArrayList<String>();
	    /** Forecast night: Low Fahrenheit */
	    private ArrayList<String> fstNightLowTempF = new ArrayList<String>();
	    /** Forecast daytime: The precip amount */
	    private ArrayList<String> fstPrecip = new ArrayList<String>();
	    /** Forecast night: The precip amount */
	    private ArrayList<String> fstNightPrecip = new ArrayList<String>();
	    /** Forecast web URL */
	    private ArrayList<String> fstWebURL = new ArrayList<String>();
	    /* } */

	    /* Hourly weather data { */
	    /** Hourly time */
	    private ArrayList<String> hourName = new ArrayList<String>();
	    /** hourConditionId tells AP which of picture need to show(daytime) */
	    private ArrayList<String> hourConditionId = new ArrayList<String>();
	    /** Hourly temperature Celsius */
	    private ArrayList<String> hourTempC = new ArrayList<String>();
	    /** Hourly temperature Fahrenheit */
	    private ArrayList<String> hourTempF = new ArrayList<String>();
	    /** Hourly Feel High Temperature(Celsius) */
	    private ArrayList<String> hourFeelTempC = new ArrayList<String>();
	    /** Hourly Feel Low Temperature(Fahrenheit) */
	    private ArrayList<String> hourFeelTempF = new ArrayList<String>();
	    /** Hourly The precip amount */
	    private ArrayList<String> hourPrecip = new ArrayList<String>();
	    /** Hourly web URL */
	    private ArrayList<String> hourWebURL = new ArrayList<String>();
	    /** Hourly epoch date time */
	    private ArrayList<String> hourEpochDateTime = new ArrayList<String>();
	    /* } */

        /** PM 2.5 */
        private String pm25 = "";

        private ArrayList<String> airQualityDaily = new ArrayList<String>();
	    /*add this information for initializing timezone {*/
	    /**timeZone Abbreviation**/
	    private String timeZoneAbbreviation="";
	    private String currentSetTimezone = "";
	    /* } */
	    
	    /** maximum index of forcast weather data. */
	    private int mMaxIndex = -1;
	    
	    private static final long DEFAULT_SUNRISE = 600;
	    private static final long DEFAULT_SUNSET  = 1800;
	    private static final int  STATUS_DAWN     = 0;
	    private static final int  STATUS_DAY      = 1;
	    private static final int  STATUS_NIGHT    = 2;
	    private static final int  DAY             = 1;
	    private static final int  NIGHT           = 2;
	    private static final long ONE_HOUR_MILLIS = 1000*60*60;
	    private static final long ONE_DAY_MILLIS  = 1000*60*60*24;
	    
	    /**ALL AVAILABLE FORECAST DATA.*/
	    private static final int ALL_AVAILABLE_FORECAST_DATA = -1;
	    
	    private static final int[] CONDITION_STATUS = {
	        // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
	           0, 1, 1, 1, 1, 1, 1, 0, 0, 0,
	           0, 0, 0, 1, 1, 0, 1, 1, 0, 0,
	           1, 1, 0, 1, 0, 0, 0, 0, 0, 0,
	           0, 0, 0, 2, 2, 2, 2, 2, 2, 2,
	           2, 2, 2, 2, 2, 0, 0, 0, 0, 0,
	           0, 0, 0, 0, 0
	    };
	    
	    private static final int[] CONDITION_CONVERT = {
	        // 0,  1,  2,  3,  4,  5,  6,  7,  8,  9,
	           0, 33, 34, 35, 36, 37, 38,  7,  8,  0,
	           0, 11, 12, 39, 40, 15, 41, 42, 18, 19,
	          43, 43, 22, 44, 24, 25, 26,  0,  0, 29,
	          30, 31, 32,  1,  2,  3,  4,  5,  6, 13,
	          14, 16, 17, 20, 23,  0,  0,  0,  0,  0,
	           0, 51, 52, 53, 54
	    };
	    
	    /**
	     * Constructor of WSPPData[1]
	     */
	    public WeatherData(){
	    }
	    
		/**
	     * Constructor of WSPPData[2]
	     * @param cursor input weather data by cursor
	     */
	    protected WeatherData(Cursor cursor){
//	        if (cursor.getColumnCount() == PROJECTION.length) {
	        try {

	            type = cursor.getInt(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.type.name()));
	            param1 = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.param1.name()));
	            param2 = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.param2.name()));
	            lastUpdate = cursor.getLong(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.lastUpdate.name()));
	            curTempC = cursor.getInt(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curTempC.name()));
	            curTempF = cursor.getInt(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curTempF.name()));
	            curConditionId = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curConditionId.name()));
	            
	            fstName.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstName.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstName.name())).split(SEPARATE, -1)) {
	                    fstName.add(item);
	                }
	            }

	            fstDate.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstDate.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstDate.name())).split(SEPARATE, -1)) {
	                    fstDate.add(item);
	                }
	            }

	            fstConditionId.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstConditionId.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstConditionId.name())).split(SEPARATE, -1)) {
	                    fstConditionId.add(item);
	                }
	            }

	            fstHighTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstHighTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstHighTempC.name())).split(SEPARATE, -1)) {
	                    fstHighTempC.add(item);
	                }
	            }

	            fstHighTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstHighTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstHighTempF.name())).split(SEPARATE, -1)) {
	                    fstHighTempF.add(item);
	                }
	            }

	            fstLowTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstLowTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstLowTempC.name())).split(SEPARATE, -1)) {
	                    fstLowTempC.add(item);
	                }
	            }

	            fstLowTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstLowTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstLowTempF.name())).split(SEPARATE, -1)) {
	                    fstLowTempF.add(item);
	                }
	            }

	            curLocLat = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curLocLat.name()));
	            curLocLng = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curLocLng.name()));
	            curLocLatTrim = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curLocLatTrim.name()));
	            curLocLngTrim = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curLocLngTrim.name()));
	            curLocName = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curLocName.name()));
	            curLocState = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curLocState.name()));
	            curLocCountry = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curLocCountry.name()));
	            curLocTimezoneId = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curLocTimezoneId.name()));
	            
	            cityLocalTime = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.cityLocalTime.name()));
	            cityLatitude = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.cityLatitude.name()));
	            cityLongitude = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.cityLongitude.name()));
	            cityTimeZone = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.cityTimeZone.name()));
	            cityWebURL = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.cityWebURL.name()));
	            
	            dayLightFlag = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.dayLightFlag.name()));
	            curFeelTempC = cursor.getInt(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curFeelTempC.name()));
	            curFeelTempF = cursor.getInt(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curFeelTempF.name()));
	            curHumidity = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curHumidity.name()));
	            curWinddirection = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curWinddirection.name()));
	            curWindspeedMI = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curWindspeed.name()));
	            curVisibilityMI = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.curVisibility.name()));
	            curWindspeedKM = mileToKilo(curWindspeedMI);
	            curVisibilityKM = mileToKilo(curVisibilityMI);

	            fstSunrise.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstSunrise.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstSunrise.name())).split(SEPARATE, -1)) {
	                    fstSunrise.add(item);
	                }
	            }

	            fstSunset.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstSunset.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstSunset.name())).split(SEPARATE, -1)) {
	                    fstSunset.add(item);
	                }
	            }

	            fstFeelHighTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstFeelHighTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstFeelHighTempC.name())).split(SEPARATE, -1)) {
	                    fstFeelHighTempC.add(item);
	                }
	            }

	            fstFeelHighTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstFeelHighTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstFeelHighTempF.name())).split(SEPARATE, -1)) {
	                    fstFeelHighTempF.add(item);
	                }
	            }

	            fstFeelLowTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstFeelLowTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstFeelLowTempC.name())).split(SEPARATE, -1)) {
	                    fstFeelLowTempC.add(item);
	                }
	            }

	            fstFeelLowTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstFeelLowTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstFeelLowTempF.name())).split(SEPARATE, -1)) {
	                    fstFeelLowTempF.add(item);
	                }
	            }

	            fstNightFeelHighTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelHighTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelHighTempC.name())).split(SEPARATE, -1)) {
	                    fstNightFeelHighTempC.add(item);
	                }
	            }

	            fstNightFeelHighTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelHighTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelHighTempF.name())).split(SEPARATE, -1)) {
	                    fstNightFeelHighTempF.add(item);
	                }
	            }

	            fstNightFeelLowTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelLowTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelLowTempC.name())).split(SEPARATE, -1)) {
	                    fstNightFeelLowTempC.add(item);
	                }
	            }

	            fstNightFeelLowTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelLowTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelLowTempF.name())).split(SEPARATE, -1)) {
	                    fstNightFeelLowTempF.add(item);
	                }
	            }

	            fstNightConditionId.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightConditionId.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightConditionId.name())).split(SEPARATE, -1)) {
	                    fstNightConditionId.add(item);
	                }
	            }

	            fstNightHighTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightHighTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightHighTempC.name())).split(SEPARATE, -1)) {
	                    fstNightHighTempC.add(item);
	                }
	            }

	            fstNightHighTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightHighTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightHighTempF.name())).split(SEPARATE, -1)) {
	                    fstNightHighTempF.add(item);
	                }
	            }

	            fstNightLowTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightLowTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightLowTempC.name())).split(SEPARATE, -1)) {
	                    fstNightLowTempC.add(item);
	                }
	            }

	            fstNightLowTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightLowTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightLowTempF.name())).split(SEPARATE, -1)) {
	                    fstNightLowTempF.add(item);
	                }
	            }

	            fstPrecip.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstPrecip.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstPrecip.name())).split(SEPARATE, -1)) {
	                    fstPrecip.add(item);
	                }
	            }

	            fstNightPrecip.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightPrecip.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstNightPrecip.name())).split(SEPARATE, -1)) {
	                    fstNightPrecip.add(item);
	                }
	            }

	            hourName.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourName.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourName.name())).split(SEPARATE, -1)) {
	                    hourName.add(item);
	                }
	            }

	            hourConditionId.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourConditionId.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourConditionId.name())).split(SEPARATE, -1)) {
	                    hourConditionId.add(item);
	                }
	            }

	            hourTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourTempC.name())).split(SEPARATE, -1)) {
	                    hourTempC.add(item);
	                }
	            }

	            hourTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourTempF.name())).split(SEPARATE, -1)) {
	                    hourTempF.add(item);
	                }
	            }

	            hourFeelTempC.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourFeelTempC.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourFeelTempC.name())).split(SEPARATE, -1)) {
	                    hourFeelTempC.add(item);
	                }
	            }

	            hourFeelTempF.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourFeelTempF.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourFeelTempF.name())).split(SEPARATE, -1)) {
	                    hourFeelTempF.add(item);
	                }
	            }

	            hourPrecip.clear();
	            if(cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourPrecip.name())).length() != 0) {
	                for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourPrecip.name())).split(SEPARATE, -1)) {
	                    hourPrecip.add(item);
	                }
	            }

	            timeZoneAbbreviation = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.timeZoneAbbreviation.name()));
	            currentSetTimezone = cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.currentSetTimezone.name()));

                hourEpochDateTime.clear();
                int idxEDT = cursor
                        .getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourEpochDateTime.name());
                if (idxEDT != -1) {
                    if (cursor.getString(idxEDT).length() != 0) {
                        for (String item : cursor.getString(idxEDT).split(SEPARATE, -1)) {
                            hourEpochDateTime.add(item);
                        }
                    }
                }

                hourWebURL.clear();
                int idxHWU = cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.hourWebURL
                        .name());
                if (idxHWU != -1) {
                    if (cursor.getString(idxHWU).length() != 0) {
                        for (String item : cursor.getString(idxHWU).split(SEPARATE, -1)) {
                            hourWebURL.add(item);
                        }
                    }
                }

                fstWebURL.clear();
                int idxFWU = cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.fstWebURL
                        .name());
                if (idxFWU != -1) {
                    if (cursor.getString(idxFWU).length() != 0) {
                        for (String item : cursor.getString(idxFWU).split(SEPARATE, -1)) {
                            fstWebURL.add(item);
                        }
                    }
                }

                int idxPM25 = cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.pm25.name());
                if (idxPM25 != -1) {
                    pm25 = cursor.getString(cursor
                            .getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.pm25.name()));
                }
                
                airQualityDaily.clear();
                try {
                    if (cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.airQualityDaily.name())).length() != 0) {
                        for (String item : cursor.getString(cursor.getColumnIndex(WeatherConsts.TABLE_DATA_COLUMNS.airQualityDaily.name())).split(SEPARATE, -1)) {
                            airQualityDaily.add(item);
                        }
                    }
                } catch (Exception e) {
                    Log.w(LOG_TAG, "fail to get air quality");
                }

	            //check the max availableIndex
	            checkMaxAvailableIndex();
	        } catch (Exception e) {
	            if (LOG_FLAG) {
	                Log.d(LOG_TAG, "create weather data from cursor fail, " + e.getMessage());
	            }
	        }
//	        }
	    }
	    
	    /** Name of parameters and also name of the weahterSync database colume name */
	    private static final String[] PROJECTION = new String[] {
	    	WeatherConsts.TABLE_DATA_COLUMNS.type.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.param1.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.param2.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.lastUpdate.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curConditionId.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstName.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstDate.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstConditionId.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstHighTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstHighTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstLowTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstLowTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curLocLat.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curLocLng.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curLocLatTrim.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curLocLngTrim.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curLocName.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curLocState.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curLocCountry.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curLocTimezoneId.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.cityLocalTime.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.cityLatitude.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.cityLongitude.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.cityTimeZone.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.cityWebURL.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.dayLightFlag.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curFeelTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curFeelTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curHumidity.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curWinddirection.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curWindspeed.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.curVisibility.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstSunrise.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstSunset.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstFeelHighTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstFeelHighTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstFeelLowTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstFeelLowTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelHighTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelHighTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelLowTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightFeelLowTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightConditionId.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightHighTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightHighTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightLowTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightLowTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstPrecip.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.fstNightPrecip.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.hourName.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.hourConditionId.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.hourTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.hourTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.hourFeelTempC.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.hourFeelTempF.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.hourPrecip.name(),
	        //add for initializing the data timezone
	    	WeatherConsts.TABLE_DATA_COLUMNS.timeZoneAbbreviation.name(),
	    	WeatherConsts.TABLE_DATA_COLUMNS.currentSetTimezone.name()
	    };
	    
	    /**
	     *  Hide Automatically by SDK Team [U12000]<br><br>
	     *  get the projection of WSPPData
	     *  @return PROJECTION the column field of WSSPData 
	     *  @hide
	     */
	    protected static String[] getProjection() {
	        return PROJECTION;
	    }
	    
	    /**
	     * check the max index for every array list.[by error handling]
	     */
	    protected void checkMaxAvailableIndex()
	    {      
	        List<Integer> length = new ArrayList<Integer>();
	        if (length!=null) {
	            // - fstName
	            if (fstName!=null)
	                length.add(fstName.size());
	            // - fstDate
	            if (fstDate!=null)
	                length.add(fstDate.size());
	            // - fstConditionId
	            if (fstConditionId!=null)
	                length.add(fstConditionId.size());
	            // - fstNightConditionId
	            if (fstNightConditionId!=null)
	                length.add(fstNightConditionId.size());
	            // - fstHighTempC
	            if (fstHighTempC!=null)
	                length.add(fstHighTempC.size());
	            // - fstHighTempF
	            if (fstHighTempF!=null)
	                length.add(fstHighTempF.size());
	            // - fstLowTempC
	            if (fstLowTempC!=null)
	                length.add(fstLowTempC.size());
	            // - fstLowTempF
	            if (fstLowTempF!=null)
	                length.add(fstLowTempF.size());
	            // - fstNightHighTempC
	            if (fstNightHighTempC!=null)
	                length.add(fstNightHighTempC.size());
	            // - fstNightHighTempF
	            if (fstNightHighTempF!=null)
	                length.add(fstNightHighTempF.size());
	            // - fstNightLowTempC
	            if (fstNightLowTempC!=null)
	                length.add(fstNightLowTempC.size());
	            // - fstNightLowTempF
	            if (fstNightLowTempF!=null)
	                length.add(fstNightLowTempF.size());
	            // - fstFeelHighTempC
	            if (fstFeelHighTempC!=null)
	                length.add(fstFeelHighTempC.size());
	            // - fstFeelHighTempF
	            if (fstFeelHighTempF!=null)
	                length.add(fstFeelHighTempF.size());
	            // - fstFeelLowTempC
	            if (fstFeelLowTempC!=null)
	                length.add(fstFeelLowTempC.size());
	               // - fstFeelLowTempF
	             if (fstFeelLowTempF!=null)
	                length.add(fstFeelLowTempF.size());
	            // - fstNightFeelHighTempC
	             if (fstNightFeelHighTempC!=null)
	                length.add(fstNightFeelHighTempC.size());
	            // - fstNightFeelHighTempF
	            if (fstNightFeelHighTempF!=null)
	                length.add(fstNightFeelHighTempF.size());
	            // - fstNightFeelLowTempC
	            if (fstNightFeelLowTempC!=null)
	                length.add(fstNightFeelLowTempC.size());
	            // - fstNightFeelLowTempF
	            if (fstNightFeelLowTempF!=null)
	                length.add(fstNightFeelLowTempF.size());
	            int size = length.size();
	            if (size > 0) {
	                Collections.sort(length);
	                mMaxIndex = (length.get(0) <= length.get(length.size()-1)) ? length.get(0) : length.get(length.size()-1);
	// turn off log, it's for dev
//	                if (LOG_FLAG) Log.d(LOG_TAG, "checkMaxAvailableIndex() - mMaxIndex = " + mMaxIndex);
	            }
	            else {
	                if (LOG_FLAG) Log.w(LOG_TAG, "checkMaxAvailableIndex() - NO Available Index");
	                mMaxIndex = -1;
	            }
	        }
	    }
	    
	    /**
	     * Get the current location name
	     * 
	     * @return current location name
	     */
	    protected String getCurLocName() {
	        return curLocName;
	    }

	    /**
	     * Get the current location state
	     * 
	     * @return current location state
	     */
	    protected String getCurLocState() {
	        return curLocState;
	    }

	    /**
	     * Get the current location country
	     * 
	     * @return Current country
	     */
	    protected String getCurLocCountry() {
	        return curLocCountry;
	    }
	    
	    /**
	     * @return String current location latitude after trimming.
	     * @hide
	     */
	    protected String getCurLocLatTrim() {
	        return curLocLatTrim;
	    }

	    /**
	     * get Current Location Longitude Trim
	     * 
	     * @return curLocLngTrim
	     * @hide
	     */
	    protected String getCurLocLngTrim() {
	        return curLocLngTrim;
	    }
	    
	    protected String getCurLocLat() {
	        return curLocLat;
	    }

	    /**
	     * Get the current location longitude
	     * 
	     * @return current location longitude
	     */
	    protected String getCurLocLng() {
	        return curLocLng;
	    }
	    
	    /**
	     * Get the current location TimeZone id
	     * 
	     * @return the current location TimeZone id
	     */
	    protected String getCurLocTimezoneId() {
	        return curLocTimezoneId;
	    }
	    
	    /**
	     * Get the last update
	     * 
	     * @return last update time
	     */
	    protected long getLastUpdate() {
	        return lastUpdate;
	    }
	    
	    protected Bundle getCurWeatherDataInfo(Context context, Bundle extras) {
	        // resTemp: { isCurrentID, indexDaily, isDay, conditionID, indexHourly }
	        int[] resTemp = getCurrentDataStatus(context);
	        Bundle res = new Bundle();
	        if (res!=null && resTemp!=null) {
	            boolean useDayInfo = (resTemp[2]==1 || resTemp[2]==0) ? true : false; // Day and Dawn both use day info
	            // Current Condition ID, if the time is overdue to get forcast condition Id
	            int currID = resTemp[3];
	            if (resTemp[0] == 0) {
	                currID = safe_parseInt(getStringValue(useDayInfo ? fstConditionId : fstNightConditionId, resTemp[1]));
	                if (resTemp[2] == STATUS_DAWN) {
	                    currID = convertConditionID(resTemp[2], currID);
	                }
	            } else if (resTemp[0] == 2) {
	                currID = safe_parseInt(getStringValue(hourConditionId, resTemp[4]));
	                currID = convertConditionID(resTemp[2], currID);
	            }
	            
	            res.putInt(WeatherConsts.KEY_OUT_CURR_COND_ID, currID);
	            res.putBoolean(WeatherConsts.KEY_OUT_DAYLIGHT, resTemp[2] == 1);

//	            if (LOG_FLAG) Log.d(LOG_TAG, "getCurWeatherDataInfo()[1] - Current_ID = " + currID /*+ ", DayLight_Flag: " + isDay*/);

	            int currentTempC = curTempC;
	            int currentTempF = curTempF;
	            int currentFeelTempC = curFeelTempC;
	            int currentFeelTempF = curFeelTempF;
	            boolean useCurrentTempC, useCurrentTempF, useCurrentFeelTempC, useCurrentFeelTempF;
	            useCurrentTempC = useCurrentTempF = useCurrentFeelTempC = useCurrentFeelTempF = resTemp[0] == 1;
	            if (resTemp[0] == 2) {
	                final int hourIndex = resTemp[4];
	                if (hourIndex < hourTempC.size()) {
	                    currentTempC = safe_parseInt(hourTempC.get(hourIndex));
	                    useCurrentTempC = true;
	                }
	                if (hourIndex < hourTempF.size()) {
	                    currentTempF = safe_parseInt(hourTempF.get(hourIndex));
	                    useCurrentTempF = true;
	                }
	                if (hourIndex < hourFeelTempC.size()) {
	                    currentFeelTempC = safe_parseInt(hourFeelTempC.get(hourIndex));
	                    useCurrentFeelTempC = true;
	                }
	                if (hourIndex < hourFeelTempF.size()) {
	                    currentFeelTempF = safe_parseInt(hourFeelTempF.get(hourIndex));
	                    useCurrentFeelTempF = true;
	                }
	            }

	            // Temperature
	            putTemperature(useDayInfo?fstHighTempC:fstNightHighTempC, useDayInfo?fstLowTempC:fstNightLowTempC, resTemp[1], currentTempC, useCurrentTempC, false, true, res);
	            putTemperature(useDayInfo?fstHighTempF:fstNightHighTempF, useDayInfo?fstLowTempF:fstNightLowTempF, resTemp[1], currentTempF, useCurrentTempF, false, false, res);

	            // Real Feel Temperature
	            putTemperature(useDayInfo?fstFeelHighTempC:fstNightFeelHighTempC, useDayInfo?fstFeelLowTempC:fstNightFeelLowTempC, resTemp[1], currentFeelTempC, useCurrentFeelTempC, true, true, res);
	            putTemperature(useDayInfo?fstFeelHighTempF:fstNightFeelHighTempF, useDayInfo?fstFeelLowTempF:fstNightFeelLowTempF, resTemp[1], currentFeelTempF, useCurrentFeelTempF, true, false, res);

	            // Humidity & Winds & Visibility
	            if (resTemp[0] == 1) {
	                res.putString(WeatherConsts.KEY_OUT_HUMIDITY, curHumidity);
	                res.putString(WeatherConsts.KEY_OUT_WINDDIRECTION, curWinddirection);
	                res.putString(WeatherConsts.KEY_OUT_WINDSPEED_MI, curWindspeedMI);
	                res.putString(WeatherConsts.KEY_OUT_WINDSPEED_KM, curWindspeedKM);
	                res.putString(WeatherConsts.KEY_OUT_VISIBILITY_MI, curVisibilityMI);
	                res.putString(WeatherConsts.KEY_OUT_VISIBILITY_KM, curVisibilityKM);

//	                if (LOG_FLAG) Log.d(LOG_TAG, "getCurWeatherDataInfo()[2] - " +
//	                      "( curHumidity, curWinddirection, curWindspeedMI, curWindspeedKM, curVisibilityMI, curVisibilityKM ) = ( " +
//	                        curHumidity     + ", " + curWinddirection + ", " +
//	                        curWindspeedMI  + ", " + curWindspeedKM   + ", " +
//	                        curVisibilityMI + ", " + curVisibilityKM  + " )");
	            } else {
	                res.putString(WeatherConsts.KEY_OUT_HUMIDITY, "");
	                res.putString(WeatherConsts.KEY_OUT_WINDDIRECTION, "");
	                res.putString(WeatherConsts.KEY_OUT_WINDSPEED_MI, "");
	                res.putString(WeatherConsts.KEY_OUT_WINDSPEED_KM, "");
	                res.putString(WeatherConsts.KEY_OUT_VISIBILITY_MI, "");
	                res.putString(WeatherConsts.KEY_OUT_VISIBILITY_KM, "");

//	                if (LOG_FLAG) Log.d(LOG_TAG, "getCurWeatherDataInfo()[2] - " +
//	                      "( curHumidity, curWinddirection, curWindspeedMI, curWindspeedKM, curVisibilityMI, curVisibilityKM ) = ( , , , , , )");
	            }

	            // Sunrise & Sunset
	            String sunrise = "";
	            String sunset = "";
	            sunrise = getStringValue(fstSunrise, resTemp[1]);
	            sunset  = getStringValue(fstSunset, resTemp[1]);
	            res.putString(WeatherConsts.KEY_OUT_SUNRISE, sunrise);
	            res.putString(WeatherConsts.KEY_OUT_SUNSET, sunset);

	            // PM 2.5
	            if (!TextUtils.isEmpty(pm25)) {
                    res.putString(WeatherConsts.KEY_OUT_PM25, pm25);
	            }

//	            if (LOG_FLAG) Log.d(LOG_TAG, "getCurWeatherDataInfo()[3] - (Sunrise, Sunset) = (" + sunrise+ ", " + sunset + ")");

	            long NextTriggerWidgetTime = 0;
	            NextTriggerWidgetTime =triggerWidgetUpdateTime(context);
	            res.putLong(WeatherConsts.KEY_OUT_TRIGGER_TIME, NextTriggerWidgetTime);
	        }
	        return res;
	    }
	    
	    /**
	     * Getting the forcast weather data by parameter of days.
	     * Note: [Limit by data length. If the parameters of days is over the data length, the data will return the large of data. ]
	     * @param context context
	     * @param days how many days forcast weather data you wants.[Upper bound is the forcast weather data size from Accu weather ]
	     * @return bundle of arraylist for forcast weather data information.
	     */
	     protected ArrayList<Bundle> getFstWeatherDataInfo(Context context, int days) {
//	         if(LOG_FLAG) Log.d(LOG_TAG, "getFstWeatherDataInfo() - Intput days: " + days + ", " +
//	                     "Available days: " + ( ((days < mMaxIndex) && (days != -1)) ? days : mMaxIndex));
	         // Current data status: { isCurrentID, index, isDay, currentID }
	         int[] CurrentDataStatus = getCurrentDataStatus(context);

	         // Initial the forcast weather data list
	         ArrayList<Bundle> FstWeatherData = new  ArrayList<Bundle>();  

	         if (CurrentDataStatus!=null) {
	             //the initial index.
	             int index = CurrentDataStatus[1];
	             if (mMaxIndex > 0) { // have forecast weather data
	                 int requestDays = days; // default by request days
	                 if(days == ALL_AVAILABLE_FORECAST_DATA){
	                     requestDays = mMaxIndex;
	                     if(LOG_FLAG) Log.d(LOG_TAG, "Request all available forecast days");
	                 }

	                 int indAfterShift = -1;
	                 for(int i = 0 ; i < requestDays ; i++) {
	                     Bundle weatherData = new Bundle(); // the forcast weather data for each data
	                     //shift the offset to the right forecast data.
	                     indAfterShift = index + i;
	                     if(indAfterShift <= (mMaxIndex - 1)) {
	                         //forcast date
	                         weatherData.putString(WeatherConsts.KEY_OUT_FSTDATE, fstDate.get(indAfterShift));
	                         //forcast day
	                         weatherData.putString(WeatherConsts.KEY_OUT_FSTNAME, fstName.get(indAfterShift));
	                         //CondtionId
	                         weatherData.putInt(WeatherConsts.KEY_OUT_FSTCONDITIONID, safe_parseInt(fstConditionId.get(indAfterShift)));
	                         //High temperature of Celsius/Fahrenheit(daytime)
	                         weatherData.putString(WeatherConsts.KEY_OUT_HIGH_TEMP_C, fstHighTempC.get(indAfterShift));
	                         weatherData.putString(WeatherConsts.KEY_OUT_HIGH_TEMP_F, fstHighTempF.get(indAfterShift));
	                         //Low temperature of Celsius/Fahrenheit(night)
	                         weatherData.putString(WeatherConsts.KEY_OUT_LOW_TEMP_C, fstNightLowTempC.get(indAfterShift));
	                         weatherData.putString(WeatherConsts.KEY_OUT_LOW_TEMP_F, fstNightLowTempF.get(indAfterShift));

	                         //Night condition ID
	                         weatherData.putInt(WeatherConsts.KEY_OUT_FSTNIGHTCONDITIONID, safe_parseInt(fstNightConditionId.get(indAfterShift)));
	                         //Night High temperature of Celsius/Fahrenheit
	                         weatherData.putString(WeatherConsts.KEY_OUT_NIGHT_HIGH_TEMP_C, fstNightHighTempC.get(indAfterShift));
	                         weatherData.putString(WeatherConsts.KEY_OUT_NIGHT_HIGH_TEMP_F, fstNightHighTempF.get(indAfterShift));
	                         //Night Low temperature of Celsius/Fahrenheit
	                         weatherData.putString(WeatherConsts.KEY_OUT_NIGHT_LOW_TEMP_C, fstNightLowTempC.get(indAfterShift));
	                         weatherData.putString(WeatherConsts.KEY_OUT_NIGHT_LOW_TEMP_F, fstNightLowTempF.get(indAfterShift));

	                         //forecast sunset and sunrise
	                         weatherData.putString(WeatherConsts.KEY_OUT_SUNRISE, fstSunrise.get(indAfterShift));
	                         weatherData.putString(WeatherConsts.KEY_OUT_SUNSET, fstSunset.get(indAfterShift));

	                         //web URL
	                         try {
	                             weatherData.putString(WeatherConsts.KEY_OUT_FST_WEB_URL, fstWebURL.get(indAfterShift));
	                         } catch (Exception e) {
	                             if (LOG_FLAG) {
	                                 Log.d(LOG_TAG, "add fst web url error, " + e.getMessage());
	                             }
	                         }
	                         try {
	                             weatherData.putString(WeatherConsts.KEY_OUT_AIRQUALITY_DAILY, airQualityDaily.get(indAfterShift));
	                         } catch (Exception e) {
	                         }
	                     }
	                     //add to arraylist Bundle
	                     FstWeatherData.add(weatherData);
	                     //if no weather forcast data, return data.
	                     if(indAfterShift == mMaxIndex - 1)
	                         return FstWeatherData;
	                 }
	             }
	         }
	         return FstWeatherData;
	     }
	    
	    /**
	     * For all app which need get the current to show. 
	     * Data include [cityWebURL,lastUpdate,cityLocalTime,hourName, hourConditionId ,hourPrecip, hourTempC, hourTempF]
	     * @param context context
	     * @param extras this parameter is for other design.
	     * @return bundle of current weather data information.
	     */
	    protected Bundle getHourWeatherDataInfo(Context context, Bundle extras) {
	    	Bundle res = new Bundle();
	    	if(res != null) {
	    		res.putStringArrayList(WeatherConsts.KEY_OUT_HOUR_NAME, hourName);
	    		res.putStringArrayList(WeatherConsts.KEY_OUT_HOUR_CONDITIONID, hourConditionId);
	    		res.putStringArrayList(WeatherConsts.KEY_OUT_HOUR_PRECIP, hourPrecip);
	    		res.putStringArrayList(WeatherConsts.KEY_OUT_HOUR_TEMPC, hourTempC);
	    		res.putStringArrayList(WeatherConsts.KEY_OUT_HOUR_TEMPF, hourTempF);
	    		try {
	    		    res.putStringArrayList(WeatherConsts.KEY_OUT_HOUR_WEB_URL, hourWebURL);
	    		    res.putStringArrayList(WeatherConsts.KEY_OUT_HOUR_EPOCH_DATE_TIME, hourEpochDateTime);
	    		} catch (Exception e) {
	    		    if (LOG_FLAG) {
                        Log.d(LOG_TAG, "add hour web url and date time error, " + e.getMessage());
                    }
	    		}
	    	}
	    	return res;
	    }
	    
	    /**
	     *  Putting temperature to Bundle.
	     * @param high High temperature
	     * @param low Low temperature 
	     * @param index index of ArrayList
	     * @param curTemp current temperature
	     * @param isCur Is current temperature or not?
	     * @param isFeel Is feel temperature or not?
	     * @param isCelsius Is Celsius temperature or not?
	     * @param res Bundle of storing temperature data
	     */
	    private void putTemperature(ArrayList<String> high, ArrayList<String> low, int index, 
	            int curTemp, boolean isCur, boolean isFeel, boolean isCelsius, Bundle res)
	    {
	        int intHigh, intLow, intCur;
	        String strHigh = getStringValue(high, index);
	        String strLow  = getStringValue(low, index);
	        try {
	            intHigh = Integer.parseInt(strHigh);
	            intLow  = Integer.parseInt(strLow);
	        } catch (Exception e) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "putTemperture() - Exception = " + e);
	            putExtra(res, isFeel, isCelsius, "", "", isCur?(""+curTemp):"");
	            return;
	        }

	        if (!isCur) {
	            intCur = (intHigh+intLow)/2;
	        } else {
	            intCur = curTemp;
	        }

//	      if (LOG_FLAG) Log.i(LOG_TAG, "putTemperture() - (cur, high, low) = (" + intCur + ", " + strHigh + ", " + strLow + ")");
	        // [NOTE]
	        // So far nobody need the high/low feel temperature.
	        // Therefore, don't need to check it.
	        if (!isFeel) {
	            if (intCur<intLow)  intLow  = intCur;
	            if (intCur>intHigh) intHigh = intCur;            
	        }
	        putExtra(res, isFeel, isCelsius, ""+intHigh, ""+intLow, ""+intCur);
	    }
	    
	    /**
	     * Utility of putTemperature() method.
	     * @param res bundle
	     * @param isFeel Is feel temperature or not?
	     * @param isCelsius Is Celsius of not?
	     * @param high High temperature
	     * @param low Low temperature
	     * @param cur current temperature
	     */
	    private void putExtra(Bundle res, boolean isFeel, boolean isCelsius, 
	            String high, String low, String cur)
	    {
	        String strLog = "";
	         if (isCelsius) {
	             strLog = "Celsius, ";
	             if (isFeel) {
	                 strLog += "FeelTemp";
	                 res.putString(WeatherConsts.KEY_OUT_FEEL_TEMP_C, cur);
	             }
	             else {
	                 strLog += "Temp";
	                res.putString(WeatherConsts.KEY_OUT_CURR_TEMP_C, cur);
	                res.putString(WeatherConsts.KEY_OUT_HIGH_TEMP_C, high);
	                res.putString(WeatherConsts.KEY_OUT_LOW_TEMP_C,  low);                 
	             }
	        }
	        else {
	            strLog = "Fahrenhei, ";
	             if (isFeel) {
	                 strLog += "FeelTemp";
	                 res.putString(WeatherConsts.KEY_OUT_FEEL_TEMP_F, cur);
	             }
	             else {
	                 strLog += "Temp";
	                res.putString(WeatherConsts.KEY_OUT_CURR_TEMP_F, cur);
	                res.putString(WeatherConsts.KEY_OUT_HIGH_TEMP_F, high);
	                res.putString(WeatherConsts.KEY_OUT_LOW_TEMP_F,  low);
	             }
	        }
	// turn off log, it's for dev
//	           if (LOG_FLAG) Log.d(LOG_TAG, "putExtra(" + strLog + ") - (cur, high, low) = (" + cur + ", " + high + ", " + low + ")");
	    }
	    
	    /**
	     * Utility of getting String value from Arraylist<String>
	     * @param list String type ArrayList
	     * @param index index of data
	     * @return String value
	     */
	    private String getStringValue(ArrayList<String> list, int index)
	    {
	        String strValue = "";
	        try {
	            strValue = list.get(index);            
	        }
	        catch(Exception e) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "getStringValue() - Exception = " + e);
	        }
	        return strValue;
	    }
	    
	    /**
	     * get the courrent data status
	     * @param context contex
	     * @return array of current data status 
	     * @Note[isCurrentID, index, indexHourly, isDay, currentID] =
	     *      [ (Current(1) or Overdue by day (0) or Overdue by hour (2),
	     *        (offset index of daily forecast data),
	     *        day(1) or dawn(0) night(2),
	     *        condition ID,
	     *        (offset index of hourly forecast data),
	     *        ]
	     */
	    private int[] getCurrentDataStatus(Context context)
	    {
	        // Input
	        boolean bDayLightFlag = (dayLightFlag!=null && dayLightFlag.equals("True"))?true:false;

	        // [NOTE] 
	        // If type is Current,
	        // and then set the system time zone as Time Zone of data. 
	        String strTimezoneId = currentSetTimezone;
	        
	        // Default Value
	        int[] result = {1, 0, 1, 0, -1};  // { isCurrentID, index daily, isDay, currentID, index hourly }
	        result[2] = bDayLightFlag?1:0;
	        try {
	            result[3] = Integer.parseInt(curConditionId);
	        }
	        catch (Exception e) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "getCurrentDataStatus() - Exception = " + e);
	            result[3] = 0;
	            return result;
	        }

	        // Check the input parameter
	        if (lastUpdate<=0) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "getCurrentDataStatus() - Invalid Input Parameter - Last Update Time");
	            return result;
	        }
	        if (fstDate==null || fstDate.size()<=0) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "getCurrentDataStatus() - Invalid Input Parameter - Forecast Date");
	            return result;
	        }

	        // Error Handling to avoid Java Crash.
	        int sizefstDate = fstDate.size();
	        if (fstSunrise==null || fstSunrise.size()<=0) {
	            fstSunrise = new ArrayList<String>();
	            //set to default
	            for(int i = 0 ; i < sizefstDate; i++ ) {
	                fstSunrise.add("6:00 AM");
	            }
	            if (LOG_FLAG) Log.w(LOG_TAG, "getCurrentDataStatus() - Error Handling - strSunrise[" + sizefstDate + "], and set data to default");
	        }
	        ArrayList<String> strSunrise = fstSunrise;

	        if (fstSunset==null || fstSunset.size()<=0) {
	            fstSunset = new ArrayList<String>();
	            //set to default
	            for(int i = 0 ; i < sizefstDate; i++ ) {
	                fstSunset.add("6:00 PM");
	            }
	            if (LOG_FLAG) Log.w(LOG_TAG, "getCurrentDataStatus() - Error Handling - strSunset[" + sizefstDate + "] , and set data to default");
	        }
	        ArrayList<String> strSunset  = fstSunset;

	        // [NOTE] 
	        // If Time Zone ID is Null or Empty,
	        // and then here will regard it as Online Search City.
	        boolean isOnlineSearchedCity = false;
	        // Convert CurrentSystemTime by TimeZone
	        long currentSystemTime = System.currentTimeMillis();
	        Calendar cCurrentTime = null;
	        if (strTimezoneId==null || strTimezoneId.isEmpty()) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "getCurrentDataStatus() - strTtimezone is null or empty - Online Searched City.");
//	          cCurrentTime = Calendar.getInstance();
	            isOnlineSearchedCity = true;
	        } else {
	// turn off log, it's for dev
//	            if (LOG_FLAG) Log.d(LOG_TAG, "getCurrentDataStatus() - strTtimezoneId = " + strTimezoneId);
	            cCurrentTime = Calendar.getInstance(TimeZone.getTimeZone(strTimezoneId));
	            isOnlineSearchedCity = false;
	        }

	        if (cCurrentTime==null) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "getCurrentDataStatus() - Invalid Parameter - cCurrentTime");
	            return result;
	        }

	        cCurrentTime.setTimeInMillis(currentSystemTime);
	        // Sunrise and Sunset - is Normal, Polar Day or Polar Night?
	        long longSunriseBase = convertHourMinute(strSunrise.get(0));
	        long longSunsetBase  = convertHourMinute(strSunset.get(0));
	        if (longSunriseBase==-1 && longSunsetBase==-1) {
	            strSunrise.set(0, "6:00 AM");
	            strSunset.set(0, "6:00 PM");
	            longSunriseBase = DEFAULT_SUNRISE;
	            longSunsetBase  = DEFAULT_SUNSET;
	        }

	        int hourOffset = beforeHour(cCurrentTime.getTimeInMillis());
	        int offset = beforeToday(fstDate.get(0), strTimezoneId);
	        // Current Data - is available ?
	        if (isOverdue(context, lastUpdate)) {
	            // For Forecast Data
	            result[0] = hourOffset != -1? 2: 0;
	            int sizeR  = strSunrise.size();
	            int sizeS  = strSunset.size();
	            int sizeD  = fstDate.size();
	            int size   = (sizeR<=sizeS)?sizeR:sizeS;
	                size   = (size<=sizeD)?size:sizeD;
	            boolean allOverDue = (offset>(size-1))?true:false;            
	            offset = Math.min(offset, size-1);
	            result[1] = offset;
	            result[4] = hourOffset;
	            long _longCurrent = getHourMinute(cCurrentTime.get(Calendar.HOUR_OF_DAY), cCurrentTime.get(Calendar.MINUTE));
	            long _longSunrise = convertHourMinute(strSunrise.get(offset));
	            long _longSunset  = convertHourMinute(strSunset.get(offset));

	            if (_longSunrise==-1 && _longSunset==-1) {
	                strSunrise.set(offset, "6:00 AM");
	                strSunset.set(offset, "6:00 PM");
	                _longSunrise = DEFAULT_SUNRISE;
	                _longSunset  = DEFAULT_SUNSET;
	            }

	            int  statusCur = getTimeStatus(_longCurrent, _longSunrise, _longSunset);
	            result[2] = statusCur;

	            if (LOG_FLAG) Log.i(LOG_TAG, "getCurrentDataStatus() - offset = " + offset + ", statusCur = " + statusCur);

	        } else {
	            // For Current Data Case
	            result[0] = 1;
	            result[1] = offset;
	            // For NOT Online Searched City {
	            if (!isOnlineSearchedCity) {
	                // is Day or Night ?
	                long _longCurrent = getHourMinute(cCurrentTime.get(Calendar.HOUR_OF_DAY), cCurrentTime.get(Calendar.MINUTE));
	                int statusCurTime = getTimeStatus(_longCurrent, longSunriseBase, longSunsetBase);
	                result[2] = statusCurTime;
	                // Check Current Condition ID With Local Time
	                result[3] = convertConditionID(result[2], result[3]);
	            }
	            // }
	        }

	        if (LOG_FLAG) {
	            Log.d(LOG_TAG, "current={ " + result[0] + ", " + result[1] + ", " + result[2] + ", " + result[3] + ", " + result[4] + " }");
	        }

	        return result;
	    }
	    
	    /**
	     * Calculate trigger Widget Update Time
	     * @param context context
	     * @return trigger  Widget Update Time
	     */
	    private long triggerWidgetUpdateTime(Context context)
	    {
	        //Initialization
	        long triggerTime = 0; // result value
	        SimpleDateFormat formatter1 = new SimpleDateFormat("M/d/yyyy HH:mm:ss z");
	        formatter1.setTimeZone(TimeZone.getTimeZone(currentSetTimezone)); // Format date by timeZone
	        /**Step1. Get the data next update time**/
	        long OneDayMilliSec = 1 * 24 * 60 * 60 * 1000;

            if (mMaxIndex <= 0) {
                return System.currentTimeMillis() + OneDayMilliSec;
            }

	        long interval = 3600000;
	        if (context != null) {
	            interval = WeatherUtility.getAutoSyncFrequency(context);
	        }
	        /*get the data next update time*/
	        long longNextupdatetime = System.currentTimeMillis() + interval; // default trigger time
	        if(lastUpdate > 0) {
	            longNextupdatetime = lastUpdate + interval;
	        }
	// turn off log, it's for dev
//	        if (LOG_FLAG) Log.d(LOG_TAG, "triggerWidgetUpdateTime() - interval: " + interval + ", next update time: " + longNextupdatetime 
//	                    + ", data overDue date: "+  formatter1.format(longNextupdatetime) + ", TimeZoneId = " + currentSetTimezone );

	        /**Step2. Get the sunrise and sunset time**/
	        long longCurrentSystemTime = System.currentTimeMillis(); /*get the current system time */
	        SimpleDateFormat formatter2 = new SimpleDateFormat("M/d/yyyy", Locale.US); // ask for US to ensure ASCII digits 
	        formatter2.setTimeZone(TimeZone.getTimeZone(currentSetTimezone));

	        //2.1 get current systime date
	        Calendar cCurrentTime = null;
	        cCurrentTime = Calendar.getInstance(TimeZone.getTimeZone(currentSetTimezone));
	        cCurrentTime.setTimeInMillis(longCurrentSystemTime);
	        String currentSystemDate = formatter2.format(cCurrentTime.getTime());

	        //2.2 define sunrise and sunset time.
	        //sunrise and sunset time
	        String strSunrise = "";
	        String strSunset = "";
	        int DateIndex = -1;
	        if(fstDate != null)
	            DateIndex = fstDate.indexOf(currentSystemDate);

	        if(DateIndex != -1) {
	            //Sunrise time from weather data.
	            if(DateIndex < mMaxIndex) {
	                if(fstSunrise != null)
	                    strSunrise = fstSunrise.get(DateIndex);
	            }
	            //Sunset time from weather data.
	            if(DateIndex < mMaxIndex) {
	                if(fstSunset != null)
	                    strSunset  = fstSunset.get(DateIndex);
	            }
	        } else { // no match date, no data
	            if (LOG_FLAG) Log.d(LOG_TAG, "triggerWidgetUpdateTime() - no match date from fstDate, so set the date to the last date of weather data");
	            //get the last Sunrise and Sunset time
	            if(fstSunrise != null && fstSunrise.size() != 0)
	                strSunrise = fstSunrise.get(mMaxIndex-1);
	            if(fstSunset != null && fstSunset.size() != 0)
	                strSunset = fstSunset.get(mMaxIndex-1);
	        }

	        //check string is valid or not
	        if(!checkStringValid(strSunrise, strSunset)) {
	            // string is invalid.
	            if (LOG_FLAG) Log.w(LOG_TAG, "triggerWidgetUpdateTime() - The string of Sunrise and Sunset is invaild, set to default value.");
	            strSunrise = "6:00 AM";
	            strSunset = "6:00 PM";
	        }

	// turn off log, it's for dev
//	        if (LOG_FLAG) Log.d(LOG_TAG, "triggerWidgetUpdateTime() - Today ==> CurrentSystemDate: " + currentSystemDate + ", cCurrentTime: " + formatter1.format(cCurrentTime.getTime()) 
//	                + ", TimeZoneId = " + currentSetTimezone + ", strSunrise: " + strSunrise + ", strSunset: " + strSunset);

	        /**Step 3. calculate the time about trigger widget to update.**/
	        long hourlyNextUpdateTime = 0L;
	        if (hourEpochDateTime != null && hourEpochDateTime.size() > 0) {
	            long hourStartTime = safe_parseLong(hourEpochDateTime.get(0), 0L) * 1000L;
	            if (hourStartTime > 0 && longCurrentSystemTime < hourStartTime) {
	                hourlyNextUpdateTime = hourStartTime;
	            } else {
	                int hourIndex = beforeHour(longCurrentSystemTime);
	                long hourMilli;
	                if (hourIndex != -1 && (hourMilli = safe_parseLong(hourEpochDateTime.get(hourIndex), 0L) * 1000L) > 0L) {
	                    hourlyNextUpdateTime = hourMilli + ONE_HOUR_MILLIS;
	                }
	            }
	        }
	        long longSunriseTime = specTime2MilliSec(strSunrise, longCurrentSystemTime);/*get the Sunrise Time*/
	        long longSunsetTime = specTime2MilliSec(strSunset, longCurrentSystemTime);  /*get the Sunset Time */
	        /**print log to verify the result**/
	// turn off log, it's for dev
//	        if (LOG_FLAG) Log.d(LOG_TAG,
//	                "\nfstDate: "   + (fstDate != null ? fstDate.toString() : "fstDate==null") +
//	                "\nfstSunrise: "+ (fstSunrise != null ? fstSunrise.toString() : "fstSunrise==null") +
//	                "\nfstSunset: " + (fstSunset != null ? fstSunset.toString() : "fstSunset==null") +
//	                "\nhourEpochDateTime: " + (hourEpochDateTime != null ? hourEpochDateTime.toString() : "hourEpochDateTime==null") +
//	                "\nCurrent System Time: " + formatter1.format(longCurrentSystemTime)    + ", longCurrentSystemTime:" + longCurrentSystemTime +
//	                "\nSunriseTime:         " + formatter1.format(longSunriseTime)          + ", longSunriseTime:      " + longSunriseTime +
//	                "\nSunsetTime:          " + formatter1.format(longSunsetTime)           + ", longSunsetTime:       " + longSunsetTime  +
//	                "\nNext update time:    " + formatter1.format(longNextupdatetime)       + ", longNextupdatetime:   " + longNextupdatetime +
//	                "\nHourly update time:  " + formatter1.format(hourlyNextUpdateTime)     + ", hourlyNextUpdateTime: " + hourlyNextUpdateTime
//	                );
	        //Current time before sunrise time
	        if(longCurrentSystemTime < longSunriseTime) {
//	            if (LOG_FLAG) Log.d(LOG_TAG, "triggerWidgetUpdateTime() - current time before sunrise time.");
	            if(longCurrentSystemTime - longNextupdatetime > 0) {
	                //weather data was overdue.
	                if (hourlyNextUpdateTime > 0) {
	                    // in hourly data duration
	                    triggerTime = Math.min(hourlyNextUpdateTime, longSunriseTime);
	                } else {
	                    triggerTime = longSunriseTime;
	                }
	            } else {
	                //check which time is closed to current System Time.
	                if(longSunriseTime >= longNextupdatetime) {
	                    triggerTime = longNextupdatetime;
	                } else {
	                    triggerTime = longSunriseTime;
	                }
	            }
	        }
	        //current time is after sunrise, but not yet sunset time.
	        else if((longCurrentSystemTime > longSunriseTime) && (longCurrentSystemTime < longSunsetTime)) {
//	            if (LOG_FLAG) Log.d(LOG_TAG, "triggerWidgetUpdateTime() - current time is after sunrise, but not yet sunset time.");
	            if(longCurrentSystemTime - longNextupdatetime > 0) {
	                //weather data was overdue.
	                if (hourlyNextUpdateTime > 0) {
	                    // in hourly data duration
	                    triggerTime = Math.min(hourlyNextUpdateTime, longSunsetTime);
	                } else {
	                    triggerTime = longSunsetTime;
	                }
	            } else {
	                //check which time closed to current System Time.
	                if(longSunsetTime >= longNextupdatetime) {
	                    triggerTime = longNextupdatetime;
	                } else {
	                    triggerTime = longSunsetTime;
	                }
	            }
	        }
	        //current time is after sunset time
	        else {
//	            if (LOG_FLAG) Log.d(LOG_TAG, "triggerWidgetUpdateTime() - current time is after sunset time");
	            //TODO:Get next sunrise time
	            String nextSunriseTime = "6:00 AM"; // default value
	            long nextSunriseTimeMillis = 0;
	            if(DateIndex != -1 && ((DateIndex + 1) < mMaxIndex)) {
	                if(fstSunrise != null)
	                    nextSunriseTime = fstSunrise.get(DateIndex + 1); // get the next date sunrise time
	            } else {
	                if(fstSunrise != null && fstSunrise.size() != 0) {
	                    int idx = mMaxIndex - 1;
	                    if (idx < 0) {
	                        idx = fstSunrise.size() - 1;
	                    }
	                    nextSunriseTime = fstSunrise.get(idx); //no the next date sunrise time, so set to the last date sunrise time.
	                }
	            }
	            //re-check SunriseTime String
	            if(!checkStringValid(nextSunriseTime)) nextSunriseTime = "6:00 AM";

	            //Calculate the next day sunrise time.
	            Calendar calNextSunriseTime = Calendar.getInstance(TimeZone.getTimeZone(currentSetTimezone));
	            calNextSunriseTime.setTimeInMillis( specTime2MilliSec(nextSunriseTime, longCurrentSystemTime) + OneDayMilliSec);
	            nextSunriseTimeMillis = calNextSunriseTime.getTimeInMillis();
//	            if (LOG_FLAG) Log.d(LOG_TAG, "next Sunrise Time: " + nextSunriseTime + ", next Sunrise Date: " + formatter1.format(nextSunriseTimeMillis));
	            
	            if(longCurrentSystemTime - longNextupdatetime < 0) {
	                if(longNextupdatetime > nextSunriseTimeMillis) {
	                    triggerTime = nextSunriseTimeMillis;
	                } else {
	                    triggerTime = longNextupdatetime;
	                }
	            } else {
	                //weather data was overdue.
	                if (hourlyNextUpdateTime > 0) {
	                    // in hourly data duration
	                    triggerTime = Math.min(hourlyNextUpdateTime, nextSunriseTimeMillis);
	                } else {
	                    triggerTime = nextSunriseTimeMillis;
	                }
	            }
	        }
//	        if (LOG_FLAG) Log.d(LOG_TAG , "**triggerWidgetUpdateTime() - triggerTimeMillis: " +  triggerTime + ", trigger widget update Time: " + formatter1.format(triggerTime));
	        
	        return triggerTime;
	    }
	    
	    /**
	     * Check Sunrise and Sunset String is valid or not.
	     * @param timeStr input String of Sunrise or Sunset
	     * @return true: valid string , false: has invaild string
	     */
	    private boolean checkStringValid(String ...timeStr) {
	        //null check input
	        if ( timeStr == null || timeStr.length <= 0 ) return false;

	        int strLength = timeStr.length; // the number of input parameter
	        //check input parameter String is Valid or not.
	        for(int idx = 0 ; idx < strLength ; idx ++) {
	            String vaildString = timeStr[idx]; // example: 6:00 AM or 6:00 PM
	// turn off log, it's for dev
//	            if (LOG_FLAG) Log.d(LOG_TAG, "checkStringValid() input parameter: " + vaildString);
	            //Empty string
	            if(TextUtils.isEmpty(vaildString)) {
	                if (LOG_FLAG) Log.w(LOG_TAG, "Empty string");
	                return false; 
	            }
	            //array of split string
	            String[] tempStr = vaildString.split(" ");
	            if(tempStr == null) return false;
	            //case:1
	            if (tempStr.length != 2 ) {
	                if (LOG_FLAG) Log.w(LOG_TAG, "checkStringValid()[case:1] - tempStr.length: " + tempStr.length);
	                return false;
	            }
	            //case:2
	            if ( !tempStr[1].contains("AM") ) {
	                if(!tempStr[1].contains("PM")) {
	                    if (LOG_FLAG) Log.w(LOG_TAG, "checkStringValid()[case:2] - tempStr not contain 'AM' or 'PM'");
	                    return false;
	                }
	            }
	            //case:3
	            String[] tempHM = tempStr[0].split(":"); // check time String
	            if (tempHM.length != 2) {
	                if (LOG_FLAG) Log.w(LOG_TAG, "checkStringValid()[case:3] - tempHM.length: " + tempHM.length );
	                return false;
	            }
	        }
	        //check pass
	        return true;
	    }
	    
	    /**
	     * Convert the sunrise and sunset time to 24hr format
	     * @param strTime input time (format is hh:mm AM/PM)
	     * @return time of 24hr format
	     */
	    private long convertHourMinute(String strTime)
	    {
	        // Sunrise or Sunset --- hh:mm a --- 12:59 AM/PM
	        if (strTime==null || strTime.isEmpty()) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "convertHourMinute() - Invalid strTime - null or empty");
	            return -1;
	        }
	        // Split AM/PM
	        boolean isPM = false;
	        String[] tempTime = strTime.split(" ");
	        if (tempTime.length!=2) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "convertHourMinute() - Invalid tempTime[] - length!=2");
	            return -1;            
	        }        
	        if (tempTime[1].contains("AM")) {
	            isPM = false;
	        }
	        else if (tempTime[1].contains("PM")) {
	            isPM = true;
	        }
	        else {
	            if (LOG_FLAG) Log.w(LOG_TAG, "convertHourMinute() - Invalid tempTime[1] - no AM & PM");
	            return -1;
	        }
	        // Split H:W
	        String[] tempHM = tempTime[0].split(":");
	        if (tempHM.length!=2) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "convertHourMinute() - Invalid tempHW[] - length!=2");
	            return -1;            
	        }       
	        int[] result = new int[2];
	        result[0] = Integer.parseInt(tempHM[0]);
	        result[1] = Integer.parseInt(tempHM[1]);
	         if (isPM) {
	            result[0] += 12;            
	        }
	// turn off log, it's for dev
//	        if (LOG_FLAG) {
//	            Log.d(LOG_TAG, "convertHourMinute() - strTime = " + strTime + " - " + 
//	                           result[0] + ":" + result[1]);
//	        }
	        return getHourMinute(result[0], result[1]);
	    }
	    
	    /**
	     * Chack the weather data is overdue or not.
	     * @param context
	     * @param lastUpdateTime
	     * 
	     * @return true:overdue, false:not overdue
	     */
	    private boolean isOverdue(Context context, long lastUpdateTime)
	    {
	        long interval = 3600000;
	        if (context != null) {
	            interval = WeatherUtility.getAutoSyncFrequency(context);
	        }

	        long current_time = System.currentTimeMillis();
//	        if (LOG_FLAG) Log.d(LOG_TAG, "isOverdue(): " + (((current_time >= lastUpdateTime) && (current_time - lastUpdateTime) < interval) ? "false" : "true"));

	        if ((current_time >= lastUpdateTime) && (current_time - lastUpdateTime) < interval) {
	            return false;
	        } else {
	            return true;
	        }
	    }
	    
	    /**
	     * Conver the specific time to milliseconds
	     * @param timeToConvert
	     * @param longCurrentSystemTime
	     * @return the specific time for milliseconds
	     */
	    private long specTime2MilliSec(String timeToConvert, long longCurrentSystemTime) {
	        //calculate specific time convert to milliseconds
	        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(currentSetTimezone));
	        calendar.setTimeInMillis(longCurrentSystemTime);
	        //parse hour and minute [0]:hour, [1]:minute
	        int [] arrParseHourMinute = parseHourMinute(timeToConvert);
	        //reset current time to specific Time 
	        calendar.set(Calendar.HOUR_OF_DAY, arrParseHourMinute[0]);
	        calendar.set(Calendar.MINUTE, arrParseHourMinute[1]);
	        calendar.set(Calendar.SECOND, 0);
	        calendar.set(Calendar.MILLISECOND, 0);

	        return calendar.getTimeInMillis();
	    }

	    /**
	     * When the hour is overdue, check data and send the offset for hourly forecast.
	     * @param currentTimeInMillis
	     * @return the index of hourly forecast data to receive
	     */
	    private int beforeHour(long currentTimeInMillis) {
	        if (hourEpochDateTime == null || hourEpochDateTime.size() <= 0) {
	            return -1;
	        }

	        for (int i = 0; i < hourEpochDateTime.size(); i++) {
	            final long hour = safe_parseLong(hourEpochDateTime.get(i), 0) * 1000;
	            if (currentTimeInMillis >= hour && currentTimeInMillis < hour + ONE_HOUR_MILLIS) {
	                return i;
	            }
	        }

	        return -1;
	    }
	    
	    /**
	     * When the data is overdue, check data and send the offset for forcast.
	     * @param date
	     * @param timezoneId
	     * @return the index of forcast data to receive
	     */
        private int beforeToday(String date, String timezoneId)
        {
            try {
                Calendar cityTime = new GregorianCalendar(TimeZone.getTimeZone(timezoneId));
                cityTime.setTimeInMillis(System.currentTimeMillis());

                if (LOG_FLAG) {
                    Log.i(LOG_TAG,
                            "beforeToday() - now is " + cityTime.get(Calendar.HOUR_OF_DAY) + "h, "
                                    + cityTime.get(Calendar.MINUTE) + "m, "
                                    + cityTime.get(Calendar.SECOND) + "s, timezone = " + timezoneId);
                }

                long t = getDateMillisecond(date, cityTime);
                long tn = System.currentTimeMillis();
                long r = ((((cityTime.get(Calendar.HOUR_OF_DAY) * 60) + cityTime
                        .get(Calendar.MINUTE)) * 60) + cityTime.get(Calendar.SECOND)) * 1000;

                long today = tn - r;
                int daysBetween = (int) ((today - t) / (ONE_DAY_MILLIS));
                if (daysBetween < 0)
                    daysBetween = 0;
                // TODO
                // [NOTE]
                // To avoid the data size inconsistent issue. {
                if (daysBetween > mMaxIndex) {
                    daysBetween = mMaxIndex;
                }
                // }
                return daysBetween;
            } catch (Exception e) {
                if (LOG_FLAG) {
                    e.printStackTrace();
                    if (LOG_FLAG)
                        Log.w(LOG_TAG, "beforeToday() - some error in compare2Today with " + date);
                }
            }
            return 0;
        }
	    
	    /**
	     * get the time status number --> 0:STATUS_DAWN, 1:STATUS_DAY, 2:STATUS_NIGHT
	     * @param time time of now
	     * @param sunrise time of sunrise
	     * @param sunset time of sunset
	     * @return time status number
	     */
	    private int getTimeStatus(long time, long sunrise, long sunset)
	    {
	        int status = STATUS_DAWN;
	        if (sunrise<0 && sunset<0) {
	            sunrise = DEFAULT_SUNRISE;
	            sunset  = DEFAULT_SUNSET;
	        } 
	        if (sunrise<0) {
	            // Polar Night
	            status = STATUS_NIGHT;
	        }
	        else if (sunset<0) {
	            // Polar Day
	            status = STATUS_DAY;
	        }
	        else {
	            if (sunrise<sunset) {
	                if (time<sunrise)
	                    status = STATUS_DAWN;
	                else if (time>=sunrise && time<sunset) 
	                    status = STATUS_DAY;
	                else
	                    status = STATUS_NIGHT;
	            }
	            else {
	                if (time<sunset)
	                    status = STATUS_DAY;
	                else if (time>=sunset && time<sunrise)
	                    status = STATUS_NIGHT;
	                else 
	                    status = STATUS_DAY;
	            }                    
	        }
	// turn off log, it's for dev
//	        if (LOG_FLAG) {
//	            Log.d(LOG_TAG, "getTimeStatus() - time = " + time + 
//	                           ", sunrise = " + sunrise + 
//	                           ", sunset = "  + sunset  + 
//	                           ", status = "  + status);
//	        }
	        return status;
	    }
	    
	    /**
	     * convert the condition id to the currect condtionId for day and night 
	     * @param isDay
	     * @param inputID
	     * @return result conditionID
	     */
	    private static int convertConditionID(int isDay, int inputID)
	    {
	        int resultID = inputID;
	        int statusCurID = getConditionStatus(inputID);     
	        if ((statusCurID == DAY && isDay != 1) || (statusCurID == NIGHT && isDay == 1)) {
	            if (inputID < CONDITION_CONVERT.length)
	                resultID = CONDITION_CONVERT[inputID];
	        }
	// turn off log, it's for dev
//	        if (LOG_FLAG) Log.i(LOG_TAG, "convertConditionID() - isDay = " + isDay + ", inputID = "  + inputID + ", resultID = " + resultID);
	        return resultID;
	    }
	    
	    /**
	     * Using condition ID to get the condition status 
	     * @param conditionID
	     * @return condition status
	     */
	    private static int getConditionStatus(int conditionID) 
	    {
	        if (conditionID < CONDITION_STATUS.length) {
//	          if (LOG_FLAG) Log.d(LOG_TAG, "getConditionStatus() - CONDITION_STATUS[" + conditionID + "] = " + CONDITION_STATUS[conditionID]);
	            
	            return CONDITION_STATUS[conditionID];
	        }
	        return 0;
	    } 
	    
	    /**
	     * utility of calcauting hour and minute
	     * @param hour hour
	     * @param minute minute
	     * @return total minutes
	     */
	    private long getHourMinute(int hour, int minute)
	    {
	        return hour*100 + minute;
	    }
	    
	    /**
	     * safe parse String to Integer 
	     * @param str input string
	     * @return String value and its type is Integer
	     */
	    private static int safe_parseInt(String str) 
	    {
	        int val = 0;
	        if (str == null)
	            return 0;
	        try {
	            val = Integer.parseInt(str);
	        } catch (java.lang.NumberFormatException e) {
	            if (LOG_FLAG) Log.i(LOG_TAG, "safe_parseInt() - Exception = " + e);
	        }
	        return val;
	    }

	    /**
	     * safe parse String to Long
	     * @param str input string
	     * @param defVal default value if parse fail
	     * @return String value and its type is Long
	     */
	    private static long safe_parseLong(String str, long defVal)
	    {
	        if (str == null) {
	            return defVal;
	        }
	        try {
	            return Long.parseLong(str);
	        } catch (java.lang.NumberFormatException e) {
	            if (LOG_FLAG) Log.i(LOG_TAG, "safe_parseLong() - Exception = " + e);
	            return defVal;
	        }
	    }
	    
	    /**
	     * parse HourMinute
	     * @param strTime
	     * @return result 
	     */
	    private int[] parseHourMinute(String strTime)
	    {
	        //input paremeter null check
	        if (TextUtils.isEmpty(strTime)) {
	            if (LOG_FLAG) Log.w(LOG_TAG, "parseHourMinute() - parameter of strTime is empty");
	            return new int[]{12, 0};
	        }

	        // Split AM/PM
	        boolean isPM = false;
	        String[] tempTime = strTime.split(" ");
	        if (tempTime[1].contains("AM")) {
	            isPM = false;
	        } else if (tempTime[1].contains("PM")) {
	            isPM = true;
	        }

	        // Split H:W
	        String[] tempHM = tempTime[0].split(":");
	        //parse result
	        int[] result = new int[2];
	        result[0] = Integer.parseInt(tempHM[0]);
	        result[1] = Integer.parseInt(tempHM[1]);
	        //change to 24hr
	        if (isPM) {
	            result[0] += 12;
	        }
	// turn off log, it's for dev
//	        if (LOG_FLAG) Log.d(LOG_TAG, "parseHourMinute() - strTime = " + strTime +" => "+ result[0] + ":" + result[1]);

	        return result;
	    }
	    
	    /**
	     * Get time object
	     * @param date
	     * @param timezoneId
	     * @return time object
	     */
        private long getDateMillisecond(String date, Calendar calendar)
        {
            int y = 1900;
            int m = 1;
            int d = 1;
            ArrayList<String> keywords = new ArrayList<String>();

            for (String k : date.split("/")) {
                if (!k.equals("")) {
                    keywords.add(k);
                }
            }
            try {
                if (keywords.size() > 0)
                    m = Integer.parseInt(keywords.get(0));
                if (keywords.size() > 1)
                    d = Integer.parseInt(keywords.get(1));
                if (keywords.size() > 2)
                    y = Integer.parseInt(keywords.get(2));
            } catch (Exception e) {
                // Format is error
                if (LOG_FLAG)
                    Log.w(LOG_TAG, "getTimeObject() - The format of date is not mm/dd/yy..." + date);
            }
            keywords.clear();

            Calendar c = (Calendar) calendar.clone();
            c.clear();
            c.set(y, m - 1, d);
            return c.getTimeInMillis();
        }
	    
	    /**
	     * Transfer mile to kilometer
	     * @param m mile
	     * @return kilometer
	     */
	    private String mileToKilo(String m){
	        int mi = 0;
	        String km;
	        try{
	            mi = Integer.parseInt(m);
	        }catch(Exception e){}
	        
	        km = String.valueOf((int)(mi * 1.609347));
	        return km;
	    }
	    
	    /**
	     * overwrite toString()
	     * @return type_param1_param2
	     */
	    public String toString() {
	        return type + "_" + param1 + "_" + param2;
	    }

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
	        public WeatherData createFromParcel(Parcel in) {
	            return new WeatherData(in);
	        }

	        public WeatherData[] newArray(int size) {
	            return new WeatherData[size];
	        }
	    };

		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeInt(type);
	        out.writeString(param1);
	        out.writeString(param2);
	        out.writeLong(lastUpdate);
	        out.writeInt(curTempC);
	        out.writeInt(curTempF);
	        out.writeString(curConditionId);
	        out.writeStringList(fstName);
	        out.writeStringList(fstDate);
	        out.writeStringList(fstConditionId);
	        out.writeStringList(fstHighTempC);
	        out.writeStringList(fstHighTempF);
	        out.writeStringList(fstLowTempC);
	        out.writeStringList(fstLowTempF);
	        out.writeString(curLocLat);
	        out.writeString(curLocLng);
	        out.writeString(curLocLatTrim);
	        out.writeString(curLocLngTrim);
	        out.writeString(curLocName);
	        out.writeString(curLocState);
	        out.writeString(curLocCountry);
	        out.writeString(curLocTimezoneId);
	        
	        out.writeString(cityLocalTime);
	        out.writeString(cityLatitude);
	        out.writeString(cityLongitude);
	        out.writeString(cityTimeZone);
	        out.writeString(cityWebURL);
	        out.writeString(dayLightFlag);
	        out.writeInt(curFeelTempC);
	        out.writeInt(curFeelTempF);
	        out.writeString(curHumidity);
	        out.writeString(curWinddirection);
	        out.writeString(curWindspeedMI);
	        out.writeString(curVisibilityMI);
	        out.writeString(curWindspeedKM);
	        out.writeString(curVisibilityKM);
	        out.writeStringList(fstSunrise);
	        out.writeStringList(fstSunset);
	        out.writeStringList(fstFeelHighTempC);
	        out.writeStringList(fstFeelHighTempF);
	        out.writeStringList(fstFeelLowTempC);
	        out.writeStringList(fstFeelLowTempF);
	        out.writeStringList(fstNightFeelHighTempC);
	        out.writeStringList(fstNightFeelHighTempF);
	        out.writeStringList(fstNightFeelLowTempC);
	        out.writeStringList(fstNightFeelLowTempF);
	        out.writeStringList(fstNightConditionId);
	        out.writeStringList(fstNightHighTempC);
	        out.writeStringList(fstNightHighTempF);
	        out.writeStringList(fstNightLowTempC);
	        out.writeStringList(fstNightLowTempF);
	        out.writeStringList(fstPrecip);
	        out.writeStringList(fstNightPrecip);
	        out.writeStringList(hourName);
	        out.writeStringList(hourConditionId);
	        out.writeStringList(hourTempC);
	        out.writeStringList(hourTempF);
	        out.writeStringList(hourFeelTempC);
	        out.writeStringList(hourFeelTempF);
	        out.writeStringList(hourPrecip);
	        
	        out.writeString(timeZoneAbbreviation);
	        out.writeString(currentSetTimezone);
	        
	        out.writeStringList(hourEpochDateTime);
	        out.writeStringList(hourWebURL);
	        out.writeStringList(fstWebURL);
	        
	        out.writeString(pm25);
	        out.writeStringList(airQualityDaily);
		}
		
		protected WeatherData(Parcel in) {
			type = in.readInt();
	        param1 = in.readString();
	        param2 = in.readString();
	        lastUpdate = in.readLong();
	        curTempC = in.readInt();
	        curTempF = in.readInt();
	        curConditionId = in.readString();
	        in.readStringList(fstName);
	        in.readStringList(fstDate);
	        in.readStringList(fstConditionId);
	        in.readStringList(fstHighTempC);
	        in.readStringList(fstHighTempF);
	        in.readStringList(fstLowTempC);
	        in.readStringList(fstLowTempF);
	        curLocLat = in.readString();
	        curLocLng = in.readString();
	        curLocLatTrim = in.readString();
	        curLocLngTrim = in.readString();
	        curLocName = in.readString();
	        curLocState = in.readString();
	        curLocCountry = in.readString();
	        curLocTimezoneId = in.readString();
	        
	        cityLocalTime = in.readString();
	        cityLatitude = in.readString();
	        cityLongitude = in.readString();
	        cityTimeZone = in.readString();
	        cityWebURL = in.readString();
	        dayLightFlag = in.readString();
	        curFeelTempC = in.readInt();
	        curFeelTempF = in.readInt();
	        curHumidity = in.readString();
	        curWinddirection = in.readString();
	        curWindspeedMI = in.readString();
	        curVisibilityMI = in.readString();
	        curWindspeedKM = in.readString();
	        curVisibilityKM = in.readString();
	        in.readStringList(fstSunrise);
	        in.readStringList(fstSunset);
	        in.readStringList(fstFeelHighTempC);
	        in.readStringList(fstFeelHighTempF);
	        in.readStringList(fstFeelLowTempC);
	        in.readStringList(fstFeelLowTempF);
	        in.readStringList(fstNightFeelHighTempC);
	        in.readStringList(fstNightFeelHighTempF);
	        in.readStringList(fstNightFeelLowTempC);
	        in.readStringList(fstNightFeelLowTempF);
	        in.readStringList(fstNightConditionId);
	        in.readStringList(fstNightHighTempC);
	        in.readStringList(fstNightHighTempF);
	        in.readStringList(fstNightLowTempC);
	        in.readStringList(fstNightLowTempF);
	        in.readStringList(fstPrecip);
	        in.readStringList(fstNightPrecip);
	        in.readStringList(hourName);
	        in.readStringList(hourConditionId);
	        in.readStringList(hourTempC);
	        in.readStringList(hourTempF);
	        in.readStringList(hourFeelTempC);
	        in.readStringList(hourFeelTempF);
	        in.readStringList(hourPrecip);
	        
	        timeZoneAbbreviation = in.readString();
	        currentSetTimezone = in.readString();
	        
            try {
                if (in.dataAvail() > 0) {
                    in.readStringList(hourEpochDateTime);
                    in.readStringList(hourWebURL);
                    in.readStringList(fstWebURL);
                    pm25 = in.readString();
                    in.readStringList(airQualityDaily);
                }
	        } catch (Exception e) {
	            if (LOG_FLAG) Log.e(LOG_TAG, "construct weather data from parcel exception, " + e.getMessage());
	        }
	        
	        //check the max availableIndex
	        checkMaxAvailableIndex();
	    }
	}
	
	/**
	 * Weather Location Extension information
	 * 
	 * @deprecated [Module internal use]
	 */
	/**@hide*/ 
	private static class WeatherLocationExtInfo {
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
		private boolean hasEngInfo = false;
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
		private String systemLangName = "";
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
		private String systemLangState = "";
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
		private String systemLangCountry = "";
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
		private String engName = "";
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
		private String engState = "";
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
		private String engCountry = "";		
	}

    /**
     * Query is location list database ready
     *
     * @param context
     * @return location list is ready or not
     */
    public static boolean isLocationListDBReady(Context context) {
        try {
            Bundle bundle = context.getContentResolver().call(WeatherConsts.CONTENT_URI, WeatherConsts.METHOD_LOCATION_LIST_EXIST, null, null);
            return bundle != null ? bundle.getBoolean(WeatherConsts.KEY_LOCATION_LIST_EXIST, false) : false;
        } catch (Exception e) {
            if (LOG_FLAG) Log.e(LOG_TAG, "isLocationListDBReady() exception: " + e);
        }
        return false;
    }

    /**
     * Enable or disable auto download location list database.
     * Download will not be triggered immediately, instead it will download when actually need list database.
     * If want to trigger download immediately, should call {@link #downloadLocationListDB(Context)
     *
     * @param context
     * @param enabled
     */
    public static void setEnableDownloadLocationListDB(Context context, boolean enabled) {
        try {
            context.getContentResolver().call(WeatherConsts.CONTENT_URI, WeatherConsts.METHOD_ENABLE_DOWNLOAD_LOCATION_LIST, enabled? Boolean.TRUE.toString(): Boolean.FALSE.toString(), null);
        } catch (Exception e) {
            if (LOG_FLAG) Log.e(LOG_TAG, "setEnableDownloadLocationListDB() exception: " + e);
        }
    }

    /**
     * Trigger to download location list database.
     * It will also enable auto download as calling {@link #setEnableDownloadLocationListDB(Context, boolean)}.
     *
     * @param context
     * @See #setEnableDownloadLocationListDB(Context, boolean)
     */
    public static void downloadLocationListDB(Context context) {
        try {
            context.getContentResolver().call(WeatherConsts.CONTENT_URI, WeatherConsts.METHOD_DOWNLOAD_LOCATION_LIST, null, null);
        } catch (Exception e) {
            if (LOG_FLAG) Log.e(LOG_TAG, "downloadLocationListDB() exception: " + e);
        }
    }

    /**
     * Register download location list database listener
     *
     * @param context
     * @param listener
     */
    public static void registerDownloadLocationListDBListener(Context context, DownloadLocationListDBListener listener) {
        if (listener == null) {
            return;
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable(WeatherConsts.KEY_MESSENGER, listener.getMessenger());
            context.getContentResolver().call(WeatherConsts.CONTENT_URI, WeatherConsts.METHOD_REGISTER_LISTENER, null, bundle);
        } catch (Exception e) {
            if (LOG_FLAG) Log.e(LOG_TAG, "registerDownloadLocationListDBListener() exception: " + e);
        }
    }

    /**
     * Unregister download location list database listener
     *
     * @param context
     * @param listener
     */
    public static void unregisterDownloadLocationListDBListener(Context context, DownloadLocationListDBListener listener) {
        if (listener == null) {
            return;
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable(WeatherConsts.KEY_MESSENGER, listener.getMessenger());
            context.getContentResolver().call(WeatherConsts.CONTENT_URI, WeatherConsts.METHOD_UNREGISTER_LISTENER, null, bundle);
        } catch (Exception e) {
            if (LOG_FLAG) Log.e(LOG_TAG, "unregisterDownloadLocationListDBListener() exception: " + e);
        }
    }

    private static float getWeatherProviderMigrate(Context context, String packageName) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return info.metaData.getFloat("weather_sync_provider_migrate");
        } catch (Exception e) {
            if (LOG_FLAG) Log.e(LOG_TAG, "getWeatherProviderMigrate() exception: " + e.getMessage());
            return 0;
        }
    }

    private static boolean isWeatherProviderMigrate(Context context, String packageName) {

        // from Android N, migrate should always happen
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            return true;
        }

        return getWeatherProviderMigrate(context, packageName) > 0.0f;
    }

    public static boolean isWeatherAppMigrate(Context context) {
        return isWeatherProviderMigrate(context, WeatherConsts.WEATHER_APP_PACKAGE);
    }

    public static boolean isHSPMigrate(Context context) {
        return isWeatherProviderMigrate(context, WeatherConsts.HSP_PACKAGE);
    }

    static public String getTargetPackageName() {
        return WeatherConsts.TARGET_PACKAGE_NAME;
    }
}
