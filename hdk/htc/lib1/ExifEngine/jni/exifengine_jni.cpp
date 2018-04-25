#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <ctype.h>
#include <unistd.h>
#include <android/log.h>

#include "include/exifengine_jni.h"

#define SIGN_BEGIN  0x12345678
#define SIGN_END    0x87654321

#define	EXIFENGINE_NEW(type,params,var,err_handle)		{ var = new type params; if(var) { g_NewCount++; } else { err_handle; } }
#define EXIFENGINE_DELETE(var)							{ if(var) { delete var; var=0; g_NewCount--; } }

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "EXIFENGINELIB", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "EXIFENGINELIB", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "EXIFENGINELIB", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "EXIFENGINELIB", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "EXIFENGINELIB", __VA_ARGS__)

#define CFLAG_DEBUG
#define ITER_DEBUG

/*
 * Global.
 */
int g_NewCount = 0;
int g_NewArrayCount = 0;
int g_ByteBufferCount = 0;

struct ITEROBJ {
	ITEROBJ() {
		signb = SIGN_BEGIN;
		signe = SIGN_END;
		errorCode = 0;
		pszFilename = 0;
		pfileHandle = 0;
		fd = 0;
		isReplaceExifThumbnail = false;
		exifData = 0;
	}

	int signb;
	const char* pszFilename;
	FILE* pfileHandle;
	int fd;
	int errorCode;
	bool isReplaceExifThumbnail;

	ExifData *exifData;
	int signe;
};

inline bool checkSign(ITEROBJ* pitem) {
	if (pitem == 0)
		return false;

	return (pitem->signb == SIGN_BEGIN && pitem->signe == SIGN_END);
}

void releaseResource(JNIEnv* env, ITEROBJ* pitem) {
	if (pitem == 0)
		return;
	if (!checkSign(pitem))
		return;
	if (pitem->pszFilename != 0) {
		delete[] pitem->pszFilename;
		pitem->pszFilename = 0;
	}
	if (pitem->pfileHandle != 0) {
		fclose(pitem->pfileHandle);
		pitem->pfileHandle = 0;
	}
	if (pitem->fd != 0) {
		pitem->fd = 0;
	}
	if (pitem->exifData != 0) {
#ifdef ITER_DEBUG
		LOGD("[releaseResource][new/delete] - pitem->exifData");
#endif
		exif_data_free(pitem->exifData);
		pitem->exifData = 0;
	}
	pitem->signb = pitem->signe = 0;
#ifdef ITER_DEBUG
	LOGD("[releaseResource][new/delete] - pitem");
#endif
	long ppitem = (long) pitem;
	EXIFENGINE_DELETE(pitem);
#ifdef CFLAG_DEBUG
	LOGD(
			"releseResource %08X,new:%d,new[]:%d", ppitem, g_NewCount, g_NewArrayCount);
#endif
}
static long currentTimeMillis() {
	struct timeval tv;
	gettimeofday(&tv, (struct timezone *) NULL);
	long long when = tv.tv_sec * 1000LL + tv.tv_usec / 1000;
	return (long) when;
}

jint freeExifData(JNIEnv *env, jobject obj, jlong handle) {

#ifdef CFLAG_DEBUG
	LOGD("[freeExifData] begin");
#endif
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[freeExifData] NG - Invalid handle.");
		releaseResource(env, pitem);
		return IMAGELIB_INVALID_HANDLE;
	}
	releaseResource(env, pitem);
#ifdef CFLAG_DEBUG
	LOGD("[freeExifData] end");
#endif
	return IMAGELIB_OK;
}

const char* allocAndConvertString(JNIEnv* env, jstring& filePath) {
#ifdef ITER_DEBUG
	LOGD(" alloc string begin");
#endif
	const char* pszPath = env->GetStringUTFChars(filePath, 0);
	if (pszPath == 0) {
		env->ReleaseStringUTFChars(filePath, pszPath);
		return 0;
	}

	int iPath = strlen(pszPath);
	char* pszOut = new char[iPath + 1];

	if (pszOut == 0) {
		env->ReleaseStringUTFChars(filePath, pszPath);
		return 0;
	}

	strcpy(pszOut, pszPath);
	env->ReleaseStringUTFChars(filePath, pszPath);

#ifdef ITER_DEBUG
	LOGD(" alloc string end");
#endif    
	return pszOut;
}

jint getError(JNIEnv *env, jobject obj, jlong handle) {
	if (handle == 0)
		return IMAGELIB_EMPTY_HANDLE;

	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem))
		return IMAGELIB_INVALID_HANDLE;

	return (jint) pitem->errorCode;
}

/* Remove spaces on the right of the string */
static void trim_spaces(char *buf) {
	char *s = buf - 1;
	for (; *buf; ++buf) {
		if (*buf != ' ')
			s = buf;
	}
	*++s = 0; /* nul terminate the string on the first of the final spaces */
}

/* Show the tag name and contents if the tag exists */
static void show_tag(ExifData *d, ExifIfd ifd, ExifTag tag) {
	/* See if this tag exists */
	ExifEntry *entry = exif_content_get_entry(d->ifd[ifd], tag);
	if (entry) {
		char buf[1024];

		/* Get the contents of the tag in human-readable form */
		exif_entry_get_value(entry, buf, sizeof(buf));

		/* Don't bother printing it if it's entirely blank */
		trim_spaces(buf);
		if (*buf) {
			LOGD("%s: %s\n", exif_tag_get_name_in_ifd(tag, ifd), buf);
		}
	} else {
		LOGD("entry == null for tag: 0x%0X\n", tag);
	}
}

static void checkDataSize(ExifEntry *entry, int targetSize) {
	ExifMem *mem;
	if (entry->data == NULL) {
		mem = exif_mem_new_default();
		entry->data = (unsigned char *) exif_mem_alloc(mem, targetSize);
		exif_mem_unref(mem);
		entry->size = targetSize;
	} else if (entry->size != targetSize) {
		mem = exif_mem_new_default();
		entry->data = (unsigned char *) exif_mem_realloc(mem, entry->data,
				targetSize);
		exif_mem_unref(mem);
		entry->size = targetSize;
	}

}

jlong readExifData(JNIEnv *env, jobject obj, jstring filePath) {
#ifdef CFLAG_DEBUG
	LOGD("[readExifData] readExifData begin");
#endif
	if (filePath == 0) {
		LOGE("[readExifData] NG - filePath is 0.");
		return 0;
	}

	ITEROBJ* pitem = 0;
#ifdef ITER_DEBUG
	LOGD("[readExifData][new/delete] + ITEROBJ");
#endif
	EXIFENGINE_NEW(ITEROBJ, (), pitem,
			LOGI("[readExifData] new ITEROBJ NG"); return 0;);

	if (filePath != 0) {
		pitem->pszFilename = allocAndConvertString(env, filePath);
		if (pitem->pszFilename != 0 && strlen(pitem->pszFilename) != 0) {
			LOGD("[readExifData] load exif from file:%s", pitem->pszFilename);
			pitem->exifData = exif_data_new_from_file(pitem->pszFilename);
			if (0 == pitem->exifData) {
				LOGI("[readExifData] new pitem->exifData NG");
				pitem->exifData = exif_data_new();
				//releaseResource(env, pitem);
				//return 0;
			}
		}
	}
#ifdef CFLAG_DEBUG
	LOGD("[readExifData] readExifData end");
#endif
	return (jlong) pitem;
}

void exif_loader_writer_fd(ExifLoader *l, const int fd) {
	FILE *f;
	int size;
	unsigned char data[1024];

	if (!l)
		return;

	f = fdopen(fd, "rb");
	if (!f) {
		return;
	}
	while (1) {
		size = fread(data, 1, sizeof(data), f);
		if (size <= 0)
			break;
		if (!exif_loader_write(l, data, size))
			break;
	}
	fclose(f);
}

ExifData *exif_data_new_from_fd(int fd) {
	ExifData *data;
	ExifLoader *loader;

	loader = exif_loader_new();
	exif_loader_writer_fd(loader, fd);
	data = exif_loader_get_data(loader);
	exif_loader_unref(loader);

	return data;
}

jlong readExifDataFromFD(JNIEnv *env, jobject obj, jint fd) {
#ifdef CFLAG_DEBUG
	LOGD("[readExifDataFromFD] readExifDataFromFD begin");
#endif
	if (fd <= 0) {
		LOGE("[readExifDataFromFD] NG - fd is <= 0");
		return 0;
	}

	ITEROBJ* pitem = 0;
#ifdef ITER_DEBUG
	LOGD("[readExifDataFromFD][new/delete] + ITEROBJ");
#endif
	EXIFENGINE_NEW(ITEROBJ, (), pitem,
			LOGI("[readExifDataFromFD] new ITEROBJ NG"); return 0;);

	pitem->fd = fd;
	pitem->exifData = exif_data_new_from_fd(pitem->fd);
	if (0 == pitem->exifData) {
		LOGI("[readExifData] new pitem->exifData NG");
		releaseResource(env, pitem);
		return 0;
	}
}

jint saveExifDataToFile(JNIEnv *env, jobject obj, jlong handle,
		jstring filePath) {
#ifdef CFLAG_DEBUG
	LOGD("[saveExifDataToFile] begin");
#endif	
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[saveExifDataToFile] NG - Invalid handle.");
		return IMAGELIB_INVALID_HANDLE;
	}

	if (filePath == 0) {
#ifdef CFLAG_DEBUG		
		LOGD("[saveExifDataToFile] Null file path, clear resource");
#endif
		return IMAGELIB_FAILED;
	}

	char* dstpath = (char *) allocAndConvertString(env, filePath);

	if (dstpath == 0 || strlen(dstpath) == 0) {
#ifdef CFLAG_DEBUG		
		LOGD("[saveExifDataToFile] Null file path, clear resource");
#endif		
		return IMAGELIB_FAILED;
	}

	// get file path
	char *filename = strrchr(dstpath, '/');
	int pathLenth = filename - dstpath + 1;
	char fileFolder[pathLenth + 1];
	memset(fileFolder, 0, pathLenth + 1);
	memcpy(fileFolder, dstpath, pathLenth);

	ModifyExifData(dstpath, pitem->exifData);

#ifdef CFLAG_DEBUG
	LOGD("[saveExifDataToFile] end");
#endif
	return IMAGELIB_OK;
}

jint saveExifDataToFileFD(JNIEnv *env, jobject obj, jlong handle, jint fd) {
#ifdef CFLAG_DEBUG
	LOGD("[saveExifDataToFileFD] begin");
#endif
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[saveExifDataToFileFD] NG - Invalid handle.");
		return IMAGELIB_INVALID_HANDLE;
	}

	if (fd <= 0) {
#ifdef CFLAG_DEBUG
		LOGD("[saveExifDataToFile] Null file path, clear resource");
#endif
		return IMAGELIB_FAILED;
	}

	ModifyExifDataFD(fd, pitem->exifData);

#ifdef CFLAG_DEBUG
	LOGD("[saveExifDataToFile] end");
#endif
	return IMAGELIB_OK;
}

ExifIfd getIfd(jint exifSection) {
	switch (exifSection) {
	case 0:
		return EXIF_IFD_0;
	case 1:
		return EXIF_IFD_1;
	case 2:
		return EXIF_IFD_EXIF;
	case 3:
		return EXIF_IFD_GPS;
	case 4:
		return EXIF_IFD_INTEROPERABILITY;
	default:
		return EXIF_IFD_COUNT;
		break;
	}
}

jstring getTagValueString(JNIEnv *env, jobject obj, jlong handle,
		jint exifSection, jint exifTag, jboolean appendRawData) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[getExifTagString] NG - Invalid handle.");
		return 0;
	}
	if (pitem->exifData == 0) {
		LOGE("[getExifTagString] NG - exifdata == null");
		return 0;
	}

	ExifEntry* entry = exif_content_get_entry(
			pitem->exifData->ifd[getIfd(exifSection)], ExifTag(exifTag));
	if (0 == entry) {
		LOGD("[getExifTagString] entry == null");
		return 0;
	}

	ExifFormat format = entry->format;
	switch (format) {
	case EXIF_FORMAT_ASCII:
	case EXIF_FORMAT_RATIONAL: //FIXME remove ?
	case EXIF_FORMAT_SRATIONAL: //FIXME remove ?
		break;
	default: {
		LOGE("[getExifTagString] NG - Not a String type");
		return 0;
	}
	}
	//add_by_shenkaige--------start
	const int raw_data_buf_def_len = 100;
	char tag_value[25] = { 0 };
	char *raw_data_out;
	if (appendRawData) {
		raw_data_out = (char *) malloc(raw_data_buf_def_len);
		memset(raw_data_out, 0, raw_data_buf_def_len);
	} else {
		raw_data_out = NULL;
	}
	//exif_entry_get_value(entry, str, sizeof(str));
	exif_entry_get_value_super(entry, tag_value, sizeof(tag_value),
			&raw_data_out, raw_data_buf_def_len);
	//
	jstring finalResult = NULL;
	if (raw_data_out) {
		if (strlen(raw_data_out) > 0) {
			char format[] = "%s\n%s";
			int buf_size = strlen(raw_data_out) + strlen(tag_value)
					+ strlen(format);
			char* buf = (char *) malloc(buf_size);
			memset(buf, 0, buf_size);
			snprintf(buf, buf_size, format, tag_value, raw_data_out);
			finalResult = env->NewStringUTF(buf);
			//
			free(buf);
		}
		free(raw_data_out);
	}
	if (!finalResult && strlen(tag_value) > 0) {
		finalResult = env->NewStringUTF(tag_value);
	}
	//add_by_shenkaige--------end
	return finalResult;
}

jint getTagValueInt(JNIEnv *env, jobject obj, jlong handle, jint exifSection,
		jint exifTag, jint defaultValue) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[getExifTagInt] NG - Invalid handle.");
		return (jint) defaultValue;
	}

	int retValue = defaultValue;

	if (pitem->exifData == 0) {
		LOGE("[getExifTagInt] NG - exifdata == 0");
		return retValue;
	}

	ExifData* ed = pitem->exifData;
	ExifByteOrder order;

	ExifEntry *entry = exif_content_get_entry(ed->ifd[getIfd(exifSection)],
			ExifTag(exifTag));

	if (entry != NULL) {
		ExifFormat format = entry->format;
		switch (format) {
		case EXIF_FORMAT_BYTE:
		case EXIF_FORMAT_SHORT:
		case EXIF_FORMAT_SBYTE:
		case EXIF_FORMAT_SSHORT: {
			order = exif_data_get_byte_order(ed);
			retValue = exif_get_short(entry->data, order);
			break;
		}
		case EXIF_FORMAT_LONG:
		case EXIF_FORMAT_SLONG: {
			order = exif_data_get_byte_order(ed);
			retValue = exif_get_long(entry->data, order);
			break;
		}
		default: {
			LOGE("[getExifTagInt] NG - Not a int type");
			return -1;
		}
		}
	}
	return (jint) retValue;
}

jintArray getTagValueRational(JNIEnv *env, jobject obj, jlong handle,
		jint exifSection, jint tag, jintArray defaultValue) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[getTagValueRational] NG - Invalid handle.");
		return defaultValue;
	}
	ExifRational ef;
	ExifSRational esf;
	int retValue[2] = { 0, 0 };
	if (pitem->exifData == 0) {
		return defaultValue;
	}
	ExifData* ed = pitem->exifData;
	ExifByteOrder order;
	ExifEntry *entry = exif_content_get_entry(ed->ifd[getIfd(exifSection)],
			ExifTag(tag));
	if (entry != NULL) {
		ExifFormat format = entry->format;
		switch (format) {
		case EXIF_FORMAT_RATIONAL: {
			order = exif_data_get_byte_order(ed);
			ef = exif_get_rational(entry->data, order);
			retValue[0] = ef.numerator;
			retValue[1] = ef.denominator;
			break;
		}
		case EXIF_FORMAT_SRATIONAL: {
			order = exif_data_get_byte_order(ed);
			esf = exif_get_srational(entry->data, order);
			retValue[0] = esf.numerator;
			retValue[1] = esf.denominator;
			break;
		}
		default: {
			LOGE("[getTagValueRational] NG - Not a int type");
			return defaultValue;
		}
		}
	} else {
		LOGD("[getTagValueRational] entry == null");
		return defaultValue;
	}
	jintArray finalResult = env->NewIntArray(2);
	env->SetIntArrayRegion(finalResult, 0, 2, retValue);
	return finalResult;
}
/* Get an existing tag, or create one if it doesn't exist */
static ExifEntry *init_tag(ExifData *exif, ExifIfd ifd, ExifTag tag) {
	ExifEntry *entry;
	if (!(entry = exif_content_get_entry(exif->ifd[ifd], tag))) {
		entry = exif_entry_new();
		entry->tag = tag;
		exif_content_add_entry(exif->ifd[ifd], entry);
		exif_entry_initialize(entry, tag);
	}
	return entry;
}

jint setTagValueString(JNIEnv *env, jobject obj, jlong handle, jint exifSection,
		jint exifTag, jstring value) {
#ifdef CFLAG_DEBUG
	LOGD("[setExifTagString] begin");
#endif
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[setExifTagString] NG - Invalid handle.");
		return IMAGELIB_INVALID_HANDLE;
	}
	if (pitem->exifData == 0) {
		LOGE("[setExifTagString] NG - ExifData not initialized.");
		return IMAGELIB_INVALID_HANDLE;
	}
	char* strValue = (char*) allocAndConvertString(env, value);
#ifdef CFLAG_DEBUG
	LOGD("[setExifTagString] value=%s", strValue);
#endif
	ExifEntry* entry;
	ExifTag etag = ExifTag(exifTag);
	ExifIfd exifIfd = getIfd(exifSection);
	ExifData *exifData = pitem->exifData;
	entry = init_tag(exifData, exifIfd, etag);
	if (!entry) {
		LOGE("entry init is NULL");
		return -1;
	}
	ExifFormat format = entry->format;
	switch (format) {
	case EXIF_FORMAT_ASCII:
	case EXIF_FORMAT_UNDEFINED:
	case EXIF_FORMAT_RATIONAL: //FIXME  remove ?
	case EXIF_FORMAT_SRATIONAL: //FIXME remove ?
		checkDataSize(entry,
				exif_format_get_size(format) * strlen(strValue) + 1);
		entry->components = entry->size / exif_format_get_size(format);
		if (entry->data == 0) {
			LOGE("[setExifTagString] NG - EntryData is NULL.");
			return -1;
		}
		memset(entry->data, '\0', entry->size);
		memcpy(entry->data, strValue, MIN(strlen(strValue), entry->size));
		break;
	default: {
		LOGE("[setExifTagString] NG - Not a string type");
		return -1;
	}
	}
#ifdef CFLAG_DEBUG
	LOGD("[setExifTagString] end");
#endif
	return IMAGELIB_OK;
}

jint setTagValueInt(JNIEnv *env, jobject obj, jlong handle, jint exifSection,
		jint exifTag, jint value) {
	ITEROBJ* pitem = (ITEROBJ*) handle;

	if (!checkSign(pitem)) {
		LOGE("[setExifTagInt] NG - Invalid handle.");
		return IMAGELIB_INVALID_HANDLE;
	}
	ExifByteOrder order = exif_data_get_byte_order(pitem->exifData);
	ExifEntry* entry = init_tag(pitem->exifData, getIfd(exifSection),
			ExifTag(exifTag));
	if (!entry) {
		LOGE("[setExifTagInt] entry init is NULL");
		return -1;
	}
	ExifFormat format = entry->format;
	switch (format) {
	case EXIF_FORMAT_BYTE:
	case EXIF_FORMAT_SHORT:
	case EXIF_FORMAT_SBYTE:
	case EXIF_FORMAT_SSHORT:
		checkDataSize(entry, exif_format_get_size(format) * entry->components);
		exif_set_short(entry->data, order, value);
		break;
	case EXIF_FORMAT_LONG:
	case EXIF_FORMAT_SLONG:
		checkDataSize(entry, exif_format_get_size(format) * entry->components);
		exif_set_long(entry->data, order, value);
		break;
	default: {
		LOGE("[setTagValueInt] NG - Not a int type");
		return -1;
	}
	}

	return IMAGELIB_OK;
}

jint setTagValueRational(JNIEnv *env, jobject obj, jlong handle,
		jint exifSection, jint tag, jintArray value) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[setTagValueRational] NG - Invalid handle.");
		return IMAGELIB_INVALID_HANDLE;
	}
	ExifEntry* entry;
	ExifByteOrder order;
	order = exif_data_get_byte_order(pitem->exifData);
	entry = init_tag(pitem->exifData, getIfd(exifSection), ExifTag(tag));
	if (!entry) {
		LOGE("[setTagValueRational]entry init is NULL,so create new entry");
	}
	if (env->GetArrayLength(value) < 2) {
		LOGE("[setTagValueRational] on invalidate array(must have 2 elements)");
		return IMAGELIB_INVALID_PARAMETER;
	}
	jint* elements = env->GetIntArrayElements(value, JNI_FALSE);
	ExifRational ef;
	ExifSRational esf;
	ExifRational er;
	ExifFormat format = entry->format;
	//----------------------
	entry->components = 1;
	checkDataSize(entry, exif_format_get_size(format) * entry->components);
	//
	if (entry->data == NULL) {
		return IMAGELIB_ALLOC_FAIL;
	}
	//----------------------
	switch (format) {
	case EXIF_FORMAT_RATIONAL:
		ef.numerator = (ExifLong) elements[0];
		ef.denominator = (ExifLong) elements[1];
		exif_set_rational(entry->data, order, ef);
		er = exif_get_rational(entry->data, order);
		break;
	case EXIF_FORMAT_SRATIONAL:
		esf.numerator = (ExifSLong) elements[0];
		esf.denominator = (ExifSLong) elements[1];
		exif_set_srational(entry->data, order, esf);
		break;
	default: {
		return IMAGELIB_FAILED;
	}
	}
	return IMAGELIB_OK;
}

jboolean hasThumbnail(JNIEnv *env, jobject obj, jlong handle) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[hasThumbnail] NG - Invalid handle.");
		return false;
	}
	return pitem->exifData && pitem->exifData->data && pitem->exifData->size > 0;
}

jbyteArray getThumbnail(JNIEnv *env, jobject obj, jlong handle) {
	ITEROBJ* pitem = (ITEROBJ*) handle;

	if (!checkSign(pitem)) {
		LOGE("[getThumbnail] NG - Invalid handle.");
		return NULL;
	}
	if (pitem->exifData != 0) {
		if ((pitem->exifData->data != NULL) && (pitem->exifData->size > 0)) {
			jbyteArray byteArray = env->NewByteArray(pitem->exifData->size);
			if (byteArray == NULL) {
				LOGE("[getThumbnail] NG - couldn't allocate thumbnail memory.");
				return NULL;
			}

			env->SetByteArrayRegion(byteArray, 0, pitem->exifData->size,
					(const jbyte*) (pitem->exifData->data));
			return byteArray;
		}
	}
	return NULL;
}

jint modifyThumbnail(JNIEnv *env, jobject obj, jlong handle,
		jbyteArray thumbFileData) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[modifyThumbnail] NG - Invalid handle.");
		return IMAGELIB_INVALID_HANDLE;
	}
	ExifData *exif = pitem->exifData;
	if (!exif) {
		return IMAGELIB_EMPTY_HANDLE;
	}
	jbyte * thumbBytes;
	int thumbBytesLenght = 0;
	if (thumbFileData) {
		thumbBytes = env->GetByteArrayElements(thumbFileData, 0);
		thumbBytesLenght = env->GetArrayLength(thumbFileData);
	}
	ExifMem *mem = exif_mem_new_default();
	if (thumbBytes && thumbBytesLenght > 0) {
		void *r;
		if (exif->data) {
			r = exif_mem_realloc(mem, exif->data, thumbBytesLenght);
		} else {
			r = exif_mem_alloc(mem, thumbBytesLenght);
		}
		if (r) {
			exif->data = (unsigned char *) r;
			exif->size = thumbBytesLenght;
			memcpy(exif->data, thumbBytes, thumbBytesLenght);
		} else {
			exif_mem_unref(mem);
			LOGE("[modifyThumbnail] NG - couldn't allocate thumbnail memory.");
			return IMAGELIB_ALLOC_FAIL;
		}
	} else {
		exif_mem_free(mem, exif->data);
		exif->size = 0;
	}
	exif_mem_unref(mem);
	return IMAGELIB_OK;
}

jboolean has3DMacro(JNIEnv *env, jobject obj, jlong handle) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[has3DMacro] NG - Invalid handle.");
		return false;
	}
	if (pitem->pszFilename != 0) {
		return 1 == has3DMacroFile(pitem->pszFilename);
	} else if (pitem->fd != 0) {
		return 1 == has3DMacroED(pitem->fd, pitem->exifData);
	}
	return false;
}

jboolean hasBokeh(JNIEnv *env, jobject obj, jlong handle) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[hasBokeh] NG - Invalid handle.");
		return false;
	}
	if (pitem->pszFilename != 0) {
		return 1 == hasBokehFile(pitem->pszFilename);
	} else if (pitem->fd != 0) {
		return 1 == hasBokehED(pitem->fd, pitem->exifData);
	}
	return false;
}

jboolean hasFace(JNIEnv *env, jobject obj, jlong handle) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[hasFace] NG - Invalid handle.");
		return false;
	}
	if (pitem->exifData == 0) {
		return false;
	} else {
		return 1 == hasFaceED(pitem->exifData);
	}
}

jboolean hasDepth(JNIEnv *env, jobject obj, jlong handle) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[hasDepth] NG - Invalid handle.");
		return false;
	}
	if (pitem->exifData == 0) {
		return false;
	} else {
		return 1 == hasDepthED(pitem->exifData);
	}
}

jboolean hasPanorama(JNIEnv *env, jobject obj, jlong handle) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[hasPanorama] NG - Invalid handle.");
		return false;
	}
	if (pitem->pszFilename != 0) {
		return 1 == hasPanoramaFile(pitem->pszFilename);
	} else if (pitem->fd != 0) {
		return 1 == hasPanoramaFD(pitem->fd);
	}
	return false;
}

jboolean hasProcessed(JNIEnv *env, jobject obj, jlong handle) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[hasProcessed] NG - Invalid handle.");
		return false;
	}

	if (pitem->exifData == 0) {
		return false;
	}
	return 1 == hasIsProcessedED(pitem->exifData);

}

jstring getCameraId(JNIEnv *env, jobject obj, jlong handle) {
	ITEROBJ* pitem = (ITEROBJ*) handle;
	if (!checkSign(pitem)) {
		LOGE("[getCameraId] NG - Invalid handle.");
		return 0;
	}
	char out[50];
	memset(out, 0, 50);
	if (getHTCCameraId(pitem->exifData, out, 50)) {
		return 0;
	} else {
		return env->NewStringUTF(out);
	}
}

void printLibVersion(JNIEnv *env, jobject obj) {
	LOGD("Native Version: %d.%d", LIB_VERSION, LIB_SUB_VERSION);
}

jint getLibVersion(JNIEnv *env, jobject obj) {
	return LIB_VERSION;
}
