/**
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2010 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of HTC Corporation
 * ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
 * right to employ this work within the scope of this statement.  Nevertheless, the 
 * Authorized User shall not use this work for any purpose other than the purpose 
 * agreed by HTC.  Any and all addition or modification to this work shall be 
 * unconditionally granted back to HTC and such addition or modification shall be 
 * solely owned by HTC.  No right is granted under this statement, including but not 
 * limited to, distribution, reproduction, and transmission, except as otherwise 
 * provided in this statement.  Any other usage of this work shall be subject to the 
 * further written consent of HTC.
 */
package com.htc.lib1.masthead.view;

import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.masthead.R;
import com.htc.lib1.weather.resource.WeatherIcon;
import com.htc.lib1.weather.resource.WeatherText;

//import com.htc.weather.StateResources;

/**
 * A helper class to retrieve HTC common resource identifiers.
 */
class ResourceHelper {    
	private static final String LOG_TAG = ResourceHelper.class.getSimpleName();
	
	private static final String THEME_APK_NAME = "WeatherClock";
	private static final String THEME_PACKAGE_NAME = "com.htc.theme.weatherclock";
	
	private static final String THEME_RES_TYPE_INTEGER = "integer";
	private static final String THEME_RES_TYPE_DRAWABLE = "drawable";
	
	private static final String THEME_RES_NAME_FORMAT_WEATHER = "weather_vectorgraphic_dark_xl_%s";
	private static final String THEME_RES_NAME_FORMAT_DIGIT_HOUR = "weather_clock_%s_dark_hour";
	private static final String THEME_RES_NAME_FORMAT_DIGIT_MINUTE = "weather_clock_%s_dark_minute";
	
	private static final String THEME_RES_NAME_TEMPLATE = "theme_template";
	private static final String THEME_RES_NAME_COLON = "weather_clock_point_dark";

	private static Drawable[] sThemeDrawableCache_min = new Drawable[10];
	private static Drawable[] sThemeDrawableCache_hour  = new Drawable[10];

	private static boolean[] sIsDirtyCache_min = new boolean[10];
	private static boolean[] sIsDirtyCache_hour  = new boolean[10];
	
	private static Resources sThemeResource;
	private static int mThemeTemplate = -1;
	
    public static String getSharedString(Context context, int id) {
		if (context != null && id != 0) {
		    try {
		        return context.getResources().getString(id);
		    } catch (NotFoundException e) {
		        // falling through
		    }
		}
		Logger.w(LOG_TAG, "Shared resource not found:" + id);
		return null;
    }

    public static View inflateLayout(Context context, int resourceId, ViewGroup root, boolean attach){
        if(context == null || resourceId == 0){
            return null;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        if(inflater == null){
            return null;
        }
        return inflater.inflate(resourceId, root, attach);
    }
    
    public static int getSharedDimensionPixelSize(Context context, int id) {
		if (context != null && id != 0) {
		    return context.getResources().getDimensionPixelSize(id);
		}
		return 0;
    }

    public static float getSharedDimension(Context context, int id) {
		if (context != null && id != 0) {
		    return context.getResources().getDimension(id);
		}
		return 0;
    }
    
	public static int getSharedBitmapHeight(Context context, int id) {
		if (context != null && id != 0) {
			 if (context != null) {
				 Resources res = context.getResources();
				 BitmapFactory.Options opt = new BitmapFactory.Options();
				 opt.inJustDecodeBounds = true;
				 BitmapFactory.decodeResource(res, id, opt);
				 return opt.outHeight;
			 }
		}
		return 0;
	}

/*    public static Timeline getWeatherTimeline(Context context, int condition) {
    	if (context != null) {
    		return createWeatherTimeline(context, condition);
    	}
    	return null;
    }
 */   
/*    private static Timeline createWeatherTimeline(Context context, int condition) {
    	switch (condition) {
    	case 1:
    		return new Sunny(context);
    	case 2:
    		return new MostlySunny(context, true);
    	case 3:
    		return new PartlySunny(context, true);
    	case 4:
    		return new IntermittentClouds(context, true);
    	case 5:
    		return new Hazy(context, true);
    	case 6:
    		return new MostlyCloudy(context, true);
    	case 7:
    		return new Cloudy(context);
    	case 8:
    		return new Dreary(context, false);
    	case 11:
    		return new Dreary(context, true);
    	case 12:
    		return new Showers(context, Dripping.Type.SHOWER);
    	case 13:
    		return new MostlyShowers(context, Dripping.Type.SHOWER, true);
    	case 14:
    		return new PartlySunny(context, Dripping.Type.SHOWER, true);
    	case 15:
    		return new Showers(context, Dripping.Type.THUNDER);
    	case 16:
    		return new MostlyShowers(context, Dripping.Type.THUNDER, true);
    	case 17:
    		return new PartlySunny(context, Dripping.Type.THUNDER, true);
    	case 18:
    		return new Showers(context, Dripping.Type.RAIN);
    	case 19:
    		return new Showers(context, Dripping.Type.FLURRIES);
    	case 20:
    		return new MostlyShowers(context, Dripping.Type.FLURRIES, true);
    	case 21:
    		return new PartlySunny(context, Dripping.Type.FLURRIES, true);
    	case 22:
    		return new Showers(context, Dripping.Type.SNOW);
    	case 23:
    		return new MostlyShowers(context, Dripping.Type.SNOW, true);
    	case 24:
    		return new Showers(context, Dripping.Type.ICE);
    	case 25:
    		return new Showers(context, Dripping.Type.SLEET);
    	case 26:
    		return new Showers(context, Dripping.Type.FREEZING_RAIN);
    	case 29:
    		return new Showers(context, Dripping.Type.RAIN_SNOW_MIXED);
    	case 30:
    		return new Hot(context);
    	case 31:
    		return new Cold(context);
    	case 32:
    		return new Windy(context);
    	case 33:
    		return new Clear(context);
    	case 34:
    		return new MostlySunny(context, false);
    	case 35:
    		return new PartlySunny(context, false);
    	case 36:
    		return new IntermittentClouds(context, false);
    	case 37:
    		return new Hazy(context, false);
    	case 38:
    		return new MostlyCloudy(context, false);
    	case 39:
    		return new PartlySunny(context, Dripping.Type.SHOWER, false);
    	case 40:
    		return new MostlyShowers(context, Dripping.Type.SHOWER, false);
    	case 41:
    		return new PartlySunny(context, Dripping.Type.THUNDER, false);
    	case 42:
    		return new MostlyShowers(context, Dripping.Type.THUNDER, false);
    	case 43:
    		return new MostlyShowers(context, Dripping.Type.FLURRIES, false);
    	case 44:
    		return new MostlyShowers(context, Dripping.Type.SNOW, false);
    	case 51:
    		return new MostlyShowers(context, Dripping.Type.SLEET, true);
    	case 52:
    		return new SandDust(context, false);
    	case 53:
    		return new SandDust(context, true);
    	case 54:
    		return new Tornado(context);
    	default:
    		return null;
    	}
    }
*/
    public static String getWeatherText(Context context, int condition) {
    	String text = WeatherText.getConditionText(context, condition);
    	return text;
    }
    
    public static Drawable getNoWeatherIcon(Context context) {
    	Drawable icon = null;
    	icon = getThemeNoWeatherIcon();
    	if (icon != null)
    		return icon;

    	icon = context.getResources().getDrawable(R.drawable.weather_icon_no_info_code);
    	return icon;
    }
    
    public static Drawable getWeatherIcon(Context context, int condition) {
    	Drawable icon = null;
    	icon = getThemeWeatherIcon(condition);
    	if (icon != null)
    		return icon;
    	
    	WeatherIcon wi = new WeatherIcon();
    	icon = wi. getConditionIconDark(context, condition);

    	return icon;
    }
    
    public static int getThemeTemplate() {
    	if (sThemeResource == null)
    		return 0;
    	if (mThemeTemplate < 0) {
    		mThemeTemplate = 0;
	    	int resId = sThemeResource.getIdentifier(THEME_RES_NAME_TEMPLATE, THEME_RES_TYPE_INTEGER, THEME_PACKAGE_NAME);
	    	if (resId > 0) {
	    		mThemeTemplate = sThemeResource.getInteger(resId);
	    	}
    	}
    	return mThemeTemplate;
    }
    
    public static Drawable getThemeNoWeatherIcon() {
    	if (sThemeResource == null)
    		return null;
    	
    	String themeResName = "weather_icon_no_info_dark";
    	int resId = sThemeResource.getIdentifier(themeResName, THEME_RES_TYPE_DRAWABLE, THEME_PACKAGE_NAME);
    	if (resId > 0) {
    		return sThemeResource.getDrawable(resId);
    	}
    	return null;
    }
    
    public static Drawable getThemeWeatherIcon(int condition) {
    	if (sThemeResource == null)
    		return null;
    	
    	String conditionString = condition > 9 ? Integer.toString(condition) : "0".concat(Integer.toString(condition));
    	String themeResName = String.format((Locale) null, THEME_RES_NAME_FORMAT_WEATHER, conditionString);
    	int resId = sThemeResource.getIdentifier(themeResName, THEME_RES_TYPE_DRAWABLE, THEME_PACKAGE_NAME);
    	if (resId > 0) {
    		return sThemeResource.getDrawable(resId);
    	}
    	return null;
    }
    
    public static Drawable getThemeDigitHourIcon(int num) {
    	if (sThemeResource == null)
    		return null;
    	
    	if (!sIsDirtyCache_hour[num])
    		return sThemeDrawableCache_hour[num];
    	
    	String themeResName = String.format((Locale) null, THEME_RES_NAME_FORMAT_DIGIT_HOUR, Integer.toString(num));
    	int resId = sThemeResource.getIdentifier(themeResName, THEME_RES_TYPE_DRAWABLE, THEME_PACKAGE_NAME);
    	if (resId > 0) {
    		sThemeDrawableCache_hour[num] = sThemeResource.getDrawable(resId);
    	}
    	sIsDirtyCache_hour[num] = false;
    	return sThemeDrawableCache_hour[num];
    }
    
    public static Drawable getThemeDigitMinuteIcon(int num) {
    	if (sThemeResource == null)
    		return null;
    	
    	if (!sIsDirtyCache_min[num])
    		return sThemeDrawableCache_min[num];
    	
    	String themeResName = String.format((Locale) null, THEME_RES_NAME_FORMAT_DIGIT_MINUTE, Integer.toString(num));
    	int resId = sThemeResource.getIdentifier(themeResName, THEME_RES_TYPE_DRAWABLE, THEME_PACKAGE_NAME);
    	if (resId > 0) {
    		sThemeDrawableCache_min[num] = sThemeResource.getDrawable(resId);
    	}
    	sIsDirtyCache_min[num] = false;
    	return sThemeDrawableCache_min[num];
    }
    
    public static Drawable getThemeDigitColonIcon() {
    	if (sThemeResource == null)
    		return null;
    	
    	int resId = sThemeResource.getIdentifier(THEME_RES_NAME_COLON, THEME_RES_TYPE_DRAWABLE, THEME_PACKAGE_NAME);
    	if (resId > 0) {
    		return sThemeResource.getDrawable(resId);
    	}
    	return null;
    }

    public static void initThemeResources(Context context) {
   		sThemeResource = HtcCommonUtil.getResources(context, THEME_APK_NAME);
    	mThemeTemplate = -1;
    	for (int i = 0; i < 10; ++i) {
    		sIsDirtyCache_min[i] = true;
    		sIsDirtyCache_hour[i] = true;
    		sThemeDrawableCache_min[i] = null;
    		sThemeDrawableCache_hour[i] = null;
    	}
    }
    
    public static void releaseThemeResources() {
    	sThemeResource = null;
    	mThemeTemplate = -1;
    	for (int i = 0; i < 10; ++i) {
    		sIsDirtyCache_min[i] = true;
    		sIsDirtyCache_hour[i] = true;    		
    		sThemeDrawableCache_min[i] = null;
    		sThemeDrawableCache_hour[i] = null;
    	}    	
    }
}
