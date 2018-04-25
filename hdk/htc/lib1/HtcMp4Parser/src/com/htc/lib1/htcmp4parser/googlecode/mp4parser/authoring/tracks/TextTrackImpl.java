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
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.TextSampleEntry;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.AbstractTrack;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.TrackMetaData;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.threegpp26245.FontTableBox;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @hide
 * {@exthide}
 */
public class TextTrackImpl extends AbstractTrack {
    /**
     * @hide
     */
    TrackMetaData trackMetaData = new TrackMetaData();
    /**
     * @hide
     */
    SampleDescriptionBox sampleDescriptionBox;
    /**
     * @hide
     */
    List<Line> subs = new LinkedList<Line>();

    /**
     * @hide
     */
    public List<Line> getSubs() {
        return subs;
    }

    /**
     * @hide
     */
    public TextTrackImpl() {
        sampleDescriptionBox = new SampleDescriptionBox();
        TextSampleEntry tx3g = new TextSampleEntry("tx3g");
        tx3g.setDataReferenceIndex(1);
        tx3g.setStyleRecord(new TextSampleEntry.StyleRecord());
        tx3g.setBoxRecord(new TextSampleEntry.BoxRecord());
        sampleDescriptionBox.addBox(tx3g);

        FontTableBox ftab = new FontTableBox();
        ftab.setEntries(Collections.singletonList(new FontTableBox.FontRecord(1, "Serif")));

        tx3g.addBox(ftab);


        trackMetaData.setCreationTime(new Date());
        trackMetaData.setModificationTime(new Date());
        trackMetaData.setTimescale(1000); // Text tracks use millieseconds


    }

    /**
     * @hide
     */
    public List<ByteBuffer> getSamples() {
        List<ByteBuffer> samples = new LinkedList<ByteBuffer>();
        long lastEnd = 0;
        for (Line sub : subs) {
            long silentTime = sub.from - lastEnd;
            if (silentTime > 0) {
                samples.add(ByteBuffer.wrap(new byte[]{0, 0}));
            } else if (silentTime < 0) {
                throw new Error("Subtitle display times may not intersect");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            try {
                dos.writeShort(sub.text.getBytes("UTF-8").length);
                dos.write(sub.text.getBytes("UTF-8"));
                dos.close();
            } catch (IOException e) {
                throw new Error("VM is broken. Does not support UTF-8");
            }
            samples.add(ByteBuffer.wrap(baos.toByteArray()));
            lastEnd = sub.to;
        }
        return samples;
    }
    
    /**
     * @hide
     */
    public SampleDescriptionBox getSampleDescriptionBox() {
        return sampleDescriptionBox;
    }

    /**
     * @hide
     */
    public List<TimeToSampleBox.Entry> getDecodingTimeEntries() {
        List<TimeToSampleBox.Entry> stts = new LinkedList<TimeToSampleBox.Entry>();
        long lastEnd = 0;
        for (Line sub : subs) {
            long silentTime = sub.from - lastEnd;
            if (silentTime > 0) {
                stts.add(new TimeToSampleBox.Entry(1, silentTime));
            } else if (silentTime < 0) {
                throw new Error("Subtitle display times may not intersect");
            }
            stts.add(new TimeToSampleBox.Entry(1, sub.to - sub.from));
            lastEnd = sub.to;
        }
        return stts;
    }

    /**
     * @hide
     */
    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return null;
    }

    /**
     * @hide
     */
    public long[] getSyncSamples() {
        return null;
    }

    /**
     * @hide
     */
    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    /**
     * @hide
     */
    public TrackMetaData getTrackMetaData() {
        return trackMetaData;
    }

    /**
     * @hide
     */
    public String getHandler() {
        return "sbtl";
    }

    /**
     * @hide
     * {@exthide}
     */
    public static class Line {

        /**
         * @hide
         */
        long from;
        /**
         * @hide
         */
        long to;
        /**
         * @hide
         */
        String text;

        /**
         * @hide
         */
        public Line(long from, long to, String text) {
            this.from = from;
            this.to = to;
            this.text = text;
        }
        
        /**
         * @hide
         */
        public long getFrom() {
            return from;
        }
        
        /**
         * @hide
         */
        public String getText() {
            return text;
        }
        
        /**
         * @hide
         */
        public long getTo() {
            return to;
        }
    }
    
    /**
     * @hide
     */
    public AbstractMediaHeaderBox getMediaHeaderBox() {
        return new NullMediaHeaderBox();
    }

    /**
     * @hide
     */
    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }

    /**
     * @hide
     */
	@Override
	public List<SampleOffset> getSampleOffsets() {
		// TODO Auto-generated method stub
		return null;
	}
}
