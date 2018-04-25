package com.htc.lib1.dm.cache;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.htc.lib1.dm.bo.DMRequestType;
import com.htc.lib1.dm.constants.CacheConstants;
import com.htc.lib1.dm.constants.Constants;
import com.htc.lib1.dm.env.AppEnv;
import com.htc.lib1.dm.env.DeviceEnv;
import com.htc.lib1.dm.exception.DMCacheException;
import com.htc.lib1.dm.exception.DMException;
import com.htc.lib1.dm.logging.Logger;
import com.htc.lib1.dm.util.CryptoHelper;
import com.htc.lib1.dm.util.FormatUtil;

import java.util.Date;

/**
 * Created by Joe_Wu on 8/31/14.
 */
public class DMCacheManager {
    private static final Logger LOGGER = Logger.getLogger("[DM]",DMCacheManager.class);

    private static DMCacheManager sInstance;
    private ContextWrapper ctxWrapper;
    private MemoryCache memCache = MemoryCache.getInstance();
    private Gson gson = new Gson();

    private AppEnv appEnv;
    private DeviceEnv deviceEnv;
    private String versionKey;

    private SharedPreferences sp;

    public synchronized static DMCacheManager getInstance(Context context, String versionKey) throws DMCacheException {
        if(sInstance==null || !sInstance.versionKey.equals(versionKey))
            sInstance = new DMCacheManager(context, versionKey);
        return sInstance;
    }

    private DMCacheManager(Context context, String versionKey) {
        this.ctxWrapper = new ContextWrapper(context);
        this.appEnv = AppEnv.get(context);
        this.deviceEnv = DeviceEnv.get(context);
        this.versionKey = versionKey;
        this.sp = context.getSharedPreferences(Constants.SHARED_PREF_NAME_DM_CACHE, Context.MODE_PRIVATE);
    }

    public <T> Boolean isDataInCache(Class<T> type, String cacheKey) {
        if(memCache.existCache(cacheKey))
            return true;
        if(isDataInPersistCache(cacheKey)) {
            copyPersistCacheIntoMemCache(type, cacheKey);
            return true;
        }
        return false;
    }

    public <T> T getDataFromCache(Class<T> type, String cacheKey) {
        if(memCache.existCache(cacheKey))
            return memCache.getCache(type,cacheKey);
        else {
            String spiceValue = getDataFromPersistCache(cacheKey);
            T returnValue = null;
            if(type.isAssignableFrom(String.class)){
                returnValue = (T)spiceValue;
            }else{
                returnValue = gson.fromJson(spiceValue,type);
            }
            memCache.setCache(cacheKey, returnValue);
            return returnValue;
        }
    }

    public <T> void putDataInCache(Class<T> type, String cacheKey, T value) {
        try {
            String keyForTimeObj = cacheKey+Constants.SHARED_PREF_DM_CACHE_TIME_SURFIX;
            String valueStr;
            if(type.isAssignableFrom(String.class)){
                valueStr = (String)value;
            }else{
                valueStr = gson.toJson(value);
            }
            sp.edit()
              .putString(cacheKey,valueStr)
              .putLong(keyForTimeObj,System.currentTimeMillis())
              .commit();
            memCache.setCache(cacheKey, value);
        } catch (Exception e) {
            LOGGER.error("Error in put value into cache. key:"+cacheKey+" value:"+value);
        }
    }

    public <T> void clearDataInCache(Class<?> type, String cacheKey) {
        String keyForTimeObj = cacheKey+Constants.SHARED_PREF_DM_CACHE_TIME_SURFIX;
        sp.edit()
          .remove(cacheKey)
          .remove(keyForTimeObj)
          .commit();
        memCache.removeCache(cacheKey);
    }

    public Date getDateOfDataInCache(String configKey) {
        String keyForTimeObj = configKey+Constants.SHARED_PREF_DM_CACHE_TIME_SURFIX;
        if(sp.contains(configKey) && sp.contains(keyForTimeObj)){
            // doulbe check if local date is incorrect
            Long d = sp.getLong(keyForTimeObj,0l);
            Long now = System.currentTimeMillis();
            if(d>now){
                // exiting date time > now, align date time to now
                LOGGER.warning("Cached object time:["+ FormatUtil.timestampToDate(d) +"] > now:["+FormatUtil.timestampToDate(now)+"], align object time to now.");
                d = now;
                sp.edit().putLong(keyForTimeObj,d).commit();
            }
            return new Date(d);
        }
        return null;
    }

    private Boolean isDataInPersistCache(String cacheKey) {
        try {
            boolean value = sp.contains(cacheKey);
            LOGGER.debug("isDataInPersistCache("+cacheKey+") value:"+value);
            return value;
        } catch (Exception e) {
            LOGGER.error("Fail to get value from persistent cache. ",e);
            return false;
        }
    }

    private String getDataFromPersistCache(String cacheKey) {
        try {
            if (isDataInPersistCache(cacheKey)) {
                LOGGER.debug("getDataFromPersistCache(" + cacheKey + ")");
                return sp.getString(cacheKey, null);
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Fail to get value from persistent cache. ",e);
            return null;
        }
    }

    private <T> void copyPersistCacheIntoMemCache(Class<T> type, String cacheKey) {
        String value = getDataFromPersistCache(cacheKey);
        if (value != null) {
            if (type.isAssignableFrom(String.class)) {
                memCache.setCache(cacheKey, value);
            } else {
                memCache.setCache(cacheKey, gson.fromJson(value, type));
            }
        }
    }

    public String getCacheKey_LatestInvokeTime(String requestType) throws DMException {
        return String.format(CacheConstants.LATEST_INVOKE_TIME_STRING_FORMAT, requestType, getCacheUniqueKey(requestType));
    }

    public String getCacheKey_LatestSuccessTime(String requestType) throws DMException {
        return String.format(CacheConstants.LATEST_SUCCESS_TIME_STRING_FORMAT, requestType, getCacheUniqueKey(requestType));
    }

    public String getCacheKey_RetryFailLatestTime(String requestType) throws DMException {
        return String.format(CacheConstants.RETRY_FAIL_LATEST_TIME_STRING_FORMAT, requestType, getCacheUniqueKey(requestType));
    }

    public String getCacheKey_RetryFailCount(String requestType) throws DMException {
        return String.format(CacheConstants.RETRY_FAIL_COUNT_STRING_FORMAT, requestType, getCacheUniqueKey(requestType));
    }

    public String getCacheKey_RetryAfter(String requestType) throws DMException {
        return String.format(CacheConstants.RETRY_AFTER_STRING_FORMAT, requestType, getCacheUniqueKey(requestType));
    }

    // Ansel - getConfig new retry mechanism
    public String getCacheKey_RetryPeriod(String requestType) throws DMException {
        return String.format(CacheConstants.RETRY_PERIOD_STRING_FORMAT, requestType, getCacheUniqueKey(requestType));
    }

    public String getCacheKey_AppConfig() throws DMException {
        return String.format(CacheConstants.APP_CONFIG_STRING_FORMAT, getCacheUniqueKey(DMRequestType.GET_CONFIG) );
    }

    public String getCacheKey_AppConfigMeta() throws DMException {
        return String.format(CacheConstants.APP_CONFIG_META_STRING_FORMAT, getCacheUniqueKey(DMRequestType.GET_CONFIG) );
    }

    public String getCacheKey_AppConfigMetaTtl() throws DMException {
        return String.format(CacheConstants.APP_CONFIG_META_TTL_STRING_FORMAT, getCacheUniqueKey(DMRequestType.GET_CONFIG) );
    }

    public String getCacheKey_ConfigStatus() throws DMException {
        return String.format(CacheConstants.CONFIG_STATUS_STRING_FORMAT , getCacheUniqueKey(DMRequestType.GET_CONFIG));
    }

    public String getCacheKey_ConfigRunningTime() throws DMException {
        return String.format(CacheConstants.CONFIG_RUNNING_TIME_STRING_FORMAT , getCacheUniqueKey(DMRequestType.GET_CONFIG));
    }

    public String getCacheKey_ProfileStatus() throws DMException {
        return String.format(CacheConstants.PROFILE_STATUS_STRING_FORMAT , getCacheUniqueKey(DMRequestType.PUT_PROFILE));
    }

    public String getCacheKey_ProfileRunningTime() throws DMException {
        return String.format(CacheConstants.PROFILE_RUNNING_TIME_STRING_FORMAT , getCacheUniqueKey(DMRequestType.PUT_PROFILE));
    }

    private String getCacheUniqueKey(String requestType) throws DMException {
        // String dmIdentifier = String.format("%s_%s_%s_%s",deviceEnv.getDeviceSN(),deviceEnv.getManufacturer(),deviceEnv.getPlatformDeviceIdUrn(),deviceEnv.getMobileEquipmentIdentifierUrn());
        // String uniqueKey = String.format("%s_%s_%s_%s_%s_%s_%s_%s",versionKey, dmIdentifier, deviceEnv.getAndroidApiLevel(), deviceEnv.getBuildFingerprint() ,appEnv.getAppID() ,appEnv.getAppVersion() ,deviceEnv.getDeviceModelId(), deviceEnv.getCID());

        String[] uniqueKeyValues = new String[]{
                versionKey,
                deviceEnv.getDeviceSN(),
                deviceEnv.getManufacturer(),
                deviceEnv.getPlatformDeviceIdUrn(),
                deviceEnv.getMobileEquipmentIdentifierUrn(),
                Integer.toString(deviceEnv.getAndroidApiLevel()),
                deviceEnv.getBuildFingerprint(),
                appEnv.getAppID(),
                appEnv.getAppVersion(),
                deviceEnv.getDeviceModelId(),
                deviceEnv.getCID()
        };

        String uniqueKey = TextUtils.join("_", uniqueKeyValues);
        return CryptoHelper.computeHash(uniqueKey).replace("/", "_").replace("+", "_").replace("=", "_");
    }
}
