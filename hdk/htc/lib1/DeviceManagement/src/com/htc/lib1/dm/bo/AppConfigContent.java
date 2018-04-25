package com.htc.lib1.dm.bo;

import java.util.Map;

/**
 * Created by Joe_Wu on 8/23/14.
 */
public class AppConfigContent {
    // Configuration information.
    // A JSON object whose schema is defined by the configuration author.
    private Map<String, Object> config;

    // Authorization information.
    private AppConfigAuthorization authorization;

    // --------------------------------------------------

    // Private no-arg constructor for Jackson deserialization.
    private AppConfigContent() {}

    // --------------------------------------------------

    public Map<String, Object> getConfig() {
        return config;
    }

    public AppConfigAuthorization getAuthorization() {
        return authorization;
    }

    // --------------------------------------------------

    @Override
    public String toString() {
        return String.format("<%s: config=%s, authorization=%s>", this.getClass().getSimpleName(), config, authorization);
    }
}
