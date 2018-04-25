package com.htc.lib1.upm.ulog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Wrapper class of ReusableULogData
 * @author pitt_wu
 *
 */
final class WrapReusableULogData {

	//== begin static method / data ==
    private final static String  CLASS= "com.htc.utils.ulog.ReusableULogData";
    private static Class<?> sReusableULogDataClass;
    private static Method sObtain;
    private static Method sRecyle;
    private static Method sSetAppId;
    private static Method sSetCategory;
    private static Method sSetTimestamp;
    private static Method sAddData;
    private volatile static boolean sIsInit;
    
    private static void init() throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
        
        if (!sIsInit) {
            synchronized(WrapReusableULogData.class) {
                if (!sIsInit) {
                        if (sReusableULogDataClass == null)
                            sReusableULogDataClass = Class.forName(CLASS);
                        
                        if (sObtain == null && sReusableULogDataClass != null)
                            sObtain = sReusableULogDataClass.getMethod("obtain");
                        
                        if (sRecyle == null && sReusableULogDataClass != null)
                            sRecyle = sReusableULogDataClass.getMethod("recycle");
                        
                        if (sSetAppId == null && sReusableULogDataClass != null)
                            sSetAppId = sReusableULogDataClass.getMethod("setAppId", String.class);
                        
                        if (sSetCategory == null && sReusableULogDataClass != null)
                            sSetCategory = sReusableULogDataClass.getMethod("setCategory", String.class);
                        
                        if (sSetTimestamp == null && sReusableULogDataClass != null)
                            sSetTimestamp = sReusableULogDataClass.getMethod("setTimestamp", long.class);
                        
                        if (sAddData == null && sReusableULogDataClass != null)
                            sAddData = sReusableULogDataClass.getMethod("addData", String.class, String.class);
                        
                        sIsInit = true;
                }
            }
        }
        
    }
    
	/**
	 * Obtain a instance of WrapReusableULogData
	 * @return Instance of WrapReusableULogData
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
    public static WrapReusableULogData obtain() throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{        
        init();
        Object obj = sObtain.invoke(null);
        return new WrapReusableULogData(obj);
    }
    //== end static method / data ==
    
    //== begin member method / data ==
    private final Object mObject;
    private WrapReusableULogData(Object object) {
        mObject = object;
    }
    /**
     * Recycle resource of data of user log
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public void recycle() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (mObject != null) {
            sRecyle.invoke(mObject);
        }
    }
    /**
     * Set app id to data of user log
     * @param appId the string for recognizing app 
     * @return the instance of WrapReusableULogData that had been set AppId
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
	public WrapReusableULogData setAppId(String appId) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	    if (mObject != null) {
	        sSetAppId.invoke(mObject, appId);
	    }
		return this;
	}
	/**
	 * Set category to data of user log
	 * @param category the string for recognizing category.
	 * @return the instance of WrapReusableULogData that had been set category
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public WrapReusableULogData setCategory(String category) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	    if (mObject != null) {
            sSetCategory.invoke(mObject, category);
        }
		return this;
	}
	/**
	 * Set timestamp to data of user log
	 * @param timestamp the timestamp
	 * @return the instance of WrapReusableULogData that had been set timestamp
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public WrapReusableULogData setTimestamp(long timestamp) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	    if (mObject != null) {
            sSetTimestamp.invoke(mObject, timestamp);
        }
		return this;
	}
	/**
	 * Compose key/value pair into data field of JSON-formatted string
	 * @param key the key
	 * @param value the value
	 * @return the instance of WrapReusableULogData that had been added key/value data
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public WrapReusableULogData addData(String key, String value) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	    if (mObject != null) {
            sAddData.invoke(mObject, key, value);
        }
		return this;
	}
	
	/*package*/ Object getDelegate() {
	    return mObject;
	}
    //== end member method / data ==
}
