package com.htc.lib1.locationservicessettingmanager.util;

import java.io.File;

import com.htc.lib0.customization.HtcWrapCustomizationManager;
import com.htc.lib0.customization.HtcWrapCustomizationReader;
import com.htc.lib1.cc.util.HtcCommonUtil;
import com.htc.lib1.locationservicessettingmanager.R;
import com.htc.lib1.locationservicessettingmanager.R.styleable;
import com.htc.lib1.locationservicessettingmanager.util.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Environment;

public class Utils {
	
	public static final int THEME_CATEGORY = HtcCommonUtil.BASELINE;
	private static final String TAG = Utils.class.getSimpleName();
	
	//#Common color
	public static int getOverlayColor(Context context) {	
		return HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_overlay_color);
	}
	
	public static int getCategoryColor(Context context) {	
		return HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_category_color);
	}
	
	public static int getMultiplyColor(Context context) {	
		return HtcCommonUtil.getCommonThemeColor(context, R.styleable.ThemeColor_multiply_color);
	}
	
	public static Drawable getStatusBarTexture(Context context) {
		return HtcCommonUtil.getCommonThemeTexture(context, R.styleable.CommonTexture_android_windowBackground);
	}
	
	public static Drawable getActionBarTexture(Context context) {
		return HtcCommonUtil.getCommonThemeTexture(context, R.styleable.CommonTexture_android_headerBackground);
	}
	
	//#StatusBar
	private static int mStatusBarHeight = Integer.MIN_VALUE;

	public static int getStatusBarHeight(Context context) {
		if (mStatusBarHeight < 0) {
			mStatusBarHeight = getAndroidStatusBarHeight(context);
		}
		return mStatusBarHeight;
	}

	private static final int DEFAULT_STATUS_BAR_HEIGHT = 75;
	private static final int DEFAULT_STATUS_BAR_HEIGHT_DP = 25;
	
	private static int getAndroidStatusBarHeight(Context context) {
		if (context == null) {
			SMLog.w(TAG, "context is null");
			return DEFAULT_STATUS_BAR_HEIGHT;
		}
		Resources res = context.getResources();
		if (res == null) {
			SMLog.w(TAG, "res is null");
			return DEFAULT_STATUS_BAR_HEIGHT;
		}

		int id = res.getIdentifier("status_bar_height", "dimen","android");
		if (id > 0) {
			int retValue = res.getDimensionPixelSize(id);
			SMLog.i(TAG, "google status_bar_height is :" + retValue);
			return retValue;
		} else {
			final float scale = res.getDisplayMetrics().density;
			// Convert the statusBarHeight 25dps to pixels, based on density scale
			int returnValue = (int) (DEFAULT_STATUS_BAR_HEIGHT_DP * scale + 0.5f);
			SMLog.i(TAG, "returnValue is :" + returnValue);

			return returnValue;
		}
	}
	
	private static Boolean mIsGMSPreferred = null;
	private static final String GMS_PREFERRED = "GMS_PREFERRED";
	private static boolean isGMSPreferred()
	{
		if (mIsGMSPreferred == null)
		{
			if(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), GMS_PREFERRED).exists()) {
				mIsGMSPreferred = true;
				SMLog.d(TAG, "GMS_PREFERRED");
			} else {
				mIsGMSPreferred = false;
			}
		}
		return mIsGMSPreferred.booleanValue();
	}

	private static Boolean mIsChinaSku = null;
	public static boolean isChinaSku()
	{
		if (mIsChinaSku == null)
		{
			HtcWrapCustomizationManager manager = new HtcWrapCustomizationManager();
			String readerName = "system";
			HtcWrapCustomizationReader reader = manager.getCustomizationReader(readerName,
					HtcWrapCustomizationManager.READER_TYPE_XML, true);
			int regioncode = reader.readInteger("region", 0);

			//		Log.d(TAG,"region:"+regioncode);

			mIsChinaSku = (regioncode == 3 && !isGMSPreferred());       // It means "China" build;
		}

		return mIsChinaSku.booleanValue();
	}
	
    private static final String GOOGLE_MAPS_SHARED_LIBRARY_NAME = "com.google.android.maps";
    private static boolean mIsGoogleMapsSharedLibraryChecked = false;
    private static boolean mIsGoogleMapsSharedLibraryExist = false;  
    public static boolean isGoogleMapsSharedLibraryExist(Context context) {
		if(!mIsGoogleMapsSharedLibraryChecked)
		{
			String[] installedLibraries = context.getPackageManager().getSystemSharedLibraryNames();
			if(installedLibraries != null)
			{
				for(String str : installedLibraries)
				{
					if(str.equalsIgnoreCase(GOOGLE_MAPS_SHARED_LIBRARY_NAME))
					{
						mIsGoogleMapsSharedLibraryExist = true;
						break;
					}
				}

			}
			mIsGoogleMapsSharedLibraryChecked = true;
		}
		return mIsGoogleMapsSharedLibraryExist;
	}

	public static boolean isNetworkLocationEnabled(Context context) {
		boolean result = false;
		if (context != null) {
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if (manager != null) {
				result = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			}
		}
		return result;
	}
}
