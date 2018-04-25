package com.htc.lib1.exo.wrap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.view.SurfaceHolder;

import com.htc.lib1.exo.parser.mp4.HtcVideoChecker;
import com.htc.lib1.exo.utilities.LOG;
import com.htc.lib1.exo.utilities.MimeTypeChecker;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class HtcHalMediaPlayer implements IPlayer
{
    private static String TAG = "HtcHalMediaPlayer";
    private HtcWrapMediaPlayer mWrapMediaPlayer = null;
    private HtcWrapExoPlayer mWrapExoPlayer = null;
    private IPlayer mCurrentPlayer = null;
    private boolean bUsingMediaPlayer = true;   
    private boolean bUsingAwesomePlayer = false;

    private static final int ANDROID_HTC_INVOKE_FORCE_AWESOME_SETDATASOURCE = 8990;


    public static final String  MEDIA_MIMETYPE_TEXT_SMPTETT = HtcWrapMediaPlayer.MEDIA_MIMETYPE_TEXT_SMPTETT;
    public static final String  MEDIA_MIMETYPE_TEXT_SUBRIP = HtcWrapMediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP;

    private LinkedList<Runnable> mJobAfterDataSource = new LinkedList<Runnable>();
    //**** OnErrorListener Start ***
    public void setOnErrorListener (final IPlayer.OnErrorListener listener)
    {
        LOG.I(TAG,"setOnErrorListener");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setOnErrorListener(listener);
        } else{
            mJobAfterDataSource.add(new Runnable()
            {
                @Override
                public void run()
                {
                    LOG.I(TAG,"setOnErrorListener delay");
                    if (mCurrentPlayer != null) mCurrentPlayer.setOnErrorListener(listener);
                }
            });
        }
    }
    //**** OnErrorListener End ***

    //**** OnPreparedListener Start ***

    public void setOnPreparedListener (final IPlayer.OnPreparedListener listener)
    {
        LOG.I(TAG,"setOnPreparedListener in");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setOnPreparedListener(listener);
        } else{
            mJobAfterDataSource.add(new Runnable()
            {
                @Override
                public void run()
                {
                   if (mCurrentPlayer != null) mCurrentPlayer.setOnPreparedListener(listener);
                }
            });
        }
    }
    //**** OnPreparedListener End ***

    //**** OnInfoListener Start ***
    public void setOnInfoListener(final IPlayer.OnInfoListener listener)
    {
        LOG.I(TAG,"setOnInfoListener");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setOnInfoListener(listener);
        } else{
            mJobAfterDataSource.add(new Runnable()
            {
                @Override
                public void run()
                {
                    LOG.I(TAG,"setOnInfoListener delay");
                    if (mCurrentPlayer != null) mCurrentPlayer.setOnInfoListener(listener);
                }
            });
        }
    }
    //**** OnInfoListener End ***


    //**** OnBufferingUpdateListener Start ***

    public void setOnBufferingUpdateListener(final IPlayer.OnBufferingUpdateListener listener)
    {
        LOG.I(TAG,"setOnInfoListener");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setOnBufferingUpdateListener(listener);
        } else{
            mJobAfterDataSource.add(new Runnable()
            {
                @Override
                public void run()
                {
                    LOG.I(TAG,"setOnBufferingUpdateListener delay");
                    if (mCurrentPlayer != null) mCurrentPlayer.setOnBufferingUpdateListener(listener);
                }
            });
        }
    }

    //**** OnBufferingUpdateListener End ***

    //**** OnSeekCompleteListener Start ***
    public void setOnSeekCompleteListener(final IPlayer.OnSeekCompleteListener listener)
    {
        LOG.I(TAG,"setOnSeekCompleteListener");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setOnSeekCompleteListener(listener);
        } else{
            mJobAfterDataSource.add(new Runnable()
            {
                @Override
                public void run()
                {
                    LOG.I(TAG,"setOnSeekCompleteListener delay");
                    if (mCurrentPlayer != null) mCurrentPlayer.setOnSeekCompleteListener(listener);
                }
            });
        }
    }
    //**** OnSeekCompleteListener End ***

    //**** OnVideoSizeChangedListener Start ***
    public void setOnVideoSizeChangedListener(final IPlayer.OnVideoSizeChangedListener listener)
    {
        LOG.I(TAG,"setOnVideoSizeChangedListener");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setOnVideoSizeChangedListener(listener);
        } else{
            mJobAfterDataSource.add(new Runnable()
            {
                @Override
                public void run()
                {
                    LOG.I(TAG,"setOnVideoSizeChangedListener delay");
                    if (mCurrentPlayer != null) mCurrentPlayer.setOnVideoSizeChangedListener(listener);
                }
            });
        }
    }
    //**** OnVideoSizeChangedListener End ***

    //**** OnTimedTextListener Start ***
    public void setOnTimedTextListener(final IPlayer.OnTimedTextListener listener)
    {
        LOG.I(TAG,"setOnTimedTextListener");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setOnTimedTextListener(listener);
        } else{
            mJobAfterDataSource.add(new Runnable()
            {
                @Override
                public void run()
                {
                    LOG.I(TAG,"setOnTimedTextListener delay");
                    if (mCurrentPlayer != null) mCurrentPlayer.setOnTimedTextListener(listener);
                }
            });
        }
    }
    //**** OnTimedTextListener End ***

    //**** OnCompletionListener Start ***
    public void setOnCompletionListener(final IPlayer.OnCompletionListener listener)
    {
        LOG.I(TAG,"setOnCompletionListener");
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setOnCompletionListener(listener);
        } else{
            mJobAfterDataSource.add(new Runnable()
            {
                @Override
                public void run()
                {
                    LOG.I(TAG,"setOnCompletionListener delay");
                    if (mCurrentPlayer != null) mCurrentPlayer.setOnCompletionListener(listener);
                }
            });
        }
    }
    //**** OnCompletionListener End ***



    public HtcHalMediaPlayer(Handler handler)
    {
        LOG.I(TAG,LOG.getOneLineInfo());
    }

    public HtcHalMediaPlayer()
    {
        LOG.I(TAG,LOG.getOneLineInfo());
    }

    private void init(FileDescriptor fd)
    {
        _init();
    }

    private void init(String path)
    {
        init(null, Uri.parse(path), null);
    }

    private void init(Context context, Uri uri,Map<String, String> headers /*String mimeType*/)
    {
        String mimeType = null;

        if (headers != null  )
        {
            if (headers.containsKey("x-htc-mimetype"))
            {
                mimeType = headers.get("x-htc-mimetype");
                LOG.I(TAG," mimeType = " + mimeType);
            }

            if (headers.containsKey("x-htc-prefer_exoplayer"))
            {
                String preferExoplayer = headers.get("x-htc-prefer_exoplayer");
                LOG.I(TAG," preferExoplayer = " + preferExoplayer);
                
                if (preferExoplayer != null && preferExoplayer.equals("1"))
                {
                    bUsingMediaPlayer = false;
                }
            }

            if (headers.containsKey("x-htc-use_awesome"))
            {
                String useAwesome = headers.get("x-htc-use_awesome");
                LOG.I(TAG," useAwesome = " + useAwesome);

                if (useAwesome != null && useAwesome.equals("1"))
                {
                    bUsingAwesomePlayer = true;
                }
            }
        }

        if (isStreaming(uri) == false)
        {
            HtcVideoChecker checker = new HtcVideoChecker();
            checker.parse(context, uri);

            boolean bSlowMotion = checker.isSlowMotionVideo();

            if (bSlowMotion){
                int version = (int) checker.getSlowMotionVersion();
                version = Math.max(version,1);
                LOG.I(TAG,"x-htc-slowmotion-version = " + version);

                int multiple = (int) checker.getSlowMotionMultiple();
                multiple = Math.min(Math.max(multiple,1),4);
                LOG.I(TAG,"x-htc-slowmotion-multiple = " + multiple);

                int audioOffset = (int) checker.getSlowMotionAudioOffset();
                LOG.I(TAG,"x-htc-slowmotion-audiooffset = " + audioOffset);

                if (headers == null)
                    headers = new HashMap<String, String>();

                if (headers != null)
                {
                    headers.put("x-htc-slowmotion", "1");
                    headers.put("x-htc-slowmotion-version", Integer.toString(version));
                    headers.put("x-htc-slowmotion-multiple", Integer.toString(multiple));
                    headers.put("x-htc-slowmotion-audiooffset", Integer.toString(audioOffset));
                }
            }
        }

        if (MimeTypeChecker.isRTSPorSDP(uri, mimeType) /*|| MimeTypeChecker.isHLS(uri, mimeType)*/)
        {
            //Force use MediaPlayer
            bUsingMediaPlayer = true;
        }
        else if (MimeTypeChecker.isSmoothStreaming(uri, mimeType) || MimeTypeChecker.isDash(uri, mimeType))
        {
            //Force use ExoPlayer
            bUsingMediaPlayer = false;
        }

        _init();
    }

    private void _init()
    {
        if (bUsingMediaPlayer && mWrapMediaPlayer == null)
        {
            mWrapMediaPlayer = new HtcWrapMediaPlayer();

            mCurrentPlayer = mWrapMediaPlayer;

            if(bUsingAwesomePlayer) {
               try {
                     Parcel req = Parcel.obtain();
                     req.writeInterfaceToken("android.media.IMediaPlayer");
                     req.writeInt(ANDROID_HTC_INVOKE_FORCE_AWESOME_SETDATASOURCE);

                     Parcel rep = Parcel.obtain();

                     invokeEx(req, rep);

                     rep.recycle();
                     req.recycle();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
            }

        }
        else if (bUsingMediaPlayer == false && mWrapExoPlayer == null)
        {
            mWrapExoPlayer = new HtcWrapExoPlayer();

            mCurrentPlayer = mWrapExoPlayer;
        }
        doPostDataSource();
    }

    private boolean isStreaming(Uri uri){
        if (null != uri && 
            ("rtsp".equalsIgnoreCase(uri.getScheme()) ||
             "http".equalsIgnoreCase(uri.getScheme()) || 
             "https".equalsIgnoreCase(uri.getScheme()) || 
             "dtcp".equalsIgnoreCase(uri.getScheme())
            )
           )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void invokeEx(Parcel request, Parcel reply){
        LOG.I(TAG,LOG.getOneLineInfo());
        if (mCurrentPlayer != null)
            mCurrentPlayer.invokeEx(request, reply);
    }
    public Bitmap captureFrameEx(){
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) return mWrapMediaPlayer.captureFrameEx();

        return null;
    }

    protected boolean stepFrameEx(boolean bForward){
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) return mWrapMediaPlayer.stepFrameEx(bForward);

        return false;
    }

    public void setCharsetEx(String charset){
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setCharsetEx(charset);

    }

    public void deselectLanguageEx(String language){
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.deselectLanguageEx(language);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void addTimedTextSource(Context context, Uri uri, String mimeType) throws IllegalArgumentException, IllegalStateException, IOException
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.addTimedTextSource(context, uri, mimeType);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void addTimedTextSourceEx(String path, String mimeType) throws IllegalArgumentException, IllegalStateException, IOException
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.addTimedTextSource(path, mimeType);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void addTimedTextSource(FileDescriptor fd, long offset, long length, String mimeType)
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.addTimedTextSource(fd, offset, length, mimeType);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void addTimedTextSource(FileDescriptor fd, String mimeType)
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.addTimedTextSource(fd, mimeType);
    }

    public Object getMetadataEx(final boolean update_only,
                                final boolean apply_filter){
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) return mWrapMediaPlayer.getMetadataEx(update_only, apply_filter);

        return null;
    }

    public Bitmap getAlbumArt()
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (mCurrentPlayer != null) {
            return mCurrentPlayer.getAlbumArt();
        }
        return null;
    }

    public Object getMediaTimeProviderEx()
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) return mWrapMediaPlayer.getMediaTimeProviderEx();

        return null;
    }

    public void setSubtitleAnchorEx(/*SubtitleController*/ Object controller,  /*SubtitleController.Anchor*/ Object anchor) {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setSubtitleAnchorEx(controller, anchor);
    }

    public void deselectTrack(int index)
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (mCurrentPlayer != null)
            mCurrentPlayer.deselectTrack(index);
    }

    public int getCurrentPosition()
    {
        int rtn = -1;
        if (mCurrentPlayer != null)
            rtn = mCurrentPlayer.getCurrentPosition();
       
        LOG.I(TAG,"getCurrentPosition() = " + rtn);
        return rtn;
    }

    public int getDuration()
    {
        int rtn = -1;
        if (mCurrentPlayer != null)
            rtn = mCurrentPlayer.getDuration();

        LOG.I(TAG,"getDuration() = " + rtn);      
        return rtn;
    }

    public TrackInfo[] 	getTrackInfoEx()
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        TrackInfo[] rtn = null;
        try
        {
            if (mCurrentPlayer != null)
                rtn = mCurrentPlayer.getTrackInfoEx();
        }
        catch (Exception ex)
        {
            LOG.W(TAG, "getTrackInfoEx() get Exception");
            ex.printStackTrace();
        }

        return rtn;
    }

    public boolean isHTCDevice()
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) return mWrapMediaPlayer.isHTCDevice();
       
        return false;
    }

    public void pause()
    {
        LOG.I(TAG,"pause ");
        if (mCurrentPlayer != null) mCurrentPlayer.pause();
    }

    public void prepare() throws IllegalStateException, IOException
    {
        LOG.I(TAG,"prepare ");
        if (mCurrentPlayer != null) mCurrentPlayer.pause();
    }

    public void prepareAsync() throws IllegalStateException
    {
        LOG.I(TAG,"prepareAsync ");
        if (mCurrentPlayer != null) mCurrentPlayer.prepareAsync();
    }

    public void start()
    {
        LOG.I(TAG,"start ");
        if (mCurrentPlayer != null) mCurrentPlayer.start();
    }

    public void stop()
    {
        LOG.I(TAG,"stop ");
        if (mCurrentPlayer != null) mCurrentPlayer.stop();
    }

    public void release()
    {
        LOG.I(TAG,"release ");
        if (mCurrentPlayer != null) mCurrentPlayer.release();
    }

    public void reset()
    {
        LOG.I(TAG,"reset ");
        if (mCurrentPlayer != null) mCurrentPlayer.reset();
    }

    public void seekTo(int msec)
    {
        LOG.I(TAG,"seekTo " + msec);
        if (mCurrentPlayer != null) mCurrentPlayer.seekTo(msec);
    }
    public void selectTrack(int index)
    {
        LOG.I(TAG,"selectTrack " + index);
        if (mCurrentPlayer != null) mCurrentPlayer.selectTrack(index);
    }

    public void setAudioSessionId(int sessionId)
    {
        LOG.I(TAG,"setAudioSessionId " + sessionId);
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setAudioSessionId(sessionId);
    }

    public void setAudioStreamType(int streamtype)
    {
        LOG.I(TAG,"setAudioStreamType " + streamtype);
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setAudioStreamType(streamtype);
    }

    public void setAuxEffectSendLevel(float level)
    {
        LOG.I(TAG,"setAuxEffectSendLevel " + level);
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setAuxEffectSendLevel(level);
    }

    public void setDataSource(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        init(path);
        if (mCurrentPlayer != null) mCurrentPlayer.setDataSource(path);
    }

    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        String mimeType = null;

        init(context, uri, headers);
        if (mCurrentPlayer != null) mCurrentPlayer.setDataSource(context, uri, headers);
    }

    public void setDataSource(Context context, Uri uri) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        init(context, uri, null);
        if (mCurrentPlayer != null) mCurrentPlayer.setDataSource(context, uri);
    }

    public void setDataSource(FileDescriptor fd, long offset, long length) throws IllegalArgumentException, IllegalStateException, IOException
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        init(fd);
        if (mCurrentPlayer != null) mCurrentPlayer.setDataSource(fd, offset, length);
    }

    public void	setDataSource(FileDescriptor fd) throws IllegalArgumentException, IllegalStateException, IOException
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        init(fd);
        if (mCurrentPlayer != null) mCurrentPlayer.setDataSource(fd);
    }
    private void doPostDataSource()
    {
        synchronized(mJobAfterDataSource)
        {
        	while (mJobAfterDataSource.isEmpty() == false)
        	{
                LOG.D(TAG, "doPostDataSource job");
                Runnable job = mJobAfterDataSource.remove();
                job.run();
        	}
        }
    }

    public void setDisplay(SurfaceHolder sh)
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (mCurrentPlayer != null) mCurrentPlayer.setDisplay(sh);
    }

    public void setVolume (float leftVolume, float rightVolume)
    {
        LOG.I(TAG, "setVolume(" + leftVolume + "," + rightVolume + ")");
        if (mCurrentPlayer != null) mCurrentPlayer.setVolume(leftVolume, rightVolume);
    }

    public void	setLooping(boolean looping)
    {
        LOG.I(TAG,"setLooping " + looping);
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setLooping(looping);
    }

    public boolean isPlaying ()
    {
        boolean rtn = false;
        if (mCurrentPlayer != null) rtn = mCurrentPlayer.isPlaying();

        LOG.I(TAG,"isPlaying " + rtn);
        return rtn;
    }

    public int getAudioSessionId()
    {
        int rtn = -1;
        if (mCurrentPlayer != null) rtn = mCurrentPlayer.getAudioSessionId();

        LOG.I(TAG,"getAudioSessionId " + rtn);
        return rtn;
    }

    public int getVideoWidth()
    {
        int rtn = -1;
        if (mCurrentPlayer != null) rtn = mCurrentPlayer.getVideoWidth();

        LOG.I(TAG,"getVideoWidth " + rtn);
        return rtn;
    }

    public int getVideoHeight()
    {
        int rtn = -1;
        if (mCurrentPlayer != null) rtn = mCurrentPlayer.getVideoHeight();

        LOG.I(TAG,"getVideoHeight " + rtn);
        return rtn;
    }

    public void setWakeMode (Context context, int mode)
    {
        LOG.I(TAG,"setVideoScalingMode mode = " + mode);
        if (mCurrentPlayer != null) mCurrentPlayer.setWakeMode(context, mode);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void	setNextMediaPlayer(MediaPlayer next)
    {
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setNextMediaPlayer(next);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setVideoScalingMode (int mode)
    {
        LOG.I(TAG,"setVideoScalingMode mode = " + mode);
        LOG.I(TAG,LOG.getOneLineInfo());
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setVideoScalingMode(mode);
    }
    public void setScreenOnWhilePlaying (boolean screenOn)
    {
        LOG.I(TAG,"setScreenOnWhilePlaying " + screenOn);
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setScreenOnWhilePlaying(screenOn);
    }

    public void setOnClosedCaptionListener(InvocationHandler handler)
    {
        LOG.I(TAG,"setOnClosedCaptionListener");
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setOnClosedCaptionListener(handler);
    }

    public void setAnchorToMediaPlayer(Context context, Object root)
    {
        LOG.I(TAG,"setAnchorToMediaPlayer");
        if (mCurrentPlayer == null ) return;
        if (bUsingMediaPlayer)
        {
            if (mWrapMediaPlayer != null)
            {
                mWrapMediaPlayer.setAnchorToMediaPlayer(context, root);
            }else
            {
                LOG.I(TAG,"setAnchorToMediaPlayer no mWrapMediaPlayer");
            }
        }else
        {
            LOG.I(TAG,"setAnchorToMediaPlayer ExoPlayer not support");
        }
    }
    public boolean isSeekable()
    {
        LOG.I(TAG,"isSeekabke");
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) return mWrapMediaPlayer.isSeekable();
        
        return true;
    }
    public boolean isPauseable()
    {
        LOG.I(TAG,"isPauseable");
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) return mWrapMediaPlayer.isPauseable();
        
        return true;
    }
    public void setPlaybackParams(/*PlaybackParams (API:23)*/ Object params)
    {
        LOG.I(TAG,"setPlaybackParams");
        if (bUsingMediaPlayer)
            if (mWrapMediaPlayer != null) mWrapMediaPlayer.setPlaybackParams(params);
    }
}
