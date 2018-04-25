//#include <nativehelper/JNIHelp.h>
//#include <nativehelper/jni.h>
#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <assert.h>

#include "include/com_htc_lib1_exifengine_ExifEngine.h"

#ifndef NELEM
#define NELEM(x) ((int)(sizeof(x) / sizeof((x)[0])))
#endif

static const char *classPathName = "com/htc/lib1/exifengine/ExifEngine";

JNINativeMethod methods[] =
		{
				{ "readExifDataNative", "(Ljava/lang/String;)J",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_readExifDataNative },
				{ "readExifDataFromFDNative", "(I)J",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_readExifDataFromFDNative },
				{ "getTagValueIntNative", "(JIII)I",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_getTagValueIntNative },
				{ "getTagValueStringNative", "(JIIZ)Ljava/lang/String;",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_getTagValueStringNative },
				{ "getTagValueRationalNative", "(JII[I)[I",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_getTagValueRationalNative },
				{ "setTagValueIntNative", "(JIII)I",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_setTagValueIntNative },
				{ "setTagValueStringNative", "(JIILjava/lang/String;)I",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_setTagValueStringNative },
				{ "setTagValueRationalNative", "(JII[I)I",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_setTagValueRationalNative },
				{ "hasThumbnailNative", "(J)Z",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_hasThumbnailNative },
				{ "getThumbnailNative", "(J)[B",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_getThumbnailNative },
				{ "saveExifDataToFileNative", "(JLjava/lang/String;)I",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_saveExifDataToFileNative },
				{ "saveExifDataToFileFDNative", "(JI)I",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_saveExifDataToFileFDNative },
				{ "modifyThumbnailNative", "(J[B)I",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_modifyThumbnailNative },

				{ "has3DMacroNative", "(J)Z",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_has3DMacroNative },
				{ "hasBokehNative", "(J)Z",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_hasBokehNative },
				{ "hasFaceNative", "(J)Z",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_hasFaceNative },
				{ "hasDepthNative", "(J)Z",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_hasDepthNative },
				{ "hasProcessedNative", "(J)Z",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_hasProcessedNative },
				{ "hasPanoramaNative", "(J)Z",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_hasPanoramaNative },
				{ "getCameraIdNative", "(J)Ljava/lang/String;",
								(void*) Java_com_htc_lib1_exifengine_ExifEngine_getCameraIdNative },
				{ "freeExifDataNative", "(J)I",
						(void*) Java_com_htc_lib1_exifengine_ExifEngine_freeExifDataNative }, };

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    readExifDataNative
 * Signature: (Ljava/lang/String;)j
 */
JNIEXPORT jlong JNICALL Java_com_htc_lib1_exifengine_ExifEngine_readExifDataNative(
		JNIEnv *env, jobject obj, jstring filePath) {
	return readExifData(env, obj, filePath);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    readExifDataFromFDNative
 * Signature: (I)j
 */
JNIEXPORT jlong JNICALL Java_com_htc_lib1_exifengine_ExifEngine_readExifDataFromFDNative(
		JNIEnv *env, jobject obj, jint fd) {
	return readExifDataFromFD(env, obj, fd);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    getTagValueIntNative
 * Signature: (JIII)I
 */
JNIEXPORT jint JNICALL Java_com_htc_lib1_exifengine_ExifEngine_getTagValueIntNative(
		JNIEnv *env, jobject obj, jlong handle, jint section, jint tag,
		jint defaultValue) {
	return getTagValueInt(env, obj, handle, section, tag, defaultValue);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    getTagValueStringNative
 * Signature: (JIIZ)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_htc_lib1_exifengine_ExifEngine_getTagValueStringNative(
		JNIEnv *env, jobject obj, jlong handle, jint section, jint tag, jboolean appendRawData) {
	return getTagValueString(env, obj, handle, section, tag, appendRawData);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    getTagValueRationalNative
 * Signature: (JII[I)[I
 */
JNIEXPORT jintArray JNICALL Java_com_htc_lib1_exifengine_ExifEngine_getTagValueRationalNative(
		JNIEnv *env, jobject obj, jlong handle, jint section, jint tag,
		jintArray defaultValue) {
	return getTagValueRational(env, obj, handle, section, tag, defaultValue);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    setTagValueIntNative
 * Signature: (JIII)Z
 */
JNIEXPORT jint JNICALL Java_com_htc_lib1_exifengine_ExifEngine_setTagValueIntNative(
		JNIEnv *env, jobject obj, jlong handle, jint section, jint tag,
		jint value) {
	return setTagValueInt(env, obj, handle, section, tag, value);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    setTagValueStringNative
 * Signature: (JIILjava/lang/String;)Z
 */
JNIEXPORT jint JNICALL Java_com_htc_lib1_exifengine_ExifEngine_setTagValueStringNative(
		JNIEnv *env, jobject obj, jlong handle, jint section, jint tag,
		jstring value) {
	return setTagValueString(env, obj, handle, section, tag, value);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    setTagValueRationalNative
 * Signature: (JII[I)Z
 */
JNIEXPORT jint JNICALL Java_com_htc_lib1_exifengine_ExifEngine_setTagValueRationalNative(
		JNIEnv *env, jobject obj, jlong handle, jint section, jint tag,
		jintArray value) {
	return setTagValueRational(env, obj, handle, section, tag, value);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    hasThumbnailNative
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_htc_lib1_exifengine_ExifEngine_hasThumbnailNative(
		JNIEnv *env, jobject obj, jlong handle) {
	return hasThumbnail(env, obj, handle);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    getThumbnailNative
 * Signature: (J)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_htc_lib1_exifengine_ExifEngine_getThumbnailNative(
		JNIEnv *env, jobject obj, jlong handle) {
	return getThumbnail(env, obj, handle);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    saveExifDataToFileNative
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_htc_lib1_exifengine_ExifEngine_saveExifDataToFileNative(
		JNIEnv *env, jobject obj, jlong handle, jstring filePath) {
	return saveExifDataToFile(env, obj, handle, filePath);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    saveExifDataToFileFDNative
 * Signature: (JI)Z
 */
JNIEXPORT jint JNICALL Java_com_htc_lib1_exifengine_ExifEngine_saveExifDataToFileFDNative(
		JNIEnv *env, jobject obj, jlong handle, jint fd) {
	return saveExifDataToFileFD(env, obj, handle, fd);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    modifyThumbnailNative
 * Signature: (J[B)I
 */
JNIEXPORT jint JNICALL Java_com_htc_lib1_exifengine_ExifEngine_modifyThumbnailNative(
		JNIEnv *env, jobject obj, jlong handle, jbyteArray thumbFileData) {
	return modifyThumbnail(env, obj, handle, thumbFileData);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    has3DMacroNative
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_htc_lib1_exifengine_ExifEngine_has3DMacroNative(
		JNIEnv *env, jobject obj, jlong handle) {
	return has3DMacro(env, obj, handle);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    hasBokehNative
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_htc_lib1_exifengine_ExifEngine_hasBokehNative(
		JNIEnv *env, jobject obj, jlong handle) {
	return hasBokeh(env, obj, handle);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    hasFaceNative
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_htc_lib1_exifengine_ExifEngine_hasFaceNative(
		JNIEnv *env, jobject obj, jlong handle) {
	return hasFace(env, obj, handle);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    hasDepthNative
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_htc_lib1_exifengine_ExifEngine_hasDepthNative(
		JNIEnv *env, jobject obj, jlong handle) {
	return hasDepth(env, obj, handle);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    hasProcessedNative
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_htc_lib1_exifengine_ExifEngine_hasProcessedNative(
		JNIEnv *env, jobject obj, jlong handle) {
	return hasProcessed(env, obj, handle);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    hasPanoramaNative
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_htc_lib1_exifengine_ExifEngine_hasPanoramaNative
  (JNIEnv *env, jobject obj, jlong handle){
	return hasPanorama(env,obj,handle);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    getCameraIdNative
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_htc_lib1_exifengine_ExifEngine_getCameraIdNative
  (JNIEnv *env, jobject obj, jlong handle){
	return getCameraId(env,obj,handle);
}

/*
 * Class:     com_htc_lib1_exifengine_ExifEngine
 * Method:    freeExifDataNative
 * Signature: (J)Z
 */
JNIEXPORT jint JNICALL Java_com_htc_lib1_exifengine_ExifEngine_freeExifDataNative(
		JNIEnv *env, jobject obj, jlong handle) {
	return freeExifData(env, obj, handle);
}

/*
 * Register several native methods for one class.
 */
int registerNativeMethods(JNIEnv* env, const char* className,
		JNINativeMethod* gMethods, int numMethods) {
	jclass clazz;

	//clazz = (*env)->FindClass(className);
	clazz = env->FindClass(className);
	if (clazz == NULL) {
		fprintf(stderr, "Native registration unable to find class '%s'\n",
				className);
		return JNI_FALSE;
	}

	if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
		fprintf(stderr, "RegisterNatives failed for '%s'\n", className);
		return JNI_FALSE;
	}

	return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 */
int registerNatives(JNIEnv* env) {
	return registerNativeMethods(env, classPathName, methods, NELEM(methods));
}

/*
 * Set some test stuff up.
 *
 * Returns the JNI version on success, -1 on failure.
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = -1;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		fprintf(stderr, "ERROR: GetEnv failed\n");
		goto bail;
	}
	assert(env != NULL);

	printf("In mgmain JNI_OnLoad\n");

	if (registerNatives(env) < 0) {
		fprintf(stderr, "ERROR: Exif native registration failed\n");
		goto bail;
	}

	/* success -- return valid version number */
	result = JNI_VERSION_1_4;

	bail: return result;
}
