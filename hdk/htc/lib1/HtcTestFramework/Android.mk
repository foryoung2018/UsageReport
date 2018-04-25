LOCAL_PATH:= $(call my-dir)

# the library
# ============================================================
include $(CLEAR_VARS)

#LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_JAVA_LIBRARIES := core ext framework

LOCAL_MODULE:= HTCTestFramework

LOCAL_MODULE_CLASS := JAVA_LIBRARIES

# add the system libraries to the search path
#LOCAL_AIDL_INCLUDES += java/android

LOCAL_NO_STANDARD_LIBRARIES := true

#LOCAL_CERTIFICATE := platform

include $(BUILD_STATIC_JAVA_LIBRARY)
#endif

# [GB20] Begin:Vincent_Lu,2011/06/07
# Below describe source files that will be added to HTC SDK.
# include $(BUILD_STUB) will enable this action.

# ====  the api stubs ===========================
# Below two lines should follow "include $(BUILD_JAVA_LIBRARY)" before any "include $(CLEAR_VARS)"
HTC_STUB_TARGET_MODULE := $(LOCAL_BUILT_MODULE)
HTC_STUB_TARGET_INTERMEDIATES := $(TARGET_OUT_COMMON_INTERMEDIATES)/JAVA_LIBRARIES/$(LOCAL_MODULE)_intermediates

#HTC_INCLUDE_RES_PACKAGE := $(call intermediates-dir-for,APPS,com.htc.resources,,COMMON)/package-export.apk
HTC_ADDITIONAL_JAVA_DIR :=  $(call intermediates-dir-for,APPS,com.htc.resources,,COMMON)/src/

include $(CLEAR_VARS)

LOCAL_SRC_FILES:=$(call all-subdir-java-files)

LOCAL_MODULE := HTCTestFramework_stubs

LOCAL_MODULE_CLASS := JAVA_LIBRARIES

LOCAL_JAVA_LIBRARIES := core ext framework

include $(BUILD_STUB)
# ====  the api stubs end ===========================
# [GB20] End:Vincent_Lu,2011/06/07
