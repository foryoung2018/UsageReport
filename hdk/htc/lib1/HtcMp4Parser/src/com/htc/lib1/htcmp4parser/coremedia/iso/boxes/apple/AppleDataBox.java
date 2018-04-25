package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.apple;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * Most stupid box of the world. Encapsulates actual data within
 *  @hide
 * {@exthide}
 */
public final class AppleDataBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "data";

    private byte[] fourBytes = new byte[4];
    private byte[] data;

    private static AppleDataBox getEmpty() {
        AppleDataBox appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFourBytes(new byte[4]);
        return appleDataBox;
    }
    /**
     * @hide
     */
    public static AppleDataBox getStringAppleDataBox() {
        AppleDataBox appleDataBox = getEmpty();
        appleDataBox.setFlags(1);
        appleDataBox.setData(new byte[]{0});
        return appleDataBox;
    }
    /**
     * @hide
     */
    public static AppleDataBox getUint8AppleDataBox() {
        AppleDataBox appleDataBox = new AppleDataBox();
        appleDataBox.setFlags(21);
        appleDataBox.setData(new byte[]{0});
        return appleDataBox;
    }
    /**
     * @hide
     */
    public static AppleDataBox getUint16AppleDataBox() {
        AppleDataBox appleDataBox = new AppleDataBox();
        appleDataBox.setFlags(21);
        appleDataBox.setData(new byte[]{0, 0});
        return appleDataBox;
    }
    /**
     * @hide
     */
    public static AppleDataBox getUint32AppleDataBox() {
        AppleDataBox appleDataBox = new AppleDataBox();
        appleDataBox.setFlags(21);
        appleDataBox.setData(new byte[]{0, 0, 0, 0});
        return appleDataBox;
    }
    /**
     * @hide
     */
    public AppleDataBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    protected long getContentSize() {
        return data.length + 8;
    }
    /**
     * @hide
     */
    public void setData(byte[] data) {
    	if (data != null) {
    		this.data = new byte[data.length];
            System.arraycopy(data, 0, this.data, 0, data.length);
    	} else {
    		android.util.Log.e(AppleDataBox.class.getName(),"byte[] data is null when setData()");
    	}        
    }
    /**
     * @hide
     */
    public void setFourBytes(byte[] fourBytes) {
        System.arraycopy(fourBytes, 0, this.fourBytes, 0, 4);
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        fourBytes = new byte[4];
        content.get(fourBytes);
        data = new byte[content.remaining()];
        content.get(data);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(fourBytes, 0, 4);
        byteBuffer.put(data);
    }
    /**
     * @hide
     */
    public byte[] getFourBytes() {
        return fourBytes;
    }
    /**
     * @hide
     */
    public byte[] getData() {
        return data;
    }
}
