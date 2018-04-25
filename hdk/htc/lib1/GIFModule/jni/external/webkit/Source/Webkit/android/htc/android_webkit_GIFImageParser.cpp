#include <config.h>
#include <wtf/Platform.h>

#include "image-decoders/gif/GIFImageDecoder.h"
#include "SkUtils.h"
#include "SkBitmap.h"
#include "SkColorPriv.h"
//#include "WebCoreJni.h"
#include "SkDither.h"

#include<android/log.h>
#include <jni.h>

#define LOG_TAG "libgif"
#define ALOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)
#define ALOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)

//for 20080103 version GIFImageDecoder's data storage is SharedBuffer instead of wtf::Vector
namespace android{
using namespace std;
using namespace WebCore;

//import 20080225

static jclass   gRect_class;
static jmethodID gRect_constructorMethodID;
static jobject createRect(JNIEnv* env, jint left, jint top, jint right, jint bottom);

//GIFImageDecoder gifImgDecoder;

static struct {
    jfieldID    mValue;
    jfieldID    mNativeContext;
    jfieldID    mCachedBitmap;
    jmethodID	setBitmapPixels;
    jmethodID preparedBitmap;
} gGIFImageParser;


//////////////////////////
void doThrow(JNIEnv* env, const char* exc, const char* msg)
{
    jclass npeClazz;

    npeClazz = env->FindClass(exc);

    if (npeClazz == NULL) {
        ALOGE("Unable to find class %s", exc);
    }

    env->ThrowNew(npeClazz, msg);
}

#define LOGMSGIF(q,s) {if(NULL == q){ALOGE(s);}

static jint
android_webkit_GIFImageParser_frameCount(JNIEnv *env, jobject obj)
{

	GIFImageDecoder *gifdecoder = (GIFImageDecoder *)env->GetIntField(obj, gGIFImageParser.mNativeContext);
	if(NULL == gifdecoder) {
		doThrow(env,"java/lang/IllegalStateException", "GIFImageParser was released");
		return 0;
	}

	return gifdecoder->frameCount();
}

static jobject allocJavaBitmap(JNIEnv *env, jobject obj)
{
	ImageFrame* rgbBuffer = NULL;
    GIFImageDecoder *gifdecoder = NULL;
	int nWidth, nHeight;

    if (NULL==(gifdecoder = (GIFImageDecoder *)env->GetIntField(obj, gGIFImageParser.mNativeContext))) {
    	doThrow(env,"java/lang/IllegalStateException", "GIFImageParser was released");
		return NULL;
	}

    rgbBuffer = gifdecoder->frameBufferAtIndex((size_t)0);

    const SkBitmap & skBmp = rgbBuffer->bitmap();
    nWidth = skBmp.width();
    nHeight = skBmp.height();

	if (nWidth == 0 || nHeight == 0) {
		doThrow(env,"java/lang/IllegalStateException", "frame 0 has no dimension");
		return NULL;
	}

    jobject bitmap = env->CallObjectMethod(obj, gGIFImageParser.preparedBitmap, skBmp.width(), skBmp.height());
	return bitmap;
}

static jobject
android_webkit_GIFImageParser_getFrame(JNIEnv *env, jobject obj, jint index)
{
	ImageFrame* rgbBuffer = NULL;
	unsigned int pos = 0;
	GIFImageDecoder *gifdecoder = NULL;
	int  *pResultArray ;

	if (NULL == (gifdecoder = (GIFImageDecoder *)env->GetIntField(obj, gGIFImageParser.mNativeContext))) {
		ALOGE("Impossible %s, obj:%x, fields.mNativeContext:%x",__FUNCTION__, obj, gGIFImageParser.mNativeContext);
		doThrow(env,"java/lang/IllegalStateException", "GIFImageParser was released");
		return NULL;
	}

	// init cached bitmap
	jobject bitmap = env->GetObjectField(obj, gGIFImageParser.mCachedBitmap);
	if (bitmap == NULL) {
		bitmap = allocJavaBitmap(env, obj);
   }

	if (index >= gifdecoder->frameCount() || index < 0) {
		doThrow(env,"java/lang/IllegalArgumentException", "index was invalid");
		return NULL;
	}

	rgbBuffer = gifdecoder->frameBufferAtIndex((size_t)index);

	const SkBitmap & skBmp = rgbBuffer->bitmap();
	pResultArray = (int*) skBmp.getPixels();


	if(NULL == pResultArray) {
		ALOGE("Out of Memory%s",__FUNCTION__);
		doThrow(env,"java/lang/OutOfMemoryError", "no memory allocated In frameBufferAtIndex");
		return NULL;
	}


	int count =  skBmp.width() * skBmp.height();
	jintArray intArray = env->NewIntArray(count);

	if (skBmp.config() == SkBitmap::kARGB_8888_Config ) {
		env->SetIntArrayRegion(intArray, 0, count, pResultArray);
	} else {
		ALOGE("ImageFrame uses ARGB foramt. it's impossible to reach here!");
		abort();
	}

	jobject bmp = env->CallObjectMethod(obj, gGIFImageParser.setBitmapPixels, intArray, skBmp.width(), skBmp.width(), skBmp.height());

	return bmp;
}

static jobject
android_webkit_GIFImageParser_frameRectAtIndex(JNIEnv *env, jobject obj,jint index)
{
    ImageFrame* rgbBuffer = NULL;
    IntRect iRect;

    jint mx= 0;
    jint my= 0;
    jint mright = 0;
    jint mbottom = 0;

	GIFImageDecoder *gifdecoder = (GIFImageDecoder *)env->GetIntField(obj, gGIFImageParser.mNativeContext);
	if (NULL == gifdecoder) {
		doThrow(env,"java/lang/IllegalStateException", "GIFImageParser was released");
		return NULL;
	}

	if (index >= gifdecoder->frameCount() || index < 0) {
		doThrow(env,"java/lang/IllegalArgumentException", "index was invalid");
		return NULL;
	}

	rgbBuffer = gifdecoder->frameBufferAtIndex((size_t)index);
	if (NULL == rgbBuffer)
	{
		ALOGE("frameRectAtIndex(%d) return NULL buffer.", index);
		char error[128];
		sprintf(error, "frame %d can't get a decoded bitmap");
		doThrow(env,"com/htc/lib1/GIFImageParser$JniException", error);
		return NULL;
	}

    const SkBitmap &skBmp = rgbBuffer->bitmap();
#ifdef CFLAG_DEBUG	
    ALOGI("android_webkit_GIFImageParser_frameRectAtIndex(): w=%d, h=%d", skBmp.width(), skBmp.height());
#endif

    //iRect = rgbBuffer->rect();
	//mx = iRect.x();
    //my = iRect.y();
    //mright  = mx + iRect.width();
    //mbottom = my + iRect.height();
    //ALOGI("    l=%d, t=%d, r=%d, b=%d, w=%d, h=%d", mx, my, mright, mbottom, iRect.width(), iRect.height());

    mx = my = 0;
    mright = skBmp.width();
    mbottom = skBmp.height();

    return createRect(env, mx, my, mright, mbottom);
}

/*20080425 add duration ~{ */
static jlong
android_webkit_GIFImageParser_frameDurationAtIndex(JNIEnv *env, jobject obj,jint index)
{
      ImageFrame* rgbBuffer = NULL;
//      RGBA32Array rgbArray;
      IntRect iRect;

      jint mx= 0;
      jint my= 0;
      jint mright = 0;
      jint mbottom = 0;

	GIFImageDecoder *gifdecoder = (GIFImageDecoder *)env->GetIntField(obj, gGIFImageParser.mNativeContext);
	if(NULL == gifdecoder) {
		doThrow(env,"java/lang/IllegalStateException", "GIFImageParser was released");
		return NULL;
	}

	if (index >= gifdecoder->frameCount() || index < 0) {
		doThrow(env,"java/lang/IllegalArgumentException", "index was invalid");
		return NULL;
	}

	rgbBuffer = gifdecoder->frameBufferAtIndex((size_t)index);
	if(NULL == rgbBuffer)
	{
		ALOGE("frameBufferAtIndex(%d) return NULL buffer.", index);
		char error[128];
		sprintf(error, "frame %d can't get a decoded bitmap");
		doThrow(env,"com/htc/lib1/GIFImageParser$JniException", error);
		return NULL;
	}

	long delay = rgbBuffer->duration();
	return delay;
}



static void
android_webkit_GIFImageParser_setDataPath(JNIEnv *env, jobject obj, jstring path)
{
      const char *pathStr;
      int size = 0;
      FILE* mfile = NULL;
      char* buf = NULL;
      int index = 0;

      PassRefPtr<SharedBuffer> mbuffer = NULL;

     if (path == NULL) {
         ALOGE("[android_webkit_GIFImageParser_SetData] path is null");
        doThrow(env,"java/lang/IllegalArgumentException", "file path is null");
        return;
      }

      GIFImageDecoder *gifdecoder = (GIFImageDecoder *)env->GetIntField(obj, gGIFImageParser.mNativeContext);

	if(NULL == gifdecoder) {
		doThrow(env,"java/lang/IllegalStateException", "GIFImageParser was released");
		return;
	}

      pathStr = env->GetStringUTFChars(path, NULL);
      if(NULL == pathStr)
	 {
         doThrow(env,"java/lang/IllegalArgumentException", "file path is invalid");
		return;
	}


      /*Read file*/
      mfile = fopen(pathStr,"r");
      if(NULL == mfile)
       {
	    ALOGE("fopen fail: pathStr:%s", pathStr);
	    doThrow(env,"java/lang/IllegalArgumentException", "file path is invalid");
	    return;
       }
	mbuffer = SharedBuffer::create();
	if(NULL == mbuffer)
	{
		ALOGE("[android_webkit_GIFImageParser_SetData] memory allocate fail");
		doThrow(env,"java/lang/OutOfMemoryError", "The buffer decoded can't be created");
		return;
	}
	mbuffer->ref();

	if(mfile!=NULL)
	{
          fseek(mfile,0,SEEK_END);
	   size = ftell(mfile);
	   buf = (char*)malloc(size);

	   if(NULL == buf)
	   {
		doThrow(env,"java/lang/OutOfMemoryError", "The buffer decoded can't be created");
		ALOGE("[android_webkit_GIFImageParser_SetData] memory allocate fail");
		return;
 	   }

	   memset(buf,0,(size_t)size);
	   fseek(mfile,0,SEEK_SET);
	   fread(buf,size,1,mfile);
	   fclose(mfile);

	/*for 20080103 version ~{*/
	/* Although setData() passes the pointer, its member RefPtr<SharedBuffer> catches the SharedBuffer.
	   When setData() run, SharedBuffer.deref() is invoked that decrease ref count and delete itself if conut equals zero. When RefPtr<SharedBuffer> is freed,  SharedBuffer.deref() is also invoken. (if(--mRefs<=0) delete this)
	So, We don't need to free SharedBuffer we generates after passing it to decoder. see Shared.h
	*/
	mbuffer->clear();
	mbuffer->append(buf,size);
	gifdecoder->setData(mbuffer.get(),false);
	mbuffer->deref();

	/* }~for 20080103 version*/
	}

	env->ReleaseStringUTFChars(path, pathStr);
	if(buf!=NULL){
		free(buf);
		buf=NULL;
	}
}


/*20080303 Byron Lin ~{*/
static void
android_webkit_GIFImageParser_setRawData(JNIEnv *env, jobject obj, jbyteArray byteArray)
{
	jint len = 0;
	char* bytebuffer = NULL;
	int index = 0;
	PassRefPtr<SharedBuffer> mbuffer = NULL;

	//LOGW("Enter android_webkit_GIFImageParser_SetRawData");

	if (byteArray == NULL) {
		ALOGE("path is null");
		doThrow(env,"java/lang/IllegalArgumentException", "data is null");
		return;
	}

	GIFImageDecoder *gifdecoder = (GIFImageDecoder *)env->GetIntField(obj, gGIFImageParser.mNativeContext);

	if(NULL == gifdecoder) {
		doThrow(env,"java/lang/IllegalStateException", "GIFImageParser was released");
		return;
	}

	len = env->GetArrayLength(byteArray);

	bytebuffer = (char*)env->GetByteArrayElements(byteArray,JNI_FALSE); //Not Copy
	if(NULL == bytebuffer) {
		doThrow(env,"java/lang/IllegalArgumentException", "data is invalid");
		return;
	}

	mbuffer = SharedBuffer::create();
	if(NULL == mbuffer)
	{
		env->ReleaseByteArrayElements(byteArray,(jbyte*)bytebuffer,0);
		doThrow(env,"java/lang/OutOfMemoryError", "The buffer decoded can't be created");
		ALOGE("memory allocate fail");
		return;
	}

	mbuffer->ref();
	/*for 20080103 version ~{*/
	/* Although setData() passes the pointer, its member RefPtr<SharedBuffer> catches the SharedBuffer.
	When setData() run, SharedBuffer.deref() is invoked that decrease ref count and delete itself if conut equals zero. When RefPtr<SharedBuffer> is freed,  SharedBuffer.deref() is also invoken. (if(--mRefs<=0) delete this)
	So, We don't need to free SharedBuffer we generates after passing it to decoder. see Shared.h
	*/
	mbuffer->clear();
	mbuffer->append(bytebuffer,len*sizeof(char));
	gifdecoder->setData(mbuffer.get(),false);
	mbuffer->deref();

	env->ReleaseByteArrayElements(byteArray,(jbyte*)bytebuffer,0);
	//LOGW("Leave android_webkit_GIFImageParser_SetRawData");
}


/*20080303 Byron Lin }~*/


// getFrameCount()
//char[] getFrameAtIndex(int)
//Rect getFrameRectAtIndex(int)
//


static void
android_webkit_GIFImageParser_nativeBegin(JNIEnv *env, jobject obj)
{
	//AllocMemTrace();
    
    ImageSource::AlphaOption alphaOption = ImageSource::AlphaPremultiplied;
    ImageSource::GammaAndColorProfileOption gammaAndColorProfileOption = ImageSource::GammaAndColorProfileApplied;
    GIFImageDecoder *gifdecoder = new GIFImageDecoder(alphaOption, gammaAndColorProfileOption);
	//GIFImageDecoder *gifdecoder = new GIFImageDecoder(true, false);
#ifdef CFLAG_DEBUG	
	ALOGI("sizeof ImageDecoder=%d sizeof ImageFrame=%d",sizeof(ImageDecoder),sizeof(ImageFrame));
	ALOGI("gifdecoder new:0x%x", gifdecoder);
#endif
	env->SetIntField(obj,gGIFImageParser.mNativeContext,(int)gifdecoder);

	//ALOGI("android_webkit_GIFImageParser_nativeBegin -> obj:%x, fields.mNativeContext:%x, gifdecoder:%x", obj, gGIFImageParser.mNativeContext, gifdecoder);
}

static void
android_webkit_GIFImageParser_nativeRelease(JNIEnv *env, jobject obj)
{
	GIFImageDecoder* decoder = (GIFImageDecoder*) env->GetIntField(obj, gGIFImageParser.mNativeContext);
	if(NULL!=decoder)
	{
#ifdef CFLAG_DEBUG	
		ALOGI("gifdecoder delete:0x%x", decoder);
#endif
		delete decoder;
		decoder = NULL;
		env->SetIntField(obj,gGIFImageParser.mNativeContext,(int)decoder);

		//FreeMemTrace();
	} else {
		ALOGI("gifdecoder NULL");
	}
}

static JNINativeMethod GIFImageParserMethods[] = {
    {"getFrame", "(I)Landroid/graphics/Bitmap;", (void *)android_webkit_GIFImageParser_getFrame},
    {"setDataPath",         "(Ljava/lang/String;)V",  (void *)android_webkit_GIFImageParser_setDataPath},
    {"setRawData",          "([B)V",  (void *)android_webkit_GIFImageParser_setRawData},
    {"frameCount",         "()I",                      (void *)android_webkit_GIFImageParser_frameCount},
    {"frameRectAtIndex",   "(I)Landroid/graphics/Rect;",(void *)android_webkit_GIFImageParser_frameRectAtIndex},
    {"frameDurationAtIndex","(I)J",(void*)android_webkit_GIFImageParser_frameDurationAtIndex},
    {"nativeBegin",        "()V",                      (void *)android_webkit_GIFImageParser_nativeBegin},
    {"nativeRelease",      "()V",                      (void *)android_webkit_GIFImageParser_nativeRelease},

    // The following are deprecated
};


static jclass make_globalref(JNIEnv* env, const char classname[]) {
    jclass c = env->FindClass(classname);
    SkASSERT(c);
    return (jclass)env->NewGlobalRef(c);
}

#define hasException(x) hasException1(x, __FUNCTION__,__LINE__)

static bool hasException1(JNIEnv *env, const char *szfunction, const int nLine) {
    if (env->ExceptionCheck() != 0) {
        ALOGE("***Uncaught exception returned from Java call!:%s :%d\n", szfunction, nLine);
        env->ExceptionDescribe();
        ALOGE("***??***");
        return true;
    }
    return false;
}

static jobject createRect(JNIEnv* env, jint left, jint top, jint right, jint bottom)
{
	#if 0
    jobject obj = env->AllocObject(gRect_class);
    if (obj) {
        env->CallVoidMethod(obj, gRect_constructorMethodID,left, top, right, bottom);
        if (hasException(env)) {
			ALOGE("Error");
			obj = env->NewObject(gRect_class,gRect_constructorMethodID,left, top, right, bottom);
			if (hasException(env)) {
				ALOGE("Error2");
				obj = NULL;
			}
        }
    }
	#else
	jobject obj = env->NewObject(gRect_class,gRect_constructorMethodID,left, top, right, bottom);
	if (hasException(env)) {
    	obj = NULL;
		ALOGE("*** here");
    }
	#endif
    return obj;
}

static int register_android_graphics_Graphics(JNIEnv* env)
{
    gRect_class = make_globalref(env, "android/graphics/Rect");
	gRect_constructorMethodID = env->GetMethodID(gRect_class, "<init>"/*"Rect"*/, "(IIII)V");

    return 0;
}

int register_android_webkit_GIFImageParser(JNIEnv *env)
{
    const char* const kClassPathName = "com/htc/lib1/GIFImageParser";

    jclass clazz;
	//LOGV("Enter register_android_webkit_GIFImageParser");
    clazz = env->FindClass(kClassPathName);
     int retval = 0;


    if (clazz == NULL) {
        ALOGE("Can't find class %s", kClassPathName);
        return -1;
    }

    gGIFImageParser.mValue = env->GetFieldID(clazz, "mValue", "I");
    gGIFImageParser.mNativeContext = env->GetFieldID(clazz, "mNativeContext", "I");
    gGIFImageParser.mCachedBitmap = env->GetFieldID(clazz, "mCachedBitmap", "Landroid/graphics/Bitmap;");
    gGIFImageParser.setBitmapPixels =  env->GetMethodID(clazz, "setBitmapPixels", "([IIII)Landroid/graphics/Bitmap;");
    gGIFImageParser.preparedBitmap =  env->GetMethodID(clazz, "preparedBitmap", "(II)Landroid/graphics/Bitmap;");


/*
    return JavaRuntime::registerNativeMethods(env,"android/media/GIFImageParser", GIFImageParserMethods, NELEM(GIFImageParserMethods));
*/

	register_android_graphics_Graphics(env);
	retval = env->RegisterNatives(clazz, GIFImageParserMethods, sizeof(GIFImageParserMethods)/sizeof(GIFImageParserMethods[0]));

	//LOGV("Leave register_android_webkit_GIFImageParser");
	return retval;//jniRegisterNativeMethods(env,kClassPathName, GIFImageParserMethods, NELEM(GIFImageParserMethods));

}

extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("GetEnv failed!");
        return -1;
    }
    
    register_android_webkit_GIFImageParser(env);

    return JNI_VERSION_1_4;
}

}//end namespace android

