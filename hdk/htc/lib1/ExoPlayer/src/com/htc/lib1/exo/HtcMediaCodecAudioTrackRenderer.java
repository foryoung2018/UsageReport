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

import java.nio.ByteBuffer;

import com.google.android.exoplayer.drm.DrmSessionManager;

import com.google.android.exoplayer.audio.AudioTrack;
import android.annotation.TargetApi;
//import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaFormatHolder;
import com.google.android.exoplayer.SampleSource;
import com.htc.lib1.exo.utilities.LOG;
/**
 * Decodes and renders audio using {@link MediaCodec} and {@link AudioTrack}.
 */
@TargetApi(16)
public class HtcMediaCodecAudioTrackRenderer extends MediaCodecAudioTrackRenderer {
    static private String TAG = "HtcMediaCodecAudioTrackRenderer";
    
    private EventListener mEventListener = null;
    private boolean mbMute = false;

    public interface EventListener extends MediaCodecAudioTrackRenderer.EventListener {
        void onAudioSessionIdReady(int audioSessionId);

    }

    public HtcMediaCodecAudioTrackRenderer(SampleSource source) {
        this(source, null, true);
    }


    public HtcMediaCodecAudioTrackRenderer(SampleSource source, DrmSessionManager drmSessionManager,
                                           boolean playClearSamplesWithoutKeys) {
        super(source, drmSessionManager, playClearSamplesWithoutKeys);
    }

    public HtcMediaCodecAudioTrackRenderer(SampleSource source, Handler eventHandler,
                                           EventListener eventListener) {
        super(source, eventHandler, eventListener);
    }

    public HtcMediaCodecAudioTrackRenderer(SampleSource source, DrmSessionManager drmSessionManager,
                                           boolean playClearSamplesWithoutKeys, Handler eventHandler, EventListener eventListener) {
        super(source, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener);
        this.mEventListener = eventListener;
    }

    // Override configureCodec to provide the surface.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void configureCodec(MediaCodec codec, String codecName, MediaFormat format,
      android.media.MediaCrypto crypto) {
        if (codec != null)
            LOG.I(TAG, "configureCodec codec : " + codec.getName());
        
        if (format != null){
            LOG.I(TAG, "configureCodec mime : " + format.getString(MediaFormat.KEY_MIME));
            LOG.I(TAG, "configureCodec channel : " + format.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
            LOG.I(TAG, "configureCodec samplerate : " + format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
        }
        super.configureCodec(codec, codecName, format, crypto);
    }

    com.google.android.exoplayer.MediaFormat mFormat = null;

    protected void onInputFormatChanged(MediaFormatHolder formatHolder) throws ExoPlaybackException {
        mFormat = formatHolder.format;
        LOG.I(TAG, "onInputFormatChanged samplerate : " + mFormat.sampleRate);
        super.onInputFormatChanged(formatHolder);
    }
    //We report the audio session id for SRS / HTC51 sound effect, OOD ,26 Sep,2014
    protected void onAudioSessionId(final int audioSessionId) {

        if (eventHandler != null && mEventListener != null) {
            eventHandler.post(new Runnable()  {
                @Override
                public void run() {
                    mEventListener.onAudioSessionIdReady(audioSessionId);
                }
            });
        }
    }

    @Override
	public boolean isReady()
    {
        return super.isReady();
    }

    @Override
    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec,
                                          ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo, int bufferIndex, boolean shouldSkip) throws ExoPlaybackException {

        boolean rtn = false;
        if (super.isReady() == true && mbMute == true) 
            shouldSkip = true;

        rtn = super.processOutputBuffer(positionUs, elapsedRealtimeUs, codec, buffer, bufferInfo, bufferIndex, shouldSkip);

        return rtn;
    }

    public void setVolume (float gain)
    {
        try {
            handleMessage(MSG_SET_VOLUME, gain);
        } catch (ExoPlaybackException e) {
            LOG.W(TAG, e);
        }
    }

    public void mute(boolean flag)
    {
        LOG.I(TAG,"mute(" + flag + ")");
        mbMute = flag;
    }

    private boolean mbTimeSourc = true;

    public void enableTimeSource(boolean flag) {
        mbTimeSourc =  flag;
    }

    @Override
    protected boolean isTimeSource() {
        return mbTimeSourc;
    }
}
