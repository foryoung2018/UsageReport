package com.htc.lib0.customization;

import android.util.Log;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class HtcWrapCustomizationReader {

    private static String TAG = "HtcWrapCustomizationReader";
    private Object mReader;
    private static Method[] mReaderMethods;
    private static final int READ_INTEGER=0;
    private static final int READ_STRING=1;
    private static final int READ_NULLABLE_BOOLEAN=2;
    private static final int READ_BOOLEAN=3;
    private static final int READ_BYTE=4;
    private static final int READ_INT_ARRAY=5;
    private static final int READ_STRING_ARRAY=6;
    private static final int READ_TOTAL=7;

    public HtcWrapCustomizationReader(Object reader) {
        mReader = reader;
    }
    
    public HtcWrapCustomizationReader(int type) {
        mReader = null;
    }

    static {
	initGenericReader();
    }

    static private void initGenericReader() {
     mReaderMethods = new Method[READ_TOTAL];
     try {
      Class<?> rdClass = Class.forName("com.htc.customization.HtcCustomizationReader");
   	    	     	          	    
      // if class exist, find 7-method 
      if ( null != rdClass ){
        Method[] allMethods = rdClass.getDeclaredMethods();
        for (Method mth : allMethods) {
   	      String mthName = mth.getName();
   	      if (mthName.equals("readInteger") ){
   		    //Log.d(TAG,"[ACC][RR] Method:"+ mthName);
            mReaderMethods[READ_INTEGER] = mth;
	      }else if (mthName.equals("readString") ){
   		    //Log.d(TAG,"[ACC][RR] Method:"+ mthName);
            mReaderMethods[READ_STRING] = mth;	
	      }else if (mthName.equals("readNullableBoolean") ){
   		    //Log.d(TAG,"[ACC][RR] Method:"+ mthName);
            mReaderMethods[READ_NULLABLE_BOOLEAN] = mth;
	      }else if (mthName.equals("readBoolean") ){
   		    //Log.d(TAG,"[ACC][RR] Method:"+ mthName);
            mReaderMethods[READ_BOOLEAN] = mth;
	      }else if (mthName.equals("readByte") ){
   		    //Log.d(TAG,"[ACC][RR] Method:"+ mthName);
            mReaderMethods[READ_BYTE] = mth;	
	      }else if (mthName.equals("readIntArray") ){
   		    //Log.d(TAG,"[ACC][RR] Method:"+ mthName);
            mReaderMethods[READ_INT_ARRAY] = mth;	
	      }else if (mthName.equals("readStringArray") ){
   		    //Log.d(TAG,"[ACC][RR] Method:"+ mthName);
             mReaderMethods[READ_STRING_ARRAY] = mth;	
	      }else{
   	        continue;
   	      }
        } 
      }
    } catch (ClassNotFoundException x) {
	    Log.d(TAG,"[ACC][RR] HtcCustomizationReader class NotFoundException");
   	    //x.printStackTrace();
    } catch (IllegalArgumentException x) {
	    Log.d(TAG,"[ACC][RR] HtcCustomizationReader class IllegalArgumentException");
 	    x.printStackTrace();
    }

    // Handle the GEP case


    }

    /** 
     * Get one {@code int} primitive value related to the key.
     *
     * @param key the predefined formatted string used to retrieve the related values.
     * @param defaultValue used to be the return value if there is no value related to the key.
     * @return the value related to the key, if it exists. Otherwise, return the {@code defaultValue}.
     */    
    private Object readGeneric(int methodType, String key, Object defaultValue) {
	  Object objReturn=null; 
        try {
            if (mReaderMethods[methodType] != null) {
                mReaderMethods[methodType].setAccessible(true);
                objReturn =  mReaderMethods[methodType].invoke((Object)mReader,key,defaultValue);
            } else {
                Log.d(TAG,"[ACC][RR] "+ mReaderMethods[methodType] +" method not found!");
            }
        }catch (InvocationTargetException x) {
            Throwable cause = x.getCause();
            Log.d(TAG,"[ACC][RR]invocation of "+ mReaderMethods[methodType] +" failed:" +cause.getMessage());
        }catch (IllegalArgumentException e) {
            Log.d(TAG,"[ACC][RR]invocation of "+ mReaderMethods[methodType] +" IllegalArgumentException");
	    e.printStackTrace();
        }catch (IllegalAccessException e) {
            Log.d(TAG,"[ACC][RR]invocation of "+ mReaderMethods[methodType] +" IllegalAccessException");
	    e.printStackTrace();
        }
     
     return objReturn;
    }

    /** 
     * Get one {@code int} primitive value related to the key.
     *
     * @param key the predefined formatted string used to retrieve the related values.
     * @param defaultValue used to be the return value if there is no value related to the key.
     * @return the value related to the key, if it exists. Otherwise, return the {@code defaultValue}.
     */    
    public int readInteger(String key, int defaultValue) {
      //return (mReader != null) ? mReader.readInteger(key, defaultValue) : defaultValue;
      if (mReader != null) {
          Object objReturn = readGeneric(READ_INTEGER,key,defaultValue); 
	  if (null != objReturn )
	     return (int)(Integer)objReturn;
      }

     return defaultValue;
    }

    /** 
     * Get one {@link java.lang.String} value related to the key.
     *
     * @param key the predefined formatted string used to retrieve the related values.
     * @param defaultValue used to be the return value if there is no value related to the key.
     * @return the value related to the key, if it exists. Otherwise, return the {@code defaultValue}.
     */    
    public String readString(String key, String defaultValue) {
       //return (mReader != null) ? mReader.readString(key, defaultValue) : defaultValue;
       if (mReader != null) {
          Object objReturn = readGeneric(READ_STRING,key,defaultValue); 
	  if (null != objReturn )
	      return (String)objReturn;
       }

    	return defaultValue;
    }

     /** 
      * Get one {@code Boolean} value related to the key but it can use assign null in default value.     
      *
      * @param key the predefined formatted string used to retrieve the related values.
      * @param defaultValue used to be the return value if there is no value related to the key.
      * @return the value related to the key, if it exists. Otherwise, return the {@code defaultValue}.
      */
    public Boolean readNullableBoolean(String key, Boolean defaultValue) {
        //return (mReader != null) ? mReader.readNullableBoolean(key, defaultValue) : defaultValue;
        if (mReader != null) {
          Object objReturn = readGeneric(READ_NULLABLE_BOOLEAN,key,defaultValue); 
	  if (null != objReturn )
	      return (Boolean)objReturn;
       }


    	return defaultValue;
    }


    /**
     * Get one {@link java.lang.String} value related to the key.
     *
     * @param key the predefined formatted string used to retrieve the related values.
     * @param defaultValue used to be the return value if there is no value related to the key.
     * @return the value related to the key, if it exists. Otherwise, return the {@code defaultValue}.
     */    
    public boolean readBoolean(String key, boolean defaultValue) {
        //return (mReader != null) ? mReader.readBoolean(key, defaultValue) : defaultValue;
        if (mReader != null) {
          Object objReturn = readGeneric(READ_BOOLEAN,key,defaultValue); 
	  if (null != objReturn )
	      return (Boolean)objReturn;
       }

    	return defaultValue;
    }

    /**
     * Get one {@code byte} primitive value related to the key.
     *
     * @param key the predefined formatted string used to retrieve the related values.
     * @param defaultValue used to be the return value if there is no value related to the key.
     * @return the value related to the key, if it exists. Otherwise, return the {@code defaultValue}.
     */    
    public byte readByte(String key, byte defaultValue) {
        //return (mReader != null) ? mReader.readByte(key, defaultValue) : defaultValue;
        if (mReader != null) {
          Object objReturn = readGeneric(READ_BYTE,key,defaultValue); 
	      if (null != objReturn )
	      return (byte)(Byte)objReturn;
       }

        return defaultValue;
    }

    /**
     * Get one {@code int[]} primitive value related to the key.
     *
     * @param key the predefined formatted string used to retrieve the related values.
     * @param defaultValue used to be the return value if there is no value related to the key.
     * @return the value related to the key, if it exists. Otherwise, return the {@code defaultValue}.
     */    
    public int[] readIntArray(String key, int[] defaultValue) {
        //return (mReader != null) ? mReader.readIntArray(key, defaultValue) : defaultValue;
         if (mReader != null) {
          Object objReturn = readGeneric(READ_INT_ARRAY,key,defaultValue); 
	  if (null != objReturn )
	      return (int[])objReturn;
       }

        return defaultValue;
    }
    
    /**
     * Get one {@code String[]} primitive value related to the key.
     *
     * @param key the predefined formatted string used to retrieve the related values.
     * @param defaultValue used to be the return value if there is no value related to the key.
     * @return the value related to the key, if it exists. Otherwise, return the {@code defaultValue}.
     */    
    public String[] readStringArray(String key, String[] defaultValue) {
        //return (mReader != null) ? mReader.readStringArray(key, defaultValue) : defaultValue;
        if (mReader != null) {
          Object objReturn = readGeneric(READ_STRING_ARRAY,key,defaultValue); 
	  if (null != objReturn )
	      return (String[])objReturn;
       }
    	return defaultValue;
    }
}
