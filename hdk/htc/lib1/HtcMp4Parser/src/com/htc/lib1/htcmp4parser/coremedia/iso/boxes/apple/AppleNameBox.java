package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * Apple Name box. Allowed as subbox of "----" box.
 *
 * @see AppleGenericBox
 *  @hide
 * {@exthide}
 */
public final class AppleNameBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "name";
    private String name;
    /**
     * @hide
     */
    public AppleNameBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    protected long getContentSize() {
        return 4 + Utf8.utf8StringLengthInBytes(name);
    }
    /**
     * @hide
     */
    public String getName() {
        return name;
    }
    /**
     * @hide
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        name = IsoTypeReader.readString(content, content.remaining());
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(Utf8.convert(name));
    }
}
