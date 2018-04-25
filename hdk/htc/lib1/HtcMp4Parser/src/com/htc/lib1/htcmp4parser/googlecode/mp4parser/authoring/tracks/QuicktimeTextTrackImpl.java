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
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple.BaseMediaInfoAtom;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple.GenericMediaHeaderAtom;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple.GenericMediaHeaderTextAtom;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple.QuicktimeTextSampleEntry;
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
 * A Text track as Quicktime Pro would create.
 * @hide
 * {@exthide}
 */
public class QuicktimeTextTrackImpl extends AbstractTrack {
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
     * @return
     */
    public List<Line> getSubs() {
        return subs;
    }
    
    /**
     * @hide
     */
    public QuicktimeTextTrackImpl() {
        sampleDescriptionBox = new SampleDescriptionBox();
        QuicktimeTextSampleEntry textTrack = new QuicktimeTextSampleEntry();
        textTrack.setDataReferenceIndex(1);
        sampleDescriptionBox.addBox(textTrack);


        trackMetaData.setCreationTime(new Date());
        trackMetaData.setModificationTime(new Date());
        trackMetaData.setTimescale(1000);


    }

    /**
     * @hide
     * @return
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
     * @return
     */
    public SampleDescriptionBox getSampleDescriptionBox() {
        return sampleDescriptionBox;
    }

    /**
     * @hide
     * @return
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
    public TrackMetaData getTrackMetaData() {
        return trackMetaData;
    }

    /**
     * @hide
     * @return
     */
    public String getHandler() {
        return "text";
    }

    /**
     * @hide
     * {@exthide}
     *
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
         * @param from
         * @param to
         * @param text
         */
        public Line(long from, long to, String text) {
            this.from = from;
            this.to = to;
            this.text = text;
        }

        /**
         * @hide
         * @return
         */
        public long getFrom() {
            return from;
        }
        
        /**
         * @hide
         * @return
         */
        public String getText() {
            return text;
        }
        
        /**
         * @hide
         * @return
         */
        public long getTo() {
            return to;
        }
    }
    
    /**
     * @hide
     * @return
     */
    public Box getMediaHeaderBox() {
        GenericMediaHeaderAtom ghmd = new GenericMediaHeaderAtom();
        ghmd.addBox(new BaseMediaInfoAtom());
        ghmd.addBox(new GenericMediaHeaderTextAtom());
        return ghmd;
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
	@Override
	public List<SampleOffset> getSampleOffsets() {
		// TODO Auto-generated method stub
		return null;
	}
}
