package com.htc.lib1.HtcCalendarFramework.util.calendar.holidays;

import android.content.Context;
import android.text.TextUtils;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
//import com.htc.wrap.android.provider.HtcWrapSettings;

import  android.provider.Settings.System;

import java.util.Locale;
import com.htc.lib1.HtcCalendarFramework.util.calendar.HtcWrapCustomization;

/**
  * Holiday Utils
  */
public class HolidayUtils {
	private static final String TAG = "HolidayUtils";

        /**
          * The none of the holiday type
          */	
	public static final int HOLIDAY_TYPE_NONE = 0;

        /**
          * The China of the holiday type
          */	
	public static final int HOLIDAY_TYPE_CHINA = 1;

        /**
          * The Japan of the holiday type
          */	
	public static final int HOLIDAY_TYPE_JAPAN = 2;
	
	/**
	  * To get the holiday type
	  * @param context the Context
	  * @return the holiday type
	  */
	public static int getHolidayType(Context context){
		if(isJapanHoildayEnable())
			return HOLIDAY_TYPE_JAPAN;
		
		if(isChinaHoildayEnable(context))
			return HOLIDAY_TYPE_CHINA;
		
		return HOLIDAY_TYPE_NONE;
	}
	
        /**
          * To determine if show solar holiday
          * @return boolean true in China SkU, false in other SKUs
          * @deprecated [Not use any longer]
          */
	/**@hide*/ 
	public static boolean isShowSolarHoilday() {
		return isChinaSku();
	}
	
	/**
	  * To determine if Japan holiday is enabled
	  * @return boolean true in Japan SKU, false in other SKUs
	  * @deprecated [Module internal use]
	  */
	/**@hide*/ 
	public static boolean isJapanHoildayEnable() {
		
		return isJapanSku();
	}
	
        /**
          * To determine if China holiday is enabled
          * @param context the Context
          * @return boolean true when the Lunar Calendar setting is set, false when the Lunar Calendar setting is not set
          * @deprecated [Module internal use]
          */
	/**@hide*/ 
	public static boolean isChinaHoildayEnable(Context context) {      
	    //need to get china holiday pacakge for china region
	    if(isChinaSku())
	        return true;
	    
	    final float FROM_VERSION = 2.0f;
		if (Float.parseFloat(HtcWrapCustomization.readString("System", "sense_version", "5.0")) >= FROM_VERSION) {
		    return isChinaLanguage();
		}

        return false;
	}
	
	public static final boolean isChinaLanguage() {
	    final String SUPPORT_LANGUAGE_CHINA = Locale.CHINA.getLanguage();
        final String SUPPORT_LANGUAGE_CHINESE = Locale.CHINESE.getLanguage();

        String systemLanguage = Locale.getDefault().getLanguage();
        if (TextUtils.isEmpty(systemLanguage)) {
            // if the current language is null, disable it.
            systemLanguage = Locale.ENGLISH.getLanguage();
        }
        
        if (systemLanguage.equalsIgnoreCase(SUPPORT_LANGUAGE_CHINESE)
                || systemLanguage.equalsIgnoreCase(SUPPORT_LANGUAGE_CHINA)) {
            return true;
        } else {
            return false;
        }
	}
	
	
	public static final String CALENDAR_SHOW_LUNAR = "calendar_show_lunar";
	public static boolean getLunarCalendarSetting(Context context) {
        String showLunar = System.getString(context.getContentResolver(), CALENDAR_SHOW_LUNAR);
        if (showLunar == null) {
            return true;
        } else {
            return showLunar.equals("1");
        }            
    }
	
	/**
	  * To determine if China SKU or not
	  * @return boolean true when "region" setting is 3 or 8 in ACC
	  * @deprecated [Module internal use]
	  */
	/**@hide*/ 
	public static boolean isChinaSku(){
		// This is China sku, not language. 
        int region = HtcWrapCustomization.readInteger("System", "region", 0) ;
	//remove HK region code from China Sku
        if(region == 3 )
            return true;
        return false;
    
	}
	
        /**
          * To determine if Japan SKU or not
          * @return boolean true when "region" setting is 4 in ACC
          * @deprecated [Module internal use]
          */
	/**@hide*/ 
	public static boolean isJapanSku(){
        int regin = HtcWrapCustomization.readInteger("System", "region", 0) ;
        if(regin == 4 )
            return true;
        return false;      
	}

        /**
          * @deprecated [Not use any longer]
          */
        /**@hide*/
        @Deprecated
    	public static boolean isChinaHoildayEnable() {
		return false;
	}

        /**
          * @deprecated [Not use any longer]
          */
        /**@hide*/
        @Deprecated
	public static int getHolidayType() {
		return HOLIDAY_TYPE_NONE;
	}
}
