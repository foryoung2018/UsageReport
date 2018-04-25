package com.morphoinc.app.hyperlapse;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.StatFs;

import com.htc.lib1.hyperlapse.morpho.util.Encoder_Frame;
import com.htc.lib1.hyperlapse.morpho.util.Encoder;
import com.htc.lib1.hyperlapse.morpho.util.IEncoder;
import com.htc.lib1.hyperlapse.record.HyperLapseRecorder;
import com.htc.lib1.hyperlapse.record.HyperLapseRecorder.OnInfoListener;
import com.htc.lib1.hyperlapse.util.MyLog;
import com.morphoinc.util.render.jni.RendererJni;

/**
 * @hide
 * @author Winston
 *
 */
@SuppressWarnings("deprecation")
public class PreviewAndEncodeManager {
    public static final long SECOND_TO_MICROSECOND = 1000000L;
    public static final long MAX_VIDEO_DURATION = 45 * 60*SECOND_TO_MICROSECOND;
    public static final long MAX_VIDEO_FILE_SIZE = (4L * 1022) * 1024 * 1024;
    public static final double MAX_VIDEO_SIZE_RATIO = 0.95;
    private static final int MAX_VIDEO_SIZE_UPDATE_DURATION = 500;
    private static final long KERNEL_FILE_IO_BUFFER_SIZE = 20L * 1024L * 1024L;
    private static final long MIN_VIDEO_REMAIN_SPACE = 5L * 1024 * 1024;
    private static final boolean ENCODE_IN_FRAME = false;
    public OnPreviewFrameCallback mCallback;
    private ExecutorService mExecutor;
    private Future<?> mFutureResult;
    private Future<?> mFuturePreview;
    private Runnable mRunnablePreview;
    private Runnable mRunnableResult;
    protected byte[] mData;
    private int mWidth;
    private int mHeight;
    private ByteBuffer mFrameBuffer;
    private int mBitRate;
    private int mInterval;
    private int mOrientation;
    private Context mContext;
    private IEncoder mEncoder;
    private File mFile;
    private boolean mWritable = true;
    private boolean mIsRecording;
    private boolean mIsStartRecordingCalled;
    private int mInternalFormat;
    private int mOutVideoFps = 0;
    private int mTotalFrameCount = 0;
    private int mDropRatio = 2; // default drop rate for half-FPS semi-video output.
    private Object mRecordingLock = new Object();

    private long mLimitSize = 0;
    private long mUserRequestSize = 0;
    private long mLimitDuration = 0;
    private long mUserRequestDuration = 0;
    private OnInfoListener mOnInfoListener;
    private Runnable mNotifyStorageFullTask = new Runnable(){
        @Override
        public synchronized void run() {
            if(null == mOnInfoListener || false == mIsRecording){
                return;
            }                         

            mIsRecording = false;
            // Notify app to stop record
            //TODO: only notify once
            mOnInfoListener.onInfo(
                    HyperLapseRecorder.MAX_FILESIZE_REACHED,
                    HyperLapseRecorder.EXTRA_DUMMY);

        }
    };

    private Runnable mNotifyTimeUpTask = new Runnable(){
        @Override
        public synchronized void run() {
            if(null == mOnInfoListener || false == mIsRecording){
                return;
            }                         

            mIsRecording = false;
            // Notify app to stop record
            //No need to notify camera, just control duration limitation in HDK
//            mOnInfoListener.onInfo(
//                    HyperLapseRecorder.MAX_DURATION_REACHED,
//                    HyperLapseRecorder.EXTRA_DUMMY);
            
        }                    
    };

    public static abstract interface OnPreviewFrameCallback {
        public abstract void onPreviewFrame(byte[] paramArrayOfByte,
                Camera paramCamera);
    }

    private ScheduledExecutorService mScheduler = null;
    private ScheduledFuture<?> mMaxSizeUpdateHandler = null;

    public PreviewAndEncodeManager(int width, int height, int outVideoFps, Context context) {
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        this.mOutVideoFps = outVideoFps;
    }

    public void setPreviewSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public void setFrameDropRatio(int ratio){
        mDropRatio = ratio;
    }

    public int getFrameDropRatio(){
        return mDropRatio;
    }

    public void setMaxFileSize(long maxFileSize) {
        mUserRequestSize = maxFileSize;
    }
    
    public void setMaxDuration(long maxDuration){
        mUserRequestDuration = maxDuration;
    }

    public void setOnInfoListener(OnInfoListener listener){
        mOnInfoListener = listener;
    }

    public void setEncoderParams(int bitRate, int interval, int orientation) {
        this.mBitRate = bitRate;
        this.mInterval = interval;
        this.mOrientation = orientation;
    }

    public void setPreviewRunnable(Runnable runnable) {
        this.mRunnablePreview = runnable;
    }

    public void setOnPreviewFrameCallback() {

        this.mCallback = new OnPreviewFrameCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                //Add this for debug
                MyLog.iFunc();
                PreviewAndEncodeManager.this.mData = data;
                try {
                    PreviewAndEncodeManager.this.mFuturePreview.get();
                    PreviewAndEncodeManager.this.mFutureResult.get();

                } catch (NullPointerException localNullPointerException) {
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (PreviewAndEncodeManager.this.mRecordingLock) {
                    if (PreviewAndEncodeManager.this.mIsStartRecordingCalled) {
                        PreviewAndEncodeManager.this.mIsStartRecordingCalled = false;
                        if (PreviewAndEncodeManager.this.mIsRecording) {
                            PreviewAndEncodeManager.this.initializeEncoder();
                        } else {
                            MyLog.d("Stop recorder from onPreviewFrame!");
                            PreviewAndEncodeManager.this.finalizeEncoder();
                        }
                    }                    

                    if(null == mFrameBuffer){
                        return;
                    }

                    PreviewAndEncodeManager.this.mFrameBuffer.put(data);
                    PreviewAndEncodeManager.this.mFrameBuffer.rewind();


                    if (PreviewAndEncodeManager.this.mIsRecording) {
                        PreviewAndEncodeManager.this.mFuturePreview = PreviewAndEncodeManager.this.mExecutor
                                .submit(PreviewAndEncodeManager.this.mRunnablePreview);
                        PreviewAndEncodeManager.this.mFutureResult = PreviewAndEncodeManager.this.mExecutor
                                .submit(PreviewAndEncodeManager.this.mRunnableResult);
                    }
                }
            }
        };
    }

    public void start() throws OutOfMemoryError {
        //Init total frame count
        mTotalFrameCount = 0;
        
        if (this.mRunnablePreview == null) {
            throw new NullPointerException();
        }

        this.mExecutor = Executors.newFixedThreadPool(3);

        this.mFrameBuffer = ByteBuffer.allocateDirect(this.mWidth
                * this.mHeight * 3 / 2);        

        this.mRunnableResult = new Runnable() {
            public void run() {
                PreviewAndEncodeManager.this.drainEncoder();
                
                if (PreviewAndEncodeManager.this.mEncoder.getRecordingVideoSize() > mLimitSize) {
                    MyLog.d("File size max!!");
                    new Thread(mNotifyStorageFullTask).start();         
                } else if(PreviewAndEncodeManager.this.mEncoder.getRecordingDuration() > mLimitDuration){
                    MyLog.d("Duration max!!");
                    new Thread(mNotifyTimeUpTask).start();               
                }
            }

        };
        if (!ENCODE_IN_FRAME){
            this.mInternalFormat = RendererJni.getInternalFormat(ImageFormat.NV21);
        }
    }

    public void stop() {
        try {
            this.mFuturePreview.get();

        } catch (NullPointerException localNullPointerException) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.mFutureResult.get();

        } catch (NullPointerException localNullPointerException1) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.mEncoder != null) {
            MyLog.d("Stop recorder from stop!");
            finalizeEncoder();
            this.mExecutor.shutdown();
            this.mExecutor = null;
        }

        if (this.mScheduler != null) {
            this.mScheduler.shutdown();
            this.mScheduler = null;
        }

        this.mFrameBuffer = null;
    }

    public ByteBuffer getPreviewData() {
        return this.mFrameBuffer;
    }

    public void startRecording(boolean on) {
        synchronized (this.mRecordingLock) {
            if (this.mWritable) {
                this.mIsStartRecordingCalled = true;
                this.mIsRecording = on;
            }
        }
    }
    
    public File getVideoFile() {
        return this.mFile;
    }
    
    public boolean setVideoFile(File file) {
        mFile = file;
        return true;
    }
    
    private long getDefaultMaxFileSize(long curSize){
        long maxSize = 0;
        long freeSize =  getFreeSpace();
        freeSize+=curSize;
        freeSize -= ((long)(freeSize * 0.02) + MIN_VIDEO_REMAIN_SPACE + KERNEL_FILE_IO_BUFFER_SIZE);
        maxSize = (freeSize <= 0) ? 0 : freeSize;
        maxSize = (maxSize > MAX_VIDEO_FILE_SIZE)?MAX_VIDEO_FILE_SIZE:maxSize;
        
        return maxSize;
    }
    
    public long getFreeSpace()
    {
        try
        {
            StatFs stat = new StatFs(mFile.getAbsolutePath());
            return (long)stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        }
        catch(Throwable ex)
        {
            MyLog.e("getFreeSpace() - Error occurs", ex);
            return 0;
        }
    }

    private void initializeEncoder() {
        if (ENCODE_IN_FRAME) {
            this.mEncoder = new Encoder_Frame(this.mFile, this.mWidth,
                    this.mHeight, this.mBitRate << 20, mOutVideoFps,
                    this.mInterval, this.mOrientation);
        } else {
            this.mEncoder = new Encoder(this.mFile, this.mWidth, this.mHeight,
                    this.mBitRate << 20, mOutVideoFps, this.mInterval,
                    this.mOrientation);
        }
        
        //Set duration limit
        if(-1 != mUserRequestDuration){
            mLimitDuration = mUserRequestDuration * SECOND_TO_MICROSECOND > MAX_VIDEO_DURATION?MAX_VIDEO_DURATION:mUserRequestDuration* SECOND_TO_MICROSECOND;
        } else {
            mLimitDuration = MAX_VIDEO_DURATION;
        }

        mEncoder.setVideoDuration(mLimitDuration);
        MyLog.d("Max duration is " + mLimitDuration);

        //Execute once and periodically
        if (mScheduler == null) mScheduler = Executors.newScheduledThreadPool(1);
        if (mMaxSizeUpdateHandler == null) mMaxSizeUpdateHandler = mScheduler.scheduleAtFixedRate(new Runnable(){

            @Override
            public void run() {
                //This should be update each 500ms
                long curSize = PreviewAndEncodeManager.this.mFile.length();//PreviewAndEncodeManager.this.mEncoder
                        //.getRecordingVideoSize();
                if (-1 == mUserRequestSize) {
                    mLimitSize = getDefaultMaxFileSize(curSize);
                } else//User request case
                {
                    boolean changeRequestSize = false;
                    long maxFileSize = getDefaultMaxFileSize(curSize);
                    mLimitSize = (changeRequestSize = (mUserRequestSize < maxFileSize))? mUserRequestSize:maxFileSize;
                    if(!changeRequestSize){
                        MyLog.d("Change user request size to current free space");
                    }
                }
                mLimitSize *= MAX_VIDEO_SIZE_RATIO;
                mEncoder.setMaxVideoSize((mLimitSize));
                
                MyLog.d("Max file size limit is " + mLimitSize );
            }
            
        }, 0, MAX_VIDEO_SIZE_UPDATE_DURATION, TimeUnit.MILLISECONDS);
    }

    private void finalizeEncoder() {
        if(null == mEncoder){
            return;
        }
        if(null != mMaxSizeUpdateHandler){
            mMaxSizeUpdateHandler.cancel(true);
            mMaxSizeUpdateHandler = null;
        }

        this.mEncoder.stop();
        this.mEncoder = null;

	//Comment this because HTCCamera would insert video to mediaprovider
        /*MediaScannerConnection.scanFile(this.mContext,
                new String[] { this.mFile.toString() },
                new String[] { "video/mp4" }, null);*/
    }

    private void drainEncoder() {
        //Neglect encode by drop frame ratio
        // half FPS handle
        mTotalFrameCount++;                 
        if((mTotalFrameCount % mDropRatio) != 1){
            return;
        }       
        
        if (ENCODE_IN_FRAME) {
            PreviewAndEncodeManager.this.mEncoder.process(PreviewAndEncodeManager.this.mData);
        } else {
            RendererJni.renderOnSurface(
                    PreviewAndEncodeManager.this.mEncoder.getSurface(),
                    PreviewAndEncodeManager.this.mFrameBuffer,
                    PreviewAndEncodeManager.this.mWidth,
                    PreviewAndEncodeManager.this.mHeight,
                    PreviewAndEncodeManager.this.mInternalFormat);
            PreviewAndEncodeManager.this.mEncoder.process();
        }
    }
}
