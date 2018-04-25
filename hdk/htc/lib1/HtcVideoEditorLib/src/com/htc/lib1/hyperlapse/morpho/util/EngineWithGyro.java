package com.htc.lib1.hyperlapse.morpho.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.WindowManager;
/**
 * @hide
 * @author Morpho
 *
 */
 public class EngineWithGyro
 {
   private Sensor mGyro;
   protected SensorManager mSensorManager;
   private SensorEventListener mGyroListener;
   protected int mEventRate = 1;
   
   protected int mRotation;
   
   protected int mDirectionCoeff;
   private boolean mIsGyroActivated = true;
   
   public EngineWithGyro(Context context) {
     WindowManager wm = (WindowManager)context.getSystemService("window");
     Display display = wm.getDefaultDisplay();
     this.mRotation = display.getRotation();
     this.mDirectionCoeff = 1;
     
     this.mSensorManager = ((SensorManager)context.getSystemService("sensor"));
     this.mGyro = this.mSensorManager.getDefaultSensor(4);
     
     if (this.mGyro != null) {
       this.mGyroListener = new SensorEventListener()
       {
         public void onAccuracyChanged(Sensor mSensor, int accuracy) {}
         
 
         public void onSensorChanged(SensorEvent event)
         {
           synchronized (this) {
             if (EngineWithGyro.this.mIsGyroActivated) {
               EngineWithGyro.this.addGyroData(EngineWithGyro.this.mDirectionCoeff * event.values[0], EngineWithGyro.this.mDirectionCoeff * event.values[1], event.values[2], event.timestamp, EngineWithGyro.this.mRotation);
             }
           }
         }
       };
     }
   }
   
   public void setGyroActivated(boolean on) {
     this.mIsGyroActivated = on;
   }
   
   public boolean isGyroActivated() {
     return this.mIsGyroActivated;
   }
   
   public void setFrontCameraModeActivated(boolean on) {
     if (on) {
       this.mDirectionCoeff = -1;
     }
     else {
       this.mDirectionCoeff = 1;
     }
   }
   
   public void setEventRate(int rate) {
     this.mEventRate = rate;
   }
   
   public synchronized void startGyro() {
     if (this.mGyro != null) {
       this.mSensorManager.registerListener(this.mGyroListener, this.mGyro, this.mEventRate);
     }
   }
   
   public synchronized void stopGyro() {
     if ((this.mGyroListener != null) && (this.mSensorManager != null)) {
       this.mSensorManager.unregisterListener(this.mGyroListener);
     }
   }
   
   public synchronized void addGyroData(float x, float y, float z, long t, int rotation) {}
 }