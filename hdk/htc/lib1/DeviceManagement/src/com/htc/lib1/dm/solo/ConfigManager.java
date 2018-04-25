package com.htc.lib1.dm.solo;

import android.app.Application;
import android.os.Looper;
import com.google.gson.Gson;
import com.htc.lib1.dm.bo.*;
import com.htc.lib1.dm.cache.DMCacheManager;
import com.htc.lib1.dm.constants.Constants;
import com.htc.lib1.dm.constants.RequestDispositionStatus;
import com.htc.lib1.dm.constants.RestConstants;
import com.htc.lib1.dm.env.AppEnv;
import com.htc.lib1.dm.env.FactoryPresetsEnv;
import com.htc.lib1.dm.env.NetworkEnv;
import com.htc.lib1.dm.exception.*;
import com.htc.lib1.dm.network.*;
import com.htc.lib1.dm.util.DMStatusHelper;
import com.htc.lib1.dm.util.FormatUtil;
import com.htc.lib1.dm.util.RetryHelper;
import com.htc.lib1.dm.logging.Logger;
import java.util.*;
import java.util.concurrent.*;

public class ConfigManager {

    private static final Logger LOGGER = Logger.getLogger("[DM]",ConfigManager.class);
    private static ConfigManager _instance;
    private ExecutorService executor = Executors.newFixedThreadPool(5);

    private Application mApplication;
    private String versionKey;
    private Gson gson;

    private DMCacheManager cacheManager;
    private RetryHelper retryHelper;
    private DMStatusHelper statusHelper;
    private DMRestClient restClient;

    private FactoryPresetsEnv factoryPresetsEnv;
    private AppEnv appEnv;
    private NetworkEnv networkEnv;

    private String appConfigCacheKey;
    private String appConfigMetaCacheKey;

    private Boolean hasPutProfile;

    private ConfigManager(Application application, String versionKey) throws DMCacheException, DMFatalException {
        try {
            LOGGER.informative("new ConfigManager instance. ver:" + Constants.LIBRARY_VERSION_NAME);
            this.versionKey = versionKey;
            this.mApplication = application;
            this.cacheManager = DMCacheManager.getInstance(mApplication, versionKey);
            factoryPresetsEnv = FactoryPresetsEnv.get(mApplication);
            appEnv = AppEnv.get(mApplication);
            networkEnv = NetworkEnv.get(mApplication);
            retryHelper = RetryHelper.getInstance(cacheManager);
            statusHelper = DMStatusHelper.getInstance(cacheManager);
            restClient = DMRestClient.getInstance(application, retryHelper);
            appConfigCacheKey = cacheManager.getCacheKey_AppConfig();
            appConfigMetaCacheKey = cacheManager.getCacheKey_AppConfigMeta();
            gson = new Gson();
            hasPutProfile = false;
        }catch(DMCacheException ex){
            throw ex;
        }catch(Throwable ex){
            throw new DMFatalException(ex);
        }
    }

    public static synchronized ConfigManager getInstance(Application application, String versionKey) throws DMException {
        if(versionKey==null)
            throw new DMWrongVersionKeyException("VersionKey cannot be null. Please contact DM team to apply correct VersionKey.");
        if (_instance == null || !_instance.versionKey.equals(versionKey)) {
            _instance = new ConfigManager(application, versionKey);
            LOGGER.debug("ConfigManager instance has been created. "+_instance.toString());
        }
        return _instance;
    }

    public void init() {
        LOGGER.informative("ConfigManager init().");
        Runnable taskPutProfile = new Runnable() {
            @Override
            public void run() {
                try {
                    // PutProfile if not yet been put.
                    if(checkIfNeedPutProfile()) {
                        LOGGER.informative("init() => DM PutProfile state is "+statusHelper.getProfileStatusDisplayName()+" trigger putProfile process.");
                        checkAndPutProfile();
                    }
                } catch(Throwable ex) {
                    LOGGER.error("Error in init ConfigManager. ", ex);
                    ex.printStackTrace();
                }
            }
        };
        Runnable taskGetConfig = new Runnable() {
            @Override
            public void run() {
                try {
                    // Trigger getAppConfig if first time connect to Server.
                    if(checkIfNeedGetConfigFromServer()) {
                        LOGGER.informative("init() => DM GetConfig state is "+statusHelper.getConfigStatusDisplayName()+" trigger getAppConfig process.");
                        getAppConfig();
                    }
                } catch(Throwable ex) {
                    LOGGER.error("Error in init ConfigManager. ", ex);
                    ex.printStackTrace();
                }
            }
        };
        executor.execute(taskPutProfile);
        executor.execute(taskGetConfig);
    }

    public String getStringValue(String key, String defaultValue) throws DMThreadException {
        return getConfigValue(String.class,key,defaultValue);
    }

    public FutureTask<String> getStringValue(String key) {
        return getConfigValue(String.class,key);
    }

    public Long getLongValue(String key, Long defaultValue) throws DMThreadException {
        return getConfigValue(Long.class,key,defaultValue);
    }

    public FutureTask<Long> getLongValue(String key) {
        return getConfigValue(Long.class,key);
    }

    public Integer getIntegerValue(String key, Integer defaultValue) throws DMThreadException {
        return getConfigValue(Integer.class,key,defaultValue);
    }

    public FutureTask<Integer> getIntegerValue(String key) {
        return getConfigValue(Integer.class,key);
    }

    public Double getDoubleValue(String key, Double defaultValue) throws DMThreadException {
        return getConfigValue(Double.class,key,defaultValue);
    }

    public FutureTask<Double> getDoubleValue(String key) {
        return getConfigValue(Double.class,key);
    }

    public Boolean getBooleanValue(String key, Boolean defaultValue) throws DMThreadException {
        return getConfigValue(Boolean.class,key,defaultValue);
    }

    public FutureTask<Boolean> getBooleanValue(String key) {
        return getConfigValue(Boolean.class,key);
    }

    public <T> T getConfigValue(Class<T> type, final String key, final T defaultValue) throws DMThreadException {
        // check should NOT be run in MainThread.
        mainThreadChecking();

        AppConfig cachedConfig;
        synchronized (this) {
            cachedConfig = cacheManager.getDataFromCache(AppConfig.class, appConfigCacheKey);
        }

        try {
            LOGGER.debugS(String.format("getConfigValue(key:[%s], defaultValue:[%s])",key,defaultValue));
            if(!hasPutProfile && checkIfNeedPutProfile()) {
                // Check if need to upload profile
                Runnable checkPutProfileTask = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            checkAndPutProfile();
                        } catch (Throwable ex) {
                            LOGGER.error("Error in PutProfile which triggered by getConfig. ", ex);
                            ex.printStackTrace();
                        }
                    }
                };
                executor.execute(checkPutProfileTask);
            }

            AppConfig appConfig;
            if(cachedConfig == null) {
                // execute getConfig process
                appConfig = getAppConfig();
            }else{
                // use cached AppConfig
                appConfig = cachedConfig;
                if(isConfigExpired(appConfig) && checkIfNeedGetConfigFromServer()){
                    // async notify update config
                    Runnable notifyConfigUpdateTask = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getAppConfig();
                            } catch(Throwable ex) {
                                LOGGER.error("Error in PutProfile which triggered by getConfig. ", ex);
                                ex.printStackTrace();
                            }
                        }
                    };
                    executor.execute(notifyConfigUpdateTask);
                }
            }

            if(appConfig == null)
                throw new DMNoConfigException("No AppConfig.");
            if(appConfig.getAppConfigContent() == null)
                throw new DMNoConfigException("Got AppConfig, but no AppConfigContent.");
            // Handle Unauthorized
            if(appConfig.getAppConfigContent().getAuthorization().getCode() != Constants.AUTHORIZATION_AUTHORIZED){
                throw new DMUnauthorizedException(appConfig.getAppConfigContent().getAuthorization().getCode(),appConfig.getAppConfigContent().getAuthorization().getData());
            }
            if(appConfig.getAppConfigContent().getConfig()==null || !appConfig.getAppConfigContent().getConfig().containsKey(key))
                throw new DMNoKeyInConfigException("Has AppConfig but no value for key:" + key);

            // get value
            String stringValue = appConfig.getAppConfigContent().getConfig().get(key).toString();
            if(type.isAssignableFrom(String.class))
                return (T)stringValue;
            else{
                return gson.fromJson(stringValue,type);
            }
        } catch (DMNoConfigException e) {
            // Ignore DMNoConfig
            LOGGER.debug("Use DefaultValue for key:["+key+"] cause:"+e.getMessage());
            return defaultValue;
        } catch (DMNoKeyInConfigException e) {
            // Ignore DMNoConfig
            LOGGER.debug("Use DefaultValue for key:["+key+"] cause:"+e.getMessage());
            return defaultValue;
        } catch (DMException e) {
            LOGGER.error("Use DefaultValue for key:["+key + "] cause:"+e.getMessage());
            return defaultValue;
        } catch (Throwable e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
        LOGGER.debug("Use DefaultValue cause: No available value.");
        return defaultValue;
    }

    public <T> FutureTask<T> getConfigValue(final Class<T> type, final String key) {
        LOGGER.informative("getConfigValue running... key:["+ key +"] ts:"+System.currentTimeMillis());

        //TODO check should NOT be run in MainThread.
//        mainThreadChecking();

        FutureTask<T> getConfigValueTask = new FutureTask<T>( new Callable<T>() {
            @Override
            public T call() throws DMException {
                AppConfig cachedConfig = cacheManager.getDataFromCache(AppConfig.class, appConfigCacheKey);
                AppConfig appConfig = cachedConfig;
                if(appConfig == null) {
                    // execute getConfig process
                    appConfig = getAppConfig();
                }else{
                    if(isConfigExpired(appConfig) && checkIfNeedGetConfigFromServer()){
                        appConfig = getAppConfig();
                    }
                }

                if(appConfig == null)
                    throw new DMNoConfigException("No AppConfig.");
                if(appConfig.getAppConfigContent() == null)
                    throw new DMNoConfigException("Got AppConfig, but no AppConfigContent.");
                // Handle Unauthorized
                if(appConfig.getAppConfigContent().getAuthorization().getCode() != Constants.AUTHORIZATION_AUTHORIZED){
                    throw new DMUnauthorizedException(appConfig.getAppConfigContent().getAuthorization().getCode(),appConfig.getAppConfigContent().getAuthorization().getData());
                }
                if(appConfig.getAppConfigContent().getConfig()==null || !appConfig.getAppConfigContent().getConfig().containsKey(key))
                    throw new DMNoKeyInConfigException("Has AppConfig but no value for key:" + key);

                // get value
                String stringValue = appConfig.getAppConfigContent().getConfig().get(key).toString();
                if(type.isAssignableFrom(String.class))
                    return (T)stringValue;
                else{
                    return gson.fromJson(stringValue,type);
                }
            }
        });
        executor.submit(getConfigValueTask);

        // Check if need to upload profile
        if(!hasPutProfile) {
            Runnable checkPutProfileTask = new Runnable() {
                @Override
                public void run() {
                    try {
                        checkAndPutProfile();
                    } catch (Throwable ex) {
                        LOGGER.error("Error in PutProfile which triggered by getConfig. ", ex);
                        ex.printStackTrace();
                    }
                }
            };
            executor.execute(checkPutProfileTask);
        }

        return getConfigValueTask;
    }

    private synchronized Long getTtl() throws DMException {
        if (cacheManager.isDataInCache(Long.class, cacheManager.getCacheKey_AppConfigMetaTtl())) {
            return cacheManager.getDataFromCache(Long.class, cacheManager.getCacheKey_AppConfigMetaTtl());
        } else {
            return Constants.DEFAULT_TTL;
        }
    }

    private synchronized AppConfig getAppConfig() throws DMException {
        LOGGER.informative("getAppConfig running... status:"+statusHelper.getConfigStatusDisplayName()+" ts:"+System.currentTimeMillis());
        // Prepare Config Meta for NextURI and TTL.
        String nextURI = null;
        AppConfigMeta configMeta = getAppConfigMeta();

        if(configMeta!=null) {
            nextURI = configMeta.getNextUri().toString();
            Long ttl = configMeta.getTtl();
            cacheManager.putDataInCache(Long.class,cacheManager.getCacheKey_AppConfigMetaTtl(),ttl);
        }else{
            LOGGER.debug("No ConfigMeta and using default value for NextURI & TTL. ");
            nextURI = String.format("%s/%s/apps/%s/versions/%s", factoryPresetsEnv.getServiceBaseUri(), RestConstants.URI_PLATFORM_ANDROID, appEnv.getAppID(), versionKey);
        }
        LOGGER.debugS("GetConfig with NextURI:", nextURI);

        // Check if first time getConfig without local cache
        AppConfig appConfig = getConfigFromCache(appConfigCacheKey);

        if(appConfig==null){
            // Blocking call for first time getConfig
            LOGGER.debug("No AppConfig cache in local, first time getConfig from server.");

            switch(statusHelper.getConfigStatus()){
                case DMStatus.GET_CONFIG_INIT:
                    // First time getConfig from server
                    try {
                        appConfig = getAppConfigFromServer(nextURI);
                    }catch(DMException exception){
                        LOGGER.debug("First time call getConfig fail and retry once.");
                        // Retry once immediately for first time getConfig.
                        appConfig = getAppConfigFromServer(nextURI,true);
                    }
                    break;
                case DMStatus.GET_CONFIG_RUNNING:
                    // Should not going to this step, so just block this thread and retry in getAppConfigFromServer method.
                    if (retryHelper.needRetryGetConfig()) {
                        LOGGER.debug("status is Running but trying to getConfig from server.");
                        // getConfig from server
                        appConfig = getAppConfigFromServer(nextURI);
                    } else {
                        throw new DMNoConfigException("No config value in local cache, and cannot invoke server in this time. (still under retry sleep time)");
                    }
                    break;
                case DMStatus.GET_CONFIG_RETRY:
                    if (retryHelper.needRetryGetConfig()) {
                        LOGGER.debug("retry getConfig from server.");
                        // retry getConfig from server
                        appConfig = getAppConfigFromServer(nextURI);

                    } else {
                        throw new DMNoConfigException("No config value in local cache, and cannot invoke server in this time. (still under retry sleep time)");
                    }
                    break;
                case DMStatus.GET_CONFIG_CACHE_EXPIRED:
                case DMStatus.GET_CONFIG_CACHE_OK:
                default:
                    // Wrong status
                    LOGGER.warning("GetConfig has no AppConfig but the status is "+statusHelper.getConfigStatusDisplayName() + " (force reset to INIT and get config from server)");
                    statusHelper.setConfigStatus(DMStatus.GET_CONFIG_INIT);
                    try {
                        appConfig = getAppConfigFromServer(nextURI);
                    }catch(DMException exception) {
                        LOGGER.debug("First time call getConfig fail and retry once.");
                        // Retry once immediately for first time getConfig.
                        appConfig = getAppConfigFromServer(nextURI,true);
                    }
                    break;
            }

            if(appConfig==null) {
                throw new DMNoConfigException("Cannot load Config from server and has no local cache.");
            }
        }else{
            // Config exists in cache and async check for refresh.
            switch(statusHelper.getConfigStatus()){
                case DMStatus.GET_CONFIG_RETRY:
                    // Check if need retry (over retry sleep time)
                case DMStatus.GET_CONFIG_CACHE_OK:
                case DMStatus.GET_CONFIG_CACHE_EXPIRED:
                    if(isConfigExpired(appConfig) && checkIfNeedGetConfigFromServer()){
                        // async call getAppConfigFromServer.
                        // TODO use Android Priority Job Queue.
                        final String fNextURI = nextURI;
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    LOGGER.debug("Background refreshing GetConfig...");
                                    getAppConfigFromServer(fNextURI);
                                } catch (DMException e) {
                                    LOGGER.error("Background refresh GetConfig fail. ", e);
                                    e.printStackTrace();
                                }
                            }
                        };
                        executor.execute(task);
                    }
                    break;
                case DMStatus.GET_CONFIG_RUNNING:
                    // handle timeout
                    if(cacheManager.isDataInCache(Long.class,cacheManager.getCacheKey_ConfigRunningTime())) {
                        Long ts = cacheManager.getDataFromCache(Long.class, cacheManager.getCacheKey_ConfigRunningTime());
                        if((System.currentTimeMillis() - ts) > Constants.GET_CONFIG_RUNNING_TIMEOUT){
                            // timeout and update status to retry
                            statusHelper.setConfigStatus(DMStatus.GET_CONFIG_RETRY);
                        }
                    }else{
                        cacheManager.putDataInCache(Long.class,cacheManager.getCacheKey_ConfigRunningTime(),System.currentTimeMillis());
                    }
                    // do nothing, just return cached value
                    break;
                case DMStatus.GET_CONFIG_INIT:
                default:
                    // Wrong status
                    LOGGER.warning("GetConfig has AppConfig but the status is "+statusHelper.getConfigStatusDisplayName() + " (do nothing)");
                    break;
            }
        }

        LOGGER.informative("getAppConfig done. status:"+statusHelper.getConfigStatusDisplayName()+" ts:"+System.currentTimeMillis());
        return appConfig;
    }

    private void mainThreadChecking() throws DMThreadException {
        // check should NOT be run in MainThread.
        if(Looper.getMainLooper().getThread() == Thread.currentThread()){
            throw new DMThreadException("DM cannot be invoked in main thread.");
        }
    }

    private synchronized void checkAndPutProfile() throws DMException {
        if(hasPutProfile || !checkIfNeedPutProfile())
            return;

        if(!networkEnv.isNetworkConnected()) {
            LOGGER.informative("checkAndPutProfile but no Network connected -> Ignore.");
            return;
        }

        LOGGER.debug("checkAndPutProfile put profile to server. ts:"+FormatUtil.timestampToDate(System.currentTimeMillis()));

        // Update status to running
        statusHelper.setProfileStatus(DMStatus.PUT_PROFILE_RUNNING);

        String requestUri = factoryPresetsEnv.buildConfiguredBootstrapDeviceManifestResourceUri().replace(RestConstants.URI_PLATFORM_VAR, RestConstants.URI_PLATFORM_ANDROID);

        DeviceProfile profile = new DeviceProfile(mApplication);

        // TODO: check android type
        String type = "hms";

        // Gen DeviceManifest
        AppManifest appManifest = new AppManifest(appEnv.getAppID(), versionKey, appEnv.getAppVersionCode(), appEnv.getAppVersionName(), null);
        List<AppManifest> appManifestList = new ArrayList<AppManifest>();
        appManifestList.add(appManifest);
        Map<String,Object> meta = new HashMap<String,Object>();
        meta.put("embedded",true);
        DeviceManifest deviceManifest = new DeviceManifest(type, profile,appManifestList,meta);

        try {
            // Update latest invoke time
            Long latestInvokeTime = System.currentTimeMillis();
            retryHelper.setPutProfileLatestInvokeTime(latestInvokeTime);

            // put profile
            restClient.putProfile(requestUri, deviceManifest);

            // Set Success
            retryHelper.setSuccess(DMRequestType.PUT_PROFILE, System.currentTimeMillis());
            statusHelper.setProfileStatus(DMStatus.PUT_PROFILE_DONE);
        } catch (Exception ex) {
            LOGGER.error("PutProfile got error. Set PutProfile state to fail. ", ex);
            ex.printStackTrace();

            try {
                statusHelper.setProfileStatus(DMStatus.PUT_PROFILE_RETRY);
//            if(ex instanceof DMHttpException){
//            }
                retryHelper.setFail(DMRequestType.PUT_PROFILE, System.currentTimeMillis());
            } catch (DMException dmex) {
                LOGGER.warning(dmex);
            }
        }
        LOGGER.informative("checkAndPutProfile done... status:"+statusHelper.getProfileStatusDisplayName()+" ts:"+FormatUtil.timestampToDate(System.currentTimeMillis()));
    }

    // TODO - Wait Check
    private synchronized boolean checkIfNeedPutProfile() {
        boolean needPutProfile = false;
        Long latestTime = null;
        Long now = System.currentTimeMillis();

        try {
            Integer status = statusHelper.getProfileStatus();
            switch (status) {
                case DMStatus.PUT_PROFILE_INIT:
                    // First time
                    needPutProfile = true;
                    break;

                case DMStatus.PUT_PROFILE_RUNNING:
                    // handle timeout
                    if (cacheManager.isDataInCache(Long.class, cacheManager.getCacheKey_ProfileRunningTime())) {
                        Long ts = cacheManager.getDataFromCache(Long.class, cacheManager.getCacheKey_ProfileRunningTime());
                        if ((System.currentTimeMillis() - ts) > Constants.PUT_PROFILE_RUNNING_TIMEOUT) {
                            // timeout and update status to retry
                            statusHelper.setProfileStatus(DMStatus.PUT_PROFILE_RETRY);
                        }
                    } else {
                        cacheManager.putDataInCache(Long.class, cacheManager.getCacheKey_ConfigRunningTime(), System.currentTimeMillis());
                    }
                    needPutProfile = false;
                    break;

                case DMStatus.PUT_PROFILE_RETRY:
                    Long retryAfter = retryHelper.getRetryAfter(DMRequestType.PUT_PROFILE);
                    if (retryAfter <= now) {
                        latestTime = retryHelper.getPutProfileLatestInvokeTime();
                        if (latestTime != null) {
                            if ((latestTime + (Constants.PUT_PROFILE_RETRY_PERIOD * 1000)) < now) {
                                // latest time has over retry period
                                needPutProfile = true;
                            }
                        } else {
                            // has no latest time, should be first time.
                            LOGGER.warning("Wrong PutProfile status, isRetry but no LatestInvokeTime.");
                            needPutProfile = true;
                        }
                    } else {
                        // under retry-after period
                        LOGGER.debug("PutProfile is under Retry-After period, ignore retry. Retry-After:" + FormatUtil.timestampToDate(retryAfter));
                        needPutProfile = false;
                    }
                    break;

                case DMStatus.PUT_PROFILE_DONE:
                default:
                    hasPutProfile = true;
                    needPutProfile = false;
                    break;
            }
        } catch (DMException ex) {
            LOGGER.warning(ex);
            needPutProfile = false;
        }

        LOGGER.debug("checkIfNeedPutProfile() ProfileStatus:" + statusHelper.getProfileStatusDisplayName() + " latestTime:" + (latestTime == null ? 0l : FormatUtil.timestampToDate(latestTime)) + " now:" + FormatUtil.timestampToDate(now) + " (timediff:" + (latestTime == null ? 0l : (now - latestTime)) + ") needPutProfile:" + needPutProfile);

        return needPutProfile;
    }

    private synchronized AppConfig getConfigFromCache(String configKey) {
        AppConfig appConfig = null;

        // get AppConfig from cache
        try {
            if (cacheManager.isDataInCache(AppConfig.class, configKey)) {
                appConfig = cacheManager.getDataFromCache(AppConfig.class, configKey);
            }
        }catch (Exception ex){
            LOGGER.error("get AppConfig from Cache failed. ", ex);
            ex.printStackTrace();
        }

        return appConfig;
    }

    private synchronized Boolean checkIfNeedGetConfigFromServer() {
        Boolean isNeed = false;
        Long retryAfter = 0l;
        String configKey = appConfigCacheKey;

        try {
            Long ttl = getTtl();
            // non-blocking check TTL and refresh config

            Integer status = statusHelper.getConfigStatus();
            Date latestTime = cacheManager.getDateOfDataInCache(configKey);
            Boolean isInitCall = (status == DMStatus.GET_CONFIG_INIT);
            Long now = System.currentTimeMillis();
            Boolean isConfigExpired = latestTime==null ? false : ( (latestTime.getTime() + (ttl * 1000)) < System.currentTimeMillis() );

            switch(status){
                case DMStatus.GET_CONFIG_INIT:
                    isNeed = true;
                    break;
                case DMStatus.GET_CONFIG_RUNNING:
                    // handle timeout
                    if(cacheManager.isDataInCache(Long.class,cacheManager.getCacheKey_ConfigRunningTime())) {
                        Long ts = cacheManager.getDataFromCache(Long.class, cacheManager.getCacheKey_ConfigRunningTime());
                        if((System.currentTimeMillis() - ts) > Constants.GET_CONFIG_RUNNING_TIMEOUT){
                            // timeout and update status to retry
                            statusHelper.setConfigStatus(DMStatus.GET_CONFIG_RETRY);
                        }
                    }else{
                        cacheManager.putDataInCache(Long.class,cacheManager.getCacheKey_ConfigRunningTime(),System.currentTimeMillis());
                    }
                    isNeed = false;
                    break;
                case DMStatus.GET_CONFIG_RETRY:
                    retryAfter = retryHelper.getRetryAfter(DMRequestType.GET_CONFIG);
                    if(retryAfter < now) {
                        if (retryHelper.needRetryGetConfig()) {
                            LOGGER.debug("retry refreshConfig from server.");
                            // retry getConfig from server
                            isNeed = true;
                        } else {
                            LOGGER.debug(String.format("Config has expired, but cannot invoke server in this time. (still under retry sleep time). config_timestamp:[%s], currentTime:[%s], ttl:[%s]", latestTime == null ? "null" : FormatUtil.timestampToDate(latestTime.getTime()), FormatUtil.timestampToDate(System.currentTimeMillis()), ttl));
                        }
                    }else{
                        LOGGER.debug(String.format("GetConfig status[%s] is under Retry-After period, ignore retry. Retry-After:[%s], now:[%s]",statusHelper.getConfigStatusDisplayName(),FormatUtil.timestampToDate(retryAfter), FormatUtil.timestampToDate(System.currentTimeMillis())));
                    }
                    break;
                case DMStatus.GET_CONFIG_CACHE_OK:
                case DMStatus.GET_CONFIG_CACHE_EXPIRED:
                    if (isConfigExpired) {
                        retryAfter = retryHelper.getRetryAfter(DMRequestType.GET_CONFIG);
                        if(retryAfter < now) {
                                LOGGER.debug(String.format("Config has expired, need invoke server. config_timestamp:[%s], currentTime:[%s], ttl:[%s]", latestTime == null ? "null" : FormatUtil.timestampToDate(latestTime.getTime()), FormatUtil.timestampToDate(System.currentTimeMillis()), ttl));
                                isNeed = true;
                        }else{
                            LOGGER.debug(String.format("GetConfig status[%s] is under Retry-After period, ignore retry. Retry-After:[%s]",statusHelper.getConfigStatusDisplayName(),FormatUtil.timestampToDate(retryAfter)));
                        }
                    }
                    break;

            }
            LOGGER.debug(String.format("checkIfNeedGetConfigFromServer() => ConfigStatus:[%s] latestTime:[%s], isInitCall:[%s], isConfigExpired:[%s], isNeed:[%s].",statusHelper.getConfigStatusDisplayName(),latestTime, isInitCall, isConfigExpired, isNeed ));
        } catch (Exception ex) {
            LOGGER.error("Check checkIfNeedGetConfigFromServer fail. ", ex);
            ex.printStackTrace();
        }

        return isNeed;
    }

    /**
     * First time getConfig from server and update in local cache. (blocking, retry 1 time immediately)
     * @param nextURI
     * @return
     * @throws DMException
     */
    private synchronized AppConfig getAppConfigFromServer(String nextURI) throws DMException {
        return getAppConfigFromServer(nextURI,false);
    }
    private synchronized AppConfig getAppConfigFromServer(String nextURI, Boolean isForce) throws DMException {
        AppConfig appConfig = null;

        // Check network status
        if(!networkEnv.isNetworkConnected()){
            LOGGER.informative("Try to get AppConfig from Server but no Network connected -> Ignore.");
        } else {
            // Double check if no need to getConfig.
            if (isForce || checkIfNeedGetConfigFromServer()) {
                if (isForce)
                    LOGGER.debug("force call getAppConfigFromServer()");

                // Load data from server
                LOGGER.debug("Load config value from server.");

                // Update status to running
                statusHelper.setConfigStatus(DMStatus.GET_CONFIG_RUNNING);
                cacheManager.putDataInCache(Long.class,cacheManager.getCacheKey_ConfigRunningTime(),System.currentTimeMillis());

                // Set latestInvokeTime
                Long latestInvokeTime = System.currentTimeMillis();
                retryHelper.setGetConfigLatestInvokeTime(latestInvokeTime);

                try {
                    AppConfig appConfigFromServer = restClient.getAppConfig(nextURI);
                    if (checkAppConfigAndUpdateCache(appConfigFromServer)) {
                        appConfig = appConfigFromServer;
                        retryHelper.setSuccess(DMRequestType.GET_CONFIG, System.currentTimeMillis());
                        statusHelper.setConfigStatus(DMStatus.GET_CONFIG_CACHE_OK);
                    } else {
                        retryHelper.setFail(DMRequestType.GET_CONFIG, System.currentTimeMillis());
                        statusHelper.setConfigStatus(DMStatus.GET_CONFIG_RETRY);
                    }
                }catch (DMException ex) {
                    // DMException
                    retryHelper.setFail(DMRequestType.GET_CONFIG, System.currentTimeMillis());
                    statusHelper.setConfigStatus(DMStatus.GET_CONFIG_RETRY);
                    throw ex;
                } catch (Exception e) {
                    // Non-DMException
                    retryHelper.setFail(DMRequestType.GET_CONFIG, System.currentTimeMillis());
                    statusHelper.setConfigStatus(DMStatus.GET_CONFIG_RETRY);
                    throw new DMGetConfigException("GetConfig from server fail. ", e);
                }
            } else {
                //No need to get from Server, double check if local cache has been updated.
                appConfig = getConfigFromCache(appConfigCacheKey);
            }
        }
        return appConfig;
    }

    private synchronized boolean checkAppConfigAndUpdateCache(AppConfig appConfig) throws DMWrongConfigStatusException {
        if(appConfig==null)
            return false;

        // Check App Config Meta
        if(appConfig.getAppConfigMeta()==null){
            LOGGER.debug("AppConfig returned from server has no AppConfigMeta info.");
            throw new DMWrongConfigStatusException("AppConfig returned from server has no AppConfigMeta info.");
        }

        AppConfigMeta configMeta = appConfig.getAppConfigMeta();
        if(configMeta.getStatus()==null || configMeta.getStatus().getCode() != RequestDispositionStatus.OK){
            LOGGER.debug("AppConfig returned from server with wrong status.");
            if(configMeta.getNextUri()!=null && configMeta.getTtl()!=null) {
                LOGGER.debug("The status of AppConfig is wrong, but keep nextURI and TTL but ignore AppConfigContent.");
                // AppConfigMeta has NextURI and TTL then keep then in cache.
                try {
                    //TODO Should we keep AppConfigMeta with wrong status code into persistent local cache and with no TTL expired?
                    cacheManager.putDataInCache(AppConfigMeta.class, appConfigMetaCacheKey, configMeta);
                    Long ttl = configMeta.getTtl();
                    cacheManager.putDataInCache(Long.class,cacheManager.getCacheKey_AppConfigMetaTtl(),ttl);
                } catch (Exception e) {
                    LOGGER.warning("update AppConfigMeta to cache fail.");
                }
            }
            // Handle HTTP 200 but Status 11xx response
            int statusCode = configMeta.getStatus().getCode();
            switch(statusCode) {
                case RequestDispositionStatus.NO_CONTENT:
                case RequestDispositionStatus.UNDECLARED_APP:
                case RequestDispositionStatus.NO_PUBLISHED_CONTENT:
                case RequestDispositionStatus.UNKNOWN_APP_ID:
                case RequestDispositionStatus.UNKNOWN_APP_VERSION_KEY:
                    LOGGER.warning("Status code is RequestDispositionStatus:"+statusCode+" still process AppConfig object.");
                    break;
                default:
                    throw new DMWrongConfigStatusException("AppConfig returned from server with wrong status. Status:"+configMeta.getStatus());
            }
        }

        // set Cache
        try {
            cacheManager.putDataInCache(AppConfig.class, appConfigCacheKey, appConfig);
        } catch (Exception e) {
            LOGGER.warning("update AppConfig to persistent cache fail.");
        }
        if(appConfig.getAppConfigMeta()!=null) {
            try {
                cacheManager.putDataInCache(AppConfigMeta.class, appConfigMetaCacheKey, configMeta);
            } catch (Exception e) {
                LOGGER.warning("update AppConfigMeta to cache fail.");
            }
        }
        return true;
    }

    private synchronized AppConfigMeta getAppConfigMeta() {
        AppConfigMeta configMeta = null;
        if(cacheManager.isDataInCache(AppConfigMeta.class, appConfigMetaCacheKey)){
            configMeta = cacheManager.getDataFromCache(AppConfigMeta.class, appConfigMetaCacheKey);
        }
        return configMeta;
    }

    private synchronized boolean isConfigExpired(AppConfig appConfig) {
        Date latestTime = cacheManager.getDateOfDataInCache(appConfigCacheKey);
        if(appConfig == null || latestTime == null) {
            // no config = expired (need to get from server)
            return true;
        }
        Boolean isConfigExpired = ( (latestTime.getTime() + (appConfig.getAppConfigMeta().getTtl() * 1000)) < System.currentTimeMillis() );
        return isConfigExpired;
    }

}
