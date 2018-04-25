package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.tracks;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.CompositionTimeToSample;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleDescriptionBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SubSampleInformationBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TimeToSampleBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList.SampleOffset;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Track;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.TrackMetaData;
import com.htc.lib1.htcmp4parser.utils.Log;

/**
 * Output a slowMotion mode track by SlowMotionSectors info.
 * @hide
 * {@exthide}
 */
public class MotionChangeTrack implements Track {
    private Track source;
    private ArrayList<VideoSector> sectors;
    
    /**
     * @hide
     * @param source
     * @param sectors
     */
    public MotionChangeTrack(Track source, ArrayList<VideoSector> sectors) {
        this.source = source;
        this.sectors = sectors;
    }
    
    /**
     * @hide
     * @return
     */
    public SampleDescriptionBox getSampleDescriptionBox() {
        return source.getSampleDescriptionBox();
    }
    
    private List<TimeToSampleBox.Entry> mDecodingTimeEntries = null;
    private Object mDecodingTimeEntriesLock = new Object();
    /**
     * @hide
     * @return
     */
    public List<TimeToSampleBox.Entry> getDecodingTimeEntries() {
        synchronized (mDecodingTimeEntriesLock) {
        	if (mDecodingTimeEntries == null) {
        		mDecodingTimeEntries = adjustTts();
        	}
        	return mDecodingTimeEntries;
        }
    }
    
    private List<CompositionTimeToSample.Entry> mCompositionTimeEntries = null;
    private Object mCompositionTimeEntriesLock = new Object();
    /**
     * @hide
     * @return
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        synchronized(mCompositionTimeEntriesLock) {
        	if (mCompositionTimeEntries == null) {
        		mCompositionTimeEntries = adjustCtts();
        	}
        	
        	return mCompositionTimeEntries;
        }    		
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
        return source.getTrackMetaData();
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
    public List<SampleOffset> getSampleOffsets() {
    	final List<SampleOffset> sampleOffsets = source.getSampleOffsets();
    	
    	if (sampleOffsets != null) {
    		Log.d("MotionChangeTrack getSampleOffsets from Track sucess");
    		return sampleOffsets;
    	} else {
    		final List<ByteBuffer> samples = getSamples();
	    	if (samples instanceof SampleList) {
	    		Log.d("MotionChangeTrack getSampleOffsets sucess");
	    		return ((SampleList)samples).getSampleOffsets();
	    	}
    	}
    	return null;
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
            
        	int[] compositionTimes = CompositionTimeToSample.blowupCompositionTimes(origCtts);
            LinkedList<CompositionTimeToSample.Entry> returnDecodingEntries = new LinkedList<CompositionTimeToSample.Entry>();
            
            for (int i=0;i<compositionTimes.length;i++) {
            	final int compositionTime = compositionTimes[i];
            	
                CompositionTimeToSample.Entry e = null;
                double slowMotionScale = -1;
                for (VideoSector s : sectors) {
                    if (i >= s.startSample && i <= s.endSample) {
                    	slowMotionScale = s.slowMotionScale;
                    	break;
                    }
                }
                
                int newOffset = -1;
                if (slowMotionScale > 0) {
                	newOffset = (int)(compositionTime * slowMotionScale);
                } else {
                	newOffset = compositionTime;
                }
                
                if (returnDecodingEntries.isEmpty() || returnDecodingEntries.getLast().getOffset() != newOffset) {
                	e = new CompositionTimeToSample.Entry(1, newOffset);
                	returnDecodingEntries.add(e);
                } else {
                	e = returnDecodingEntries.getLast();
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
    List<TimeToSampleBox.Entry> adjustTts() {
        
    	List<TimeToSampleBox.Entry> origTts = source.getDecodingTimeEntries();
    	
		long[] decodingTimes = TimeToSampleBox.blowupTimeToSamples(origTts);        		
        LinkedList<TimeToSampleBox.Entry> returnEnrty = new LinkedList<TimeToSampleBox.Entry>();
        
        for (int i=0;i<decodingTimes.length;i++) {
        	final long decodingTime = decodingTimes[i];
        	
        	TimeToSampleBox.Entry e = null;
    		double slowMotionScale = -1;
    		for (VideoSector s : sectors) {
                if (i >= s.startSample && i <= s.endSample) {
                	slowMotionScale = s.slowMotionScale;
                	break;
                }
            }
    		
            long newDelta = -1;
            if (slowMotionScale > 0) {
            	newDelta = (long)(decodingTime * slowMotionScale);
            } else {
            	newDelta = decodingTime;
            }
            
            if (returnEnrty.isEmpty() || returnEnrty.getLast().getDelta() != newDelta) {
            	e = new TimeToSampleBox.Entry(1, newDelta);
            	returnEnrty.add(e);
            } else {
            	e = returnEnrty.getLast();
                e.setCount(e.getCount() + 1);
            }
        	
        }
        return returnEnrty;
        
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
}
