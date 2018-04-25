package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractContainerBox;

/**
 * undocumented iTunes MetaData Box.
 *  @hide
 * {@exthide}
 */
public class AppleItemListBox extends AbstractContainerBox {
    /**
     * @hide
     */
    public static final String TYPE = "ilst";
    /**
     * @hide
     */
    public AppleItemListBox() {
        super(TYPE);
    }

}
