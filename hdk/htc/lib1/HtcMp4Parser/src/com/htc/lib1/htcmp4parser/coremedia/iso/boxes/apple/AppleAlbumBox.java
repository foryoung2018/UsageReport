package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleAlbumBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "\u00a9alb";

    /**
     * @hide
     */
    public AppleAlbumBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

}
