package com.htc.lib1.upm.uploader;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.htc.lib1.upm.HtcUPDataUtils;
import com.htc.lib1.upm.Common;
import com.htc.lib1.upm.Log;
import com.htc.xps.pomelo.log.DeviceInfo;
import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;

import org2.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org2.bouncycastle.jcajce.provider.digest.SHA3.Digest224;

public class DeviceInfoHelper {
	private final static String TAG = "DeviceInfoHelper";
	private final static String OLD_DERANGEMENT = "aO9Mz0H3tT";
	private final static String NEW_DERANGEMENT = "p7zxuhgzq5";
	private final static String NONHTC_DERANGEMENT = "npe8SXpx1m";
	
	public static DeviceInfo getLogDeviceInfo(Context ctx) {
	    DeviceInfo.Builder deviceInfoBuilder = new DeviceInfo.Builder();
		
		deviceInfoBuilder.region(getRegion(ctx));                                                    //Default value: unknown
		deviceInfoBuilder.city(Common.STR_UNKNOWN);                                      //Default value: unknown   
		deviceInfoBuilder.time_zone(UploadUtils.getTimeZone());
		deviceInfoBuilder.cid(Common.STR_UNKNOWN);                           //Default value: unknown
		deviceInfoBuilder.rom_version(getRomVersion(Common.STR_UNKNOWN)); //Default value: unknown
		deviceInfoBuilder.sense_version(getSenseVersion(ctx));  //Set 'unknown' for sense version  if we are in competitor device.
		deviceInfoBuilder.model_id(getModelId(ctx, Common.STR_UNKNOWN));            //Default value: unknown
		deviceInfoBuilder.device_id(getHashedSNForCSBI()); //[2016.7.21 Eric Lu] Set to bouncycastle-SHA3 hash SN for CSBI in N.
		deviceInfoBuilder.device_SN(getDeviceID(ctx));                                           //Default value: unknown
		deviceInfoBuilder.privacy_statement_version(Common.STR_UNKNOWN);     //TODO: CHECK

		return deviceInfoBuilder.build();		
	}
	
	// begin-region ==
	public static String getRegion(Context ctx) {
		TelephonyManager telmgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String country = telmgr.getNetworkCountryIso();
		if(TextUtils.isEmpty(country))
			country = Common.STR_UNKNOWN;
		return country;
	}
	// end-region ==
    
	// begin-rom-version ==
	public static String getRomVersion(String defaultValue) {
		return WrapSystemProperties.get("ro.build.description", defaultValue);
	}
	// end-vom-version ==
	
	// begin-sense-version ==
	private static String getSenseVersion(Context context) {
		return UploadUtils.getSenseVersionByCustomizationManager(context);
	}
	// end-sense-version ==
	
	// begin-model-id ==
	private static String getModelId(Context context, String defaultValue) {
		String brand = Build.BRAND;
		String model = WrapSystemProperties.get("ro.aa.project", defaultValue);
		
		//if we cannot get the system properties, then use Build.MODEL.
        if (model.equals(defaultValue))
        	model = Build.MODEL; 
        
        if (TextUtils.isEmpty(model))
        	model = defaultValue;
        
        if (Common._DEBUG) 
            Log.d(TAG, "Model ID: " + brand + ";" + model);
		return brand + ";" + model;
	}
	// end-model-id ==
	
	// begin-imei ==	
	/*private static String getIMEI(Context ctx) {
        String deviceId = null;
        TelephonyManager telmgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if(telmgr != null) {
            deviceId = telmgr.getDeviceId();
            deviceId = TextUtils.isEmpty(deviceId) ? Common.STR_UNKNOWN : deviceId;
        }
        return deviceId;
    }*/
	// end-imei ==
	
	// begin-device-sn ==	
	private static String getDeviceSN(Context context) {
		if (HtcUPDataUtils.isHtcDevice(context) && isHackerDevice())
			return HACKER_SN;
		
		return TextUtils.isEmpty(Build.SERIAL) ? Common.STR_UNKNOWN : Build.SERIAL;
    }
	// end-device-sn ==
	
	// begin-device-ID ==
    private static String getDeviceID(Context context) {
    	if (HtcUPDataUtils.isHtcDevice(context) && isHackerDevice())
			return HACKER_SN;
    	
    	return getNewHashedID(context);
    }	
	// end-device-ID ==
	
	//Begin the filtering for hacker's device
	// The serial number 
	private final static String HACKER_SN = "HackerDevice";
	private final static int SMALL_A = 'a' & 0X00FF; 
	private final static int SMALL_Z = 'z' & 0X00FF;
	private final static int BIG_A = 'A' & 0X00FF;
	private final static int BIG_Z = 'Z' & 0X00FF;
	private final static int ZERO = '0' & 0X00FF;
	private final static int NINE = '9' & 0X00FF;
	 
	public static boolean isHackerDevice() {
	    String sn = Build.SERIAL; 
	    if((sn == null) || (sn.length() == 0))    return true;
	    for(int i = 0; i < sn.length(); i++)    {
	        int sting = sn.charAt(i) & 0X0FFFF;
	        
	        if(((sting >= SMALL_A) && (sting <= SMALL_Z)) ||
	           ((sting >= BIG_A) && (sting <= BIG_Z)) ||
	           ((sting >= ZERO) && (sting <= NINE)))
	            continue;
	        
	        return true;
	    }
	    
	    return false;		 
	}
	//end of hacker device's filtering
	
    private static String getFinalDerangement(boolean isHtcDevice, String sense) {
    	if (!isHtcDevice)
    		return NONHTC_DERANGEMENT;
    	
        try {
            float senseVersion = Float.parseFloat(sense);
            return (senseVersion >= 7) ? NEW_DERANGEMENT : OLD_DERANGEMENT;
        } catch (Exception e) {
        	Log.e(TAG, "[getFinalDerangement] Failed to parse sense version: " + sense, e);
        }
        return OLD_DERANGEMENT;
	}
	
	/**
	 * Use SHA-256 hash algorithm to generate new hashed ID of device.<br>
	 * Rex, 2013/07/03
	 * @param ctx
	 * @param androidID
	 * @return Hex string of new hashed ID or 'unknown' if exception occurs.
	 */
	private static String getNewHashedID(Context ctx) {
		String sn = getDeviceSN(ctx);
		String derangement = getFinalDerangement(HtcUPDataUtils.isHtcDevice(ctx),getSenseVersion(ctx));
		String input = String.format("%s.%s.%s", Build.BRAND, sn, derangement);
		if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag) Log.d(TAG, "[getNewHashedID] ID: " + input);
		String result = Common.STR_UNKNOWN;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] data = input.getBytes();
			md.update(data);
			byte[] hashedBytes = md.digest();
			result = UploadUtils.byteArrayToHexString(hashedBytes);
			if (HtcWrapHtcDebugFlag.Htc_SECURITY_DEBUG_flag) Log.d(TAG, "[getNewHashedID] New hashed ID: " + result);
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "[getNewHashedID] No such hashed algorithm: " + e.getMessage());
		}
		return result;
	}

	/***
	 * Use 3rd-party bouncycastle SHA3 algorithm to hash SN for CSBI.<br>
	 * Eric Lu, 2016/07/21
	 * @return If exist DeviceSN, return hashed DeviceSN, or return empty string.
	 */
	private static String getHashedSNForCSBI() {
		try {
			if (!TextUtils.isEmpty(Build.SERIAL)) {
				DigestSHA3 sha3 = new Digest224();
				sha3.update(Build.SERIAL.getBytes("UTF-8"));
				return UploadUtils.byteArrayToHexString(sha3.digest());
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "[getHashedSNForCSBI] Unsupported Encoding Exception: " + e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, "[getHashedSNForCSBI] Fail to get hashed SN: " + e.getMessage());
		}
		return "";
	}
	
}
