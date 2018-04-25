#ifndef __exifengine_jni__
#define __exifengine_jni__

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

#include "ExifengineLibDefine.h"

//libexif
#include <libexif/exif-data.h>
#include <libexif/exif-tag.h>
#include <libexif/exif-entry.h>
#include <libexif/exif-byte-order.h>
#include <libexif/exif-format.h>
#include <libexif/exif-loader.h>
#include <libexif/exif-ifd.h>

#include <htc/htcParse.h>
#include <htc/exifModify.h>

#define LIB_VERSION 					1
#define LIB_SUB_VERSION 				0

jlong readExifData(JNIEnv *env, jobject obj, jstring filePath);
jlong readExifDataFromFD(JNIEnv *env, jobject obj, jint fd);
jint getTagValueInt(JNIEnv *env, jobject obj, jlong handle, jint exifSection,
		jint exifTag, jint defaultValue);
jstring getTagValueString(JNIEnv *env, jobject obj, jlong handle,
		jint exifSection, jint exifTag, jboolean appendRawData);
jintArray getTagValueRational(JNIEnv *env, jobject obj, jlong handle,
		jint section, jint tag, jintArray defaultValue);

jint setTagValueInt(JNIEnv *env, jobject obj, jlong handle, jint exifSection,
		jint exifTag, jint value);
jint setTagValueString(JNIEnv *env, jobject obj, jlong handle, jint exifSection,
		jint exifTag, jstring value);
jint setTagValueRational(JNIEnv *env, jobject obj, jlong handle, jint section,
		jint tag, jintArray value);
jboolean hasThumbnail(JNIEnv *env, jobject obj, jlong handle);
jbyteArray getThumbnail(JNIEnv *env, jobject obj, jlong handle);
jint saveExifDataToFile(JNIEnv *env, jobject obj, jlong handle,
		jstring filePath);
jint saveExifDataToFileFD(JNIEnv *env, jobject obj, jlong handle, jint fd);
jint modifyThumbnail(JNIEnv *env, jobject obj, jlong handle,
		jbyteArray thumbFileData);
jboolean has3DMacro(JNIEnv *env, jobject obj, jlong handle);
jboolean hasBokeh(JNIEnv *env, jobject obj, jlong handle);
jboolean hasFace(JNIEnv *env, jobject obj, jlong handle);
jboolean hasDepth(JNIEnv *env, jobject obj, jlong handle);
jboolean hasProcessed(JNIEnv *env, jobject obj, jlong handle);
jboolean hasPanorama(JNIEnv *env, jobject obj, jlong handle);
jstring getCameraId(JNIEnv *env, jobject obj, jlong handle);
jint freeExifData(JNIEnv *env, jobject obj, jlong handle);

jint getLibVersion(JNIEnv *env, jobject obj);

#ifdef __cplusplus
}
#endif /* __cplusplus */
#endif
