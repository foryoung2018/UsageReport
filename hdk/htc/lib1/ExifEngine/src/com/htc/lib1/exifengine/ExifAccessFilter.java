package com.htc.lib1.exifengine;

import com.htc.lib1.exifengine.ExifEngine.AttrsMap;

import java.util.Locale;

/**
 * Exif String value format filter
 *
 * @author amt_masd_kg_shen@htc.com
 */
interface ExifAccessFilter {
	static final String DEF_EXIF_TIMESTMAP_FORMAT = "yyyy:MM:dd HH:mm:ss";
	static final String DEF_EXIF_DATESTMAP_FORMAT = "yyyy:MM:dd";
	//
	static final String ATTR_EXPOSURE_VALUE = "exposure_value";
	static final String ATTR_PHOTO_PHOTOGRAPHER = "photo_photographer";
	static final String ATTR_PHOTO_EDITOR = "photo_editor";
	static final String ATTR_APERTURE_VALUE = "aperture_value";
	static final String ATTR_FOCAL_LENGTH = "focal_length";
	static final String ATTR_FOCAL_LENGTH_35_EQUIVALENT = "focal_length_35_equivalent";
	static final String ATTR_SUBJECT_DISTANCE = "subject_distance";
	static final String ATTR_EXPOSURE_TIME = "exposure_time";
	static final String ATTR_SHUTTER_SPEED = "shutter_speed";
	static final String ATTR_BRIGHTNESS_VALUE = "brightness_value";
	static final String ATTR_SUBJECT_AREA_X = "subject_area_x";
	static final String ATTR_SUBJECT_AREA_Y = "subject_area_y";
	static final String ATTR_SUBJECT_AREA_RECTANGLE = "subject_area_rectangle";
	static final String ATTR_SUBJECT_AREA_WIDTH = "subject_area_width";
	static final String ATTR_SUBJECT_AREA_HEIGHT = "subject_area_height";
	static final String ATTR_TIMESTAMP = "time_stamp";
	static final String ATTR_DATESTAMP = "date_stamp";
	static final String ATTR_DATE_HOUR = "date_hour";
	static final String ATTR_DATE_MINUTE = "date_minute";
	static final String ATTR_DATE_SECOND = "date_second";
	static final String ATTR_DATE_SECOND_FLOAT = "date_second";// 00.00

	public String onFilterExif(int exifTag, String defFormatResult,
							   AttrsMap kvPair, Locale locale);
}