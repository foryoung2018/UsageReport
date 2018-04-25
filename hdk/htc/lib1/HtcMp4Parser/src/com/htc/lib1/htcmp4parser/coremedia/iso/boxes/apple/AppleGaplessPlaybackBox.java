package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * Gapless Playback.
 *  @hide
 * {@exthide}
 */
public final class AppleGaplessPlaybackBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "pgap";

    /**
     * @hide
     */
    public AppleGaplessPlaybackBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getUint8AppleDataBox();
    }

}
