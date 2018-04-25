package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * Tv Episode.
 *  @hide
 * {@exthide}
 */
public class AppleTvEpisodeNumberBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "tven";

    /**
     * @hide
     */
    public AppleTvEpisodeNumberBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

}
