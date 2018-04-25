#include <config.h>

#include <exif-entry-patch.h>

#include <libexif/exif-entry.h>
#include <libexif/exif-ifd.h>
#include <libexif/exif-utils.h>

#include <stdio.h>
#include <string.h>
#include <time.h>

int exif_entry_patch_initialize(ExifEntry *e, ExifTag tag, ExifMem *mem) {
	if (!e || !e->parent || !mem) {
		return 0;
	}
	if (exif_content_get_ifd(e->parent) != EXIF_IFD_GPS) {
		return 0;
	}
	switch (tag) {
	/* RATIONAL*/
	case EXIF_TAG_GPS_TIME_STAMP:
	case EXIF_TAG_GPS_LATITUDE:
	case EXIF_TAG_GPS_LONGITUDE:
	case EXIF_TAG_GPS_ALTITUDE:
		e->components = 1;
		e->format = EXIF_FORMAT_RATIONAL;
		e->size = exif_format_get_size(e->format) * e->components;
		e->data = exif_mem_alloc(mem, e->size);
		break;
		/*BYTE*/
	case EXIF_TAG_GPS_ALTITUDE_REF:
		e->components = 1;
		e->format = EXIF_FORMAT_SHORT;
		e->size = exif_format_get_size(e->format) * e->components;
		e->data = exif_mem_alloc(mem, e->size);
		break;
		/*UNDEFINED*/
	case EXIF_TAG_GPS_PROCESSING_METHOD:
		e->components = 2;
//		e->format = EXIF_FORMAT_UNDEFINED;
		e->format = EXIF_FORMAT_ASCII;
		e->size = exif_format_get_size(e->format) * e->components;
		e->data = exif_mem_alloc(mem, e->size);
		break;
		/* ASCII, 11 components */
	case EXIF_TAG_GPS_DATE_STAMP: {
		//The format is "YYYY:MM:DD"
		time_t t;
#ifdef HAVE_LOCALTIME_R
		struct tm tms;
#endif
		struct tm *tm;
		t = time(NULL);
#ifdef HAVE_LOCALTIME_R
		tm = localtime_r(&t, &tms);
#else
		tm = localtime(&t);
#endif
		e->components = 11;
		e->format = EXIF_FORMAT_ASCII;
		e->size = exif_format_get_size(e->format) * e->components;
		e->data = exif_mem_alloc(mem, e->size);
		if (!e->data)
			break;
		snprintf((char *) e->data, e->size, "%04i:%02i:%02i",
				tm->tm_year + 1900, tm->tm_mon + 1, tm->tm_mday);
		break;
	}
		/*ASCII*/
	case EXIF_TAG_GPS_LATITUDE_REF:
	case EXIF_TAG_GPS_LONGITUDE_REF:
		e->components = 2;
		e->format = EXIF_FORMAT_ASCII;
		e->size = exif_format_get_size(e->format) * e->components;
		e->data = exif_mem_alloc(mem, e->size);
		break;
	default:
		return 0;
	}
	return 1;
}
