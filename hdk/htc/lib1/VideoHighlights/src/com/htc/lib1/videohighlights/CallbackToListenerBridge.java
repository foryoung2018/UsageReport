/**
 * 
 */
package com.htc.lib1.videohighlights;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.os.Handler.Callback;
import android.os.Message;

/**
 * @author jiahan
 *
 */
public class CallbackToListenerBridge implements Callback{
	protected final static int ON_ERROR_COMMAND = 0;
	protected final static int ON_LOAD_COMMAND = 1;
	protected final static int ON_START_COMMAND = 2;
	protected final static int ON_STOP_COMMAND = 3;
	protected final static int ON_DURATION_FINISHED_COMMAND = 4;
	protected final static int ON_CALLBACK_COMMAND = 5;
	protected final static int ON_SET_RATIO_COMMAND = 6;
	protected final static int ON_PROGRESS_COMMAND = 7;
	protected final static int ON_MUSIC_TIME_COMMAND = 8;

	protected final static String ERROR_CODE_PARAM = "ERROR_CODE_PARAM";
	protected final static String TYPE_PARAM = "TYPE_PARAM";
	protected final static String BUNDLE_PARAM = "BUNDLE_PARAM";
	protected final static String RATIO_PARAM = "RATIO_PARAM";
	protected final static String PROGRESS_PARAM = "PROGRESS_PARAM";
	protected final static String MUSIC_START_PARAM = "MUSIC_START_PARAM";
	protected final static String MUSIC_END_PARAM = "MUSIC_END_PARAM";
	
	private final VideoHighlightsPlayerListener mListener;
	
	protected final static Logger LOG =Logger.getLogger(CallbackToListenerBridge.class.getName());
	
	public CallbackToListenerBridge(final VideoHighlightsPlayerListener listener){
		mListener = listener;
	}
	
	public VideoHighlightsPlayerListener getListener(){
		return mListener;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		if(msg!=null){
			switch(msg.what){
			case ON_ERROR_COMMAND:
				mListener.onError(msg.getData().getInt(ERROR_CODE_PARAM));
				break;
			case ON_LOAD_COMMAND:
				mListener.onLoad();
				break;	
			case ON_START_COMMAND:
				mListener.onStart();
				break;
			case ON_STOP_COMMAND:
				mListener.onStop();
			case ON_DURATION_FINISHED_COMMAND:
				mListener.onDurationFinished();
				break;	
			case ON_CALLBACK_COMMAND:
				mListener.onCallback(msg.getData().getInt(TYPE_PARAM), msg.getData().getBundle(BUNDLE_PARAM));
				break;
			case ON_PROGRESS_COMMAND:
				mListener.onProgress(msg.getData().getLong(PROGRESS_PARAM));
				break;	
			default:
				LOG.log(Level.FINE, "unknown command: "+msg);
			}
			msg.recycle();
		}else{
			LOG.log(Level.SEVERE, "message was null");
		}
		
		return true;
	}

}
