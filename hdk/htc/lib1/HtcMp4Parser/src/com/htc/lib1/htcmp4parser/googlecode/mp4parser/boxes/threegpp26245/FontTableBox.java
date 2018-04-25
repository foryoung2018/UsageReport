package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.threegpp26245;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * @hide
 */
public class FontTableBox extends AbstractBox {
	/**
	 * @hide
	 */
    List<FontRecord> entries = new LinkedList<FontRecord>();

    /**
     * @hide
     */
    public FontTableBox() {
        super("ftab");
    }

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        int size = 2;
        for (FontRecord fontRecord : entries) {
            size += fontRecord.getSize();
        }
        return size;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        int numberOfRecords = IsoTypeReader.readUInt16(content);
        for (int i = 0; i < numberOfRecords; i++) {
            FontRecord fr = new FontRecord();
            fr.parse(content);
            entries.add(fr);
        }
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt16(byteBuffer, entries.size());
        for (FontRecord record : entries) {
            record.getContent(byteBuffer);
        }
    }
    /**
     * @hide
     */
    public List<FontRecord> getEntries() {
        return entries;
    }
    /**
     * @hide
     */
    public void setEntries(List<FontRecord> entries) {
        this.entries = entries;
    }
    /**
     * @hide
     */
    public static class FontRecord {
    	/**
    	 * @hide
    	 */
        int fontId;
        /**
         * @hide
         */
        String fontname;
        /**
         * @hide
         */
        public FontRecord() {
        }
        /**
         * @hide
         */
        public FontRecord(int fontId, String fontname) {
            this.fontId = fontId;
            this.fontname = fontname;
        }
        /**
         * @hide
         */
        public void parse(ByteBuffer bb) {
            fontId = IsoTypeReader.readUInt16(bb);
            int length = IsoTypeReader.readUInt8(bb);
            fontname = IsoTypeReader.readString(bb, length);
        }
        /**
         * @hide
         */
        public void getContent(ByteBuffer bb) {
            IsoTypeWriter.writeUInt16(bb, fontId);
            IsoTypeWriter.writeUInt8(bb, fontname.length());
            bb.put(Utf8.convert(fontname));
        }
        /**
         * @hide
         */
        public int getSize() {
            return Utf8.utf8StringLengthInBytes(fontname) + 3;
        }
        /**
         * @hide
         */
        @Override
        public String toString() {
            return "FontRecord{" +
                    "fontId=" + fontId +
                    ", fontname='" + fontname + '\'' +
                    '}';
        }
    }
}
