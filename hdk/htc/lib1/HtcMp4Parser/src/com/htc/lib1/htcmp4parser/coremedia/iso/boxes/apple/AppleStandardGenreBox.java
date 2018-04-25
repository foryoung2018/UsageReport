package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleStandardGenreBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "gnre";

    /**
     * @hide
     */
    public AppleStandardGenreBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getUint16AppleDataBox();
    }
}
