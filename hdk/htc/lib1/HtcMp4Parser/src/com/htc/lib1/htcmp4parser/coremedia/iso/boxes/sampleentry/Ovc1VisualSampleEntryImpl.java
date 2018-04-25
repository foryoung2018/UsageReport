package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;

import java.nio.ByteBuffer;

/**
 * 
 *  @hide
 * {@exthide}
 */
public class Ovc1VisualSampleEntryImpl extends SampleEntry {
    private byte[] vc1Content;
    /**
     * @hide
     */
    public static final String TYPE = "ovc1";

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        long size = 8;

        for (Box box : boxes) {
            size += box.getSize();
        }
        size += vc1Content.length;
        return size;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        _parseReservedAndDataReferenceIndex(content);
        vc1Content = new byte[content.remaining()];
        content.get(vc1Content);

    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(new byte[6]);
        IsoTypeWriter.writeUInt16(byteBuffer, getDataReferenceIndex());
        byteBuffer.put(vc1Content);
    }

    /**
     * @hide
     */
    protected Ovc1VisualSampleEntryImpl() {
        super(TYPE);
    }

}
