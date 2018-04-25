/**
 *
 * HTC Corporation Proprietary Rights Acknowledgment
 * Copyright (c) 2013 HTC Corporation
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
 * @file    htcGPano.c
 * @desc    a header file for jhead to check google panorama format
 * @author    aphid_wang@htc.com
 * @history    2013/12/11
 */

#ifndef HTC_FORMAT_UTIL_C
#define HTC_FORMAT_UTIL_C

#include <stdio.h>
#include <stdlib.h>
#include "htclog.h"

unsigned short verifyHtcFormat(FILE *infile, short const offset, char const *bytes, short size)
{
    if (size <= 0 || size > 32 || bytes == NULL) {
        ALOGE("[verifyHtcFormat] cannot verify size=%d", size);
        return 0;
    }

    unsigned short ok = 0;

    int cp, ep;
    cp = ftell(infile);
    fseek(infile, 0, SEEK_END);
    ep = ftell(infile);

    if (ep > size) {
        if (offset < 0) { // Verify from file end
            fseek(infile, -1*size, SEEK_END);
        } else {
            fseek(infile, 0, SEEK_SET);
        }

        char *tmpData = (char *)malloc(size);
        if (tmpData != NULL) {
            int tmpByte = fread(tmpData, 1, size, infile);
            if (tmpByte == size) {
                /* Mask: "htc" + '\0' + "twtp" */
                ok = 1;
                while(--size >= 0) {
                    if (tmpData[size] != bytes[size]) {
                        ok = 0;
                        break;
                    } // else ALOGD("[verify] OK %d: %x", size, tmpData[size]);
                }
            } else {
                ALOGE("[verifyHtcFormat] Read Fail?");
            }
            free(tmpData);
        } else {
            ALOGE("[verifyHtcFormat] could not allocate data for entire image, size=%d", size);
        }
    } else {
        ALOGD("[verifyHtcFormat] Can't Detect TAG");
    }

    ALOGD("[verifyHtcFormat] %d", ok);
    // recover offset
    fseek(infile, cp, SEEK_SET);

    return ok;
}

#endif
