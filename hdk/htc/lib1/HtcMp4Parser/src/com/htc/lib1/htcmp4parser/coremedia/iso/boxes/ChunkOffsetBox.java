package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

/**
 * Abstract Chunk Offset Box
 *  @hide
 * {@exthide}
 */
public abstract class ChunkOffsetBox extends AbstractFullBox {

	/**
	 *  @hide
	 */
    public ChunkOffsetBox(String type) {
        super(type);
    }

    /**
     *  @hide
     */
    public abstract long[] getChunkOffsets();

    /**
     *  @hide
     */
    public String toString() {
        return this.getClass().getSimpleName() + "[entryCount=" + getChunkOffsets().length + "]";
    }

}
