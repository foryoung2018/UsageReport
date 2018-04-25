package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * iTunes Artist box.
 *  @hide
 * {@exthide}
 */
public final class AppleArtistBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "\u00a9ART";

    /**
     * @hide
     */
    public AppleArtistBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }


}
