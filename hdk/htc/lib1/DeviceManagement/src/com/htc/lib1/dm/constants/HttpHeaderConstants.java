package com.htc.lib1.dm.constants;

/**
 * Created by Joe_Wu on 8/21/14.
 */
public interface HttpHeaderConstants {
    /**
     * PLMN of the mobile subscriber's operator.
     * <p>
     * That is, the PLMN of the SIM card in GSM networks.
     */
    public static final String OPERATOR_PLMN = "X-HTC-Operator-PLMN";

    /**
     * PLMN of the currently attached mobile network.
     */
    public static final String NETWORK_PLMN = "X-HTC-Network-PLMN";

    /**
     * Device serial number.
     */
    public static final String DEVICE_SN = "X-HTC-Device-SN";

    /**
     * Device telephony identifier (IMEI/MEID/ESN).
     */
    public static final String DEVICE_TEL_ID = "X-HTC-Tel-ID";

    /**
     * Device manufacturer.
     */
    public static final String DEVICE_MFR = "X-HTC-Device-Mfr";

    /**
     * A CS platform identifier.
     * <p>
     * An identifier that can be used as a fall-back for identifying the device
     * in cases where device serial number is not available.
     * <p>
     * Not guaranteed to survive a factory reset.
     */
    public static final String PLATFORM_DEVICE_ID = "X-HTC-PD-ID";

    /**
     * Application version information to report on calls from feature APKs.
     * <p>
     * This is information about the application making the request.
     * Information about the REST agent that makes the underlying HTTP
     * request is specified in the UserAgent header.
     */
    public static final String APP_VERSION = "X-HTC-APP-Version";

    /**
     * An identifier injected into each request that can be used
     * to trace requests from a device serial number through the
     * network and backend infrastructure.
     */
    public static final String TRACER_ID = "X-HTC-Tracer-ID";

    /**
     * Optional security session token.
     * <p>
     * Only on authenticated requests.
     */
    public static final String LEGACY_AUTH_KEY = "AuthKey";
    public static final String AUTH_KEY = "X-HTC-AuthKey";

    /**
     * The user's preferred locale (language).
     */
    public static final String DEFAULT_LOCALE = "X-HTC-Default-Locale";


    public static final String DM_LIB_VERSION = "X-HTC-DM-Lib-Version";


    /**
     * Device Profile Info ( from DM 3.0 )
     */
    public static final String ANDROID_API_LEVEL = "X-HTC-Android-API-Level";

    public static final String ANDROID_VERSION = "X-HTC-Android-Version";

    public static final String BUILD_DESCRIPTION = "X-HTC-Build-Description";

    public static final String CUSTOMER_ID = "X-HTC-Customer-ID";

    public static final String MARKETING_NAME = "X-HTC-Marketing-Name";

    public static final String MODEL_ID = "X-HTC-Model-ID";

    public static final String PRODUCT_NAME = "X-HTC-Product-Name";

    public static final String PROJECT_NAME = "X-HTC-Project-Name";

    public static final String ROM_VERSION = "X-HTC-ROM-Version";

    public static final String SENSE_VERSION = "X-HTC-Sense-Version";

    public static final String REGION_ID = "X-HTC-Region-ID";

    public static final String SKU_ID = "X-HTC-SKU-ID";
}
