package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import java.nio.ByteBuffer;

/**
 * @hide
 * {@exthide}
 */

public class SubtitleMediaHeaderBox extends AbstractMediaHeaderBox {

	/**
	 * @hide
	 */
    public static final String TYPE = "sthd";

    /**
     * @hide
     */
    public SubtitleMediaHeaderBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    protected long getContentSize() {
        return 4;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
    }

    /**
     * @hide
     */
    public String toString() {
        return "SubtitleMediaHeaderBox";
    }
}
