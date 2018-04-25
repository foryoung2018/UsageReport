/*
 * Copyright 2012 Sebastian Annies, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.tracks;

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.*;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList.SampleOffset;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Track;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.TrackMetaData;
import com.htc.lib1.htcmp4parser.utils.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * Changes the timescale of a track by wrapping the track.
 * @hide
 * {@exthide}
 */
public class ChangeTimeScaleTrack implements Track {
    private static final Logger LOG = Logger.getLogger(ChangeTimeScaleTrack.class.getName());
    
    /**
     * @hide
     */
    Track source;
    /**
     * @hide
     */
    List<CompositionTimeToSample.Entry> ctts;
    /**
     * @hide
     */
    List<TimeToSampleBox.Entry> tts;
    /**
     * @hide
     */
    long timeScale;

    /**
     * Changes the time scale of the source track to the target time scale and makes sure
     * that any rounding errors that may have summed are corrected exactly before the syncSamples.
     *
     * @param source          the source track
     * @param targetTimeScale the resulting time scale of this track.
     * @param syncSamples     at these sync points where rounding error are corrected.
     * @hide
     */
    public ChangeTimeScaleTrack(Track source, long targetTimeScale, long[] syncSamples) {
        this.source = source;
        this.timeScale = targetTimeScale;
        final TrackMetaData trackMetaData= source.getTrackMetaData();
        double timeScaleFactor = 0;
        if (trackMetaData != null) {
        	timeScaleFactor = (double) targetTimeScale / source.getTrackMetaData().getTimescale();
        } else {
        	Log.e(ChangeTimeScaleTrack.class.getName(), "Get trackMetaData is null");
        }        
        ctts = adjustCtts(source.getCompositionTimeEntries(), timeScaleFactor);
        tts = adjustTts(source.getDecodingTimeEntries(), timeScaleFactor, syncSamples, getTimes(source, syncSamples, targetTimeScale));
    }

    private static long[] getTimes(Track track, long[] syncSamples, long targetTimeScale) {
        long[] syncSampleTimes = new long[syncSamples.length];
        Queue<TimeToSampleBox.Entry> timeQueue = new LinkedList<TimeToSampleBox.Entry>(track.getDecodingTimeEntries());

        int currentSample = 1;  // first syncsample is 1
        long currentDuration = 0;
        long currentDelta = 0;
        int currentSyncSampleIndex = 0;
        long left = 0;


        while (currentSample <= syncSamples[syncSamples.length - 1]) {
            if (currentSample++ == syncSamples[currentSyncSampleIndex]) {
                syncSampleTimes[currentSyncSampleIndex++] = (currentDuration * targetTimeScale) / track.getTrackMetaData().getTimescale();
            }
            if (left-- == 0) {
                TimeToSampleBox.Entry entry = timeQueue.poll();
                left = entry.getCount() - 1;
                currentDelta = entry.getDelta();
            }
            currentDuration += currentDelta;
        }
        return syncSampleTimes;

    }
    
    /**
     * @hide
     * @return
     */
    public SampleDescriptionBox getSampleDescriptionBox() {
        return source.getSampleDescriptionBox();
    }
    
    /**
     * @hide
     * @return
     */
    public List<TimeToSampleBox.Entry> getDecodingTimeEntries() {
        return tts;
    }

    /**
     * @hide
     * @return
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return ctts;
    }

    /**
     * @hide
     * @return
     */
    public long[] getSyncSamples() {
        return source.getSyncSamples();
    }

    /**
     * @hide
     * @return
     */
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return source.getSampleDependencies();
    }

    /**
     * @hide
     * @return
     */
    public TrackMetaData getTrackMetaData() {
        TrackMetaData trackMetaData = (TrackMetaData) source.getTrackMetaData().clone();
        trackMetaData.setTimescale(timeScale);
        return trackMetaData;
    }

    /**
     * @hide
     * @return
     */
    public String getHandler() {
        return source.getHandler();
    }
    
    /**
     * @hide
     * @return
     */
    public boolean isEnabled() {
        return source.isEnabled();
    }
    
    /**
     * @hide
     * @return
     */
    public boolean isInMovie() {
        return source.isInMovie();
    }
    
    /**
     * 
     * @return
     */
    public boolean isInPreview() {
        return source.isInPreview();
    }
    
    /**
     * 
     * @return
     */
    public boolean isInPoster() {
        return source.isInPoster();
    }
    
    /**
     * @hide
     * @return
     */
    public List<ByteBuffer> getSamples() {
        return source.getSamples();
    }


    /**
     * Adjusting the composition times is easy. Just scale it by the factor - that's it. There is no rounding
     * error summing up.
     *
     * @param source
     * @param timeScaleFactor
     * @return
     * @hide
     */
    static List<CompositionTimeToSample.Entry> adjustCtts(List<CompositionTimeToSample.Entry> source, double timeScaleFactor) {
        if (source != null) {
            List<CompositionTimeToSample.Entry> entries2 = new ArrayList<CompositionTimeToSample.Entry>(source.size());
            for (CompositionTimeToSample.Entry entry : source) {
                entries2.add(new CompositionTimeToSample.Entry(entry.getCount(), (int) Math.round(timeScaleFactor * entry.getOffset())));
            }
            return entries2;
        } else {
            return null;
        }
    }

    /**
     * @hide
     * @param source
     * @param timeScaleFactor
     * @param syncSample
     * @param syncSampleTimes
     * @return
     */
    static List<TimeToSampleBox.Entry> adjustTts(List<TimeToSampleBox.Entry> source, double timeScaleFactor, long[] syncSample, long[] syncSampleTimes) {

        long[] sourceArray = TimeToSampleBox.blowupTimeToSamples(source);
        long summedDurations = 0;

        LinkedList<TimeToSampleBox.Entry> entries2 = new LinkedList<TimeToSampleBox.Entry>();
        for (int i = 1; i <= sourceArray.length; i++) {
            long duration = sourceArray[i - 1];

            long x = Math.round(timeScaleFactor * duration);


            TimeToSampleBox.Entry last = entries2.peekLast();
            int ssIndex;
            if ((ssIndex = Arrays.binarySearch(syncSample, i + 1)) >= 0) {
                // we are at the sample before sync point
                if (syncSampleTimes[ssIndex] != summedDurations) {
                    long correction = syncSampleTimes[ssIndex] - (summedDurations + x);
                    LOG.finest(String.format("Sample %d %d / %d - correct by %d", i, summedDurations, syncSampleTimes[ssIndex], correction));
                    x += correction;
                }
            }
            summedDurations += x;
            if (last == null) {
                entries2.add(new TimeToSampleBox.Entry(1, x));
            } else if (last.getDelta() != x) {
                entries2.add(new TimeToSampleBox.Entry(1, x));
            } else {
                last.setCount(last.getCount() + 1);
            }

        }
        return entries2;
    }

    /**
     * @hide
     * @return
     */
    public Box getMediaHeaderBox() {
        return source.getMediaHeaderBox();
    }
    
    /**
     * @hide
     * @return
     */
    public SubSampleInformationBox getSubsampleInformationBox() {
        return source.getSubsampleInformationBox();
    }
    
    /**
     * @hide
     */
    @Override
    public String toString() {
        return "ChangeTimeScaleTrack{" +
                "source=" + source +
                '}';
    }
    
    /**
     * @hide
     * @return
     */
	@Override
	public List<SampleOffset> getSampleOffsets() {
		final List<ByteBuffer> samples = source.getSamples();
    	if (samples instanceof SampleList)        	
    		return ((SampleList)samples).getSampleOffsets();
    	else
    		return null;
	}
}
