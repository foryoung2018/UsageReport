package com.htc.lib1.exo.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * API for sending log output if loggable. </br>
 * Generally, use the LOG.V() LOG.D() LOG.I() LOG.W() and LOG.E() methods.
 * Furthurmore, could dump call stack from getLineInfo() getOneLineInfo() and getTwoLineInfo() methods.
 */
public class LOG {
    private static Object mHtcDebugFlag = null;
    private static Method mGetHtcDebugFlagMethod = null;

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
                        mth.setAccessible(true);
                        mGetHtcDebugFlagMethod = mth;
                    } else {
                        continue;
                    }
                }
            }
        } catch (ClassNotFoundException x) {
        } catch (IllegalAccessException x) {
        } catch (IllegalArgumentException x) {
        } catch (InstantiationException e) {
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
                mGetHtcDebugFlagMethod.setAccessible(true);
                result = mGetHtcDebugFlagMethod.invoke(mHtcDebugFlag);
            } catch (InvocationTargetException x) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }

            if (null != result) {
                return (Boolean)result;
            }
        }
        // Handle the GEP case
        return false;
    }
    /**
     * Print an information log message. </br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach.
     */

    private static boolean sIsDebug = getHtcDebugFlag();

    public static void initDebug(Context context)
    {
        if (sIsDebug == true) return;

        try
        {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo localApplicationInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            if (0 != (localApplicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE))
            {
                sIsDebug = true;
            }
        }
        catch (Exception ex)
        {
            sIsDebug = false;
        }
    }

    /**
     * Print a debug log message.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach.
     */
    public static void I (String TAG, String Info)
    {
        if(isDebug() == true && TAG != null && Info != null)
        {
            Log.i(TAG, Info);
        }
    }
    /**
     * Print a debug log message.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach.
     */
    public static void D (String TAG, String Info)
    {
        if(isDebug() == true && TAG != null && Info != null)
        {
            Log.d(TAG, Info);
        }
    }
    /**
     * Print an error log message.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach.
     */
    public static void E (String TAG, String Info)
    {
        if( TAG != null && Info != null)
        {
            Log.e(TAG, Info);
        }
    }
    /**
     * Print a verbose log message.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach.
     */
    public static void V (String TAG, String Info)
    {
        if(isDebug() == true && TAG != null && Info != null)
        {
            Log.v(TAG, Info);
        }
    }
    /**
     * Print a warn log message and log the exception.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach.
     */
    public static void W (String TAG, String Info)
    {
        if(TAG != null && Info != null)
        {
            Log.w(TAG, Info);
        }
    }
    /**
     * Print an information log message.</br>
     * @param TAG Used to identify the class name.
     * @param e An exception to log. And the toppest two stack trace elements of this throwable. And Exception's call stack
     */
    public static void W (String TAG, Exception e)
    {
        if(TAG != null && e != null)
        {
            Log.w(TAG, "Call Stack = " + getTwoLineInfo());
            Log.w(TAG, Log.getStackTraceString(e));
        }
    }
    /**
     * Print an information log message.</br>
     * @param TAG Used to identify the class name.
     * @param e An exception to log. And the toppest two stack trace elements of this throwable.
     */
    public static void I (String TAG, Exception e)
    {
        if(TAG != null && e != null)
        {
            Log.i(TAG, "ex="+e.getMessage()+getTwoLineInfo());
        }
    }
    /**
     * Checks to see whether or not a log is loggable by Htc_DEBUG_flag. </br>
     * @return Whether or not that this is allowed to be logged.
     */
    public static boolean isDebug() {
        return sIsDebug;
    }
    /**
     * Handy function to get a loggable stack trace from a Throwable.
     * @return All of stack trace elements while call this function.
     */
    public static String getLineInfo()
    {

        StackTraceElement[] ste = new Throwable().getStackTrace();
        StringBuffer result = new StringBuffer();

        if(ste.length <2)
            result.append("");

        if(isDebug())
        {
            result.append("[");
            result.append(ste[1].getMethodName());
            result.append("] \n");

            for(int i=1; i < ste.length; i++)
            {
                result.append("  (");
                result.append(i);
                result.append(") ");
                result.append(ste[i].getFileName());
                result.append(", Func:");
                result.append(ste[i].getMethodName());
                result.append(", Line:");
                result.append(ste[i].getLineNumber());
                result.append(" \n");
            }
        }
        return result.toString();
    }
    /**
     * Handy function to get a loggable stack trace from a Throwable.
     * @return The lastest stack trace element while call this function.
     */
    public static String getOneLineInfo()
    {

        StackTraceElement[] ste = new Throwable().getStackTrace();
        StringBuffer result = new StringBuffer();

        if(ste.length <2)
            result.append("");

        if(isDebug())
        {
            result.append("[");
            result.append(ste[1].getMethodName());
            result.append("] \n");

            for(int i=1; i < ste.length && i < 3; i++)
            {
                result.append("  (");
                result.append(i);
                result.append(") ");
                result.append(ste[i].getFileName());
                result.append(", Func:");
                result.append(ste[i].getMethodName());
                result.append(", Line:");
                result.append(ste[i].getLineNumber());
                result.append(" \n");
            }
        }
        return result.toString();
    }
    /**
     * Handy function to get a loggable stack trace from a Throwable.
     * @return The toppest two stack trace element while call this function.
     */
    public static String getTwoLineInfo()
    {

        StackTraceElement[] ste = new Throwable().getStackTrace();

        StringBuffer result = new StringBuffer();

        if(ste.length <2)
            result.append("");

        if(isDebug())
        {
            result.append("[");
            result.append(ste[1].getMethodName());
            result.append("] \n");

            for(int i=1; i < ste.length && i < 4; i++)
            {
                result.append("  (");
                result.append(i);
                result.append(") ");
                result.append(ste[i].getFileName());
                result.append(", Func:");
                result.append(ste[i].getMethodName());
                result.append(", Line:");
                result.append(ste[i].getLineNumber());
                result.append(" \n");
            }
        }
        return result.toString();
    }
}
