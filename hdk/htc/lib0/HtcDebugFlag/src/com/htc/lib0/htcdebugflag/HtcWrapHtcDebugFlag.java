
package com.htc.lib0.htcdebugflag;

// for reflection/wrapper class
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HtcWrapHtcDebugFlag {

    public HtcWrapHtcDebugFlag() {
    }

    private static String TAG = "HtcWrapHtcDebugFlag";
    private static Object mHtcDebugFlag = null;
    private static Method mGetHtcDebugFlagMethod = null;

    public final static boolean Htc_DEBUG_flag = getHtcDebugFlag();
    public final static boolean Htc_SECURITY_DEBUG_flag = true;

    static private void init() {
        try {
            if (mHtcDebugFlag != null) return;
            Class<?> mgrClass = Class.forName("com.htc.htcjavaflag.HtcDebugFlag");
            // if class exist, find getHtcDebugFlag method
            if (null != mgrClass) {
                mHtcDebugFlag = mgrClass.newInstance();
                Method[] allMethods = mgrClass.getDeclaredMethods();
                for (Method mth : allMethods) {
                    String mthName = mth.getName();
                    if (mthName.equals("getHtcDebugFlag")) {
                        //Log.d(TAG, "[HtcDebugFlag] Method:" + mthName);
                        mth.setAccessible(true);
                        mGetHtcDebugFlagMethod = mth;
                    } else {
                        // Log.d(TAG,"[HtcDebugFlag] Other Field:"+ mthName);
                        continue;
                    }
                }
            }
        } catch (ClassNotFoundException x) {
            Log.d(TAG, "HtcDebugFlag class Not Found!");
            // x.printStackTrace();
        } catch (IllegalAccessException x) {
            x.printStackTrace();
        } catch (IllegalArgumentException x) {
            x.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Get debug flag value
     *
     * @return true or false related to the device's setting
     *
     */
    static private boolean getHtcDebugFlag() {
        init();
        Object result = null;
        if (null != mHtcDebugFlag) {
            try {
                if (mGetHtcDebugFlagMethod != null) {
                    mGetHtcDebugFlagMethod.setAccessible(true);
                    result = mGetHtcDebugFlagMethod.invoke(mHtcDebugFlag);
                } else {
                    Log.d(TAG, mGetHtcDebugFlagMethod + " method not found!");
                }
            } catch (InvocationTargetException x) {
                Throwable cause = x.getCause();
                Log.d(TAG, "invocation of " + mGetHtcDebugFlagMethod + " failed:"
                        + cause.getMessage());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (null != result) {
                return (Boolean)result;
            }
        }
        // Handle the GEP case
        return false;
    }
}
