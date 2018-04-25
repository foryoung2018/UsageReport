package com.htc.lib1.exifengine;

import android.text.TextUtils;
import android.text.format.DateFormat;

import com.htc.lib1.exifengine.i18n.I18nRes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author amt_masd_kg_shen@htc.com
 */
class ExifAccessFilterBase implements ExifAccessFilter {
	private static final String TIME_STAMP_FORMAT = "yyyyMMddHHmmss";
	private static final String DATE_STAMP_FORMAT = "yyyyMMddHH";
	private final Map<String, String> i18nMap = new HashMap<String, String>();
	private Locale mCurrentI18ValueLocale;

	private synchronized void checkI18nValues(Locale locale) {
		if (mCurrentI18ValueLocale != locale) {
			mCurrentI18ValueLocale = locale;
			i18nMap.clear();
			I18nRes.loadRes(locale, i18nMap);
		}
	}

	private synchronized String getAppendUnit(String key) {
		String v = getString(key);
        if(v == null){
            return "";
        }
		if ( v.length() > 0 && v.charAt(0) != ' ') {
			v = ' ' + v;
		}
		return v;
	}

	private synchronized String getString(String key) {
		return i18nMap.get(key);
	}

	@Override
	public String onFilterExif(int exifTag, String defFormatResult, ExifEngine.AttrsMap kvPair, Locale locale) {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		checkI18nValues(locale);
		String result = null;
		switch (exifTag & ExifEngine.MASK_TAG_ID) {
			case ExifEngine.EXIF_TAG_EXPOSURE_TIME & ExifEngine.MASK_TAG_ID:
				// float:EXPOSURE_TIME
				// format:time>1? "%i sec." : "1/%i sec."
				// ---------------------------compromise_result
				// string:compromise value
				// format:none
				double et = kvPair.optDouble(ATTR_EXPOSURE_TIME, 0D);
				if (et > 1d) {
					result = ((int) et) + getAppendUnit(I18nRes.UNIT_SECOND);
				} else if (et != 0d) {
					result = String.format("1/%d", (int) (1d / et)) + getAppendUnit(I18nRes.UNIT_SECOND);
				}
				break;
			case ExifEngine.EXIF_TAG_SHUTTER_SPEED_VALUE & ExifEngine.MASK_TAG_ID:
				// float:EV//"%.02f EV"
				// float:shutter_speed_time//time>1?"%i sec.":"1/%i sec."
				// format:"%.02f EV" time>1?"%i sec.":"1/%i sec."
				// (4.06EV(1/16 sec.))
				// ---------------------------compromise_result
				// string:compromise value
				// format:none
				double speed = kvPair.optDouble(ATTR_SHUTTER_SPEED, 0D);
				if (speed > 1d) {
					result = ((int) speed) + getAppendUnit(I18nRes.UNIT_SECOND);
				} else if (speed != 0d) {
					result = String.format("1/%d", (int) (1d / speed)) + getAppendUnit(I18nRes.UNIT_SECOND);
				}
				break;
			case ExifEngine.EXIF_TAG_DATE_TIME_DIGITIZED & ExifEngine.MASK_TAG_ID:
			case ExifEngine.EXIF_TAG_DATE_TIME_ORIGINAL & ExifEngine.MASK_TAG_ID:
			case ExifEngine.EXIF_TAG_DATE_TIME & ExifEngine.MASK_TAG_ID:
				result = tryFormatExifTime(
						kvPair.get(ATTR_TIMESTAMP),//
						DEF_EXIF_TIMESTMAP_FORMAT,//
						TIME_STAMP_FORMAT,//
						locale);
				break;
			case ExifEngine.EXIF_TAG_APERTURE_VALUE & ExifEngine.MASK_TAG_ID:
			case ExifEngine.EXIF_TAG_MAX_APERTURE_VALUE & ExifEngine.MASK_TAG_ID:
				// float:EXPOSURE_VALUE(EV)
				// float:APERTURE
				// format:"%.02f EV" (f/%.01f)
				// ---------------------------compromise_result
				// string:compromise value
				// format:none
				break;
			case ExifEngine.EXIF_TAG_FOCAL_LENGTH & ExifEngine.MASK_TAG_ID:
				// float:FOCAL_LENGTH//"%.1f mm"
				// float:equivalent_35//(35 equivalent)
				// format:"%.1f mm" (35 equivalent: %d mm)
				// ---------------------------compromise_result
				// string:compromise value
				// format:none
				double fl = kvPair.optDouble(ATTR_FOCAL_LENGTH, 0D);
				if (fl != 0) {
					result = String.format("%.1f", fl) + getAppendUnit(I18nRes.UNIT_MM);
				}
				break;
			case ExifEngine.EXIF_TAG_GPS_TIME_STAMP & ExifEngine.MASK_TAG_ID:
				// int:gps_time_stamp_i
				// int:gps_time_stamp_j
				// int:gps_time_stamp_d
				// format:"%02u:%02u:%05.2f"
				// format sample:("%02u:%02u:%05.2f", i, j, d)
				// ---------------------------compromise_result
				// string:compromise value
				// format:none
				try {
					int h = kvPair.optInt(ATTR_DATE_HOUR, 0);
					int m = kvPair.optInt(ATTR_DATE_MINUTE, 0);
					float fs = kvPair.optFloat(ATTR_DATE_SECOND_FLOAT, 0f);
					result = String.format("%02d:%02d:%05.2f", h, m, fs) + getAppendUnit(I18nRes.UNIT_SECOND);
				} catch (Exception e) {
					if (ExifEngine.DEBUG) {
						e.printStackTrace();
					}
				}
				break;
			case ExifEngine.EXIF_TAG_GPS_DATE_STAMP & ExifEngine.MASK_TAG_ID:
				// exif date stamp:yyyy:MM:dd
				result = tryFormatExifTime(//
						kvPair.get(ATTR_DATESTAMP),//
						DEF_EXIF_DATESTMAP_FORMAT,//
						DATE_STAMP_FORMAT, //
						locale);
				break;

		}
		return result;
	}

	private String tryFormatExifTime(String inTime, String inFormat, String outFormat, Locale locale) {
		if (TextUtils.isEmpty(inTime) || TextUtils.isEmpty(inFormat) || TextUtils.isEmpty(outFormat)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(inFormat, locale);
		try {
			Date date = sdf.parse(inTime);
			if (date == null) {
				return null;
			}
			String fmt = DateFormat.getBestDateTimePattern(locale, outFormat);
			if (fmt == null) {
				return null;
			}
			sdf.applyPattern(fmt);
			return sdf.format(date);
		} catch (Exception e) {
			if (ExifEngine.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
