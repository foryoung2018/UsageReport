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
package com.htc.lib1.exo.wrap;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.htc.lib1.exo.player.DefaultRendererBuilder;
import com.htc.lib1.exo.player.DemoPlayer;
import com.htc.lib1.exo.player.DemoPlayer.RendererBuilder;
import com.htc.lib1.exo.player.ExtractorRendererBuilder;
import com.htc.lib1.exo.player.HlsRendererBuilder;
import com.htc.lib1.exo.player.UnsupportedDrmException;
import com.google.android.exoplayer.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer.extractor.ts.AdtsExtractor;
import com.google.android.exoplayer.extractor.ts.TsExtractor;
import com.google.android.exoplayer.extractor.webm.WebmExtractor;
import com.google.android.exoplayer.metadata.GeobMetadata;
import com.htc.lib1.exo.metadata.PicMetadata;
import com.google.android.exoplayer.metadata.PrivMetadata;
import com.google.android.exoplayer.metadata.TxxxMetadata;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.util.Util;
import com.htc.lib1.exo.EventLogger;
import com.htc.lib1.exo.HtcMediaCodecVideoTrackRenderer;
import com.htc.lib1.exo.utilities.ContentHelper;
import com.htc.lib1.exo.upstream.HTTPHelper;
import com.htc.lib1.exo.utilities.LOG;
import com.htc.lib1.exo.utilities.MimeTypeChecker;

import android.annotation.TargetApi;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.PowerManager;
import android.view.SurfaceHolder;
import android.view.accessibility.CaptioningManager;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * refference from PlayerActivity that plays media using {@link DemoPlayer}.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
class HtcWrapExoPlayer implements IPlayer,
    DemoPlayer.Listener, DemoPlayer.CaptionListener, DemoPlayer.Id3MetadataListener,
    AudioCapabilitiesReceiver.Listener {

  public static final int TYPE_OTHER = -1;
  public static final int TYPE_DASH = 0;
  public static final int TYPE_SS = 1;
  public static final int TYPE_HLS = 2;
  public static final int TYPE_MP4 = 3;
  public static final int TYPE_MP3 = 4;
  public static final int TYPE_FMP4 = 5;
  public static final int TYPE_WEBM = 6;
  public static final int TYPE_TS = 7;
  public static final int TYPE_AAC = 8;
  public static final int TYPE_M4A = 9;

  private static final String TAG = "HtcWrapExoPlayer";

  private EventLogger eventLogger;

  private DemoPlayer player;
  private boolean playerNeedsPrepare;

  private long playerPosition = 0;

  private Uri uri;
  private int contentType = TYPE_OTHER;
  private String contentId = null;

  private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
  private AudioCapabilities audioCapabilities;
  private PicMetadata picMetadata;
  @Override
  public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
    boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
    if (player == null || audioCapabilitiesChanged) {
      this.audioCapabilities = audioCapabilities;
      releasePlayer();
      preparePlayer(mContext);
    } else if (player != null) {
      player.setBackgrounded(false);
    }
  }

  // Internal methods
  private RendererBuilder getRendererBuilder(Context context,Uri uri, String mimeType) {
    String userAgent = Util.getUserAgent(context, "HtcVideoPlayer");
    int type =  checkInternalContentType(uri, mimeType, contentType);
    switch (type) {
      case TYPE_HLS:
        LOG.I(TAG,"getRendererBuilder HlsRendererBuilder");
        return new HlsRendererBuilder(context, userAgent, uri.toString(), audioCapabilities);
      case TYPE_MP4:
        LOG.I(TAG,"getRendererBuilder ExtractorRendererBuilder Mp4Extractor");
        return new ExtractorRendererBuilder(context, userAgent, uri, new Mp4Extractor(), mHeaders);
      case TYPE_MP3:
        LOG.I(TAG,"getRendererBuilder ExtractorRendererBuilder Mp3Extractor");
        return new ExtractorRendererBuilder(context, userAgent, uri, new Mp3Extractor(), mHeaders);
      case TYPE_AAC:
        LOG.I(TAG,"getRendererBuilder ExtractorRendererBuilder AdtsExtractor");
        return new ExtractorRendererBuilder(context, userAgent, uri, new AdtsExtractor(), mHeaders);
      case TYPE_FMP4:
        return new ExtractorRendererBuilder(context, userAgent, uri, new FragmentedMp4Extractor(), mHeaders);
      case TYPE_TS:
        LOG.I(TAG,"getRendererBuilder ExtractorRendererBuilder TsExtractor");
        return new ExtractorRendererBuilder(context, userAgent, uri, new TsExtractor(0, audioCapabilities), mHeaders);
      case TYPE_WEBM:
        LOG.I(TAG,"getRendererBuilder ExtractorRendererBuilder WebmExtractor");
        return new ExtractorRendererBuilder(context, userAgent, uri, new WebmExtractor(), mHeaders);
      default:
        LOG.I(TAG,"getRendererBuilder DefaultRendererBuilder");
        return new DefaultRendererBuilder(context, uri, null, mHeaders);
    }
  }

  private void preparePlayer(Context context) {
    LOG.I(TAG,"preparePlayer");
    if (player == null) {
      player = new DemoPlayer(getRendererBuilder(context, uri, mMimeType));
      player.addListener(this);
      player.setCaptionListener(this);
      player.setMetadataListener(this);
      player.seekTo(playerPosition);
      playerNeedsPrepare = true;
      eventLogger = new EventLogger();
      eventLogger.startSession();
      player.addListener(eventLogger);
      player.setInfoListener(eventLogger);
      player.setInternalErrorListener(eventLogger);
    }
    if (playerNeedsPrepare) {
      player.prepare();
      playerNeedsPrepare = false;
    }
    if (mSurfaceHolder != null) player.setSurface(mSurfaceHolder.getSurface());
    //player.setPlayWhenReady(true);
  }

  private void releasePlayer() {
    LOG.I(TAG,"releasePlayer");
    if (player != null) {
      playerPosition = player.getCurrentPosition();
      player.release();
      player = null;
      eventLogger.endSession();
      eventLogger = null;
    }
  }

  // DemoPlayer.Listener implementation

  @Override
  public void onStateChanged(boolean playWhenReady, int playbackState) {
    String text = "playWhenReady=" + playWhenReady + ", playbackState=";
    switch(playbackState) {
      case ExoPlayer.STATE_BUFFERING:
        text += "buffering";
        mbBuffering = true;
        break;
      case ExoPlayer.STATE_ENDED:
        text += "ended";
        if (mUpperOnCompletionListener != null) {
          mUpperOnCompletionListener.onCompletion();
        }
        stayAwake(false);
        break;
      case ExoPlayer.STATE_IDLE:
        text += "idle";
        break;
      case ExoPlayer.STATE_PREPARING:
        text += "preparing";
        break;
      case ExoPlayer.STATE_READY:
        text += "ready";
        if (mbPrepared == false) {
          if (mbSlowMotionVideo)
          {
            enableSlowMotion1XMode(false);
          }

          if (mUpperOnPreparedListener != null) {
            boolean has_video = haveTracks(DemoPlayer.TYPE_VIDEO);
            boolean has_audio = haveTracks(DemoPlayer.TYPE_AUDIO);

            mUpperOnPreparedListener.onPrepared();

            //For audio only 
            if (has_audio == true && has_video == false) {
              LOG.I(TAG,"ready: has_video " + has_video);
              LOG.I(TAG,"ready: has_audio " + has_audio);

              if (mUpperOnVideoSizeChangedListener != null) {
                mUpperOnVideoSizeChangedListener.onVideoSizeChanged(mVideoWidth, mVideoHeight);
              }
            }
          }
          mbPrepared = true;
        } else if (mbPrepared == true ) {
          /*if (mUpperOnInfoListener != null && mbBuffering) {
              mUpperOnInfoListener.onInfo(MediaPlayer.MEDIA_INFO_BUFFERING_END,0);
          } else*/ if (mUpperOnSeekCompleteListener != null) {
            mUpperOnSeekCompleteListener.onSeekComplete();
          }
        }
        mbBuffering = false;
        break;
      default:
        text += "unknown";
        break;
      }
      LOG.I(TAG, text);
    }

  @Override
  public void onError(Exception e) {
    LOG.I(TAG, "onError ");
    if (e instanceof UnsupportedDrmException) {
      // Special case DRM failures.
      /*UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
      int stringId = unsupportedDrmException.reason == UnsupportedDrmException.REASON_NO_DRM
          ? R.string.drm_error_not_supported
          : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
          ? R.string.drm_error_unsupported_scheme
          : R.string.drm_error_unknown;*/
      //Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
    }
    playerNeedsPrepare = true;
    if (mUpperOnErrorListener != null) {
      mUpperOnErrorListener.onError(-1,-1004);
    }

  }
  @Override
  public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
    LOG.I(TAG, "onVideoSizeChanged (" + width + "," + height + ")");
    mVideoWidth = width;
    mVideoHeight = height;
    if (mUpperOnVideoSizeChangedListener != null) {
      mUpperOnVideoSizeChangedListener.onVideoSizeChanged(width, height);
    }
  }

  private boolean haveTracks(int type) {
    if (player == null ) return false;
    if (player.getTracks(type) == null) return false;

    if (type == DemoPlayer.TYPE_VIDEO && player.getVideoRenderer() != null) 
        return player.getVideoRenderer().isReady();

    else if (type == DemoPlayer.TYPE_AUDIO && player.getAudioRenderer() != null)
        return player.getAudioRenderer().isReady();

    else if (type == DemoPlayer.TYPE_TEXT && player.getTextRenderer() != null)
        return player.getTextRenderer().isReady();

    return false;
  }

  @Override
  public void onCues(List<Cue> cues) {
    
    if (mUpperOnTimedTextListener != null) {
      StringBuilder text = new StringBuilder();
      for(Cue cue : cues) {
    	  text.append(cue.text);
      }
      LOG.I(TAG,"onCues " + text.toString());
      mUpperOnTimedTextListener.onTimedText(text.toString());
    }
  }

  // DemoPlayer.MetadataListener implementation


  @Override
  public void onId3Metadata(Map<String, Object> metadata) {
    for (Map.Entry<String, Object> entry : metadata.entrySet()) {
      if (TxxxMetadata.TYPE.equals(entry.getKey())) {
        TxxxMetadata txxxMetadata = (TxxxMetadata) entry.getValue();
        LOG.I(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s",
            TxxxMetadata.TYPE, txxxMetadata.description, txxxMetadata.value));
      } else if (PrivMetadata.TYPE.equals(entry.getKey())) {
        PrivMetadata privMetadata = (PrivMetadata) entry.getValue();
        LOG.I(TAG, String.format("ID3 TimedMetadata %s: owner=%s",
            PrivMetadata.TYPE, privMetadata.owner));
      } else if (GeobMetadata.TYPE.equals(entry.getKey())) {
        GeobMetadata geobMetadata = (GeobMetadata) entry.getValue();
        LOG.I(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
            GeobMetadata.TYPE, geobMetadata.mimeType, geobMetadata.filename,
            geobMetadata.description));
      } else if (entry.getKey().equals("APIC") || entry.getKey().equals("PIC")){
        LOG.I(TAG, "ID3 TimedMetadata find APIC " + entry.getKey());
        picMetadata = (PicMetadata) entry.getValue();
        if (mUpperOnInfoListener != null) {
            mUpperOnInfoListener.onInfo(android.media.MediaPlayer.MEDIA_INFO_METADATA_UPDATE, 0);
        }
      } else {
        LOG.I(TAG, String.format("ID3 TimedMetadata %s", entry.getKey()));
      }
    }
  }

  private float getUserCaptionFontScaleV19(Context context) {
    CaptioningManager captioningManager =
      (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
    return captioningManager.getFontScale();
  }

  private CaptionStyleCompat getUserCaptionStyleV19(Context context) {
    CaptioningManager captioningManager =
      (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
    return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
  }


  /*
   * HTC Defined Interface
   */
  public HtcWrapExoPlayer() {
    picMetadata = null;
  }

  private void init(Uri uri) {
    if (isLocalFile(uri)) {
      String path = convertUriToPath(mContext, uri);
      this.uri = Uri.parse(path);
    }else {
      this.uri = uri;
	}
    if (mContext != null)
    {
      audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(mContext, this);
      audioCapabilitiesReceiver.register();
    }
  }

  private void deInit() {
    audioCapabilitiesReceiver.unregister();
  }

  private int checkInternalContentType(Uri uri, String mimeType, int type) {
    if (MimeTypeChecker.isSmoothStreaming(uri, mimeType))
    {
      type = TYPE_SS;
    }
    else if (MimeTypeChecker.isDash(uri, mimeType))
    {
      type = TYPE_DASH;
    }
    else if (MimeTypeChecker.isHLS(uri, mimeType))
    {
      type = TYPE_HLS;
    }
    else if (MimeTypeChecker.isMp3(uri, mimeType))
    {
      type = TYPE_MP3;
    }
    else if (MimeTypeChecker.isTs(uri, mimeType))
    {
      type = TYPE_TS;
    }
    else if (MimeTypeChecker.isWebm(uri, mimeType) || MimeTypeChecker.isMkv(uri, mimeType))
    {
      type = TYPE_WEBM;
    }
    else if (mbDLNA == true && MimeTypeChecker.isStreaming(uri))
    {
      //For DLNA Certification
      if (mimeType == null)
      {
        try {
          HashMap<String, String> headers = new HashMap<String, String>();
          //DLNA CTT 7.4.3.2,
          headers.put("transferMode.dlna.org", "Streaming");
          mimeType = HTTPHelper.getContentType(uri, headers);
          LOG.I(TAG,"get mimeType = " + mimeType);
        } catch (Exception e) {
          LOG.W(TAG, e);
        }
      }

      if (MimeTypeChecker.isMp4(uri, mimeType))
      {
        type = TYPE_MP4;
      }
      else if (MimeTypeChecker.isTs(uri, mimeType))
      {
        type = TYPE_TS;
      }

    }  else if (mbSlowMotionVideo) {
      //Exo Player doesn't support local mp4 yet.
      type = TYPE_MP4;
      /*
    } else if (MimeTypeChecker.isStreaming(uri) && MimeTypeChecker.isMP4(uri, mimeType)) {
      //Exo Player doesn't support local mp4 yet.
      type = TYPE_MP4;*/
    } else if (MimeTypeChecker.isStreaming(uri) == false) {
        LOG.I(TAG, "test from local byteBuffer");
        ByteBuffer buffer = ContentHelper.getByteBuffer(mContext, uri, 1000);

        if (MimeTypeChecker.isMp4(buffer)) {
            type = TYPE_MP4;

        } else if (MimeTypeChecker.isAsf(buffer)) {
            type = TYPE_OTHER;

        } else if (MimeTypeChecker.isWebm(buffer)) {
            type = TYPE_WEBM;

        } else if (MimeTypeChecker.isTs(buffer)) {
            type = TYPE_TS;

        }
    }
    LOG.I(TAG,"checkInternalContentType contentType " + type);
    return type;
  }

  private void enableSlowMotion1XMode(boolean flag)
  {
    enableSlowMotion1XMode(flag, mbSlowMotionVideoMultiple);
  }
  private void enableSlowMotion1XMode(boolean flag, float rate)
  {
    LOG.I(TAG,"enableSlowMotion1XMode " + flag + ", multiple = " + rate);
    if (player == null) return;
    if (mbSlowMotionVideo == false) return;

    if (flag) {
      player.mute(rate != mbSlowMotionVideoMultiple ? true : false);
      player.updatePlaySpeed(rate);
      player.setAudioSpeed(rate/4);
    } else {
      player.updatePlaySpeed(1.0f);
      player.mute(false);
      player.setAudioSpeed(1.0f/rate);
    }
  }

  private boolean isSurfaceReadyOrAudioOnly() {
    if (player == null) return false;

    return ((player.getSurface() != null && player.getSurface().isValid())
            || player.getSelectedTrackIndex(DemoPlayer.TYPE_VIDEO) == DemoPlayer.DISABLED_TRACK);
  }

  static private boolean isLocalFile(final Uri uri) {
    if (uri == null) return false;
    if (uri.getScheme() == null) return true;
    
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

  private String convertUriToPath(Context context, final Uri uri)
  {
        LOG.I(TAG,"convertUriToPath uri = " + uri);
        final String DATA = "_data";

        String path = null;
        if (null != uri)
        {
            String scheme = uri.getScheme();
            LOG.I(TAG,"convertUriToPath scheme = " + scheme);

            if (null == scheme || scheme.equals("") ||
                    scheme.equals(ContentResolver.SCHEME_FILE)) {
                path = "file://" + uri.getPath();
            }
            else if (scheme.equals("http"))
            {
                path = uri.toString();
            }
            else if (scheme.equals(ContentResolver.SCHEME_CONTENT))
            {
                String[] projection = new String[] {DATA};
                Cursor cursor = null;
                ContentResolver resolver = null;
                ContentProviderClient cpc = null;

                try
                {
                    if (context == null) return null;

                    resolver = context.getContentResolver();

                    if (resolver == null) return null;

                    cpc = resolver.acquireUnstableContentProviderClient(uri);

                    if (cpc == null) return null;

                    cursor = cpc.query(uri, projection, null,null, null);

                    if (null == cursor || 0 == cursor.getCount() || !cursor.moveToFirst())
                    {
                        throw new IllegalArgumentException("Given Uri could not be found" +
                                                           " in media store");
                    }
                    int pathIndex = cursor.getColumnIndexOrThrow(DATA);
                    path = "file://" + cursor.getString(pathIndex);
                    LOG.I(TAG,"convertUriToPath path = " + path);
                }
                catch (SQLiteException e)
                {
                    LOG.I(TAG,"Given Uri is not formatted in a way so that it can be found in media store.");
                }
                catch (Exception e)
                {
                    LOG.I(TAG,e);
                }
                finally
                {
                    if (null != cpc)
                    {
                        cpc.release();
                    }

                    if (null != cursor)
                    {
                        cursor.close();
                    }
                }
            }
            else
            {
                LOG.W(TAG,"Given Uri scheme is not supported");
            }
        }
        return path;
    }

  public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
    LOG.I(TAG,"setDataSource");
    mContext = context;
    mHeaders = headers;

    if (headers != null)
    {
      if (headers.containsKey("x-htc-mimetype"))
      {
        String mimeType = headers.get("x-htc-mimetype");
        LOG.I(TAG," mimeType = " + mimeType);
        mMimeType = mimeType;
      }

      if (headers.containsKey("x-htc-slowmotion"))
      {
        String isSlowMotionVideo = headers.get("x-htc-slowmotion");
        LOG.I(TAG," isSlowMotionVideo = " + isSlowMotionVideo);
        if (isSlowMotionVideo != null && isSlowMotionVideo.equals("1"))
        {
          mbSlowMotionVideo = true;
        }
      }
      if (headers.containsKey("x-htc-dlna"))
      {
        String isDLNA = headers.get("x-htc-dlna");
        LOG.I(TAG," isDLNA = " + isDLNA);
        if (isDLNA != null && isDLNA.equals("1"))
        {
          mbDLNA = true;
        }
      }

      if (headers.containsKey("x-htc-slowmotion-multiple"))
      {
        String fSlowMotionVideoMultiple = headers.get("x-htc-slowmotion-multiple");
        LOG.I(TAG," fSlowMotionVideoMultiple = " + fSlowMotionVideoMultiple);
        if (fSlowMotionVideoMultiple != null)
        {
          if (fSlowMotionVideoMultiple.equals("2"))
            mbSlowMotionVideoMultiple = 2.0f;
          else if (fSlowMotionVideoMultiple.equals("4"))
            mbSlowMotionVideoMultiple = 4.0f;
        }
      }
    }
    init(uri);
  }

  public void setDataSource(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
    LOG.I(TAG,"setDataSource");
    init(Uri.parse(path));
  }
  public void setDataSource(Context context, Uri uri) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
    LOG.I(TAG,"setDataSource");
    mContext = context;
    this.uri = uri;
    init(uri);
  }
  public void setDataSource(FileDescriptor fd, long offset, long length) throws IllegalArgumentException, IllegalStateException, IOException {
    LOG.I(TAG,"setDataSource");
    this.uri = null;
  }
  public void setDataSource(FileDescriptor fd) throws IllegalArgumentException, IllegalStateException, IOException {
    LOG.I(TAG,"setDataSource");
    this.uri = null;
  }

  public void setDisplay(SurfaceHolder sh) {
    LOG.I(TAG,"setDisplay");
    mSurfaceHolder = sh;
  }
  public int getCurrentPosition() {
    if (player == null) return -1;
    long rtn = player.getCurrentPosition();
    return (int) rtn;
  }
  public int getDuration() {
    if (player == null) return -1;
    long rtn = player.getDuration();
    return (int) rtn;
  }
  public void seekTo(int nPos) {
    if (player == null) return;
    LOG.I(TAG,"seekTo " + nPos);
    player.seekTo((long)nPos);
  }
  public void stop() {
    LOG.I(TAG,"stop do nothing");
    stayAwake(false);
  }
  public void pause() {
    if (player == null) return;
    if (isSurfaceReadyOrAudioOnly()) {
      LOG.I(TAG,"pause ");
      player.setPlayWhenReady(false);
    }
    stayAwake(false);
  }
  public void start() {
    if (player == null) return;
    if (isSurfaceReadyOrAudioOnly()) {
      LOG.I(TAG,"start ");
      player.setPlayWhenReady(true);
    }
    stayAwake(true);
  }
  public void reset() {
    LOG.I(TAG,"reset ");
    stayAwake(false);
  }
  public void release() {
    if (player == null) return;

    releasePlayer();
    LOG.I(TAG,"release ");
    stayAwake(false);
    deInit();
  }
  public void prepare() throws IllegalStateException, IOException {
    LOG.I(TAG,"prepare ");
    preparePlayer(mContext);
  }

  public void prepareAsync() throws IllegalStateException {
    LOG.I(TAG,"prepareAsync ");
    preparePlayer(mContext);
  }

  public int getVideoWidth() {
    return mVideoWidth;
  }

  public int getVideoHeight() {
    return mVideoHeight;
  }

  public void setWakeMode(Context context, int mode) {
    boolean washeld = false;
    if (mWakeLock != null) {
      if (mWakeLock.isHeld()) {
        washeld = true;
        mWakeLock.release();
      }
      mWakeLock = null;
    }

    PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    mWakeLock = pm.newWakeLock(mode|PowerManager.ON_AFTER_RELEASE, HtcWrapExoPlayer.class.getName());
    mWakeLock.setReferenceCounted(false);
    if (washeld) {
      mWakeLock.acquire();
    }
  }

  private void stayAwake(boolean awake) {
    LOG.I(TAG,"stayAwake " + awake);
    if (mWakeLock != null) {
      if (awake && !mWakeLock.isHeld()) {
        mWakeLock.acquire();
      } else if (!awake && mWakeLock.isHeld()) {
        mWakeLock.release();
      }
    }
  }

  public int getAudioSessionId()
  {
    if (player != null)
      return player.getAudioSessionId();

    return 0;
  }

  public void deselectTrack(int index)
  {
    //Do nothing
  }

  public Bitmap getAlbumArt()
  {
	  if(picMetadata != null)
          return picMetadata.getBitmap();
	  return null;
  }

  public void invokeEx(Parcel request, Parcel reply) {
    if (request == null) return;

    int pos = request.dataPosition();

    if (pos < 4) return;

    request.setDataPosition(4);
    String interfaceName = request.readString();

    if (interfaceName == null) return;
    if (interfaceName.equals("android.media.IMediaPlayer") == false)
    {
        LOG.W(TAG, "invokeEx wrong interface name = " + interfaceName);
        return;
    }
    if (request.dataAvail() == 0) return;

    int methodId = -1;
    methodId = request.readInt();


    switch(methodId)
    {
    case INVOKE_VIDEO_SET_SLOW_MOTION:
    {
      float rate = request.readFloat();

      LOG.I(TAG,"invokeEx INVOKE_VIDEO_SET_SLOW_MOTION rate " + rate);
      if (checkSlowMotionRate(rate))
      {
          //This is for Trim n Slow
          mbEnableSlowMotion1XMode = checkSlowMotionRateEnable(rate) ? true : false;
      }else{
          //This is for video player
          mbEnableSlowMotion1XMode = !mbEnableSlowMotion1XMode;
          rate = mbSlowMotionVideoMultiple;
      }

      if (mbEnableSlowMotion1XMode)
      {
        LOG.I(TAG,"invokeEx INVOKE_VIDEO_SET_SLOW_MOTION 1X");
        enableSlowMotion1XMode(true, rate);
        //player.selectTrack(DemoPlayer.TYPE_AUDIO, 0);
      } else
      {
        LOG.I(TAG,"invokeEx INVOKE_VIDEO_SET_SLOW_MOTION Slow");
        //player.selectTrack(DemoPlayer.TYPE_AUDIO, -1);
        enableSlowMotion1XMode(false);
      }
    }
    break;

    case INVOKE_VIDEO_GET_SLOW_MOTION_SPEED:
      LOG.I(TAG,"invokeEx INVOKE_VIDEO_GET_SLOW_MOTION_SPEED");
      {
        if (mbEnableSlowMotion1XMode)
          reply.writeInt(1);
        else
          reply.writeInt(0);
        reply.setDataPosition(0);
      }
      break;

    case INVOKE_VIDEO_SUPPORT_FUNCTION:
      LOG.I(TAG,"invokeEx INVOKE_VIDEO_SUPPORT_FUNCTION");
      {
        final int IS_SUPPORT_UNKNOWN       = -1;
        final int IS_SUPPORT_NONE          = 0;
        final int IS_SUPPORT_SLOW_MOTION   = (1 << 0);
        final int IS_SUPPORT_FF_RR         = (1 << 1);
        final int IS_SUPPORT_CAPTURE_FRAME = (1 << 2);
        final int IS_SUPPORT_SEAMLESS_LOOP = (1 << 3);

        int rtn = IS_SUPPORT_NONE;

        if(mbSlowMotionVideo)
        {
          rtn = (rtn | IS_SUPPORT_SLOW_MOTION);
        }

        reply.writeInt(rtn);
        reply.setDataPosition(0);
      }
      break;
    default:
      LOG.I(TAG,"invokeEx methodId = " + methodId);
      break;
    }
  }

  public boolean isPlaying() {
    return player.getPlayWhenReady();
  }

  private TrackInfo mInfos[] = null;
  private void initTrackInfo()
  {
    if (mInfos != null) return;

    int trackCount = 0;
    TrackInfo infos[] = null;
    if (player != null)
    {
      if (player.getTracks(DemoPlayer.TYPE_VIDEO) != null) trackCount++;
      if (player.getTracks(DemoPlayer.TYPE_AUDIO) != null) trackCount++;
      if (player.getTracks(DemoPlayer.TYPE_TEXT) != null)
      {
        String[] tracks = player.getTracks(DemoPlayer.TYPE_TEXT);
        trackCount += tracks.length;
      }

      infos = new TrackInfo[trackCount];

      int cur = 0;
      if (player.getTracks(DemoPlayer.TYPE_VIDEO) != null)
      {
        TrackInfo info = new TrackInfo(TrackInfo.MEDIA_TRACK_TYPE_VIDEO, new android.media.MediaFormat());
        infos[cur++] = info;
      }

      if (player.getTracks(DemoPlayer.TYPE_AUDIO) != null)
      {
        TrackInfo info = new TrackInfo(TrackInfo.MEDIA_TRACK_TYPE_AUDIO, new android.media.MediaFormat());
        infos[cur++] = info;
      }

      if (player.getTracks(DemoPlayer.TYPE_TEXT) != null)
      {
        String[] tracks = player.getTracks(DemoPlayer.TYPE_TEXT);

        for (int i=0; i < tracks.length; i++)
        {
          String language = tracks[i];
          if (language == null)
            language = "eng";

          if (language != null)
          {
            Locale locale = new Locale(language);

            String displayLanguage = (locale != null) ?locale.getDisplayLanguage() : language;

            if (language != null)
            {
              android.media.MediaFormat format = new android.media.MediaFormat();
              format.setString(android.media.MediaFormat.KEY_LANGUAGE, displayLanguage);
              TrackInfo info = new TrackInfo(TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, format);
              infos[cur++] = info;
            }
          }
        }
      }
    }
    mInfos = infos;
  }

  public TrackInfo[] getTrackInfoEx()
  {
    LOG.I(TAG,"getTrackInfo");

    initTrackInfo();

    return mInfos;
  }

  public void selectTrack(int index)
  {
    LOG.I(TAG,"selectTrack index " + index);
    if (mInfos == null) return;

    if (index < 2) return;

    int indexShift = index -2;
    player.selectTrack(DemoPlayer.TYPE_TEXT, indexShift);
  }

  public void setVolume (float leftVolume, float rightVolume)
  {
    if (player == null) return;

    try {
      player.setVolume(leftVolume, rightVolume);
    } catch (Exception e) {
      LOG.W(TAG, e);
    }
  }
  //**** OnErrorListener Start ***
  private IPlayer.OnErrorListener mUpperOnErrorListener = null;

  public void setOnErrorListener (IPlayer.OnErrorListener listener) {
    LOG.I(TAG,"setOnErrorListener");
    mUpperOnErrorListener = listener;
  }
  //**** OnErrorListener End ***

  //**** OnPreparedListener Start ***
  private IPlayer.OnPreparedListener mUpperOnPreparedListener = null;

  public void setOnPreparedListener (IPlayer.OnPreparedListener listener) {
    LOG.I(TAG,"setOnPreparedListener");
    mUpperOnPreparedListener = listener;
  }
  //**** OnPreparedListener End ***

  //**** OnInfoListener Start ***
  private IPlayer.OnInfoListener mUpperOnInfoListener = null;
  public void setOnInfoListener(IPlayer.OnInfoListener listener) {
    LOG.I(TAG,"setOnInfoListener");
    mUpperOnInfoListener = listener;
  }
  //**** OnInfoListener End ***
  //**** OnBufferingUpdateListener Start ***
  private IPlayer.OnBufferingUpdateListener mUpperOnBufferingUpdateListener = null;

  public void setOnBufferingUpdateListener(IPlayer.OnBufferingUpdateListener listener) {
    LOG.I(TAG,"setOnBufferingUpdateListener");
    mUpperOnBufferingUpdateListener = listener;
  }
  //**** OnBufferingUpdateListener End ***
  //**** OnSeekCompleteListener Start ***
  private IPlayer.OnSeekCompleteListener mUpperOnSeekCompleteListener = null;
  public void setOnSeekCompleteListener(IPlayer.OnSeekCompleteListener listener) {
    LOG.I(TAG,"setOnSeekCompleteListener");
    mUpperOnSeekCompleteListener = listener;
  }
  //**** OnSeekCompleteListener End ***
  //**** OnVideoSizeChangedListener Start ***
  private IPlayer.OnVideoSizeChangedListener mUpperOnVideoSizeChangedListener = null;

  public void setOnVideoSizeChangedListener(IPlayer.OnVideoSizeChangedListener listener) {
    LOG.I(TAG,"setOnVideoSizeChangedListener");
    mUpperOnVideoSizeChangedListener = listener;
  }
  //**** OnVideoSizeChangedListener End ***
  //**** OnTimedTextListener Start ***
  private IPlayer.OnTimedTextListener mUpperOnTimedTextListener = null;
  public void setOnTimedTextListener(IPlayer.OnTimedTextListener listener) {
    LOG.I(TAG,"setOnTimedTextListener");
    mUpperOnTimedTextListener = listener;
  }
  //**** OnTimedTextListener End ***
  //**** OnCompletionListener Start ***
  private IPlayer.OnCompletionListener mUpperOnCompletionListener = null;
  public void setOnCompletionListener(IPlayer.OnCompletionListener listener) {
    LOG.I(TAG,"setOnCompletionListener");
    mUpperOnCompletionListener = listener;
  }
  //**** OnCompletionListener End ***

  boolean mbPrepared = false;
  boolean mbBuffering = false;
  public static final int RENDERER_COUNT = 4;
  private SurfaceHolder mSurfaceHolder = null;
  private Context mContext = null;
  private int mVideoWidth = -1;
  private int mVideoHeight = -1;
  private String mMimeType = null;
  private Map<String, String> mHeaders = null;

  // send invoke to mediaplayer to set slow motion play mode
  private static final int INVOKE_VIDEO_SET_SLOW_MOTION = 8925;

  // send invoke to mediaplayer to get tge player slow motion speed 1x or slow
  private static final int INVOKE_VIDEO_GET_SLOW_MOTION_SPEED = 8927;

  private static final int INVOKE_VIDEO_SUPPORT_FUNCTION = 8935;

  private PowerManager.WakeLock mWakeLock = null;
  private boolean mbEnableSlowMotion1XMode = false;
  private boolean mbSlowMotionVideo = false;
  private float mbSlowMotionVideoMultiple = 2.0f;

  private boolean mbDLNA = false;

  private boolean checkSlowMotionRate(float rate) {
      if (mbSlowMotionVideoMultiple > 0.0f && rate > mbSlowMotionVideoMultiple) return false;

      return (rate == 1.0f || rate == 2.0f || rate == 4.0f);
  }

  private boolean checkSlowMotionRateEnable(float rate) {
      return (rate == 2.0f || rate == 4.0f);
  }
}
