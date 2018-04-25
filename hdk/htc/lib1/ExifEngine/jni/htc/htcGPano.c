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
 * @author    leon_chu@htc.com
 * @history    2013/05/02
 */


#ifndef HTCGPANO_C
#define HTCGPANO_C

#include <string.h>

//#include <cstring> /* use function: strstr(~) */

/*
#define ALOGD
#define ALOGV ALOGD
#define ALOGW ALOGD
#define ALOGE ALOGD
*/

// ref: https://developers.google.com/panorama/metadata/?hl=en
// The namespace URI is http://ns.google.com/photos/1.0/panorama/
// return true if URI is found.
//

static char const   SearchChar  = 'h';
static char const   SearchStr[] = "http://ns.google.com/photos/1.0/panorama/";
static int  const   SearchStrLen = strlen(SearchStr);

unsigned short checkIsGPano( char const * _xmpData, int const _xmpSize )
{
    if( NULL == _xmpData || _xmpSize <= 0){
        return 0;
    }

    unsigned short isGPano = 0;

    char const * strEnd = NULL;
    char const * strBeg = _xmpData;
    ptrdiff_t    strLen = _xmpSize;

    while( strLen > 0 && strLen >= SearchStrLen) {
        strEnd = (char*) memchr(strBeg, SearchChar, strLen);

        if( NULL == strEnd ){
            break;

        }

        strLen = strLen - (strEnd - strBeg);
        strBeg = strEnd + 1;
        if(strLen < SearchStrLen)
            break;
 
        if(memcmp(strEnd, SearchStr, SearchStrLen) == 0) {
            isGPano = 1;
            break;
        }

        strLen --;
    }
    return isGPano;
}


#ifdef _WIN32
    // A patch to support jhead compatibility in windows
    // support snprintf(~)
    #ifndef __MINGW32__
        #include <stdio.h>
        #define snprintf(str, n, format, ...)  \
            _snprintf_s(str, n, _TRUNCATE, format, __VA_ARGS__)
    #endif

    // support Andriod log functions
    #include <cstdarg>
    inline void ALOGD(char const * format, ...)
    {
        char buf[1024];
        std::va_list va;
        va_start(va, format);
    // MS suggest useing "vsnprintf_s" due to _CRT_SECURE_NO_WARNINGS
    // but by now just disable this warning (4996).
    #pragma warning( push )
    #pragma warning( disable: 4996 )
        int nc = vsnprintf( buf, 1023, format, va );
    #pragma warning( pop )
        va_end(va);
        printf( buf );
    }
    
    #define ALOGV ALOGD
    #define ALOGW ALOGD
    #define ALOGE ALOGD
#endif

#endif
