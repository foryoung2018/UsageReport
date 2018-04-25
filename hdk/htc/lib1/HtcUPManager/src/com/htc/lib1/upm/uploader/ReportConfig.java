package com.htc.lib1.upm.uploader;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
/**
 * {@hide}
 */
public class ReportConfig {
	
	private static final String TAG = "Utils";
	private static final boolean IS_SHIPPING_ROM =  isShippingRom(Build.TYPE, WrapSystemProperties.get("ro.aa.report", ""), WrapSystemProperties.get("ro.sf", ""));
	
	private ReportConfig() {}
	
	/**
	 * Tell HTC can't send engineering report by judging Build.TYPE "userdebug" because of user ROM 
	 * will be earlier before CRC. Instead, new ROM needs to check ro.aa.report with ro.build.type and ro.sf
	 * Date: 2012/06/04
	 * @return
	 * @hide
	 */
	public static boolean isShippingRom() {
		return IS_SHIPPING_ROM;
	}
	
	private static boolean isShippingRom(String buildType, String roAaReport, String roSf) {
		if(TextUtils.isEmpty(roAaReport)) { 
			if(HtcWrapHtcDebugFlag.Htc_DEBUG_flag) Log.d(TAG, "[Old ROM] isShippingROM: " + "user".equals(buildType));
			return "user".equals(buildType);
		}
		else {
			char lastCharOfSF = 0x00;
			if(roSf != null && roSf.length() > 0)
				lastCharOfSF = roSf.charAt(roSf.length()-1);

			// Consider as shipping ROM from all of the following three cases
			// 1. 'user' of ro.build.type
			// 2. All cases of ro.aa.report except 'eng'
			// 3. All cases of ro.sf except '0'
			if("user".equals(buildType) && !"eng".equals(roAaReport) && lastCharOfSF != '0') {
				if(HtcWrapHtcDebugFlag.Htc_DEBUG_flag) Log.d(TAG, "[New ROM] isShippingROM: true");
				return true;
			}
			
			if(HtcWrapHtcDebugFlag.Htc_DEBUG_flag) Log.d(TAG, "[New ROM] isShippingROM: false");
			return false;
		}
	}
}
