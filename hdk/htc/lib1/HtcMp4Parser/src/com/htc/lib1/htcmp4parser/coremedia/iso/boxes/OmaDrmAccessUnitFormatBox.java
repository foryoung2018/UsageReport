/*  
 * Copyright 2008 CoreMedia AG, Hamburg
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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * Describes the format of media access units in PDCF files.
 * @hide
 * {@exthide}
 */
public final class OmaDrmAccessUnitFormatBox extends AbstractFullBox {
	/**
	 * @hide
	 */
	public static final String TYPE = "odaf";

    private boolean selectiveEncryption;
    private byte allBits;

    private int keyIndicatorLength;
    private int initVectorLength;

    /**
     * @hide
     */
    protected long getContentSize() {
        return 7;
    }

    /**
     * @hide
     */
    public OmaDrmAccessUnitFormatBox() {
        super("odaf");
    }

    /**
     * @hide
     */
    public boolean isSelectiveEncryption() {
        return selectiveEncryption;
    }

    /**
     * @hide
     */
    public int getKeyIndicatorLength() {
        return keyIndicatorLength;
    }

    /**
     * @hide
     */
    public int getInitVectorLength() {
        return initVectorLength;
    }

    /**
     * @hide
     */
    public void setInitVectorLength(int initVectorLength) {
        this.initVectorLength = initVectorLength;
    }

    /**
     * @hide
     */
    public void setKeyIndicatorLength(int keyIndicatorLength) {
        this.keyIndicatorLength = keyIndicatorLength;
    }

    /**
     * @hide
     */
    public void setAllBits(byte allBits) {
        this.allBits = allBits;
        selectiveEncryption = (allBits & 0x80) == 0x80;
    }
    
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        allBits = (byte) IsoTypeReader.readUInt8(content);
        selectiveEncryption = (allBits & 0x80) == 0x80;
        keyIndicatorLength = IsoTypeReader.readUInt8(content);
        initVectorLength = IsoTypeReader.readUInt8(content);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt8(byteBuffer, allBits);
        IsoTypeWriter.writeUInt8(byteBuffer, keyIndicatorLength);
        IsoTypeWriter.writeUInt8(byteBuffer, initVectorLength);
    }

}
