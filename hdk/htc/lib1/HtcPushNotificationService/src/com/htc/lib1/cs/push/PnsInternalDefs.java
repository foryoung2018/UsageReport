
package com.htc.lib1.cs.push;

import com.htc.lib1.cs.push.service.RegistrationService;
import com.htc.lib1.cs.push.service.UnregistrationService;
import com.htc.lib1.cs.push.service.UpdateRegistrationService;

/**
 * Constants used by PNS client / library internally.
 */
/**
 * @author Ted_Hsu
 *
 */
public class PnsInternalDefs {

    /** The http timeout in seconds used in all PNS client REST requests. */
    public static final long HTTP_TIMEOUT_SECONDS = 10;

    /**
     * GCM sender ID.
     */
    public static final String GCM_SENDER_ID = "1065916003157";

    /** Fallback URI to use if DM doesn't work. */
    public static final String REGISTRATION_URI = "https://pns.htcsense.com";

    /** By default retry time is in hour. */
    public static final int RETRY_TIME_RATIO = 60;

    /** By default register period distributed in 10 minutes. */
    public static final int DISTRIBUTED_REGISTER_IN_MINUTES = 10;

    /** After package updated, register period distributed in 2 days. */
    public static final int DISTRIBUTED_REGISTER_PKGUPDATE_IN_MINUTES = 2 * 24 * 60;

    /** By default update period distributed in 2 days. */
    public static final int DISTRIBUTED_UPDATE_IN_MINUTES = 2 * 24 * 60; 

    /** Max number of event / message records to keep. */
    public static final int MAX_PNS_RECORDS = 100;

    /** Permission to allow send messages from push client. */
    public static final String PERMISSION_SEND_MESSAGE = "com.htc.cs.pushclient.permission.SEND_MESSAGE";

    /** Permission to allow receiving messages from push client. */
    public static final String PERMISSION_RECEIVE_MESSAGE = "com.htc.cs.pushclient.permission.RECEIVE_MESSAGE";

    /** Name of the system property for GCM base URI. */
    public static final String KEY_SYSTEM_PROP_GCM_BASE_URI = "debug.pns.gcm.baseuri";

    /** Name of the system property for Baidu base URI. */
    public static final String KEY_SYSTEM_PROP_BAIDU_BASE_URI = "debug.pns.baidu.baseuri";

    /** Name of the system property for register period in milliseconds. */
    public static final String KEY_SYSTEM_PROP_DISTRIBUTED_REGISTER_PERIOD = "debug.pns.distri.reg.period";

    /** Name of the system property for update period in milliseconds. */
    public static final String KEY_SYSTEM_PROP_DISTRIBUTED_UPDATE_PERIOD = "debug.pns.distri.update.period";

    /** Name of the system property for change ratio of retry time. */
    public static final String KEY_SYSTEM_PROP_RETRY_TIME_RATIO = "debug.pns.retry.time.ratio";

    /** Name of the system property to specify what push provider to use. */
    public static final String KEY_SYSTEM_PROP_PUSH_PROVIDER = "debug.pns.push.provider";
    
    /** Name of the system property to keep SIM MCC/MNC info defined by Google since Android 4.0 */
    public static final String KEY_SYSTEM_PROP_SIM_MCC_MNC = "gsm.sim.operator.numeric";

//    /** Name of DM config for GCM base URI. */
//    public static final String KEY_DM_GCM_BASE_URI = "gcmBaseUri";
//
//    /** Name of DM config for Baidu base URI. */
//    public static final String KEY_DM_BAIDU_BASE_URI = "baiduBaseUri";
//
//    /** Name of DM config for JPush base URI. */
//    public static final String KEY_DM_JPUSH_BASE_URI = "jpushBaseUri";
//
//    /** Name of DM config for update period in milliseconds. */
//    public static final String KEY_DM_UPDATE_PERIOD = "updatePeriod";
//
//    /** Name of DM config to specify what push provider to use. */
//    public static final String KEY_DM_PUSH_PROVIDER = "pushProvider";

    /** DM timeout in milliseconds. */
    public static final long DM_TIMEOUT_MILLIS = 10000;

//    /**
//     * Permission to get configs through dm-client.
//     */
//    public static final String PERMISSION_DM_GET_CONFIG = "com.htc.cs.dm.permission.CONFIG";

    /**
     * The prefix of all metadata keys in the push notification message sent
     * from PNS server.
     */
    public static final String PREFIX_MESSAGE_META_KEYS = "__";

    /** Key of message id in the push notification message sent from PNS server. */
    public static final String KEY_MESSAGE_ID = PREFIX_MESSAGE_META_KEYS + "msg_id";

    /** Key of app list in the push notification message sent from PNS server. */
    public static final String KEY_MESSAGE_APPS = PREFIX_MESSAGE_META_KEYS + "apps";

    /** Key of cipher in the push notification message sent from PNS server. */
    public static final String KEY_MESSAGE_CIPHER = PREFIX_MESSAGE_META_KEYS + "cipher";

    /** Secret code for diagnostic. */
    public static final String SECRET_CODE_PNS = "767";
    
    /**
     * Action to register baidu push
     */
    public static final String ACTION_PNS_REGISTER_BAIDU = "com.htc.cs.pns.action.baidu.register";
    
    /**
     * The name of the attribute of Baidu app id
     */
    public static final String KEY_BAIDU_APP_ID = "BAIDU_APPID";
    
    /**
     * The name of the attribute of Baidu user id
     */
    public static final String KEY_BAIDU_USER_ID = "BAIDU_USERID";
    
    /**
     * The name of the attribute of Baidu channel id
     */
    public static final String KEY_BAIDU_CHANNEL_ID = "BAIDU_CHANNELID";
    
    /**
     * The name of the attribute of Baidu request id
     */
    public static final String KEY_BAIDU_REQUEST_ID = "BAIDU_REQUESTID";

    /**
     * Key of the a string describing why the registration / update /
     * unregistration service is triggered when starting
     * {@link RegistrationService}, {@link UpdateRegistrationService} or
     * {@link UnregistrationService}.
     */
    public static final String KEY_CAUSE = "com.htc.cs.pns.Cause";

    /** The name of the attribute of Baidu api key in application metadata. */
    public static final String KEY_BAIDU_API_KEY = "BAIDU_APIKEY";

    /** Baidu push service class name. */
    public static final String CLASS_BAIDU_SERVICE = "com.baidu.android.pushservice.PushService";

    /** Baidu push receiver class name. */
    public static final String CLASS_BAIDU_PUSH_RECEIVER = "com.baidu.android.pushservice.PushServiceReceiver";

    /** Baidu push registration receiver class name. */
    public static final String CLASS_BAIDU_REG_RECEIVER = "com.baidu.android.pushservice.RegistrationReceiver";
    
    /** Baidu push message receiver class name. */
    public static final String CLASS_BAIDU_PUSH_MESSAGE_RECEIVER = "com.htc.lib1.cs.push.receiver.BaiduMessageReceiver";

    /** Baidu push command service class name. */
    public static final String CLASS_BAIDU_PUSH_COMMAND_SERVICE = "com.baidu.android.pushservice.CommandService";

    public static final String CLASS_BAIDU_PUSH_INFO_PROVIDER = "com.baidu.android.pushservice.PushInfoProvider";
    
    /** Baidu subprocess name. */
    public static final String PROCESS_BAIDU = ":baidu";
}
