package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * Tv Season.
 *  @hide
 * {@exthide}
 */
public final class AppleTvSeasonBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "tvsn";

    /**
     * @hide
     */
    public AppleTvSeasonBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getUint32AppleDataBox();
    }

}
