package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * 
 * @hide
 * {@exthide}
 */
public class BaseMediaInfoAtom extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "gmin";

    /**
     * @hide
     */
    short graphicsMode = 64;
    /**
     * @hide
     */
    int opColorR = 32768;
    /**
     * @hide
     */
    int opColorG = 32768;
    /**
     * @hide
     */
    int opColorB = 32768;
    /**
     * @hide
     */
    short balance;
    /**
     * @hide
     */
    short reserved;

    /**
     * @hide
     */
    public BaseMediaInfoAtom() {
        super(TYPE);
    }

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return 16;
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.putShort(graphicsMode);
        IsoTypeWriter.writeUInt16(byteBuffer, opColorR);
        IsoTypeWriter.writeUInt16(byteBuffer,opColorG );
        IsoTypeWriter.writeUInt16(byteBuffer,opColorB );
        byteBuffer.putShort(balance);
        byteBuffer.putShort(reserved);
    }

    /**
     * @hide
     */
    @Override
    protected void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        graphicsMode = content.getShort();
        opColorR = IsoTypeReader.readUInt16(content);
        opColorG = IsoTypeReader.readUInt16(content);
        opColorB = IsoTypeReader.readUInt16(content);
        balance = content.getShort();
        reserved = content.getShort();

    }

    /**
     * @hide
     */
    public short getGraphicsMode() {
        return graphicsMode;
    }

    /**
     * @hide
     */
    public void setGraphicsMode(short graphicsMode) {
        this.graphicsMode = graphicsMode;
    }

    /**
     * @hide
     */
    public int getOpColorR() {
        return opColorR;
    }

    /**
     * @hide
     */
    public void setOpColorR(int opColorR) {
        this.opColorR = opColorR;
    }

    /**
     * @hide
     */
    public int getOpColorG() {
        return opColorG;
    }

    /**
     * @hide
     */
    public void setOpColorG(int opColorG) {
        this.opColorG = opColorG;
    }

    /**
     * @hide
     */
    public int getOpColorB() {
        return opColorB;
    }

    /**
     * @hide
     */
    public void setOpColorB(int opColorB) {
        this.opColorB = opColorB;
    }

    /**
     * @hide
     */
    public short getBalance() {
        return balance;
    }

    /**
     * @hide
     */
    public void setBalance(short balance) {
        this.balance = balance;
    }

    /**
     * @hide
     */
    public short getReserved() {
        return reserved;
    }

    /**
     * @hide
     */
    public void setReserved(short reserved) {
        this.reserved = reserved;
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "BaseMediaInfoAtom{" +
                "graphicsMode=" + graphicsMode +
                ", opColorR=" + opColorR +
                ", opColorG=" + opColorG +
                ", opColorB=" + opColorB +
                ", balance=" + balance +
                ", reserved=" + reserved +
                '}';
    }
}
