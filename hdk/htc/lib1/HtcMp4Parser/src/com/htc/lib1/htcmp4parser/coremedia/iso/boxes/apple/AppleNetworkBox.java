package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleNetworkBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "tvnn";

    /**
     * @hide
     */
    public AppleNetworkBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }


}
