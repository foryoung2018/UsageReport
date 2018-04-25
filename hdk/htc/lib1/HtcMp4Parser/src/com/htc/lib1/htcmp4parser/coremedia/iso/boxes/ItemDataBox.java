package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;

import java.nio.ByteBuffer;

/**
 * @hide
 * {@exthide}
 */
public class ItemDataBox extends AbstractBox {
	/**
	 * @hide
	 */
	ByteBuffer data = ByteBuffer.allocate(0);
	/**
	 * @hide
	 */
	public static final String TYPE = "idat";

	/**
	 * @hide
	 */
    public ItemDataBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public ByteBuffer getData() {
        return data;
    }

    /**
     * @hide
     */
    public void setData(ByteBuffer data) {
        this.data = data;
    }

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return data.limit();
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        data = content.slice();
        content.position(content.position() + content.remaining());
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(data);
    }
}
