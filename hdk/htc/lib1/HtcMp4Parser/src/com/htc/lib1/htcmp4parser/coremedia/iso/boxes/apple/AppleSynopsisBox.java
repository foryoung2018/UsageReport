package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleSynopsisBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "ldes";

    /**
     * @hide
     */
    public AppleSynopsisBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }


}
