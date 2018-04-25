package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * iTunes Rating Box.
 *  @hide
 * {@exthide}
 */
public final class AppleRatingBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "rtng";

    /**
     * @hide
     */
    public AppleRatingBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getUint8AppleDataBox();
    }


}
