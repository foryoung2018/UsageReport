 package com.htc.lib1.hyperlapse.morpho.util;
 
 import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
 /**
 * @hide
 * @author Morpho
 *
 */
 public class EngineWithGyroAndAccelerometer extends EngineWithGyro
 {
   private Sensor mAccel;
   private SensorEventListener mAccelListener;
   private boolean mIsAccelerometerActivated = true;
   
   public EngineWithGyroAndAccelerometer(Context context) {
     super(context);
     
     this.mAccel = this.mSensorManager.getDefaultSensor(1);
     
     if (this.mAccel != null) {
       this.mAccelListener = new SensorEventListener()
       {
         public void onAccuracyChanged(Sensor mSensor, int accuracy) {}
         
 
         public void onSensorChanged(SensorEvent event)
         {
           synchronized (this) {
             if (EngineWithGyroAndAccelerometer.this.mIsAccelerometerActivated) {
               EngineWithGyroAndAccelerometer.this.addAccelerometerData(EngineWithGyroAndAccelerometer.this.mDirectionCoeff * event.values[0], EngineWithGyroAndAccelerometer.this.mDirectionCoeff * event.values[1], event.values[2], event.timestamp, EngineWithGyroAndAccelerometer.this.mRotation);
             }
           }
         }
       };
     }
   }
   
   public void setAccelerometerActivated(boolean on) {
     this.mIsAccelerometerActivated = on;
   }
   
   public boolean isAccelerometerActivated() {
     return this.mIsAccelerometerActivated;
   }
   
   public synchronized void startAccelerometer() {
     if (this.mAccel != null) {
       this.mSensorManager.registerListener(this.mAccelListener, this.mAccel, this.mEventRate);
     }
   }
   
   public synchronized void stopAccelerometer() {
     if ((this.mAccelListener != null) && (this.mSensorManager != null)) {
       this.mSensorManager.unregisterListener(this.mAccelListener);
     }
   }
   
   public synchronized void addAccelerometerData(float x, float y, float z, long t, int rotation) {}
 }