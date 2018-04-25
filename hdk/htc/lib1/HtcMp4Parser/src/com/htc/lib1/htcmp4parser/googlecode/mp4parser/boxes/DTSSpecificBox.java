package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: magnus
 * Date: 2012-03-09
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 * @hide
 */
public class DTSSpecificBox extends AbstractBox {
	/**
	 * @hide
	 */
    long DTSSamplingFrequency;
    /**
     * @hide
     */
    long maxBitRate;
    /**
     * @hide
     */
    long avgBitRate;
    /**
     * @hide
     */
    int pcmSampleDepth;
    /**
     * @hide
     */
    int frameDuration;
    /**
     * @hide
     */
    int streamConstruction;
    /**
     * @hide
     */
    int coreLFEPresent;
    /**
     * @hide
     */
    int coreLayout;
    /**
     * @hide
     */
    int coreSize;
    /**
     * @hide
     */
    int stereoDownmix;
    /**
     * @hide
     */
    int representationType;
    /**
     * @hide
     */
    int channelLayout;
    /**
     * @hide
     */
    int multiAssetFlag;
    /**
     * @hide
     */
    int LBRDurationMod;
    /**
     * @hide
     */
    int reservedBoxPresent;
    /**
     * @hide
     */
    int reserved;
    /**
     * @hide
     */
    public DTSSpecificBox() {
        super("ddts");
    }
    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return 20;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        DTSSamplingFrequency = IsoTypeReader.readUInt32(content);
        maxBitRate = IsoTypeReader.readUInt32(content);
        avgBitRate = IsoTypeReader.readUInt32(content);
        pcmSampleDepth = IsoTypeReader.readUInt8(content);
        BitReaderBuffer brb = new BitReaderBuffer(content);
        frameDuration = brb.readBits(2);
        streamConstruction = brb.readBits(5);
        coreLFEPresent = brb.readBits(1);
        coreLayout = brb.readBits(6);
        coreSize = brb.readBits(14);
        stereoDownmix = brb.readBits(1);
        representationType = brb.readBits(3);
        channelLayout = brb.readBits(16);
        multiAssetFlag = brb.readBits(1);
        LBRDurationMod = brb.readBits(1);
        reservedBoxPresent = brb.readBits(1);
        reserved = brb.readBits(5);

    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt32(byteBuffer, DTSSamplingFrequency);
        IsoTypeWriter.writeUInt32(byteBuffer, maxBitRate);
        IsoTypeWriter.writeUInt32(byteBuffer, avgBitRate);
        IsoTypeWriter.writeUInt8(byteBuffer, pcmSampleDepth);
        BitWriterBuffer bwb = new BitWriterBuffer(byteBuffer);
        bwb.writeBits(frameDuration, 2);
        bwb.writeBits(streamConstruction, 5);
        bwb.writeBits(coreLFEPresent, 1);
        bwb.writeBits(coreLayout, 6);
        bwb.writeBits(coreSize, 14);
        bwb.writeBits(stereoDownmix, 1);
        bwb.writeBits(representationType, 3);
        bwb.writeBits(channelLayout, 16);
        bwb.writeBits(multiAssetFlag, 1);
        bwb.writeBits(LBRDurationMod, 1);
        bwb.writeBits(reservedBoxPresent, 1);
        bwb.writeBits(reserved, 5);

    }
    /**
     * @hide
     */
    public long getAvgBitRate() {
        return avgBitRate;
    }
    /**
     * @hide
     */
    public void setAvgBitRate(long avgBitRate) {
        this.avgBitRate = avgBitRate;
    }
    /**
     * @hide
     */
    public long getDTSSamplingFrequency() {
        return DTSSamplingFrequency;
    }
    /**
     * @hide
     */
    public void setDTSSamplingFrequency(long DTSSamplingFrequency) {
        this.DTSSamplingFrequency = DTSSamplingFrequency;
    }
    /**
     * @hide
     */
    public long getMaxBitRate() {
        return maxBitRate;
    }
    /**
     * @hide
     */
    public void setMaxBitRate(long maxBitRate) {
        this.maxBitRate = maxBitRate;
    }
    /**
     * @hide
     */
    public int getPcmSampleDepth() {
        return pcmSampleDepth;
    }
    /**
     * @hide
     */
    public void setPcmSampleDepth(int pcmSampleDepth) {
        this.pcmSampleDepth = pcmSampleDepth;
    }
    /**
     * @hide
     */
    public int getFrameDuration() {
        return frameDuration;
    }
    /**
     * @hide
     */
    public void setFrameDuration(int frameDuration) {
        this.frameDuration = frameDuration;
    }
    /**
     * @hide
     */
    public int getStreamConstruction() {
        return streamConstruction;
    }
    /**
     * @hide
     */
    public void setStreamConstruction(int streamConstruction) {
        this.streamConstruction = streamConstruction;
    }
    /**
     * @hide
     */
    public int getCoreLFEPresent() {
        return coreLFEPresent;
    }
    /**
     * @hide
     */
    public void setCoreLFEPresent(int coreLFEPresent) {
        this.coreLFEPresent = coreLFEPresent;
    }
    /**
     * @hide
     */
    public int getCoreLayout() {
        return coreLayout;
    }
    /**
     * @hide
     */
    public void setCoreLayout(int coreLayout) {
        this.coreLayout = coreLayout;
    }
    /**
     * @hide
     */
    public int getCoreSize() {
        return coreSize;
    }
    /**
     * @hide
     */
    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }
    /**
     * @hide
     */
    public int getStereoDownmix() {
        return stereoDownmix;
    }
    /**
     * @hide
     */
    public void setStereoDownmix(int stereoDownmix) {
        this.stereoDownmix = stereoDownmix;
    }
    /**
     * @hide
     */
    public int getRepresentationType() {
        return representationType;
    }
    /**
     * @hide
     */
    public void setRepresentationType(int representationType) {
        this.representationType = representationType;
    }
    /**
     * @hide
     */
    public int getChannelLayout() {
        return channelLayout;
    }
    /**
     * @hide
     */
    public void setChannelLayout(int channelLayout) {
        this.channelLayout = channelLayout;
    }
    /**
     * @hide
     */
    public int getMultiAssetFlag() {
        return multiAssetFlag;
    }
    /**
     * @hide
     */
    public void setMultiAssetFlag(int multiAssetFlag) {
        this.multiAssetFlag = multiAssetFlag;
    }
    /**
     * @hide
     */
    public int getLBRDurationMod() {
        return LBRDurationMod;
    }
    /**
     * @hide
     */
    public void setLBRDurationMod(int LBRDurationMod) {
        this.LBRDurationMod = LBRDurationMod;
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
    public int getReservedBoxPresent() {
        return reservedBoxPresent;
    }
    /**
     * @hide
     */
    public void setReservedBoxPresent(int reservedBoxPresent) {
        this.reservedBoxPresent = reservedBoxPresent;
    }
}
