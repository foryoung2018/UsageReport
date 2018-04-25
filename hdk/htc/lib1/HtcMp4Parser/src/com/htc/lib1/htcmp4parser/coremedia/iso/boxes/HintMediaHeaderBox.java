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

import java.nio.ByteBuffer;

/**
 * The hint media header contains general information, independent of the protocaol, for hint tracks. Resides
 * in Media Information Box.
 *
 * @see com.htc.lib1.htcmp4parser.coremedia.iso.boxes.MediaInformationBox
 * @hide
 * {@exthide}
 */
public class HintMediaHeaderBox extends AbstractMediaHeaderBox {
    private int maxPduSize;
    private int avgPduSize;
    private long maxBitrate;
    private long avgBitrate;
    /**
     * @hide
     */
    public static final String TYPE = "hmhd";

    /**
     * @hide
     */
    public HintMediaHeaderBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public int getMaxPduSize() {
        return maxPduSize;
    }

    /**
     * @hide
     */
    public int getAvgPduSize() {
        return avgPduSize;
    }

    /**
     * @hide
     */
    public long getMaxBitrate() {
        return maxBitrate;
    }

    /**
     * @hide
     */
    public long getAvgBitrate() {
        return avgBitrate;
    }

    /**
     * @hide
     */
    protected long getContentSize() {
        return 20;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        maxPduSize = IsoTypeReader.readUInt16(content);
        avgPduSize = IsoTypeReader.readUInt16(content);
        maxBitrate = IsoTypeReader.readUInt32(content);
        avgBitrate = IsoTypeReader.readUInt32(content);
        IsoTypeReader.readUInt32(content);    // reserved!

    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt16(byteBuffer, maxPduSize);
        IsoTypeWriter.writeUInt16(byteBuffer, avgPduSize);
        IsoTypeWriter.writeUInt32(byteBuffer, maxBitrate);
        IsoTypeWriter.writeUInt32(byteBuffer, avgBitrate);
        IsoTypeWriter.writeUInt32(byteBuffer, 0);
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "HintMediaHeaderBox{" +
                "maxPduSize=" + maxPduSize +
                ", avgPduSize=" + avgPduSize +
                ", maxBitrate=" + maxBitrate +
                ", avgBitrate=" + avgBitrate +
                '}';
    }
}
