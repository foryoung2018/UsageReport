
# ------------------------------------------------------------------
#  Define Variables                                                 
# ------------------------------------------------------------------
LOCAL_PATH := $(call my-dir)

ifneq (${TARGET_VENDOR}, samsung)
include $(CLEAR_VARS)

ifeq (${TARGET_ARCH_ABI},armeabi-v7a)
LOCAL_MODULE := morphohyperlapse
LOCAL_SRC_FILES := armeabi-v7a/libmorphohyperlapse.a
endif

ifeq (${TARGET_ARCH_ABI},arm64-v8a)
LOCAL_MODULE := morphohyperlapse
LOCAL_SRC_FILES := arm64-v8a/libmorphohyperlapse.a
endif

include $(PREBUILT_STATIC_LIBRARY)
endif

include $(CLEAR_VARS)

ifeq (${TARGET_ARCH_ABI},armeabi-v7a)
LOCAL_MODULE := morpho_render_util
LOCAL_SRC_FILES := armeabi-v7a/libmorpho_render_util.so
endif

ifeq (${TARGET_ARCH_ABI},arm64-v8a)
LOCAL_MODULE := morpho_render_util
LOCAL_SRC_FILES := arm64-v8a/libmorpho_render_util.so
endif

include $(PREBUILT_SHARED_LIBRARY)
