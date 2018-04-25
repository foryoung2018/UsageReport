package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * itunes MetaData comment box.
 *  @hide
 * {@exthide}
 */
public final class AppleGroupingBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "\u00a9grp";

    /**
     * @hide
     */
    public AppleGroupingBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

}
