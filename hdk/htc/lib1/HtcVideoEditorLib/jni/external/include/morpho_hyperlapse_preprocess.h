/**
 * @file    morpho_hyperlapse_preprocess.h
 * @brief   morpho Hyperlapse preprocess
 * @par Copyright
 * Copyright(c) 2014 Morpho,Inc.
 */
#ifndef MORPHO_HYPERLAPSE_PREPROCESS_H
#define MORPHO_HYPERLAPSE_PREPROCESS_H

#ifdef __cplusplus
extern "C" {
#endif

#include "morpho_hyperlapse.h"

typedef struct {
    void *p; /**< The pointer for internal objects. */

} morpho_HyperlapsePreprocess;


/**
 * @brief
 * Returns the working buffer size required by the engine with the specified parameters.
 *
 * @param[in]     width    Input width.
 * @param[in]     height   Input height.
 * @param[in]     p_format Image format.
 *  @arg "YUV420_SEMIPLANAR"
 *  @arg "YVU420_SEMIPLANAR"
 *  @arg "YUV420_PLANAR"
 *
 * @param[in] accuracy Motion detection accuracy.
 *  @arg @ref MORPHO_HYPERLAPSE_ACCURACY_HIGH,
 *  @arg @ref MORPHO_HYPERLAPSE_ACCURACY_MIDDLE,
 *  @arg @ref MORPHO_HYPERLAPSE_ACCURACY_LOW,
 * 
 * @return The buffer size.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_getBufferSize( const int width, const int height, const char *p_format, const int accuracy );

/**
 * @brief
 * Initializes the engine.
 *
 * @param[in,out] p_lapse
 * @param[in,out] p_buffer
 * @param[in]     buffer_size
 * @param[in]     width       Input width.
 * @param[in]     height      Input height.
 * @param[in]     p_format    Image format.
 *  @arg "YUV420_SEMIPLANAR"
 *  @arg "YVU420_SEMIPLANAR"
 *  @arg "YUV420_PLANAR"
 *
 * @param[in] accuracy Motion detection accuracy.
 *  @arg @ref MORPHO_HYPERLAPSE_ACCURACY_HIGH,
 *  @arg @ref MORPHO_HYPERLAPSE_ACCURACY_MIDDLE,
 *  @arg @ref MORPHO_HYPERLAPSE_ACCURACY_LOW,
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_initialize( morpho_HyperlapsePreprocess * const p_lapse, void *p_buffer, const int buffer_size, 
                                        const int width, const int height, const char *p_format, const int accuracy );

/**
 * @brief
 * Finalizes the engine.
 *
 * @param[in,out] p_lapse
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_finalize( morpho_HyperlapsePreprocess * const p_lapse );

/**
 * @brief
 * Sets the motion detection mode.
 *
 * @param[in,out] p_lapse
 * @param[in]     mode
 *  @arg @ref MORPHO_HYPERLAPSE_MODE_SOFT
 *  @arg @ref MORPHO_HYPERLAPSE_MODE_HYBRID
 *  @arg @ref MORPHO_HYPERLAPSE_MODE_HARD
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_setMode( morpho_HyperlapsePreprocess * const p_lapse, const int mode );

/**
 * @brief
 * Activates the OIS mode.
 *
 * @param[in,out] p_lapse
 * @param[in]     on
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_activateOisMode( morpho_HyperlapsePreprocess * const p_lapse, const int on );

/**
 * @brief
 * Specifies the frame rate of the input video.
 *
 * @param[in,out] p_lapse
 * @param[in]     fps
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_setFrameRate( morpho_HyperlapsePreprocess * const p_lapse, const int fps );

/**
 * @brief
 * Specifies the view angle of the camera.
 *
 * @param[in,out] p_lapse
 * @param[in]     angle_type
 *  @arg @ref MORPHO_HYPERLAPSE_ANGLE_TYPE_HORIZONTAL
 *  @arg @ref MORPHO_HYPERLAPSE_ANGLE_TYPE_VERTICAL
 *  @arg @ref MORPHO_HYPERLAPSE_ANGLE_TYPE_DIAGONAL
 *
 * @param[in]     angle
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_setViewAngle( morpho_HyperlapsePreprocess * const p_lapse, const int angle_type, const double angle );

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
morpho_HyperlapsePreprocess_setRollingShutterCoeff( morpho_HyperlapsePreprocess * const p_lapse, const int coeff, const int orien );

/**
 * @brief
 * Specifies the time-lag between gyroscope and image data.
 *
 * @param[in,out] p_lapse
 * @param[in]     lag     [nsec]
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_setGyroTimeLag( morpho_HyperlapsePreprocess * const p_lapse, const long long lag );

/**
 * @brief
 * Sets the unreliable level.
 *
 * The engine discards unreliable results of motion detection.
 * This function sets the threshold for this decision.
 *
 * Larger levels for more frequent unreliable decisions.
 *
 * @param[in,out] p_lapse
 * @param[in]     level   Unreliable level. [0,7]
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_setUnreliableLevel( morpho_HyperlapsePreprocess * const p_lapse, const int level );

/**
 * @brief
 * Sets the no movement level.
 *
 * The engine discards small detected motoion.
 * This function sets the threshold for this decision.
 *
 * Larger levels for more frequent small-motion decisions.
 *
 * @param[in,out] p_lapse
 * @param[in]     level   No movement level. [0,7]
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_setNoMovementLevel( morpho_HyperlapsePreprocess * const p_lapse, const int level );

/**
 * @brief
 * Starts the engine.
 *
 * @param[in,out] p_lapse
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_start( morpho_HyperlapsePreprocess * const p_lapse );

/**
 * @brief
 * Add a set of gyroscope data.
 *
 * @param[in,out] p_lapse
 * @param[in]     rot_x   [rad/sec]
 * @param[in]     rot_y   [rad/sec]
 * @param[in]     rot_z   [rad/sec]
 * @param[in]     time    [nsec]
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_addGyroData( morpho_HyperlapsePreprocess * const p_lapse,
                                         const double rot_x, const double rot_y, const double rot_z, const long long time );

/**
 * @biref
 * Add a set of accelerometer data.
 *
 * @param[in,out] p_lapse
 * @param[in]     acc_x   [m/sec/sec]
 * @param[in]     acc_y   [m/sec/sec]
 * @param[in]     acc_z   [m/sec/sec]
 * @param[in]     time    [nsec]
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_addAccelerometerData( morpho_HyperlapsePreprocess * const p_lapse,
                                                  const double acc_x, const double acc_y, const double acc_z, const long long time );

MORPHO_API(int)
morpho_HyperlapsePreprocess_addTimestamp( morpho_HyperlapsePreprocess * const p_lapse, const long long time );

/**
 * @brief
 * Processes the current input.
 *
 * @param[in,out] p_lapse
 * @param[in]     p_img
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_preprocess( morpho_HyperlapsePreprocess * const p_lapse, const morpho_ImageData * const p_img );

/**
 * @brief
 * Processes the current input which has padding data.
 *
 * @param[in,out] p_lapse
 * @param[in]     p_img
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_preprocessEx( morpho_HyperlapsePreprocess * const p_lapse, const morpho_ImageDataEx * const p_img );

/**
 * @brief
 * Gets the size of the motion data.
 *
 * @param[in]  p_lapse
 * @param[out] p_size
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_getDataSize( const morpho_HyperlapsePreprocess * const p_lapse, int *p_size, const long long time );

/**
 * @brief
 * Gets the motion data.
 *
 * The data obtained here must be fed to the morho_Hyperlapse engine.
 *
 * @param[in]  p_lapse
 * @param[out] p_data
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_getData( const morpho_HyperlapsePreprocess * const p_lapse, void * const p_data );

/**
 * @brief
 * Returns the failure code of the most recent preprocess.
 *
 * The code represents the disjunction of all applicable failure codes.
 *
 * @param[in,out] p_lapse
 * @param[out]    p_fcode
 *  @arg @ref MORPHO_HYPERLAPSE_FAILURE_NO_ERROR
 *  @arg @ref MORPHO_HYPERLAPSE_FAILURE_UNRELIABLE
 *  @arg @ref MORPHO_HYPERLAPSE_FAILURE_GENERIC
 *
 * @return Error codes.
 */
MORPHO_API(int)
morpho_HyperlapsePreprocess_getFcode( morpho_HyperlapsePreprocess * const p_lapse, int * const p_fcode );

#ifdef __cplusplus
}
#endif

#endif /* MORPHO_HYPERLAPSE_PREPROCESS_H */
