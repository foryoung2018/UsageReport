package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleTrackTitleBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "\u00a9nam";

    /**
     * @hide
     */
    public AppleTrackTitleBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

}
