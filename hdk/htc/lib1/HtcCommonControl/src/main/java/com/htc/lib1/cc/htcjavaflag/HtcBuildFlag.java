
//HTC customized flag for java

package com.htc.lib1.cc.htcjavaflag;

/*
 * @deprecated [module internal use] Only use in CC, please do not use it
 */
@Deprecated
public class HtcBuildFlag
{
// Before add your java flage here, please contact Bill_Yang( Bill_Yang@htc.com, Nelson_Li@htc.com, cc: Morris_Lin@htc.com David.DC_Lo@htc.com), thanks.
// DEBUG usage
    public final static boolean Htc_SECURITY_DEBUG_flag        =  true ;
    public final static boolean Htc_DEBUG_flag         =  getHtc_DEBUG_flag();
    public final static boolean getHtc_DEBUG_flag() {return HtcDebugFlag.getHtcDebugFlag() ;}

// for HTC_DISCLOSE_FLAG
    public final static boolean HTC_DISCLOSE_FLAG        = getHTC_DISCLOSE_FLAG();
    public final static boolean getHTC_DISCLOSE_FLAG() {return false ;}
}

