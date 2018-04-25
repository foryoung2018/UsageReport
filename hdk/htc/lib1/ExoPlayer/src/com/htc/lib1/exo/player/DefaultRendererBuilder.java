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
package com.htc.lib1.exo.player;

import java.util.Map;

import com.htc.lib1.exo.HtcMediaCodecAudioTrackRenderer;
import com.htc.lib1.exo.HtcMediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.htc.lib1.exo.player.DemoPlayer.RendererBuilder;
import com.htc.lib1.exo.player.DemoPlayer.RendererBuilderCallback;
import com.htc.lib1.exo.utilities.LOG;
import com.htc.lib1.exo.source.DefaultSampleSource;
import com.htc.lib1.exo.source.HtcMediaExtractor;

import android.content.Context;
import android.media.MediaCodec;
import android.net.Uri;
import android.widget.TextView;

/**
 * A {@link RendererBuilder} for streams that can be read using
 * {@link android.media.MediaExtractor}.
 */
public class DefaultRendererBuilder implements RendererBuilder {
  private String TAG = "DefaultRendererBuilder";
  private final Context context;
  private final Uri uri;
  private TrackRenderer[] mRenderers = null;
  private Map<String, String> mHeaders = null;

  public DefaultRendererBuilder(Context context, Uri uri, TextView debugTextView, Map<String, String> headers /* htc */) {
    this.context = context;
    this.uri = uri;
    this.mHeaders = headers;
  }

  @Override
  public void buildRenderers(DemoPlayer player, RendererBuilderCallback callback) {
    // Build the video and audio renderers.
	DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    DefaultSampleSource sampleSource =
        new DefaultSampleSource(new HtcMediaExtractor(context, uri, mHeaders), 2);
    HtcMediaCodecVideoTrackRenderer videoRenderer = new HtcMediaCodecVideoTrackRenderer(sampleSource,
        null, true, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, null, player.getMainHandler(), 
        player, 50);
    HtcMediaCodecAudioTrackRenderer audioRenderer = new HtcMediaCodecAudioTrackRenderer(sampleSource,
        null, true, player.getMainHandler(), player);

    // Build the debug renderer.
    TrackRenderer timeRenderer = new TimerAdjustRenderer(videoRenderer);

    // Invoke the callback.
    TrackRenderer[] renderers = new TrackRenderer[DemoPlayer.RENDERER_COUNT];
    renderers[DemoPlayer.TYPE_VIDEO] = videoRenderer;
    renderers[DemoPlayer.TYPE_AUDIO] = audioRenderer;
    renderers[DemoPlayer.TYPE_DEBUG] = timeRenderer;

    mRenderers = renderers;

    enableExternalTimer();

    callback.onRenderers(null, null, renderers, bandwidthMeter);
  }

  private void enableExternalTimer()
  {
      LOG.I(TAG,"enableExternalTimer()");

      HtcMediaCodecVideoTrackRenderer vRenderer = getVideoRenderer();
      if (vRenderer != null)
      {
          vRenderer.enableTimeSource(false);
      }

      HtcMediaCodecAudioTrackRenderer aRenderer = getAudioRenderer();
      if (aRenderer != null)
      {
          aRenderer.enableTimeSource(false);
      }

      TimerAdjustRenderer tRenderer = getTimerAdjustRenderer();

      if (tRenderer != null)
      {
          tRenderer.enableTimeSource(true);
      }
  }

  private HtcMediaCodecVideoTrackRenderer getVideoRenderer()
  {
      if (mRenderers == null) return null;

      TrackRenderer myRenderer = mRenderers[DemoPlayer.TYPE_VIDEO];              
      if (myRenderer != null && myRenderer instanceof HtcMediaCodecVideoTrackRenderer)
      {
          HtcMediaCodecVideoTrackRenderer renderer = (HtcMediaCodecVideoTrackRenderer) myRenderer;
          return renderer;
      }
      return null;
  }

  private HtcMediaCodecAudioTrackRenderer getAudioRenderer()
  {
      if (mRenderers == null) return null;

      TrackRenderer myRenderer = mRenderers[DemoPlayer.TYPE_AUDIO];              
      if (myRenderer != null && myRenderer instanceof HtcMediaCodecAudioTrackRenderer)
      {
          HtcMediaCodecAudioTrackRenderer renderer = (HtcMediaCodecAudioTrackRenderer) myRenderer;
          return renderer;
      }
      return null;
  }

  private TimerAdjustRenderer getTimerAdjustRenderer()
  {
      if (mRenderers == null) return null;

      TrackRenderer myRenderer = mRenderers[DemoPlayer.TYPE_DEBUG];              
      if (myRenderer != null && myRenderer instanceof TimerAdjustRenderer)
      {
          TimerAdjustRenderer renderer = (TimerAdjustRenderer) myRenderer;
          return renderer;
      }
      return null;
  }
}
