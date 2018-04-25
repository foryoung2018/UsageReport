package com.htc.lib1.upm;

import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

public class HandlerThreadUtils {

    private static HandlerThreadUtils mInstance;
    public static HandlerThreadUtils getInstance() {
        if (mInstance == null)
            mInstance = new HandlerThreadUtils();
        return mInstance;
    }
    private HandlerThreadUtils() {
        mHandlerThread = new HandlerThread("HtcAppUPManager", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
    }
    
    public Looper getLooper() {
        return mHandlerThread.getLooper();
    }
    
    private HandlerThread mHandlerThread;
}
