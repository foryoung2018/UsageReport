package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleDescriptionBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "desc";

    /**
     * @hide
     */
    public AppleDescriptionBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

}
