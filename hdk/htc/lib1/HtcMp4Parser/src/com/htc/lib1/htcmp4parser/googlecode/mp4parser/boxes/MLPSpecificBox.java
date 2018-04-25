package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;

import java.nio.ByteBuffer;

/**
 * @hide 
 */
public class MLPSpecificBox extends AbstractBox {

	/**
	 * @hide 
	 */
    int format_info;
    /**
     * @hide 
     */
    int peak_data_rate;
    /**
     * @hide 
     */
    int reserved;
    /**
     * @hide 
     */
    int reserved2;

    /**
     * @hide 
     */
    public MLPSpecificBox() {
        super("dmlp");
    }

    /**
     * @hide 
     */
    @Override
    protected long getContentSize() {
        return 10;
    }

    /**
     * @hide 
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        BitReaderBuffer brb = new BitReaderBuffer(content);
        format_info = brb.readBits(32);
        peak_data_rate = brb.readBits(15);
        reserved = brb.readBits(1);
        reserved2 = brb.readBits(32);
    }
    /**
     * @hide 
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        BitWriterBuffer bwb = new BitWriterBuffer(byteBuffer);
        bwb.writeBits(format_info, 32);
        bwb.writeBits(peak_data_rate, 15);
        bwb.writeBits(reserved, 1);
        bwb.writeBits(reserved2, 32);
        //To change body of implemented methods use File | Settings | File Templates.
    }
    /**
     * @hide 
     */
    public int getFormat_info() {
        return format_info;
    }
    /**
     * @hide 
     */
    public void setFormat_info(int format_info) {
        this.format_info = format_info;
    }
    /**
     * @hide 
     */
    public int getPeak_data_rate() {
        return peak_data_rate;
    }
    /**
     * @hide 
     */
    public void setPeak_data_rate(int peak_data_rate) {
        this.peak_data_rate = peak_data_rate;
    }
    /**
     * @hide 
     */
    public int getReserved() {
        return reserved;
    }
    /**
     * @hide 
     */
    public void setReserved(int reserved) {
        this.reserved = reserved;
    }
    /**
     * @hide 
     */
    public int getReserved2() {
        return reserved2;
    }
    /**
     * @hide 
     */
    public void setReserved2(int reserved2) {
        this.reserved2 = reserved2;
    }
}
