
LOCAL_PATH:= $(call my-dir)

#--------libexif2_v0.so------------
include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

LOCAL_SRC_FILES:= \
	libexif/libexif/exif-byte-order.c \
	libexif/libexif/exif-content.c \
	libexif/libexif/exif-data.c \
	libexif/libexif/exif-entry.c \
	libexif/libexif/exif-entry-attributes.c \
	libexif/libexif/exif-entry-patch.c \
	libexif/libexif/exif-format.c \
	libexif/libexif/exif-ifd.c \
	libexif/libexif/exif-loader.c \
	libexif/libexif/exif-log.c \
	libexif/libexif/exif-mem.c \
	libexif/libexif/exif-mnote-data.c \
	libexif/libexif/exif-tag.c \
	libexif/libexif/exif-utils.c \
	libexif/libexif/canon/exif-mnote-data-canon.c \
	libexif/libexif/canon/mnote-canon-entry.c \
	libexif/libexif/canon/mnote-canon-tag.c \
	libexif/libexif/fuji/exif-mnote-data-fuji.c \
	libexif/libexif/fuji/mnote-fuji-entry.c \
	libexif/libexif/fuji/mnote-fuji-tag.c \
	libexif/libexif/olympus/exif-mnote-data-olympus.c \
	libexif/libexif/olympus/mnote-olympus-entry.c \
	libexif/libexif/olympus/mnote-olympus-tag.c \
	libexif/libexif/pentax/exif-mnote-data-pentax.c \
	libexif/libexif/pentax/mnote-pentax-entry.c \
	libexif/libexif/pentax/mnote-pentax-tag.c \
	libexif/libexif/htc/exif-mnote-data-htc.c \
	libexif/libexif/htc/mnote-htc-entry.c \
	libexif/libexif/htc/mnote-htc-tag.c \
	
LOCAL_C_INCLUDES += \
        $(LOCAL_PATH)/libexif \
	$(LOCAL_PATH)/libexif/libexif \
	$(LOCAL_PATH)/libexif/libexif/canon \
	$(LOCAL_PATH)/libexif/libexif/fuji \
	$(LOCAL_PATH)/libexif/libexif/olympus \
	$(LOCAL_PATH)/libexif/libexif/pentax \
	$(LOCAL_PATH)/libexif/libexif/htc 

LOCAL_CFLAGS += -W -Wall
LOCAL_CFLAGS += -fPIC -DPIC
LOCAL_CFLAGS += -DGETTEXT_PACKAGE=\"libexif-12\"

ifeq ($(TARGET_BUILD_TYPE),release)
	LOCAL_CFLAGS += -O2
endif

ifneq ($(BUILD_CONF),minimal)
	LOCAL_C_INCLUDES += $(TOP)/libiconv
	LOCAL_CFLAGS += -DHAVE_ICONV
	LOCAL_SHARED_LIBRARIES += libiconv
endif

LOCAL_MODULE:= libexif2_v0

LOCAL_PRELINK_MODULE := false

LOCAL_SHARED_LIBRARIES := \
    	libnativehelper \
	libcutils \
	libutils \
	liblog \
	
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

#--------libexifhtc_v0.so------------
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional eng

LOCAL_MODULE := libexifhtc_v0

LOCAL_SRC_FILES:= \
        htc/exifModify.c \
        htc/htcFormatUtil.c \
        htc/htcGPano.c \
        htc/htcParse.c \

LOCAL_C_INCLUDES += \
        $(LOCAL_PATH)/libexif \
        $(LOCAL_PATH)/libexif/libexif \
        $(LOCAL_PATH)/libexif/libexif/htc \
        $(LOCAL_PATH)/htc

LOCAL_PRELINK_MODULE := false

LOCAL_SHARED_LIBRARIES := \
	libandroid_runtime \
	libutils \
	libcutils \
	libstdc++ \
    libstlport \
    libexif2_v0
    
LOCAL_CPPFLAGS := $(LOCAL_CFLAGS)

LOCAL_CXXFLAGS := $(LOCAL_CFLAGS)
LOCAL_LDLIBS := -llog -ljnigraphics -landroid

include $(BUILD_SHARED_LIBRARY)


#--------libexif_engine_v0.so------------
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional eng

LOCAL_MODULE := exifengine_v0

LOCAL_SRC_FILES:= \
        exifengine_jni.cpp \
        com_htc_lib1_exifengine_ExifEngine.cpp \

LOCAL_C_INCLUDES += \
        $(LOCAL_PATH)/libexif \
        $(LOCAL_PATH)/libexif/libexif \
        $(LOCAL_PATH)/libexif/libexif/htc \
        $(LOCAL_PATH)/htc \
        $(LOCAL_PATH)/include

LOCAL_PRELINK_MODULE := false

LOCAL_SHARED_LIBRARIES := \
	libandroid_runtime \
	libutils \
	libcutils \
	libstdc++ \
    libstlport \
    libexif2_v0 \
    libexifhtc_v0
    
LOCAL_CPPFLAGS := $(LOCAL_CFLAGS)

LOCAL_CXXFLAGS := $(LOCAL_CFLAGS)
LOCAL_LDLIBS := -llog -ljnigraphics -landroid

include $(BUILD_SHARED_LIBRARY)

