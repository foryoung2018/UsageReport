package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * Tv Episode.
 *  @hide
 * {@exthide}
 */
public class AppleTvEpisodeBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "tves";

    /**
     * @hide
     */
    public AppleTvEpisodeBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getUint32AppleDataBox();
    }

}
