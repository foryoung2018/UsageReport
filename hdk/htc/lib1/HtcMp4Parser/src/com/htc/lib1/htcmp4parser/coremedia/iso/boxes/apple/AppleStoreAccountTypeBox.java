package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * itunes MetaData comment box.
 *  @hide
 * {@exthide}
 */
public class AppleStoreAccountTypeBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "akID";

    /**
     * @hide
     */
    public AppleStoreAccountTypeBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getUint8AppleDataBox();
    }
    /**
     * @hide
     */
    public String getReadableValue() {
        byte value = this.appleDataBox.getData()[0];
        switch (value) {
            case 0:
                return "iTunes Account";
            case 1:
                return "AOL Account";
            default:
                return "unknown Account";
        }

    }
}
