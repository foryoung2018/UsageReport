package com.htc.lib1.exo.player;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.MediaClock;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.chunk.ChunkSampleSource;

import com.htc.lib1.exo.utilities.LOG;
/**
 * A {@link TrackRenderer} that periodically updates debugging information displayed by a
 * {@link TextView}.
 */
/* package */ class TimerAdjustRenderer extends TrackRenderer implements Runnable {
    private String TAG = "TimerAdjustRenderer";
    private final MediaCodecTrackRenderer renderer;
    private final ChunkSampleSource videoSampleSource;

    private volatile boolean pendingFailure;
    private volatile long currentPositionUs;
    private final MediaClock mediaClock;

    private long durationUs = -1;
    private boolean durationUsSet = false;

    public TimerAdjustRenderer(MediaCodecTrackRenderer renderer) {
        this(renderer, null);
    }

    public TimerAdjustRenderer(MediaCodecTrackRenderer renderer,
                              ChunkSampleSource videoSampleSource) {
        this.renderer = renderer;
        this.videoSampleSource = videoSampleSource;
        this.mediaClock = new MediaClock();
    }

    public void injectFailure() {
        LOG.I(TAG,"injectFailure");
        pendingFailure = true;
    }

    @Override
    protected boolean isEnded() {
        //LOG.I(TAG,"isEnded");
        
        if (durationUs > 0 && getCurrentPositionUs() < durationUs)
        {
            return false;
        }
        return true;
    }

    @Override
    protected boolean isReady() {
        //LOG.I(TAG,"isReady");
        return true;
    }

    @Override
    protected int doPrepare(long positionUs) throws ExoPlaybackException {
        LOG.I(TAG,"doPrepare");
        maybeFail();
        return STATE_PREPARED;
    }

    @Override
    protected void doSomeWork(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        //LOG.I(TAG,"doSomeWork(" + positionUs + "," + elapsedRealtimeUs + ")");
        maybeFail();
        if (positionUs < currentPositionUs || positionUs > currentPositionUs + 1000000) {
            currentPositionUs = positionUs;
        }
    }

    @Override
    public void run() {
        LOG.I(TAG,"run");
    }

    @Override
    protected long getCurrentPositionUs() {
        long rtn = 0;

        //rtn = currentPositionUs;
        rtn = mlMediaClockBaseUs + (long)((mediaClock.getPositionUs() - mlMediaClockBaseUs )* mfPlayRate);

        //LOG.I(TAG,"getCurrentPositionUs(MS) = " + (rtn / 1000) + "/" + (currentPositionUs / 1000) + "/" + ((rtn - currentPositionUs) / 1000) + "/" + mfPlayRate);
        return rtn;
    }

    @Override
    protected long getDurationUs() {
        //LOG.I(TAG,"getDurationUs");
        return TrackRenderer.MATCH_LONGEST_US;
    }

    @Override
    protected long getBufferedPositionUs() {
        return TrackRenderer.END_OF_TRACK_US;
    }

    @Override
    protected void seekTo(long timeUs) {
        if (currentPositionUs != timeUs)
        {
            LOG.I(TAG,"seekTo(" + currentPositionUs + "->" + timeUs + ")");
            currentPositionUs = timeUs;

            updateClock(timeUs, mfPlayRate);
        }
    }

    @Override
    protected void onStarted() {
        LOG.I(TAG,"onStarted");
        mediaClock.start();

        if (durationUsSet == false){
            durationUsSet = true;
            durationUs = renderer.getDurationUs();
        }
    }

    @Override
    protected void onStopped() {
        LOG.I(TAG,"onStopped");
        mediaClock.stop();
    }

    private void maybeFail() throws ExoPlaybackException {
        if (pendingFailure) {
            pendingFailure = false;
            throw new ExoPlaybackException("fail() was called on DebugTrackRenderer");
        }
    }

    private float mfPlayRate = 1.0f;
    private long mlMediaClockBaseUs = 0;
    public void setPlayRate (float rate)
    {
        if (rate == 0) return;
        LOG.I(TAG,"setPlayRate " + rate);
        //update parameter
        {

            long tmpCur =  getCurrentPositionUs();
            updateClock(tmpCur, rate);
        }
    }

    private void updateClock(long positionUS, float rate)
    {
        mlMediaClockBaseUs = positionUS;
        mediaClock.setPositionUs(positionUS);
        mfPlayRate = rate;
    }

    private boolean mbTimeSourc = false;

    public void enableTimeSource(boolean flag) {
        LOG.I(TAG,"enableTimeSource(" + flag + ")");
        mbTimeSourc =  flag;
    }

    @Override
    protected boolean isTimeSource() {
        return mbTimeSourc;
    }

}
