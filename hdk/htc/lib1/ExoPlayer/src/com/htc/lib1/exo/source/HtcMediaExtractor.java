/*
 * This is merged from com.google.android.exoplayer.source.FrameworkSampleExtractor.java
 */
package com.htc.lib1.exo.source;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.SampleHolder;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.drm.DrmInitData;
import com.google.android.exoplayer.TrackInfo;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.util.Assertions;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.Util;
import com.htc.lib1.exo.player.DataSourceHandler;
import com.htc.lib1.exo.player.SlowMotionDataSource;
//import com.htc.lib1.exo.http.HTTPMediaDataSource;
import com.htc.lib1.exo.utilities.LOG;
import com.htc.lib1.exo.utilities.MimeTypeChecker;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaExtractor;
import android.net.Uri;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

/** {@link SampleExtractor} that extracts samples from a stream using {@link MediaExtractor}. */
// TODO: This implementation needs to be fixed so that its methods are non-blocking (either
// through use of a background thread, or through changes to the framework's MediaExtractor API).
@TargetApi(16)
public final class HtcMediaExtractor implements SampleExtractor {

  // Parameters for a Uri data source.
  private final Context context;
  private final Uri uri;
  private final Map<String, String> headers;

  // Parameters for a FileDescriptor data source.
  private final FileDescriptor fileDescriptor;
  private final long fileDescriptorOffset;
  private final long fileDescriptorLength;

  private final MediaExtractor mediaExtractor;

  private TrackInfo[] trackInfos;

  private String TAG = "hExtractor";

  private boolean mbSlowMotionVideo = false;
  private boolean mbDLNAVideo = false;
  private DataSourceHandler mDataSourceHandler = null;
  private String mimeType = null;

  /**
   * Instantiates a new sample extractor reading from the specified {@code uri}.
   *
   * @param context Context for resolving {@code uri}.
   * @param uri The content URI from which to extract data.
   * @param headers Headers to send with requests for data.
   */
  public HtcMediaExtractor(Context context, Uri uri, Map<String, String> headers) {
    Assertions.checkState(Util.SDK_INT >= 16);

    this.context = Assertions.checkNotNull(context);
    this.uri = Assertions.checkNotNull(uri);
    this.headers = headers;

    fileDescriptor = null;
    fileDescriptorOffset = 0;
    fileDescriptorLength = 0;

    mediaExtractor = new MediaExtractor();
    checkHeader(context, uri, headers);
  }

  /**
   * Instantiates a new sample extractor reading from the specified seekable {@code fileDescriptor}.
   * The caller is responsible for releasing the file descriptor.
   *
   * @param fileDescriptor File descriptor from which to read.
   * @param offset The offset in bytes into the file where the data to be extracted starts.
   * @param length The length in bytes of the data to be extracted.
   */
  public HtcMediaExtractor(FileDescriptor fileDescriptor, long offset, long length) {
    LOG.I(TAG,"HtcMediaExtractor()");
    Assertions.checkState(Util.SDK_INT >= 16);

    context = null;
    uri = null;
    headers = null;

    this.fileDescriptor = Assertions.checkNotNull(fileDescriptor);
    fileDescriptorOffset = offset;
    fileDescriptorLength = length;

    mediaExtractor = new MediaExtractor();
  }

  @Override
  public boolean prepare() throws IOException {
    LOG.I(TAG,"prepare()");
    if (context != null) {
      if (mbSlowMotionVideo) {
        doSetDataSourceEx(context,uri,headers, new SlowMotionDataSource());
      /*} else if (MimeTypeChecker.isHTTPProgressive(uri, mimeType)) {
        doSetDataSourceEx(context, uri, headers, new HTTPMediaDataSource());*/
      } else {
        mediaExtractor.setDataSource(context, uri, headers);
      }
    } else {
      mediaExtractor.setDataSource(fileDescriptor, fileDescriptorOffset, fileDescriptorLength);
    }

    int trackCount = mediaExtractor.getTrackCount();
    trackInfos = new TrackInfo[trackCount];
    for (int i = 0; i < trackCount; i++) {
      android.media.MediaFormat format = mediaExtractor.getTrackFormat(i);
      long durationUs = format.containsKey(android.media.MediaFormat.KEY_DURATION)
          ? format.getLong(android.media.MediaFormat.KEY_DURATION) : C.UNKNOWN_TIME_US;
      String mime = format.getString(android.media.MediaFormat.KEY_MIME);
      trackInfos[i] = new TrackInfo(mime, durationUs);
    }

    return true;
  }

  /*@Override
  public TrackInfo[] getTrackInfos() {
    return trackInfos;
  }*/

  @Override
  public void selectTrack(int index) {
    LOG.I(TAG,"selectTrack(" + index + ")");
    mediaExtractor.selectTrack(index);
  }

  @Override
  public void deselectTrack(int index) {
    LOG.I(TAG,"deselectTrack(" + index + ")");
    mediaExtractor.unselectTrack(index);
  }

  @Override
  public long getBufferedPositionUs() {
    long bufferedDurationUs = mediaExtractor.getCachedDuration();
    if (bufferedDurationUs == -1) {
      return TrackRenderer.UNKNOWN_TIME_US;
    } else {
      long sampleTime = mediaExtractor.getSampleTime();
      return sampleTime == -1 ? TrackRenderer.END_OF_TRACK_US : sampleTime + bufferedDurationUs;
    }
  }

  @Override
  public void seekTo(long positionUs) {
    LOG.I(TAG,"seekTo(" + positionUs + ")");
    mediaExtractor.seekTo(positionUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
  }

  /*@Override
  public void getTrackMediaFormat(int track, MediaFormatHolder mediaFormatHolder) {
    mediaFormatHolder.format =
        MediaFormat.createFromFrameworkMediaFormatV16(mediaExtractor.getTrackFormat(track));
    mediaFormatHolder.drmInitData = Util.SDK_INT >= 18 ? getPsshInfoV18() : null;
  }*/

  @Override
  public int readSample(int track, SampleHolder sampleHolder) {
    int sampleTrack = mediaExtractor.getSampleTrackIndex();
    if (sampleTrack != track) {
      return sampleTrack < 0 ? SampleSource.END_OF_STREAM : SampleSource.NOTHING_READ;
    }

    if (sampleHolder.data != null) {
      int offset = sampleHolder.data.position();
      sampleHolder.size = mediaExtractor.readSampleData(sampleHolder.data, offset);
      sampleHolder.data.position(offset + sampleHolder.size);
      //LOG.I(TAG,"readSample(" + track + "," + offset + "+" + sampleHolder.size + " =" + sampleHolder.data.position() + ")");
    } else {
      sampleHolder.size = 0;
    }
    sampleHolder.timeUs = mediaExtractor.getSampleTime();
    sampleHolder.flags = mediaExtractor.getSampleFlags();
    if ((sampleHolder.flags & MediaExtractor.SAMPLE_FLAG_ENCRYPTED) != 0) {
      sampleHolder.cryptoInfo.setFromExtractorV16(mediaExtractor);
    }

    mediaExtractor.advance();

    return SampleSource.SAMPLE_READ;
  }

  @Override
  public final void release() {
    LOG.I(TAG,"release()");
    if (mediaExtractor != null)
    {
      mediaExtractor.release();
    }

    if (mDataSourceHandler != null)
    {
      mDataSourceHandler.release();
    }
  }

  @TargetApi(18)
  private Map<UUID, byte[]> getPsshInfoV18() {
    Map<UUID, byte[]> psshInfo = mediaExtractor.getPsshInfo();
    return (psshInfo == null || psshInfo.isEmpty()) ? null : psshInfo;
  }

  private void checkHeader(Context context, Uri uri, Map<String, String> headers)
  {
    LOG.I(TAG,"setDataSource() 1 uri = " + uri);

    if (headers != null)
    {
      if (headers != null  )
      {
        if (headers.containsKey("x-htc-mimetype"))
        {
          mimeType = headers.get("x-htc-mimetype");
        }
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
      else if (headers.containsKey("x-htc-dlna"))
      {
        String isDLNAVideo = headers.get("x-htc-dlna");
        LOG.I(TAG," isDLNAVideo = " + isDLNAVideo);
        if (isDLNAVideo != null && isDLNAVideo.equals("1"))
        {
          mbDLNAVideo = true;
        }
      }
    }
  }

  private void doSetDataSourceEx(Context context, Uri uri, Map<String, String> headers, DataSourceHandler dataSourceHandler) {
    Class<?> interfaze = null;
    try {

      Method[] methods = MediaExtractor.class.getDeclaredMethods();
      Method setDataSourceMethod = null;
      for(Method method : methods)
      {

        if (method.getName().endsWith("setDataSource"))
        {
          Type[] types = method.getGenericParameterTypes();
          for(Type type : types) {
            if (type.toString().equals("interface android.media.DataSource")) {
              LOG.I(TAG, "DataSource() find method " + method.getName() + ",type"+type.toString());
              setDataSourceMethod = method;
              break;
            }
          }
        }
      }

      if (setDataSourceMethod == null) return ;
      interfaze = Class.forName("android.media.DataSource");
      if (interfaze == null) return;
      if (interfaze.isInterface() == false) return;

      Class<?>[] classArray = new Class<?>[1];
      classArray[0] = interfaze;

      mDataSourceHandler = dataSourceHandler;
      if (mDataSourceHandler == null) return;

      mDataSourceHandler.init(context, uri, headers);

      LOG.I(TAG, "DataSource isInterface = " + interfaze.isInterface());
      Object mDataSource = Proxy.newProxyInstance(interfaze.getClassLoader(), classArray, (InvocationHandler) mDataSourceHandler);
      if (mDataSource == null) return;

      setDataSourceMethod.invoke(mediaExtractor, mDataSource);

    } catch (Exception e) {
      LOG.W(TAG, e);
    }
  }

@Override
public int getTrackCount() {
    if (mediaExtractor != null)
        return mediaExtractor.getTrackCount();
    
    return 0;
}

@Override
public MediaFormat getMediaFormat(int track) {
    if (mediaExtractor != null)
        return MediaFormat.createFromFrameworkMediaFormatV16(mediaExtractor.getTrackFormat(track));
    
    return null;
}

@TargetApi(18)
private DrmInitData getDrmInitDataV18() {
  // MediaExtractor only supports psshInfo for MP4, so it's ok to hard code the mimeType here.
  Map<UUID, byte[]> psshInfo = mediaExtractor.getPsshInfo();
  if (psshInfo == null || psshInfo.isEmpty()) {
    return null;
  }
  DrmInitData.Mapped drmInitData = new DrmInitData.Mapped(MimeTypes.VIDEO_MP4);
  drmInitData.putAll(psshInfo);
  return drmInitData;
}

@Override
public DrmInitData getDrmInitData(int track) {
    return Util.SDK_INT >= 18 ? getDrmInitDataV18() : null;
}
}
