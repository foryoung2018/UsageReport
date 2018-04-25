package com.htc.lib1.hyperlapse.record;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;

import com.htc.lib0.media.zoe.HtcZoeMetadata;
import com.htc.lib1.hyperlapse.util.MyLog;
import com.htc.lib1.media.zoe.HtcSVWriter;
import com.morphoinc.app.hyperlapse.PreviewAndEncodeManager;
import com.morphoinc.app.hyperlapse.engine.Engine2;

/**
 * Hyperlapse recorder use to record semi-video
 * @author Winston
 */
@SuppressWarnings("deprecation")
public class HyperLapseRecorder {
    //Define info code here++
    /**
     * info in "what" represents video duration is maximum
     */
    public static final int MAX_DURATION_REACHED = 800;
    /**
     * info in "what" represents video size is maximum
     */
    public static final int MAX_FILESIZE_REACHED = 801;
    /**
     * Info in "what" represents error case
     */
    public static final int ERROR = 1;
    /**
     * Info in "extra" represents nothing(only send dummy when MAX_DURATION_REACHED or MAX_FILESIZE_REACHED)
     */
    public static final int EXTRA_DUMMY = 0;
    //Define info code here--
    private static final HyperLapseRecorder sInstance = new HyperLapseRecorder();
    private Engine2 mEngine;
    private PreviewAndEncodeManager mPreviewManager;
    private OnInfoListener mInfoListener;
    
    /**
     * HMS should impl. this to receive file size/duration maximum notification. HMS must call HyperlapseRecorder.getInstance().stop() if received size/duration maximum notification
     * @author Winston
     */
    public interface OnInfoListener {
        public abstract void onInfo(int what, int extra);
     }
    
    private HyperLapseRecorder(){}         
    
    /**
     * get instance of semi-video recorder 
     * @return semi-video recorder instance
     */
    public static HyperLapseRecorder getInstance(){
        return sInstance;
    }
    
    //TODO do not receive camera object?
    /**
     * HMS must set this in Camera.PreviewCallback method onPreviewFrame(byte[] data, Camera camera)
     * @param paramArrayOfByte: call back frame byte array
     * @param paramCamera: the Camera service object
     */
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera) {
        if (null != mPreviewManager) {
            mPreviewManager.mCallback.onPreviewFrame(paramArrayOfByte,
                    paramCamera);
        }
    }
     
    //return false if init fail, lock file for start/stop
    /**
     * init/start morpho engine and start encode frame, HMS should start preview callback before start
     * @param context: activity context to init morpho engine
     * @param outFilePath: output video abs file path 
     * @param prev_frame_Width: output frame width, ensure this is the same as preview size
     * @param prev_frame_Height: output frame height, ensure this is the same as preview size
     * @param outVideoFps: output video frame rate
     * @param bitrate: customize bit rate to control video quality
     * @param iInterval: customize MPEG4 I, P frame interval setting provided from SSD. (Value: 1001, 1002, 1003)
     * @param orientation: the rotation angle in degrees relative to the orientation of the camera. Rotation can only be 0, 90, 180 or 270.
     * @param verticalViewAngle: camera verticalViewAngle (morpho engine required)
     * @return Success or fail
     */
    public boolean start(Context context, String outFilePath, int prev_frame_Width, int prev_frame_Height, int outVideoFps, int bitRate, int iInterval, int orientation, double verticalViewAngle){
        return start(context, outFilePath, prev_frame_Width, prev_frame_Height, outVideoFps, bitRate, iInterval, -1, orientation, 0, 100, 41667000, verticalViewAngle, 0, 0, 2);
    }
    
    //File size must < 4GB, Duration must < 45min, Duration unit is second
    /**
     * init/start morpho engine and start encode frame, HMS should start preview callback before start
     * @param context: activity context to init morpho engine
     * @param outFilePath: output video abs file path 
     * @param prev_frame_Width: output frame width, ensure this is the same as preview size
     * @param prev_frame_Height: output frame height, ensure this is the same as preview size
     * @param outVideoFps: output video frame rate
     * @param bitrate: customize bit rate to control video quality
     * @param iInterval: customize MPEG4 I, P frame interval setting provided from SSD. (Value: 1001, 1002, 1003)
     * @param orientation: the rotation angle in degrees relative to the orientation of the camera. Rotation can only be 0, 90, 180 or 270.
     * @param verticalViewAngle: camera verticalViewAngle (morpho engine required)
     * @param maxFileSize: limit max video size(Unit is byte), set -1 as default
     * @return Success or fail
     */
    public boolean start(Context context, String outFilePath,  int prev_frame_Width, int prev_frame_Height, int outVideoFps, int bitRate, int iInterval, int orientation, double verticalViewAngle, long maxFileSize){
        return start(context, outFilePath, prev_frame_Width, prev_frame_Height, outVideoFps, bitRate, iInterval, maxFileSize, orientation, 0, 100, 41667000, verticalViewAngle, 0, 0, 2);
    }
    
    //File size must < 4GB, Duration must < 45min, Duration unit is second
    /**
     * init/start morpho engine and start encode frame, HMS should start preview callback before start
     * @param context: activity context to init morpho engine
     * @param outFilePath: output video abs file path 
     * @param prev_frame_Width: output frame width, ensure this is the same as preview size
     * @param prev_frame_Height: output frame height, ensure this is the same as preview size
     * @param outVideoFps: output video frame rate
     * @param bitrate: customize bit rate to control video quality
     * @param iInterval: customize MPEG4 I, P frame interval setting provided from SSD. (Value: 1001, 1002, 1003)
     * @param orientation: the rotation angle in degrees relative to the orientation of the camera. Rotation can only be 0, 90, 180 or 270.
     * @param verticalViewAngle: camera verticalViewAngle (morpho engine required)
     * @param maxFileSize: limit max video size(Unit is byte), set -1 as default
     * @param frameDropRatio: drop callback frame ratio
     * @return Success or fail
     */
    public boolean start(Context context, String outFilePath,  int prev_frame_Width, int prev_frame_Height, int outVideoFps, int bitRate, int iInterval, int orientation, double verticalViewAngle, long maxFileSize, int frameDropRatio){
        return start(context, outFilePath, prev_frame_Width, prev_frame_Height, outVideoFps, bitRate, iInterval, maxFileSize, orientation, 0, 100, 41667000, verticalViewAngle, 0, 0, frameDropRatio);
    }

    //open this api for app
    /**
     * init/start morpho engine and start encode frame, HMS should start preview callback before start
     * @param context: activity context to init morpho engine
     * @param outFilePath: output video abs file path 
     * @param prev_frame_Width: output frame width, ensure this is the same as preview size
     * @param prev_frame_Height: output frame height, ensure this is the same as preview size
     * @param outVideoFps: output video frame rate
     * @param bitrate: customize bit rate to control video quality
     * @param iInterval: customize MPEG4 I, P frame interval setting provided from SSD. (Value: 1001, 1002, 1003)
     * @param maxFileSize: limit max video size(Unit is byte), set -1 as default
     * @param orientation: the rotation angle in degrees relative to the orientation of the camera. Rotation can only be 0, 90, 180 or 270.
     * @param mode: morpho engine mode(Soft/Hard/Hybrid)
     * @param rollingShutterCoeff: morpho engine param 
     * @param gyroTimeLag: morpho engine param
     * @param verticalViewAngle: camera verticalViewAngle (morpho engine required)
     * @param unReliableLevel: morpho engine param
     * @param noMovementLevel: morpho engine param
     * @param frameDropRatio: drop callback frame ratio
     * @return Success or fail
     */
    public synchronized boolean start(Context context, String outFilePath,  int prev_frame_Width, int prev_frame_Height, int outVideoFps, int bitRate, int iInterval, long maxFileSize, int orientation, int mode, int rollingShutterCoeff, long gyroTimeLag, double verticalViewAngle, int unReliableLevel, int noMovementLevel, int frameDropRatio){
        MyLog.iFunc();
        
        if(!init(context, prev_frame_Width, prev_frame_Height, outVideoFps, bitRate, iInterval, orientation)){
            return false;
        }
        
        if(null == outFilePath){
            MyLog.e("Null outFilePath!");
            return false;
        }        
        
        StringBuilder sb = new StringBuilder(" outFilePath = ").append(outFilePath)
                .append(" maxFileSize = ").append(maxFileSize)
                .append(" orientation = ").append(orientation)
                .append(" mode = ").append(mode)
                .append(" rollingShutterCoeff = ").append(rollingShutterCoeff)
                .append(" gyroTimeLag = ").append(gyroTimeLag)
                .append(" verticalViewAngle = ").append(verticalViewAngle)
                .append(" unReliableLevel = ").append(unReliableLevel)
                .append(" noMovementLevel = ").append(noMovementLevel)
                .append(" frameDropRatio = ").append(frameDropRatio);
        MyLog.d(sb.toString());
        
        if(maxFileSize > PreviewAndEncodeManager.MAX_VIDEO_FILE_SIZE * PreviewAndEncodeManager.MAX_VIDEO_SIZE_RATIO){
            MyLog.d("illegal file size, set default!");
            maxFileSize = -1;            
        }
        
        if(frameDropRatio <= 0){
            throw new IllegalArgumentException("frameDropRatio cannot <= 0!!");
        }
        
        mEngine.setMode(mode);

        mEngine.setRollingShutterCoeff(rollingShutterCoeff, orientation);        
//        mEngine.setGyroTimeLag(gyroTimeLag);
        mEngine.setVerticalViewAngle(verticalViewAngle);
        mEngine.setUnreliableLevel(unReliableLevel);
        mEngine.setNoMovementLevel(noMovementLevel);
        mEngine.startPreprocess();
          //Comment Gyro, since this is not necessary when SW mode
//        mEngine.startGyro();
        mEngine.startAccelerometer();
        
        mPreviewManager.setVideoFile(new File(outFilePath));        
        mPreviewManager.setMaxFileSize(maxFileSize);
        mPreviewManager.setMaxDuration(-1);
        mPreviewManager.setFrameDropRatio(frameDropRatio);
        mPreviewManager.startRecording(true);
        return true;
    }	
        
    private boolean init(Context context, int prev_frame_Width, int prev_frame_Height, int outVideoFps, int bitRate, int iInterval, int orientation){
        MyLog.iFunc();
        if(null == context) {
            MyLog.e("Null context!");
            return false;
        }
        
        if(0  >= prev_frame_Width || 0 >= prev_frame_Height){
            MyLog.e("illegal frame width or height!");
            return false;
        }
        
        MyLog.d("prev_frame_Width is " + prev_frame_Width + " prev_frame_Height is " + prev_frame_Height);        
        
        if(0 >= outVideoFps){
            MyLog.e("illegal frame rate!");
            return false;
        }
        
        MyLog.d("outVideoFps is " + outVideoFps);
        MyLog.d("bitRate is " + bitRate);
        MyLog.d("iInterval is " + iInterval);
        
        mPreviewManager = new PreviewAndEncodeManager( prev_frame_Width, prev_frame_Height, outVideoFps, context);
        mPreviewManager.setPreviewSize( prev_frame_Width, prev_frame_Height );
        mPreviewManager.setOnPreviewFrameCallback();
        
        mPreviewManager.setPreviewRunnable( new Runnable() {
            @Override
            public void run() {
                mEngine.preprocess( mPreviewManager.getPreviewData(), 0, System.currentTimeMillis() );
            }
        } );
        
        mPreviewManager.setEncoderParams( bitRate, iInterval, orientation);
        if(null != mInfoListener){
            mPreviewManager.setOnInfoListener(mInfoListener);
        }
        
        mEngine = new Engine2(context);
        mEngine.initializePreprocess(prev_frame_Width, prev_frame_Height, ImageFormat.NV21, Engine2.ACCURACY_MIDDLE);
        
        mPreviewManager.start();
        
        return true;
    }
    
    //return false if init fail, api contains io, lock file for start/stop
    /**
     * stop morpho engine and stop encode frame to generate semi-video, HMS should stop preview callback before call stop
     * @return Success or fail
     */
    public synchronized boolean stop() {
        MyLog.iFunc();
        
        if( mPreviewManager != null ) {
            mPreviewManager.startRecording(false);    
            mPreviewManager.stop();
            writeMotionData(mPreviewManager.getVideoFile(), mPreviewManager.getFrameDropRatio());
            mPreviewManager = null;
        }
        
        MyLog.d("Stop preview manager finish!");

        if( mEngine != null ) {
//            mEngine.stopGyro();
            mEngine.stopAccelerometer();
	    mEngine.finishPreprocess();
//            mEngine.finish();
            mEngine = null;
        }
        
        MyLog.d("Stop Morpho engine finish!");
                
        return true;
    }
    
    /**
     * HMS should set listener to receive file size/duration maximum notification, this should be called before start
     * @param listener: listener to receive notification
     */
    public void setOnInfoListener(OnInfoListener listener) {
        MyLog.d("setOnInfoListener " + listener);
        mInfoListener = listener;
    }    
    
    //Change to merge motion data in MPEG4, must add bytebuffer pos processing before call merge api
    private void writeMotionData(File file, int frameDropRatio) {
        MyLog.i("writeMotionData " + file.getAbsolutePath());
        ByteBuffer buffer = mEngine.getPreprocessData();

        try {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(HtcZoeMetadata.HTC_DATA_SEMIVIDEO_MD, getByteArray(buffer));
            map.put(HtcZoeMetadata. HTC_METADATA_FRAME_DROP_RATIO, frameDropRatio);
            HtcSVWriter.mergeUserData(file.getAbsolutePath(), map);
        } catch (IOException e) {
            MyLog.e(e.getMessage(), e);
        }
    }
    
    private static byte[] getByteArray(ByteBuffer buffer) {
        final ByteBuffer byteBuffer = buffer;
        if (byteBuffer.hasArray()) {
            final byte[] array = byteBuffer.array();
            final int arrayOffset = byteBuffer.arrayOffset();
            return Arrays.copyOfRange(array, arrayOffset + byteBuffer.position(),
                                      arrayOffset + byteBuffer.limit());
        }
        return null;
    }
}
