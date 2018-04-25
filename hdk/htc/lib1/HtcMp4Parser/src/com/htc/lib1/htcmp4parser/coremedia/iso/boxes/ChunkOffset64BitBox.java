package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;

import java.nio.ByteBuffer;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * Abstract Chunk Offset Box
 *  @hide
 * {@exthide}
 */
public class ChunkOffset64BitBox extends ChunkOffsetBox {
	/**
	 *  @hide
	 */
	public static final String TYPE = "co64";
    private long[] chunkOffsets;

    /**
     *  @hide
     */
    public ChunkOffset64BitBox() {
        super(TYPE);
    }

    /**
     *  @hide
     */
    @Override
    public long[] getChunkOffsets() {
        return chunkOffsets;
    }

    /**
     *  @hide
     */
    @Override
    protected long getContentSize() {
        return 8 + 8 * chunkOffsets.length;
    }

    /**
     *  @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        int entryCount = l2i(IsoTypeReader.readUInt32(content));
        chunkOffsets = new long[entryCount];
        for (int i = 0; i < entryCount; i++) {
            chunkOffsets[i] = IsoTypeReader.readUInt64(content);
        }
    }

    /**
     *  @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, chunkOffsets.length);
        for (long chunkOffset : chunkOffsets) {
            IsoTypeWriter.writeUInt64(byteBuffer, chunkOffset);
        }
    }


}
