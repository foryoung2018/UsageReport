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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ContainerBox;

import java.nio.ByteBuffer;

/**
 * Contains information common to all visual tracks.
 * <code>
 * <pre>
 * class VisualSampleEntry(codingname) extends SampleEntry (codingname){
 * unsigned int(16) pre_defined = 0;
 * const unsigned int(16) reserved = 0;
 * unsigned int(32)[3] pre_defined = 0;
 * unsigned int(16) width;
 * unsigned int(16) height;
 * template unsigned int(32) horizresolution = 0x00480000; // 72 dpi
 * template unsigned int(32) vertresolution = 0x00480000; // 72 dpi
 * const unsigned int(32) reserved = 0;
 * template unsigned int(16) frame_count = 1;
 * string[32] compressorname;
 * template unsigned int(16) depth = 0x0018;
 * int(16) pre_defined = -1;
 * }<br>
 * </pre>
 * </code>
 * <p/>
 * Format-specific informationis appened as boxes after the data described in ISO/IEC 14496-12 chapter 8.16.2.
 *  @hide
 * {@exthide}
 */
public class VisualSampleEntry extends SampleEntry implements ContainerBox {
    /**
     * @hide
     */
    public static final String TYPE1 = "mp4v";
    /**
     * @hide
     */
    public static final String TYPE2 = "s263";
    /**
     * @hide
     */
    public static final String TYPE3 = "avc1";


    /**
     * Identifier for an encrypted video track.
     *
     * @see com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ProtectionSchemeInformationBox
     * @hide
     */
    public static final String TYPE_ENCRYPTED = "encv";


    private int width;
    private int height;
    private double horizresolution;
    private double vertresolution;
    private int frameCount;
    private String compressorname;
    private int depth;

    private long[] predefined = new long[3];
    /**
     * @hide
     */
    public VisualSampleEntry(String type) {
        super(type);
    }
    /**
     * @hide
     */
    public int getWidth() {
        return width;
    }
    /**
     * @hide
     */
    public int getHeight() {
        return height;
    }
    /**
     * @hide
     */
    public double getHorizresolution() {
        return horizresolution;
    }
    /**
     * @hide
     */
    public double getVertresolution() {
        return vertresolution;
    }
    /**
     * @hide
     */
    public int getFrameCount() {
        return frameCount;
    }
    /**
     * @hide
     */
    public String getCompressorname() {
        return compressorname;
    }
    /**
     * @hide
     */
    public int getDepth() {
        return depth;
    }
    /**
     * @hide
     */
    public void setCompressorname(String compressorname) {
        this.compressorname = compressorname;
    }
    /**
     * @hide
     */
    public void setWidth(int width) {
        this.width = width;
    }
    /**
     * @hide
     */
    public void setHeight(int height) {
        this.height = height;
    }
    /**
     * @hide
     */
    public void setHorizresolution(double horizresolution) {
        this.horizresolution = horizresolution;
    }
    /**
     * @hide
     */
    public void setVertresolution(double vertresolution) {
        this.vertresolution = vertresolution;
    }
    /**
     * @hide
     */
    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }
    /**
     * @hide
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        _parseReservedAndDataReferenceIndex(content);
        long tmp = IsoTypeReader.readUInt16(content);
        assert 0 == tmp : "reserved byte not 0";
        tmp = IsoTypeReader.readUInt16(content);
        assert 0 == tmp : "reserved byte not 0";
        predefined[0] = IsoTypeReader.readUInt32(content);     // should be zero
        predefined[1] = IsoTypeReader.readUInt32(content);     // should be zero
        predefined[2] = IsoTypeReader.readUInt32(content);     // should be zero
        width = IsoTypeReader.readUInt16(content);
        height = IsoTypeReader.readUInt16(content);
        horizresolution = IsoTypeReader.readFixedPoint1616(content);
        vertresolution = IsoTypeReader.readFixedPoint1616(content);
        tmp = IsoTypeReader.readUInt32(content);
        assert 0 == tmp : "reserved byte not 0";
        frameCount = IsoTypeReader.readUInt16(content);
        int compressornameDisplayAbleData = IsoTypeReader.readUInt8(content);
        if (compressornameDisplayAbleData > 31) {
            System.out.println("invalid compressor name displayable data: " + compressornameDisplayAbleData);
            compressornameDisplayAbleData = 31;
        }
        byte[] bytes = new byte[compressornameDisplayAbleData];
        content.get(bytes);
        compressorname = Utf8.convert(bytes);
        if (compressornameDisplayAbleData < 31) {
            byte[] zeros = new byte[31 - compressornameDisplayAbleData];
            content.get(zeros);
            //assert Arrays.equals(zeros, new byte[zeros.length]) : "The compressor name length was not filled up with zeros";
        }
        depth = IsoTypeReader.readUInt16(content);
        tmp = IsoTypeReader.readUInt16(content);
        assert 0xFFFF == tmp;

        _parseChildBoxes(content);

    }

    /**
     * @hide
     */
    protected long getContentSize() {
        long contentSize = 78;
        for (Box boxe : boxes) {
            contentSize += boxe.getSize();
        }
        return contentSize;
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        _writeReservedAndDataReferenceIndex(byteBuffer);
        IsoTypeWriter.writeUInt16(byteBuffer, 0);
        IsoTypeWriter.writeUInt16(byteBuffer, 0);
        IsoTypeWriter.writeUInt32(byteBuffer, predefined[0]);
        IsoTypeWriter.writeUInt32(byteBuffer, predefined[1]);
        IsoTypeWriter.writeUInt32(byteBuffer, predefined[2]);

        IsoTypeWriter.writeUInt16(byteBuffer, getWidth());
        IsoTypeWriter.writeUInt16(byteBuffer, getHeight());

        IsoTypeWriter.writeFixedPont1616(byteBuffer, getHorizresolution());
        IsoTypeWriter.writeFixedPont1616(byteBuffer, getVertresolution());


        IsoTypeWriter.writeUInt32(byteBuffer, 0);
        IsoTypeWriter.writeUInt16(byteBuffer, getFrameCount());
        IsoTypeWriter.writeUInt8(byteBuffer, Utf8.utf8StringLengthInBytes(getCompressorname()));
        byteBuffer.put(Utf8.convert(getCompressorname()));
        int a = Utf8.utf8StringLengthInBytes(getCompressorname());
        while (a < 31) {
            a++;
            byteBuffer.put((byte) 0);
        }
        IsoTypeWriter.writeUInt16(byteBuffer, getDepth());
        IsoTypeWriter.writeUInt16(byteBuffer, 0xFFFF);

        _writeChildBoxes(byteBuffer);

    }

}
