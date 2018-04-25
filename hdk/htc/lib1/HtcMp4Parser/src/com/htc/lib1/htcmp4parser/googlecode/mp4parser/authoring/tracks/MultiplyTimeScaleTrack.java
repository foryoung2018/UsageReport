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
import java.util.LinkedList;
import java.util.List;

/**
 * Changes the timescale of a track by wrapping the track.
 * @hide
 * {@exthide}
 */
public class MultiplyTimeScaleTrack implements Track {
    /**
     * @hide
     */
    Track source;
    private int timeScaleFactor;

    /**
     * @hide
     * @param source
     * @param timeScaleFactor
     */
    public MultiplyTimeScaleTrack(Track source, int timeScaleFactor) {
        this.source = source;
        this.timeScaleFactor = timeScaleFactor;
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
        return adjustTts(source.getDecodingTimeEntries(), timeScaleFactor);
    }
    
    /**
     * @hide
     * @return
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return adjustCtts(source.getCompositionTimeEntries(), timeScaleFactor);
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
        if (trackMetaData!=null) {
        	trackMetaData.setTimescale(source.getTrackMetaData().getTimescale() * this.timeScaleFactor);
        } else {
        	Log.e(MultiplyTimeScaleTrack.class.getName(), "Get trackMetaData is null");
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
     * @param source
     * @param timeScaleFactor
     * @return
     */
    static List<CompositionTimeToSample.Entry> adjustCtts(List<CompositionTimeToSample.Entry> source, int timeScaleFactor) {
        if (source != null) {
            List<CompositionTimeToSample.Entry> entries2 = new ArrayList<CompositionTimeToSample.Entry>(source.size());
            for (CompositionTimeToSample.Entry entry : source) {
                entries2.add(new CompositionTimeToSample.Entry(entry.getCount(), timeScaleFactor * entry.getOffset()));
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
     * @return
     */
    static List<TimeToSampleBox.Entry> adjustTts(List<TimeToSampleBox.Entry> source, int timeScaleFactor) {
        LinkedList<TimeToSampleBox.Entry> entries2 = new LinkedList<TimeToSampleBox.Entry>();
        for (TimeToSampleBox.Entry e : source) {
            entries2.add(new TimeToSampleBox.Entry(e.getCount(), timeScaleFactor * e.getDelta()));
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
