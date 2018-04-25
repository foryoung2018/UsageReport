package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleTrackAuthorBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "\u00a9wrt";

    /**
     * @hide
     */
    public AppleTrackAuthorBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }


}
