package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.tracks;

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.*;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList.SampleOffset;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.SampleEntry;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Track;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.TrackMetaData;
import com.htc.lib1.htcmp4parser.utils.Log;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This is just a basic idea how things could work but they don't.
 * @hide
 * {@exthide}
 */
public class SilenceTrackImpl implements Track {
    /**
     * @hide
     */
    Track source;
    /**
     * @hide
     */
    List<ByteBuffer> samples = new LinkedList<ByteBuffer>();
    /**
     * @hide
     */
    TimeToSampleBox.Entry entry;

    /**
     * @hide
     * @param ofType
     * @param ms
     */
    public SilenceTrackImpl(Track ofType, long ms) {
        source = ofType;
        SampleEntry sampleEntry = ofType.getSampleDescriptionBox().getSampleEntry();
        if (sampleEntry != null) {
			if ("mp4a".equals(sampleEntry.getType())) {
				long numFrames = getTrackMetaData().getTimescale() * ms / 1000 / 1024;
				long standZeit = getTrackMetaData().getTimescale() * ms	/ numFrames / 1000;
				entry = new TimeToSampleBox.Entry(numFrames, standZeit);

				while (numFrames-- > 0) {
					samples.add((ByteBuffer) ByteBuffer.wrap( new byte[] { 0x21, 0x10, 0x04, 0x60, (byte) 0x8c,	0x1c, }).rewind());
				}
			} else {
				throw new RuntimeException("Tracks of type " + ofType.getClass().getSimpleName() + " are not supported");
			}
        } else {
        	Log.e(SilenceTrackImpl.class.getName(), "Get sampleEntry is null");
        }        
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
        return Collections.singletonList(entry);

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
    public List<ByteBuffer> getSamples() {
        return samples;
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
        return null;
    }

    /**
     * @hide
     * @return
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return null;
    }

    /**
     * @hide
     * @return
     */
    public long[] getSyncSamples() {
        return null;
    }

    /**
     * @hide
     * @return
     */
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
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
