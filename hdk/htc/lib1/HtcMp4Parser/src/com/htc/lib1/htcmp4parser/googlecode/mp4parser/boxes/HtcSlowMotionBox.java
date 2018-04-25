package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes;

import java.nio.ByteBuffer;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

/**
 * @hide
 */
public class HtcSlowMotionBox extends AbstractFullBox {
	/**
	 * @hide
	 */
	public static final String TYPE = "_htc";
	/**
	 * @hide
	 */
	public static final String SLOW_MOTION_TAG = "slmt";
	
	private String mode;
    private long scale;
    
    /**
     * @hide
     */
	public HtcSlowMotionBox() {
		super(TYPE);		
	}
	/**
	 * @hide
	 */
	@Override
	protected long getContentSize() {
		return 4 + 8; //versionAndFlag + mode and scale
	}
	/**
	 * @hide
	 */
	@Override
	protected void getContent(ByteBuffer byteBuffer) {
		writeVersionAndFlags(byteBuffer);
		byteBuffer.put(IsoFile.fourCCtoBytes(mode));
        IsoTypeWriter.writeUInt32(byteBuffer, scale);		
	}
	/**
	 * @hide
	 */
	@Override
	protected void _parseDetails(ByteBuffer content) {
		parseVersionAndFlags(content);
		mode = IsoTypeReader.read4cc(content);
		scale = IsoTypeReader.readUInt32(content);		
	}
	/**
	 * @hide
	 */
	public boolean isSlowMotion() {
        return SLOW_MOTION_TAG.equals(mode);
    }
	/**
	 * @hide
	 */
	public long getSlowMotionScale() {
		return scale;
	}

}