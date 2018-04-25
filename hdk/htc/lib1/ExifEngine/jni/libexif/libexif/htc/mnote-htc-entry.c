/* mnote-htc-entry.c
 *
 * Copyright (c) 2014 Astley Chen <astley_chen@htc>
 * Copyright (c) 2014 QiQiang Zhong <amt_masd_qq_zhong@htc.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA.
 */

#include "config.h"
#include "mnote-htc-entry.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#include <libexif/exif-format.h>
#include <libexif/exif-utils.h>
#include <libexif/i18n.h>

/* #define DEBUG */

#define CF(format,target,v,maxlen)                              \
{                                                               \
        if (format != target) {                                 \
                snprintf (v, maxlen,                            \
                        _("Invalid format '%s', "               \
                        "expected '%s'."),                      \
                        exif_format_get_name (format),          \
                        exif_format_get_name (target));         \
                break;                                          \
        }                                                       \
}

#define CC(number,target,v,maxlen)                                      \
{                                                                       \
        if (number != target) {                                         \
                snprintf (v, maxlen,                                    \
                        _("Invalid number of components (%i, "          \
                        "expected %i)."), (int) number, (int) target);  \
                break;                                                  \
        }                                                               \
}
#define CC2(number,t1,t2,v,maxlen)                                      \
{                                                                       \
	if ((number != t1) && (number != t2)) {                         \
		snprintf (v, maxlen,                                    \
			_("Invalid number of components (%i, "          \
			"expected %i or %i)."), (int) number,		\
			(int) t1, (int) t2);  				\
		break;                                                  \
	}                                                               \
}

#define UNDEFINED 0xFF
    
static const struct htc_entry_table_t {
  unsigned int subtag;
  ExifLong value;
  const char *name;
} entries_focus_area [] = {
#ifndef NO_VERBOSE_TAG_DATA
	{0, 0, N_("FocusArea.x1")},
	{1, 1, N_("FocusArea.y1")},
	{2, 2, N_("FocusArea.x2")},
	{3, 3, N_("FocusArea.y2")},
#endif
	{0, 0, NULL}
};

static void
htc_search_table_value (const struct htc_entry_table_t table[],
    unsigned int t, ExifLong vl, char *val, unsigned int maxlen)
{
	unsigned int j;

	/* Search the table for the first matching subtag and value. */
	for (j = 0; table[j].name && ((table[j].subtag < t) ||
			((table[j].subtag == t) && table[j].value <= vl)); j++) {
		if ((table[j].subtag == t) && (table[j].value == vl)) {
			break;
		}
	}
	if ((table[j].subtag == t) && (table[j].value == vl) && table[j].name) {
		/* Matching subtag and value found. */
		strncpy (val, _(table[j].name), maxlen);
	} else {
		/* No matching subtag and/or value found. */
		snprintf (val, maxlen, "0x%04x", vl);
	}
}

static void
htc_search_table_bitfield (const struct htc_entry_table_t table[],
    unsigned int t, ExifShort vs, char *val, unsigned int maxlen)
{
	unsigned int j;

	/* Search the table for the first matching subtag. */
	for (j = 0; table[j].name && (table[j].subtag <= t); j++) {
		if (table[j].subtag == t) {
			break;
		}
	}
	if ((table[j].subtag == t) && table[j].name) {
		unsigned int i, bit, lastbit = 0;

		/*
		 * Search the table for the last matching bit, because
		 * that one needs no additional comma appended.
		 */
		for (i = j; table[i].name && (table[i].subtag == t); i++) {
			bit = table[i].value;
			if ((vs >> bit) & 1) {
				lastbit = bit;
			}
		}
		/* Search the table for all matching bits. */
		for (i = j; table[i].name && (table[i].subtag == t); i++) {
			bit = table[i].value;
			if ((vs >> bit) & 1) {
				strncat(val, _(table[i].name), maxlen - strlen (val));
				if (bit != lastbit) 
					strncat (val, _(", "), maxlen - strlen (val));
			}
		}
	} else {
		/* No matching subtag found. */
		snprintf (val, maxlen, "0x%04x", vs);
	}
}

unsigned int
mnote_htc_entry_count_values (const MnoteHtcEntry *entry)
{
	unsigned int  val;

	if (!entry) return 0;

	switch (entry->tag) {
	    case HTC_MARKERNOTE_TAG_NUM:
	    case HTC_MARKERNOTE_TAG_DUAL_CAM:
	    case HTC_MARKERNOTE_TAG_VCM:
	    case HTC_MARKERNOTE_TAG_FOCUS_AREA:
	    case HTC_MARKERNOTE_TAG_CALIBRATION:
	    case HTC_MARKERNOTE_TAG_EX_FLAG:
	    case HTC_MARKERNOTE_TAG_CAMERAID:
		return entry->components;
	default:
		return 1;
	}
}

/*
 * For reference, see Exif 2.1 specification (Appendix C), 
 * or http://en.wikipedia.org/wiki/APEX_system
 */
static double
apex_value_to_aperture (double x)
{
	return pow (2, x / 2.);
}

static double
apex_value_to_shutter_speed(double x)
{
	return 1.0 / pow (2, x);
}

static double
apex_value_to_iso_speed (double x)
{
	return 3.125 * pow (2, x);
}

char *
mnote_htc_entry_get_value (const MnoteHtcEntry *entry, unsigned int t, char *val, unsigned int maxlen)
{
	char buf[128];
	ExifLong vl;
	ExifShort vs, n;
	unsigned char *data;
	double d;

	if (!entry) 
		return NULL;

	data = entry->data;

	memset (val, 0, maxlen);
	maxlen--;

	switch (entry->tag) {

    case HTC_MARKERNOTE_TAG_NUM:
    case HTC_MARKERNOTE_TAG_DUAL_CAM:
    case HTC_MARKERNOTE_TAG_VCM:
    case HTC_MARKERNOTE_TAG_EX_FLAG:
    case HTC_MARKERNOTE_TAG_CAMERAID:
	    CF (entry->format, EXIF_FORMAT_LONG, val, maxlen);
	    CC (entry->components, 1, val, maxlen);
	    vl = exif_get_long (data, entry->order);
	    snprintf (val, maxlen,"%u",vl );
	    printf("mnote-htc-entry.c line 215:The value is :%u\n",vl);//add by qqzhong for log
		break;
    case HTC_MARKERNOTE_TAG_FOCUS_AREA:
	    vl = exif_get_long(data + t*4, entry->order);
		htc_search_table_value(entries_focus_area, t, vl, val, maxlen);
		break;

	default:
#ifdef DEBUG
	  {
		int i;
		if (entry->format == EXIF_FORMAT_SHORT)
		for(i=0;i<entry->components;i++) {
			vs = exif_get_short (data, entry->order);
			data+=2;
			printf ("Value%d=%d\n", i, vs);
		}
		else if (entry->format == EXIF_FORMAT_LONG)
		for(i=0;i<entry->components;i++) {
			vl = exif_get_long (data, entry->order);
			data+=4;
			printf ("Value%d=%d\n", i, vs);
		}
		else if (entry->format == EXIF_FORMAT_ASCII)
		    strncpy (val, data, MIN (entry->size, maxlen));
	  }
#endif
		break;
	}
	return val;
}
