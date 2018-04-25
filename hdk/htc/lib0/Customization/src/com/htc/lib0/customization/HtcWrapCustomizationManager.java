package com.htc.lib0.customization;


// for reflection/wrapper class
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class HtcWrapCustomizationManager {

    public HtcWrapCustomizationManager() {
    }

    private static String TAG = "HtcWrapCustomizationManager";
    private static Object mCustomizationManager = null;
    private static Method mGetCustomizationReaderMethod = null;
    private static Method mGetCustomizationReaderExMethod = null;
    private static Method mGetCustomizationReaderEx2Method = null;
    private static Method mReadCIDMethod = null;

    //static public final int READER_TYPE_XML = HtcCustomizationManager.READER_TYPE_XML;
    //static public final int READER_TYPE_BINARY = HtcCustomizationManager.READER_TYPE_BINARY;
    static public final int READER_TYPE_XML = 0x0001;
    static public final int READER_TYPE_BINARY = 0x0002;

    static {
      initGenericManager();
    }

    static private void initGenericManager(){
     try {
       Class<?> mgrClass = Class.forName("com.htc.customization.HtcCustomizationManager");
       // if class exist, find getCustomizationReader() method
       if ( null != mgrClass ){
         Method[] allMethods = mgrClass.getDeclaredMethods();
         for (Method mth : allMethods) {
   	      String mthName = mth.getName();
              Class<?>[] type = mth.getParameterTypes();

   	      if (mthName.equals("getCustomizationReader")){
   		Log.d(TAG,"[ACC][RR] Method:"+ mthName);
                if(type.length == 3) {
                    if(type[0].getName().equals("java.lang.String") &&
                       type[1].getName().equals("int") &&
                       type[2].getName().equals("boolean"))
                       mGetCustomizationReaderMethod = mth;
                }
                else if(type.length == 4) {
                    if(type[0].getName().equals("java.lang.String") &&
                       type[1].getName().equals("int") &&
                       type[2].getName().equals("java.lang.String") &&
                       type[3].getName().equals("java.lang.String"))
                        mGetCustomizationReaderExMethod = mth;
                }
                else if(type.length == 5) {
                    if(type[0].getName().equals("java.lang.String") &&
                       type[1].getName().equals("int") &&
                       type[2].getName().equals("java.lang.String") &&
                       type[3].getName().equals("java.lang.String") &&
                       type[4].getName().equals("java.lang.String"))
                        mGetCustomizationReaderEx2Method = mth;
                }
                else {
                    mGetCustomizationReaderExMethod = null;
                    mGetCustomizationReaderEx2Method = null;
                }
   	      }else if (mthName.equals("readCID") ){	  
   		//Log.d(TAG,"[ACC][RR] Method:"+ mthName);
                mReadCIDMethod = mth;	
   	      }else if (mthName.equals("getInstance") ){
   		//Log.d(TAG,"[ACC][RR] Method:"+ mthName);
                mth.setAccessible(true);
                mCustomizationManager = mth.invoke(null);
	      }else{
   		//Log.d(TAG,"[ACC][RR] Other Method:"+ mthName);
   	        continue;
   	      }
          } 
       }
      } catch (ClassNotFoundException x) {
        Log.d(TAG,"[ACC][RR] HtcCustomizationManager class NotFoundException");
   	    //x.printStackTrace();
      } catch (IllegalAccessException x) {
   	    x.printStackTrace();
      } catch (IllegalArgumentException x) {
 	    x.printStackTrace();
      } catch (InvocationTargetException x) {
	    x.printStackTrace();
      } 
    
   }


    /**
     * read CID from system property ro.cid. 
     * @return The CID string
     */
    public String readCID() {
      //return mCustomizationManager.readCID();
      if (null != mCustomizationManager){
         Object objCID = null;
         try {
            if (mReadCIDMethod != null) {
               mReadCIDMethod.setAccessible(true);
               objCID = mReadCIDMethod.invoke((Object)mCustomizationManager);
            } else {
               Log.d(TAG,"[ACC][RR] "+ mReadCIDMethod +" method not found!");
            }
          } catch (InvocationTargetException x) {
            Throwable cause = x.getCause();
            Log.d(TAG,"[ACC][RR]invocation of "+ mReadCIDMethod +" failed:" +cause.getMessage());
          } catch (IllegalArgumentException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
          } catch (IllegalAccessException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
          }

          if (null != objCID){
        	 return (String)objCID;
	  }   
	}

        return "null";
    }

    /**
     * Get one {@code HtcCustomizationReader} primitive value related to the
     * name.
     * 
     * @param name
     *            The predefined formatted string used to retrieve the related
     *            HtcCustomizationReader.
     * @param type
     *            The reader type, only {@code READER_TYPE_XML} is supported.
     * @param needSIMReady
     *            check SIM ready or not. some applications need to use sim info
     *            to load configurations.
     *            Reserved for future, not enabled in current version.
     * @return the HtcCustomizationReader related to the name, if it exists.
     *         Otherwise, return a default {@code AccXmlReader}.
     *         It may be null if needSIMReady is true.
     */
    public HtcWrapCustomizationReader getCustomizationReader(String name, int type, boolean needSIMReady) {
        //return new HtcWrapCustomizationReader(mCustomizationManager.getCustomizationReader(name, type, needSIMReady));
	Object objCustReader = null;
	if (null != mCustomizationManager){
          try {
            if(mGetCustomizationReaderMethod != null) {
                mGetCustomizationReaderMethod.setAccessible(true);
                objCustReader = mGetCustomizationReaderMethod.invoke((Object)mCustomizationManager,name,type,needSIMReady);
            }
            else {
                Log.d(TAG, "[ACC][RR] " + mGetCustomizationReaderMethod + " method not found!");
            }
          } catch (InvocationTargetException x) {
            Throwable cause = x.getCause();
            Log.d(TAG,"[ACC][RR]invocation of "+ mGetCustomizationReaderMethod +" failed:" +cause.getMessage());
          } catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
          } catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
          }

          if (null != objCustReader){
	     return new HtcWrapCustomizationReader(objCustReader);
	  }   
	}
	
	// Handle the GEP case
        return new HtcWrapCustomizationReader(0);
    }

     /**
     * Get one {@code HtcCustomizationReader} primitive value related to the
     * name.
     * :
     * @param name
     *            The predefined formatted string used to retrieve the related
     *            HtcCustomizationReader.
     * @param type
     *            The reader type, only {@code READER_TYPE_XML} is supported.
     * @param spn
     *            The Service Provider Name.
     * @param gid1
     *            The Group Identifier Level 1
     * @return the HtcCustomizationReader related to the name, if it exists.
     *         Otherwise, return a default {@code AccXmlReader}.
     *         It may be null if needSIMReady is true.
     */
    public HtcWrapCustomizationReader getCustomizationReader(String name, int type, String spn, String gid1) {
        Object objCustReader = null;
        if (null != mCustomizationManager) {
            try {
                if(mGetCustomizationReaderExMethod != null) {
                    mGetCustomizationReaderExMethod.setAccessible(true);
                    objCustReader = mGetCustomizationReaderExMethod.invoke((Object)mCustomizationManager,name,type,spn,gid1);
                }
                else if(mGetCustomizationReaderMethod != null) {
                    mGetCustomizationReaderMethod.setAccessible(true);
                    objCustReader = mGetCustomizationReaderMethod.invoke((Object)mCustomizationManager, name, type, false);
                }
                else {
                    Log.d(TAG, "[ACC][RR] " + mGetCustomizationReaderExMethod + " method not found!");
                }
            } catch (InvocationTargetException x) {
                Throwable cause = x.getCause();
                Log.d(TAG,"[ACC][RR]invocation of "+ mGetCustomizationReaderExMethod +" failed:" + cause.getMessage());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (null != objCustReader) {
                return new HtcWrapCustomizationReader(objCustReader);
            }
        }

        // Handle the GEP case
        return new HtcWrapCustomizationReader(0);
    }

    /**
     * Get one {@code HtcCustomizationReader} primitive value related to the
     * name.
     * :
     * @param name
     *            The predefined formatted string used to retrieve the related
     *            HtcCustomizationReader.
     * @param type
     *            The reader type, only {@code READER_TYPE_XML} is supported.
     * @param mccmnc
     *            MCCMNC
     * @param spn
     *            The Service Provider Name.
     * @param gid1
     *            The Group Identifier Level 1
     * @return the HtcCustomizationReader related to the name, if it exists.
     *         Otherwise, return a default {@code AccXmlReader}.
     *         It may be null if needSIMReady is true.
     */
    public HtcWrapCustomizationReader getCustomizationReader(String name, int type, String mccmnc, String spn, String gid1) {
        Object objCustReader = null;
        if (null != mCustomizationManager) {
            try {
                if(mGetCustomizationReaderEx2Method != null) {
                    mGetCustomizationReaderEx2Method.setAccessible(true);
                    objCustReader = mGetCustomizationReaderEx2Method.invoke((Object)mCustomizationManager,name,type,mccmnc,spn,gid1);
                }
                else if(mGetCustomizationReaderMethod != null) {
                    mGetCustomizationReaderMethod.setAccessible(true);
                    objCustReader = mGetCustomizationReaderMethod.invoke((Object)mCustomizationManager, name, type, false);
                }
                else {
                    Log.d(TAG, "[ACC][RR] " + mGetCustomizationReaderEx2Method + " method not found!");
                }
            } catch (InvocationTargetException x) {
                Throwable cause = x.getCause();
                Log.d(TAG,"[ACC][RR]invocation of "+ mGetCustomizationReaderEx2Method +" failed:" + cause.getMessage());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            if (null != objCustReader) {
                return new HtcWrapCustomizationReader(objCustReader);
            }
        }
        
        // Handle the GEP case
        return new HtcWrapCustomizationReader(0);
    }
    
    /** 
     * Get {@code HtcCustomizationManager} status. 
     *
     * @return the status 
     */

    //public int getStatus() {
        //return mCustomizationManager.getStatus();
    //	return 0;
    //}     
}

