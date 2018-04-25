/* exif-mnote-data-htc.c
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
#include "exif-mnote-data-htc.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include <libexif/exif-byte-order.h>
#include <libexif/exif-utils.h>
#include <libexif/exif-data.h>

#define DEBUG

typedef enum{
    /* "htc"begin with htcmakernote */
    HTC_MNOTE_OFFSET_HTC = 4,
    /* "cameraid_" begin with htcmakernote */
    HTC_MNOTE_OFFSET_CAMERAID = 10
} HtcMakerNoteOffset;

static ExifByteOrder global_order =  EXIF_BYTE_ORDER_INTEL;

static int global_offset = HTC_MNOTE_OFFSET_HTC ;

static void
exif_mnote_data_htc_clear (ExifMnoteDataHtc *n)
{
	ExifMnoteData *d = (ExifMnoteData *) n;
	unsigned int i;

	if (!n) return;

	if (n->entries) {
		for (i = 0; i < n->count; i++)
			if (n->entries[i].data) {
				exif_mem_free (d->mem, n->entries[i].data);
				n->entries[i].data = NULL;
			}
		exif_mem_free (d->mem, n->entries);
		n->entries = NULL;
		n->count = 0;
	}
}

static void
exif_mnote_data_htc_free (ExifMnoteData *n)
{
	if (!n) return;

	exif_mnote_data_htc_clear ((ExifMnoteDataHtc *) n);
}

static void
exif_mnote_data_htc_get_tags (ExifMnoteDataHtc *dc, unsigned int n,
		unsigned int *m, unsigned int *s)
{
	unsigned int from = 0, to;

	if (!dc || !m) return;
	for (*m = 0; *m < dc->count; (*m)++) {
		to = from + mnote_htc_entry_count_values (&dc->entries[*m]);
		if (to > n) {
			if (s) *s = n - from;
			break;
		}
		from = to;
	}
}

static char *
exif_mnote_data_htc_get_value (ExifMnoteData *note, unsigned int n, char *val, unsigned int maxlen)
{
	ExifMnoteDataHtc *dc = (ExifMnoteDataHtc *) note;
	unsigned int m, s;

	if (!dc) return NULL;
	exif_mnote_data_htc_get_tags (dc, n, &m, &s);
	if (m >= dc->count) return NULL;
	return mnote_htc_entry_get_value (&dc->entries[m], s, val, maxlen);
}

static void
exif_mnote_data_htc_set_byte_order (ExifMnoteData *d, ExifByteOrder o)
{
	ExifByteOrder o_orig;
	ExifMnoteDataHtc *n = (ExifMnoteDataHtc *) d;
	unsigned int i;

	if (!n) return;

	o_orig = n->order;
	n->order = global_order;
	for (i = 0; i < n->count; i++) {
		n->entries[i].order = global_order;
		exif_array_set_byte_order (n->entries[i].format, n->entries[i].data,
				n->entries[i].components, o_orig, global_order);
	}
}

static void
exif_mnote_data_htc_set_offset (ExifMnoteData *n, unsigned int o)
{
	if (n) ((ExifMnoteDataHtc *) n)->offset = o;
}

static void
exif_mnote_data_htc_save (ExifMnoteData *ne, 
	unsigned char **buf, unsigned int *buf_size)
{
	ExifMnoteDataHtc *n = (ExifMnoteDataHtc *) ne;
	size_t i, o, s, doff;
	unsigned char *t;
	size_t ts;

	if (!n || !buf || !buf_size) return;

	/*
	 * Allocate enough memory for all entries and the number
	 * of entries.
	 */
	*buf_size = 16 + n->count * 16 + 4*3; //4*3 is the focus area mem, but ,calibration is not right 
	*buf = exif_mem_alloc (ne->mem, sizeof (char) * *buf_size);
	if (!*buf) {
		EXIF_LOG_NO_MEMORY(ne->log, "ExifMnoteHtc", *buf_size);
		return;
	}

	/* Save the htc number of entries */
	exif_set_long(*buf + 0, n->order, (ExifLong) HTC_MARKERNOTE_TAG_NUM);
	exif_set_long(*buf + 4, n->order, (ExifLong) 1);
	exif_set_long(*buf + 8, n->order, (ExifLong) 1);
	exif_set_long(*buf + 12, n->order, (ExifLong) n->count);
	
	o =16;

	/* Save each entry */
	for (i = 0; i < n->count; i++) {

		exif_set_long (*buf + o + 0, n->order, (ExifLong) n->entries[i].tag);
		exif_set_long (*buf + o + 4, n->order, (ExifLong) n->entries[i].format);
		exif_set_long  (*buf + o + 8, n->order,
				n->entries[i].components);
		o += 12;
                
		if(n->entries[i].tag == HTC_MARKERNOTE_TAG_CALIBRATION)
		{
		    s = n->entries[i].components;
		}else{
                    s = exif_format_get_size (n->entries[i].format) * n->entries[i].components;
		}

		if (s > 65536) {
			/* Corrupt data: EXIF data size is limited to the
			 * maximum size of a JPEG segment (64 kb).
			 */
			continue;
		}
		if (s > 4 && n->entries[i].tag != HTC_MARKERNOTE_TAG_FOCUS_AREA) {
			ts = *buf_size + s;

			/* Ensure even offsets. Set padding bytes to 0. */
			if (s & 1) ts += 1;
			t = exif_mem_realloc (ne->mem, *buf,
						 sizeof (char) * ts);
			if (!t) {
				EXIF_LOG_NO_MEMORY(ne->log, "ExifMnoteHtc", ts);
				return;
			}
			*buf = t;
			*buf_size = ts;
			doff = *buf_size - s;
			if (s & 1) { doff--; *(*buf + *buf_size - 1) = '\0'; }
			exif_set_long (*buf + o, n->order, n->offset + doff);
		} else
			doff = o;

		/*
		 * Write the data. Fill unneeded bytes with 0. Do not
		 * crash if data is NULL.
		 */
		if (!n->entries[i].data) memset (*buf + doff, 0, s);
		else memcpy (*buf + doff, n->entries[i].data, s);
		if (s < 4 && n->entries[i].tag != HTC_MARKERNOTE_TAG_CALIBRATION) memset (*buf + doff + s, 0, (4 - s));

		o += s;
	}
}

/* XXX
 * FIXME: exif_mnote_data_htc_load() may fail and there is no
 *        semantics to express that.
 *        See bug #1054323 for details, especially the comment by liblit
 *        after it has supposedly been fixed:
 *
 *        https://sourceforge.net/tracker/?func=detail&aid=1054323&group_id=12272&atid=112272
 *        Unfortunately, the "return" statements aren't commented at
 *        all, so it isn't trivial to find out what is a normal
 *        return, and what is a reaction to an error condition.
 */

static void
exif_mnote_data_htc_load (ExifMnoteData *ne,
	const unsigned char *buf, unsigned int buf_size)
{
	ExifMnoteDataHtc *n = (ExifMnoteDataHtc *) ne;
	ExifLong c, tagnum, tagnum_format, tagnum_components;
	size_t i, tcount, o, datao, byte_count;

	if (!n || !buf || !buf_size) {
		exif_log (ne->log, EXIF_LOG_CODE_CORRUPT_DATA,
			  "ExifMnoteHtc", "Short MakerNote");
		return;
	}

	datao = 6+4+n->offset;
        tagnum = exif_get_long(buf + datao,n->order);
	tagnum_format = exif_get_long(buf + datao +4,n->order);
	tagnum_components = exif_get_long(buf + datao + 8,n->order);

	datao +=12;
	if ((datao + 2 < datao) || (datao + 2 < 2) || (datao + 2 > buf_size)) {
		exif_log (ne->log, EXIF_LOG_CODE_CORRUPT_DATA,
			  "ExifMnoteHtc", "Short MakerNote");
		return;
	}

	/* Read the number of tags */

	if(tagnum == HTC_MARKERNOTE_TAG_NUM){
	    c = exif_get_long (buf + datao, n->order);
	}else
	    return;

	byte_count = exif_format_get_size(tagnum_format) * tagnum_components;
        datao += byte_count;

	/* Remove any old entries */
	exif_mnote_data_htc_clear (n);

	/* Reserve enough space for all the possible MakerNote tags */
	n->entries = exif_mem_alloc (ne->mem, sizeof (MnoteHtcEntry) * c);
	if (!n->entries) {
		EXIF_LOG_NO_MEMORY(ne->log, "ExifMnoteHtc", sizeof (MnoteHtcEntry) * c);
		return;
	}

	/* Parse the entries */
	tcount = 0;
	for (i = c, o = datao; i; --i) {
		size_t s;
		if ((o + 12 < o) || (o + 12 < 12) || (o + 12 > buf_size)) {
			exif_log (ne->log, EXIF_LOG_CODE_CORRUPT_DATA,
				"ExifMnoteHtc", "Short MakerNote");
			break;
	        }

		n->entries[tcount].tag        = exif_get_long (buf + o, n->order);
		n->entries[tcount].format     = exif_get_long (buf + o + 4, n->order);
		n->entries[tcount].components = exif_get_long (buf + o + 8, n->order);
		n->entries[tcount].order      = n->order;

		exif_log (ne->log, EXIF_LOG_CODE_DEBUG, "ExifMnoteHtc",
			"Loading entry 0x%x ('%s')...", n->entries[tcount].tag,
			 mnote_htc_tag_get_name (n->entries[tcount].tag));

		/*
		 * Size? If bigger than 4 bytes, the actual data is not
		 * in the entry but somewhere else (offset).
		 */
		if(n->entries[tcount].tag == HTC_MARKERNOTE_TAG_CALIBRATION)
		{
		    s = n->entries[tcount].components;
		}else{
                    s = exif_format_get_size (n->entries[tcount].format) * n->entries[tcount].components;
		}

		n->entries[tcount].size = s;
		if (!s) {
			exif_log (ne->log, EXIF_LOG_CODE_CORRUPT_DATA,
				  "ExifMnoteHtc",
				  "Invalid zero-length tag size");
			continue;

		} else {
			size_t dataofs = o + 12;
			//if (s > 4 &&(HTC_MARKERNOTE_TAG_FOCUS_AREA != n->entries[tcount].tag)) dataofs = exif_get_long (buf + dataofs, n->order) + 6;
			if ((dataofs + s < s) || (dataofs + s < dataofs) || (dataofs + s > buf_size)) {
				exif_log (ne->log, EXIF_LOG_CODE_DEBUG,
					"ExifMnoteHtc",
					"Tag data past end of buffer (%u > %u)",
					dataofs + s, buf_size);
				continue;
			}

			n->entries[tcount].data = exif_mem_alloc (ne->mem, s);
			if (!n->entries[tcount].data) {
				EXIF_LOG_NO_MEMORY(ne->log, "ExifMnoteHtc", s);
				continue;
			}
			memcpy (n->entries[tcount].data, buf + dataofs, s);
		}

		/* Tag was successfully parsed */
		++tcount;
		o +=s;
	}
	/* Store the count of successfully parsed tags */
	n->count = tcount;
}

static unsigned int
exif_mnote_data_htc_count (ExifMnoteData *n)
{
	ExifMnoteDataHtc *dc = (ExifMnoteDataHtc *) n;
	unsigned int i, c;

	for (i = c = 0; dc && (i < dc->count); i++)
		c += mnote_htc_entry_count_values (&dc->entries[i]);
	return c;
}

static unsigned int
exif_mnote_data_htc_get_id (ExifMnoteData *d, unsigned int i)
{
	ExifMnoteDataHtc *dc = (ExifMnoteDataHtc *) d;
	unsigned int m;

	if (!dc) return 0;
	exif_mnote_data_htc_get_tags (dc, i, &m, NULL);
	if (m >= dc->count) return 0;
	return dc->entries[m].tag;
}

static const char *
exif_mnote_data_htc_get_name (ExifMnoteData *note, unsigned int i)
{
	ExifMnoteDataHtc *dc = (ExifMnoteDataHtc *) note;
	unsigned int m, s;

	if (!dc) return NULL;
	exif_mnote_data_htc_get_tags (dc, i, &m, &s);
	if (m >= dc->count) return NULL;
	return mnote_htc_tag_get_name_sub (dc->entries[m].tag, s, dc->options);
}

static const char *
exif_mnote_data_htc_get_title (ExifMnoteData *note, unsigned int i)
{
	ExifMnoteDataHtc *dc = (ExifMnoteDataHtc *) note;
	unsigned int m, s;

	if (!dc) return NULL;
	exif_mnote_data_htc_get_tags (dc, i, &m, &s);
	if (m >= dc->count) return NULL;
	return mnote_htc_tag_get_title_sub (dc->entries[m].tag, s, dc->options);
}

static const char *
exif_mnote_data_htc_get_description (ExifMnoteData *note, unsigned int i)
{
	ExifMnoteDataHtc *dc = (ExifMnoteDataHtc *) note;
	unsigned int m;

	if (!dc) return NULL;
	exif_mnote_data_htc_get_tags (dc, i, &m, NULL);
	if (m >= dc->count) return NULL;
	return mnote_htc_tag_get_description (dc->entries[m].tag);
}

static void exif_mnote_data_htc_set_order(const ExifEntry *e){

    if((global_offset == HTC_MNOTE_OFFSET_HTC) && (e->data)[3] == 0x49 ){
	    global_order =  EXIF_BYTE_ORDER_INTEL;
    }else if((global_offset == HTC_MNOTE_OFFSET_HTC) && (e->data)[3] == 0x4D ){
	    global_order =  EXIF_BYTE_ORDER_MOTOROLA;
    }else if((global_offset == HTC_MNOTE_OFFSET_CAMERAID)&&((e->data[9])==0x49)){
	    global_order =  EXIF_BYTE_ORDER_INTEL;
    }else if((global_offset == HTC_MNOTE_OFFSET_CAMERAID)&&((e->data[9])==0x4D)){
	    global_order =  EXIF_BYTE_ORDER_MOTOROLA;
    }
}

int
exif_mnote_data_htc_identify (const ExifData *ed, const ExifEntry *e)
{
	if(((e->size)>=4) && !memcmp(e->data, "htc",3)){
	    global_offset =  HTC_MNOTE_OFFSET_HTC;
	    exif_mnote_data_htc_set_order(e);
	    return 1;
	}else if((e->size >= 10) && !memcmp(e->data,"cameraid_",9)){
	    global_offset = HTC_MNOTE_OFFSET_CAMERAID;
	    exif_mnote_data_htc_set_order(e);
	    return 1;
	}
	return 0;
}

ExifMnoteData *
exif_mnote_data_htc_new (ExifMem *mem)
{
	ExifMnoteData *d;
	ExifMnoteDataHtc *dc;

	if (!mem) return NULL;

	d = exif_mem_alloc (mem, sizeof (ExifMnoteDataHtc));
	if (!d)
		return NULL;

	exif_mnote_data_construct (d, mem);

	/* Set up function pointers */
	d->methods.free            = exif_mnote_data_htc_free;
	d->methods.set_byte_order  = exif_mnote_data_htc_set_byte_order;
	d->methods.set_offset      = exif_mnote_data_htc_set_offset;
	d->methods.load            = exif_mnote_data_htc_load;
	d->methods.save            = exif_mnote_data_htc_save;
	d->methods.count           = exif_mnote_data_htc_count;
	d->methods.get_id          = exif_mnote_data_htc_get_id;
	d->methods.get_name        = exif_mnote_data_htc_get_name;
	d->methods.get_title       = exif_mnote_data_htc_get_title;
	d->methods.get_description = exif_mnote_data_htc_get_description;
	d->methods.get_value       = exif_mnote_data_htc_get_value;

	dc = (ExifMnoteDataHtc*)d;
	//dc->options = o;
	return d;
}
