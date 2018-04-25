package com.htc.lib1.dm.util;

import com.htc.lib1.dm.bo.DMStatus;
import com.htc.lib1.dm.cache.DMCacheManager;
import com.htc.lib1.dm.exception.DMException;
import com.htc.lib1.dm.logging.Logger;

/**
 * Created by Joe_Wu on 9/2/14.
 */
public class DMStatusHelper {

    private static final Logger LOGGER = Logger.getLogger("[DM]", DMStatusHelper.class);
    private static DMStatusHelper sInstance;
    private DMCacheManager cacheManager;

    public synchronized static DMStatusHelper getInstance(DMCacheManager cacheManager) {
        if (sInstance == null)
            sInstance = new DMStatusHelper(cacheManager);
        return sInstance;
    }

    private DMStatusHelper(DMCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Integer getConfigStatus() throws DMException {
        Integer status = DMStatus.GET_CONFIG_INIT;
        if (this.cacheManager.isDataInCache(Integer.class, cacheManager.getCacheKey_ConfigStatus())) {
            status = this.cacheManager.getDataFromCache(Integer.class, this.cacheManager.getCacheKey_ConfigStatus());
        } else {
            // No data in cache, then update cache to default;
            setConfigStatus(status);
        }
        return status;
    }

    public Integer getProfileStatus() throws DMException {
        Integer status = DMStatus.PUT_PROFILE_INIT;
        if (this.cacheManager.isDataInCache(Integer.class, cacheManager.getCacheKey_ProfileStatus())) {
            status = this.cacheManager.getDataFromCache(Integer.class, this.cacheManager.getCacheKey_ProfileStatus());
        } else {
            // No data in cache, then update cache to default;
            setProfileStatus(status);
        }
        return status;
    }

    public void setConfigStatus(Integer dmStatus) throws DMException {
        this.cacheManager.putDataInCache(Integer.class, this.cacheManager.getCacheKey_ConfigStatus(), dmStatus);
    }


    public void setProfileStatus(Integer dmStatus) throws DMException {
        this.cacheManager.putDataInCache(Integer.class, this.cacheManager.getCacheKey_ProfileStatus(), dmStatus);
    }

    public String getConfigStatusDisplayName() {
        try {
            switch (getConfigStatus()) {
                case DMStatus.GET_CONFIG_INIT:
                    return "GET_CONFIG_INIT";
                case DMStatus.GET_CONFIG_CACHE_OK:
                    return "GET_CONFIG_CACHE_OK";
                case DMStatus.GET_CONFIG_CACHE_EXPIRED:
                    return "GET_CONFIG_CACHE_EXPIRED";
                case DMStatus.GET_CONFIG_RETRY:
                    return "GET_CONFIG_RETRY";
                case DMStatus.GET_CONFIG_RUNNING:
                    return "GET_CONFIG_RUNNING";
                default:
                    return "GET_CONFIG_UNDEFINED_STATUS";
            }
        } catch (DMException ex) {
            LOGGER.warning(ex);
            return "GET_CONFIG_STATUS_FAIL";
        }
    }

    public String getProfileStatusDisplayName() {
        try {
            switch (getProfileStatus()) {
                case DMStatus.PUT_PROFILE_INIT:
                    return "PUT_PROFILE_INIT";
                case DMStatus.PUT_PROFILE_DONE:
                    return "PUT_PROFILE_DONE";
                case DMStatus.PUT_PROFILE_RETRY:
                    return "PUT_PROFILE_RETRY";
                case DMStatus.PUT_PROFILE_RUNNING:
                    return "PUT_PROFILE_RUNNING";
                default:
                    return "PUT_PROFILE_UNDEFINED_STATUS";
            }
        } catch (Exception ex) {
            LOGGER.warning(ex);
            return "PUT_PROFILE_STATUS_FAIL";
        }
    }
}
