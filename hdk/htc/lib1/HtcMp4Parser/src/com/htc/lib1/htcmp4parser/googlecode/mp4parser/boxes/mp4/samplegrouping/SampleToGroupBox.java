/*
 * Copyright 2012 castLabs, Berlin
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

package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * This table can be used to find the group that a sample belongs to and the associated description of that
 * sample group. The table is compactly coded with each entry giving the index of the first sample of a run of
 * samples with the same sample group descriptor. The sample group description ID is an index that refers to a
 * SampleGroupDescription box, which contains entries describing the characteristics of each sample group.
 * <p/>
 * There may be multiple instances of this box if there is more than one sample grouping for the samples in a
 * track. Each instance of the SampleToGroup box has a type code that distinguishes different sample
 * groupings. Within a track, there shall be at most one instance of this box with a particular grouping type. The
 * associated SampleGroupDescription shall indicate the same value for the grouping type.
 * <p/>
 * Version 1 of this box should only be used if a grouping type parameter is needed.
 * @hide
 */
public class SampleToGroupBox extends AbstractFullBox {
	/**
	 * @hide
	 */
    public static final String TYPE = "sbgp";


    private String groupingType;
    private String groupingTypeParameter;
    /**
     * @hide
     */
    List<Entry> entries = new LinkedList<Entry>();
    /**
     * @hide
     */
    public SampleToGroupBox() {
        super(TYPE);

    }
    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return this.getVersion() == 1 ? entries.size() * 8 + 16 : entries.size() * 8 + 12;
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(groupingType.getBytes());
        if (this.getVersion() == 1) {
            byteBuffer.put(groupingTypeParameter.getBytes());
        }
        IsoTypeWriter.writeUInt32(byteBuffer, entries.size());
        for (Entry entry : entries) {
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getSampleCount());
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getGroupDescriptionIndex());
        }

    }
    /**
     * @hide
     */
    @Override
    protected void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        groupingType = IsoTypeReader.read4cc(content);
        if (this.getVersion() == 1) {
            groupingTypeParameter = IsoTypeReader.read4cc(content);
        }
        long entryCount = IsoTypeReader.readUInt32(content);
        while (entryCount-- > 0) {
            entries.add(new Entry(l2i(IsoTypeReader.readUInt32(content)), l2i(IsoTypeReader.readUInt32(content))));
        }
    }
    /**
     * @hide
     */
    public static class Entry {
        private long sampleCount;
        private int groupDescriptionIndex;
        /**
         * @hide
         */
        public Entry(long sampleCount, int groupDescriptionIndex) {
            this.sampleCount = sampleCount;
            this.groupDescriptionIndex = groupDescriptionIndex;
        }
        /**
         * @hide
         */
        public long getSampleCount() {
            return sampleCount;
        }
        /**
         * @hide
         */
        public void setSampleCount(long sampleCount) {
            this.sampleCount = sampleCount;
        }
        /**
         * @hide
         */
        public int getGroupDescriptionIndex() {
            return groupDescriptionIndex;
        }
        /**
         * @hide
         */
        public void setGroupDescriptionIndex(int groupDescriptionIndex) {
            this.groupDescriptionIndex = groupDescriptionIndex;
        }
        /**
         * @hide
         */
        @Override
        public String toString() {
            return "Entry{" +
                    "sampleCount=" + sampleCount +
                    ", groupDescriptionIndex=" + groupDescriptionIndex +
                    '}';
        }
        /**
         * @hide
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Entry entry = (Entry) o;

            if (groupDescriptionIndex != entry.groupDescriptionIndex) {
                return false;
            }
            if (sampleCount != entry.sampleCount) {
                return false;
            }

            return true;
        }
        /**
         * @hide
         */
        @Override
        public int hashCode() {
            int result = (int) (sampleCount ^ (sampleCount >>> 32));
            result = 31 * result + groupDescriptionIndex;
            return result;
        }
    }
    /**
     * @hide
     */
    public String getGroupingType() {
        return groupingType;
    }
    /**
     * @hide
     */
    public void setGroupingType(String groupingType) {
        this.groupingType = groupingType;
    }
    /**
     * @hide
     */
    public String getGroupingTypeParameter() {
        return groupingTypeParameter;
    }
    /**
     * @hide
     */
    public void setGroupingTypeParameter(String groupingTypeParameter) {
        this.groupingTypeParameter = groupingTypeParameter;
    }
    /**
     * @hide
     */
    public List<Entry> getEntries() {
        return entries;
    }
    /**
     * @hide
     */
    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
