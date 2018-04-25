package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleShowBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "tvsh";

    /**
     * @hide
     */
    public AppleShowBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

}
