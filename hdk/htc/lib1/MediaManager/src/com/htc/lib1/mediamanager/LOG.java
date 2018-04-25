package com.htc.lib1.mediamanager;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import android.util.Log;

/**
 *  Hide Automatically by Justin Hou
 *  @hide
 */
public class LOG {

    private static String TAG = "MediaManager_HDK";
    private static boolean sIsDebug = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;

    /**
     * Checks to see whether or not a log is loggable by Htc_DEBUG_flag. </br>
     * @return Whether or not that this is allowed to be logged.
     */
    public static boolean isDebug() { return sIsDebug;}

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

            for(int i=1;i < ste.length;i++)
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

            for(int i=1;i < ste.length && i < 3;i++)
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

            for(int i=1;i < ste.length && i < 4;i++)
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
     * Print an error log message.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach. 
     */
    public static void E(String userTAG, String Info)
    {
        if (userTAG != null && Info != null)
        {
            Log.e(TAG, "[" + userTAG + "]\t" + Info);
        }
    }

    /**
     * Print a warn log message and log the exception.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach. 
     */
    public static void W(String userTAG, String Info)
    {
        if (userTAG != null && Info != null)
        {
            Log.w(TAG, "[" + userTAG + "]\t" + Info);
        }
    }


    /**
     * Print a verbose log message.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach. 
     */
    public static void V(String userTAG, String Info)
    {
        if (isDebug() == true && userTAG != null && Info != null)
        {
            Log.v(TAG, "[" + userTAG + "]\t" + Info);
        }
    }

    /**
     * Print a debug log message.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach. 
     */
    public static void I(String userTAG, String Info)
    {
        if (isDebug() == true && userTAG != null && Info != null)
        {
            Log.i(TAG, "[" + userTAG + "]\t" + Info);
        }
    }

    /**
     * Print a debug log message.</br>
     * @param TAG Used to identify the class name.
     * @param Info The message you need to attach. 
     */
    public static void D(String userTAG, String Info)
    {
        if (isDebug() == true && userTAG != null && Info != null)
        {
            Log.d(TAG, "[" + userTAG + "]\t" + Info);
        }
    }

    /**
     * Print an information log message.</br>
     * @param TAG Used to identify the class name.
     * @param e An exception to log. And the toppest two stack trace elements of this throwable. And Exception's call stack 
     */
    public static void W(String userTAG, Exception e)
    {
        if (userTAG != null && e != null)
        {
            Log.w(TAG, "[" + userTAG + "]\t" + "Call Stack = " + getTwoLineInfo());
            Log.w(TAG, "[" + userTAG + "]\t" + Log.getStackTraceString(e));
        }
    }

    /**
     * Print an information log message.</br>
     * @param TAG Used to identify the class name.
     * @param e An exception to log. And the toppest two stack trace elements of this throwable. 
     */
    public static void I(String userTAG, Exception e)
    {
        if (userTAG != null && e != null)
        {
            Log.i(TAG, "[" + userTAG + "]\t" + "ex=" + e.getMessage() + getTwoLineInfo());
        }
    }
}
