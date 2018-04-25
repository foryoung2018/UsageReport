package com.htc.lib1.cc.htcjavaflag;

import android.util.Log;

/*
 * @deprecated [module internal use] Only use in CC, please do not use it
 */
@Deprecated
public class HtcDebugFlag {

    static Boolean DEBUG_FLAG = null;

    static Short PROJECT_FLAG = null;
    static Short LANGUAGE_FLAG = null;

    final static short HTCLOG_MASK_VERBOSE_DEBUG_INFO = (short)0x0707;

    public final static boolean getHtcDebugFlag() {

//        if (DEBUG_FLAG == null) {
//
//            int buffer = Native.htcDebugFlagFromJNI();
//            short flag = (short)((buffer >>> 8) & HTCLOG_MASK_VERBOSE_DEBUG_INFO);
//
//            if (flag == 0)
//                DEBUG_FLAG = false;
//            else
//                DEBUG_FLAG = true;
//        }
//        return DEBUG_FLAG;
        return Log.isLoggable("htc.debug", Log.DEBUG);
    }
    /** No need for these 2 method after sense 5.5
    public final static short getProject() {

        if (PROJECT_FLAG == null){

            String str = Native.getProjectFromJNI();
            PROJECT_FLAG = HtcBuildFlag.PROJECT_DEFAULT_flag;
            try {

                if (str.trim().equals("unknown")) {
                    PROJECT_FLAG = HtcBuildFlag.PROJECT_DEFAULT_flag;
                } else {
                    String[] temp = str.trim().split(",");
                    PROJECT_FLAG = Short.decode((temp[1]).trim());
                }

            } catch (NumberFormatException e) {

            } catch (Exception e) {

            }
        }
        return PROJECT_FLAG;
    }

    public final static short getLanguage() {

        if (LANGUAGE_FLAG == null){

            String str = Native.getLanguageFromJNI();
            LANGUAGE_FLAG = HtcBuildFlag.LANGUAGE_DEFAULT_flag;
            try {

                if (str.trim().equals("unknown")) {
                    LANGUAGE_FLAG = HtcBuildFlag.LANGUAGE_DEFAULT_flag;
                } else {
                    String[] temp = str.trim().split(",");
                    LANGUAGE_FLAG = Short.decode((temp[1]).trim());
                }

            } catch (NumberFormatException e) {

            } catch (Exception e) {

            }
        }
        return LANGUAGE_FLAG;
    }
    **/

}

class Native {
    static {
        // The runtime will add "lib" on the front and ".o" on the end of
        // the name supplied to loadLibrary.
        System.loadLibrary("htcflag-jni");
    }

    static native int htcDebugFlagFromJNI();
    static native String getProjectFromJNI();
    static native String getLanguageFromJNI();
}
