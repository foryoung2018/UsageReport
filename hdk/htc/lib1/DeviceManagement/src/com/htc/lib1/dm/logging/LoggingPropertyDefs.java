// Available white list:
//    { "net.rmnet0.",      AID_RADIO,    0 },
//    { "net.gprs.",        AID_RADIO,    0 },
//    { "net.ppp",          AID_RADIO,    0 },
//    { "net.qmi",          AID_RADIO,    0 },
//    { "net.lte",          AID_RADIO,    0 },
//    { "net.cdma",         AID_RADIO,    0 },
//    { "ril.",             AID_RADIO,    0 },
//    { "gsm.",             AID_RADIO,    0 },
//    { "persist.radio",    AID_RADIO,    0 },
//    { "net.dns",          AID_RADIO,    0 },
//    { "sys.usb.config",   AID_RADIO,    0 },
//    { "net.",             AID_SYSTEM,   0 },
//    { "dev.",             AID_SYSTEM,   0 },
//    { "runtime.",         AID_SYSTEM,   0 },
//    { "hw.",              AID_SYSTEM,   0 },
//    { "sys.",             AID_SYSTEM,   0 },
//    { "service.",         AID_SYSTEM,   0 },
//    { "wlan.",            AID_SYSTEM,   0 },
//    { "dhcp.",            AID_SYSTEM,   0 },
//    { "dhcp.",            AID_DHCP,     0 },
//    { "debug.",           AID_SYSTEM,   0 },
//    { "debug.",           AID_SHELL,    0 },
//    { "log.",             AID_SHELL,    0 },
//    { "service.adb.root", AID_SHELL,    0 },
//    { "service.adb.tcp.port", AID_SHELL,    0 },
//    { "persist.sys.",     AID_SYSTEM,   0 },
//    { "persist.service.", AID_SYSTEM,   0 },
//    { "persist.security.", AID_SYSTEM,   0 }
package com.htc.lib1.dm.logging;


/**
 * Constants of logging properties.
 */
class LoggingPropertyDefs {
    
    /**
     * Logger tag.
     */
    public static final String KEY_TAG = "log.cs.tag";
    
    /**
     * Logger tag for sensitive logs.
     */
    public static final String KEY_SENSITIVE_TAG = "log.cs.tag.sensitive";

    /** Boolean value to enable sensitive log messages. */
    public static final String KEY_ENABLE_SENSITIVE_LOG = "log.cs.sensitive";

    /** Boolean value to enable method name logs. */
    public static final String KEY_ENABLE_METHOD_NAME_LOG = "log.cs.method";

    /** Boolean value to enable file info logs. */
    public static final String KEY_ENABLE_FILE_INFO_LOG = "log.cs.fileinfo";

    /**
     * Boolean value to enable {@link ThrowableFormatter}.
     */
    public static final String KEY_ENABLE_LOG_THROWABLE_FORMATTER = "log.cs.throwable";

    /**
     * Boolean value to enable {@link BundleFormatter}.
     */
    public static final String KEY_ENABLE_LOG_BUNDLE_FORMATTER = "log.cs.bundle";
}
