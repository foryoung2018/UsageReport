/* mnote-htc-tag.c
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

#include <config.h>
#include "mnote-htc-tag.h"

#include <stdlib.h>

#include <libexif/i18n.h>

static const struct {
	MnoteHtcTag tag;
	const char *name;
	const char *title;
	const char *description;
} table[] = {
#ifndef NO_VERBOSE_TAG_STRINGS
	{HTC_MARKERNOTE_TAG_NUM, "MakeNoteTagNum", N_("MakeNote Tag Num"), ""},
	{HTC_MARKERNOTE_TAG_DUAL_CAM, "DualCam", N_("Dual Cam"), ""},
	{HTC_MARKERNOTE_TAG_VCM, "Vcm", N_("Vcm"), ""},
	{HTC_MARKERNOTE_TAG_FOCUS_AREA, "FocusArea", N_("Focus Area"), ""},
	{HTC_MARKERNOTE_TAG_CALIBRATION, "Calibration", N_("Calibration"), ""},
	{HTC_MARKERNOTE_TAG_EX_FLAG, "DuolensExFlag", N_("Duolens ExFlag"), ""},
	{HTC_MARKERNOTE_TAG_CAMERAID, "CameraID", N_("CameraID"),""},

#endif
	{0, NULL, NULL, NULL}
};

static const struct {
	MnoteHtcTag tag;
	unsigned int subtag;
	const char *name;
} table_sub[] = {
#ifndef NO_VERBOSE_TAG_STRINGS
	{HTC_MARKERNOTE_TAG_FOCUS_AREA,  0, N_("FocusArea.x1")},
	{HTC_MARKERNOTE_TAG_FOCUS_AREA,  1, N_("FocusArea.y1")},
	{HTC_MARKERNOTE_TAG_FOCUS_AREA,  2, N_("FocusArea.x2")},
	{HTC_MARKERNOTE_TAG_FOCUS_AREA,  3, N_("FocusArea.y2")},
#endif
	{0, 0, NULL}
};

const char *
mnote_htc_tag_get_name (MnoteHtcTag t)
{
	unsigned int i;

	for (i = 0; i < sizeof (table) / sizeof (table[0]); i++)
		if (table[i].tag == t) return table[i].name; /* do not translate */
	return NULL;
}

const char *
mnote_htc_tag_get_name_sub (MnoteHtcTag t, unsigned int s, ExifDataOption o)
{
	unsigned int i;
	int tag_found = 0;

	for (i = 0; i < sizeof (table_sub) / sizeof (table_sub[0]); i++) {
		if (table_sub[i].tag == t) {
			if (table_sub[i].subtag == s)
				return table_sub[i].name;
			tag_found = 1;
		}
	}
	if (!tag_found || !(o & EXIF_DATA_OPTION_IGNORE_UNKNOWN_TAGS))
		return mnote_htc_tag_get_name (t);
	else
		return NULL;
}

const char *
mnote_htc_tag_get_title (MnoteHtcTag t)
{
	unsigned int i;

	bindtextdomain (GETTEXT_PACKAGE, LOCALEDIR); 
	for (i = 0; i < sizeof (table) / sizeof (table[0]); i++)
		if (table[i].tag == t) return (_(table[i].title));
	return NULL;
}

const char *
mnote_htc_tag_get_title_sub (MnoteHtcTag t, unsigned int s, ExifDataOption o)
{
	unsigned int i;
	int tag_found = 0;

	for (i = 0; i < sizeof (table_sub) / sizeof (table_sub[0]); i++) {
		if (table_sub[i].tag == t) {
			if (table_sub[i].subtag == s)
				return _(table_sub[i].name);
			tag_found = 1;
		}
	}
	if (!tag_found || !(o & EXIF_DATA_OPTION_IGNORE_UNKNOWN_TAGS))
		return mnote_htc_tag_get_title (t);
	else
		return NULL;
}

const char *
mnote_htc_tag_get_description (MnoteHtcTag t)
{
	unsigned int i;

	for (i = 0; i < sizeof (table) / sizeof (table[0]); i++)
		if (table[i].tag == t) {
			if (!table[i].description || !*table[i].description)
				return "";
			bindtextdomain (GETTEXT_PACKAGE, LOCALEDIR);
			return _(table[i].description);
		}
	return NULL;
}
