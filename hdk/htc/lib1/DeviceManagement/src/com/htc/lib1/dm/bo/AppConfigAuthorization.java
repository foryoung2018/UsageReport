package com.htc.lib1.dm.bo;

import java.util.Map;

/**
 * Created by Joe_Wu on 8/23/14.
 */
public class AppConfigAuthorization {

    // Authorization code.
    private int code;

    // Authorization data...a JSON object.
    private Map<String, Object> data;

    // --------------------------------------------------

    // Private no-arg constructor for Jackson de-serialization.
    private AppConfigAuthorization() {}

    public static AppConfigAuthorization createForTest(int authCode, Map<String, Object> authData) {
        return new AppConfigAuthorization(authCode, authData);
    }

    // Test...
    private AppConfigAuthorization(int authCode, Map<String, Object> authData) {
        this.code = authCode;
        this.data = authData;
    }

    // --------------------------------------------------

    public int getCode() {
        return code;
    }

    public Map<String, Object> getData() {
        return data;
    }

    // --------------------------------------------------

    @Override
    public String toString() {
        return String.format("<%s: code=%s, data=%s>", this.getClass().getSimpleName(), code, data);
    }
}
