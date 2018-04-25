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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.h264;

import com.htc.lib1.htcmp4parser.coremedia.iso.Hex;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.h264.model.PictureParameterSet;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.h264.model.SeqParameterSet;
import com.htc.lib1.htcmp4parser.utils.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defined in ISO/IEC 14496-15:2004.
 *  @hide
 * {@exthide}
 */
public final class AvcConfigurationBox extends AbstractBox {
    /**
     * @hide
     */
    public static final String TYPE = "avcC";
    /**
     * @hide
     */
    public AVCDecoderConfigurationRecord avcDecoderConfigurationRecord = new AVCDecoderConfigurationRecord();

    /**
     * @hide
     */
    public AvcConfigurationBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    public int getConfigurationVersion() {
        return avcDecoderConfigurationRecord.configurationVersion;
    }
    /**
     * @hide
     */
    public int getAvcProfileIndication() {
        return avcDecoderConfigurationRecord.avcProfileIndication;
    }
    /**
     * @hide
     */
    public int getProfileCompatibility() {
        return avcDecoderConfigurationRecord.profileCompatibility;
    }
    /**
     * @hide
     */
    public int getAvcLevelIndication() {
        return avcDecoderConfigurationRecord.avcLevelIndication;
    }
    /**
     * @hide
     */
    public int getLengthSizeMinusOne() {
        return avcDecoderConfigurationRecord.lengthSizeMinusOne;
    }
    /**
     * @hide
     */
    public List<byte[]> getSequenceParameterSets() {
        return Collections.unmodifiableList(avcDecoderConfigurationRecord.sequenceParameterSets);
    }
    /**
     * @hide
     */
    public List<byte[]> getPictureParameterSets() {
        return Collections.unmodifiableList(avcDecoderConfigurationRecord.pictureParameterSets);
    }
    /**
     * @hide
     */
    public void setConfigurationVersion(int configurationVersion) {
        this.avcDecoderConfigurationRecord.configurationVersion = configurationVersion;
    }
    /**
     * @hide
     */
    public void setAvcProfileIndication(int avcProfileIndication) {
        this.avcDecoderConfigurationRecord.avcProfileIndication = avcProfileIndication;
    }
    /**
     * @hide
     */
    public void setProfileCompatibility(int profileCompatibility) {
        this.avcDecoderConfigurationRecord.profileCompatibility = profileCompatibility;
    }
    /**
     * @hide
     */
    public void setAvcLevelIndication(int avcLevelIndication) {
        this.avcDecoderConfigurationRecord.avcLevelIndication = avcLevelIndication;
    }
    /**
     * @hide
     */
    public void setLengthSizeMinusOne(int lengthSizeMinusOne) {
        this.avcDecoderConfigurationRecord.lengthSizeMinusOne = lengthSizeMinusOne;
    }
    /**
     * @hide
     */
    public void setSequenceParameterSets(List<byte[]> sequenceParameterSets) {
        this.avcDecoderConfigurationRecord.sequenceParameterSets = sequenceParameterSets;
    }
    /**
     * @hide
     */
    public void setPictureParameterSets(List<byte[]> pictureParameterSets) {
        this.avcDecoderConfigurationRecord.pictureParameterSets = pictureParameterSets;
    }
    /**
     * @hide
     */
    public int getChromaFormat() {
        return avcDecoderConfigurationRecord.chromaFormat;
    }
    /**
     * @hide
     */
    public void setChromaFormat(int chromaFormat) {
        this.avcDecoderConfigurationRecord.chromaFormat = chromaFormat;
    }
    /**
     * @hide
     */
    public int getBitDepthLumaMinus8() {
        return avcDecoderConfigurationRecord.bitDepthLumaMinus8;
    }
    /**
     * @hide
     */
    public void setBitDepthLumaMinus8(int bitDepthLumaMinus8) {
        this.avcDecoderConfigurationRecord.bitDepthLumaMinus8 = bitDepthLumaMinus8;
    }
    /**
     * @hide
     */
    public int getBitDepthChromaMinus8() {
        return avcDecoderConfigurationRecord.bitDepthChromaMinus8;
    }
    /**
     * @hide
     */
    public void setBitDepthChromaMinus8(int bitDepthChromaMinus8) {
        this.avcDecoderConfigurationRecord.bitDepthChromaMinus8 = bitDepthChromaMinus8;
    }
    /**
     * @hide
     */
    public List<byte[]> getSequenceParameterSetExts() {
        return avcDecoderConfigurationRecord.sequenceParameterSetExts;
    }
    /**
     * @hide
     */
    public void setSequenceParameterSetExts(List<byte[]> sequenceParameterSetExts) {
        this.avcDecoderConfigurationRecord.sequenceParameterSetExts = sequenceParameterSetExts;
    }
    /**
     * @hide
     */
    public boolean hasExts() {
        return avcDecoderConfigurationRecord.hasExts;
    }
    /**
     * @hide
     */
    public void setHasExts(boolean hasExts) {
        this.avcDecoderConfigurationRecord.hasExts = hasExts;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        avcDecoderConfigurationRecord = new AVCDecoderConfigurationRecord(content);
    }

    /**
     * @hide
     */
    @Override
    public long getContentSize() {
        return avcDecoderConfigurationRecord.getContentSize();
    }

    /**
     * @hide
     */
    @Override
    public void getContent(ByteBuffer byteBuffer) {
        avcDecoderConfigurationRecord.getContent(byteBuffer);
    }

    // just to display sps in isoviewer no practical use
    /**
     * @hide
     */
    public String[] getSPS() {
        return avcDecoderConfigurationRecord.getSPS();
    }
    /**
     * @hide
     */
    public String[] getPPS() {
        return avcDecoderConfigurationRecord.getPPS();
    }
    /**
     * @hide
     */
    public List<String> getSequenceParameterSetsAsStrings() {
        return avcDecoderConfigurationRecord.getSequenceParameterSetsAsStrings();
    }
    /**
     * @hide
     */
    public List<String> getSequenceParameterSetExtsAsStrings() {
        return avcDecoderConfigurationRecord.getSequenceParameterSetExtsAsStrings();
    }
    /**
     * @hide
     */
    public List<String> getPictureParameterSetsAsStrings() {
        return avcDecoderConfigurationRecord.getPictureParameterSetsAsStrings();
    }
    /**
     * @hide
     */
    public AVCDecoderConfigurationRecord getavcDecoderConfigurationRecord() {
        return avcDecoderConfigurationRecord;
    }

    /**
     * @hide
     */
    public static class AVCDecoderConfigurationRecord {
    /**
     * @hide
     */
        public int configurationVersion;
    /**
     * @hide
     */
        public int avcProfileIndication;
    /**
     * @hide
     */
        public int profileCompatibility;
    /**
     * @hide
     */
        public int avcLevelIndication;
    /**
     * @hide
     */
        public int lengthSizeMinusOne;
    /**
     * @hide
     */
        public int lengthSizeMinusOnePaddingBits;
    /**
     * @hide
     */
        public List<byte[]> sequenceParameterSets = new ArrayList<byte[]>();
    /**
     * @hide
     */
        public List<byte[]> pictureParameterSets = new ArrayList<byte[]>();
    /**
     * @hide
     */
        public boolean hasExts = true;
    /**
     * @hide
     */
        public int chromaFormat = 1;
    /**
     * @hide
     */
        public int bitDepthLumaMinus8 = 0;
    /**
     * @hide
     */
        public int bitDepthChromaMinus8 = 0;
    /**
     * @hide
     */
        public List<byte[]> sequenceParameterSetExts = new ArrayList<byte[]>();

        /**
         * Just for non-spec-conform encoders
         * @hide
         */
        public int numberOfSequenceParameterSetsPaddingBits = 7;
    /**
     * @hide
     */
        public int chromaFormatPaddingBits = 31;
    /**
     * @hide
     */
        public int bitDepthLumaMinus8PaddingBits = 31;
    /**
     * @hide
     */
        public int bitDepthChromaMinus8PaddingBits = 31;
    /**
     * @hide
     */
        public AVCDecoderConfigurationRecord() {
        }
    /**
     * @hide
     */
        public AVCDecoderConfigurationRecord(ByteBuffer content) {
            configurationVersion = IsoTypeReader.readUInt8(content);
            avcProfileIndication = IsoTypeReader.readUInt8(content);
            profileCompatibility = IsoTypeReader.readUInt8(content);
            avcLevelIndication = IsoTypeReader.readUInt8(content);
            BitReaderBuffer brb = new BitReaderBuffer(content);
            lengthSizeMinusOnePaddingBits = brb.readBits(6);
            lengthSizeMinusOne = brb.readBits(2);
            numberOfSequenceParameterSetsPaddingBits = brb.readBits(3);
            int numberOfSeuqenceParameterSets = brb.readBits(5);
            for (int i = 0; i < numberOfSeuqenceParameterSets; i++) {
                int sequenceParameterSetLength = IsoTypeReader.readUInt16(content);

                byte[] sequenceParameterSetNALUnit = new byte[sequenceParameterSetLength];
                content.get(sequenceParameterSetNALUnit);
                sequenceParameterSets.add(sequenceParameterSetNALUnit);
            }
            long numberOfPictureParameterSets = IsoTypeReader.readUInt8(content);
            for (int i = 0; i < numberOfPictureParameterSets; i++) {
                int pictureParameterSetLength = IsoTypeReader.readUInt16(content);
                byte[] pictureParameterSetNALUnit = new byte[pictureParameterSetLength];
                content.get(pictureParameterSetNALUnit);
                pictureParameterSets.add(pictureParameterSetNALUnit);
            }
            if (content.remaining() < 4) {
                hasExts = false;
            }
            if (hasExts && (avcProfileIndication == 100 || avcProfileIndication == 110 || avcProfileIndication == 122 || avcProfileIndication == 144)) {
                // actually only some bits are interesting so masking with & x would be good but not all Mp4 creating tools set the reserved bits to 1.
                // So we need to store all bits
                brb = new BitReaderBuffer(content);
                chromaFormatPaddingBits = brb.readBits(6);
                chromaFormat = brb.readBits(2);
                bitDepthLumaMinus8PaddingBits = brb.readBits(5);
                bitDepthLumaMinus8 = brb.readBits(3);
                bitDepthChromaMinus8PaddingBits = brb.readBits(5);
                bitDepthChromaMinus8 = brb.readBits(3);
                long numOfSequenceParameterSetExt = IsoTypeReader.readUInt8(content);
                for (int i = 0; i < numOfSequenceParameterSetExt; i++) {
                    int sequenceParameterSetExtLength = IsoTypeReader.readUInt16(content);
                    byte[] sequenceParameterSetExtNALUnit = new byte[sequenceParameterSetExtLength];
                    content.get(sequenceParameterSetExtNALUnit);
                    sequenceParameterSetExts.add(sequenceParameterSetExtNALUnit);
                }
            } else {
                chromaFormat = -1;
                bitDepthLumaMinus8 = -1;
                bitDepthChromaMinus8 = -1;
            }
        }
    /**
     * @hide
     */
        public void getContent(ByteBuffer byteBuffer) {
            IsoTypeWriter.writeUInt8(byteBuffer, configurationVersion);
            IsoTypeWriter.writeUInt8(byteBuffer, avcProfileIndication);
            IsoTypeWriter.writeUInt8(byteBuffer, profileCompatibility);
            IsoTypeWriter.writeUInt8(byteBuffer, avcLevelIndication);
            BitWriterBuffer bwb = new BitWriterBuffer(byteBuffer);
            bwb.writeBits(lengthSizeMinusOnePaddingBits, 6);
            bwb.writeBits(lengthSizeMinusOne, 2);
            bwb.writeBits(numberOfSequenceParameterSetsPaddingBits, 3);
            bwb.writeBits(pictureParameterSets.size(), 5);
            for (byte[] sequenceParameterSetNALUnit : sequenceParameterSets) {
                IsoTypeWriter.writeUInt16(byteBuffer, sequenceParameterSetNALUnit.length);
                byteBuffer.put(sequenceParameterSetNALUnit);
            }
            IsoTypeWriter.writeUInt8(byteBuffer, pictureParameterSets.size());
            for (byte[] pictureParameterSetNALUnit : pictureParameterSets) {
                IsoTypeWriter.writeUInt16(byteBuffer, pictureParameterSetNALUnit.length);
                byteBuffer.put(pictureParameterSetNALUnit);
            }
            if (hasExts && (avcProfileIndication == 100 || avcProfileIndication == 110 || avcProfileIndication == 122 || avcProfileIndication == 144)) {

                bwb = new BitWriterBuffer(byteBuffer);
                bwb.writeBits(chromaFormatPaddingBits, 6);
                bwb.writeBits(chromaFormat, 2);
                bwb.writeBits(bitDepthLumaMinus8PaddingBits, 5);
                bwb.writeBits(bitDepthLumaMinus8, 3);
                bwb.writeBits(bitDepthChromaMinus8PaddingBits, 5);
                bwb.writeBits(bitDepthChromaMinus8, 3);
                for (byte[] sequenceParameterSetExtNALUnit : sequenceParameterSetExts) {
                    IsoTypeWriter.writeUInt16(byteBuffer, sequenceParameterSetExtNALUnit.length);
                    byteBuffer.put(sequenceParameterSetExtNALUnit);
                }
            }
        }
    /**
     * @hide
     */
        public long getContentSize() {
            long size = 5;
            size += 1; // sequenceParamsetLength
            for (byte[] sequenceParameterSetNALUnit : sequenceParameterSets) {
                size += 2; //lengthSizeMinusOne field
                size += sequenceParameterSetNALUnit.length;
            }
            size += 1; // pictureParamsetLength
            for (byte[] pictureParameterSetNALUnit : pictureParameterSets) {
                size += 2; //lengthSizeMinusOne field
                size += pictureParameterSetNALUnit.length;
            }
            if (hasExts && (avcProfileIndication == 100 || avcProfileIndication == 110 || avcProfileIndication == 122 || avcProfileIndication == 144)) {
                size += 4;
                for (byte[] sequenceParameterSetExtNALUnit : sequenceParameterSetExts) {
                    size += 2;
                    size += sequenceParameterSetExtNALUnit.length;
                }
            }

            return size;
        }
    /**
     * @hide
     */
        public String[] getPPS() {
            ArrayList<String> l = new ArrayList<String>();
            for (byte[] pictureParameterSet : pictureParameterSets) {
                String details = "not parsable";
                try {
                    details = PictureParameterSet.read(pictureParameterSet).toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                l.add(details);
            }
            return l.toArray(new String[l.size()]);
        }
    /**
     * @hide
     */
        public String[] getSPS() {
            ArrayList<String> l = new ArrayList<String>();
            for (byte[] sequenceParameterSet : sequenceParameterSets) {
                String detail = "not parsable";
                try {
                    detail = SeqParameterSet.read(new ByteArrayInputStream(sequenceParameterSet)).toString();
                } catch (IOException e) {
                	Log.d("getSPS IO fault");
                }
                l.add(detail);
            }
            return l.toArray(new String[l.size()]);
        }
    /**
     * @hide
     */
        public List<String> getSequenceParameterSetsAsStrings() {
            List <String> result = new ArrayList<String>(sequenceParameterSets.size());
            for (byte[] parameterSet : sequenceParameterSets) {
                result.add(Hex.encodeHex(parameterSet));
            }
            return result;
        }
    /**
     * @hide
     */
        public List<String> getSequenceParameterSetExtsAsStrings() {
            List <String> result = new ArrayList<String>(sequenceParameterSetExts.size());
            for (byte[] parameterSet : sequenceParameterSetExts) {
                result.add(Hex.encodeHex(parameterSet));
            }
            return result;
        }
    /**
     * @hide
     */
        public List<String> getPictureParameterSetsAsStrings() {
            List <String> result = new ArrayList<String>(pictureParameterSets.size());
            for (byte[] parameterSet : pictureParameterSets) {
                result.add(Hex.encodeHex(parameterSet));
            }
            return result;
        }

    }
}

