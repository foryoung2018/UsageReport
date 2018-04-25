package com.htc.lib1.dm.bo;

import com.google.gson.Gson;
import com.htc.lib1.dm.exception.DMJsonParseException;
import com.htc.lib1.dm.logging.Logger;

import java.net.URI;

/**
 * Created by Joe_Wu on 8/23/14.
 */
public class AppConfigMeta {
    private static final Logger LOGGER = Logger.getLogger("[DM]",AppConfigMeta.class);

    // Server supplied request status.
    private RequestStatus status;

    // When to next contact the server (in seconds).
    // A value of 0 implies that the client should never again contact the server.
    private Long ttl;

    // The URI to use for the next server request.
    private URI nextUri;

    // Config ID.
    // An opaque identifier defined by the server used to identify the content.
    private String configID;

    // --------------------------------------------------

    // Private no-arg constructor for Jackson de-serialization.
    private AppConfigMeta() {}


    public static AppConfigMeta parseAppConfigFromJson(String jsonString) throws DMJsonParseException {
        // Extract Config value
        AppConfigMeta appConfigMeta = null;
        try {
            appConfigMeta = new Gson().fromJson(jsonString, AppConfigMeta.class);
        }catch(Exception ex) {
            LOGGER.debugS("Cannot parse JsonString to AppConfigMeta obj. JsonString:"+jsonString+"\r\n", ex);
            throw new DMJsonParseException("Cannot parse JsonString to AppConfigMeta obj. ",ex);
        }
        return appConfigMeta;
    }

    // --------------------------------------------------

    public RequestStatus getStatus() {
        return status;
    }

    public Long getTtl() {
        return ttl;
    }

    public URI getNextUri() {
        return nextUri;
    }

    public String getConfigID() {
        return configID;
    }

    @Override
    public String toString() {
        return String.format("<%s: status=%s, ttl=%d seconds, nextUri=%s, configID=%s>", this.getClass().getSimpleName(), status, ttl, configID);
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }
}
