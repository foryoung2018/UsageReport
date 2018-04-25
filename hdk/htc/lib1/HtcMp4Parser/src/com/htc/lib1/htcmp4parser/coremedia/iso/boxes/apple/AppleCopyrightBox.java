package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * itunes MetaData comment box.
 *  @hide
 * {@exthide}
 */
public final class AppleCopyrightBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "cprt";

    /**
     * @hide
     */
    public AppleCopyrightBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

}
