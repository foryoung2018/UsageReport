##
## Copyright 2009, The Android Open Source Project
## Copyright (C) 2011, 2012, Sony Ericsson Mobile Communications AB
## Copyright (C) 2012 Sony Mobile Communications AB
##
## Redistribution and use in source and binary forms, with or without
## modification, are permitted provided that the following conditions
## are met:
##  * Redistributions of source code must retain the above copyright
##    notice, this list of conditions and the following disclaimer.
##  * Redistributions in binary form must reproduce the above copyright
##    notice, this list of conditions and the following disclaimer in the
##    documentation and/or other materials provided with the distribution.
##
## THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS ``AS IS'' AND ANY
## EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
## IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
## PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
## CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
## EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
## PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
## PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
## OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
## (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
## OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
##

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := gifdecoder2


LOCAL_CFLAGS := -DANDROID_SMP=1

LOCAL_LDLIBS    := -llog 

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/external/webkit/Source/WebCore \
	$(LOCAL_PATH)/external/webkit/Source/WebCore/platform \
	$(LOCAL_PATH)/external/webkit/Source/WebCore/platform/graphics/android \
	$(LOCAL_PATH)/external/webkit/Source/WebCore/platform/text \
	$(LOCAL_PATH)/external/webkit/Source/WebCore/platform/graphics \
	$(LOCAL_PATH)/external/webkit/Source/WebCore/platform/graphics/skia \
	$(LOCAL_PATH)/external/webkit/Source/WebCore/platform/graphics/transforms \
	$(LOCAL_PATH)/external/webkit/Source/WebCore/platform/image-decoders \
	$(LOCAL_PATH)/external/webkit/Source/WebCore/platform/image-decoders/gif \
	$(LOCAL_PATH)/external/webkit/Source/JavaScriptCore \
	$(LOCAL_PATH)/external/webkit/Source/JavaScriptCore/wtf \
	$(LOCAL_PATH)/external/webkit/Source/JavaScriptCore/wtf/unicode \
	$(LOCAL_PATH)/external/webkit/Source/JavaScriptCore/wtf/unicode/icu \
	$(LOCAL_PATH)/external/webkit/Source/JavaScriptCore/icu \
	$(LOCAL_PATH)/external/skia/include/core \
	$(LOCAL_PATH)/external/skia/include/lazy \


LOCAL_SRC_FILES += \
	external/webkit/Source/Webkit/android/htc/android_webkit_GIFImageParser.cpp \
	external/webkit/Source/WebCore/platform/image-decoders/ImageDecoder.cpp \
	external/webkit/Source/WebCore/platform/image-decoders/skia/ImageDecoderSkia.cpp \
	external/webkit/Source/WebCore/platform/image-decoders/gif/GIFImageDecoder.cpp \
	external/webkit/Source/WebCore/platform/image-decoders/gif/GIFImageReader.cpp \
	external/webkit/Source/WebCore/platform/SharedBuffer.cpp \
	external/webkit/Source/WebCore/platform/graphics/skia/NativeImageSkia.cpp \
	external/webkit/Source/WebCore/platform/graphics/IntRect.cpp \
	external/webkit/Source/JavaScriptCore/wtf/FastMalloc.cpp \
	external/webkit/Source/JavaScriptCore/wtf/Assertions.cpp \
	external/skia/src/core/SkBitmap.cpp \
	external/skia/src/core/SkUtils.cpp \
	external/skia/src/core/SkPixelRef.cpp \
	external/skia/src/core/SkMallocPixelRef.cpp \
	external/skia/src/core/Sk64.cpp \
	external/skia/src/core/SkColorTable.cpp \
	external/skia/src/core/SkString.cpp \
	external/skia/src/core/SkDither.cpp \
	external/skia/src/core/SkFlattenable.cpp \
	external/skia/src/core/SkPtrRecorder.cpp \
	external/skia/src/ports/SkMemory_malloc.cpp \
	external/skia/src/opts/SkUtils_opts_none.cpp \
    
include $(BUILD_SHARED_LIBRARY)
