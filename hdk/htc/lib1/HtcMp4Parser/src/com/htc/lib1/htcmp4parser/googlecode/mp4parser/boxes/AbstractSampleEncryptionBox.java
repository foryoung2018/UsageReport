package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.Hex;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.TrackHeaderBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.basemediaformat.TrackEncryptionBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.Path;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @hide
 */
public abstract class AbstractSampleEncryptionBox extends AbstractFullBox {
	/**
	 * @hide
	 */
    int algorithmId = -1;
    /**
     * @hide
     */
    int ivSize = -1;
    /**
     * @hide
     */
    byte[] kid = new byte[16];
    /**
     * @hide
     */
    List<Entry> entries = new LinkedList<Entry>();

    /**
     * @hide
     */
    protected AbstractSampleEncryptionBox(String type) {
        super(type);
    }
    /**
     * @hide
     */
    public int getOffsetToFirstIV() {
        int offset = (getSize() > (1l << 32) ? 16 : 8);
        offset += isOverrideTrackEncryptionBoxParameters() ? 20 : 0;
        offset += 4; //num entries
        return offset;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        if ((getFlags() & 0x1) > 0) {
            algorithmId = IsoTypeReader.readUInt24(content);
            ivSize = IsoTypeReader.readUInt8(content);
            kid = new byte[16];
            content.get(kid);
        }
        long numOfEntries = IsoTypeReader.readUInt32(content);
        if (((getFlags() & 0x1) == 0)) {
            List<Box> tkhds = Path.getPaths(this, "/moov[0]/trak/tkhd");
            for (Box tkhd : tkhds) {
                if (((TrackHeaderBox) tkhd).getTrackId() == this.getParent().getBoxes(TrackFragmentHeaderBox.class).get(0).getTrackId()) {
                    TrackEncryptionBox tenc = (TrackEncryptionBox) Path.getPath(tkhd, "../mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/tenc[0]");
                    if (tenc!=null) {
                    	ivSize = tenc.getDefaultIvSize();
                    }
                }
            }
        }

        while (numOfEntries-- > 0) {
            Entry e = new Entry();
            e.iv = new byte[ivSize < 0 ? 8 : ivSize];  // default to 8
            content.get(e.iv);
            if ((getFlags() & 0x2) > 0) {
                int numOfPairs = IsoTypeReader.readUInt16(content);
                e.pairs = new LinkedList<Entry.Pair>();
                while (numOfPairs-- > 0) {
                    e.pairs.add(e.createPair(IsoTypeReader.readUInt16(content), IsoTypeReader.readUInt32(content)));
                }
            }
            entries.add(e);

        }
    }

    /**
     * @hide
     */
    public int getSampleCount() {
        return entries.size();
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
    public int getAlgorithmId() {
        return algorithmId;
    }
    /**
     * @hide
     */
    public void setAlgorithmId(int algorithmId) {
        this.algorithmId = algorithmId;
    }
    /**
     * @hide
     */
    public int getIvSize() {
        return ivSize;
    }
    /**
     * @hide
     */
    public void setIvSize(int ivSize) {
        this.ivSize = ivSize;
    }
    /**
     * @hide
     */
    public byte[] getKid() {
        return kid;
    }
    /**
     * @hide
     */
    public void setKid(byte[] kid) {
        this.kid = kid;
    }
    /**
     * @hide
     */
    public boolean isSubSampleEncryption() {
        return (getFlags() & 0x2) > 0;
    }
    /**
     * @hide
     */
    public boolean isOverrideTrackEncryptionBoxParameters() {
        return (getFlags() & 0x1) > 0;
    }
    /**
     * @hide
     */
    public void setSubSampleEncryption(boolean b) {
        if (b) {
            setFlags(getFlags() | 0x2);
        } else {
            setFlags(getFlags() & (0xffffff ^ 0x2));
        }
    }
    /**
     * @hide
     */
    public void setOverrideTrackEncryptionBoxParameters(boolean b) {
        if (b) {
            setFlags(getFlags() | 0x1);
        } else {
            setFlags(getFlags() & (0xffffff ^ 0x1));
        }
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        if (isOverrideTrackEncryptionBoxParameters()) {
            IsoTypeWriter.writeUInt24(byteBuffer, algorithmId);
            IsoTypeWriter.writeUInt8(byteBuffer, ivSize);
            byteBuffer.put(kid);
        }
        IsoTypeWriter.writeUInt32(byteBuffer, entries.size());
        for (Entry entry : entries) {
            if (isOverrideTrackEncryptionBoxParameters()) {
                byte[] ivFull = new byte[ivSize];
                System.arraycopy(entry.iv, 0, ivFull, ivSize - entry.iv.length, entry.iv.length);
                byteBuffer.put(ivFull);
            } else {
                // just put the iv - i don't know any better
                byteBuffer.put(entry.iv);
            }
            if (isSubSampleEncryption()) {
                IsoTypeWriter.writeUInt16(byteBuffer, entry.pairs.size());
                for (Entry.Pair pair : entry.pairs) {
                    IsoTypeWriter.writeUInt16(byteBuffer, pair.clear);
                    IsoTypeWriter.writeUInt32(byteBuffer, pair.encrypted);
                }
            }
        }
    }
    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        long contentSize = 4;
        if (isOverrideTrackEncryptionBoxParameters()) {
            contentSize += 4;
            contentSize += kid.length;
        }
        contentSize += 4;
        for (Entry entry : entries) {
            contentSize += entry.getSize();
        }
        return contentSize;
    }
    /**
     * @hide
     */
    @Override
    public void getBox(WritableByteChannel os) throws IOException {
        super.getBox(os);
    }
    /**
     * @hide
     */
    public Entry createEntry() {
        return new Entry();
    }
    /**
     * @hide
     */
    public class Entry {
    	/**
    	 * @hide
    	 */
        public byte[] iv;
        /**
         * @hide
         */
        public List<Pair> pairs = new LinkedList<Pair>();

        /**
         * @hide
         */
        public int getSize() {
            int size = 0;
            if (isOverrideTrackEncryptionBoxParameters()) {
                size = ivSize;
            } else {
                size = iv.length;
            }


            if (isSubSampleEncryption()) {
                size += 2;
                for (Entry.Pair pair : pairs) {
                    size += 6;
                }
            }
            return size;
        }
        /**
         * @hide
         */
        public Pair createPair(int clear, long encrypted) {
            return new Pair(clear, encrypted);
        }

        /**
         * @hide
         */
        public class Pair {
        	/**
        	 * @hide
        	 */
            public int clear;
            /**
             * @hide
             */
            public long encrypted;

            /**
             * @hide
             */
            public Pair(int clear, long encrypted) {
                this.clear = clear;
                this.encrypted = encrypted;
            }
            /**
             * @hide
             */
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Pair pair = (Pair) o;

                if (clear != pair.clear) return false;
                if (encrypted != pair.encrypted) return false;

                return true;
            }
            /**
             * @hide
             */
            @Override
            public int hashCode() {
                int result = clear;
                result = 31 * result + (int) (encrypted ^ (encrypted >>> 32));
                return result;
            }
            /**
             * @hide
             */
            @Override
            public String toString() {
                return "clr:" + clear + " enc:" + encrypted;
            }
        }

        /**
         * @hide
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Entry entry = (Entry) o;

            if (!new BigInteger(iv).equals(new BigInteger(entry.iv))) return false;
            if (pairs != null ? !pairs.equals(entry.pairs) : entry.pairs != null) return false;

            return true;
        }
        /**
         * @hide
         */
        @Override
        public int hashCode() {
            int result = iv != null ? Arrays.hashCode(iv) : 0;
            result = 31 * result + (pairs != null ? pairs.hashCode() : 0);
            return result;
        }
        /**
         * @hide
         */
        @Override
        public String toString() {
            return "Entry{" +
                    "iv=" + Hex.encodeHex(iv) +
                    ", pairs=" + pairs +
                    '}';
        }
    }
    /**
     * @hide
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractSampleEncryptionBox that = (AbstractSampleEncryptionBox) o;

        if (algorithmId != that.algorithmId) return false;
        if (ivSize != that.ivSize) return false;
        if (entries != null ? !entries.equals(that.entries) : that.entries != null) return false;
        if (!Arrays.equals(kid, that.kid)) return false;

        return true;
    }
    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = algorithmId;
        result = 31 * result + ivSize;
        result = 31 * result + (kid != null ? Arrays.hashCode(kid) : 0);
        result = 31 * result + (entries != null ? entries.hashCode() : 0);
        return result;
    }
    /**
     * @hide
     */
    public List<Short> getEntrySizes() {
        List<Short> entrySizes = new ArrayList<Short>(entries.size());
        for (Entry entry : entries) {
            short size = (short) entry.iv.length;
            if (isSubSampleEncryption()) {
                size += 2; //numPairs
                size += entry.pairs.size() * 6;
            }
            entrySizes.add(size);
        }
        return entrySizes;
    }
}
