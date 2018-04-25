package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleIdBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "apID";

    /**
     * @hide
     */
    public AppleIdBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

}
