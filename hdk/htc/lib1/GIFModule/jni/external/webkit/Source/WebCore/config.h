/*
 * Copyright (C) 2004, 2005, 2006 Apple Inc.
 * Copyright (C) 2009 Google Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; see the file COPYING.LIB.  If not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 */

#if defined(HAVE_CONFIG_H) && HAVE_CONFIG_H
#ifdef BUILDING_WITH_CMAKE
#include "cmakeconfig.h"
#else
#include "autotoolsconfig.h"
#endif
#endif

#include <wtf/Platform.h>

/* See note in wtf/Platform.h for more info on EXPORT_MACROS. */
#if USE(EXPORT_MACROS)

#include <wtf/ExportMacros.h>

#if defined(BUILDING_JavaScriptCore) || defined(BUILDING_WTF)
#define WTF_EXPORT_PRIVATE WTF_EXPORT
#define JS_EXPORT_PRIVATE WTF_EXPORT
#else
#define WTF_EXPORT_PRIVATE WTF_IMPORT
#define JS_EXPORT_PRIVATE WTF_IMPORT
#endif

#define JS_EXPORTDATA JS_EXPORT_PRIVATE
#define JS_EXPORTCLASS JS_EXPORT_PRIVATE

#if defined(BUILDING_WebCore) || defined(BUILDING_WebKit)
#define WEBKIT_EXPORTDATA WTF_EXPORT
#else
#define WEBKIT_EXPORTDATA WTF_IMPORT
#endif

#else /* !USE(EXPORT_MACROS) */

#if !PLATFORM(CHROMIUM) && OS(WINDOWS) && !defined(BUILDING_WX__) && !COMPILER(GCC)
#if defined(BUILDING_JavaScriptCore) || defined(BUILDING_WTF)
#define JS_EXPORTDATA __declspec(dllexport)
#else
#define JS_EXPORTDATA __declspec(dllimport)
#endif
#if defined(BUILDING_WebCore) || defined(BUILDING_WebKit)
#define WEBKIT_EXPORTDATA __declspec(dllexport)
#else
#define WEBKIT_EXPORTDATA __declspec(dllimport)
#endif
#define WTF_EXPORT_PRIVATE
#define JS_EXPORT_PRIVATE
#define JS_EXPORTCLASS JS_EXPORTDATA
#else
#define JS_EXPORTDATA
#define JS_EXPORTCLASS
#define WEBKIT_EXPORTDATA
#define WTF_EXPORT_PRIVATE
#define JS_EXPORT_PRIVATE
#endif

#endif /* USE(EXPORT_MACROS) */

#ifdef __APPLE__
#define HAVE_FUNC_USLEEP 1
#endif /* __APPLE__ */

#if OS(WINDOWS)

#ifndef _WIN32_WINNT
#define _WIN32_WINNT 0x0500
#endif

#ifndef WINVER
#define WINVER 0x0500
#endif

// If we don't define these, they get defined in windef.h.
// We want to use std::min and std::max.
#ifndef max
#define max max
#endif
#ifndef min
#define min min
#endif

// CURL needs winsock, so don't prevent inclusion of it
#if !USE(CURL)
#ifndef _WINSOCKAPI_
#define _WINSOCKAPI_ // Prevent inclusion of winsock.h in windows.h
#endif
#endif

#endif /* OS(WINDOWS) */

#ifdef __cplusplus

// These undefs match up with defines in WebCorePrefix.h for Mac OS X.
// Helps us catch if anyone uses new or delete by accident in code and doesn't include "config.h".
#undef new
#undef delete
#include <wtf/FastMalloc.h>

#endif

// On MSW, wx headers need to be included before windows.h is.
// The only way we can always ensure this is if we include wx here.
#if PLATFORM(WX)
#include <wx/defs.h>
#endif

// this breaks compilation of <QFontDatabase>, at least, so turn it off for now
// Also generates errors on wx on Windows, presumably because these functions
// are used from wx headers. On GTK+ for Mac many GTK+ files include <libintl.h>
// or <glib/gi18n-lib.h>, which in turn include <xlocale/_ctype.h> which uses
// isacii().
#if !PLATFORM(QT) && !PLATFORM(WX) && !PLATFORM(CHROMIUM) && !(OS(DARWIN) && PLATFORM(GTK))
#include <wtf/DisallowCType.h>
#endif

#if COMPILER(MSVC)
#define SKIP_STATIC_CONSTRUCTORS_ON_MSVC 1
#elif !COMPILER(WINSCW)
#define SKIP_STATIC_CONSTRUCTORS_ON_GCC 1
#endif

#if PLATFORM(WIN)
#if defined(WIN_CAIRO)
#undef WTF_USE_CG
#define WTF_USE_CAIRO 1
#define WTF_USE_CURL 1
#ifndef _WINSOCKAPI_
#define _WINSOCKAPI_ // Prevent inclusion of winsock.h in windows.h
#endif
#elif !OS(WINCE)
#define WTF_USE_CG 1
#undef WTF_USE_CAIRO
#undef WTF_USE_CURL
#endif
#endif

#if PLATFORM(MAC)
// New theme
#define WTF_USE_NEW_THEME 1
#endif // PLATFORM(MAC)

#if OS(SYMBIAN)
#define USE_SYSTEM_MALLOC 1
#endif

#if OS(UNIX) || OS(WINDOWS)
#define WTF_USE_OS_RANDOMNESS 1
#endif

#if PLATFORM(CHROMIUM)

// Chromium uses this file instead of JavaScriptCore/config.h to compile
// JavaScriptCore/wtf (chromium doesn't compile the rest of JSC). Therefore,
// this define is required.
#define WTF_CHANGES 1

#define WTF_USE_GOOGLEURL 1

#if !defined(WTF_USE_V8)
#define WTF_USE_V8 1
#endif

#endif /* PLATFORM(CHROMIUM) */

#if !defined(WTF_USE_V8)
#define WTF_USE_V8 0
#endif /* !defined(WTF_USE_V8) */

/* Using V8 implies not using JSC and vice versa */
#if !defined(WTF_USE_JSC)
#define WTF_USE_JSC !WTF_USE_V8
#endif

#if USE(CG)
#ifndef CGFLOAT_DEFINED
#ifdef __LP64__
typedef double CGFloat;
#else
typedef float CGFloat;
#endif
#define CGFLOAT_DEFINED 1
#endif
#endif /* USE(CG) */

#ifdef BUILDING_ON_TIGER
#undef ENABLE_FTPDIR
#define ENABLE_FTPDIR 0
#endif

#if PLATFORM(WIN) && USE(CG)
#define WTF_USE_SAFARI_THEME 1
#endif

// CoreAnimation is available to IOS, Mac and Windows if using CG
#if PLATFORM(MAC) || PLATFORM(IOS) || (PLATFORM(WIN) && USE(CG))
#define WTF_USE_CA 1
#endif

#if PLATFORM(QT) && USE(V8) && defined(Q_WS_X11)
/* protect ourselves from evil X11 defines */
#include <bridge/npruntime_internal.h>
#endif

// htc modified begin
#define ANDROID_HTC_MODIFIED 1

#ifdef ANDROID_HTC_MODIFIED
// 20111102 add by wente_kuo for browser blocking
//#define HTC_ACCELERATE_BROWSER 0
// end add by wente_kuo

// 20130514 start: different resolution definition
/* HTC FULLHD devices */
#if ( defined(CFLAG_DEVICE_DlpDTU) \
   || defined(CFLAG_DEVICE_DlpDUG) \
   || defined(CFLAG_DEVICE_DlpDWG) \
   || defined(CFLAG_DEVICE_Dlx) \
   || defined(CFLAG_DEVICE_DlxJ) \
   || defined(CFLAG_DEVICE_DlxpU) \
   || defined(CFLAG_DEVICE_DlxpUL) \
   || defined(CFLAG_DEVICE_Dlxpwl) \
   || defined(CFLAG_DEVICE_DlxR) \
   || defined(CFLAG_DEVICE_DlxU) \
   || defined(CFLAG_DEVICE_DlxUB1) \
   || defined(CFLAG_DEVICE_DlxUL) \
   || defined(CFLAG_DEVICE_M7) \
   || defined(CFLAG_DEVICE_M7CDTL) \
   || defined(CFLAG_DEVICE_M7CDTU) \
   || defined(CFLAG_DEVICE_M7CDUG) \
   || defined(CFLAG_DEVICE_M7CDWG) \
   || defined(CFLAG_DEVICE_M7wlj) \
   || defined(CFLAG_DEVICE_M7wls) \
   || defined(CFLAG_DEVICE_M7wlv) \
   || defined(CFLAG_DEVICE_T6TL) \
   || defined(CFLAG_DEVICE_T6DUG) \
   || defined(CFLAG_DEVICE_T6DWG) \
   || defined(CFLAG_DEVICE_T6U) \
   || defined(CFLAG_DEVICE_T6WHL) \
   || defined(CFLAG_DEVICE_T6WL) \
   || defined(CFLAG_DEVICE_T6ULATT) \
   || defined(CFLAG_DEVICE_T6UL) \
    )
    #define FULLHD_PROJECT 1

/* HTC QHD devices */
#elif ( defined(CFLAG_DEVICE_CP3DCG) \
     || defined(CFLAG_DEVICE_CP3DTG) \
     || defined(CFLAG_DEVICE_CP3DUG) \
     || defined(CFLAG_DEVICE_Fireball) \
     || defined(CFLAG_DEVICE_TotemC2) \
     || defined(CFLAG_DEVICE_Valentewxc9) \
     || defined(CFLAG_DEVICE_Valentewx) \
     || defined(CFLAG_DEVICE_VilleC2) \
     || defined(CFLAG_DEVICE_VILLE) \
      )
    #define QHD_PROJECT 1

#endif
// 20130514 end: different resolution definition

// html editing
#define ENABLE_HTC_HTML_EDITING 1

// Support HTC text selection
#define ENABLE_HTC_SELECTION 1

#define ENABLE_HTC_HTML5_SUPPORT 1

// htc modified begin: Address detection.
#define DETECT_TEXTLINK_BY_LIB 1
// htc modified end: Address detection.

// TileGrid performance patch, by Iver Hsieh
#define ANDROID_HTC_PERFORMANCE_PATCH 1

//#define QCT_NETWORK_CACHE_OPTIMIZATION 1
//#define QCT_IMPROVE_SUNSPIDER_CHANGE_IFRAME_UPDATE 1
#define HTC_POSTPONE_PLUGIN_LOADING

#define SLOW_NETWORK_MONITOR

#define PLUGIN_UTILITY

#define ENABLE_COPY_IMAGE 1

#define ENABLE_WATCH_LATER 1

#define ENABLE_COPY_IMAGE 1

#endif//end of ANDROID_HTC_MODIFIED

// localization
#if defined(CFLAG_LANGUAGE_ARA) || defined(CFLAG_LANGUAGE_ARA)
#define ARABIC_ENABLE 1
#else
#define ARABIC_ENABLE 0
#endif

#ifdef CFLAG_DEBUG
#include "android/log.h"
//#include <wtf/text/CString.h>
#define __FILENAME__ (strrchr(__FILE__,'/')+1)//__FILE__//
//ANDROID_LOG_INFO, ANDROID_LOG_DEBUG, ANDROID_LOG_WARN
#define _DUMP_(...) __android_log_print(ANDROID_LOG_INFO, __FILENAME__, __VA_ARGS__)//SkDebugf(__VA_ARGS__)//
#else
#define _DUMP_(...) void(0)
#endif

#define ENABLE_XHR_RESPONSE_BLOB 1
#define ENABLE_DATALIST 1
#define ENABLE_BLOB 1
#define REGISTER_HTC_JNI 1
// htc modified end
