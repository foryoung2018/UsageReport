package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * Apple Meaning box. Allowed as subbox of "----" box.
 *
 * @see com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple.AppleGenericBox
 *  @hide
 * {@exthide}
 */
public final class AppleMeanBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "mean";
    private String meaning;
    /**
     * @hide
     */
    public AppleMeanBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    protected long getContentSize() {
        return 4 + Utf8.utf8StringLengthInBytes(meaning);
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        meaning = IsoTypeReader.readString(content, content.remaining());
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(Utf8.convert(meaning));
    }
    /**
     * @hide
     */
    public String getMeaning() {
        return meaning;
    }
    /**
     * @hide
     */
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }


}
