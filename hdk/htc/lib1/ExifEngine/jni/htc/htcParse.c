/**
 *
 * HTC Corporation Proprietary Rights Acknowledgment
 * Copyright (c) 2014 HTC Corporation
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of HTC Corporation
 * ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
 * right to employ this work within the scope of this statement.  Nevertheless, the
 * Authorized User shall not use this work for any purpose other than the purpose
 * agreed by HTC.  Any and all addition or modification to this work shall be
 * unconditionally granted back to HTC and such addition or modification shall be
 * solely owned by HTC.  No right is granted under this statement, including but not
 * limited to, distribution, reproduction, and transmission, except as otherwise
 * provided in this statement.  Any other usage of this work shall be subject to the
 * further written consent of HTC.
 *
 * @file    htcParse.c
 * @desc    a code file for get htc makenote
 * @author    astley_chen@htc amt_masd_qq_zhong@htc.com
 * @history    2014/08/08
 */

#include <stdio.h>
#include <string.h>
#include "htcParse.h"
#include <libexif/htc/mnote-htc-tag.h>

#undef M_SOI
#define M_SOI 0xd8
#undef M_EOI
#define M_EOI 0xd9
#undef M_EXIF
#define M_EXIF 0xe1
#undef M_SOS
#define M_SOS 0xda

typedef unsigned char uchar;

// HTC Panorama+
extern unsigned short checkIsGPano( char const * _xmpData, int const _xmpSize );
// HTC Panorama+

// HTC Format (3DMacro, Bokeh)
extern unsigned short verifyHtcFormat(FILE *infile, short const offset, char const *bytes, short size);
// HTC Format (3DMacro, Bokeh)

/* Remove spaces on the right of the string */
static void trim_spaces(char *buf)
{
    char *s = buf-1;
    for (; *buf; ++buf) {
        if (*buf != ' ')
            s = buf;
    }
    *++s = 0; /* nul terminate the string on the first of the final spaces */
}

/* Get the tag name and contents if the tag exists */
static const char * get_tag_value(ExifData *d, ExifIfd ifd, ExifTag tag,char *val, unsigned int maxlen)
{
    memset(val,0,maxlen);
    /* See if this tag exists */
    ExifEntry *entry = exif_content_get_entry(d->ifd[ifd],tag);
    if (entry) {

        /* Get the contents of the tag in human-readable form */
        exif_entry_get_value(entry, val, maxlen);

        /* Don't bother printing it if it's entirely blank */
       // trim_spaces(val);
    }
    return val;
}

/* Get the given MakerNote tag if it exists */
static const char * get_mnote_tag(ExifData *d, unsigned tag,char *val, unsigned int maxlen)
{
    ExifMnoteData *mn = exif_data_get_mnote_data(d);
    if (mn) {
        int num = exif_mnote_data_count(mn);
        int i;

        /* Loop through all MakerNote tags, searching for the desired one */
        for (i=0; i < num; ++i) {
            if (exif_mnote_data_get_id(mn, i) == tag) {
                if (exif_mnote_data_get_value(mn, i, val, maxlen)) {
                    /* Don't bother printing it if it's entirely blank */
                   // trim_spaces(buf);
                    if (*val) {
                        printf("%s: %s\n", exif_mnote_data_get_title(mn, i),val);
                    }
                }
            }
        }
    }
    return val;
}

int isHtcED(const ExifData *ed){
	if(!ed){
		    return 0;
	}
        ExifEntry *entry;
	entry = exif_content_get_entry(ed->ifd[EXIF_IFD_0],EXIF_TAG_MAKE);

	if(entry){
		char buf[64];
		if(exif_entry_get_value(entry,buf,sizeof(buf))){
		    trim_spaces(buf);
		    if(!strcmp(buf,"Htc"))
			    return 1;
		}
	}
	return 0;
}

int isHtcFile(const char *path){
	int ret = 0;
    ExifData *ed;
    ExifEntry *entry;

    ed = exif_data_new_from_file(path);
    if(!ed){
	    return 0;
    }

    ret = isHtcED(ed);
    /* Free the EXIF data */
    exif_data_unref(ed);
    return ret;
}


int has3DMacroED(int fd,const ExifData *ed){

    int ret=0;
    char comment[2000];
    FILE *file;
    file = fdopen(fd,"rb");

    if(!ed){
	    ret = 0;
	    goto error_;
    }

    get_tag_value(ed,EXIF_IFD_EXIF,EXIF_TAG_USER_COMMENT,comment,sizeof(comment));

    if(comment[0] == 0)
	    get_tag_value(ed,EXIF_IFD_0,EXIF_TAG_USER_COMMENT,comment,sizeof(comment));

    if((comment[0]==0x68)&&(comment[1]==0x54)&&(comment[2]==0x43)){
	    if((comment[3]==0x5F)&&(comment[4]==0x33)&&(comment[5]==0x44)){
	        const char bytes[] = {0x68,0x74,0x63,0x00,0x74,0x77,0x74,0x70};
	        ret= verifyHtcFormat(file,-1,bytes,sizeof(bytes));
	    }
    }

    fclose(file);
    return ret;

    error_:
    fclose(file);
    return ret;
}


// The function is used to test whether htc's photo is 3DMacro
// if is ,return 1,else return -1 or 0
int has3DMacroFile(const char *path){

	int ret=0;
    ExifData *ed;
    char comment[2000];
    FILE *file;
    file = fopen(path,"rb");

    ed = exif_data_new_from_file(path);
    if(!ed){
	    ret= 0;
	    goto error_;
    }

    get_tag_value(ed,EXIF_IFD_EXIF,EXIF_TAG_USER_COMMENT,comment,sizeof(comment));

    if(comment[0] == 0)
	    get_tag_value(ed,EXIF_IFD_0,EXIF_TAG_USER_COMMENT,comment,sizeof(comment));

    /* Free the EXIF data */
    exif_data_unref(ed);

    if((comment[0]==0x68)&&(comment[1]==0x54)&&(comment[2]==0x43)){
	    if((comment[3]==0x5F)&&(comment[4]==0x33)&&(comment[5]==0x44)){
	        const char bytes[] = {0x68,0x74,0x63,0x00,0x74,0x77,0x74,0x70};
	        ret = verifyHtcFormat(file,-1,bytes,sizeof(bytes));
	    }
    }

    fclose(file);
    return ret;

    error_:
    fclose(file);
    return ret;
}

//hasBoken fun is used to check the htc's photo whether has Bokeh, if return 1, or others.
int hasBokehFile(const char *path){

	int ret = 0;
    ExifData *ed;
    int dualCam =-1;
    FILE *file;
    char val[200];

    file = fopen(path,"rb");
    ed = exif_data_new_from_file(path);
    if(!ed){
	    ret = 0;
	    goto error_;
    }

    get_mnote_tag(ed,HTC_MARKERNOTE_TAG_DUAL_CAM,val,sizeof(val));

    /* Free the EXIF data */
    exif_data_unref(ed);

    /* Don't bother printing it if it's entirely blank */
    trim_spaces(val);
    if(val[0] != 0)
	dualCam = atoi(val);

    if(dualCam ==300){
	    const char bytes[] = {0x55,0x46,0x43,0x53};
	    ret = verifyHtcFormat(file,-1,bytes,sizeof(bytes));
    }

    fclose(file);
    return ret;

    error_:
    fclose(file);
    return ret;
}

int hasBokehED(int fd,const ExifData *ed){

	int ret = 0;
    int dualCam =-1;
    FILE *file;
    char val[200];

    file = fdopen(fd,"rb");
    if(!ed){
	    ret = 0;
	    goto error_;
    }

    get_mnote_tag(ed,HTC_MARKERNOTE_TAG_DUAL_CAM,val,sizeof(val));

    /* Don't bother printing it if it's entirely blank */
    trim_spaces(val);
    if(val[0] != 0)
	dualCam = atoi(val);

    if(dualCam ==300){
	    const char bytes[] = {0x55,0x46,0x43,0x53};
	    ret = verifyHtcFormat(file,-1,bytes,sizeof(bytes));
    }

    fclose(file);
    return ret;

    error_:
    fclose(file);
    return ret;

}

int hasFaceED(const ExifData *ed){

	int ret = 0;
    int extra_flag = -1;
    char val[200];

    if(!ed){
	    return 0;
    }

    get_mnote_tag(ed,HTC_MARKERNOTE_TAG_EX_FLAG,val,sizeof(val));

    /* Don't bother printing it if it's entirely blank */
    trim_spaces(val);
    if(val[0] != 0)
	    extra_flag = atoi(val);

    if((extra_flag & 0x4) == 0x4)
	    return 1;

    return 0;
}

//hasFace fun is used to check the htc's photo whether has Face, if return 1, or others.
int hasFaceFile(const char *path){

    int ret =0;
    ExifData *ed;
    int extra_flag = -1;
    char val[200];

    ed = exif_data_new_from_file(path);
    if(!ed){
	    return 0;
    }

    ret = hasFaceED(ed);

    /* Free the EXIF data */
    exif_data_unref(ed);
    return ret;
}

int hasDepthED(const ExifData *ed){
    int extra_flag = -1;
    char val[200];

    if(!ed){
	    return 0;
    }

    get_mnote_tag(ed,HTC_MARKERNOTE_TAG_EX_FLAG,val,sizeof(val));

    /* Don't bother printing it if it's entirely blank */
    trim_spaces(val);
    if(val[0] != 0)
	    extra_flag = atoi(val);

    if((extra_flag & 0x2) == 0x2)
	    return 1;

    return 0;
}

//hasDepth fun is used to check the htc's photo whether has depth, if return 1, or others.
int hasDepthFile(const char *path){

    int ret = 0;
    ExifData *ed;
    int extra_flag = -1;
    char val[200];

    ed = exif_data_new_from_file(path);
    if(!ed){
	    return 0;
    }

    ret = hasDepthED(ed);

    /* Free the EXIF data */
    exif_data_unref(ed);

    return ret;
}


int hasIsProcessedED(const ExifData *ed){
    int dualcam = -1;
    char val[200];
    int isface = -1;
    int isdepth = -1;

    if(!ed){
	    return 0;
    }

    isface = hasFaceED(ed);
    isdepth = hasDepthED(ed);

    get_mnote_tag(ed,HTC_MARKERNOTE_TAG_EX_FLAG,val,sizeof(val));

    /* Don't bother printing it if it's entirely blank */
    trim_spaces(val);
    if(val[0] != 0)
	dualcam = atoi(val);

    if((isface == 1) || (isdepth ==1) || (dualcam !=0))
	    return 1;

    return 0;
}

int hasIsProcessedFile(const char *path){

    int ret = 0;
    ExifData *ed;
    int dualcam = -1;
    char val[200];
    int isface = -1;
    int isdepth = -1;

    ed = exif_data_new_from_file(path);
    if(!ed){
	    return 0;
    }

    ret = hasIsProcessedED(ed);

    /* Free the EXIF data */
    exif_data_unref(ed);
    return ret;
}


int hasPanoramaFile(const char *path){

	int ret = -1;
    FILE * file;
    file = fopen(path,"rb");

    int a;
    a = fgetc(file);
    if(a != 0xff || fgetc(file) != 0xD8){
	    goto error_;
    }
    for(;;){
	    int itemlen;
	    int marker = 0;
	    int ll,lh,got;
	    uchar * Data;

	    for(a=0;a<=16;a++){
	        marker = fgetc(file);
	        if(marker != 0xff)
		        break;
	        if(a>=16){
		        goto error_;
	        }
	    }

	    //Read the lenght of the section
	    lh = fgetc(file);
	    ll = fgetc(file);

	    itemlen = (lh <<8) | ll;

	    if(itemlen < 2){
	       goto error_;
	    }

	    Data = (uchar *)malloc(itemlen);
	    if(Data == NULL){
	    	goto error_;
	    }

	    //Store first two pre-read bytes
	    Data[0] = (uchar)lh;
	    Data[1] = (uchar)ll;

	    got = fread(Data+2,1,itemlen-2,file);
	    if(got != itemlen -2){
	        free(Data);
	        goto error_;
	    }

	    switch(marker){
	        case M_EXIF:
		        if(memcmp(Data+2,"Exif",4)==0){
		            free(Data);
		            ret= 0;
			        fclose(file);
			        return ret;
		        }else if(memcmp(Data+2,"http:",5)==0){
		             ret =checkIsGPano(Data,itemlen);
		             free(Data);
				     fclose(file);
				     return ret;
		             }
		        break;
	        case M_SOS:
	        case M_EOI:
		        free(Data);
		        goto error_;
	        default:
		        break;
	    }
    }

    error_:
    fclose(file);
    return -1;
}

int hasPanoramaFD(int fd){

	int ret = -1;
    FILE * file;
    file = fdopen(fd,"rb");

    int a;
    a = fgetc(file);
    if(a != 0xff || fgetc(file) != 0xD8){
	    goto error_;
    }
    for(;;){
	    int itemlen;
	    int marker = 0;
	    int ll,lh,got;
	    uchar * Data;

	    for(a=0;a<=16;a++){
	        marker = fgetc(file);
	        if(marker != 0xff)
		        break;
	        if(a>=16){
		        goto error_;
	        }
	    }

	    //Read the lenght of the section
	    lh = fgetc(file);
	    ll = fgetc(file);

	    itemlen = (lh <<8) | ll;

	    if(itemlen < 2){
	       goto error_;
	    }

	    Data = (uchar *)malloc(itemlen);
	    if(Data == NULL){
	    	goto error_;
	    }

	    //Store first two pre-read bytes
	    Data[0] = (uchar)lh;
	    Data[1] = (uchar)ll;

	    got = fread(Data+2,1,itemlen-2,file);
	    if(got != itemlen -2){
	        free(Data);
	        goto error_;
	    }

	    switch(marker){
	        case M_EXIF:
		        if(memcmp(Data+2,"Exif",4)==0){
		            free(Data);
		            ret= 0;
			        fclose(file);
			        return ret;
		        }else if(memcmp(Data+2,"http:",5)==0){
		             ret =checkIsGPano(Data,itemlen);
		             free(Data);
				     fclose(file);
				     return ret;
		             }
		        break;
	        case M_SOS:
	        case M_EOI:
		        free(Data);
		        goto error_;
	        default:
		        break;
	    }
    }

    error_:
    fclose(file);
    return -1;
}

int getHTCCameraId(const ExifData* d, char* out, int maxLen) {
	if (!d) {
		return 0;
	}
	ExifMnoteData *mn = exif_data_get_mnote_data(d);
	if (!mn || !isHtcED(d)) {
		return 0;
	}
	int num = exif_mnote_data_count(mn);
	int i;

	for (i = 0; i < num; ++i) {
		if (exif_mnote_data_get_id(mn, i) == HTC_MARKERNOTE_TAG_CAMERAID) {
			exif_mnote_data_get_value(mn, i, out, maxLen);
			return 1;
		}
	}
	return 0;
}
