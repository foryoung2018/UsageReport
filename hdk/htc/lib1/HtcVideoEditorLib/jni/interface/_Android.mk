LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := morpho_hyperlapse_jni

LOCAL_SRC_FILES += \
	engine2_jni.c \

LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/../external/include \
    $(LOCAL_PATH)

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
LOCAL_CFLAGS += -DAVOID_TABLES -O3 -ftree-vectorize
LOCAL_CFLAGS += -DNDEBUG -DNO_DEBUG -DMOR_ANDROID_UTIL_SPEC_USE_NEON -DCPU64 \
		-DBUILD_WITHOUT_MULTITHREAD 

endif

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
LOCAL_CFLAGS += -DAVOID_TABLES -O3 -ftree-vectorize -mfloat-abi=softfp -mfpu=neon -mvectorize-with-neon-quad
LOCAL_CFLAGS += -DNDEBUG -DNO_DEBUG -DMOR_ANDROID_UTIL_SPEC_USE_NEON \
		-DBUILD_WITHOUT_MULTITHREAD

endif


LOCAL_STATIC_LIBRARIES := morphohyperlapse

LOCAL_LDLIBS := -llog -ljnigraphics -landroid

include $(BUILD_SHARED_LIBRARY)

