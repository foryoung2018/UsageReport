package com.htc.lib1.dm.bo;

import com.google.gson.Gson;
import com.htc.lib1.dm.exception.DMJsonParseException;
import com.htc.lib1.dm.logging.Logger;

/**
 * Created by Joe_Wu on 8/23/14.
 */
public class AppConfig {
    private static final Logger LOGGER = Logger.getLogger("[DM]",AppConfig.class);

    // The application ID that this corresponds to.
    private String appID;

    // The application version key that this corresponds to.
    private String versionKey;

    // Request meta data...
    private AppConfigMeta meta;

    // Content...
    private AppConfigContent content;

    public AppConfig() {}


    public static AppConfig parseAppConfigFromJson(String jsonString) throws DMJsonParseException {
        // Extract Config value
        AppConfig appConfig = null;
        try {
            appConfig = new Gson().fromJson(jsonString, AppConfig.class);
        }catch(Exception ex) {
            LOGGER.debugS("Cannot parse JsonString to AppConfig obj. JsonString:"+jsonString+"\r\n", ex);
            throw new DMJsonParseException("Cannot parse JsonString to AppConfig obj. ",ex);
        }
        return appConfig;
    }

    public String getAppID() {
        return appID;
    }

    public String getVersionKey() {
        return versionKey;
    }

    public AppConfigMeta getAppConfigMeta() {
        return meta;
    }

    public AppConfigContent getAppConfigContent() {
        return content;
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }
}
