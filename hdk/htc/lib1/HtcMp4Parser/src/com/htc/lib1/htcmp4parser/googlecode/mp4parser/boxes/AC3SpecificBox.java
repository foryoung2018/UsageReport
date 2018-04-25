package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;

import java.nio.ByteBuffer;

/**
 * @hide
 */
public class AC3SpecificBox extends AbstractBox {
	/**
	 * @hide
	 */
    int fscod;
    /**
     * @hide
     */
    int bsid;
    /**
     * @hide
     */
    int bsmod;
    /**
     * @hide
     */
    int acmod;
    /**
     * @hide
     */
    int lfeon;
    /**
     * @hide
     */
    int bitRateCode;
    /**
     * @hide
     */
    int reserved;

    /**
     * @hide
     */
    public AC3SpecificBox() {
        super("dac3");
    }
    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return 3;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        BitReaderBuffer brb = new BitReaderBuffer(content);
        fscod = brb.readBits(2);
        bsid = brb.readBits(5);
        bsmod = brb.readBits(3);
        acmod = brb.readBits(3);
        lfeon = brb.readBits(1);
        bitRateCode = brb.readBits(5);
        reserved = brb.readBits(5);
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        BitWriterBuffer bwb = new BitWriterBuffer(byteBuffer);
        bwb.writeBits(fscod, 2);
        bwb.writeBits(bsid, 5);
        bwb.writeBits(bsmod, 3);
        bwb.writeBits(acmod, 3);
        bwb.writeBits(lfeon, 1);
        bwb.writeBits(bitRateCode, 5);
        bwb.writeBits(reserved, 5);
    }
    /**
     * @hide
     */
    public int getFscod() {
        return fscod;
    }
    /**
     * @hide
     */
    public void setFscod(int fscod) {
        this.fscod = fscod;
    }
    /**
     * @hide
     */
    public int getBsid() {
        return bsid;
    }
    /**
     * @hide
     */
    public void setBsid(int bsid) {
        this.bsid = bsid;
    }
    /**
     * @hide
     */
    public int getBsmod() {
        return bsmod;
    }
    /**
     * @hide
     */
    public void setBsmod(int bsmod) {
        this.bsmod = bsmod;
    }
    /**
     * @hide
     */
    public int getAcmod() {
        return acmod;
    }
    /**
     * @hide
     */
    public void setAcmod(int acmod) {
        this.acmod = acmod;
    }
    /**
     * @hide
     */
    public int getLfeon() {
        return lfeon;
    }
    /**
     * @hide
     */
    public void setLfeon(int lfeon) {
        this.lfeon = lfeon;
    }
    /**
     * @hide
     */
    public int getBitRateCode() {
        return bitRateCode;
    }
    /**
     * @hide
     */
    public void setBitRateCode(int bitRateCode) {
        this.bitRateCode = bitRateCode;
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
    @Override
    public String toString() {
        return "AC3SpecificBox{" +
                "fscod=" + fscod +
                ", bsid=" + bsid +
                ", bsmod=" + bsmod +
                ", acmod=" + acmod +
                ", lfeon=" + lfeon +
                ", bitRateCode=" + bitRateCode +
                ", reserved=" + reserved +
                '}';
    }
}
