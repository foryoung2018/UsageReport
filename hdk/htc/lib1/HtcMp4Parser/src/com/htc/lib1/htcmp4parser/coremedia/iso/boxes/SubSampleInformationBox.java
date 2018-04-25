package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * aligned(8) class SubSampleInformationBox
 * extends FullBox('subs', version, 0) {
 * unsigned int(32) entry_count;
 * int i,j;
 * for (i=0; i < entry_count; i++) {
 * unsigned int(32) sample_delta;
 * unsigned int(16) subsample_count;
 * if (subsample_count > 0) {
 * for (j=0; j < subsample_count; j++) {
 * if(version == 1)
 * {
 * unsigned int(32) subsample_size;
 * }
 * else
 * {
 * unsigned int(16) subsample_size;
 * }
 * unsigned int(8) subsample_priority;
 * unsigned int(8) discardable;
 * unsigned int(32) reserved = 0;
 * }
 * }
 * }
 * }
 */

/**
 * @hide
 * {@exthide}
 */
public class SubSampleInformationBox extends AbstractFullBox {
	/**
	 * @hide
	 */
	public static final String TYPE = "subs";

    private long entryCount;
    private List<SampleEntry> entries = new ArrayList<SampleEntry>();

    /**
     * @hide
     */
    public SubSampleInformationBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public List<SampleEntry> getEntries() {
        return entries;
    }

    /**
     * @hide
     */
    public void setEntries(List<SampleEntry> entries) {
        this.entries = entries;
        entryCount = entries.size();
    }

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        long entries = 8 + ((4 + 2) * entryCount);
        int subsampleEntries = 0;
        for (SampleEntry sampleEntry : this.entries) {
            subsampleEntries += sampleEntry.getSubsampleCount() * (((getVersion() == 1) ? 4 : 2) + 1 + 1 + 4);
        }
        return entries + subsampleEntries;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);

        entryCount = IsoTypeReader.readUInt32(content);

        for (int i = 0; i < entryCount; i++) {
            SampleEntry sampleEntry = new SampleEntry();
            sampleEntry.setSampleDelta(IsoTypeReader.readUInt32(content));
            int subsampleCount = IsoTypeReader.readUInt16(content);
            for (int j = 0; j < subsampleCount; j++) {
                SampleEntry.SubsampleEntry subsampleEntry = new SampleEntry.SubsampleEntry();
                subsampleEntry.setSubsampleSize(getVersion() == 1 ? IsoTypeReader.readUInt32(content) : IsoTypeReader.readUInt16(content));
                subsampleEntry.setSubsamplePriority(IsoTypeReader.readUInt8(content));
                subsampleEntry.setDiscardable(IsoTypeReader.readUInt8(content));
                subsampleEntry.setReserved(IsoTypeReader.readUInt32(content));
                sampleEntry.addSubsampleEntry(subsampleEntry);
            }
            entries.add(sampleEntry);
        }

    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, entries.size());
        for (SampleEntry sampleEntry : entries) {
            IsoTypeWriter.writeUInt32(byteBuffer, sampleEntry.getSampleDelta());
            IsoTypeWriter.writeUInt16(byteBuffer, sampleEntry.getSubsampleCount());
            List<SampleEntry.SubsampleEntry> subsampleEntries = sampleEntry.getSubsampleEntries();
            for (SampleEntry.SubsampleEntry subsampleEntry : subsampleEntries) {
                if (getVersion() == 1) {
                    IsoTypeWriter.writeUInt32(byteBuffer, subsampleEntry.getSubsampleSize());
                } else {
                    IsoTypeWriter.writeUInt16(byteBuffer, l2i(subsampleEntry.getSubsampleSize()));
                }
                IsoTypeWriter.writeUInt8(byteBuffer, subsampleEntry.getSubsamplePriority());
                IsoTypeWriter.writeUInt8(byteBuffer, subsampleEntry.getDiscardable());
                IsoTypeWriter.writeUInt32(byteBuffer, subsampleEntry.getReserved());
            }
        }
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "SubSampleInformationBox{" +
                "entryCount=" + entryCount +
                ", entries=" + entries +
                '}';
    }

    /**
     * @hide
     * {@exthide}
     */
    public static class SampleEntry {
        private long sampleDelta;
        private int subsampleCount;
        private List<SubsampleEntry> subsampleEntries = new ArrayList<SubsampleEntry>();

        /**
         * @hide
         */
        public long getSampleDelta() {
            return sampleDelta;
        }

        /**
         * @hide
         */
        public void setSampleDelta(long sampleDelta) {
            this.sampleDelta = sampleDelta;
        }

        /**
         * @hide
         */
        public int getSubsampleCount() {
            return subsampleCount;
        }

        /**
         * @hide
         */
        public void setSubsampleCount(int subsampleCount) {
            this.subsampleCount = subsampleCount;
        }

        /**
         * @hide
         */
        public List<SubsampleEntry> getSubsampleEntries() {
            return subsampleEntries;
        }

        /**
         * @hide
         */
        public void addSubsampleEntry(SubsampleEntry subsampleEntry) {
            subsampleEntries.add(subsampleEntry);
            subsampleCount++;
        }

        /**
         * @hide
         * {@exthide}
         */
        public static class SubsampleEntry {
            private long subsampleSize;
            private int subsamplePriority;
            private int discardable;
            private long reserved;

            /**
             * @hide
             */
            public long getSubsampleSize() {
                return subsampleSize;
            }

            /**
             * @hide
             */
            public void setSubsampleSize(long subsampleSize) {
                this.subsampleSize = subsampleSize;
            }

            /**
             * @hide
             */
            public int getSubsamplePriority() {
                return subsamplePriority;
            }

            /**
             * @hide
             */
            public void setSubsamplePriority(int subsamplePriority) {
                this.subsamplePriority = subsamplePriority;
            }

            /**
             * @hide
             */
            public int getDiscardable() {
                return discardable;
            }

            /**
             * @hide
             */
            public void setDiscardable(int discardable) {
                this.discardable = discardable;
            }

            /**
             * @hide
             */
            public long getReserved() {
                return reserved;
            }

            /**
             * @hide
             */
            public void setReserved(long reserved) {
                this.reserved = reserved;
            }

            /**
             * @hide
             */
            @Override
            public String toString() {
                return "SubsampleEntry{" +
                        "subsampleSize=" + subsampleSize +
                        ", subsamplePriority=" + subsamplePriority +
                        ", discardable=" + discardable +
                        ", reserved=" + reserved +
                        '}';
            }
        }

        /**
         * @hide
         */
        @Override
        public String toString() {
            return "SampleEntry{" +
                    "sampleDelta=" + sampleDelta +
                    ", subsampleCount=" + subsampleCount +
                    ", subsampleEntries=" + subsampleEntries +
                    '}';
        }
    }
}
