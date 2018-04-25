package com.htc.lib2.weather;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib2.weather.WeatherConsts.TABLE_DATA_COLUMNS;
import com.htc.lib2.weather.WeatherUtility.WeatherData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * weather data request information
 * @author MASD_U59000
 */
public class WeatherRequest implements Parcelable{
    private static final boolean LOG_FLAG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    private static final boolean LOG_FLAG_SECURITY = HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag;
    private static final String LOG_TAG = "WeatherRequest";
    private int type = 0;
    private String param1 = "";
    private String param2 = "";
    
    // for cur location use, when trigger source is:
    // (1) FORCE_UPDATE: use cache cur as req cur
    // (2) REQUEST: use sys cur as req cur
    private String reqCurLocLat = "";
    private String reqCurLocLng = "";
    private String reqCurLocLatTrim = "";
    private String reqCurLocLngTrim = "";
    private String reqCurLocName = "";
    private String reqCurLocState = "";
    private String reqCurLocCountry = "";
    private String reqCurLocTimezoneId = "";
    private WeatherData reqCurLocCacheData = null;
    
    private static final String[] PROJECTION = new String[] {
    	WeatherConsts.TABLE_DATA_COLUMNS.type.name(),
    	WeatherConsts.TABLE_DATA_COLUMNS.param1.name(),
    	WeatherConsts.TABLE_DATA_COLUMNS.param2.name()
    };   
    
    public int describeContents() {
        return 0;
    }
    
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        /**
         * createFromParcel
         * @param Parcel parcel
         */
        public WeatherRequest createFromParcel(Parcel in) {
            return new WeatherRequest(in);
        }
        /**
         * newArray
         * @param size WSPRequest size
         */
        public WeatherRequest[] newArray(int size) {
            return new WeatherRequest[size];
        }
    };
    
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(type);
        out.writeString(param1);
        out.writeString(param2);
        out.writeString(reqCurLocLat);
        out.writeString(reqCurLocLng);
        out.writeString(reqCurLocLatTrim);
        out.writeString(reqCurLocLngTrim);
        out.writeString(reqCurLocName);
        out.writeString(reqCurLocState);
        out.writeString(reqCurLocCountry);
        out.writeString(reqCurLocTimezoneId);
        out.writeParcelable(reqCurLocCacheData, flags);
    }
    
    private WeatherRequest(Parcel in) {
        type = in.readInt();
        param1 = in.readString();
        param2 = in.readString();
        reqCurLocLat = in.readString();
        reqCurLocLng = in.readString();
        reqCurLocLatTrim = in.readString();
        reqCurLocLngTrim = in.readString();
        reqCurLocName = in.readString();
        reqCurLocState = in.readString();
        reqCurLocCountry = in.readString();
        reqCurLocTimezoneId = in.readString();
        reqCurLocCacheData = in.readParcelable(this.getClass().getClassLoader());
    }
    
    /**
     * constructor
     */
    public WeatherRequest() {
    }

 // /*~ generate request data message
    /**
     * generate WSPReqest For CurrentLocation [type:1]
     * 
     * @return WSPReqest For CurrentLocation
     */
    public static WeatherRequest generateWeatherRequestForCurrentLocation() {
        WeatherRequest req = new WeatherRequest();
        req.setTypeCurrentLocation();
        return req;
    }
    
    /**
     * generate WSPRequest with city code For LocCode [type:2]
     * 
     * @param locCode city code
     * @return WSPRequest For LocCode
     */
    public static WeatherRequest generateWeatherRequestForLocCode(String locCode) {
        if (locCode == null || locCode.length() < 1) {
            throw new IllegalArgumentException("locCode can not be null or empty");
        }

        WeatherRequest req = new WeatherRequest();
        req.setTypeLocCode(locCode);
        return req;
    }
    
    /**
     * generate WSPRequest For Latitude [type:3]
     * 
     * @param latitude latitude
     * @param longitude longitude
     * @return WSPRequest For Latitude and longitude
     */
    public static WeatherRequest generateWeatherRequestForLatitude(String latitude, String longitude) {
        if (latitude == null || latitude.length() < 1) {
            throw new IllegalArgumentException("latitude can not be null or empty");
        }

        if (longitude == null || longitude.length() < 1) {
            throw new IllegalArgumentException("longitude can not be null or empty");
        }

        WeatherRequest req = new WeatherRequest();
        req.setTypeLatitude(latitude, longitude);
        return req;
    }
    // /~*generate request data message
    
    /**
     *  Hide Automatically by SDK Team [U12000]<br><br>
     *  Set request Type of Current Location[Type 1]
     *  
     *  @hide
     */
    private void setTypeCurrentLocation() {
        type = WeatherConsts.TYPE_CURRENT_LOCATION;
        param1 = "";
        param2 = "";
    }

    /**
     * Set Type by LocCode[Type 2]
     * @param locCode locCode
     */
    public void setTypeLocCode(String locCode) {
        type = WeatherConsts.TYPE_LOC_CODE;
        param1 = locCode;
        param2 = "";
    }
    
    /**
     *  Hide Automatically by SDK Team [U12000]<br><br>
     *  Set Type of Latitude[Type 3]
     *  @param latitude 
     *  @param longitude
     *  
     *  @hide
     */
    private void setTypeLatitude(String latitude, String longitude) {
        type = WeatherConsts.TYPE_LATITUDE;
        param1 = latitude;
        param2 = longitude;
    }
    
    /**
     * generate WSPRequest For CurrentLocation With Current CacheData
     * @param context context
     * @return WSPRequest with Current CacheData
     */
    public static WeatherRequest generateWeatherRequestForCurrentLocationWithCurCacheData(Context context) {
    	WeatherRequest req = generateWeatherRequestForCurrentLocation();
        
        WeatherData curInCache = _getCurCacheData(context, req);
        if (curInCache == null) {
            Log.w(LOG_TAG, "Generate request for cur loc with cur in db, but there is no cur in db. (Called from Force update or Auto-sync)");
            return null;
        }
        
        req.setReqCurLoc(curInCache.getCurLocLat(), curInCache.getCurLocLng(), curInCache.getCurLocLatTrim(), curInCache.getCurLocLngTrim(), 
            curInCache.getCurLocName(), curInCache.getCurLocState(), curInCache.getCurLocCountry(), curInCache.getCurLocTimezoneId(), null);
        
        return req;
    }
    
    /**
     * generate Weather Request Database Where Condition
     * @param type type
     * @param param1 param1
     * @param param2 param2
     * @return query database condition string
     */
    public static String generateWeatherRequestDbWhereCondition(int type, String param1, String param2) {
        String where = TABLE_DATA_COLUMNS.type.name() + " = " + type;
        
        if (type == WeatherConsts.TYPE_LATITUDE) {
            where += " AND " + TABLE_DATA_COLUMNS.param1.name() + " = '" + param1 + "'" +
                     " AND " + TABLE_DATA_COLUMNS.param2.name() + " = '" + param2 + "'";
        } else if (type == WeatherConsts.TYPE_LOC_CODE) {
            where += " AND " + TABLE_DATA_COLUMNS.param1.name() + " = " + DatabaseUtils.sqlEscapeString(param1);
        }
        return where;
    }
    
    /**
     * Get Current Cache Data 
     * @param context context
     * @param req weather request message
     * @return weather current cache data
     */
    private static WeatherData _getCurCacheData(Context context, WeatherRequest req) {
        if (context == null || req.getType() != WeatherConsts.TYPE_CURRENT_LOCATION) {
            return null;
        }

        // check if WeatherSyncProvider is installed and permission is valid
        if (!WeatherUtility.isWeatherSyncProviderInstalled(context.getContentResolver())) {
            return null;
        }

        WeatherData cacheData = null;
        
        String where = generateWeatherRequestDbWhereCondition(req.getType(), req.getParam1(), req.getParam2());

        Cursor cursor = null;
        ContentProviderClient unstableContentProvider = null;
        try {
            unstableContentProvider = context.getContentResolver().acquireUnstableContentProviderClient(WeatherConsts.URI_DATA);
            cursor = unstableContentProvider.query(
            		WeatherConsts.URI_DATA, 
//                WeatherData.getProjection(), 
            		null,
                where,
                null, 
                null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                cacheData = new WeatherData(cursor);
            }
        } catch (Exception e) {
            if(LOG_FLAG) {
                Log.w(LOG_TAG, "_getCurCacheData() - Catch Exception: " , e);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            //release ContentProviderClient
            if (unstableContentProvider != null) {
                unstableContentProvider.release();
            }
        }
        
        return cacheData;
    }
    
    /**
     * request weather data cache data.
     * @param context context
     * @param req weather request message
     * @return weather cache data 
     */
    public static Bundle request(Context context, WeatherRequest req) {
        if (context == null || req == null) {
            throw new IllegalArgumentException("Context or WeatherRequest can not be null");
        }
        
        if (LOG_FLAG) Log.d(LOG_TAG, "EVENT - REQUEST, req: " + req.toString());
        
        WeatherLocation sysCurLoc = null;
        boolean isCurLocType = (WeatherConsts.TYPE_CURRENT_LOCATION == req.getType());
        
        // for current location type, getting current location from HtcLocationService
        if (isCurLocType) {
            sysCurLoc = WeatherUtility._getCurrentLocation(context);
            if (sysCurLoc == null) {
                Log.w(LOG_TAG,"request cur loc, but there is no sys cur");
                return null;
            }
        }
        
        // Get weather cache data.
        // If the data is current location weather data, it will check the cache data is available or not.
        WeatherData cacheData = WeatherUtility._getCacheData(context, req, sysCurLoc);
        Bundle dataBundle = null;
        if(cacheData != null) {
        	dataBundle = WeatherUtility.getDataBundle(context, cacheData);
        }
        
        // write weather request to database
        WeatherUtility._addRequestInDatabase(context, req);
        
        // step: check cache data
        boolean isSyncAutomatically = WeatherUtility.isSyncAutomatically(context);
//      boolean areBackgroundDataAndAutoSyncEnabled = areBackgroundDataAndAutoSyncEnabled(context);
        boolean hasCacheData = (cacheData != null);
        boolean isCacheOverdue = false;
        
        // in case auto sync service did not update successful at last check point,
        // check cache last update time, if it is overdue, trigger sync service
        if (hasCacheData) {
            long autoSyncFrequency = WeatherUtility.getAutoSyncFrequency(context);
            long acceptablyTime = System.currentTimeMillis() - autoSyncFrequency;
            
            long cacheDataUpdate = cacheData.getLastUpdate();
            isCacheOverdue = cacheDataUpdate < acceptablyTime;
            SimpleDateFormat fmt = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
            if (LOG_FLAG) Log.d(LOG_TAG, "req " + req.toString() + " cache at: " + fmt.format(new Date(cacheDataUpdate)) +", " + isCacheOverdue);
        }

        // for current location
        if (isCurLocType) {
            if (hasCacheData && !isCacheOverdue && WeatherUtility.isWSPCurrentLocationFlagOn(context)) {
            	WeatherUtility.turnOffWSPCurrentLocationFlag(context);
            	WeatherUtility.broadcastDataIntent(context, dataBundle);
            }
            
            /*
             * // Bravo ITS#4443
             * There are three settings related to Auto-Sync:
             * (1) Background data in Sync Manager
             * (2) Auto-sync in Sync Manager
             * (3) Auto-sync in Weather
             * 
             *  isSyncAutomatically() will only check (2) & (3)
             *  areBackgroundDataAndAutoSyncEnabled() will check (1), (2) & (3)
             *  
             *  In this issue, we need to check all settings by areBackgroundDataAndAutoSyncEnabled()
             */
            // if (!isSyncAutomatically && !hasCacheData) {
            
/**
            if (!areBackgroundDataAndAutoSyncEnabled && !hasCacheData) { 
                // generate WSPPData
                WSPPData d = new WSPPData();
                
                String curLocLatTrim = trimLatitude(sysCurLoc.getLatitude());
                String curLocLngTrim = trimLatitude(sysCurLoc.getLongitude());
                
                d.setTypeCurrentLocation(sysCurLoc.getLatitude(), sysCurLoc.getLongitude(), curLocLatTrim, curLocLngTrim, 
                    sysCurLoc.getName(), sysCurLoc.getState(), sysCurLoc.getCountry(), sysCurLoc.getTimezoneId());
                
                // save sys cur
                String where = WSPPUtility.generateWeatherRequestDbWhereCondition(req.getType(), req.getParam1(), req.getParam2());
                int ret = context.getContentResolver().update(WSPPUtility.URI_DATA, d.toContentValues(), where, null);
                if (ret > 0) {
                    if (LOG_FLAG) Log.d(LOG_TAG,"(auto-sync is disabled and no cur in cache) save new cur loc data to cache successful, " + d.toDebugInfo());
                } else {
                    Log.w(LOG_TAG, "(auto-sync is disabled and no cur in cache) save new cur loc data to cache failed, " + d.toDebugInfo());
                }

                // broadcast cur
                broadcastDataIntent(context, d);
            }
**/
        }

        boolean triggerSyncService = isSyncAutomatically && (!hasCacheData || isCacheOverdue);        
        if (LOG_FLAG) Log.v(LOG_TAG, "req info: " + req.toDebugInfo() + ", cache:" + hasCacheData + ", due:" + isCacheOverdue +
                ", auto:" + isSyncAutomatically + ", trigger:" + triggerSyncService);

        if (triggerSyncService) {
        	WeatherUtility.triggerSyncService(context, null, new WeatherRequest[] { req }, WeatherConsts.SYNC_SERVICE_TRIGGER_SOURCE_REQUEST);
        }
        
        return dataBundle;
    }

    /**
     * get type
     * @return type: 1:current location ,2:city code ,3: latitude and longitude
     */
    public int getType() {
        return type;
    }

    /**
     * get Param1
     * @return Param1
     */
    public String getParam1() {
        return param1;
    }

    /**
     * get Param2
     * @return Param2
     */
    public String getParam2() {
        return param2;
    }
    
    /**
     * get Projection
     * @return WSPRequest Projection
     * 
     * @deprecated [Not use any longer]
     */
    /**@hide*/ 
    protected static String[] getProjection() {
        return PROJECTION;
    }
    
    /**
     * Set Request by Current Location 
     * @param reqCurLocLat  request Current Location latitude
     * @param reqCurLocLng  request Current Location longitude
     * @param reqCurLocLatTrim  request Current Location latitude[trim]
     * @param reqCurLocLngTrim  request Current Location longitude[trim]
     * @param reqCurLocName request Current Location name
     * @param reqCurLocState  request Current Location state
     * @param reqCurLocCountry  request Current Location country
     * @param reqCurLocTimezoneId  request Current Location TimeZone ID
     * @param reqCurLocCacheData  request Current Location Cache Data
     */
    public void setReqCurLoc(String reqCurLocLat, String reqCurLocLng, String reqCurLocLatTrim, String reqCurLocLngTrim,
            String reqCurLocName, String reqCurLocState, String reqCurLocCountry, String reqCurLocTimezoneId, WeatherData reqCurLocCacheData) {
        this.reqCurLocLat = reqCurLocLat;
        this.reqCurLocLng = reqCurLocLng;
        this.reqCurLocLatTrim = reqCurLocLatTrim;
        this.reqCurLocLngTrim = reqCurLocLngTrim;
        this.reqCurLocName = reqCurLocName;
        this.reqCurLocState = reqCurLocState;
        this.reqCurLocCountry = reqCurLocCountry;
        this.reqCurLocTimezoneId = reqCurLocTimezoneId;
        this.reqCurLocCacheData = reqCurLocCacheData;
    }
    
    /**
     *  Hide Automatically by SDK Team [U12000]<br><br>
     *  check is Cur Equals To CacheCur ?
     *  @hide
     */
    protected boolean isCurEqualsToCacheCur() {
        return reqCurLocCacheData != null;
    }
    
    /**
     * Get Current Location CacheData
     * @return Current Location CacheData
     */
    public WeatherData getCurLocCacheData() {
        return reqCurLocCacheData;
    }
    
    /**
     * Get request Current Location latitude
     * @return request Current Location latitude
     */
    public String getReqCurLocLat() {
        return reqCurLocLat;
    }
    /**
     * Get request Current Location longitude
     * @return Current Location longitude
     */
    public String getReqCurLocLng() {
        return reqCurLocLng;
    }
    
    /**
     * Get request Current Location longitude
     * @return request Current Location longitude
     */
    public String getReqCurLocLatTrim() {
        return reqCurLocLatTrim;
    }
    
    /**
     * Get request Current Location longitude[trim]
     * @return Current Location longitude[trim]
     */
    public String getReqCurLocLngTrim() {
        return reqCurLocLngTrim;
    }
    
    /**
     * Get request Current Location name
     * @return request Current Location name
     */
    public String getReqCurLocName() {
        return reqCurLocName;
    }
    
    /**
     * Get request Current Location state
     * @return request Current Location state
     */
    public String getReqCurLocState() {
        return reqCurLocState;
    }

    /**
     * Get request Current Location country
     * @return request Current Location country
     */
    public String getReqCurLocCountry() {
        return reqCurLocCountry;
    }

    /**
     * Get request Current Location TimeZone Id
     * @return request Current Location TimeZone Id
     */
    public String getReqCurLocTimezoneId() {
        return reqCurLocTimezoneId;
    }

    /**
     * print Debug Information
     * @return Debug Information
     */
    public String toDebugInfo() {
        if(LOG_FLAG) {
            try {
                StringBuilder sb = new StringBuilder("");
                sb.append("type: ").append(type);

                if (LOG_FLAG_SECURITY) {
                    sb.append(", param1: ")
                      .append(param1)
                      .append(", param2: ")
                      .append(param2);
                    if (type == WeatherConsts.TYPE_CURRENT_LOCATION) {
                        sb.append(", Lat: ").append(reqCurLocLat)
                          .append(", Lng: ").append(reqCurLocLng)
                          // .append(", reqCurLocLatTrim: ").append(reqCurLocLatTrim)
                          // .append(", reqCurLocLngTrim: ").append(reqCurLocLngTrim)
                          .append(", ").append(reqCurLocName)
                          .append("_").append(reqCurLocState)
                          .append("_").append(reqCurLocCountry)
                          .append(", TZ_Id: ").append(reqCurLocTimezoneId);
                    }
                }

                sb.append(", equalCache: ").append(isCurEqualsToCacheCur());
                return sb.toString();
            } catch (Exception e){
                e.printStackTrace();
                return "Exception is caught. (can't generate debug info)[WSPRequset]";
            }
        } else {
            return "";
        }
    }
    
    /**
     * print WSPRequest: type_param1_param2;
     * @return String type_param1_param2
     */
    public String toString() {
        return type + "_" + param1 + "_" + param2;
    }
    
    
}
