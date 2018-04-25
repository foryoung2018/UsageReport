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
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.AbstractTrack;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Track;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.TrackMetaData;
import com.htc.lib1.htcmp4parser.utils.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Generates a Track that starts at fromSample and ends at toSample (exclusive). The user of this class
 * has to make sure that the fromSample is a random access sample.
 * <ul>
 * <li>In AAC this is every single sample</li>
 * <li>In H264 this is every sample that is marked in the SyncSampleBox</li>
 * </ul>
 * @hide
 * {@exthide}
 */
public class CroppedTrack extends AbstractTrack {
    /**
     * @hide
     */
    Track origTrack;
    private int fromSample;
    private int toSample;
    private long[] syncSampleArray;
    
    private final String TAG = this.getClass().getSimpleName();

    /**
     * @hide
     * @param origTrack
     * @param fromSample
     * @param toSample
     */
    public CroppedTrack(Track origTrack, long fromSample, long toSample) {
        this.origTrack = origTrack;
        assert fromSample <= Integer.MAX_VALUE;
        assert toSample <= Integer.MAX_VALUE;
        this.fromSample = (int) fromSample;
        
        if (origTrack.getSamples().size() >= toSample + 1) {
        	Log.d("toSample :" + (toSample + 1) + ", " + origTrack.getSamples().size());
        	this.toSample = (int) toSample + 1;// since we need to keep last frame that showen in preview, cut EndSample + 1 to reserve sync sample
        } else 
        	this.toSample = (int) toSample;
    }
    
    /**
     * @hide
     * @return
     */
    public List<SampleOffset> getSampleOffsets() {
    	
    	List<SampleOffset> sof = origTrack.getSampleOffsets();
    	if (sof != null) {
    		Log.d(TAG ,"CroppedTrack getSampleOffsets sucess.");    		
    	} else {
    		final List<ByteBuffer> samples = origTrack.getSamples();
	    	if (samples instanceof SampleList) {	    		
	    		sof =  ((SampleList)samples).getSampleOffsets();
	    		Log.d(TAG ,"CroppedTrack getSampleOffsets sucess");
	    	}
    	}
    	
    	if (sof != null) {
    		return sof.subList(fromSample, toSample);
    	}
    	
    	return null;
    }
    
    /**
     * @hide
     * @return
     */
    public List<ByteBuffer> getSamples() {
        return origTrack.getSamples().subList(fromSample, toSample);
    }
    
    /**
     * @hide
     * @return
     */
    public SampleDescriptionBox getSampleDescriptionBox() {
        return origTrack.getSampleDescriptionBox();
    }
    
    /**
     * @hide
     * @return
     */
    public List<TimeToSampleBox.Entry> getDecodingTimeEntries() {
        if (origTrack.getDecodingTimeEntries() != null && !origTrack.getDecodingTimeEntries().isEmpty()) {
            // todo optimize! too much long is allocated but then not used
            long[] decodingTimes = TimeToSampleBox.blowupTimeToSamples(origTrack.getDecodingTimeEntries());
            long[] nuDecodingTimes = new long[toSample - fromSample];
            System.arraycopy(decodingTimes, fromSample, nuDecodingTimes, 0, toSample - fromSample);

            LinkedList<TimeToSampleBox.Entry> returnDecodingEntries = new LinkedList<TimeToSampleBox.Entry>();

            for (long nuDecodingTime : nuDecodingTimes) {
                if (returnDecodingEntries.isEmpty() || returnDecodingEntries.getLast().getDelta() != nuDecodingTime) {
                    TimeToSampleBox.Entry e = new TimeToSampleBox.Entry(1, nuDecodingTime);
                    returnDecodingEntries.add(e);
                } else {
                    TimeToSampleBox.Entry e = returnDecodingEntries.getLast();
                    e.setCount(e.getCount() + 1);
                }
            }
            return returnDecodingEntries;
        } else {
            return null;
        }
    }
    
    /**
     * @hide
     * @return
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        if (origTrack.getCompositionTimeEntries() != null && !origTrack.getCompositionTimeEntries().isEmpty()) {
            int[] compositionTime = CompositionTimeToSample.blowupCompositionTimes(origTrack.getCompositionTimeEntries());
            int[] nuCompositionTimes = new int[toSample - fromSample];
            System.arraycopy(compositionTime, fromSample, nuCompositionTimes, 0, toSample - fromSample);

            LinkedList<CompositionTimeToSample.Entry> returnDecodingEntries = new LinkedList<CompositionTimeToSample.Entry>();

            for (int nuDecodingTime : nuCompositionTimes) {
                if (returnDecodingEntries.isEmpty() || returnDecodingEntries.getLast().getOffset() != nuDecodingTime) {
                    CompositionTimeToSample.Entry e = new CompositionTimeToSample.Entry(1, nuDecodingTime);
                    returnDecodingEntries.add(e);
                } else {
                    CompositionTimeToSample.Entry e = returnDecodingEntries.getLast();
                    e.setCount(e.getCount() + 1);
                }
            }
            return returnDecodingEntries;
        } else {
            return null;
        }
    }
    
    /**
     * @hide
     * @return
     */
    synchronized public long[] getSyncSamples() {
        if (this.syncSampleArray == null) {
            if (origTrack.getSyncSamples() != null && origTrack.getSyncSamples().length > 0) {
                List<Long> syncSamples = new LinkedList<Long>();
                for (long l : origTrack.getSyncSamples()) {
                    if (l >= fromSample && l < toSample) {
                        syncSamples.add(l - fromSample);
                    }
                }
                syncSampleArray = new long[syncSamples.size()];
                for (int i = 0; i < syncSampleArray.length; i++) {
                    syncSampleArray[i] = syncSamples.get(i);

                }
                return syncSampleArray;
            } else {
                return null;
            }
        } else {
            return this.syncSampleArray;
        }
    }
    
    /**
     * @hide
     * @return
     */
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        if (origTrack.getSampleDependencies() != null && !origTrack.getSampleDependencies().isEmpty()) {
            return origTrack.getSampleDependencies().subList(fromSample, toSample);
        } else {
            return null;
        }
    }
    
    /**
     * @hide
     * @return
     */
    public TrackMetaData getTrackMetaData() {
        return origTrack.getTrackMetaData();
    }
    
    /**
     * @hide
     * @return
     */
    public String getHandler() {
        return origTrack.getHandler();
    }
    
    /**
     * @hide
     * @return
     */
    public Box getMediaHeaderBox() {
        return origTrack.getMediaHeaderBox();
    }
    
    /**
     * @hide
     * @return
     */
    public SubSampleInformationBox getSubsampleInformationBox() {
        return origTrack.getSubsampleInformationBox();
    }

}