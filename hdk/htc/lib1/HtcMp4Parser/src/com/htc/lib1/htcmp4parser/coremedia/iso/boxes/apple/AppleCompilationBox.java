package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

/**
 * Compilation.
 *  @hide
 * {@exthide}
 */
public final class AppleCompilationBox extends AbstractAppleMetaDataBox {
    /**
     * @hide
     */
    public static final String TYPE = "cpil";

    /**
     * @hide
     */
    public AppleCompilationBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getUint8AppleDataBox();
    }

}
