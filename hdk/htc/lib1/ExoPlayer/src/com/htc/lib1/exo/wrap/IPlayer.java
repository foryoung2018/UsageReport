package com.htc.lib1.exo.wrap;

import android.media.TimedText;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Parcel;
import android.view.SurfaceHolder;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

interface IPlayer
{

    /*static public class TrackInfo {
        final int mTrackType;
        final MediaFormat mFormat;
        
        public static final int MEDIA_TRACK_TYPE_UNKNOWN = android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_UNKNOWN;
        public static final int MEDIA_TRACK_TYPE_VIDEO = android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO;
        public static final int MEDIA_TRACK_TYPE_AUDIO = android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO;
        public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT;

        public static final int MEDIA_TRACK_TYPE_SUBTITLE =  4; //android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT

    	public TrackInfo(android.media.MediaPlayer.TrackInfo info)
    	{
            if (info != null)
            {
                mTrackType = info.getTrackType();
                mFormat = info.getFormat();
            }else{
                mTrackType = MEDIA_TRACK_TYPE_UNKNOWN;
                mFormat = null;
            }
    	}

    	public TrackInfo()
        {
            mTrackType = MEDIA_TRACK_TYPE_UNKNOWN;
            mFormat = null;
        }

    	public TrackInfo(int type, MediaFormat format)
        {
            mTrackType = type;
            mFormat = format;
        }

        public String getLanguage() {
            if (mFormat == null) return "und";

            String language = mFormat.getString(MediaFormat.KEY_LANGUAGE);
            return language == null ? "und" : language;
        }

        public MediaFormat getFormat() {
            if (mTrackType == MEDIA_TRACK_TYPE_TIMEDTEXT
                    || mTrackType == MEDIA_TRACK_TYPE_SUBTITLE) {
                return mFormat;
            }
            return null;
        }
    	
        public int getTrackType()
        {
        	return mTrackType;
        }
    }*/

    /**
     * Sets the data source as a content Uri with 
     * 
     * @param uri the Content URI of the data you want to play
     * @param headers the headers to be sent together with the request for the data,or null value if there is no.
     */
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException ;

    public void setDataSource(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException ;

    public void setDataSource(Context context, Uri uri) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException ;

    public void setDataSource(FileDescriptor fd, long offset, long length) throws IllegalArgumentException, IllegalStateException, IOException ;

    public void	setDataSource(FileDescriptor fd) throws IllegalArgumentException, IllegalStateException, IOException;
    
    /**
     * Get the current position of the clip, default value is -1, 
     * we may get this value in all state.
     *
     * @return the current position in milliseconds
     */
    public int getCurrentPosition();

    /**
     * Get total duration of the clip, default value is -1 
     * we may get this value after STATE_PREPARED.
     *
     * @return the duration in milliseconds
     */
    public int getDuration();

    /**
     * Seeks to specified time position.
     * 
     * @param nPos the offset in milliseconds from the start to seek to
     */
    public void seekTo(int nPos);
    
    /**
     * Stops playback after playback has been stopped or paused and then release MediaPlayer.
     */
    public void stop();
    
    /**
     * Pauses playback. Call play() to resume.
     */
    public void pause();
    
    /**
     * Starts or resumes playback.
     */
    public void start();
    
    public void reset();

    public void release();

    public void prepare() throws IllegalStateException, IOException;

    public void prepareAsync() throws IllegalStateException;

    public int getVideoWidth();

    public int getVideoHeight();

    public void setWakeMode (Context context, int mode);

    public int getAudioSessionId();

    public void selectTrack(int index);

    public void deselectTrack(int index);

    public void invokeEx(Parcel request, Parcel reply);

    public void setDisplay(SurfaceHolder sh);

    public void setVolume (float leftVolume, float rightVolume);

    public boolean isPlaying ();

    public TrackInfo[] getTrackInfoEx();

    public interface OnPreparedListener
    {
        void onPrepared();
    }

    public void setOnPreparedListener (OnPreparedListener listener) ;

    public interface OnErrorListener
    {
        boolean onError(int what, int extra);
    }

    public void setOnErrorListener (OnErrorListener listener);

    public interface OnInfoListener
    {
        boolean onInfo(int what, int extra);
    }

    public void setOnInfoListener(OnInfoListener listener);

    public interface OnBufferingUpdateListener
    {

        void onBufferingUpdate(int percent);
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener);

    public interface OnSeekCompleteListener
    {
        public void onSeekComplete();
    }
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener);

    public interface OnVideoSizeChangedListener
    {
        public void onVideoSizeChanged(int width, int height);
    }
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener);

    public interface OnTimedTextListener
    {
        public void onTimedText(TimedText text);

        public void onTimedText(String text);
    }
    public void setOnTimedTextListener(OnTimedTextListener listener);

    public interface OnCompletionListener
    {
        void onCompletion();
    }
    public void setOnCompletionListener(OnCompletionListener listener);

    public Bitmap getAlbumArt();
}
