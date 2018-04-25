package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes;

import java.nio.ByteBuffer;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;

/**
 * @hide
 * {@exthide}
 */
public class GeoDataBox extends AbstractBox {

	private byte[] content = null;
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public GeoDataBox(){
		super("©xyz");
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	@Override
	protected long getContentSize() {
		return null != content ? content.length : 0;
	}

	/**
	 * @hide
	 * {@exthide}
	 */
	@Override
	protected void getContent(ByteBuffer byteBuffer) {
		if(null == content){
			return;
		}
		byteBuffer.put(content, 0,  content.length);
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	@Override
	protected void _parseDetails(ByteBuffer content) {
		this.content = new byte[ content.remaining()];
		content.get(this.content, 0, this.content.length);
	}
}
