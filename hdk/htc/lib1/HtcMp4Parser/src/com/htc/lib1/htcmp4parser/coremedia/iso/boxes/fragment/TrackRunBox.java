/*
 * Copyright 2009 castLabs GmbH, Berlin
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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MovieBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * aligned(8) class TrackRunBox
 * extends FullBox('trun', 0, tr_flags) {
 * unsigned int(32) sample_count;
 * // the following are optional fields
 * signed int(32) data_offset;
 * unsigned int(32) first_sample_flags;
 * // all fields in the following array are optional
 * {
 * unsigned int(32) sample_duration;
 * unsigned int(32) sample_size;
 * unsigned int(32) sample_flags
 * unsigned int(32) sample_composition_time_offset;
 * }[ sample_count ]
 * }
 *  @hide
 * {@exthide}
 */

public class TrackRunBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "trun";
    private int dataOffset;
    private SampleFlags firstSampleFlags;
    private List<Entry> entries = new ArrayList<Entry>();

    /**
     * @hide
     */
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * @hide
     */
    public static class Entry {
        private long sampleDuration;
        private long sampleSize;
        private SampleFlags sampleFlags;
        private int sampleCompositionTimeOffset;
    /**
     * @hide
     */
        public Entry() {
        }
    /**
     * @hide
     */
        public Entry(long sampleDuration, long sampleSize, SampleFlags sampleFlags, int sampleCompositionTimeOffset) {
            this.sampleDuration = sampleDuration;
            this.sampleSize = sampleSize;
            this.sampleFlags = sampleFlags;
            this.sampleCompositionTimeOffset = sampleCompositionTimeOffset;
        }
    /**
     * @hide
     */
        public long getSampleDuration() {
            return sampleDuration;
        }
    /**
     * @hide
     */
        public long getSampleSize() {
            return sampleSize;
        }
    /**
     * @hide
     */
        public SampleFlags getSampleFlags() {
            return sampleFlags;
        }
    /**
     * @hide
     */
        public int getSampleCompositionTimeOffset() {
            return sampleCompositionTimeOffset;
        }
    /**
     * @hide
     */
        public void setSampleDuration(long sampleDuration) {
            this.sampleDuration = sampleDuration;
        }
    /**
     * @hide
     */
        public void setSampleSize(long sampleSize) {
            this.sampleSize = sampleSize;
        }
    /**
     * @hide
     */
        public void setSampleFlags(SampleFlags sampleFlags) {
            this.sampleFlags = sampleFlags;
        }
    /**
     * @hide
     */
        public void setSampleCompositionTimeOffset(int sampleCompositionTimeOffset) {
            this.sampleCompositionTimeOffset = sampleCompositionTimeOffset;
        }
    /**
     * @hide
     */
        @Override
        public String toString() {
            return "Entry{" +
                    "sampleDuration=" + sampleDuration +
                    ", sampleSize=" + sampleSize +
                    ", sampleFlags=" + sampleFlags +
                    ", sampleCompositionTimeOffset=" + sampleCompositionTimeOffset +
                    '}';
        }
    }
    /**
     * @hide
     */
    public void setDataOffset(int dataOffset) {
        if (dataOffset == -1) {
            setFlags(getFlags() & (0xFFFFFF ^ 1));
        } else {
            setFlags(getFlags() | 0x1); // turn on dataoffset
        }
        this.dataOffset = dataOffset;
    }
    /**
     * @hide
     */
    public long[] getSampleCompositionTimeOffsets() {
        if (isSampleCompositionTimeOffsetPresent()) {
            long[] result = new long[entries.size()];

            for (int i = 0; i < result.length; i++) {
                result[i] = entries.get(i).getSampleCompositionTimeOffset();
            }
            return result;
        }
        return null;
    }
    /**
     * @hide
     */
    public TrackExtendsBox getTrackExtendsBox() {
        final TrackFragmentHeaderBox tfhd = ((TrackFragmentBox) getParent()).getTrackFragmentHeaderBox();
        final List<MovieBox> movieBoxes;
        
        if (tfhd != null) {
        	movieBoxes = tfhd.getIsoFile().getBoxes(MovieBox.class);
        } else {        	
        	android.util.Log.e(TrackRunBox.class.getName(), "getTrackFragmentHeaderBox is null");
        	return null;
        }        
        if (movieBoxes.size() == 0) {
            return null;
        }

        final List<TrackExtendsBox> trexBoxes = movieBoxes.get(0).getBoxes(TrackExtendsBox.class, true);
        TrackExtendsBox trex = null;
        for (TrackExtendsBox aTrex : trexBoxes) {
            if (aTrex.getTrackId() == tfhd.getTrackId()) {
                trex = aTrex;
            }
        }
        return trex;
    }
    /**
     * @hide
     */
    public TrackRunBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    protected long getContentSize() {
        long size = 8;
        int flags = getFlags();

        if ((flags & 0x1) == 0x1) { //dataOffsetPresent
            size += 4;
        }
        if ((flags & 0x4) == 0x4) { //firstSampleFlagsPresent
            size += 4;
        }

        long entrySize = 0;
        if ((flags & 0x100) == 0x100) { //sampleDurationPresent
            entrySize += 4;
        }
        if ((flags & 0x200) == 0x200) { //sampleSizePresent
            entrySize += 4;
        }
        if ((flags & 0x400) == 0x400) { //sampleFlagsPresent
            entrySize += 4;
        }
        if ((flags & 0x800) == 0x800) { //sampleCompositionTimeOffsetPresent
            entrySize += 4;
        }
        size += entrySize * entries.size();
        return size;
    }
    /**
     * @hide
     */
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, entries.size());
        int flags = getFlags();

        if ((flags & 0x1) == 1) { //dataOffsetPresent
            IsoTypeWriter.writeUInt32(byteBuffer, dataOffset);
        }
        if ((flags & 0x4) == 0x4) { //firstSampleFlagsPresent
            firstSampleFlags.getContent(byteBuffer);
        }

        for (Entry entry : entries) {
            if ((flags & 0x100) == 0x100) { //sampleDurationPresent
                IsoTypeWriter.writeUInt32(byteBuffer, entry.sampleDuration);
            }
            if ((flags & 0x200) == 0x200) { //sampleSizePresent
                IsoTypeWriter.writeUInt32(byteBuffer, entry.sampleSize);
            }
            if ((flags & 0x400) == 0x400) { //sampleFlagsPresent
                entry.sampleFlags.getContent(byteBuffer);
            }
            if ((flags & 0x800) == 0x800) { //sampleCompositionTimeOffsetPresent
                byteBuffer.putInt(entry.sampleCompositionTimeOffset);
            }
        }
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        long sampleCount = IsoTypeReader.readUInt32(content);

        if ((getFlags() & 0x1) == 1) { //dataOffsetPresent
            dataOffset = l2i(IsoTypeReader.readUInt32(content));
        } else {
            dataOffset = -1;
        }
        if ((getFlags() & 0x4) == 0x4) { //firstSampleFlagsPresent
            firstSampleFlags = new SampleFlags(content);
        }

        for (int i = 0; i < sampleCount; i++) {
            Entry entry = new Entry();
            if ((getFlags() & 0x100) == 0x100) { //sampleDurationPresent
                entry.sampleDuration = IsoTypeReader.readUInt32(content);
            }
            if ((getFlags() & 0x200) == 0x200) { //sampleSizePresent
                entry.sampleSize = IsoTypeReader.readUInt32(content);
            }
            if ((getFlags() & 0x400) == 0x400) { //sampleFlagsPresent
                entry.sampleFlags = new SampleFlags(content);
            }
            if ((getFlags() & 0x800) == 0x800) { //sampleCompositionTimeOffsetPresent
                entry.sampleCompositionTimeOffset = content.getInt();
            }
            entries.add(entry);
        }

    }
    /**
     * @hide
     */
    public long getSampleCount() {
        return entries.size();
    }
    /**
     * @hide
     */
    public boolean isDataOffsetPresent() {
        return (getFlags() & 0x1) == 1;
    }
    /**
     * @hide
     */
    public boolean isFirstSampleFlagsPresent() {
        return (getFlags() & 0x4) == 0x4;
    }

    /**
     * @hide
     */
    public boolean isSampleSizePresent() {
        return (getFlags() & 0x200) == 0x200;
    }
    /**
     * @hide
     */
    public boolean isSampleDurationPresent() {
        return (getFlags() & 0x100) == 0x100;
    }
    /**
     * @hide
     */
    public boolean isSampleFlagsPresent() {
        return (getFlags() & 0x400) == 0x400;
    }
    /**
     * @hide
     */
    public boolean isSampleCompositionTimeOffsetPresent() {
        return (getFlags() & 0x800) == 0x800;
    }
    /**
     * @hide
     */
    public void setDataOffsetPresent(boolean v) {
        if (v) {
            setFlags(getFlags() | 0x01);
        } else {
            setFlags(getFlags() & (0xFFFFFF ^ 0x1));
        }
    }
    /**
     * @hide
     */
    public void setSampleSizePresent(boolean v) {
        if (v) {
            setFlags(getFlags() | 0x200);
        } else {
            setFlags(getFlags() & (0xFFFFFF ^ 0x200));
        }
    }
    /**
     * @hide
     */
    public void setSampleDurationPresent(boolean v) {

        if (v) {
            setFlags(getFlags() | 0x100);
        } else {
            setFlags(getFlags() & (0xFFFFFF ^ 0x100));
        }
    }
    /**
     * @hide
     */
    public void setSampleFlagsPresent(boolean v) {
        if (v) {
            setFlags(getFlags() | 0x400);
        } else {
            setFlags(getFlags() & (0xFFFFFF ^ 0x400));
        }
    }
    /**
     * @hide
     */
    public void setSampleCompositionTimeOffsetPresent(boolean v) {
        if (v) {
            setFlags(getFlags() | 0x800);
        } else {
            setFlags(getFlags() & (0xFFFFFF ^ 0x800));
        }

    }
    /**
     * @hide
     */
    public int getDataOffset() {
        return dataOffset;
    }
    /**
     * @hide
     */
    public SampleFlags getFirstSampleFlags() {
        return firstSampleFlags;
    }
    /**
     * @hide
     */
    public void setFirstSampleFlags(SampleFlags firstSampleFlags) {
        if (firstSampleFlags == null) {
            setFlags(getFlags() & (0xFFFFFF ^ 0x4));
        } else {
            setFlags(getFlags() | 0x4);
        }
        this.firstSampleFlags = firstSampleFlags;
    }
    /**
     * @hide
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TrackRunBox");
        sb.append("{sampleCount=").append(entries.size());
        sb.append(", dataOffset=").append(dataOffset);
        sb.append(", dataOffsetPresent=").append(isDataOffsetPresent());
        sb.append(", sampleSizePresent=").append(isSampleSizePresent());
        sb.append(", sampleDurationPresent=").append(isSampleDurationPresent());
        sb.append(", sampleFlagsPresentPresent=").append(isSampleFlagsPresent());
        sb.append(", sampleCompositionTimeOffsetPresent=").append(isSampleCompositionTimeOffsetPresent());
        sb.append(", firstSampleFlags=").append(firstSampleFlags);
        sb.append('}');
        return sb.toString();
    }
    /**
     * @hide
     */
    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
