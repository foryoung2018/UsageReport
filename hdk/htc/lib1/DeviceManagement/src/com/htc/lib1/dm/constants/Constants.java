package com.htc.lib1.dm.constants;

/**
 * Created by Joe_Wu on 8/21/14.
 */
public interface Constants {

    public static final String LIBRARY_PACKAGE_NAME = "com.htc.lib1.dm";

    public static final String LIBRARY_VERSION_NAME = "3.0.26";

    public static final String LIBRARY_BUILD_STATE_DEBUG = "debug";

    public static final String LIBRARY_BUILD_STATE_RELEASE = "release";

    public static final Long DEFAULT_TTL = 86400l;

    public static final Integer HTTP_GET_CONFIG_REQUEST_TIMEOUT = 5000;

    public static final Integer HTTP_PUT_PROFILE_REQUEST_TIMEOUT = 20000;

    public static final Long PUT_PROFILE_RETRY_PERIOD = 86400l;

    public static final Long SPICE_CACHE_RETRIEVE_TIMEOUT = 86400l;

    public static final Integer RETRY_GROWTH_FACTOR = 2;

    public static final Integer AUTHORIZATION_AUTHORIZED = 0;
    public static final Integer AUTHORIZATION_UNAUTHORIZED = 1;

    public static final Long RETRY_MAX_SLEEP_PERIOD = 3600l;

    public static final Long RETRY_AFTER_MAX_PERIOD = 604800l;

    public static final Long GET_CONFIG_RUNNING_TIMEOUT = 300000l;
    public static final Long PUT_PROFILE_RUNNING_TIMEOUT = 600000l;

    public static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss z";

    public static final String SHARED_PREF_NAME_DM_CACHE = "DM_CACHE";
    public static final String SHARED_PREF_DM_CACHE_TIME_SURFIX = "__UpdateTime";
}
