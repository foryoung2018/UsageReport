package com.htc.lib1.dm.util;

import com.htc.lib1.dm.bo.DMRequestType;
import com.htc.lib1.dm.cache.DMCacheManager;
import com.htc.lib1.dm.constants.Constants;
import com.htc.lib1.dm.exception.DMCacheException;
import com.htc.lib1.dm.exception.DMException;
import com.htc.lib1.dm.logging.Logger;

import java.util.Random;

/**
 * Created by Joe_Wu on 8/26/14.
 */
public class RetryHelper {
    private static final Logger LOGGER = Logger.getLogger("[DM]",RetryHelper.class);

    private static RetryHelper sInstance;
    private DMCacheManager cacheManager;

    public synchronized static RetryHelper getInstance(DMCacheManager cacheManager) throws DMCacheException {
        if(sInstance==null)
            sInstance = new RetryHelper(cacheManager);
        return sInstance;
    }

    private RetryHelper(DMCacheManager cacheManager) throws DMCacheException {
        this.cacheManager = cacheManager;
    }

    public Boolean needRetryGetConfig() throws DMException {
        LOGGER.debug("checking needRetryGetConfig...");
        String requestType = DMRequestType.GET_CONFIG;
        Long latestSuccessTime = getGetConfigLatestSuccessTime();
        Long latestInvokeTime = getGetConfigLatestInvokeTime();
        if(latestInvokeTime==null)
            latestInvokeTime = 0l;

        // No LatestSuccessTime.
        if(latestSuccessTime==null && latestInvokeTime==null){
            LOGGER.debug("needRetryGetConfig() - Has no LatestInvokeTime & LatestSuccessTimeStr, will be first time invoke.");
            return true;
        }else {
//            if( latestSuccessTime != null ){
//                if( latestSuccessTime > latestInvokeTime ) {
//                    LOGGER.debug(String.format("needRetryGetConfig() - LatestSuccessTime:[%s] > LatesetInvokeTime:[%s], no need retry.", latestSuccessTime, latestInvokeTime));
//                    return false;
//                }
//            }
            Integer retryCount = cacheManager.getDataFromCache(Integer.class, cacheManager.getCacheKey_RetryFailCount(requestType));
            Integer failCount = retryCount==null ? 1 : retryCount;
            Long now = System.currentTimeMillis();
            Long retrySleepPeriod = failCount < 2 ? 0 : getRetryPeriod(requestType);
            Boolean needRetry = (latestInvokeTime + retrySleepPeriod < now);
            LOGGER.debug(String.format("needRetryGetConfig() latestInvokeTime:%s, now:%s, timediff:%s, failCount:%s, retrySleepPeriod:%s, needRetry:%s", FormatUtil.timestampToDate(latestInvokeTime), FormatUtil.timestampToDate(now), (now - latestInvokeTime), failCount, retrySleepPeriod, needRetry));
            return needRetry;
        }
    }

    public void setGetConfigLatestInvokeTime(Long latestTime) throws DMException {
        String retryType = DMRequestType.GET_CONFIG;
        cacheManager.putDataInCache(Long.class, cacheManager.getCacheKey_LatestInvokeTime(retryType), latestTime);
    }

    public Long getGetConfigLatestInvokeTime() throws DMException {
        String retryType = DMRequestType.GET_CONFIG;
        Long latestTime = cacheManager.getDataFromCache(Long.class, cacheManager.getCacheKey_LatestInvokeTime(retryType));
        Long now = System.currentTimeMillis();
        // Check if latestTime is broken and fix it
        if(latestTime!=null && latestTime > now ){
            LOGGER.debug(String.format("Wrong GetConfig LatestInvokeTime:[%s], update to now:[%s]",FormatUtil.timestampToDate(latestTime),FormatUtil.timestampToDate(now)));
            latestTime = now;
            setGetConfigLatestInvokeTime(latestTime);
        }
        return latestTime;
    }

    // Ansel - new retry mechanism
    public void setRetryPeriod(String requestType, Long timePeriod) throws DMException {
        cacheManager.putDataInCache(Long.class, cacheManager.getCacheKey_RetryPeriod(requestType), timePeriod);
    }

    public Long getRetryPeriod(String requestType) throws DMException {
        Long timePeriod = cacheManager.getDataFromCache(Long.class, cacheManager.getCacheKey_RetryPeriod(requestType));
        // check if the time is broken
        Long retryLimit = 22 * 60 * 60 * 1000l;
        if( timePeriod != null ) {
            if( timePeriod > retryLimit || timePeriod <= 0 ) {
                return 0l;
            }
            return timePeriod;
        }
        return 0l;
    }
    // End

    public void setPutProfileLatestInvokeTime(Long latestTime) throws DMException {
        String retryType = DMRequestType.PUT_PROFILE;
        cacheManager.putDataInCache(Long.class, cacheManager.getCacheKey_LatestInvokeTime(retryType), latestTime);
    }

    public Long getPutProfileLatestInvokeTime() throws DMException {
        String retryType = DMRequestType.PUT_PROFILE;
        Long latestTime = cacheManager.getDataFromCache(Long.class, cacheManager.getCacheKey_LatestInvokeTime(retryType));
        Long now = System.currentTimeMillis();
        // Check if latestTime is broken and fix it
        if(latestTime!=null && latestTime > now ){
            LOGGER.debug(String.format("Wrong PutProfile LatestInvokeTime:[%s], update to now:[%s]",FormatUtil.timestampToDate(latestTime),FormatUtil.timestampToDate(now)));
            latestTime = now;
            setPutProfileLatestInvokeTime(latestTime);
        }
        return latestTime;
    }

    public void setGetConfigLatestSuccessTime(Long latestTime) throws DMException {
        String retryType = DMRequestType.GET_CONFIG;
        cacheManager.putDataInCache(Long.class, cacheManager.getCacheKey_LatestSuccessTime(retryType), latestTime);
    }

    public Long getGetConfigLatestSuccessTime() throws DMException {
        String retryType = DMRequestType.GET_CONFIG;
        Long latestTime = cacheManager.getDataFromCache(Long.class, cacheManager.getCacheKey_LatestSuccessTime(retryType));
        Long now = System.currentTimeMillis();
        // Check if latestTime is broken and fix it
        if(latestTime!=null && latestTime > now ){
            LOGGER.debug(String.format("Wrong GetConfig LatestSuccessTime:[%s], update to now:[%s]",FormatUtil.timestampToDate(latestTime),FormatUtil.timestampToDate(now)));
            latestTime = now;
            setGetConfigLatestSuccessTime(latestTime);
        }
        return latestTime;
    }

    public void setFail(String requestType, Long timestamp) throws DMException {
        cacheManager.putDataInCache(Long.class, cacheManager.getCacheKey_RetryFailLatestTime(requestType), timestamp);
        String cacheKey_retryFailCount = cacheManager.getCacheKey_RetryFailCount(requestType);
        if( cacheManager.isDataInCache(Integer.class, cacheKey_retryFailCount) ){
            Integer failCount = cacheManager.getDataFromCache(Integer.class, cacheKey_retryFailCount);
            cacheManager.putDataInCache(Integer.class, cacheKey_retryFailCount, (failCount + 1));
            // Ansel - set retry period
            if( failCount >= 1 ) { // failCount is >=2 now
                Random gen = new Random();
                long retryPeriod = gen.nextInt(21*60*60)*1000l + 60*60*1000l; // 1~22hr
                setRetryPeriod(requestType, retryPeriod);
            }
            // End
        }else{
            cacheManager.putDataInCache(Integer.class, cacheKey_retryFailCount, 1);
        }
    }

    public void setSuccess(String requestType, Long timestamp) throws DMException {
        cacheManager.putDataInCache(Long.class, cacheManager.getCacheKey_LatestSuccessTime(requestType), timestamp);
        // clear fail data
        cacheManager.clearDataInCache(Long.class, cacheManager.getCacheKey_RetryFailLatestTime(requestType));
        cacheManager.clearDataInCache(Integer.class, cacheManager.getCacheKey_RetryFailCount(requestType));
        cacheManager.clearDataInCache(Long.class, cacheManager.getCacheKey_RetryPeriod(requestType));
    }

    public void setRetryAfter(String requestType, String retryAfterString) {
        // Convert retry-After string into local timestamp
        if(retryAfterString!=null) {
            try {
                Long moratoriumTime = parseMoratoriumTimeHttp(retryAfterString);
                setRetryAfter(requestType, moratoriumTime);
            }catch(Exception ex){
                LOGGER.warning("fail to parse retry-after:["+retryAfterString+"]. ",ex);
            }
        }
    }

    public void setRetryAfter(String requestType, Long retryAfterValue) throws DMException {
        Long now = System.currentTimeMillis();
        Long retryAfterLimit = (now + (Constants.RETRY_AFTER_MAX_PERIOD * 1000) );
        if(retryAfterValue > retryAfterLimit){
            // Retry-After large than Max Retry-Ater Period and fix it
            LOGGER.warning(String.format("Retry-After value:[%s] is large than Now:[%s]+RETRY_AFTER_MAX_PERIOD:[%s](s), set to Now+RETRY_AFTER_MAX_PERIOD:[%s]", FormatUtil.timestampToDate(retryAfterValue), FormatUtil.timestampToDate(now), Constants.RETRY_AFTER_MAX_PERIOD, FormatUtil.timestampToDate(retryAfterLimit)));
            retryAfterValue = retryAfterLimit;
        }
        cacheManager.putDataInCache(Long.class, cacheManager.getCacheKey_RetryAfter(requestType), retryAfterValue);
    }

    public Long getRetryAfter(String requestType) throws DMException {
        if(cacheManager.isDataInCache(Long.class, cacheManager.getCacheKey_RetryAfter(requestType))){
            Long retryAfter = cacheManager.getDataFromCache(Long.class, cacheManager.getCacheKey_RetryAfter(requestType));
            Long now = System.currentTimeMillis();
            Long retryAfterLimit = (now + (Constants.RETRY_AFTER_MAX_PERIOD * 1000) );
            if(retryAfter > retryAfterLimit){
                // Retry-After large than Max Retry-Ater Period and fix it
                LOGGER.warning(String.format("Retry-After value:[%s] is large than Now:[%s]+RETRY_AFTER_MAX_PERIOD:[%s](s), set to Now+RETRY_AFTER_MAX_PERIOD:[%s]", FormatUtil.timestampToDate(retryAfter), FormatUtil.timestampToDate(now), Constants.RETRY_AFTER_MAX_PERIOD, FormatUtil.timestampToDate(retryAfterLimit)));
                retryAfter = retryAfterLimit;
                setRetryAfter(requestType, retryAfter);
            }
            return retryAfter;
        }else{
            return Long.MIN_VALUE;
        }
    }

    public long parseMoratoriumTimeHttp(String retryAfter) {
        try {
            long ms = Long.parseLong(retryAfter) * 1000;
            return (ms + System.currentTimeMillis());
        } catch (NumberFormatException nfe) {
            try {
                return HttpDateTimeUtil.parseDateTime(retryAfter);
            } catch (IllegalArgumentException iae) {
                LOGGER.debug("parseMoratoriumTimeHttp error, ignore this value. ", iae);
                return Long.MIN_VALUE;
            }
        }
    }

}
