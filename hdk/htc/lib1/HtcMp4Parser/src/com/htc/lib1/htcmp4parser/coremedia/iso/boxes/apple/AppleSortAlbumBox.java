package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleSortAlbumBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "soal";

    /**
     * @hide
     */
    public AppleSortAlbumBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
