/* mnote-htc-tag.h
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

#ifndef __MNOTE_HTC_TAG_H__
#define __MNOTE_HTC_TAG_H__

#include <libexif/exif-data.h>

#ifdef __cplusplus
extern "C" {
#endif /* __cplusplus */

enum _MnoteHtcTag {
    HTC_MARKERNOTE_TAG_NUM          = 0x0000,
	HTC_MARKERNOTE_TAG_DUAL_CAM     = 0x0001,
	HTC_MARKERNOTE_TAG_VCM          = 0x0002,
	HTC_MARKERNOTE_TAG_FOCUS_AREA   = 0x0003,

	HTC_MARKERNOTE_TAG_CALIBRATION  = 0x0006,
	HTC_MARKERNOTE_TAG_EX_FLAG      = 0x0007,
	HTC_MARKERNOTE_TAG_CAMERAID     = 0x0010,

};
typedef enum _MnoteHtcTag MnoteHtcTag;

const char *mnote_htc_tag_get_name        (MnoteHtcTag);
const char *mnote_htc_tag_get_name_sub    (MnoteHtcTag, unsigned int, ExifDataOption);
const char *mnote_htc_tag_get_title       (MnoteHtcTag);
const char *mnote_htc_tag_get_title_sub   (MnoteHtcTag, unsigned int, ExifDataOption);
const char *mnote_htc_tag_get_description (MnoteHtcTag);

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* __MNOTE_HTC_TAG_H__ */
