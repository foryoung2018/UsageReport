#include <stdio.h>
#include <string.h>
#include <android/log.h>

#include "engine2_jni.h"

#include "morpho_error.h"
#include "morpho_hyperlapse.h"
#include "morpho_hyperlapse_preprocess.h"
#include "morpho_get_image_size.h"
#include "morpho_image_data_ex.h"

#define LOG_TAG "debug"
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)

#define CAST_HANDLE( p ) (morpho_Hyperlapse_handler*)( (uintptr_t)p )
#define COLOR_FORMAT_I420 "YUV420_PLANAR"
#define COLOR_FORMAT_NV12 "YUV420_SEMIPLANAR"
#define COLOR_FORMAT_NV21 "YVU420_SEMIPLANAR"
typedef enum { false, true } bool;

typedef struct {
    int buffer_size;
    void *p_buffer;

    int buffer_size_pre;
    void *p_buffer_pre;

    int in_width, in_height;
    int out_width, out_height;
    //Decode frame width/height may have padding comparing with in_width/in_height
    int dec_width, dec_height;
    bool isQualColFmt;

    const void *p_data;

    const char *p_format;

    morpho_Hyperlapse lapse;
    morpho_HyperlapsePreprocess preprocess;
    
} morpho_Hyperlapse_handler;

JNIEXPORT jlong JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_initializePreprocessJni
( JNIEnv *jenv, jclass jclazz, jint jin_width, jint jin_height, jint jformat, jint jaccuracy )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = malloc( sizeof( morpho_Hyperlapse_handler ) );

    memset( handler, 0, sizeof( morpho_Hyperlapse_handler ) );

    {
        handler->in_width = jin_width;
        handler->in_height = jin_height;
    }

    {
    	if( jformat == 19 ) {
    		//yv12 COLOR_FormatYUV420Planar
    	    handler->p_format = COLOR_FORMAT_I420;
    	}
    	else if( jformat == 21 ) {
    		//nv12 COLOR_FormatYUV420SemiPlanar
    		handler->p_format = COLOR_FORMAT_NV12;
    	}
    	else if (jformat == 0x7FA30C04) {
    		//nv21
    	    handler->p_format = COLOR_FORMAT_NV21;
    	}
    	else {
    		//nv21
    		handler->p_format = COLOR_FORMAT_NV21;
    	}
    }

    handler->buffer_size_pre = morpho_HyperlapsePreprocess_getBufferSize( jin_width, jin_height, handler->p_format, jaccuracy );
    handler->p_buffer_pre = malloc( handler->buffer_size_pre );

    ret |= morpho_HyperlapsePreprocess_initialize( &handler->preprocess, handler->p_buffer_pre, handler->buffer_size_pre, jin_width, jin_height, 
            handler->p_format, jaccuracy );

    return (jlong)( (uintptr_t)handler );
}

JNIEXPORT jlong JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_initializePreprocessExJni
( JNIEnv *jenv, jclass jclazz, jint jin_width, jint jin_height, jint jdec_width, jint jdec_height, jint jformat, jint jaccuracy )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = malloc( sizeof( morpho_Hyperlapse_handler ) );

    memset( handler, 0, sizeof( morpho_Hyperlapse_handler ) );

    {
        handler->in_width = jin_width;
        handler->in_height = jin_height;
	handler->dec_width = jdec_width;
	handler->dec_height = jdec_height;
    }

    {
    	if( jformat == 19 ) {
    		//yv12 COLOR_FormatYUV420Planar
    	    handler->p_format = COLOR_FORMAT_I420;
    	}
    	else if( jformat == 21 ) {
    		//nv12 COLOR_FormatYUV420SemiPlanar
    		handler->p_format = COLOR_FORMAT_NV12;
    	}
    	else if (jformat == 0x7FA30C04) {
    		//nv21
    	    handler->p_format = COLOR_FORMAT_NV21;
    	}
    	else {
    		//nv21
    		handler->p_format = COLOR_FORMAT_NV21;
    	}
    }

    handler->buffer_size_pre = morpho_HyperlapsePreprocess_getBufferSize( jin_width, jin_height, handler->p_format, jaccuracy );
    handler->p_buffer_pre = malloc( handler->buffer_size_pre );

    ret |= morpho_HyperlapsePreprocess_initialize( &handler->preprocess, handler->p_buffer_pre, handler->buffer_size_pre, jin_width, jin_height, 
            handler->p_format, jaccuracy );

    return (jlong)( (uintptr_t)handler );
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_startPreprocessJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    if( handler->p_buffer_pre != NULL ) {
        ret |= morpho_HyperlapsePreprocess_start( &handler->preprocess );
    }

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_preprocessJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jobject jsrc, jint padding, jlong time )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    morpho_ImageData src_image;
    unsigned char *p_buffer = (*jenv)->GetDirectBufferAddress( jenv, jsrc );

    src_image.width = handler->in_width;
    src_image.height = handler->in_height;
	
    if( !strcmp( handler->p_format, COLOR_FORMAT_NV21 ) ) {
    	src_image.dat.semi_planar.y = p_buffer;
    	src_image.dat.semi_planar.uv = p_buffer + src_image.width * src_image.height + padding;
    } else if( !strcmp( handler->p_format, COLOR_FORMAT_I420 ) ) {
        int stride_y = ( src_image.width + 15 ) & ~15;
        int stride_uv = ( src_image.width / 2 + 15 ) & ~15;

    	src_image.dat.planar.y = p_buffer;
    	src_image.dat.planar.u = p_buffer + stride_y * src_image.height;
    	src_image.dat.planar.v = src_image.dat.planar.u + stride_uv * src_image.height / 2;
    } else if(!strcmp( handler->p_format, COLOR_FORMAT_NV12 )){
    	src_image.dat.semi_planar.y = p_buffer;
    	src_image.dat.semi_planar.uv = p_buffer + src_image.width * src_image.height;
    }

    ret |= morpho_HyperlapsePreprocess_preprocess( &handler->preprocess, &src_image );

	return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_preprocessExJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jobject jsrc, jlong time )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    morpho_ImageDataEx src_image;
    int dummy_size;
    int dummy_height;
    //Dec height should be larger than in height
    dummy_height = handler->dec_height - handler->in_height;
    dummy_size = handler->dec_width * dummy_height;
	
    unsigned char *p_buffer = (*jenv)->GetDirectBufferAddress( jenv, jsrc );

    src_image.width = handler->in_width;
    src_image.height = handler->in_height;
	
    //TODO: Could combine NV21 with NV12? Remove previous padding solution in Ex case 
    if( !strcmp( handler->p_format, COLOR_FORMAT_NV21 ) || !strcmp( handler->p_format, COLOR_FORMAT_NV12 )) {
    	src_image.dat.semi_planar.y = p_buffer;
    	src_image.dat.semi_planar.uv = p_buffer + src_image.width * src_image.height + dummy_size;
	src_image.pitch.semi_planar.y = handler->dec_width;
        src_image.pitch.semi_planar.uv = handler->dec_width;
    } else if( !strcmp( handler->p_format, COLOR_FORMAT_I420 ) ) {
        int stride_y = ( src_image.width + 15 ) & ~15;
        int stride_uv = ( src_image.width / 2 + 15 ) & ~15;

    	src_image.dat.planar.y = p_buffer;
    	src_image.dat.planar.u = p_buffer + stride_y * src_image.height + dummy_size;
    	src_image.dat.planar.v = src_image.dat.planar.u + stride_uv * src_image.height / 2;
	src_image.pitch.planar.y = handler->dec_width;
        src_image.pitch.planar.u = handler->dec_width;
        src_image.pitch.planar.v = handler->dec_width;
    }

    ret |= morpho_HyperlapsePreprocess_preprocessEx( &handler->preprocess, &src_image );

	return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_getDataSizeJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    int size = 0;

    ret |= morpho_HyperlapsePreprocess_getDataSize( &handler->preprocess, &size, 0);

    return size;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_getDataJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jobject jdata )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    void *p_data = (*jenv)->GetDirectBufferAddress( jenv, jdata );

    ret |= morpho_HyperlapsePreprocess_getData( &handler->preprocess, p_data );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_finishPreprocessJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    if( handler->p_buffer_pre != NULL ) {
        ret |= morpho_HyperlapsePreprocess_finalize( &handler->preprocess );
        free( handler->p_buffer_pre );
    }
    free( handler );

    return ret;
}


JNIEXPORT jlong JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_initializeJni
( JNIEnv *jenv, jclass jclazz, jobject jdata, jint jin_width, jint jin_height, jint jout_width, jint jout_height, jint jformat, jint jaccuracy )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = malloc( sizeof( morpho_Hyperlapse_handler ) );

    memset( handler, 0, sizeof( morpho_Hyperlapse_handler ) );

    {
        handler->in_width = jin_width;
        handler->in_height = jin_height;
        handler->out_width = jout_width;
        handler->out_height = jout_height;
    }

    {
        if( jformat == 19 ) {
        	//yv12 COLOR_FormatYUV420Planar
        	handler->p_format = COLOR_FORMAT_I420;
        }
        else if( jformat == 21 ) {
            //nv12 COLOR_FormatYUV420SemiPlanar
            handler->p_format = COLOR_FORMAT_NV12;
        }
        else if (jformat == 0x7FA30C04) {		
          	//nv21
           	handler->p_format = COLOR_FORMAT_NV21;
        }
        else {
           	//nv21
          	handler->p_format = COLOR_FORMAT_NV21;
        }
    }

    {
        const void *p_data = (*jenv)->GetDirectBufferAddress( jenv, jdata );

        handler->p_data = p_data;

        handler->buffer_size = morpho_Hyperlapse_getBufferSize( p_data );
        handler->p_buffer = malloc( handler->buffer_size );

        ret |= morpho_Hyperlapse_initialize( &handler->lapse, handler->p_buffer, handler->buffer_size, p_data,
                                                 jin_width, jin_height, jout_width, jout_height, handler->p_format );
    }

    return (jlong)( (uintptr_t)handler );
}

JNIEXPORT jlong JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_initializeExJni
( JNIEnv *jenv, jclass jclazz, jobject jdata, jint jin_width, jint jin_height, jint jout_width, jint jout_height, jint jdec_width, jint jdec_height,  jint jformat, jint jaccuracy )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = malloc( sizeof( morpho_Hyperlapse_handler ) );

    memset( handler, 0, sizeof( morpho_Hyperlapse_handler ) );

    {
        handler->in_width = jin_width;
        handler->in_height = jin_height;
        handler->out_width = jout_width;
        handler->out_height = jout_height;
	handler->dec_width = jdec_width;
        handler->dec_height = jdec_height;
    }

    //Init is Qualcomm color format flag as false
    handler->isQualColFmt = false;

    {
        if( jformat == 19 ) {
        	//yv12 COLOR_FormatYUV420Planar
        	handler->p_format = COLOR_FORMAT_I420;
        }
        else if( jformat == 21 ) {
            //nv12 COLOR_FormatYUV420SemiPlanar
            handler->p_format = COLOR_FORMAT_NV12;
        }
        else if (jformat == 0x7FA30C04) {
		handler->isQualColFmt = true;
          	//nv21
           	handler->p_format = COLOR_FORMAT_NV21;
        }
        else {
           	//nv21
          	handler->p_format = COLOR_FORMAT_NV21;
        }
    }

    {
        const void *p_data = (*jenv)->GetDirectBufferAddress( jenv, jdata );

        handler->p_data = p_data;

        handler->buffer_size = morpho_Hyperlapse_getBufferSize( p_data );
        handler->p_buffer = malloc( handler->buffer_size );

        ret |= morpho_Hyperlapse_initialize( &handler->lapse, handler->p_buffer, handler->buffer_size, p_data,
                                                 jin_width, jin_height, jout_width, jout_height, handler->p_format );
    }

    return (jlong)( (uintptr_t)handler );
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_startJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    if( handler->p_buffer != NULL ) {
        ret |= morpho_Hyperlapse_start( &handler->lapse);
    }

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_getFrameNumJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    int num = 0;

    ret |= morpho_Hyperlapse_getFrameNum( &handler->lapse, &num );

    return num;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_getCroppingSizeJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jintArray jsize )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    int *p_size = (*jenv)->GetIntArrayElements( jenv, jsize, NULL );

    ret |= morpho_Hyperlapse_getCroppingSize( &handler->lapse, p_size, p_size + 1 );

    (*jenv)->ReleaseIntArrayElements( jenv, jsize, p_size, 0 );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_getMatrixJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jfloatArray jmatrix, jint index )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    float *p_matrix = (*jenv)->GetFloatArrayElements( jenv, jmatrix, NULL );

    ret |= morpho_Hyperlapse_processWithIndex( &handler->lapse, NULL, NULL, index );
    ret |= morpho_Hyperlapse_getMatrix( &handler->lapse, p_matrix );

    (*jenv)->ReleaseFloatArrayElements( jenv, jmatrix, p_matrix, 0 );


    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_getMatrixExJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jfloatArray jmatrix, jint index )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    float *p_matrix = (*jenv)->GetFloatArrayElements( jenv, jmatrix, NULL );

    ret |= morpho_Hyperlapse_processWithIndexEx( &handler->lapse, NULL, NULL, index );
    ret |= morpho_Hyperlapse_getMatrix( &handler->lapse, p_matrix );

    (*jenv)->ReleaseFloatArrayElements( jenv, jmatrix, p_matrix, 0 );


    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_processJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jobject jsrc, jobject jdst, jint index, jint padding)
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    morpho_ImageData src_image;
    morpho_ImageData dst_image;

    unsigned char *p_src = (*jenv)->GetDirectBufferAddress( jenv, jsrc );
    unsigned char *p_dst = (*jenv)->GetDirectBufferAddress( jenv, jdst );

    src_image.width = handler->in_width;
	src_image.height = handler->in_height;
	LOGI("Engine2_processJni input width=%d, height=%d", src_image.width, src_image.height);
	if (!strcmp(handler->p_format, COLOR_FORMAT_NV21)) {
		src_image.dat.semi_planar.y = p_src;
		src_image.dat.semi_planar.uv = p_src + src_image.width * src_image.height + padding;
	} else if (!strcmp(handler->p_format, COLOR_FORMAT_I420)) {
		int stride_y = (src_image.width + 15) & ~15;
		int stride_uv = (src_image.width / 2 + 15) & ~15;
		src_image.dat.planar.y = p_src;
		src_image.dat.planar.u = p_src + stride_y * src_image.height;
		src_image.dat.planar.v = src_image.dat.planar.u + stride_uv * src_image.height / 2;
	} else if(!strcmp(handler->p_format, COLOR_FORMAT_NV12)){
		src_image.dat.semi_planar.y = p_src;
		src_image.dat.semi_planar.uv = p_src + src_image.width * src_image.height;
	}

	LOGI("+++++++allocateImageData");
	int out_size = 0;
	int allocate_result = allocateImageData(&dst_image, handler->in_width, handler->in_height, handler->p_format, &out_size);
	LOGI("-------allocateImageData result:%d, size:%d", allocate_result, out_size);
	LOGI("Engine2_processJni output width=%d, height=%d, format=%s", dst_image.width, dst_image.height, handler->p_format);

	LOGI("+++++++morpho_Hyperlapse_setOutputImageSize");
	int err = morpho_Hyperlapse_setOutputImageSize(&handler->lapse, handler->in_width, handler->in_height);
	LOGI("-------morpho_Hyperlapse_setOutputImageSize result:%d", err);
	LOGI("+++++++processWithIndex");
	ret |= morpho_Hyperlapse_processWithIndex(&handler->lapse, &dst_image, &src_image, index);
	memcpy(p_dst, dst_image.dat.p, out_size);
	finalizeImageData(&dst_image);
	ret = out_size;
	LOGI("-------processWithIndex");

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_processExJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jobject jsrc, jobject jdst, jint index)
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    morpho_ImageDataEx src_image;
    morpho_ImageDataEx dst_image;

    int dummy_size;
    int dummy_height;
    //Dec height should be larger than in height
    dummy_height = handler->dec_height - handler->in_height;
    dummy_size = handler->dec_width * dummy_height;

    unsigned char *p_src = (*jenv)->GetDirectBufferAddress( jenv, jsrc );
    unsigned char *p_dst = (*jenv)->GetDirectBufferAddress( jenv, jdst );

    	src_image.width = handler->in_width;
	src_image.height = handler->in_height;
	LOGI("Engine2_processExJni input width=%d, height=%d", src_image.width, src_image.height);
	if (!strcmp(handler->p_format, COLOR_FORMAT_NV21) || !strcmp(handler->p_format, COLOR_FORMAT_NV12)) {
		src_image.dat.semi_planar.y = p_src;
		src_image.dat.semi_planar.uv = p_src + src_image.width * src_image.height + dummy_size;
		src_image.pitch.semi_planar.y = handler->dec_width;
	        src_image.pitch.semi_planar.uv = handler->dec_width;
	} else if (!strcmp(handler->p_format, COLOR_FORMAT_I420)) {
		int stride_y = (src_image.width + 15) & ~15;
		int stride_uv = (src_image.width / 2 + 15) & ~15;
		src_image.dat.planar.y = p_src;
		src_image.dat.planar.u = p_src + stride_y * src_image.height + dummy_size;
		src_image.dat.planar.v = src_image.dat.planar.u + stride_uv * src_image.height / 2;
		src_image.pitch.planar.y = handler->dec_width;
	        src_image.pitch.planar.u = handler->dec_width;
	        src_image.pitch.planar.v = handler->dec_width;
	}

	LOGI("+++++++allocateImageDataEx");
	int out_size = 0;
	int allocate_result = allocateImageDataEx(&dst_image, handler->in_width, handler->in_height, handler->dec_width, dummy_height, handler->p_format, &out_size);
	LOGI("-------allocateImageDataEx result:%d, size:%d", allocate_result, out_size);
	LOGI("Engine2_processExJni output width=%d, height=%d, format=%s", handler->in_width, handler->in_height, handler->p_format);

	LOGI("+++++++morpho_Hyperlapse_setOutputImageSize");
	int err = morpho_Hyperlapse_setOutputImageSize(&handler->lapse, handler->in_width, handler->in_height);
	LOGI("-------morpho_Hyperlapse_setOutputImageSize result:%d", err);
	LOGI("+++++++processWithIndex");
	ret |= morpho_Hyperlapse_processWithIndexEx(&handler->lapse, &dst_image, &src_image, index);

	if((false == handler->isQualColFmt)){
	  //function to remove pitch & copy to p_dst
	  _remove_pitch(p_dst, handler->in_width, &dst_image, handler->p_format);
	} else {	
	  //return pitch output if Qualcomm color format
	  memcpy(p_dst, dst_image.dat.p, out_size);
	}

	finalizeImageDataEx(&dst_image);
	ret = out_size;
	LOGI("-------processWithIndex");

    return ret;
}

int _remove_pitch(unsigned char *p_dst, int dstWidth, const morpho_ImageDataEx *p_src, const char *p_format){
    int i;
    if( !strcmp(p_format, COLOR_FORMAT_NV21) || !strcmp(p_format, COLOR_FORMAT_NV12) ){
        char *ps_y;
        char *ps_uv;

        ps_y = (char *)(p_src->dat.semi_planar.y);
        for(i=0; i<p_src->height; i++){
            memcpy(p_dst, ps_y, p_src->width);
            ps_y += p_src->pitch.semi_planar.y;
            p_dst += dstWidth;
        }

        ps_uv = (char *)(p_src->dat.semi_planar.uv);
        for(i=0; i<(p_src->height/2); i++){
            memcpy(p_dst, ps_uv, p_src->width);
            ps_uv += p_src->pitch.semi_planar.uv;
            p_dst += dstWidth;
        }
    }else{
        return -1;
    }

    return 0;
}

int allocateImageDataEx( morpho_ImageDataEx *p_image, int width, int height, int pitch_width, int dummy_height, const char *p_format, int *size)
{
    int size_all;
    int size_y;
    int dummy_size;

    p_image->width = width;
    p_image->height = height;
    dummy_size = pitch_width * dummy_height;

    if( !strcmp(p_format, COLOR_FORMAT_NV21) || !strcmp(p_format, COLOR_FORMAT_NV12) ){
        p_image->pitch.semi_planar.y = pitch_width;
        p_image->pitch.semi_planar.uv = pitch_width;
    }else if( !strcmp(p_format, COLOR_FORMAT_I420) ){
        p_image->pitch.planar.y = pitch_width;
        p_image->pitch.planar.u = pitch_width;
        p_image->pitch.planar.v = pitch_width;
    }else{
        return MORPHO_ERROR_UNSUPPORTED;
    }

    size_all = morpho_getImageSize( pitch_width, p_image->height, p_format ) + pitch_width * dummy_height;
    LOGI("allocateImageDataEx size_all=%d", size_all);
    size_y = morpho_getImageSizeY( pitch_width, p_image->height, p_format );
    LOGI("allocateImageDataEx size_y=%d", size_y);

    p_image->dat.p = malloc( size_all );
    if (!p_image->dat.p) {
        return MORPHO_ERROR_MALLOC;
    }

    if ( size_y > 0 ) {
        int size_u = morpho_getImageSizeU( p_image->width, p_image->height, p_format );
        LOGI("allocateImageDataEx size_u=%d", size_u);
        int size_v = morpho_getImageSizeV( p_image->width, p_image->height, p_format );
        LOGI("allocateImageDataEx size_v=%d", size_v);
        int size_uv = morpho_getImageSizeUV( p_image->width, p_image->height, p_format );
        LOGI("allocateImageDataEx size_uv=%d", size_uv);
        if ( size_u > 0 ) {
            p_image->dat.planar.u = (unsigned char*)p_image->dat.p + size_y + dummy_size;
            p_image->dat.planar.v = (unsigned char*)p_image->dat.planar.u + size_u;
        } else if ( size_uv > 0 ) {
            p_image->dat.semi_planar.uv = (unsigned char*)p_image->dat.p + size_y + dummy_size;
        }
    }    
    *size = size_all;
    return MORPHO_OK;
}

int finalizeImageDataEx( morpho_ImageDataEx *p_image )
{
    int ret = MORPHO_OK;

    free( p_image->dat.p );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_finishJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    if( handler->p_buffer != NULL ) {
        ret |= morpho_Hyperlapse_finalize( &handler->lapse );
        free( handler->p_buffer );
    }

    free( handler );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_setModeJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jint jmode )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    ret |= morpho_HyperlapsePreprocess_setMode( &handler->preprocess, jmode );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_activateOisModeJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jint jon )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    ret |= morpho_HyperlapsePreprocess_activateOisMode( &handler->preprocess, jon );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_setGyroTimeLagJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jlong jlag )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    ret |= morpho_HyperlapsePreprocess_setGyroTimeLag( &handler->preprocess, jlag );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_setVerticalViewAngleJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jdouble jangle )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    ret |= morpho_HyperlapsePreprocess_setViewAngle( &handler->preprocess, MORPHO_HYPERLAPSE_ANGLE_TYPE_VERTICAL, jangle );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_setRollingShutterCoeffJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jint jcoeff , jint jangle)
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );
    LOGI("setRollingShutterCoeff angle:%d", jangle);
    if( handler->p_buffer != NULL ) {
    	if (jangle == 0 || jangle == 180) {
    		ret |= morpho_Hyperlapse_setRollingShutterCoeff( &handler->lapse, jcoeff, MORPHO_HYPERLAPSE_SCANLINE_ORIENTATION_HORIZONTAL );
    	} else {
    		ret |= morpho_Hyperlapse_setRollingShutterCoeff( &handler->lapse, jcoeff, MORPHO_HYPERLAPSE_SCANLINE_ORIENTATION_VERTICAL );
    	}
    }
    if( handler->p_buffer_pre != NULL ) {
    	if (jangle == 0 || jangle == 180) {
    		ret |= morpho_HyperlapsePreprocess_setRollingShutterCoeff( &handler->preprocess, jcoeff, MORPHO_HYPERLAPSE_SCANLINE_ORIENTATION_HORIZONTAL );
    	} else {
    		ret |= morpho_HyperlapsePreprocess_setRollingShutterCoeff( &handler->preprocess, jcoeff, MORPHO_HYPERLAPSE_SCANLINE_ORIENTATION_VERTICAL );
    	}
    }

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_setUnreliableLevelJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jint jlevel )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    ret |= morpho_HyperlapsePreprocess_setUnreliableLevel( &handler->preprocess, jlevel );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_setNoMovementLevelJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jint jlevel )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    ret |= morpho_HyperlapsePreprocess_setNoMovementLevel( &handler->preprocess, jlevel );

    return ret;
}


JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_setFixLevelJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jint jlevel )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    ret |= morpho_Hyperlapse_setFixLevel( &handler->lapse, jlevel );

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_addGyroDataJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jdouble jrot_x, jdouble jrot_y, jdouble jrot_z, jlong jtime )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    if( handler->p_buffer_pre != NULL ) {
        ret |= morpho_HyperlapsePreprocess_addGyroData( &handler->preprocess, jrot_x, jrot_y, jrot_z, jtime );
    }

    return ret;
}

JNIEXPORT jint JNICALL Java_com_morphoinc_app_hyperlapse_engine_Engine2_addAccelerometerDataJni
( JNIEnv *jenv, jclass jclazz, jlong jhandler, jdouble jacc_x, jdouble jacc_y, jdouble jacc_z, jlong jtime )
{
    int ret = MORPHO_OK;
    morpho_Hyperlapse_handler *handler = CAST_HANDLE( jhandler );

    if( handler->p_buffer_pre != NULL ) {
        ret |= morpho_HyperlapsePreprocess_addAccelerometerData( &handler->preprocess, jacc_x, jacc_y, jacc_z, jtime );
    }

    return ret;
}

int allocateImageData( morpho_ImageData *p_image, int width, int height, const char *p_format, int *size)
{
    int size_all;
    int size_y;

    p_image->width = width;
    p_image->height = height;
    size_all = morpho_getImageSize( p_image->width, p_image->height, p_format );
    LOGI("allocateImageData size_all=%d", size_all);
    size_y = morpho_getImageSizeY( p_image->width, p_image->height, p_format );
    LOGI("allocateImageData size_y=%d", size_y);

    p_image->dat.p = malloc( size_all );
    if (!p_image->dat.p) {
        return MORPHO_ERROR_MALLOC;
    }

    if ( size_y > 0 ) {
        int size_u = morpho_getImageSizeU( p_image->width, p_image->height, p_format );
        LOGI("allocateImageData size_u=%d", size_u);
        int size_v = morpho_getImageSizeV( p_image->width, p_image->height, p_format );
        LOGI("allocateImageData size_v=%d", size_v);
        int size_uv = morpho_getImageSizeUV( p_image->width, p_image->height, p_format );
        LOGI("allocateImageData size_uv=%d", size_uv);
        if ( size_u > 0 ) {
            p_image->dat.planar.u = (unsigned char*)p_image->dat.p + size_y;
            p_image->dat.planar.v = (unsigned char*)p_image->dat.planar.u + size_u;
        } else if ( size_uv > 0 ) {
            p_image->dat.semi_planar.uv = (unsigned char*)p_image->dat.p + size_y;
        }
    }
    *size = size_all;
    return MORPHO_OK;
}

int finalizeImageData( morpho_ImageData *p_image )
{
    int ret = MORPHO_OK;

    free( p_image->dat.p );

    return ret;
}
