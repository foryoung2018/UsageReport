package com.htc.lib1.dm.bo;

import java.util.Map;

/**
 * Created by Joe_Wu on 8/23/14.
 */
public class RequestStatus {
    // --------------------------------------------------

    // Status code.
    // These may be codes coming from the server for server-side errors
    // or codes from DM.apk for client-side errors.
    private int code;

    // Status data...a JSON object.
    // Specific to each code.
    private Map<String, Object> data;

    // --------------------------------------------------

    // Private no-arg constructor for Jackson de-serialization.
    private RequestStatus() {}

    public static RequestStatus createForTest(int statusCode, Map<String, Object> statusData) {
        return new RequestStatus(statusCode, statusData);
    }

    // Test...
    private RequestStatus(int statusCode, Map<String, Object> statusData) {
        this.code = statusCode;
        this.data = statusData;
    }

    // --------------------------------------------------

    /**
     * Request disposition status code.
     * <p>
     * A code may indicate a server-side error or a client-side (DM.apk) error.
     * <p>
     * See {@link ConfigManager} for definitions of all known codes.
     *
     * @return request disposition status code
     */
    public int getCode() {
        return code;
    }

    /**
     * Request disposition data.
     * <p>
     * Descriptive data associated with a disposition code.
     *
     * @return request disposition data
     */
    public Map<String, Object> getData() {
        return data;
    }

    // --------------------------------------------------

    @Override
    public String toString() {
        return String.format("<%s: code=%s, data=%s>", this.getClass().getSimpleName(), code, data);
    }
}
