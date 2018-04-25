/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htc.lib1.exo;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Surface;
import com.google.android.exoplayer.drm.DrmSessionManager;
import com.google.android.exoplayer.util.Util;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.htc.lib1.exo.utilities.LOG;
import java.nio.ByteBuffer;

/**
 * Decodes and renders video using {@link MediaCodec}.
 */
@TargetApi(16)
public class HtcMediaCodecVideoTrackRenderer extends MediaCodecVideoTrackRenderer {

    private static final String TAG = "HtcMediaCodecVideoTrackRenderer";
    private static final String KEY_CROP_LEFT = "crop-left";
    private static final String KEY_CROP_RIGHT = "crop-right";
    private static final String KEY_CROP_BOTTOM = "crop-bottom";
    private static final String KEY_CROP_TOP = "crop-top";

    public static final int OMX_COLOR_FormatVendorStartUnused = 0x7F000000; /**< Reserved region for introducing Vendor Extensions */
    public static final int OMX_COLOR_FormatVendorMTKYUV = 0x7F000001;
    public static final int OMX_COLOR_FormatVendorMTKYUV_FCM = 0x7F000002;
    public static final int OMX_COLOR_FormatVendorMTKYUV_UFO = 0x7F000003;

    public static final int QOMX_COLOR_FormatYVU420PackedSemiPlanar32m4ka = 0x7FA30C01;
    public static final int QOMX_COLOR_FormatYUV420PackedSemiPlanar16m2ka = 0x7FA30C02;
    public static final int QOMX_COLOR_FormatYUV420PackedSemiPlanar64x32Tile2m8ka = 0x7FA30C03;
    public static final int QOMX_COLOR_FormatYUV420PackedSemiPlanar32m4ka_nv21 = 0x7FA30C04;
    public static final int QOMX_COLOR_FormatYUV420PackedSemiPlanar16m2ka_nv21 = 0x7FA30C05;
    public static final int QOMX_COLOR_FORMATYUV420PackedSemiPlanar32m = 0x7FA30C06;

    private android.media.MediaFormat mFormat = null;
    private String mMimeType = null;
    private int mWidth = 0;
    private int mHeight = 0;
    private FrameReleaseTimeHelper mFrameReleaseTimeHelper = null;
    private boolean mbRenderedFirstFrame = false;
    private boolean mbSlowMotionMode1XMode = false;
    private boolean mbTimeSourc = false;
    private int miVideoRotateDegree = 0;

    public interface EventListener extends MediaCodecVideoTrackRenderer.EventListener {

        void onDroppedFrames(int count, long elapsed);

        void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio);

        void onDrawnToSurface(Surface surface);
    }

    public HtcMediaCodecVideoTrackRenderer(SampleSource source, int videoScalingMode) {
        super(source, videoScalingMode);
    }

    public HtcMediaCodecVideoTrackRenderer(SampleSource source, DrmSessionManager drmSessionManager,
                                           boolean playClearSamplesWithoutKeys, int videoScalingMode) {
        super(source, drmSessionManager, playClearSamplesWithoutKeys, videoScalingMode);
    }

    public HtcMediaCodecVideoTrackRenderer(SampleSource source, int videoScalingMode,
                                           long allowedJoiningTimeMs) {
        super(source, videoScalingMode, allowedJoiningTimeMs);
    }


    public HtcMediaCodecVideoTrackRenderer(SampleSource source, DrmSessionManager drmSessionManager,
                                           boolean playClearSamplesWithoutKeys, int videoScalingMode, long allowedJoiningTimeMs) {
        super(source, drmSessionManager, playClearSamplesWithoutKeys, videoScalingMode,
              allowedJoiningTimeMs);
    }


    public HtcMediaCodecVideoTrackRenderer(SampleSource source, int videoScalingMode,
                                           long allowedJoiningTimeMs, Handler eventHandler, EventListener eventListener,
                                           int maxDroppedFrameCountToNotify) {
        super(source, videoScalingMode, allowedJoiningTimeMs, eventHandler,
              eventListener, maxDroppedFrameCountToNotify);
    }

    public HtcMediaCodecVideoTrackRenderer(SampleSource source, DrmSessionManager drmSessionManager,
                                           boolean playClearSamplesWithoutKeys, int videoScalingMode, long allowedJoiningTimeMs,
                                           FrameReleaseTimeHelper frameReleaseTimeHelper, Handler eventHandler,
                                           EventListener eventListener, int maxDroppedFrameCountToNotify) {
        super(source, drmSessionManager, playClearSamplesWithoutKeys, videoScalingMode, allowedJoiningTimeMs,
              frameReleaseTimeHelper, eventHandler, eventListener, maxDroppedFrameCountToNotify);

        mFrameReleaseTimeHelper = frameReleaseTimeHelper;
    }

    // Override configureCodec to provide the surface.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void configureCodec(MediaCodec codec, String codecName, MediaFormat format,
      android.media.MediaCrypto crypto) {
        if (codec != null)
            LOG.I(TAG, "configureCodec codec : " + codec.getName());
        
        if (format != null){
            if (format.containsKey(MediaFormat.KEY_MIME)){
                LOG.I(TAG, "configureCodec mime : " + format.getString(MediaFormat.KEY_MIME));
            }
            if (format.containsKey("rotation-degrees")){
                int degree = format.getInteger("rotation-degrees");
                LOG.I(TAG, "configureCodec rotate degree " + degree);
                miVideoRotateDegree = degree;
            }
        }
        super.configureCodec(codec, codecName, format, crypto);
    }
    @Override
    protected void onOutputFormatChanged(com.google.android.exoplayer.MediaFormat inputFormat,
        android.media.MediaFormat outputFormat) {
    	super.onOutputFormatChanged(inputFormat, outputFormat);
        mFormat = outputFormat;
    

        if (outputFormat == null) return;

        boolean hasCrop = outputFormat.containsKey(KEY_CROP_RIGHT) && outputFormat.containsKey(KEY_CROP_LEFT)
                          && outputFormat.containsKey(KEY_CROP_BOTTOM) && outputFormat.containsKey(KEY_CROP_TOP);
        mWidth = hasCrop
                 ? outputFormat.getInteger(KEY_CROP_RIGHT) - outputFormat.getInteger(KEY_CROP_LEFT) + 1
                 : outputFormat.getInteger(android.media.MediaFormat.KEY_WIDTH);
        mHeight = hasCrop
                  ? outputFormat.getInteger(KEY_CROP_BOTTOM) - outputFormat.getInteger(KEY_CROP_TOP) + 1
                  : outputFormat.getInteger(android.media.MediaFormat.KEY_HEIGHT);
        
        if (outputFormat.containsKey(MediaFormat.KEY_COLOR_FORMAT)){
            int colorF =  outputFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
            LOG.D(TAG, "onOutputFormatChanged color format = " + colorToString(colorF));
        }
    }

    @Override
	public boolean isReady()
    {
        boolean rtn = super.isReady();

        if (rtn == false)
        {
            LOG.I(TAG , "isReady() = " + rtn);
        }
        return rtn;
    }
    @Override
    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec,
                                          ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo, int bufferIndex, boolean shouldSkip) {

      return super.processOutputBuffer(positionUs, elapsedRealtimeUs, codec, buffer, bufferInfo, bufferIndex, shouldSkip);
    }

    //htc_processOutputBuffer method performance is bad and has some drop frame problem.
    //So we use super.processOutputBuffer instead of it.
    protected boolean htc_processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec,
            ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo, int bufferIndex, boolean shouldSkip) {
        if (shouldSkip) {
            skipOutputBuffer(codec, bufferIndex);
            LOG.I(TAG , "processOutputBuffer shouldSkip 1");
            return true;
        }

        // Compute how many microseconds it is until the buffer's presentation time.
        long elapsedSinceStartOfLoopUs = (SystemClock.elapsedRealtime() * 1000) - elapsedRealtimeUs;
        long earlyUs = bufferInfo.presentationTimeUs - positionUs - elapsedSinceStartOfLoopUs;


        // Compute the buffer's desired release time in nanoseconds.
        long systemTimeNs = System.nanoTime();
        long unadjustedFrameReleaseTimeNs = systemTimeNs + (earlyUs * 1000);

        // Apply a timestamp adjustment, if there is one.
        long adjustedReleaseTimeNs;
        if (mFrameReleaseTimeHelper != null) {
            adjustedReleaseTimeNs = mFrameReleaseTimeHelper.adjustReleaseTime(
                                        bufferInfo.presentationTimeUs, unadjustedFrameReleaseTimeNs);
            earlyUs = (adjustedReleaseTimeNs - systemTimeNs) / 1000;
        } else {
            adjustedReleaseTimeNs = unadjustedFrameReleaseTimeNs;
        }

        if (earlyUs < (long)(-30000)) {
            // We're more than 30ms late rendering the frame.
            skipOutputBuffer(codec, bufferIndex);
            LOG.I(TAG , "processOutputBuffer earlyUs(" + (earlyUs /1000) + ") , late drop 2");
            return true;
        }

        if (!mbRenderedFirstFrame) {

            renderOutputBufferImmediate(codec, bufferIndex);
            mbRenderedFirstFrame = true;
            LOG.I(TAG , "processOutputBuffer renderedFirstFrame 3");
            return true;
        }

        if (getState() != TrackRenderer.STATE_STARTED) {
            LOG.I(TAG , "processOutputBuffer getState() = " + getState() + "!= TrackRenderer.STATE_STARTED 4");
            return false;
        }

        if (Util.SDK_INT >= 21) {
            // Let the underlying framework time the release.
            //boolean isSyncFrame = ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_SYNC_FRAME) > 0);

            if (earlyUs < (long)(50000)) {

                if (mbSlowMotionMode1XMode)
                {
                    //For slow motion video, we do not want to block the thread.
                    //Just draw on the screen.
                    renderOutputBufferImmediate(codec, bufferIndex);
                } else {
                    renderOutputBufferTimedV21(codec, bufferIndex, adjustedReleaseTimeNs);
                }
                return true;
            }else{
                //LOG.I(TAG , "processOutputBuffer earlyUs(" + (earlyUs/1000) + ") early skip 3");
                return false;
            }
        } 

        // We're either not playing, or it's not time to render the frame yet.
        LOG.I(TAG , "processOutputBuffer skip 7");
        return false;
    }

    @Override
    protected boolean handlesMimeType(String mimeType) {
        LOG.I(TAG, "handlesMimeType mimeType = " + mimeType);
        mMimeType = mimeType;

        return super.handlesMimeType(mimeType);
    }

    public void enableTimeSource(boolean flag) {
        LOG.I(TAG,"enableTimeSource(" + flag + ")");
        mbTimeSourc =  flag;
    }

    @Override
    protected boolean isTimeSource() {
        return mbTimeSourc;
    }

    private String colorToString(int colorFormat)
    {
        switch (colorFormat) {
        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            return "COLOR_FormatYUV420Planar";
        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            return "COLOR_FormatYUV420PackedPlanar";
        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            return "COLOR_FormatYUV420SemiPlanar";
        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            return "COLOR_FormatYUV420PackedSemiPlanar";
        case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
            return "COLOR_TI_FormatYUV420PackedSemiPlanar";
        case QOMX_COLOR_FormatYVU420PackedSemiPlanar32m4ka:
            return "QOMX_COLOR_FormatYVU420PackedSemiPlanar32m4ka";
        case QOMX_COLOR_FormatYUV420PackedSemiPlanar16m2ka:
            return "QOMX_COLOR_FormatYUV420PackedSemiPlanar16m2ka";
        case QOMX_COLOR_FormatYUV420PackedSemiPlanar64x32Tile2m8ka:
            return "QOMX_COLOR_FormatYUV420PackedSemiPlanar16m2ka";
        case QOMX_COLOR_FormatYUV420PackedSemiPlanar32m4ka_nv21:
            return "QOMX_COLOR_FormatYUV420PackedSemiPlanar16m2ka";
        case QOMX_COLOR_FormatYUV420PackedSemiPlanar16m2ka_nv21:
            return "QOMX_COLOR_FormatYUV420PackedSemiPlanar16m2ka_nv21";
        case QOMX_COLOR_FORMATYUV420PackedSemiPlanar32m:
            return "QOMX_COLOR_FORMATYUV420PackedSemiPlanar32m";
        case OMX_COLOR_FormatVendorMTKYUV:
            return "OMX_COLOR_FormatVendorMTKYUV";
        case OMX_COLOR_FormatVendorMTKYUV_FCM:
            return "OMX_COLOR_FormatVendorMTKYUV_FCM";
        case OMX_COLOR_FormatVendorMTKYUV_UFO:
            return "OMX_COLOR_FormatVendorMTKYUV_UFO";
        default:
            return "UNKNOWN COLOR(" + colorFormat + ")";
        }
    }

    private void enableSlowMotion1XMode(boolean flag)
    {
        LOG.I(TAG,"enableSlowMotion( " + flag + ")");
        mbSlowMotionMode1XMode = flag;
    }

    public void setPlayRate (float rate)
    {
        if (rate <= 0) return;

        if (rate == 1.0f)
            enableSlowMotion1XMode(false);
        else
            enableSlowMotion1XMode(true);
    }

    public int getVideoRotateDegree()
    {
        return miVideoRotateDegree;
    }
}
