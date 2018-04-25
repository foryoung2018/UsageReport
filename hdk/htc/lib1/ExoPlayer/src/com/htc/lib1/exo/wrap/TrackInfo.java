package com.htc.lib1.exo.wrap;
import android.annotation.TargetApi;
import android.media.MediaFormat;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class TrackInfo {
    final int mTrackType;
    final MediaFormat mFormat;
    
    public static final int MEDIA_TRACK_TYPE_UNKNOWN = android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_UNKNOWN;
    public static final int MEDIA_TRACK_TYPE_VIDEO = android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO;
    public static final int MEDIA_TRACK_TYPE_AUDIO = android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO;
    public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT;
    /** @hide */
    public static final int MEDIA_TRACK_TYPE_SUBTITLE = /*android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT*/ 4;

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
}
