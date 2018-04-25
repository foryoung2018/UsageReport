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

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * Each sample of a track may be associated to (zero or) one of a number of sample group descriptions, each of
 * which defines a record of rate-share information. Typically the same rate-share information applies to many
 * consecutive samples and it may therefore be enough to define two or three sample group descriptions that
 * can be used at different time intervals.
 * <p/>
 * The grouping type 'rash' (short for rate share) is defined as the grouping criterion for rate share information.
 * Zero or one sample-to-group box ('sbgp') for the grouping type 'rash' can be contained in the sample
 * table box ('stbl') of a track. It shall reside in a hint track, if a hint track is used, otherwise in a media track.
 * <p/>
 * Target rate share may be specified for several operation points that are defined in terms of the total available
 * bitrate, i.e., the bitrate that should be shared. If only one operation point is defined, the target rate share
 * applies to all available bitrates. If several operation points are defined, then each operation point specifies a
 * target rate share. Target rate share values specified for the first and the last operation points also specify the
 * target rate share values at lower and higher available bitrates, respectively. The target rate share between two
 * operation points is specified to be in the range between the target rate shares of those operation points. One
 * possibility is to estimate with linear interpolation.
 * @hide
 */
public class RateShareEntry extends GroupEntry {
	/**
	 * @hide
	 */
    public static final String TYPE = "rash";

    private short operationPointCut;
    private short targetRateShare;
    private List<Entry> entries = new LinkedList<Entry>();
    private int maximumBitrate;
    private int minimumBitrate;
    private short discardPriority;

    /**
     * @hide
     */
    @Override
    public void parse(ByteBuffer byteBuffer) {
        operationPointCut = byteBuffer.getShort();
        if (operationPointCut == 1) {
            targetRateShare = byteBuffer.getShort();
        } else {
            int entriesLeft = operationPointCut;
            while (entriesLeft-- > 0) {
                entries.add(new Entry(l2i(IsoTypeReader.readUInt32(byteBuffer)), byteBuffer.getShort()));
            }
        }
        maximumBitrate = l2i(IsoTypeReader.readUInt32(byteBuffer));
        minimumBitrate = l2i(IsoTypeReader.readUInt32(byteBuffer));
        discardPriority = (short) IsoTypeReader.readUInt8(byteBuffer);
    }
    /**
     * @hide
     */
    @Override
    public ByteBuffer get() {
        ByteBuffer buf = ByteBuffer.allocate(operationPointCut == 1?13:(operationPointCut * 6 + 11 ));
        buf.putShort(operationPointCut);
        if (operationPointCut == 1) {
            buf.putShort(targetRateShare );
        } else {
            for (Entry entry : entries) {
                buf.putInt(entry.getAvailableBitrate());
                buf.putShort(entry.getTargetRateShare());
            }
        }
        buf.putInt(maximumBitrate);
        buf.putInt(minimumBitrate);
        IsoTypeWriter.writeUInt8(buf, discardPriority);
        buf.rewind();
        return buf;
    }
    /**
     * @hide
     */
    public static class Entry {
    	/**
    	 * @hide
    	 */
        public Entry(int availableBitrate, short targetRateShare) {
            this.availableBitrate = availableBitrate;
            this.targetRateShare = targetRateShare;
        }
        /**
         * @hide
         */
        int availableBitrate;
        /**
         * @hide
         */
        short targetRateShare;
        /**
         * @hide
         */
        @Override
        public String toString() {
            return "{" +
                    "availableBitrate=" + availableBitrate +
                    ", targetRateShare=" + targetRateShare +
                    '}';
        }
        /**
         * @hide
         */
        public int getAvailableBitrate() {
            return availableBitrate;
        }
        /**
         * @hide
         */
        public void setAvailableBitrate(int availableBitrate) {
            this.availableBitrate = availableBitrate;
        }
        /**
         * @hide
         */
        public short getTargetRateShare() {
            return targetRateShare;
        }
        /**
         * @hide
         */
        public void setTargetRateShare(short targetRateShare) {
            this.targetRateShare = targetRateShare;
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

            if (availableBitrate != entry.availableBitrate) {
                return false;
            }
            if (targetRateShare != entry.targetRateShare) {
                return false;
            }

            return true;
        }
        /**
         * @hide
         */
        @Override
        public int hashCode() {
            int result = availableBitrate;
            result = 31 * result + (int) targetRateShare;
            return result;
        }
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

        RateShareEntry that = (RateShareEntry) o;

        if (discardPriority != that.discardPriority) {
            return false;
        }
        if (maximumBitrate != that.maximumBitrate) {
            return false;
        }
        if (minimumBitrate != that.minimumBitrate) {
            return false;
        }
        if (operationPointCut != that.operationPointCut) {
            return false;
        }
        if (targetRateShare != that.targetRateShare) {
            return false;
        }
        if (entries != null ? !entries.equals(that.entries) : that.entries != null) {
            return false;
        }

        return true;
    }
    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = (int) operationPointCut;
        result = 31 * result + (int) targetRateShare;
        result = 31 * result + (entries != null ? entries.hashCode() : 0);
        result = 31 * result + maximumBitrate;
        result = 31 * result + minimumBitrate;
        result = 31 * result + (int) discardPriority;
        return result;
    }
    /**
     * @hide
     */
    public short getOperationPointCut() {
        return operationPointCut;
    }
    /**
     * @hide
     */
    public void setOperationPointCut(short operationPointCut) {
        this.operationPointCut = operationPointCut;
    }
    /**
     * @hide
     */
    public short getTargetRateShare() {
        return targetRateShare;
    }
    /**
     * @hide
     */
    public void setTargetRateShare(short targetRateShare) {
        this.targetRateShare = targetRateShare;
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
    public int getMaximumBitrate() {
        return maximumBitrate;
    }
    /**
     * @hide
     */
    public void setMaximumBitrate(int maximumBitrate) {
        this.maximumBitrate = maximumBitrate;
    }
    /**
     * @hide
     */
    public int getMinimumBitrate() {
        return minimumBitrate;
    }
    /**
     * @hide
     */
    public void setMinimumBitrate(int minimumBitrate) {
        this.minimumBitrate = minimumBitrate;
    }
    /**
     * @hide
     */
    public short getDiscardPriority() {
        return discardPriority;
    }
    /**
     * @hide
     */
    public void setDiscardPriority(short discardPriority) {
        this.discardPriority = discardPriority;
    }
}
