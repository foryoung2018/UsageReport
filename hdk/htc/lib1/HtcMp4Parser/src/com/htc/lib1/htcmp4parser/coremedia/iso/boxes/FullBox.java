package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;

/**
 * The <code>FullBox</code> contains all getters and setters specific
 * to a so-called full box according to the ISO/IEC 14496/12 specification.
 * @hide
 * {@exthide}
 */
public interface FullBox extends Box {
	/**
	 * @hide
	 */
	int getVersion();

	/**
	 * @hide
	 */
    void setVersion(int version);

    /**
     * @hide
     */
    int getFlags();

    /**
     * @hide
     */
    void setFlags(int flags);
}
