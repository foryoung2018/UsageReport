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
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList.SampleOffset;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.AbstractTrack;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Track;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.TrackMetaData;

import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;

/**
 * Generates a Track where a single sample has been replaced by a given <code>ByteBuffer</code>.
 * @hide
 * {@exthide}
 */
public class ReplaceSampleTrack extends AbstractTrack {
    /**
     * @hide
     */
    Track origTrack;
    private long sampleNumber;
    private ByteBuffer sampleContent;
    private List<ByteBuffer>  samples;

    /**
     * @hide
     * @param origTrack
     * @param sampleNumber
     * @param content
     */
    public ReplaceSampleTrack(Track origTrack, long sampleNumber, ByteBuffer content) {
        this.origTrack = origTrack;
        this.sampleNumber = sampleNumber;
        this.sampleContent = content;
        this.samples = new ReplaceASingleEntryList();

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
    public SampleDescriptionBox getSampleDescriptionBox() {
        return origTrack.getSampleDescriptionBox();
    }

    /**
     * @hide
     * @return
     */
    public List<TimeToSampleBox.Entry> getDecodingTimeEntries() {
        return origTrack.getDecodingTimeEntries();

    }

    /**
     * @hide
     * @return
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return origTrack.getCompositionTimeEntries();

    }
    
    /**
     * @hide
     * @return
     */
    synchronized public long[] getSyncSamples() {
        return origTrack.getSyncSamples();
    }

    /**
     * @hide
     * @return
     */
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return origTrack.getSampleDependencies();
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

    private class ReplaceASingleEntryList extends AbstractList<ByteBuffer> {
        @Override
        public ByteBuffer get(int index) {
            if (ReplaceSampleTrack.this.sampleNumber == index) {
                return ReplaceSampleTrack.this.sampleContent;
            } else {
                return ReplaceSampleTrack.this.origTrack.getSamples().get(index);
            }
        }

        @Override
        public int size() {
            return ReplaceSampleTrack.this.origTrack.getSamples().size();
        }
    }
    
    /**
     * @hide
     * @return
     */
	@Override
	public List<SampleOffset> getSampleOffsets() {
		// TODO Auto-generated method stub
		return null;
	}

}
