package com.htc.lib1.weather.resource;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.htc.lib0.htcdebugflag.HtcWrapHtcDebugFlag;
import com.htc.lib1.weather.R;

import java.io.IOException;

/**
 * Weather SoundEffect
 */
public class SoundEffect implements OnCompletionListener {
    /** LOG_FLAG */
    public static final boolean LOG_FLAG = HtcWrapHtcDebugFlag.Htc_DEBUG_flag;
    /** LOG_TAG */
    public static final String  LOG_TAG  = "WeatherSound";
	private Context mContext;
	private Resources mRes;
	private MediaPlayer mMediaPlayer;
	private AudioManager mAudioManager;
	private boolean mbSoundOn = true;
	private static int pathRaw[] = { R.raw.sound_clouds, R.raw.sound_cold,
		R.raw.sound_fog, R.raw.sound_hot, R.raw.sound_showers,
		R.raw.sound_snow, R.raw.sound_thunder, R.raw.sound_windy,R.raw.sound_sunny, 0 };
	
	private final static int mMapToWeatherSound[] = { 9, 
		8, 8, 8, 0, 9, 0, 0, 9, 9, 9, 
		2, 4, 4, 4, 6, 6, 6, 4, 9, 9, 
		9, 5, 5, 1, 9, 4, 9, 9, 4, 3, 
		1, 7, 9, 9, 0, 0, 9, 0, 4, 4, 
		6, 6, 9, 9, 9, 9, 9, 9, 9, 9,
		6, 2, 2, 6 };
	/** SETTING_KEY_SYNC_SOUND */
	public static String SETTING_KEY_SYNC_SOUND = "com.htc.Weather.SoundsMap";
	
	private static final int STATE_INIT = 10;
	private static final int STATE_PLAYING = 11;
	private static final int STATE_FADEOUT = 12;
	private static final int STATE_STOP = 13;
	
	private static final int MSG_START   = 0;
	private static final int MSG_FADEOUT = 1;
	private static final int MSG_RELEASE = 2;
    private int     state       = STATE_INIT;
    private boolean isNeedStart = false;
    private int     conditionId = 0;    
    
	/**
	 * SoundEffect constructor
	 * @param c context
	 */
    public SoundEffect(Context c) {
        mContext = c;
        try {
        	mRes = mContext.getResources();
        } catch (Exception e) {
            if (LOG_FLAG) Log.i(LOG_TAG, "create resource failed!");
        }
    }
    
    /**
     * set Sound On
     * @param sound true : sound on, false: sound off
     */
	public void setSoundOn(boolean sound) {
		mbSoundOn = sound;
	}
	
	/**
	 * start Media Player
	 * @param id condition id
	 */
	public void startMediaPlayer(int id){
		
		conditionId = id;
        if (LOG_FLAG) Log.i(LOG_TAG, "startMediaPlayer: state = " + state);
		
		if((state == STATE_FADEOUT) || (state == STATE_PLAYING)){
			isNeedStart = true;
            if (LOG_FLAG) Log.i(LOG_TAG, "startMediaPlayer: FADEOUT/RELEASE NEED TO BE DONE FIRST");

		}else{  //state = INIT, STOP
			mHandler.sendEmptyMessage(MSG_START);
		}	
	} 
	
	private void start() {
		int map;
		int rawPath;
        if (LOG_FLAG) Log.i(LOG_TAG, "start: state = " + state);
		
		if (!mbSoundOn){
			state = STATE_STOP;
			return;
		}	

		if ((conditionId < 55) && (0 < conditionId)) {
		    mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

		    if (mAudioManager.isSpeakerphoneOn()){
                if (LOG_FLAG) Log.d(LOG_TAG, "mAudioManager.isSpeakerphoneOn()");
				state = STATE_STOP;
				return;
			}	

			if (mAudioManager.isMusicActive()){
                if (LOG_FLAG) Log.d(LOG_TAG, "mAudioManager.isMusicActive()");
				state = STATE_STOP;
				return;
			}	

			map = SoundEffect.mMapToWeatherSound[conditionId];
			if (map == 9){
                if (LOG_FLAG) Log.d(LOG_TAG, "map == 9");
				state = STATE_STOP;
				return;
			}
			rawPath = pathRaw[map];

			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			createPlayerbyRes(mContext, rawPath);
			mMediaPlayer.setOnCompletionListener(SoundEffect.this);

			disableBeatsEffect(true);

            if (LOG_FLAG) Log.v(LOG_TAG, "mMediaPlayer.start();" + conditionId);
			try{
				mMediaPlayer.start();
				//requestAudio Focus 
	            if(LOG_FLAG) Log.v(LOG_TAG, "requestAudioFocus()");
	            mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			}catch (IllegalStateException e){
                Log.e(LOG_TAG, "can't start media: " + e.toString());
                state = STATE_STOP;
                forceReleaseMediaPlayer();
			}
		} else { 
			state = STATE_STOP;
            if (LOG_FLAG) Log.i(LOG_TAG, "illegal conditionId = "+conditionId+"; state = "+state);
			return;
		}	
	}

    private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (LOG_FLAG) Log.i(LOG_TAG, "focusChange: " + focusChange);
            if(focusChange != AudioManager.AUDIOFOCUS_GAIN || focusChange != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT ){
                if(mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_RELEASE);
                } else {
                    forceReleaseMediaPlayer();
                    checkNeedRestart();
                }
            }
        }
    };
	
/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    private void createPlayerbyRes(Context context, int resid) {
        if (LOG_FLAG) Log.d(LOG_TAG, "create in");
        try {
            if (mRes == null) {
                if (LOG_FLAG)
                    Log.d(LOG_TAG, "resource is null!");
                return;
            }

            AssetFileDescriptor afd = mRes.openRawResourceFd(resid);
            if (afd == null) {
                if (LOG_FLAG)
                    Log.d(LOG_TAG, "descriptor is null!");
                return;
            }

           //     mMediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM); // set the specif type
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            	mMediaPlayer.prepare();
        } catch (IOException ex) {
            if (LOG_FLAG) Log.w(LOG_TAG, "create failed:", ex);
                // fall through
        } catch (IllegalArgumentException ex) {
            if (LOG_FLAG) Log.w(LOG_TAG, "create failed:", ex);
                // fall through
        } catch (SecurityException ex) {
            if (LOG_FLAG) Log.w(LOG_TAG, "create failed:", ex);
                // fall through
        } catch (IllegalStateException ex){
            if (LOG_FLAG) Log.w(LOG_TAG, "IllegalStateException", ex);
     	   		// fall through
        }
        if (LOG_FLAG) Log.d(LOG_TAG, "create out");

    }
	
	private Handler mHandler = new Handler() {
		
		float mCurrentVolume = 1.0f;
		
		public void handleMessage(Message msg){
			switch(msg.what){
				case MSG_START:
                    if (LOG_FLAG) Log.i(LOG_TAG, "case MSG_START: state = "+state);
					state = STATE_PLAYING;
					
					start();
					break;
					
				case MSG_FADEOUT:
                    if (LOG_FLAG) Log.i(LOG_TAG, "case MSG_FADEOUT");                    
					
					if(mMediaPlayer != null){
                                                state = STATE_FADEOUT;			
						if(mCurrentVolume >= 0.0f){
							mCurrentVolume -= 0.1f;
							setVolume(mCurrentVolume);
							mHandler.sendEmptyMessageDelayed(MSG_FADEOUT, 10);
						} else {
                            if (LOG_FLAG) Log.i(LOG_TAG, "case FADEOUT end");
                            mHandler.removeMessages(MSG_FADEOUT);
							mHandler.sendEmptyMessage(MSG_RELEASE);	
							mCurrentVolume = 1.0f;
						}
					} else {
                        if (LOG_FLAG) Log.i(LOG_TAG, "MSG_FADEOUT - set state = STATE_INIT"); 
                        state = STATE_INIT;
                    }
                    break;
                    
                case MSG_RELEASE:
                    if (LOG_FLAG) Log.i(LOG_TAG, "case MSG_RELEASE: state = " + state);
                    mHandler.removeMessages(MSG_FADEOUT);
                    forceReleaseMediaPlayer();
                    checkNeedRestart();
                    break;
            }
        }
    };
    
    
    private void setVolume(float vol) 
    {
        if (mMediaPlayer != null)
            mMediaPlayer.setVolume(vol, vol);
    }


        private void checkNeedRestart() {
            if(isNeedStart){
            if (LOG_FLAG) Log.i(LOG_TAG, "isNeedStart");
                mHandler.sendEmptyMessage(MSG_START);
                isNeedStart = false;
            }
	}

    /**
     * release MediaPalyer
     */
	public void releaseMediaPalyer() {
        if (LOG_FLAG) Log.v(LOG_TAG, "releaseMediaPalyer(): state = " + state);
		if (mMediaPlayer != null) {
            if (LOG_FLAG) Log.v(LOG_TAG, "releaseMediaPalyer() - MSG_FADEOUT");
			mHandler.sendEmptyMessage(MSG_FADEOUT);
		}
        else {
            if (LOG_FLAG) Log.v(LOG_TAG, "releaseMediaPalyer() - MSG_RELEASE");
            mHandler.sendEmptyMessage(MSG_RELEASE);
        }
    }

/**
 *  Hide Automatically by SDK Team [U12000]
 *  @hide
 */
    public void onCompletion(MediaPlayer mp) {
        if (LOG_FLAG) Log.v(LOG_TAG, "onCompletion: state = " + state);
        state = STATE_INIT;

        if (mMediaPlayer != null) {
            if (LOG_FLAG) Log.v(LOG_TAG, "mMediaPlayer onCompletion");
            mMediaPlayer.release();
            mMediaPlayer = null;
            checkNeedRestart();
        }
        //restore beats effect status
        disableBeatsEffect(false);

        //abandon AudioFocus Listener
        if(mAudioManager != null) {
            if (LOG_FLAG) Log.v(LOG_TAG, "abandon AudioFocus");
            mAudioManager.abandonAudioFocus(mAudioFocusListener);
        }
    }

    private void forceReleaseMediaPlayer() {
        if (LOG_FLAG) Log.i(LOG_TAG, "forceReleaseMediaPlayer()  - state = " + state);
        state = STATE_STOP;

        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
            } catch (Exception e) {
                Log.w(LOG_TAG, "forceReleaseMediaPlayer() - Exception = " + e.getMessage());
            } finally {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        //restore beats effect status
        disableBeatsEffect(false);

        //abandon AudioFocus Listener
        if(mAudioManager != null) {
            if (LOG_FLAG) Log.v(LOG_TAG, "abandon AudioFocus");
            mAudioManager.abandonAudioFocus(mAudioFocusListener);
        }
    }

    /**
     * Control Beats effect
     * @param on true to disable Beats effect , false to restore it.
     */
    private void disableBeatsEffect(boolean on) {
        if (mAudioManager == null) {
            if (mContext != null) {
                //mAudioManager = new AudioManager(mContext);
            }
        }
        if (mAudioManager != null) {
            if (on) {
                if (LOG_FLAG) Log.d(LOG_TAG, "force Sound Effect to None");
//                ((HtcIfAudioManager) mAudioManager).setGlobalEffect(HtcIfAudioManager.GLOBAL_SOUNDEFFECT_BEATS, "HtcSpecificAP");
                //mAudioManager.forceSoundEffect(AudioManager.GLOBAL_SOUNDEFFECT_NONE);
            } else {
                if (LOG_FLAG) Log.d(LOG_TAG, "restore Sound Effect");
//                ((HtcIfAudioManager) mAudioManager).setGlobalEffect(HtcIfAudioManager.GLOBAL_SOUNDEFFECT_NONE, "HtcSpecificAP");
               // mAudioManager.restoreSoundEffect();
            }
        }
    }
}
