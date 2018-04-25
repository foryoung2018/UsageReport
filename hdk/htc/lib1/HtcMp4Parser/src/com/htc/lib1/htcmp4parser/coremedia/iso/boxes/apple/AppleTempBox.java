package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * Beats per minute.
 *  @hide
 * {@exthide}
 */
public final class AppleTempBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "tmpo";

    /**
     * @hide
     */
    public AppleTempBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getUint16AppleDataBox();
    }

    /**
     * @hide
     */
    public int getTempo() {
        return appleDataBox.getData()[1];
    }
    /**
     * @hide
     */
    public void setTempo(int tempo) {
        appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFlags(21);
        appleDataBox.setFourBytes(new byte[4]);
        appleDataBox.setData(new byte[]{0, (byte) (tempo & 0xFF)});

    }
}
