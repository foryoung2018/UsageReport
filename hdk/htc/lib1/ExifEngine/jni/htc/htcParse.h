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
 * @desc    a header file for get htc makenote
 * @author    astley_chen@htc amt_masd_qq_zhong@htc.com
 * @history    2014/08/08
 */

#ifndef _HTC_PARSE_EXIF_H_
#define _HTC_PARSE_EXIF_H_

#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

#include <stdio.h>
#include <string.h>
#include <libexif/exif-data.h>
#include <libexif/htc/mnote-htc-tag.h>

int isHtcFile(const char *);
int isHtcED(const ExifData *);

// The function is used to test whether htc's photo is 3DMacro
// if is ,return 1,else return -1 or 0
int has3DMacroFile(const char *);
int has3DMacroED(int fd,const ExifData *);
  
//hasBoken fun is used to check the htc's photo whether has Bokeh, if return 1, or others.
int hasBokehFile(const char *);
int hasBokehED(int fd,const ExifData *);

//hasFace fun is used to check the htc's photo whether has Face, if return 1, or others.
int hasFaceFile(const char *);
int hasFaceED(const ExifData *);

//hasDepth fun is used to check the htc's photo whether has depth, if return 1, or others.
int hasDepthFile(const char *);
int hasDepthED(const ExifData *);

//hasFace fun is used to check the htc's photo whether has Face, if return 1, or others.
int hasIsProcessedFile(const char *);
int hasIsProcessedED(const ExifData *);
 
int hasPanoramaFile(const char *);
int hasPanoramaFD(int);
//if found camera id return 1, or others.
int getHTCCameraId(const ExifData* data,char* out,int maxLen);

#ifdef __cplusplus
}
#endif /* __cplusplus */

#endif /* __HTC_PARSE_EXIF_H_ */

