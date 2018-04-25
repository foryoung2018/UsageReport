/**
 * @file    morpho_hyperlapse.h
 * @brief   morpho Hyperlapse engine
 * @par Copyright
 * Copyright(c) 2014 Morpho,Inc.
 */
#ifndef MORPHO_HYPERLAPSE_H
#define MORPHO_HYPERLAPSE_H

#ifdef __cplusplus
extern "C" {
#endif

#include "morpho_api.h"
#include "morpho_image_data.h"
#include "morpho_image_data_ex.h"

#include "morpho_quadr_int.h"

enum {
    MORPHO_HYPERLAPSE_MODE_SOFT,
    MORPHO_HYPERLAPSE_MODE_HYBRID,
    MORPHO_HYPERLAPSE_MODE_HARD,

    MORPHO_HYPERLAPSE_MODE_NUM
};

enum {
    MORPHO_HYPERLAPSE_FAILURE_NO_ERROR       = 0,      /**< No error. */
    MORPHO_HYPERLAPSE_FAILURE_OUT_OF_RANGE   = (1<<0), /**< Too large camera motion to be stabilized. */
    MORPHO_HYPERLAPSE_FAILURE_UNRELIABLE     = (1<<1), /**< Unreliable motion detection. */
    MORPHO_HYPERLAPSE_FAILURE_GENERIC        = (1<<2), /**< Other undesirable situations. */
};

enum {
    MORPHO_HYPERLAPSE_ACCURACY_HIGH,
    MORPHO_HYPERLAPSE_ACCURACY_MIDDLE,
    MORPHO_HYPERLAPSE_ACCURACY_LOW,

    MORPHO_HYPERLAPSE_ACCURACY_NUM,
};

enum {
    MORPHO_HYPERLAPSE_ANGLE_TYPE_HORIZONTAL,
    MORPHO_HYPERLAPSE_ANGLE_TYPE_VERTICAL,
    MORPHO_HYPERLAPSE_ANGLE_TYPE_DIAGONAL,

    MORPHO_HYPERLAPSE_ANGLE_TYPE_NUM,
};

enum {
    MORPHO_HYPERLAPSE_SCANLINE_ORIENTATION_HORIZONTAL,
    MORPHO_HYPERLAPSE_SCANLINE_ORIENTATION_VERTICAL,

    MORPHO_HYPERLAPSE_SCANLINE_ORIENTATION_NUM,
};

#define MORPHO_HYPERLAPSE_VER "Morpho Hyperlapse Ver.1.0.0 2015/10/06 HTC3 32bit"


/**
 * Hyperlapse.
 */
typedef struct {
    void *p; /**< The pointer for internal objects. */

} morpho_Hyperlapse;

/**
 * @brief
 * Returns the engine version.
 *
 * @return The version.
 */
MORPHO_API(const char *)
morpho_Hyperlapse_getVersion( void );

/**
 * @brief
 * Returns the working buffer size for the engine.
 *
 * @param[in] p_data
 *
 * @return The buffer size.
 */
MORPHO_API(int)
morpho_Hyperlapse_getBufferSize( const void * const p_data );

/**
 * @brief
 * Initializes the engine.
 *
 * With negative values for the cropping size, the engine internally calculates the best size.
 * This can be obtained by using morpho_Hyperlapse_getCroppingSize().
 *
 * @param[in,out] p_lapse
 * @param[in,out] p_buffer
 * @param[in]     buffer_size
 * @param[in]     p_data      The data from the HyperlapsePreprocess engine.
 * @param[in]     in_width    Input width.
 * @param[in]     in_height   Input height.
 * @param[in]     crop_width  Cropping width.
 * @param[in]     crop_height Cropping height.
 * @param[in]     p_format    Image format.
 *  @arg "YUV420_SEMIPLANAR"
 *  @arg "YVU420_SEMIPLANAR"
 *  @arg "YUV420_PLANAR"
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_initialize( morpho_Hyperlapse * const p_lapse, void *p_buffer, const int buffer_size, const void * const p_data,
                                  const int in_width, const int in_height, const int crop_width, const int crop_height, const char *p_format );

/**
 * @brief
 * Finalizes the engine.
 *
 * @param[in,out] p_lapse
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_finalize( morpho_Hyperlapse * const p_lapse );

/**
 * @brief
 * Sets the size of the output image.
 *
 * The engine automatically scales the output image, corresponding to the size set here.
 * When this function is not called, the engine expects that the output is the same size as the cropping size.
 *
 * @param[in,out] p_lapse
 * @param[in]     width  Output width.
 * @param[in]     height Output height.
 *
 * @return Error codes
 */
MORPHO_API(int)
morpho_Hyperlapse_setOutputImageSize( morpho_Hyperlapse * const p_lapse, const int width, const int height );

/**
 * @brief
 * Specifies the rolling shutter coefficient and the scanline orientation.
 *
 * @param[in,out] p_lapse
 * @param[in]     coeff   [-100,100]
 * @param[in]     orien
 *  @arg MORPHO_HYPERLAPSE_SCANLINE_ORIENTATION_HORIZONTAL
 *  @arg MORPHO_HYPERLAPSE_SCANLINE_ORIENTATION_VERTICAL
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_setRollingShutterCoeff( morpho_Hyperlapse * const p_lapse, const int coeff, const int orien );

/**
 * @brief
 * Specifies the indices of input frames which will be inputted to the engine.
 *
 * @note
 * Must be called for using morpho_Hyperlapse_process() and
 * must not for morpho_Hyperlapse_processWithIndex().
 *
 * @param[in,out] p_lapse
 * @param[in]     start
 * @param[in]     end
 * @param[in]     skip
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_setProcessingIndices( morpho_Hyperlapse * const p_lapse, const int start, const int end, const int skip );

/**
 * @brief
 * Sets the fix level.
 *
 * @param[in,out] p_lapse
 * @param[in]     level
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_setFixLevel( morpho_Hyperlapse * const p_lapse, const int level );

/**
 * @brief
 * Gets the number of valid frames.
 *
 * @param[in]  p_lapse
 * @param[out] p_num
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_getFrameNum( const morpho_Hyperlapse * const p_lapse, int * const p_num );

MORPHO_API(int)
morpho_Hyperlapse_getFrameIndex( const morpho_Hyperlapse * const p_lapse, int * const p_index, const long long timestamp );

/**
 * @brief
 * Starts the engine.
 *
 * @param[in,out] p_lapse
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_start( morpho_Hyperlapse * const p_lapse );

/**
 * @brief
 * Gets the automatically calculated cropping size.
 *
 * @param[in,out] p_lapse
 * @param[out]    p_width
 * @param[out]    p_height
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_getCroppingSize( const morpho_Hyperlapse * const p_lapse, int * const p_width, int * const p_height );

/**
 * @brief
 * Processes an input frame.
 *
 * Input frames must be fed sequentially as specified by the function morpho_Hyperlapse_setProcessingIndices().
 *
 * @param[in,out] p_lapse
 * @param[out]    p_dst_img
 * @param[in]     p_src_img
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_process( morpho_Hyperlapse * const p_lapse, morpho_ImageData * const p_dst_img, const morpho_ImageData * const p_src_img );

/**
 * @brief
 * Processes an input frame which has padding data.
 *
 * Input frames must be fed sequentially as specified by the function morpho_Hyperlapse_setProcessingIndices().
 *
 * @param[in,out] p_lapse
 * @param[out]    p_dst_img
 * @param[in]     p_src_img
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_processEx( morpho_Hyperlapse * const p_lapse, morpho_ImageDataEx * const p_dst_img, const morpho_ImageDataEx * const p_src_img );

/**
 * @brief
 * Processes an input frame.
 *
 * @param[in,out] p_lapse
 * @param[out]    p_dst_img
 * @param[in]     p_src_img
 * @param[in]     index
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_processWithIndex( morpho_Hyperlapse * const p_lapse, morpho_ImageData * const p_dst_img, const morpho_ImageData * p_src_img, const int index );

/**
 * @brief
 * Processes an input frame which has padding data.
 *
 * @param[in,out] p_lapse
 * @param[out]    p_dst_img
 * @param[in]     p_src_img
 * @param[in]     index
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_processWithIndexEx( morpho_Hyperlapse * const p_lapse, morpho_ImageDataEx * const p_dst_img, const morpho_ImageDataEx * p_src_img, const int index );

/**
 * @brief
 * Returns the failure code of the most recent process.
 *
 * The code represents the disjunction of all applicable failure codes.
 *
 * @param[in,out] p_lapse
 * @param[out]    p_fcode
 *  @arg @ref MORPHO_HYPERLAPSE_FAILURE_NO_ERROR
 *  @arg @ref MORPHO_HYPERLAPSE_FAILURE_OUT_OF_RANGE
 *  @arg @ref MORPHO_HYPERLAPSE_FAILURE_GENERIC
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_Hyperlapse_getFcode( morpho_Hyperlapse * const p_lapse, int * const p_fcode );


/* internal */

MORPHO_API(int)
morpho_Hyperlapse_getCroppingQuadr( const morpho_Hyperlapse * const p_lapse, morpho_QuadrInt * const p_quadr );

MORPHO_API(int)
morpho_Hyperlapse_getMatrix( const morpho_Hyperlapse * const p_lapse, float * const p_matrix );

MORPHO_API(int)
morpho_Hyperlapse_getMatrixInvY( const morpho_Hyperlapse * const p_lapse, float * const p_matrix );

#ifdef __cplusplus
}
#endif

#endif /* MORPHO_HYPERLAPSE_H */
