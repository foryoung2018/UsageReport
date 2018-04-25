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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Changes the timescale of a track by wrapping the track.
 * @hide
 * {@exthide}
 */
public class DivideTimeScaleTrack implements Track {
    /**
     * @hide
     */
    Track source;
    private int timeScaleDivisor;

    /**
     * @hide
     * @param source
     * @param timeScaleDivisor
     */
    public DivideTimeScaleTrack(Track source, int timeScaleDivisor) {
        this.source = source;
        this.timeScaleDivisor = timeScaleDivisor;
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
        return adjustTts();
    }
    
    /**
     * @hide
     * @return
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return adjustCtts();
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
        if (trackMetaData != null) {
        	trackMetaData.setTimescale(source.getTrackMetaData().getTimescale() / this.timeScaleDivisor);
        }
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
     * @hide
     * @return
     */
    public boolean isInPreview() {
        return source.isInPreview();
    }
    
    /**
     * @hide
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
     * @hide
     * @return
     */
    List<CompositionTimeToSample.Entry> adjustCtts() {
        List<CompositionTimeToSample.Entry> origCtts = this.source.getCompositionTimeEntries();
        if (origCtts != null) {
            List<CompositionTimeToSample.Entry> entries2 = new ArrayList<CompositionTimeToSample.Entry>(origCtts.size());
            for (CompositionTimeToSample.Entry entry : origCtts) {
                entries2.add(new CompositionTimeToSample.Entry(entry.getCount(), entry.getOffset() / timeScaleDivisor));
            }
            return entries2;
        } else {
            return null;
        }
    }
    
    /**
     * @hide
     * @return
     */
    List<TimeToSampleBox.Entry> adjustTts() {
        List<TimeToSampleBox.Entry> origTts = source.getDecodingTimeEntries();
        LinkedList<TimeToSampleBox.Entry> entries2 = new LinkedList<TimeToSampleBox.Entry>();
        for (TimeToSampleBox.Entry e : origTts) {
            entries2.add(new TimeToSampleBox.Entry(e.getCount(), e.getDelta() / timeScaleDivisor));
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
        return "MultiplyTimeScaleTrack{" +
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
