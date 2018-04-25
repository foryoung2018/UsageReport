package com.htc.lib1.upm.ulog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper class of ULog
 * @author pittwu
 *
 */
final class WrapULog {
	
    private final static String  ULOG_CLASS= "com.htc.utils.ulog.ULog";
    private final static String  ULOGDATA_CLASS = "com.htc.utils.ulog.ULogData";
    private static Class<?> sULogClass;
    private static Class<?> sULogDataClass;
    private static Method sLog;
    private volatile static boolean sIsInit;
    
    private static void init() throws ClassNotFoundException, NoSuchMethodException {
        if (!sIsInit) {
            synchronized(WrapULog.class) {
                if (!sIsInit) {
                    if (sULogClass == null)
                        sULogClass = Class.forName(ULOG_CLASS);
                    if (sULogDataClass == null)
                        sULogDataClass = Class.forName(ULOGDATA_CLASS);
                    if (sLog == null && sULogDataClass!= null && sULogClass!=null)
                        sLog = sULogClass.getMethod("log", sULogDataClass);
                    
                    sIsInit = true;
                }
            }
        }
    }
    
	private WrapULog() {
	}

    /** 
     * <p>This function will put a data point which equals to a instance of {@link com.htc.lib3.htcreport.ulog.WrapReusableULogData WrapReusableULogData} 
     * into data point center. Data points in the center will be uploaded to server periodically.</p>
     * 
     * <ul>
     * <li>Caller must require the permission "com.htc.permission.UBLS_WRITE_LOG", or 
     * the SecurityException will be thrown. This permission is for built-in apps only.
     * </li>
     * <li>The data field which is added by {@link com.htc.lib3.htcreport.ulog.WrapReusableULogData#addData() WrapReusableULogData.addData()} 
     * will be composed by {@link org.json.JSONObject#toString() JSONObject.toString()} before uploading to server.
     * The followings is an example uploaded to server.
     * 
     * <p>App ID : com.htc.sample.cs<br>
     *    Category : device_info<br>
     *    Timestamp : 123456789 (Note, timestamp can be generated automatically)<br>
     *    Data : {"framework":"gingerbread","model_id":123456} (Note, key-value pairs composed by JSONObject</p>
     * </li>
     * </ul>
     * @param wrapReusableULogData see {@link com.htc.lib3.htcreport.ulog.WrapReusableULogData WrapReusableULogData} for the definition
     * @throws ClassNotFoundException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public static void log(WrapReusableULogData wrapReusableULogData) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(wrapReusableULogData != null) {
            init();
            sLog.invoke(null, wrapReusableULogData.getDelegate());
        }
            
    }
}
