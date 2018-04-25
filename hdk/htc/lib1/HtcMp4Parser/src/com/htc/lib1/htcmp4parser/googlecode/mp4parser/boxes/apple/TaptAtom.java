package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractContainerBox;

/**
 * Don't know what it is but it is obviously a container box.
 * @hide
 * {@exthide}
 */
public class TaptAtom extends AbstractContainerBox {
    /**
     * @hide
     */
    public static final String TYPE = "tapt";
    
    /**
     * @hide
     */
    public TaptAtom() {
        super(TYPE);
    }


}
