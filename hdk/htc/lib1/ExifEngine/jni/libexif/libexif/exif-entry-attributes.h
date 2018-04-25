/*
 * exif-entry-attributes.h
 *
 *  Created on: 2014-12-25
 *      Author: shenkaige
 */

#ifndef EXIF_ENTRY_ATTRIBUTES_H_
#define EXIF_ENTRY_ATTRIBUTES_H_

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

#define KEY_VALUE_SPLITOR "\n"
#define KEY_VALUE_LINKER "="

#define ATTR_EXPOSURE_VALUE "exposure_value"
#define ATTR_PHOTO_PHOTOGRAPHER "photo_photographer"
#define ATTR_PHOTO_EDITOR "photo_editor"
#define ATTR_APERTURE_VALUE "aperture_value"
#define ATTR_FOCAL_LENGTH "focal_length"
#define ATTR_FOCAL_LENGTH_35_EQUIVALENT "focal_length_35_equivalent"
#define ATTR_SUBJECT_DISTANCE "subject_distance"
#define ATTR_EXPOSURE_TIME "exposure_time"
#define ATTR_SHUTTER_SPEED "shutter_speed"
#define ATTR_BRIGHTNESS_VALUE "brightness_value"
#define ATTR_SUBJECT_AREA_X "subject_area_x"
#define ATTR_SUBJECT_AREA_Y "subject_area_y"
#define ATTR_SUBJECT_AREA_RECTANGLE "subject_area_rectangle"
#define ATTR_SUBJECT_AREA_WIDTH "subject_area_width"
#define ATTR_SUBJECT_AREA_HEIGHT "subject_area_height"
#define ATTR_TIMESTAMP "time_stamp"
#define ATTR_DATESTAMP "date_stamp"
#define ATTR_DATE_HOUR "date_hour"
#define ATTR_DATE_MINUTE "date_minute"
#define ATTR_DATE_SECOND "date_second"
#define ATTR_DATE_SECOND_FLOAT "date_second"//00.00

// returns new buffer length
int addKeyValueString(char** buf, int bufLen, const char* key,
		const char* value);

// returns new buffer length
int addKeyValueInt(char** buf, int bufLen, const char* key, int value);

// returns new buffer length
int addKeyValueDouble(char** buf, int bufLen, const char* key, double value,
		const char* format);

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* EXIF_ENTRY_ATTRIBUTES_H_ */
