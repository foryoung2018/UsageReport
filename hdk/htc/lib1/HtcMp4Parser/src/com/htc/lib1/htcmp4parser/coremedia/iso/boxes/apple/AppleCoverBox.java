package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

import java.util.logging.Logger;

/**
 *
 *  @hide
 * {@exthide}
 */
public final class AppleCoverBox extends AbstractAppleMetaDataBox {
    private static Logger LOG = Logger.getLogger(AppleCoverBox.class.getName());
    /**
     * @hide
     */
    public static final String TYPE = "covr";

    /**
     * @hide
     */
    public AppleCoverBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public void setPng(byte[] pngData) {
        appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFlags(0xe);
        appleDataBox.setFourBytes(new byte[4]);
        appleDataBox.setData(pngData);
    }

    /**
     * @hide
     */
    public void setJpg(byte[] jpgData) {
        appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFlags(0xd);
        appleDataBox.setFourBytes(new byte[4]);
        appleDataBox.setData(jpgData);
    }
    /**
     * @hide
     */
    @Override
    public void setValue(String value) {
        LOG.warning("---");
    }
    /**
     * @hide
     */
    @Override
    public String getValue() {
        return "---";
    }
}
