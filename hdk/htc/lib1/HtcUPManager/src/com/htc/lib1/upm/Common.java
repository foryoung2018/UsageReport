package com.htc.lib1.upm;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

public final class Common {
    public final static String TAG = "HtcUPManager";
    public final static String DEBUG_TAG = "AppUPDebug";
    public final static String APP_ID = "app_id";
    public final static String TIMESTAMP = "timestamp";
    public final static String EVENT_ACTION = "action";
    public final static String EVENT_CATEGORY = "category";
    public final static String EVENT_LABEL = "label";
    public final static String EVENT_VALUE = "value";
    public final static String ATTRIBUTE_LABLE = "attribute_label";
    public final static String ATTRIBUTE_EXTRA = "attribute_extra";
    public final static String IS_SECURE = "is_secure";
    public final static String VERSION_CODE = "version_code";
    public final static String PACKAGE_NAME = "package_name";
    public final static String IS_DEBUGGING = "is_debugging";
    
    public final static String UP_PREFERENCE_NAME = "up";
    
    public final static String PACKAGE_CONTROL = "package_control";
    public final static String PACKAGE_UP_ENABLE = "package_enable";
    //Move from MyBreeze----------------------------------------------------
    public final static boolean _DEBUG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    public final static String WAKELOCK_TAG = "Wakelock4Data";
    public final static String WAKELOCK_POMELO = "logPomeloSender_133";
    
    public static final String RELATIVE_LOG_FOLDER_PATH = "logs";
    public static final String ZIP_FILE_ENTITY = "file_entity";
    public static final String STR_UNKNOWN = "unknown";
    public static final String STR_DISABLED = "NA";
    public static final String STR_KEY_ENABLE = "enable";
    public static final String STR_VALUE_ENABLED = "1";
    
    public static final String STR_HEADER_APPID = "tellhtc.header";
    public static final String STR_CATEGORY_REGION = "region";
    public static final String STR_CATEGORY_CITY = "city";
    public static final String STR_CATEGORY_TIMEZONE = "timezone";
    public static final String STR_CATEGORY_MODEL_ID = "model_id";
    public static final String STR_CATEGORY_DEVICE_ID = "device_id";
    public static final String STR_CATEGORY_DEVICE_SN = "device_sn";
    public static final String STR_CATEGORY_CID = "cid";
    public static final String STR_CATEGORY_ROM_VERSION = "rom_version";
    public static final String STR_CATEGORY_SENSE_VERSION = "sense_version";
    public static final String STR_CATEGORY_PRIVACY_VERSION = "privacy_statement_version";

    public static final String SRT_UP_TAG = "UP";
    
    public final static long KILOBYTE_TO_BYTES = 1024L;
    public final static long MEGABYTE_TO_KILOBYTES = 1024L;
    public final static long MEGABYTE_TO_BYTES = MEGABYTE_TO_KILOBYTES * KILOBYTE_TO_BYTES;
    
    public final static long SECOND_TO_MILLISECONDS = 1000L; 
    public final static long MINUTE_TO_SECONDS = 60L; 
    public final static long HOUR_TO_MINUTES = 60L; 
    public final static long DAY_TO_HOURS = 24L; 
    
    public final static long MINUTE_TO_MILLISECONDS = MINUTE_TO_SECONDS * SECOND_TO_MILLISECONDS; 
    public final static long HOUR_TO_MILLISECONDS = HOUR_TO_MINUTES * MINUTE_TO_MILLISECONDS; 
    public final static long DAY_TO_MILLISECONDS = DAY_TO_HOURS * HOUR_TO_MILLISECONDS; 
    
    public final static String KEY_BUDGET_PREFIX_MOBILE = "mobile_";
    public final static String KEY_BUDGET_PREFIX_OTHER = "other_";
    public final static String KEY_BUDGET_PREFIX_ALL = "all_";
    public final static String KEY_BUDGET_SUFFIX_UL = "UL";
    public final static String KEY_BUDGET_SUFFIX_DL = "DL";
    public final static String KEY_BUDGET_SUFFIX_TOTAL = "total";
    public final static String KEY_BUDGET_SUFFIX_CALC_UNIT = "calc_unit";
    
    // ----
    public final static String HSP_PACKAGE_NAME = "com.htc.sense.hsp";
    public final static String APP_PACKAGE_NAME_HTCBIDHANDLER = "com.htc.bidhandler";
}
