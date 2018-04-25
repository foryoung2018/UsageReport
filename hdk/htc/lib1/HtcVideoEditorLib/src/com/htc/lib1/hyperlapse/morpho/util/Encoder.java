package com.htc.lib1.hyperlapse.morpho.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.view.Surface;

import com.htc.lib1.hyperlapse.util.MyLog;






/**
 * @hide
 * @author Morpho
 *
 */
@SuppressWarnings("deprecation")
public class Encoder implements IEncoder
{
  private MediaCodec mCodec;
  private MediaFormat mFormat;
  private ByteBuffer[] mOutputBuffers;
  private MediaCodec.BufferInfo mInfo = new MediaCodec.BufferInfo();
  
  private MediaMuxer mMuxer;
  private int mIndexTrack;
  private Surface mSurface;
  private int mSkip;
  private int mInterval;
  private long mTimeStamp;
  
  private long mCurSize = 0;
  private long mDuration = 0;
  private long mMaxSize = 0;
  private long mMaxDuration = 0;
  private long mInitTime = 0;
  private long mFrameIndex = 0L;
  private boolean mIsReachLimit = false;
  
  
//  public Encoder(File file, int width, int height, int bitrate, int fps)
//  {
//    this(file, width, height, bitrate, fps, 10);
//  }
  
public Encoder(File file, int width, int height, int bitrate, int fps, int interval, int orientation) {
    String mime = "video/avc";
    
    this.mFormat = MediaFormat.createVideoFormat(mime, width, height);
    MyLog.d("Bit rate is " + bitrate);
    this.mFormat.setInteger("bitrate", bitrate);
    this.mFormat.setInteger("frame-rate", fps);
    this.mFormat.setInteger("i-frame-interval", interval);
    this.mFormat.setInteger("color-format", 2130708361);
    try
    {
      this.mCodec = MediaCodec.createEncoderByType(mime);
    }
    catch (IOException e1) {
      e1.printStackTrace();
    }
    this.mCodec.configure(this.mFormat, null, null, 1);
    
    this.mSurface = this.mCodec.createInputSurface();
    
    this.mCodec.start();
    this.mOutputBuffers = this.mCodec.getOutputBuffers();
    
    this.mSkip = 1;
    this.mInterval = -1;
    this.mTimeStamp = 0L;
    this.mInitTime = 0L;
    try
    {
      this.mMuxer = new MediaMuxer(file.toString(), 0);
      this.mMuxer.setOrientationHint(orientation);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  


  public Surface getSurface()
  {
    return this.mSurface;
  }
  
  public void setMaxVideoSize(long maxSize){
      mMaxSize = maxSize;
  }
  
  public void setVideoDuration(long maxDuration){
      mMaxDuration = maxDuration;
  }
  
  public long getRecordingVideoSize(){
      return mCurSize;
  }
  
  public long getRecordingDuration(){
      return mDuration;
  }
  
  public void setSkipNum(int skip) {
    this.mSkip = skip;
  }
  
  public void setInterval(int interval) {
    this.mInterval = interval;
  }
  


  private void process()
  {
      if(mIsReachLimit){
          return;
      }
      
    // IPPP solution
    if (0 == (mFrameIndex++) % 4) {
        Bundle b = new Bundle();
        b.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
        mCodec.setParameters(b);
    }

    int indexOutput = this.mCodec.dequeueOutputBuffer(this.mInfo, -1L);
    
    if(0 == mInitTime){
        mInitTime = this.mInfo.presentationTimeUs;
    }        
    mDuration = this.mInfo.presentationTimeUs - mInitTime;    
    mCurSize += this.mInfo.size;
    if(mDuration > mMaxDuration || mCurSize > mMaxSize){
        mIsReachLimit = true;
        this.mCodec.releaseOutputBuffer(indexOutput, false);
		MyLog.d("Duration is " + mDuration + " Size is " + mCurSize);		
        return;
    }
    
    if (indexOutput == -2) {
      this.mIndexTrack = this.mMuxer.addTrack(this.mCodec.getOutputFormat());
      this.mMuxer.start();
      MyLog.d("Start MediaMuxer");
    }
    else if (indexOutput >= 0) {
      if ((this.mInfo.flags & 0x2) != 0) {
        this.mInfo.size = 0;
      }
      
      ByteBuffer buffer = this.mOutputBuffers[indexOutput];
      
      buffer.position(buffer.position() + this.mInfo.offset);
      buffer.limit(buffer.position() + this.mInfo.size);
      if (this.mInterval > 0) {
        this.mInfo.presentationTimeUs = this.mTimeStamp;
        this.mTimeStamp += this.mInterval;
      }
      else {
        this.mInfo.presentationTimeUs /= this.mSkip;
      }  
      
      MyLog.d("MediaMuxer writeSampleData");
      this.mMuxer.writeSampleData(this.mIndexTrack, buffer, this.mInfo);
      
      this.mCodec.releaseOutputBuffer(indexOutput, false);
    }
    else if (indexOutput == -3) {
      this.mOutputBuffers = this.mCodec.getOutputBuffers();
    }
  }
  
  public void flush() {
    this.mCodec.flush();
  }
  


  public void stop()
  {
    this.mCodec.signalEndOfInputStream();
	if (!mIsReachLimit) {
        do
        {
          int indexOutput = this.mCodec.dequeueOutputBuffer(this.mInfo, -1L);
          
          if(0 == mInitTime){
              mInitTime = this.mInfo.presentationTimeUs;
          }        
          mDuration = this.mInfo.presentationTimeUs - mInitTime;
          mCurSize += this.mInfo.size;
          if(mDuration > mMaxDuration || mCurSize > mMaxSize){
              mIsReachLimit = true;
              this.mCodec.releaseOutputBuffer(indexOutput, false);
              MyLog.d("Duration is " + mDuration + " Size is " + mCurSize);      
              break;
          }
          
          if (indexOutput >= 0) {
            try {
              ByteBuffer buffer = this.mOutputBuffers[indexOutput];
              
              buffer.position(buffer.position() + this.mInfo.offset);
              buffer.limit(buffer.position() + this.mInfo.size);
              if (this.mInterval > 0) {
                this.mInfo.presentationTimeUs = this.mTimeStamp;
                this.mTimeStamp += this.mInterval;
              }
              else {
                this.mInfo.presentationTimeUs /= this.mSkip;
              }
              
              MyLog.d("MediaMuxer writeSampleData_stop");
              this.mMuxer.writeSampleData(this.mIndexTrack, buffer, this.mInfo);
            }
            catch (Exception localException) {}
            this.mCodec.releaseOutputBuffer(indexOutput, false);
          }
          
        } while ((this.mInfo.flags & 0x4) != 0 && !mIsReachLimit);
	}
    
    this.mCodec.stop();
    this.mCodec.release();
    this.mCodec = null;
    
    try {
        this.mMuxer.stop();
        this.mMuxer.release();
    } catch (IllegalStateException e) {
        MyLog.e(e.getMessage(), e);
    }
    this.mMuxer = null;
    
    this.mSurface.release();
    this.mSurface = null;
  }

    @Override
    public void process(byte... bt) {
        process();
    }
}
