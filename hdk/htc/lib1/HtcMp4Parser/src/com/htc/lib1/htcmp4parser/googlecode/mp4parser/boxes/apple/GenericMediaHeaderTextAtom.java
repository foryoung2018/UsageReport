package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple;

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;

import java.nio.ByteBuffer;

/**
 * Undocumented atom in the gmhd atom of text tracks.
 * @hide
 * {@exthide}
 */
public class GenericMediaHeaderTextAtom extends AbstractBox {
    
    /**
     * @hide
     */
    public static final String TYPE = "text";
    
    /**
     * @hide
     */
    int unknown_1 = 65536;
    /**
     * @hide
     */
    int unknown_2;
    /**
     * @hide
     */
    int unknown_3;
    /**
     * @hide
     */
    int unknown_4;
    /**
     * @hide
     */
    int unknown_5 = 65536;
    /**
     * @hide
     */
    int unknown_6;
    /**
     * @hide
     */
    int unknown_7;
    /**
     * @hide
     */
    int unknown_8;
    /**
     * @hide
     */
    int unknown_9 = 1073741824;

    /**
     * @hide
     */
    public GenericMediaHeaderTextAtom() {
        super(TYPE);
    }

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return 36;
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.putInt(unknown_1);
        byteBuffer.putInt(unknown_2);
        byteBuffer.putInt(unknown_3);
        byteBuffer.putInt(unknown_4);
        byteBuffer.putInt(unknown_5);
        byteBuffer.putInt(unknown_6);
        byteBuffer.putInt(unknown_7);
        byteBuffer.putInt(unknown_8);
        byteBuffer.putInt(unknown_9);
    }

    /**
     * @hide
     */
    @Override
    protected void _parseDetails(ByteBuffer content) {
        unknown_1 = content.getInt();
        unknown_2 = content.getInt();
        unknown_3 = content.getInt();
        unknown_4 = content.getInt();
        unknown_5 = content.getInt();
        unknown_6 = content.getInt();
        unknown_7 = content.getInt();
        unknown_8 = content.getInt();
        unknown_9 = content.getInt();
    }

    /**
     * @hide
     */
    public int getUnknown_1() {
        return unknown_1;
    }

    /**
     * @hide
     */
    public void setUnknown_1(int unknown_1) {
        this.unknown_1 = unknown_1;
    }

    /**
     * @hide
     */
    public int getUnknown_2() {
        return unknown_2;
    }

    /**
     * @hide
     */
    public void setUnknown_2(int unknown_2) {
        this.unknown_2 = unknown_2;
    }

    /**
     * @hide
     */
    public int getUnknown_3() {
        return unknown_3;
    }

    /**
     * @hide
     */
    public void setUnknown_3(int unknown_3) {
        this.unknown_3 = unknown_3;
    }

    /**
     * @hide
     */
    public int getUnknown_4() {
        return unknown_4;
    }

    /**
     * @hide
     */
    public void setUnknown_4(int unknown_4) {
        this.unknown_4 = unknown_4;
    }

    /**
     * @hide
     */
    public int getUnknown_5() {
        return unknown_5;
    }

    /**
     * @hide
     */
    public void setUnknown_5(int unknown_5) {
        this.unknown_5 = unknown_5;
    }

    /**
     * @hide
     */
    public int getUnknown_6() {
        return unknown_6;
    }

    /**
     * @hide
     */
    public void setUnknown_6(int unknown_6) {
        this.unknown_6 = unknown_6;
    }

    /**
     * @hide
     */
    public int getUnknown_7() {
        return unknown_7;
    }

    /**
     * @hide
     */
    public void setUnknown_7(int unknown_7) {
        this.unknown_7 = unknown_7;
    }

    /**
     * @hide
     */
    public int getUnknown_8() {
        return unknown_8;
    }

    /**
     * @hide
     */
    public void setUnknown_8(int unknown_8) {
        this.unknown_8 = unknown_8;
    }

    /**
     * @hide
     */
    public int getUnknown_9() {
        return unknown_9;
    }

    /**
     * @hide
     */
    public void setUnknown_9(int unknown_9) {
        this.unknown_9 = unknown_9;
    }
}
