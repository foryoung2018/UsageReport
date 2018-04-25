/*
 * Copyright 2012 Sebastian Annies, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.SampleEntry;

import java.nio.ByteBuffer;

/**
 * Entry type for timed text samples defined in the timed text specification (ISO/IEC 14496-17).
 * @hide
 * {@exthide}
 */
public class QuicktimeTextSampleEntry extends SampleEntry {
    /**
     * @hide
     */
    public static final String TYPE = "text";
    /**
     * @hide
     */
    int displayFlags;
    /**
     * @hide
     */
    int textJustification;

    /**
     * @hide
     */
    int backgroundR;
    /**
     * @hide
     */
    int backgroundG;
    /**
     * @hide
     */
    int backgroundB;

    /**
     * @hide
     */
    long defaultTextBox;
    /**
     * @hide
     */
    long reserved1;

    /**
     * @hide
     */
    short fontNumber;
    /**
     * @hide
     */
    short fontFace;
    /**
     * @hide
     */
    byte reserved2;
    /**
     * @hide
     */
    short reserved3;

    /**
     * @hide
     */
    int foregroundR = 65535;
    /**
     * @hide
     */
    int foregroundG = 65535;
    /**
     * @hide
     */
    int foregroundB = 65535;

    /**
     * @hide
     */
    String fontName = "";

    /**
     * @hide
     */
    public QuicktimeTextSampleEntry() {
        super(TYPE);
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        _parseReservedAndDataReferenceIndex(content);

        displayFlags = content.getInt();
        textJustification = content.getInt();
        backgroundR = IsoTypeReader.readUInt16(content);
        backgroundG = IsoTypeReader.readUInt16(content);
        backgroundB = IsoTypeReader.readUInt16(content);
        defaultTextBox = IsoTypeReader.readUInt64(content);
        reserved1 = IsoTypeReader.readUInt64(content);
        fontNumber = content.getShort();
        fontFace = content.getShort();
        reserved2 = content.get();
        reserved3 = content.getShort();
        foregroundR = IsoTypeReader.readUInt16(content);
        foregroundG = IsoTypeReader.readUInt16(content);
        foregroundB = IsoTypeReader.readUInt16(content);

        if (content.remaining() > 0) {
            int length = IsoTypeReader.readUInt8(content);
            byte[] myFontName = new byte[length];
            content.get(myFontName);
            fontName = new String(myFontName);
        } else {
            fontName = null;
        }
    }

    /**
     * @hide
     */
    protected long getContentSize() {
        return 52 + (fontName != null ? fontName.length() : 0);
    }

    /**
     * @hide
     */
    public int getDisplayFlags() {
        return displayFlags;
    }

    /**
     * @hide
     */
    public void setDisplayFlags(int displayFlags) {
        this.displayFlags = displayFlags;
    }

    /**
     * @hide
     */
    public int getTextJustification() {
        return textJustification;
    }

    /**
     * @hide
     */
    public void setTextJustification(int textJustification) {
        this.textJustification = textJustification;
    }

    /**
     * @hide
     */
    public int getBackgroundR() {
        return backgroundR;
    }

    /**
     * @hide
     */
    public void setBackgroundR(int backgroundR) {
        this.backgroundR = backgroundR;
    }

    /**
     * @hide
     */
    public int getBackgroundG() {
        return backgroundG;
    }

    /**
     * @hide
     */
    public void setBackgroundG(int backgroundG) {
        this.backgroundG = backgroundG;
    }

    /**
     * @hide
     */
    public int getBackgroundB() {
        return backgroundB;
    }

    /**
     * @hide
     */
    public void setBackgroundB(int backgroundB) {
        this.backgroundB = backgroundB;
    }

    /**
     * @hide
     */
    public long getDefaultTextBox() {
        return defaultTextBox;
    }

    /**
     * @hide
     */
    public void setDefaultTextBox(long defaultTextBox) {
        this.defaultTextBox = defaultTextBox;
    }

    /**
     * @hide
     */
    public long getReserved1() {
        return reserved1;
    }

    /**
     * @hide
     */
    public void setReserved1(long reserved1) {
        this.reserved1 = reserved1;
    }

    /**
     * @hide
     */
    public short getFontNumber() {
        return fontNumber;
    }

    /**
     * @hide
     */
    public void setFontNumber(short fontNumber) {
        this.fontNumber = fontNumber;
    }

    /**
     * @hide
     */
    public short getFontFace() {
        return fontFace;
    }

    /**
     * @hide
     */
    public void setFontFace(short fontFace) {
        this.fontFace = fontFace;
    }

    /**
     * @hide
     */
    public byte getReserved2() {
        return reserved2;
    }

    /**
     * @hide
     */
    public void setReserved2(byte reserved2) {
        this.reserved2 = reserved2;
    }

    /**
     * @hide
     */
    public short getReserved3() {
        return reserved3;
    }

    /**
     * @hide
     */
    public void setReserved3(short reserved3) {
        this.reserved3 = reserved3;
    }

    /**
     * @hide
     */
    public int getForegroundR() {
        return foregroundR;
    }

    /**
     * @hide
     */
    public void setForegroundR(int foregroundR) {
        this.foregroundR = foregroundR;
    }

    /**
     * @hide
     */
    public int getForegroundG() {
        return foregroundG;
    }

    /**
     * @hide
     */
    public void setForegroundG(int foregroundG) {
        this.foregroundG = foregroundG;
    }

    /**
     * @hide
     */
    public int getForegroundB() {
        return foregroundB;
    }

    /**
     * @hide
     */
    public void setForegroundB(int foregroundB) {
        this.foregroundB = foregroundB;
    }

    /**
     * @hide
     */
    public String getFontName() {
        return fontName;
    }

    /**
     * @hide
     */
    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        _writeReservedAndDataReferenceIndex(byteBuffer);
        byteBuffer.putInt(displayFlags);
        byteBuffer.putInt(textJustification);
        IsoTypeWriter.writeUInt16(byteBuffer, backgroundR);
        IsoTypeWriter.writeUInt16(byteBuffer, backgroundG);
        IsoTypeWriter.writeUInt16(byteBuffer, backgroundB);
        IsoTypeWriter.writeUInt64(byteBuffer, defaultTextBox);
        IsoTypeWriter.writeUInt64(byteBuffer, reserved1);
        byteBuffer.putShort(fontNumber);
        byteBuffer.putShort(fontFace);
        byteBuffer.put(reserved2);
        byteBuffer.putShort(reserved3);

        IsoTypeWriter.writeUInt16(byteBuffer, foregroundR);
        IsoTypeWriter.writeUInt16(byteBuffer, foregroundG);
        IsoTypeWriter.writeUInt16(byteBuffer, foregroundB);
        if (fontName != null) {
            IsoTypeWriter.writeUInt8(byteBuffer, fontName.length());
            byteBuffer.put(fontName.getBytes());
        }

    }


}
