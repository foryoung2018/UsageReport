
package com.htc.lib1.cs.push.httputils;

import android.util.SparseArray;

import com.htc.lib1.cs.logging.HtcLogger;
import com.htc.lib1.cs.push.PushLoggerFactory;

/**
 * The standard error codes returned in HTTP error status response payload.
 */
public enum PnsServiceErrorCode {

    /**
     * The error code representing an invalid/unknown error code.
     */
    UnknownErrorCode(-1),

    /**
     * 400: "Auth-key is missing, expired or malformed."
     */
    InvalidAuthKey(200),

    /**
     * 400: "Client is not authorized to access this service."
     */
    UnauthorizedClient(201),

    /**
     * 400: "Request parameter is missing or malformed."
     */
    InvalidRequest(1140000),

    /**
     * 401: "Client is not authorized, or insufficient permission to complete this action."
     */
    Unauthorized(1140100),

    /**
     * 404: "targeted resource could not be found."
     */
    TargetNotFound(1140400),

    /**
     * 500: "Unexpected service error."
     */
    ServerError(1150000),

    /**
     * 503: "service is unavailable, it could be caused by over loading or system maintenance."
     */
    ServiceUnavailable(1150300);

    private static final HtcLogger sLogger = new PushLoggerFactory(PnsServiceErrorCode.class).create();
    private static final SparseArray<PnsServiceErrorCode> sErrorCodeMap = new SparseArray<PnsServiceErrorCode>();
    static {
        for (PnsServiceErrorCode ec : PnsServiceErrorCode.values())
            sErrorCodeMap.append(ec.mValue, ec);
    }

    private int mValue;

    private PnsServiceErrorCode(int value) {
        this.mValue = value;
    }

    /**
     * Set the value manually. This only make sense when the code was not able
     * to map to any known ServiceErrorCode, but you still want to keep the
     * original value for debug.
     * 
     * @param value
     */
    public void setValue(int value) {
        this.mValue = value;
    }

    /**
     * Get the integer value of the error code.
     * 
     * @return Integer value of the error code.
     */
    public int getValue() {
        return this.mValue;
    }

    /**
     * Get the ServiceErrorCode from integer.
     * 
     * @param code
     * @return Corresponding CSErrorCode or UnknownErrorCode if the code maps to
     *         nothing.
     */
    public static PnsServiceErrorCode valueOf(int code) {
        PnsServiceErrorCode err = sErrorCodeMap.get(code);
        if (err == null) {
            sLogger.warning("Unknown service error code: ", code);
            err = PnsServiceErrorCode.UnknownErrorCode;
            err.setValue(code);
        }
        return err;
    }
}
