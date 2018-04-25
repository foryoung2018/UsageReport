package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry;

import com.htc.lib1.htcmp4parser.coremedia.iso.BoxParser;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ContainerBox;

import java.nio.ByteBuffer;
import java.util.Arrays;
/**
 * 
 *  @hide
 * {@exthide}
 */
public class MpegSampleEntry extends SampleEntry implements ContainerBox {

    private BoxParser boxParser;
    /**
     * @hide
     */
    public MpegSampleEntry(String type) {
        super(type);
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
    protected long getContentSize() {
        long contentSize = 8;
        for (Box boxe : boxes) {
            contentSize += boxe.getSize();
        }
        return contentSize;
    }
    /**
     * @hide
     */
    public String toString() {
        return "MpegSampleEntry" + Arrays.asList(getBoxes());
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
