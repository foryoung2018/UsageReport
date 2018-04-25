package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleTrackNumberBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "trkn";

    /**
     * @hide
     */
    public AppleTrackNumberBox() {
        super(TYPE);
    }


    /**
     * @param track the actual track number
     * @param of    number of tracks overall
     * @hide
     */
    public void setTrackNumber(byte track, byte of) {
        appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFlags(0);
        appleDataBox.setFourBytes(new byte[4]);
        appleDataBox.setData(new byte[]{0, 0, 0, track, 0, of, 0, 0});
    }
    /**
     * @hide
     */
    public byte getTrackNumber() {
        return appleDataBox.getData()[3];
    }
    /**
     * @hide
     */
    public byte getNumberOfTracks() {
        return appleDataBox.getData()[5];
    }
    /**
     * @hide
     */
    public void setNumberOfTracks(byte numberOfTracks) {
        byte[] content = appleDataBox.getData();
        content[5] = numberOfTracks;
        appleDataBox.setData(content);
    }
    /**
     * @hide
     */
    public void setTrackNumber(byte trackNumber) {
        byte[] content = appleDataBox.getData();
        content[3] = trackNumber;
        appleDataBox.setData(content);
    }


}
