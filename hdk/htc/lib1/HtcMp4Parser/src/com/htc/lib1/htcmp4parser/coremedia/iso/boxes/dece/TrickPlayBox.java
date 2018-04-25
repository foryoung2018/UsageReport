package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.dece;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * aligned(8) class TrickPlayBox
 * extends FullBox(‘trik’, version=0, flags=0)
 * {
 * for (i=0; I < sample_count; i++) {
 * unsigned int(2) pic_type;
 * unsigned int(6) dependency_level;
 * }
 * }
 *  @hide
 * {@exthide}
 */
public class TrickPlayBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "trik";

    private List<Entry> entries = new ArrayList<Entry>();
    /**
     * @hide
     */
    public TrickPlayBox() {
        super(TYPE);
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
    public List<Entry> getEntries() {
        return entries;
    }
    /**
     * @hide
     */
    public static class Entry {
    /**
     * @hide
     */
        public Entry() {
        }
    /**
     * @hide
     */
        public Entry(int value) {
            this.value = value;
        }


        private int value;
    /**
     * @hide
     */
        public int getPicType() {
            return (value >> 6) & 0x03;
        }
    /**
     * @hide
     */
        public void setPicType(int picType) {
            value = value & (0xff >> 3);
            value = (picType & 0x03) << 6 | value;
        }
    /**
     * @hide
     */
        public int getDependencyLevel() {
            return value & 0x3f;
        }
    /**
     * @hide
     */
        public void setDependencyLevel(int dependencyLevel) {
            value = (dependencyLevel & 0x3f) | value;
        }

    /**
     * @hide
     */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Entry");
            sb.append("{picType=").append(getPicType());
            sb.append(",dependencyLevel=").append(getDependencyLevel());
            sb.append('}');
            return sb.toString();
        }
    }
    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return 4 + entries.size();
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        while (content.remaining() > 0) {
            entries.add(new Entry(IsoTypeReader.readUInt8(content)));
        }
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        for (Entry entry : entries) {
            IsoTypeWriter.writeUInt8(byteBuffer, entry.value);
        }
    }
    /**
     * @hide
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TrickPlayBox");
        sb.append("{entries=").append(entries);
        sb.append('}');
        return sb.toString();
    }
}
