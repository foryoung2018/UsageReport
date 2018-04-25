/*  
 * Copyright 2008 CoreMedia AG, Hamburg
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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * Samples within the media data are grouped into chunks. Chunks can be of different sizes, and the
 * samples within a chunk can have different sizes. This table can be used to find the chunk that
 * contains a sample, its position, and the associated sample description. Defined in ISO/IEC 14496-12.
 * @hide
 * {@exthide}
 */
public class SampleToChunkBox extends AbstractFullBox {
	/**
	 * @hide
	 */
	List<Entry> entries = Collections.emptyList();

    /**
     * @hide
     */
    public static final String TYPE = "stsc";

    /**
     * @hide
     */
    public SampleToChunkBox() {
        super(TYPE);
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

    /**
     * @hide
     */
    protected long getContentSize() {
        return entries.size() * 12 + 8;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);

        int entryCount = l2i(IsoTypeReader.readUInt32(content));
        entries = new ArrayList<Entry>(entryCount);
        for (int i = 0; i < entryCount; i++) {
            entries.add(new Entry(
                    IsoTypeReader.readUInt32(content),
                    IsoTypeReader.readUInt32(content),
                    IsoTypeReader.readUInt32(content)));
        }
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, entries.size());
        for (Entry entry : entries) {
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getFirstChunk());
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getSamplesPerChunk());
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getSampleDescriptionIndex());
        }
    }

    /**
     * @hide
     */
    public String toString() {
        return "SampleToChunkBox[entryCount=" + entries.size() + "]";
    }

    /**
     * Decompresses the list of entries and returns the number of samples per chunk for
     * every single chunk.
     *
     * @param chunkCount overall number of chunks
     * @return number of samples per chunk
     * @hide
     */
    public long[] blowup(int chunkCount) {
        long[] numberOfSamples = new long[chunkCount];
        int j = 0;
        List<SampleToChunkBox.Entry> sampleToChunkEntries = new LinkedList<Entry>(entries);
        Collections.reverse(sampleToChunkEntries);
        Iterator<Entry> iterator = sampleToChunkEntries.iterator();
        SampleToChunkBox.Entry currentEntry = iterator.next();

        for (int i = numberOfSamples.length; i > 1; i--) {
            numberOfSamples[i - 1] = currentEntry.getSamplesPerChunk();
            if (i == currentEntry.getFirstChunk()) {
                currentEntry = iterator.next();
            }
        }
        numberOfSamples[0] = currentEntry.getSamplesPerChunk();
        return numberOfSamples;
    }

    /**
     * @hide
     * {@exthide}
     */
    public static class Entry {
    	/**
    	 * @hide
    	 */
    	long firstChunk;
    	/**
    	 * @hide
    	 */
    	long samplesPerChunk;
    	/**
    	 * @hide
    	 */
    	long sampleDescriptionIndex;

    	/**
    	 * @hide
    	 */
        public Entry(long firstChunk, long samplesPerChunk, long sampleDescriptionIndex) {
            this.firstChunk = firstChunk;
            this.samplesPerChunk = samplesPerChunk;
            this.sampleDescriptionIndex = sampleDescriptionIndex;
        }

        /**
         * @hide
         */
        public long getFirstChunk() {
            return firstChunk;
        }

        /**
         * @hide
         */
        public void setFirstChunk(long firstChunk) {
            this.firstChunk = firstChunk;
        }

        /**
         * @hide
         */
        public long getSamplesPerChunk() {
            return samplesPerChunk;
        }

        /**
         * @hide
         */
        public void setSamplesPerChunk(long samplesPerChunk) {
            this.samplesPerChunk = samplesPerChunk;
        }

        /**
         * @hide
         */
        public long getSampleDescriptionIndex() {
            return sampleDescriptionIndex;
        }

        /**
         * @hide
         */
        public void setSampleDescriptionIndex(long sampleDescriptionIndex) {
            this.sampleDescriptionIndex = sampleDescriptionIndex;
        }

        /**
         * @hide
         */
        @Override
        public String toString() {
            return "Entry{" +
                    "firstChunk=" + firstChunk +
                    ", samplesPerChunk=" + samplesPerChunk +
                    ", sampleDescriptionIndex=" + sampleDescriptionIndex +
                    '}';
        }
    }
}
