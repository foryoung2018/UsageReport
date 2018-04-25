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
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.htc.lib1.exo.player.DemoPlayer.RendererBuilder;
import com.htc.lib1.exo.player.DemoPlayer.RendererBuilderCallback;
import com.htc.lib1.exo.upstream.DLNADataSource;
import com.htc.lib1.exo.utilities.LOG;
import com.google.android.exoplayer.extractor.Extractor;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.text.TextTrackRenderer;
import com.google.android.exoplayer.text.tx3g.Tx3gParser;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.FileDataSource;
import com.google.android.exoplayer.upstream.TransferListener;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

import android.content.Context;
import android.media.MediaCodec;
import android.net.Uri;

/**
 * A {@link RendererBuilder} for streams that can be read using an {@link Extractor}.
 */
public class ExtractorRendererBuilder implements RendererBuilder {

  private static final int BUFFER_SIZE = 10 * 1024 * 1024;

  private final Context context;
  private final String userAgent;
  private String TAG = "ExtractorRendererBuilder";
  private final Uri uri;
  private TrackRenderer[] mRenderers = null;
  private boolean mbDLNA = false;
  private Map<String, String> mHeaders = null;
  private DelegateListener mListener = new DelegateListener();
  private final Extractor extractor;

  public ExtractorRendererBuilder(Context context /* htc */,String userAgent, Uri uri, Extractor extractor, Map<String, String> headers /* htc */) {
    this.context = context;
    this.userAgent = userAgent;
    this.uri = uri;
    this.extractor = extractor;
    this.mHeaders = headers;
    checkHeaders(headers);
  }

  @Override
  public void buildRenderers(DemoPlayer player, RendererBuilderCallback callback) {
    // Build the video and audio renderers.
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(player.getMainHandler(),
        null);
    DataSource dataSource = isLocalFile(uri) ? new FileDataSource(null) :
                           (mbDLNA ? new DLNADataSource("exoplayer", null, mListener, mHeaders) :
                                     new DefaultUriDataSource(context, userAgent));
    SampleSource sampleSource;

    sampleSource = new ExtractorSampleSource(uri, dataSource, extractor, 2, BUFFER_SIZE);

    HtcMediaCodecVideoTrackRenderer videoRenderer = new HtcMediaCodecVideoTrackRenderer(sampleSource,
        null, true, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, null, player.getMainHandler(),
        player, 50);
    HtcMediaCodecAudioTrackRenderer audioRenderer = new HtcMediaCodecAudioTrackRenderer(sampleSource,
        null, true, player.getMainHandler(), player);

    // Build the time renderer to adjust playrate.
    TrackRenderer timeRenderer = new TimerAdjustRenderer(videoRenderer);

    TrackRenderer textRenderer = new TextTrackRenderer(sampleSource, player,
        player.getMainHandler().getLooper(), new Tx3gParser());

    // Invoke the callback.
    TrackRenderer[] renderers = new TrackRenderer[DemoPlayer.RENDERER_COUNT];
    renderers[DemoPlayer.TYPE_VIDEO] = videoRenderer;
    renderers[DemoPlayer.TYPE_AUDIO] = audioRenderer;
    renderers[DemoPlayer.TYPE_TEXT] = textRenderer;
    renderers[DemoPlayer.TYPE_DEBUG] = timeRenderer;

    mRenderers = renderers;
    enableExternalTimer();

    callback.onRenderers(null, null, renderers, bandwidthMeter);
  }

  static private boolean isLocalFile(Uri uri) {
    boolean isFile = false;
    final String[] patern = new String[] { "file", "content" };
    final int iCount = patern.length;
    for(int i=0; i < iCount ; i++)
    {
        if (uri.getScheme().equals(patern[i]))
        {
            isFile = true;
        }
    }
    return isFile;
  }

  private void enableExternalTimer()
  {
      LOG.I(TAG,"enableExternalTimer()");

      HtcMediaCodecVideoTrackRenderer vRenderer = getVideoRenderer();
      if (vRenderer != null)
      {
          LOG.I(TAG,"enableExternalTimer() video set");
          vRenderer.enableTimeSource(false);
      }

      HtcMediaCodecAudioTrackRenderer aRenderer = getAudioRenderer();
      if (aRenderer != null)
      {
          LOG.I(TAG,"enableExternalTimer() audio set");
          aRenderer.enableTimeSource(false);
      }

      TimerAdjustRenderer tRenderer = getTimerAdjustRenderer();

      if (tRenderer != null)
      {
          LOG.I(TAG,"enableExternalTimer() timer set");
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
  private void checkHeaders(Map<String, String> headers)
  {
      if (headers == null) return;

      if (headers.containsKey("x-htc-dlna"))
      {
        String isDLNA = headers.get("x-htc-dlna");
        LOG.I(TAG," isDLNA = " + isDLNA);
        if (isDLNA != null && isDLNA.equals("1"))
        {
          mbDLNA = true;
        }
      }

      if (headers.containsKey("x-htc-dlna-stallmode"))
      {
        String sVlaue = headers.get("x-htc-dlna-stallmode");
        if (sVlaue != null && sVlaue.equals("1"))
        {
          bStallMode = true;
        }
      }

      if (headers.containsKey("x-htc-dlna-timeseek"))
      {
        String sVlaue = headers.get("x-htc-dlna-timeseek");
        if (sVlaue != null && sVlaue.equals("1"))
        {
          bTimeSeek = true;
        }
      }

      if (headers.containsKey("x-htc-dlna-byteseek"))
      {
        String sVlaue = headers.get("x-htc-dlna-byteseek");
        if (sVlaue != null && sVlaue.equals("1"))
        {
          bByteSeek = true;
        }
      }
  }

  private boolean bStallMode = false;
  private boolean bTimeSeek = false;
  private boolean bByteSeek = false;
  
  private boolean enableTimeSeek(){
      if (mbDLNA == false) return false;
      if (bStallMode == true) return false;
      if (bByteSeek == true) return false;
      return bTimeSeek;      
  }
  
  private boolean enableByteSeek(){
      if (mbDLNA == false) return false;
      if (bStallMode == true) return false;
      if (bTimeSeek == true) return false;
      return bByteSeek;      
  }

  private boolean enableStallMode(){
      if (mbDLNA == false) return false;
      if (bByteSeek == true) return false;
      if (bTimeSeek == true) return false;
      return bStallMode;      
  }
  class DelegateListener implements TransferListener{
    private TransferListener innerListener = null;

    public void setListener(TransferListener listener) {
        innerListener = listener;
    }

    @Override
    public void onTransferStart() {
        if (innerListener != null)
            innerListener.onTransferStart();
    }

    @Override
    public void onBytesTransferred(int bytesTransferred) {
        if (innerListener != null)
            innerListener.onBytesTransferred(bytesTransferred);
    }

    @Override
    public void onTransferEnd() {
        if (innerListener != null)
            innerListener.onTransferEnd();
    }
  }
}
