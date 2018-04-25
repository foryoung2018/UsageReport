package com.htc.lib1.hyperlapse.morpho.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.view.Surface;

import com.htc.lib1.hyperlapse.util.MyLog;

/**
 * @hide
 * @author Winston
 *
 */
public class Encoder_Frame implements IEncoder {
    private int mIndexTrack;
    private int mWidth;
    private int mHeight;
    private long mFrameIndex = 0L;
    private long mCurSize = 0L;
    private MediaCodec mMediaCodec;
    private MediaMuxer mMediaMuxer;
    private MediaCodec.BufferInfo mInfo = new MediaCodec.BufferInfo();

    public Encoder_Frame(File file, int width, int height, int bitrate, int fps,
            int interval, int orientation) {
        mWidth = width;
        mHeight = height;
        String mime = "video/avc";
        MediaFormat format = MediaFormat.createVideoFormat(mime, width, height);
        format.setInteger("bitrate", bitrate);
        format.setInteger("frame-rate", fps);
        format.setInteger("i-frame-interval", interval);
        format.setInteger("color-format",
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        try {
            mMediaCodec = MediaCodec.createEncoderByType(mime);
        } catch (IOException e) {
            MyLog.e(e.getMessage(), e);
        }
        mMediaCodec.configure(format, null, null,
                MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
        try {
            mMediaMuxer = new MediaMuxer(file.getAbsolutePath(),
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            MyLog.e(e.getMessage(), e);
        }
        mMediaMuxer.setOrientationHint(orientation);
    }

    public void process(byte... data) {
        MyLog.d("NV21 length " + data.length);
        int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mMediaCodec
                    .getInputBuffer(inputBufferIndex);
            inputBuffer.clear();
            inputBuffer.rewind();

            // Change color space from NV21 to codec color format(NV12)++
            // Put Y data first
            inputBuffer.put(data, 0, mWidth * mHeight);

            // Get UV ->VU
            byte[] interlacedRegion = new byte[mWidth * mHeight / 2];

            for (int i = 0; i < mWidth * mHeight / 4; i++) {
                interlacedRegion[i * 2] = data[mWidth * mHeight + (i * 2 + 1)];
                interlacedRegion[i * 2 + 1] = data[mWidth * mHeight + (i * 2)];
            }

            inputBuffer.put(interlacedRegion);
            // Change color space from NV21 to codec color format(NV12)--

            // IPPP solution
            if (0 == (mFrameIndex++) % 4) {
                Bundle b = new Bundle();
                b.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                mMediaCodec.setParameters(b);
            }

            mMediaCodec.queueInputBuffer(inputBufferIndex, 0, data.length,
                    System.currentTimeMillis() * 1000, 0);
        } else {
            return;
        }

        mInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mInfo, 0);
        do {
            // Calculate size
            mCurSize += this.mInfo.size;

            if (-2 == outputBufferIndex) {
                mIndexTrack = mMediaMuxer.addTrack(mMediaCodec
                        .getOutputFormat());
                mMediaMuxer.start();
                MyLog.d("Start MediaMuxer");
            } else if (outputBufferIndex >= 0) {
                if ((mInfo.flags & 0x2) != 0) {
                    mInfo.size = 0;
                }

                ByteBuffer buffer = mMediaCodec
                        .getOutputBuffer(outputBufferIndex);

                buffer.position(buffer.position() + mInfo.offset);
                buffer.limit(buffer.position() + mInfo.size);
                // if (mInterval> 0) {
                // mInfo.presentationTimeUs = mTimeStamp;
                // mTimeStamp += mInterval;
                // }
                // else {
                // mInfo.presentationTimeUs /= mSkip;
                // }

                // MyLog.d("mInfo.presentationTimeUs = " +
                // mInfo.presentationTimeUs);
                MyLog.d("MediaMuxer writeSampleData");
                mMediaMuxer.writeSampleData(mIndexTrack, buffer, mInfo);

                mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mInfo, 0);
            }
        } while (outputBufferIndex >= 0);
    }

    public long getRecordingVideoSize() {
        return mCurSize;
    }

    public void stop() {
//        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mInfo, 0);
//        do {
//                if ((mInfo.flags & 0x2) != 0) {
//                    mInfo.size = 0;
//                }
//
//                ByteBuffer buffer = mMediaCodec
//                        .getOutputBuffer(outputBufferIndex);
//
//                buffer.position(buffer.position() + mInfo.offset);
//                buffer.limit(buffer.position() + mInfo.size);
//                
//                MyLog.d("MediaMuxer writeSampleData_stop");
//                mMediaMuxer.writeSampleData(mIndexTrack, buffer, mInfo);
//
//                mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
//                outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mInfo, 0);
//        } while (outputBufferIndex >= 0);
        
        mMediaCodec.stop();
        mMediaCodec.release();
        mMediaCodec = null;
        try {
            mMediaMuxer.stop();
            mMediaMuxer.release();
        } catch (IllegalStateException e) {
            MyLog.e(e.getMessage(), e);
        }
        mMediaMuxer = null;
    }

    @Override
    public Surface getSurface() {
        return null;
    }

    @Override
    public long getRecordingDuration() {
        return 0;
    }

    @Override
    public void setMaxVideoSize(long maxSize) {
    }

    @Override
    public void setVideoDuration(long maxDuration) {
    }
}
