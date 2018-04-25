package com.htc.lib1.panoviewer;
/**
*
* HTC Corporation Proprietary Rights Acknowledgment
* Copyright (c) 2013 HTC Corporation
* All Rights Reserved.
*
* The information contained in this work is the exclusive property of HTC Corporation
* ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
* right to employ this work within the scope of this statement.  Nevertheless, the
* Authorized User shall not use this work for any purpose other than the purpose
* agreed by HTC.  Any and all addition or modification to this work shall be
* unconditionally granted back to HTC and such addition or modification shall be
* solely owned by HTC.  No right is granted under this statement, including but not
* limited to, distribution, reproduction, and transmission, except as otherwise
* provided in this statement.  Any other usage of this work shall be subject to the
* further written consent of HTC.
*
* @file     PanoSEGLSurfaceView.java
* @desc     Panorama+ Viewing surface for HTC Gallery (main delivery)
* @author   yumei chen @ studio engineering, SE1300
* @history  2013/04/10 created
*           2013/06/24 reviewed by andre
*/
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public class PanoSEGLSurfaceView extends SEGLSurfaceView implements SensorEventListener
{
    static {
        System.loadLibrary("panoglviewer_hdk_v14");
    }

    // constants
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_FAIL = -1;
    private static final String LOG_TAG = "PanoSEGLSurfaceView";
    private static final float MAGNITUDE_CHECK = 0.0019f; //power(2.5f * deg_to_rad, 2);

    public interface ViewerCallbackListener
    {
        public void onCaptureFrameEnd(int id, Bitmap bmp);
        public void onCurrentFrame(float[] position); // not used
        public void onLoadFail(int errorCode);
    }

    // callback to communicate with our client
    private ViewerCallbackListener viewerCallback_ = null;

    private long nativeEngine_ = 0;

    // indicator
    private Context drawableResourceContext_ = null; // in case drawableContext_ != getContext()

	private enum BUTTON_ICON {INDICATOR_BASE, 
							  INDICATOR_DIRECTION,
							  GYRO_SWITCH_ON, 
							  GYRO_SWITCH_OFF,
							  POLAR_SWITCH_ON,
							  POLAR_SWITCH_OFF,
							  ICON_SIZE};
	private class Margin {
		public boolean isSet = false;	// must set ,then be detected touch
		private int Left 	= -1;
		private int Top 	= -1;
		private int Right	= -1;
		private int Bottom	= -1;
		private int Width	= -1;
		private int Height	= -1;
		private int screenW = -1;
		private int screenH = -1;
		public Margin() {
		}
		public void set(int left, int top, int right, int bottom) {
			isSet = true;
			Left = left; Top = top; Right = right; Bottom = bottom;
		}
		public void setWH(int width, int height) {
			Width = width; Height = height;
			if (screenW+screenH > 0) 
				updateValue();
		}
		public void setScreenWH(int width, int height) {
			screenW = width; screenH = height;
			if (Width+Height > 0)
				updateValue();
		}
		private void updateValue() {
			if (Left <= 0 && Right <= 0) {
				Left = 0; Right = screenW - Width;
			} else if (Left <= 0 && Right > 0) {
				Left = screenW - Right - Width;
			} else if (Left > 0 && Right <= 0) {
				Right = screenW - Left - Width;
			} else {} // Left > 0 && Right > 0
			
			if (Top <= 0 && Bottom <= 0) {
				Top = 0; Bottom = screenH - Height;
			} else if (Top <= 0 && Bottom > 0) {
				Top = screenH - Bottom - Height;
			} else if (Top > 0 && Bottom <= 0) {
				Bottom = screenH - Top - Height;
			} else {} // Top > 0 && Bottom > 0
		}
		public boolean isInRange(int x, int y) {
			if (!isSet) return false;
			return (Left<x && x<(screenW - Right) && Top<y && y<(screenH - Bottom));
		}
	}
	private class ViewerButton {
		public int size = 0;
		public int[] drawableId = new int[BUTTON_ICON.ICON_SIZE.ordinal()];
		public Margin[] margin = new Margin[BUTTON_ICON.ICON_SIZE.ordinal()];
		public ViewerButton() {
			for (int i = 0; i < BUTTON_ICON.ICON_SIZE.ordinal(); i++) {
				margin[i] = new Margin();
			}
		}
		public void setScreenWH(int width, int height) {
			for (int i = 0; i < margin.length; i++) {
				margin[i].setScreenWH(width, height);
			}
		}
		public boolean isTouching(int x, int y) {
			for (int i = 0; i < margin.length; i++) {
				if (margin[i].isInRange(x, y))
					return true;
			}
			return false;
		}
	}
	private ViewerButton button_ = new ViewerButton();

    // load panorama
    private ReentrantLock loadImageLock_ = new ReentrantLock();
    private ParcelFileDescriptor fd_ = null;
    private enum LOAD_PANORAMA { INVALID, FROM_FD, LOADED };
    LOAD_PANORAMA loadPanorama_ = LOAD_PANORAMA.INVALID;

    // take picture... say cheeze...
    private int pictureWidth_ = 0;
    private int pictureHeight_ = 0;
    private int pictureTakenId_	  = 0; // always be even number
    private int pictureDevelopId_ = 0;

    // to ensure renderer start working
    private volatile boolean request_1st_render = true;

    // for low-end devices (no gyro mode), a period to be requestRender automatically
    private enum SENSOR_MODE {GYROSCOPE, NONE, UNDEFINED};
    SENSOR_MODE sensorMode_ = SENSOR_MODE.UNDEFINED;
    Timer timer_ = null;
    
    // another thread for sensor
    private HandlerThread sensorThread_ = null;

    public PanoSEGLSurfaceView(Context context)
    {
        super(context);
        super.setEGLContextClientVersion(2);
        super.setEGLConfigChooser(new SimpleEGLConfigChooser(false));
        super.setRenderer(new PanoGLSurfaceRenderer());
        super.setRenderMode(RENDERMODE_WHEN_DIRTY);

        nativeEngine_ = createNativeEngine();
    }
    
    @Override
    public void finalize()
    {
        if (nativeEngine_ != 0) {
            closeNativeEngine(nativeEngine_);
            nativeEngine_ = 0;
        }
    }

    public void setListener(ViewerCallbackListener listener)
    {
        viewerCallback_ = listener;
    }

    public void setAssets(Context context, int[] icons)
    {
    	if (icons.length > BUTTON_ICON.ICON_SIZE.ordinal()) {
    		Log.e(LOG_TAG, "setAssets -- drawable number are larger than expected");
    		return;
    	}
    	
    	button_.size = icons.length;
    	for (int i = 0; i < icons.length; i++) {
    		button_.drawableId[i] = icons[i];
    	}
        drawableResourceContext_ = context;
    }

    public void setAssetsMargin(int index, int left, int top, int right, int bottom)
    {
    	if (index >= button_.size) {
    		Log.e(LOG_TAG, "setAssetsMargin -- button index out of defined.");
    		return;
    	}
    	
    	setButtonMargin(nativeEngine_, index, left, top, right, bottom);
    	button_.margin[index].set(left, top, right, bottom);
    }
    
    public void setAssetsOverlayColor(int red, int green, int blue)
    {
    	setButtonOverlayColor(nativeEngine_, red, green, blue);
    }

    public int loadFromFilePath(String photoPath)
    {
		ParcelFileDescriptor descriptor = null;
		try {
			descriptor = ParcelFileDescriptor.open(new File(photoPath), ParcelFileDescriptor.MODE_READ_ONLY);			
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		}
		return loadFromFileDescriptor( descriptor );
    }

    public int loadFromUri(Context context, Uri uriPath)
    {
    	ParcelFileDescriptor descriptor = null;
    	try {
    		AssetFileDescriptor ad = context.getContentResolver().openAssetFileDescriptor(uriPath, "r");
    		descriptor = ad.getParcelFileDescriptor();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	}
    	return loadFromFileDescriptor(descriptor);
    }

    public int loadFromFileDescriptor(ParcelFileDescriptor fd)
    {
    	if (null != fd) {
	    	loadPanorama_ = LOAD_PANORAMA.FROM_FD;
	    	fd_ = fd;
	    	return CODE_SUCCESS;
    	}
    	else {
    		return CODE_FAIL;
    	}
    }

    ///////////////////////////////////////////
    // ???
    public int release()
    {
        return CODE_SUCCESS;
    }//////////////////////////////////////////

    private synchronized int issuePictureTaking(int action) {
        if (-1==action) {
            // check if a image is already developed
            if ((pictureDevelopId_<pictureTakenId_) && (0!=(1&pictureDevelopId_))) {
                ++pictureDevelopId_;
                return pictureDevelopId_/2;
            }
            return 0;
        }
        else if (0==action) {
            // check if we'd like to take a picture at this moment
            if ((pictureDevelopId_<pictureTakenId_) && (0==(1&pictureDevelopId_))) {
                ++pictureDevelopId_;
                return (pictureDevelopId_+1)/2;
            }
            return 0;
        }
        else if (1==action) {
            // by asyncCaptureFrame()
            pictureTakenId_ += 2;
            requestRender();
            return pictureTakenId_/2;
        }
        //Log.e(LOG_TAG, "issuePictureTaking("+action+")");
        return -1;
    }
    /**
    * Async capture frame.
    * @return ID and this value will pass to onCaptureFrameEnd.
    */
    public int asyncCaptureFrame()
    {
        Log.v(LOG_TAG, "asyncCaptureFrame() is called.");
        return issuePictureTaking(1);
    }
    
    @Override
    public void onResume()
    {
        super.onResume();

        // ensure renderer to work when starting up(since the rendering mode is RENDERMODE_WHEN_DIRTY)
        request_1st_render = true;

        /*
         * by registering accelerometer before gyroscope doesn't mean the view will firstly receive accelerometer data before gyroscope data.
         * so we make the view hold off rendering frame until first accelerometer data comes in.
         */
        sensorMode_ = SENSOR_MODE.NONE;

  		SensorManager sensorMgr_ = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor gyroscope = sensorMgr_.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (null!=gyroscope) {
        	// reset timestamps
        	accTimestamp_  = gyroTimestamp_ = System.nanoTime();

        	// register sensor event
            sensorThread_ = new HandlerThread("SensorThread");
            sensorThread_.start();
            Handler handler = new Handler(sensorThread_.getLooper());
            if (sensorMgr_.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME, handler)) {
            	if (sensorMgr_.registerListener(this,
            								sensorMgr_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            								SensorManager.SENSOR_DELAY_GAME,
            								handler)) {
            		sensorMode_ = SENSOR_MODE.GYROSCOPE;
            	}
            	else {
            		sensorMgr_.unregisterListener(this);
            		sensorThread_.quit();
            		sensorThread_ = null;
            	}
            }
        }

        if (SENSOR_MODE.GYROSCOPE==sensorMode_) {
        	enableGyro(nativeEngine_, true);
        }
        else {
        	enableGyro(nativeEngine_, false);

        	// timer to guard 1st rendering
        	timer_ = new Timer();	
    		timer_.schedule(new TimerTask() {
	        	public void run() {
	        		if (request_1st_render) { // renderer isn't working yet. keep pushing!
	        			//Log.e(LOG_TAG, "timer : request 1st rendering...");
	        			requestRender();
	        		}
	        		else {
	        			cancel();
	        		}
	        	}
	        }, 50, 50);
        }

        // resume, start sensor fusion(all operations must not relate to GL context)
        onViewResume(nativeEngine_);
    }

    @Override
    public void onPause()
    {
    	// pause, stop sensor fusion(all operations must not relate to GL context)
        onViewPause(nativeEngine_);

        // either have timer_(motion sensor off) or sensorThread_(motion sensor on)
    	if (SENSOR_MODE.NONE!=sensorMode_) {
    		((SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
    	}

    	if (null!=sensorThread_) {
    		sensorThread_.quit();
    		sensorThread_ = null;
    	}

    	if (null!=timer_) {
    		timer_.cancel();
    		timer_ = null;
    	}

        super.onPause();
    }

    protected class PanoGLSurfaceRenderer implements SEGLSurfaceView.Renderer
    {
    	private boolean isReady_ = false;
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
        	onGLSurfaceCreated(nativeEngine_);
            
            if (null != drawableResourceContext_) {
            	Resources res = drawableResourceContext_.getResources();
            	// load indicator base(0), indicator direction(1), polar switch on(4), polar switch off(5)
            	int btnSet[] = {0, 1, 4, 5}; 
            	for (int i = 0; i < btnSet.length; i++) {
                	if (0 != button_.drawableId[btnSet[i]]) {
                		Bitmap bitmap = BitmapFactory.decodeResource(res, button_.drawableId[btnSet[i]]);
                		button_.margin[btnSet[i]].setWH(bitmap.getWidth(), bitmap.getHeight());
                		onLoadButtonImage(nativeEngine_, btnSet[i], bitmap);
                	}	
            	}
            	// load gyro switch
                if (sensorMode_ == SENSOR_MODE.GYROSCOPE) {
                	if (0 != button_.drawableId[2]) {
                		Bitmap bitmap = BitmapFactory.decodeResource(res, button_.drawableId[2]);
                		button_.margin[2].setWH(bitmap.getWidth(), bitmap.getHeight());
                		onLoadButtonImage(nativeEngine_, 2, bitmap);
                	}
                	if (0 != button_.drawableId[3]) {
                		Bitmap bitmap = BitmapFactory.decodeResource(res, button_.drawableId[3]);
                		button_.margin[3].setWH(bitmap.getWidth(), bitmap.getHeight());
                		onLoadButtonImage(nativeEngine_, 3, bitmap);
                	}
                }
            }
            isReady_ = true;
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            pictureWidth_ = width;
            pictureHeight_ = height;
            onGLSurfaceChanged(nativeEngine_, pictureWidth_, pictureHeight_);
        	button_.setScreenWH(pictureWidth_, pictureHeight_);
        	onTouchCancelled(nativeEngine_);
        }

        @Override
        public void onDestroySurface()
        {
        	isReady_ = false;
            onGLDestroySurface(nativeEngine_);
        }

        @Override
        public void onSurfaceLost()
        {
        	isReady_ = false;
        }

        @Override
        public void onDrawFrame(GL10 gl)
        {
        	if (isReady_) {
	            loadImageLock_.lock();
	            switch (loadPanorama_)
	            {
	            case FROM_FD:
	                if (onLoadPanoramaFD(nativeEngine_, fd_.getFileDescriptor())) {
	                    loadPanorama_ = LOAD_PANORAMA.LOADED;
	                    fd_ = null;
	                }
	                else {
	                    Log.e(LOG_TAG, "failed to load panorama from FD");
	                    if (null!=viewerCallback_)
	                        viewerCallback_.onLoadFail(CODE_FAIL);
	                }
	                break;
	
	            case LOADED: // fall through
	            case INVALID:
	                break;
	            }
	            loadImageLock_.unlock();
	
	            // capture image if it's developed
	            if (null!=viewerCallback_) {
	                final int dId = issuePictureTaking(-1);
	                if (0!=dId) {
	                    Log.v(LOG_TAG, "developing image:" + dId);
	                    // a picture(last frame) is ready to be pick.
	                    Bitmap bitmap = Bitmap.createBitmap(pictureWidth_, pictureHeight_, Bitmap.Config.ARGB_8888);
	                    boolean succ = getCaptureFrame(nativeEngine_, bitmap);
	                    if (succ) {
	                        viewerCallback_.onCaptureFrameEnd(dId, bitmap);
	                    }
	                    else {
	                        Log.e(LOG_TAG, "bitmap factory fail!");
	                    }
	                }
	            }
	
	            // draw frame
	            final int imageTakeId = issuePictureTaking(0);
	            boolean isAnimate = onGLDrawFrame(nativeEngine_, imageTakeId);
	            if (0!=imageTakeId) {
	                //Log.e(LOG_TAG, "take image:" + imageTakeId);
	                requestRender();
	            }
	            else if (isAnimate) {
	            	requestRender();
	            }
	            
	            // cancel timer
	            if (request_1st_render) {
	            	request_1st_render = false;

	            	//
	            	//if (null!=timer_) {
	            	//    timer_.cancel();
	     		    //    timer_ = null;
	            	//}
	            }
	        }
        	else {
        		Log.w(LOG_TAG, "onDrawFrame()... Not Ready!!!");
        		requestRender();
        	}
        }
    }
        
    public void enableTouch(boolean input) {
    	enableTouch(nativeEngine_, input);
    }

    // request by Gallery 2013/12/...  for M8 virtual button
    private boolean showHideBarSwitch_ = false; //  a switch to seperate gesture in viewer and show/hide bar
    private final int SHOWHIDE_BOUNDARY = 45;
    private int detectClickID_ = 0;		// detect click
    private long detectClickPeriod_ = 0;
    private float detectClickX_ = 0;
    private float detectClickY_ = 0;
    private final int DETECT_TIMEOUT = ViewConfiguration.getTapTimeout();
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (0!=(android.view.InputDevice.SOURCE_CLASS_POINTER&event.getSource())) {
            final int action = event.getAction();
            final int touchCount = event.getPointerCount();
            switch (action & MotionEvent.ACTION_MASK)
            {
            case MotionEvent.ACTION_DOWN: {
                    final int pointerID = event.getPointerId(0);
                    final float x = event.getX();
                    final float y = event.getY();
                    if (0 == detectClickID_) {
                    	detectClickID_ = pointerID;
                    	detectClickX_ = x;
                    	detectClickY_ = y;
                    }
                    if (x<SHOWHIDE_BOUNDARY || x>pictureWidth_-SHOWHIDE_BOUNDARY
                    	|| y<SHOWHIDE_BOUNDARY || y>pictureHeight_-SHOWHIDE_BOUNDARY) {
                    	showHideBarSwitch_ = true;
                    }
                    if (!showHideBarSwitch_) {
	                    queueEvent(new Runnable() {
	                        public void run() {
	                            onTouchBegan(nativeEngine_, pointerID, x, y);
	                        	requestRender();
	                        }
	                    });
                    }
                }
                return true; // directly return here! no sub-classing thing here!

            case MotionEvent.ACTION_UP: {
                    final int pointerID = event.getPointerId(0);
                    final float x = event.getX();
                    final float y = event.getY();
                    // provide click event for OnClickListener
                    boolean touchButton = button_.isTouching((int)x, (int)y);
                    
                    if (pointerID == detectClickID_ && !touchButton) {
                    	detectClickPeriod_ = (event.getEventTime() - event.getDownTime());
                    	detectClickX_ -= x;
                    	detectClickY_ -= y;
                    	if (detectClickPeriod_ < DETECT_TIMEOUT // 180ms
                    	&& (detectClickX_*detectClickX_+detectClickY_*detectClickY_) < 200) {
                    		performClick();
                    	}
                    }
                    detectClickID_ = 0;
                    detectClickPeriod_ = 0;
                    detectClickX_ = 0;
                    detectClickY_ = 0;
                    if (!showHideBarSwitch_) {
	                    queueEvent(new Runnable() {
	                        public void run() {
	                            onTouchEnded(nativeEngine_, pointerID, x, y);
	                        	requestRender();
	                        }
	                    });
                    }
                    showHideBarSwitch_ = false;
                }
                return true; // directly return here! no sub-classing thing here!

            case MotionEvent.ACTION_POINTER_DOWN: {
                    final int pointerID = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final float x = event.getX(pointerID);
                    final float y = event.getY(pointerID);
                    if (!showHideBarSwitch_) {
	                    queueEvent(new Runnable() {
	                        public void run() {
	                            onTouchBegan(nativeEngine_, pointerID, x, y);
	                        	requestRender();
	                        }
	                    });
                    }
                } 
                return true; // directly return here! no sub-classing thing here!

            case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerID = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final float x = event.getX(pointerID);
                    final float y = event.getY(pointerID);
                    if (!showHideBarSwitch_) {
	                    queueEvent(new Runnable() {
	                        public void run() {
	                        	onTouchEnded(nativeEngine_, pointerID, x, y);
	                        	requestRender();
	                        }
	                    });
                    }
                }
                return true; // directly return here! no sub-classing thing here!

            case MotionEvent.ACTION_MOVE:
                for (int i=0; i<touchCount; i++) {
                    final int pointerID = event.getPointerId(i);
                    final float x = event.getX(i);
                    final float y = event.getY(i);
                    if (!showHideBarSwitch_) {
	                    queueEvent(new Runnable() {
	                        public void run() {
	                            onTouchMoved(nativeEngine_, pointerID, x, y);
	                            requestRender(); // will this causes overdraw? actually it just needs to draw once for each action_move
	                        }
	                    });
                    }
                }
                return true; // directly return here! no sub-classing thing here!

            //
            // NOTE : MotionEvent.ACTION_CANCEL can be triggered, if 3rd touch down, currently.
            //
            case MotionEvent.ACTION_CANCEL:
                queueEvent(new Runnable() {
                    public void run() {
                        onTouchCancelled(nativeEngine_);
                    }
                });
                return true; // directly return here! no sub-classing thing here!
                
            default:
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    ////======== implement SensorEventListener ========
    private long gyroTimestamp_ = 0;
    private long accTimestamp_ = 0;
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event)
    {
    	switch (event.sensor.getType())
        {
        case Sensor.TYPE_ACCELEROMETER:
            {
                final float x = event.values[0];
                final float y = event.values[1];
                final float z = event.values[2];
                final float delta_time = (float) ((event.timestamp-accTimestamp_)*1.e-9);
                onSensorChanged(nativeEngine_, Sensor.TYPE_ACCELEROMETER, x, y, z, delta_time);
                accTimestamp_ = event.timestamp;
        	}
            break;

        case Sensor.TYPE_GYROSCOPE:
        	if (!request_1st_render) {
        		final float x = event.values[0];
                final float y = event.values[1];
                final float z = event.values[2];
                final float delta_time = (float) ((event.timestamp-gyroTimestamp_)*1.e-9);
                onSensorChanged(nativeEngine_, Sensor.TYPE_GYROSCOPE, x, y ,z, delta_time);
                if (MAGNITUDE_CHECK<(x*x + y*y + z*z)) { // view angle changed
                    requestRender();
                }
        	}
        	else {
        		// renderer isn't working yet. keep pushing!
            	//Log.e(LOG_TAG, "gyroscope : request 1st rendering...");
            	requestRender();
            }
            gyroTimestamp_ = event.timestamp;
            break;

        default:
            break;
        }
    }

    // engine create/delete
    private static native long createNativeEngine();
    private static native void closeNativeEngine(long nativeEngine);

    // set button margin
    private static native void setButtonMargin(long nativeEngine, int buttonIndex, int left, int top, int right, int bottom);
    private static native void setButtonOverlayColor(long nativeEngine, int red, int green, int blue);
    
    // pause/resume sensor
    private native void onViewResume(long nativeEngine);
    private native void onViewPause(long nativeEngine);

    // sensor event
    private native void enableGyro(long nativeEngine, boolean input);
    private native void onSensorChanged(long nativeEngine, int sensorIndex, float x, float y, float z, float deltaTime);

    // touch event
    private native void enableTouch(long nativeEngine, boolean input);
    private native void onTouchBegan(long nativeEngine, int pointerID, float x, float y);
    private native void onTouchEnded(long nativeEngine, int pointerID, float x, float y);
    private native void onTouchMoved(long nativeEngine, int pointerID, float x, float y);
    private native void onTouchCancelled(long nativeEngine);

    // OpenGL settings
    private native void onGLSurfaceCreated(long nativeEngine);
    private native void onGLSurfaceChanged(long nativeEngine, int width, int height);
    private native void onGLDestroySurface(long nativeEngine);
    private native boolean onGLDrawFrame(long nativeEngine, int imageTakeId);

    // glyph from resource(called after surface created)
    private native boolean onLoadButtonImage(long nativeEngine, int buttonIndex, Bitmap bitmap);
    
    // load panorama
    private native boolean onLoadPanoramaFD(long nativeEngine, FileDescriptor fd);

    // capture image
    private native boolean getCaptureFrame(long nativeEngine, Bitmap result);
}