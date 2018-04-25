package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleCustomGenreBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "\u00a9gen";

    /**
     * @hide
     */
    public AppleCustomGenreBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
    /**
     * @hide
     */
    public void setGenre(String genre) {
        appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFlags(1);
        appleDataBox.setFourBytes(new byte[4]);
        appleDataBox.setData(Utf8.convert(genre));
    }
    /**
     * @hide
     */
    public String getGenre() {
        return Utf8.convert(appleDataBox.getData());
    }
}
