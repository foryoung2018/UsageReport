package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * itunes MetaData comment box.
 *  @hide
 * {@exthide}
 */
public class AppleAlbumArtistBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "aART";

    /**
     * @hide
     */
    public AppleAlbumArtistBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }


}
