package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.adobe;

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.SampleEntry;

import java.nio.ByteBuffer;

/**
 * Sample Entry as used for Action Message Format tracks.
 * @hide
 */
public class ActionMessageFormat0SampleEntryBox extends SampleEntry {
	/**
     * @hide
     */
    public ActionMessageFormat0SampleEntryBox() {
        super("amf0");
    }

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        long size = 8;
        for (Box box : boxes) {
            size += box.getSize();
        }

        return size;
    }


    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        _parseReservedAndDataReferenceIndex(content);
        _parseChildBoxes(content);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        _writeReservedAndDataReferenceIndex(byteBuffer);
        _writeChildBoxes(byteBuffer);
    }
}
