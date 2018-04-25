package com.htc.lib2.locationservicessetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib2.locationservicessetting.object.AddressInfo;
import com.htc.lib2.locationservicessetting.object.LocationInfo;
import com.htc.lib2.locationservicessetting.object.WiFiInfo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class HtcLocationServiceClient {
	public static final boolean DEBUG_LOG_ENABLED = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
	private static final String TAG = "[LocationSvClient]";
	
	private static final String AUTHORITY = "com.htc.locationservicessettingprovider.provider";
	public static final String CUSTOM_AUTHORITY_PREFIX = BuildConfig.CUSTOM_AUTHORITY_PREFIX;
	public static final String AUTHORITYINPRISM = CUSTOM_AUTHORITY_PREFIX + "com.htc.locationservicessettingprovider.provider";

	private static final String INTENT = "com.htc.sense.hsp.locationservicessettingprovider.ui.LOCATIONSERVICEACTIVITY";
	private static final String URI_HOME_ADDRESS = "home_address";
	private static final String URI_WORK_ADDRESS = "work_address";
	private static final String URI_TABLE_ADDRESS = "table_address";
	private static final String URI_TABLE_WIFI = "table_wifi";
	private static final String URI_LAST_LOCALE = "last_locale";
	private static final String URI_UNDEFINED = "undefined";
	private static final String[] COLUMNS = new String[]{"address", "latitude", "longitude", "mode"};
	private static final String[] ADDRESS_COLUMNS = new String[]{"_id", "address", "latitude", "longitude", "manual_mode", "contextual_mode"};
	private static final String[] WIFI_COLUMNS = new String[]{"_id", "ssid", "bssid", "contextual_mode"};
	private static final String[] LOCALE_COLUMN = new String[]{"locale"};
	
	// Const value of max latitude and longitude
	private static final double CONSTANT_MAX_LATITUDE_E = 90.0;
	private static final double CONSTANT_MAX_LATITUDE_W = -90.0;
	private static final double CONSTANT_MAX_LONGITUDE_N = 180.0;
	private static final double CONSTANT_MAX_LONGITUDE_S = -180.0;

	public static final int CODE_ALL_ADDRESS = 0;
	public static final int CODE_HOME_ADDRESS = 1;
	public static final int CODE_WORK_ADDRESS = 2;

	public static enum UPDATE_ADDRESS_STATUS {
		FAIL,
		SUCCESS,
		INVALID_GEOCODE,
		ADDRESS_DUPLICATE_SAME_MODE,
		ADDRESS_DUPLICATE_OTHER_MODE
	}
	/**
	 * Set last locale to provider
	 * @param context
	 * @param locale
	 * @return true for set locale success; otherwise false.
	 */
	public static boolean setLastLocale(Context context, String locale) {
		boolean result = false;
		if (context == null) {
            return result;
        }		

		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri = Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_LAST_LOCALE);
		else
			uri = Uri.parse("content://" + AUTHORITY + "/" + URI_LAST_LOCALE);		

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        
        ContentValues updateValues = new ContentValues();
        try {
            if (updateValues != null) {
                updateValues.put(LOCALE_COLUMN[0], locale);
            }
            if (resolver.update(uri, updateValues, null, null)<0) {
                Log.w(TAG, "setLastLocale update fail");
                return result;
            }
            else {
            	result = true;
            }
        } catch (Exception e) {
            Log.w(TAG, "saveLoc E:" + e);
            return result;
        }
        return result;
	}
	/**
	 * Get last locale from provider
	 * @param context
	 * @return locale string
	 */
	public static String getLastLocale(Context context){
		String lastLocale = "";	
		ContentResolver resolver =
                (context != null)? context.getContentResolver():null;
        if (resolver == null) {
            Log.w(TAG, "getLastLocale Fail");
            return "";
        }

		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri = Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_LAST_LOCALE);
		else
			uri = Uri.parse("content://" + AUTHORITY + "/" + URI_LAST_LOCALE);
		
		Cursor cursor = null;
		try {
			cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {				
				int index = cursor.getColumnIndex(LOCALE_COLUMN[0]);				
				if (index >= 0) {
					lastLocale = cursor.getString(0);
				}
			} else {
				Log.i(TAG, "cursor is null");
			}
		} catch (Exception e) {
			Log.w(TAG, "getLoc E: " + e);
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception e) {
					Log.w(TAG, "getLoc E: " + e);
				}
			}
		}
		return lastLocale;
	}
	/**
	 * Used in Sense70. Return LocationInfo stored in HSP.
	 * @param context caller context
	 * @param which the request code (Home\work)
	 * @return LocationInfo stored in HSP
	 */
	public static LocationInfo getLocationInfo(Context context, int which) {
		LocationInfo locationInfo = new LocationInfo();
		
		ContentResolver resolver =
                (context != null)? context.getContentResolver():null;
        if (resolver == null) {
            Log.w(TAG, "getLoc Fail");
            return locationInfo;
        }
	    String path = "";
	    // By suggest, use switch case to handle undefined address type
	    switch(which){
	    case CODE_HOME_ADDRESS:
	    	path = URI_HOME_ADDRESS;
	    	break;
	    case CODE_WORK_ADDRESS:
	    	path = URI_WORK_ADDRESS;
	    	break;
	    default:
	    	path = URI_UNDEFINED;
	    	break;
	    }	  
	    // By suggest, return undefined data when address is undefined 
	    if(path.equals(URI_UNDEFINED)){
			Log.w(TAG, "no match address type");
			return locationInfo;
		}

		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri = Uri.parse("content://" + AUTHORITYINPRISM + "/" + path);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + path);
		
		Cursor cursor = null;
		try {
			cursor = resolver.query(uri, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndex(COLUMNS[0]);
				if (index >= 0) {
					locationInfo.address = cursor.getString(0);
				}
				index = cursor.getColumnIndex(COLUMNS[1]);
				if (index >= 0) {
					locationInfo.latitude = Double.longBitsToDouble(cursor.getLong(index));
				}
				index = cursor.getColumnIndex(COLUMNS[2]);
				if (index >= 0) {
					locationInfo.longitude = Double.longBitsToDouble(cursor.getLong(index));
				}
				index = cursor.getColumnIndex(COLUMNS[3]);
				if (index >= 0) {
					locationInfo.mode = Double.longBitsToDouble(cursor.getLong(index));
				}
				locationInfo.isdatadefined = true;
			} else {
				Log.i(TAG, "cursor is null");
			}
		} catch (Exception e) {
			Log.w(TAG, "getLoc E: " + e);
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception e) {
					Log.w(TAG, "getLoc E: " + e);
				}
			}
		}
		
		return locationInfo;
	}
	
	/**
	 * Used in Sense70. Store LocationInfo to HSP.
	 * @param context caller context
	 * @param locationInfo LocationInfo store to HSP
	 * @param which the request code (Home\work)
	 * @return true storing LocationInfo to HSP successfully, false is failed
	 */
	public static boolean setLocationInfo(Context context, LocationInfo locationInfo, int which) {
		boolean result = false;
		if (context == null) {
            return result;
        }
		else if (false == IsValidLatitude(locationInfo.latitude)){
			// By suggest, check latitude range and return if it's invalid 
			Log.w(TAG, "invalid latitude");
			return result;
		}
		else if (false == IsValidLongitude(locationInfo.longitude)){
			// By suggest, check longitude range and return if it's invalid
			Log.w(TAG, "invalid longitude");
			return result;
		}
		String path = "";
		// By suggest, use switch case to handle undefined address type
		switch(which){
	    case CODE_HOME_ADDRESS:
	    	path = URI_HOME_ADDRESS;
	    	break;
	    case CODE_WORK_ADDRESS:
	    	path = URI_WORK_ADDRESS;
	    	break;
	    default:
	    	path = URI_UNDEFINED;
	    	break;
	    }
		if(path.equals(URI_UNDEFINED)){
			Log.w(TAG, "no match address type");
			return result;
		}

		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =  Uri.parse("content://" + AUTHORITYINPRISM + "/" + path);
		else
			uri = Uri.parse("content://" + AUTHORITY + "/" + path);
		
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        
        ContentValues updateValues = new ContentValues();
        try {
            if (updateValues != null) {
                updateValues.put(COLUMNS[0], locationInfo.address);
                updateValues.put(COLUMNS[1], Double.doubleToRawLongBits(locationInfo.latitude));
                updateValues.put(COLUMNS[2], Double.doubleToRawLongBits(locationInfo.longitude));
                updateValues.put(COLUMNS[3], Double.doubleToRawLongBits(locationInfo.mode));
            }
            if (resolver.update(uri, updateValues, null, null)<0) {
                Log.w(TAG, "saveLoc update fail");
                return result;
            }
            else {
            	result = true;
            }
        } catch (Exception e) {
            Log.w(TAG, "saveLoc E:" + e);
            return result;
        }
        return result;
	}
	
	/**
	 * Used in Sense71. Get AddressInfo list from HSP.
	 * @param context caller context
	 * @param which the request code (Home\work)
	 * @return a AddressInfo List from HSP
	 */
	public static List<AddressInfo> queryAddressInfo(Context context, int which) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[queryAddressInfo] query from client");
		
		List<AddressInfo> listLocation = new ArrayList<AddressInfo>();
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_ADDRESS);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_ADDRESS);
		
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
        	Log.w(TAG, "[queryAddressInfo] resolver is null");
            return listLocation;
        }
        Cursor cursor = null;
        try {
        	if (CODE_HOME_ADDRESS == which || CODE_WORK_ADDRESS == which) {
	        	String[] whereArgs = new String[] {Integer.toString(which)};
	        	cursor = resolver.query(uri, null, ADDRESS_COLUMNS[5]+"=?", whereArgs, null);
        	}
        	else {
        		cursor = resolver.query(uri, null, null, null, null);
        	}
        	
        	if (cursor != null) {
        		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[queryAddressInfo] Cursor count = " + cursor.getCount());
        		
        		for (cursor.moveToFirst(); !cursor.isAfterLast() ; cursor.moveToNext()) {
        			int _id = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[0]));
        			String address = cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMNS[1]));
        			double latitude = cursor.getFloat(cursor.getColumnIndex(ADDRESS_COLUMNS[2]));
        			double longitude = cursor.getFloat(cursor.getColumnIndex(ADDRESS_COLUMNS[3]));
        			double mode = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[4]));
        			int contextualMode = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[5]));
        			
        			listLocation.add(new AddressInfo(_id, address, latitude, longitude, mode, contextualMode));
        		}
        	}
        	else {
        		Log.i(TAG, "[queryAddressInfo] cursor is null");
        	}
        	
        } catch(Exception e) {
        	Log.w(TAG, "[queryAddressInfo] E:" + e);
            return listLocation;
        } finally{
        	if(null!=cursor)
        		cursor.close();
        }
		
		return listLocation;
	}
	
	/**
	 * Used for Migration. Get AddressInfo list from HSP.
	 * @param context caller context
	 * @param which the request code (Home\work)
	 * @return a AddressInfo List from HSP
	 */
	private static List<AddressInfo> queryAddressInfo_Migrate(Context context, int which) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[queryAddressInfo_Migrate] query from client");

		List<AddressInfo> listLocation = new ArrayList<AddressInfo>();

		final Uri uri = Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_ADDRESS);
		ContentResolver resolver = context.getContentResolver();
		if (resolver == null) {
			Log.w(TAG, "[queryAddressInfo_Migrate] resolver is null");
			return listLocation;
		}
		Cursor cursor = null;
		try {
			if (CODE_HOME_ADDRESS == which || CODE_WORK_ADDRESS == which) {
				String[] whereArgs = new String[] {Integer.toString(which)};
				cursor = resolver.query(uri, null, ADDRESS_COLUMNS[5]+"=?", whereArgs, null);
			}
			else {
				cursor = resolver.query(uri, null, null, null, null);
			}

			if (cursor != null) {
				if (DEBUG_LOG_ENABLED) Log.d(TAG, "[queryAddressInfo_Migrate] Cursor count = " + cursor.getCount());

				for (cursor.moveToFirst(); !cursor.isAfterLast() ; cursor.moveToNext()) {
					int _id = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[0]));
					String address = cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMNS[1]));
					double latitude = cursor.getFloat(cursor.getColumnIndex(ADDRESS_COLUMNS[2]));
					double longitude = cursor.getFloat(cursor.getColumnIndex(ADDRESS_COLUMNS[3]));
					double mode = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[4]));
					int contextualMode = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[5]));

					listLocation.add(new AddressInfo(_id, address, latitude, longitude, mode, contextualMode));
				}
			}
			else {
				Log.i(TAG, "[queryAddressInfo_Migrate] cursor is null");
			}

		} catch(Exception e) {
			Log.w(TAG, "[queryAddressInfo_Migrate] E:" + e);
			return listLocation;
		} finally{
			if(null!=cursor)
				cursor.close();
		}

		return listLocation;
	}

	/**
	 * Used in Sense71. Insert AddressInfo to HSP.
	 * @param context caller context
	 * @param addressInfo insert this address to HSP
	 * @param forceUpdate if duplicate address exist, the inserted address will replace original one
	 * @return true storing AddressInfo to HSP successfully, false is failed
	 */
	public static UPDATE_ADDRESS_STATUS insertAddressInfo(Context context, AddressInfo addressInfo, boolean forceUpdate) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[insertAddressInfo] insert from client");
		
		UPDATE_ADDRESS_STATUS result = UPDATE_ADDRESS_STATUS.FAIL;
		if (context == null) {
            return result;
        }
		else if (false == IsValidLatitude(addressInfo.latitude)){
			// By suggest, check latitude range and return if it's invalid 
			Log.w(TAG, "[insertAddressInfo] invalid latitude");
			result = UPDATE_ADDRESS_STATUS.INVALID_GEOCODE;
			return result;
		}
		else if (false == IsValidLongitude(addressInfo.longitude)){
			// By suggest, check longitude range and return if it's invalid
			Log.w(TAG, "[insertAddressInfo] invalid longitude");
			result = UPDATE_ADDRESS_STATUS.INVALID_GEOCODE;
			return result;
		}
		
		AddressInfo duplicateAddress = IsAddressExist(context, addressInfo);
		if (duplicateAddress != null) {
			Log.w(TAG, "[insertAddressInfo] address existed");
			
			if (false == forceUpdate) {
				if (duplicateAddress.contextualMode == addressInfo.contextualMode) {
					result = UPDATE_ADDRESS_STATUS.ADDRESS_DUPLICATE_SAME_MODE;
				}
				else {
					result = UPDATE_ADDRESS_STATUS.ADDRESS_DUPLICATE_OTHER_MODE;
				}
				
				return result;
			}
			else {
				deleteAddressInfo(context, duplicateAddress._id);
			}
		}
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_ADDRESS);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_ADDRESS);

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        
        ContentValues updateValues = new ContentValues();
        try {
            if (updateValues != null) {
                updateValues.put(ADDRESS_COLUMNS[1], addressInfo.address);
                updateValues.put(ADDRESS_COLUMNS[2], addressInfo.latitude);
                updateValues.put(ADDRESS_COLUMNS[3], addressInfo.longitude);
                updateValues.put(ADDRESS_COLUMNS[4], addressInfo.mode);
                updateValues.put(ADDRESS_COLUMNS[5], addressInfo.contextualMode);
            }
            
            if (null != resolver.insert(uri, updateValues)) {
            	result = UPDATE_ADDRESS_STATUS.SUCCESS;
            }
        } catch (Exception e) {
            Log.w(TAG, "[insertAddressInfo] E:" + e);
            return result;
        }
		
		return result;
	}

	/**
	 * Used for Migration. Insert AddressInfo to LocationService Provider of Prism.
	 * @param context caller context
	 * @param addressInfo insert this address to  LocationService Provider of Prism
	 * @param forceUpdate if duplicate address exist, the inserted address will replace original one
	 * @return true storing AddressInfo to LocationService Provider of Prism successfully, false is failed
	 */
	private static UPDATE_ADDRESS_STATUS insertAddressInfo_Migrate(Context context, AddressInfo addressInfo, boolean forceUpdate) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[insertAddressInfo_Migrate] insert from client");

		UPDATE_ADDRESS_STATUS result = UPDATE_ADDRESS_STATUS.FAIL;
		if (context == null) {
			return result;
		}
		else if (false == IsValidLatitude(addressInfo.latitude)){
			// By suggest, check latitude range and return if it's invalid
			Log.w(TAG, "[insertAddressInfo_Migrate] invalid latitude");
			result = UPDATE_ADDRESS_STATUS.INVALID_GEOCODE;
			return result;
		}
		else if (false == IsValidLongitude(addressInfo.longitude)){
			// By suggest, check longitude range and return if it's invalid
			Log.w(TAG, "[insertAddressInfo_Migrate] invalid longitude");
			result = UPDATE_ADDRESS_STATUS.INVALID_GEOCODE;
			return result;
		}

		AddressInfo duplicateAddress = IsAddressExist_Migrate(context, addressInfo);
		if (duplicateAddress != null) {
			Log.w(TAG, "[insertAddressInfo_Migrate] address existed");

			if (false == forceUpdate) {
				if (duplicateAddress.contextualMode == addressInfo.contextualMode) {
					result = UPDATE_ADDRESS_STATUS.ADDRESS_DUPLICATE_SAME_MODE;
				}
				else {
					result = UPDATE_ADDRESS_STATUS.ADDRESS_DUPLICATE_OTHER_MODE;
				}

				return result;
			}
			else {
				deleteAddressInfo(context, duplicateAddress._id);
			}
		}

		final Uri uri = Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_ADDRESS);
		ContentResolver resolver = context.getContentResolver();
		if (resolver == null) {
			return result;
		}

		ContentValues updateValues = new ContentValues();
		try {
			if (updateValues != null) {
				updateValues.put(ADDRESS_COLUMNS[1], addressInfo.address);
				updateValues.put(ADDRESS_COLUMNS[2], addressInfo.latitude);
				updateValues.put(ADDRESS_COLUMNS[3], addressInfo.longitude);
				updateValues.put(ADDRESS_COLUMNS[4], addressInfo.mode);
				updateValues.put(ADDRESS_COLUMNS[5], addressInfo.contextualMode);
			}

			if (null != resolver.insert(uri, updateValues)) {
				result = UPDATE_ADDRESS_STATUS.SUCCESS;
			}
		} catch (Exception e) {
			Log.w(TAG, "[insertAddressInfo_Migrate] E:" + e);
			return result;
		}

		return result;
	}

	/**
	 * Used in Sense71. Delete AddressInfo from HSP.
	 * @param context caller context
	 * @param _id the index of deleted AddressInfo
	 * @return true deleting AddressInfo from HSP successfully, false is failed
	 */
	public static boolean deleteAddressInfo(Context context, int _id) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[deleteAddressInfo] delete from client");
		
		boolean result = false;
		if (context == null) {
            return result;
        }
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_ADDRESS);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_ADDRESS);

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        
        try {
        	String where = ADDRESS_COLUMNS[0]+"=?";
        	String[] whereArgs = new String[] {String.valueOf(_id)};
        	if (resolver.delete(uri, where, whereArgs)<0) {
                Log.w(TAG, "[deleteAddressInfo] update fail");
                return result;
            }
        	result = true;
        }
        catch (Exception e) {
            Log.w(TAG, "[deleteAddressInfo] E:" + e);
            return result;
        }
        
		return result;
	}
	
	/**
	 * Used for Migration. Delete AddressInfo from HSP.
	 * @param context caller context
	 * @param _id the index of deleted AddressInfo
	 * @return true deleting AddressInfo from HSP successfully, false is failed
	 */
	public static boolean deleteAddressInfo_Migrate(Context context, int _id) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[deleteAddressInfo_Migrate] delete from client");

		boolean result = false;
		if (context == null) {
			return result;
		}


		final Uri uri = Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_ADDRESS);
		ContentResolver resolver = context.getContentResolver();
		if (resolver == null) {
			return result;
		}

		try {
			String where = ADDRESS_COLUMNS[0]+"=?";
			String[] whereArgs = new String[] {String.valueOf(_id)};
			if (resolver.delete(uri, where, whereArgs)<0) {
				Log.w(TAG, "[deleteAddressInfo_Migrate] update fail");
				return result;
			}
			result = true;
		}
		catch (Exception e) {
			Log.w(TAG, "[deleteAddressInfo_Migrate] E:" + e);
			return result;
		}

		return result;
	}
	
	/**
	 * Used in Sense71. Update AddressInfo from HSP.
	 * @param context caller context
	 * @param addressInfo addressInfo to update
	 * @param forceUpdate force update addressInfo to database
	 * @return true updating AddressInfo to HSP successfully, false is failed
	 */
	public static UPDATE_ADDRESS_STATUS updateAddressInfo(Context context, AddressInfo addressInfo, boolean forceUpdate) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[updateAddressInfo] update from client");
		
		UPDATE_ADDRESS_STATUS result = UPDATE_ADDRESS_STATUS.FAIL;
		if (context == null) {
            return result;
        }
		else if (false == IsValidLatitude(addressInfo.latitude)){
			// By suggest, check latitude range and return if it's invalid 
			Log.w(TAG, "[updateAddressInfo] invalid latitude");
			result = UPDATE_ADDRESS_STATUS.INVALID_GEOCODE;
			return result;
		}
		else if (false == IsValidLongitude(addressInfo.longitude)){
			// By suggest, check longitude range and return if it's invalid
			Log.w(TAG, "[updateAddressInfo] invalid longitude");
			result = UPDATE_ADDRESS_STATUS.INVALID_GEOCODE;
			return result;
		}
		
		if (false == forceUpdate) {
			AddressInfo duplicateAddress = IsAddressExist(context, addressInfo);
			if (duplicateAddress != null) {
				Log.w(TAG, "[updateAddressInfo] address existed");
				
				if (duplicateAddress.contextualMode == addressInfo.contextualMode) {
					result = UPDATE_ADDRESS_STATUS.ADDRESS_DUPLICATE_SAME_MODE;
				}
				else {
					result = UPDATE_ADDRESS_STATUS.ADDRESS_DUPLICATE_OTHER_MODE;
				}
				
				return result;
			}
		}
		else {
			if (DEBUG_LOG_ENABLED) Log.d(TAG, "[updateAddressInfo] force update");
		}
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_ADDRESS);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_ADDRESS);

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        
        ContentValues updateValues = new ContentValues();
        try {
        	if (updateValues != null) {
                updateValues.put(ADDRESS_COLUMNS[1], addressInfo.address);
                updateValues.put(ADDRESS_COLUMNS[2], addressInfo.latitude);
                updateValues.put(ADDRESS_COLUMNS[3], addressInfo.longitude);
                updateValues.put(ADDRESS_COLUMNS[4], addressInfo.mode);
                updateValues.put(ADDRESS_COLUMNS[5], addressInfo.contextualMode);
                
            	String where = ADDRESS_COLUMNS[0]+"=?";
            	String[] whereArgs = new String[] {String.valueOf(addressInfo._id)};
            	if (resolver.update(uri, updateValues, where, whereArgs)<0) {
                    Log.w(TAG, "[updateAddressInfo] update fail");
                    return result;
                }
            	
            	result = UPDATE_ADDRESS_STATUS.SUCCESS;
            }
        }
        catch (Exception e) {
            Log.w(TAG, "[updateAddressInfo] E:" + e);
            return result;
        }
		
		
		return result;
	}
	
	/**
	 * Used in Sense71. Get WifiInfo list from HSP.
	 * @param context caller context
	 * @param which the request code (Home\work)
	 * @return a WifiInfo list from HSP
	 */
	public static List<WiFiInfo> queryWiFiInfo(Context context, int which) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[queryWiFiInfo] query from client");
		
		List<WiFiInfo> listWiFi = new ArrayList<WiFiInfo>();
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_WIFI);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_WIFI);

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
        	Log.w(TAG, "[queryWiFiInfo] resolver is null");
            return listWiFi;
        }
        Cursor cursor = null;
        try {
        	if (CODE_HOME_ADDRESS == which || CODE_WORK_ADDRESS == which) {
	        	String[] whereArgs = new String[] {Integer.toString(which)};
	        	cursor = resolver.query(uri, null, WIFI_COLUMNS[3]+"=?", whereArgs, null);
        	}
        	else {
        		cursor = resolver.query(uri, null, null, null, null);
        	}

        	if (cursor != null) {
        		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[queryWiFiInfo] Cursor count = " + cursor.getCount());
        		
        		for (cursor.moveToFirst(); !cursor.isAfterLast() ; cursor.moveToNext()) {
        			int _id = cursor.getInt(cursor.getColumnIndex(WIFI_COLUMNS[0]));
        			String ssid = cursor.getString(cursor.getColumnIndex(WIFI_COLUMNS[1]));
        			String bssid = cursor.getString(cursor.getColumnIndex(WIFI_COLUMNS[2]));
        			List<String> bssidList = new ArrayList<String>(Arrays.asList(deserialize(bssid)));
        			int contextualMode = cursor.getInt(cursor.getColumnIndex(WIFI_COLUMNS[3]));
        			
        			listWiFi.add(new WiFiInfo(_id, ssid, bssidList, contextualMode));
        		}
        	}
        	else {
        		Log.i(TAG, "[queryWiFiInfo] cursor is null");
        	}
        	
        } catch(Exception e) {
        	Log.w(TAG, "[queryWiFiInfo] E:" + e);
            return listWiFi;
        } finally{
        	if(null!=cursor)
        		cursor.close();
        }
		
		return listWiFi;
	}
	
	/**
	 * Used ifor Migration. Get WifiInfo list from HSP.
	 * @param context caller context
	 * @param which the request code (Home\work)
	 * @return a WifiInfo list from HSP
	 */
	private static List<WiFiInfo> queryWiFiInfo_Migrate(Context context, int which) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[queryWiFiInfo_Migrate] query from client");

		List<WiFiInfo> listWiFi = new ArrayList<WiFiInfo>();

		final Uri uri = Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_WIFI);
		ContentResolver resolver = context.getContentResolver();
		if (resolver == null) {
			Log.w(TAG, "[queryWiFiInfo_Migrate] resolver is null");
			return listWiFi;
		}
		Cursor cursor = null;
		try {
			if (CODE_HOME_ADDRESS == which || CODE_WORK_ADDRESS == which) {
				String[] whereArgs = new String[] {Integer.toString(which)};
				cursor = resolver.query(uri, null, WIFI_COLUMNS[3]+"=?", whereArgs, null);
			}
			else {
				cursor = resolver.query(uri, null, null, null, null);
			}

			if (cursor != null) {
				if (DEBUG_LOG_ENABLED) Log.d(TAG, "[queryWiFiInfo_Migrate] Cursor count = " + cursor.getCount());

				for (cursor.moveToFirst(); !cursor.isAfterLast() ; cursor.moveToNext()) {
					int _id = cursor.getInt(cursor.getColumnIndex(WIFI_COLUMNS[0]));
					String ssid = cursor.getString(cursor.getColumnIndex(WIFI_COLUMNS[1]));
					String bssid = cursor.getString(cursor.getColumnIndex(WIFI_COLUMNS[2]));
					List<String> bssidList = new ArrayList<String>(Arrays.asList(deserialize(bssid)));
					int contextualMode = cursor.getInt(cursor.getColumnIndex(WIFI_COLUMNS[3]));

					listWiFi.add(new WiFiInfo(_id, ssid, bssidList, contextualMode));
				}
			}
			else {
				Log.i(TAG, "[queryWiFiInfo_Migrate] cursor is null");
			}

		} catch(Exception e) {
			Log.w(TAG, "[queryWiFiInfo_Migrate] E:" + e);
			return listWiFi;
		} finally{
			if(null!=cursor)
				cursor.close();
		}

		return listWiFi;
	}

	/**
	 * Used in Sense71. Insert WifiInfo to HSP.
	 * @param context caller context
	 * @param wifiInfo insert this wifi to HSP
	 * @return true storing WifiInfo to HSP successfully, false is failed
	 */
	public static boolean insertWiFiInfo(Context context, WiFiInfo wifiInfo) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[insertWiFiInfo] insert from client");
		
		boolean result = false;
		if (context == null) {
            return result;
        }

		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_WIFI);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_WIFI);

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        
        ContentValues updateValues = new ContentValues();
        try {
            if (updateValues != null) {
                updateValues.put(WIFI_COLUMNS[1], wifiInfo.ssid);
                updateValues.put(WIFI_COLUMNS[2], serialize(wifiInfo.bssid));
                updateValues.put(WIFI_COLUMNS[3], wifiInfo.contextualMode);
            }
            
            WiFiInfo existWiFi = IsSsidExist(context, wifiInfo.ssid, wifiInfo.contextualMode);
            if (null == existWiFi) {
            	if(null!=resolver.insert(uri, updateValues))
            		result = true;
            }
            else {
            	List<String> bssidArray = existWiFi.bssid;
            	bssidArray.addAll(wifiInfo.bssid);
            	result = updateWiFiInfo(context, existWiFi);
            }
        } catch (Exception e) {
            Log.w(TAG, "[insertWiFiInfo] E:" + e);
            return result;
        }
		
		return result;
	}
	
	/**
	 * Used for Migration. Insert WifiInfo to LocationService Provider of Prism.
	 * @param context caller context
	 * @param wifiInfo insert this wifi to Prism
	 * @return true storing WifiInfo to LocationService Provider of Prism successfully, false is failed
	 */
	private static boolean insertWiFiInfo_Migrate(Context context, WiFiInfo wifiInfo) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[insertWiFiInfo_Migrate] insert from client");

		boolean result = false;
		if (context == null) {
			return result;
		}

		final Uri uri = Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_WIFI);
		ContentResolver resolver = context.getContentResolver();
		if (resolver == null) {
			return result;
		}

		ContentValues updateValues = new ContentValues();
		try {
			if (updateValues != null) {
				updateValues.put(WIFI_COLUMNS[1], wifiInfo.ssid);
				updateValues.put(WIFI_COLUMNS[2], serialize(wifiInfo.bssid));
				updateValues.put(WIFI_COLUMNS[3], wifiInfo.contextualMode);
			}

			WiFiInfo existWiFi = IsSsidExist_Migrate(context, wifiInfo.ssid, wifiInfo.contextualMode);
			if (null == existWiFi) {
				if(null!=resolver.insert(uri, updateValues))
					result = true;
			}
			else {
				List<String> bssidArray = existWiFi.bssid;
				bssidArray.addAll(wifiInfo.bssid);
				result = updateWiFiInfo_Migrate(context, existWiFi);
			}
		} catch (Exception e) {
			Log.w(TAG, "[insertWiFiInfo_Migrate] E:" + e);
			return result;
		}

		return result;
	}

	/**
	 * Used in Sense71. Delete WifiInfo from HSP.
	 * @param context caller context
	 * @param _id the index of deleted WifiInfo
	 * @return true deleting WifiInfo from HSP successfully, false is failed
	 */
	public static boolean deleteWiFiInfo(Context context, int _id) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[deleteWiFiInfo] delete from client");
		
		boolean result = false;
		if (context == null) {
            return result;
        }
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_WIFI);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_WIFI);

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        
        try {
        	String where = WIFI_COLUMNS[0]+"=?";
        	String[] whereArgs = new String[] {String.valueOf(_id)};
        	if (resolver.delete(uri, where, whereArgs)<0) {
                Log.w(TAG, "[deleteWiFiInfo] update fail");
                return result;
            }
        	result = true;
        }
        catch (Exception e) {
            Log.w(TAG, "[deleteWiFiInfo] E:" + e);
            return result;
        }
        
		return result;
	}
	
	/**
	 * Used for Migration. Delete WifiInfo from HSP.
	 * @param context caller context
	 * @param _id the index of deleted WifiInfo
	 * @return true deleting WifiInfo from HSP successfully, false is failed
	 */
	public static boolean deleteWiFiInfo_Migrate(Context context, int _id) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[deleteWiFiInfo_Migrate] delete from client");

		boolean result = false;
		if (context == null) {
			return result;
		}

		final Uri uri = Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_WIFI);
		ContentResolver resolver = context.getContentResolver();
		if (resolver == null) {
			return result;
		}

		try {
			String where = WIFI_COLUMNS[0]+"=?";
			String[] whereArgs = new String[] {String.valueOf(_id)};
			if (resolver.delete(uri, where, whereArgs)<0) {
				Log.w(TAG, "[deleteWiFiInfo_Migrate] update fail");
				return result;
			}
			result = true;
		}
		catch (Exception e) {
			Log.w(TAG, "[deleteWiFiInfo_Migrate] E:" + e);
			return result;
		}

		return result;
	}
	
	/**
	 * Used in Sense71. Update WifiInfo from HSP.
	 * @param context caller context
	 * @param wifiInfo wifiInfo to update
	 * @return true updating WifiInfo to HSP successfully, false is failed
	 */
	public static boolean updateWiFiInfo(Context context, WiFiInfo wifiInfo) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[updateWiFiInfo] update from client");
		
		boolean result = false;
		if (context == null) {
            return result;
        }
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_WIFI);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_WIFI);

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        
        ContentValues updateValues = new ContentValues();
        try {
        	if (updateValues != null) {
                updateValues.put(WIFI_COLUMNS[1], wifiInfo.ssid);
                updateValues.put(WIFI_COLUMNS[2], serialize(wifiInfo.bssid));
                updateValues.put(WIFI_COLUMNS[3], wifiInfo.contextualMode);
                
            	String where = WIFI_COLUMNS[0]+"=?";
            	String[] whereArgs = new String[] {String.valueOf(wifiInfo._id)};
            	if (resolver.update(uri, updateValues, where, whereArgs)<0) {
                    Log.w(TAG, "[updateWiFiInfo] update fail");
                    return result;
                }
            	result = true;
            }
        }
        catch (Exception e) {
            Log.w(TAG, "[updateWiFiInfo] E:" + e);
            return result;
        }
		
		return result;
	}
	
	/**
	 * Used for Migration. Update WifiInfo from LocationService Provider of Prism.
	 * @param context caller context
	 * @param wifiInfo wifiInfo to update
	 * @return true updating WifiInfo to LocationService Provider of Prism successfully, false is failed
	 */
	private static boolean updateWiFiInfo_Migrate(Context context, WiFiInfo wifiInfo) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[updateWiFiInfo_Migrate] update from client");

		boolean result = false;
		if (context == null) {
			return result;
		}

		final Uri uri = Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_WIFI);
		ContentResolver resolver = context.getContentResolver();
		if (resolver == null) {
			return result;
		}

		ContentValues updateValues = new ContentValues();
		try {
			if (updateValues != null) {
				updateValues.put(WIFI_COLUMNS[1], wifiInfo.ssid);
				updateValues.put(WIFI_COLUMNS[2], serialize(wifiInfo.bssid));
				updateValues.put(WIFI_COLUMNS[3], wifiInfo.contextualMode);

				String where = WIFI_COLUMNS[0]+"=?";
				String[] whereArgs = new String[] {String.valueOf(wifiInfo._id)};
				if (resolver.update(uri, updateValues, where, whereArgs)<0) {
					Log.w(TAG, "[updateWiFiInfo_Migrate] update fail");
					return result;
				}
				result = true;
			}
		}
		catch (Exception e) {
			Log.w(TAG, "[updateWiFiInfo_Migrate] E:" + e);
			return result;
		}

		return result;
	}

	/**
	 * Used in Sense71. get all stored wifi's bssid from HSP.
	 * @param context caller context
	 * @return a list of stored wifi's bssid
	 */
	public static List<String> getBssidList(Context context, int which) {
		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[getBssidList] query from client");		
		List<String> listBssid = new ArrayList<String>();
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_WIFI);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_WIFI);

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
        	Log.w(TAG, "[getBssidList] resolver is null");
            return listBssid;
        }
        Cursor cursor = null;
        try {
        	if (CODE_HOME_ADDRESS == which || CODE_WORK_ADDRESS == which) {
	        	String[] whereArgs = new String[] {Integer.toString(which)};
	        	cursor = resolver.query(uri, null, WIFI_COLUMNS[3]+"=?", whereArgs, null);
        	}
        	else {
        		cursor = resolver.query(uri, null, null, null, null);
        	}
        	
        	if (cursor != null) {
        		if (DEBUG_LOG_ENABLED) Log.d(TAG, "[getBssidList] Cursor count = " + cursor.getCount());
        		
        		for (cursor.moveToFirst(); !cursor.isAfterLast() ; cursor.moveToNext()) {
        			String bssid = cursor.getString(cursor.getColumnIndex(WIFI_COLUMNS[2]));       			
        			listBssid.addAll(Arrays.asList(deserialize(bssid)));
         		}
        	}
        	else {
        		Log.i(TAG, "[getBssidList] cursor is null");
        	}   	
        } catch(Exception e) {
        	Log.w(TAG, "[getBssidList] E:" + e);
            return listBssid;
        } finally{
        	if(cursor!=null)
        		cursor.close();
        }
		
		return listBssid;
	}
	
	/**
	 * Used in Sense75. start location services UI from HSP.
	 * @param activity caller activity
	 * @return true if activity start correctly, otherwise false
	 */
	public static boolean startLocationServiceActivity(Activity activity) {
		boolean result = true;
		
		try {
			Intent intent = new Intent(INTENT);
			activity.startActivity(intent);
		} catch (Exception e) {
			Log.w(TAG, "[startLocationServiceActivity] E:" + e);
			result = false;
		}
		
		return result;
	}
	
	private static AddressInfo IsAddressExist(Context context, AddressInfo addressInfo) {
		AddressInfo result = null;
		if (context == null) {
            return result;
        }
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_ADDRESS);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_ADDRESS);

		ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        Cursor cursor = null;
        try {
        	String[] whereArgs = new String[] {addressInfo.address};
        	cursor = resolver.query(uri, null, ADDRESS_COLUMNS[1]+"=?", whereArgs, null);
        	if (cursor != null) {
        		if (cursor.getCount() > 0) {
        			for (cursor.moveToFirst(); !cursor.isAfterLast() ; cursor.moveToNext()) {
        				int _id = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[0]));
            			String address = cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMNS[1]));
            			double latitude = cursor.getFloat(cursor.getColumnIndex(ADDRESS_COLUMNS[2]));
            			double longitude = cursor.getFloat(cursor.getColumnIndex(ADDRESS_COLUMNS[3]));
            			double mode = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[4]));
            			int contextualMode = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[5]));
            			
            			if (isNearBy(addressInfo.latitude, addressInfo.longitude, latitude, longitude)) {
            				result = new AddressInfo(_id, address, latitude, longitude, mode, contextualMode);
            				break;
            			}
             		}		
        		}
        	}
        }
        catch (Exception e) {
        	Log.w(TAG, "[IsAddressExist] E:" + e);
            return result;
        } finally{
        	if(null!=cursor)
        		cursor.close();
        }
		
		return result;
	}
	
	/**
	 * Used for Migration.
	 */
	private static AddressInfo IsAddressExist_Migrate(Context context, AddressInfo addressInfo) {
		AddressInfo result = null;
		if (context == null) {
			return result;
		}

		final Uri uri = Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_ADDRESS);
		ContentResolver resolver = context.getContentResolver();
		if (resolver == null) {
			return result;
		}
		Cursor cursor = null;
		try {
			String[] whereArgs = new String[] {addressInfo.address};
			cursor = resolver.query(uri, null, ADDRESS_COLUMNS[1]+"=?", whereArgs, null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					for (cursor.moveToFirst(); !cursor.isAfterLast() ; cursor.moveToNext()) {
						int _id = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[0]));
						String address = cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMNS[1]));
						double latitude = cursor.getFloat(cursor.getColumnIndex(ADDRESS_COLUMNS[2]));
						double longitude = cursor.getFloat(cursor.getColumnIndex(ADDRESS_COLUMNS[3]));
						double mode = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[4]));
						int contextualMode = cursor.getInt(cursor.getColumnIndex(ADDRESS_COLUMNS[5]));

						if (isNearBy(addressInfo.latitude, addressInfo.longitude, latitude, longitude)) {
							result = new AddressInfo(_id, address, latitude, longitude, mode, contextualMode);
							break;
						}
					}
				}
			}
		}
		catch (Exception e) {
			Log.w(TAG, "[IsAddressExist_Migrate] E:" + e);
			return result;
		} finally{
			if(null!=cursor)
				cursor.close();
		}

		return result;
	}
	
	private static final double ERROR_GEO = 0.001;
    private static boolean isNearBy(double lat0, double lng0, double lat1, double lng1) {
    	return Math.abs(lat0 - lat1) <= ERROR_GEO &&
				Math.abs(lng0 - lng1) <= ERROR_GEO;
    }
	
	private static WiFiInfo IsSsidExist(Context context, String mySsid, int which) {
		WiFiInfo result = null;
		if (context == null) {
            return result;
        }
		
		Uri uri = Uri.EMPTY;
		if(IsMigrate(context))
			uri =   Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_WIFI);
		else
			uri =  Uri.parse("content://" + AUTHORITY + "/" + URI_TABLE_WIFI);

		ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {	
            return result;
        }
        Cursor cursor = null;
        try {
        	String where = WIFI_COLUMNS[1]+"=? AND "+WIFI_COLUMNS[3]+"=?";
        	String[] whereArgs = new String[] {mySsid, String.valueOf(which)};	
        	cursor = resolver.query(uri, null, where, whereArgs, null);
        	if (cursor != null && cursor.moveToFirst()) {
        		int _id = cursor.getInt(cursor.getColumnIndex(WIFI_COLUMNS[0]));
    			String ssid = cursor.getString(cursor.getColumnIndex(WIFI_COLUMNS[1]));
    			String bssid = cursor.getString(cursor.getColumnIndex(WIFI_COLUMNS[2]));
    			List<String> bssidList = new ArrayList<String>(Arrays.asList(deserialize(bssid)));
    			int contextualMode = cursor.getInt(cursor.getColumnIndex(WIFI_COLUMNS[3]));
    			
    			result = new WiFiInfo(_id, ssid, bssidList, contextualMode);
        	}
        }
        catch (Exception e) {
        	Log.w(TAG, "[IsSsidExist] E:" + e);
            return result;
        } finally{
        	if(null!=cursor)
        		cursor.close();
        }
		
		return result;
	}
	
	/**
	 * Used for Migration.
	 */
	private static WiFiInfo IsSsidExist_Migrate(Context context, String mySsid, int which) {
		WiFiInfo result = null;
		if (context == null) {
			return result;
		}

		final Uri uri = Uri.parse("content://" + AUTHORITYINPRISM + "/" + URI_TABLE_WIFI);
		ContentResolver resolver = context.getContentResolver();
		if (resolver == null) {
			return result;
		}
		Cursor cursor = null;
		try {
			String where = WIFI_COLUMNS[1]+"=? AND "+WIFI_COLUMNS[3]+"=?";
			String[] whereArgs = new String[] {mySsid, String.valueOf(which)};
			cursor = resolver.query(uri, null, where, whereArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int _id = cursor.getInt(cursor.getColumnIndex(WIFI_COLUMNS[0]));
				String ssid = cursor.getString(cursor.getColumnIndex(WIFI_COLUMNS[1]));
				String bssid = cursor.getString(cursor.getColumnIndex(WIFI_COLUMNS[2]));
				List<String> bssidList = new ArrayList<String>(Arrays.asList(deserialize(bssid)));
				int contextualMode = cursor.getInt(cursor.getColumnIndex(WIFI_COLUMNS[3]));

				result = new WiFiInfo(_id, ssid, bssidList, contextualMode);
			}
		}
		catch (Exception e) {
			Log.w(TAG, "[IsSsidExist_Migrate] E:" + e);
			return result;
		} finally{
			if(null!=cursor)
				cursor.close();
		}

		return result;
	}

	private final static String ARRAY_DIVIDER = "##__,__##";
	private static String serialize(List<String> content){      
	    return TextUtils.join(ARRAY_DIVIDER, content.toArray());
	}

	private static String[] deserialize(String content){
	    return content.split(ARRAY_DIVIDER);
	}
	
	/**
	 * Check input latitude is valid
	 * @param latitude
	 * @return ture if latitude in 0~90;otherwise false
	 */
	private static boolean IsValidLatitude(double latitude){
		if(latitude < CONSTANT_MAX_LATITUDE_W || latitude > CONSTANT_MAX_LATITUDE_E)
			return false;
		return true;
	}
	/**
	 * Check input longitude is valid
	 * @param longitude
	 * @return ture if latitude in 0~180;otherwise false
	 */
	private static boolean IsValidLongitude(double longitude){
		if(longitude < CONSTANT_MAX_LONGITUDE_S || longitude > CONSTANT_MAX_LONGITUDE_N)
			return false;
		return true;
	}

	public static boolean IsMigrate(Context context)
	{
		final String sharedPreferencesName = "LocationServiceDBMigrate";
		final String fieldNameMigrate = "ISMIGRATE";
		final String fieldNameChecked = "ISCHECKED";

		boolean bRet = false;
		SharedPreferences dbMigrate = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		boolean isChecked = dbMigrate.getBoolean(fieldNameChecked, false);
		boolean isMigrate = dbMigrate.getBoolean(fieldNameMigrate, false);

		if(isChecked) {
			Log.d(TAG, "isChecked:" + isChecked + " ,isMigrate:" + isMigrate);
			return isMigrate;
		}
		else{
			try {
				android.content.ContentProviderClient HSPLocationServiceProviderClient = context.getContentResolver().acquireContentProviderClient(AUTHORITY);
				android.content.ContentProviderClient PrismLocationServiceProviderClient = context.getContentResolver().acquireContentProviderClient(AUTHORITYINPRISM);
				if(HSPLocationServiceProviderClient != null && PrismLocationServiceProviderClient != null)
				{
					//Do Migration.
					DoLocationServiceProviderMigration(context);

					//Then use Location Service Provider of Prism.
					dbMigrate.edit().putBoolean(fieldNameMigrate, true).apply();
					bRet = true;
				}
				else if (HSPLocationServiceProviderClient == null)
				{
					Log.d(TAG, "HSP Location Service Provider is not exist");
					//Then use Location Service Provider of Prism.
					dbMigrate.edit().putBoolean(fieldNameMigrate, true).apply();
					bRet = true;
				}
				else //PrismLocationServiceProviderClient == null
				{
					Log.d(TAG, "Prism Location Service Provider is not exist");
					//Then use Location Service Provider of HSP.
					dbMigrate.edit().putBoolean(fieldNameMigrate, false).apply();
					bRet = false;
				}

				if(HSPLocationServiceProviderClient != null )
					HSPLocationServiceProviderClient.release();
				if(PrismLocationServiceProviderClient != null)
					PrismLocationServiceProviderClient.release();
				dbMigrate.edit().putBoolean(fieldNameChecked, true).apply();
			}catch(Exception e)
			{
				Log.d(TAG, "IsMigrate() exception: " + e.toString());
			}
		}

		return bRet ;
	}

	//Migrate Location Service Provider from HSP to Prism.
	private static void DoLocationServiceProviderMigration(Context context)
	{
		Log.d(TAG, "DoLocationServiceProviderMigration()");

		//1.Address info
		List<AddressInfo> addressInfos = queryAddressInfo_Migrate(context, CODE_HOME_ADDRESS);
		if (addressInfos != null && addressInfos.size() > 0 ) {
			for (AddressInfo addrInfo: addressInfos) {
				if (!TextUtils.isEmpty(addrInfo.address)) {
					insertAddressInfo_Migrate(context, addrInfo, false);
				}
				//Clear DB of HSP
				deleteAddressInfo_Migrate(context, addrInfo._id);
			}
		}

		addressInfos = queryAddressInfo_Migrate(context, CODE_WORK_ADDRESS);
		if (addressInfos != null && addressInfos.size() > 0 ) {
			for (AddressInfo addrInfo: addressInfos) {
				if (!TextUtils.isEmpty(addrInfo.address)) {
					insertAddressInfo_Migrate(context, addrInfo, false);
				}
				//Clear DB of HSP
				deleteAddressInfo_Migrate(context, addrInfo._id);
			}
		}

		//2.WiFi info
		List<WiFiInfo> wifiInfos = queryWiFiInfo_Migrate(context, CODE_HOME_ADDRESS);
		if (wifiInfos != null && wifiInfos.size() > 0 ) {
			for (WiFiInfo wifiInfo: wifiInfos) {
				if (!TextUtils.isEmpty(wifiInfo.ssid)) {
					insertWiFiInfo_Migrate(context, wifiInfo);
				}
				//Clear DB of HSP
				deleteWiFiInfo_Migrate(context, wifiInfo._id);
			}
		}

		wifiInfos = queryWiFiInfo_Migrate(context, CODE_WORK_ADDRESS);
		if (wifiInfos != null && wifiInfos.size() > 0 ) {
			for (WiFiInfo wifiInfo: wifiInfos) {
				if (!TextUtils.isEmpty(wifiInfo.ssid)) {
					insertWiFiInfo_Migrate(context, wifiInfo);
				}
				//Clear DB of HSP
				deleteWiFiInfo_Migrate(context, wifiInfo._id);
			}
		}
	}
}
