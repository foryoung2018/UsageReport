package com.htc.lib1.hyperlapse.morpho.util;

import android.view.Surface;

public interface IEncoder {
    public void process(byte... bt);
    public void stop();
    public Surface getSurface();
    public long getRecordingVideoSize();
    public long getRecordingDuration();
    public void setMaxVideoSize(long maxSize);    
    public void setVideoDuration(long maxDuration);
}
