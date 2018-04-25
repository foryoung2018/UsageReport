package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class ApplePurchaseDateBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "purd";

    /**
     * @hide
     */
    public ApplePurchaseDateBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

}
