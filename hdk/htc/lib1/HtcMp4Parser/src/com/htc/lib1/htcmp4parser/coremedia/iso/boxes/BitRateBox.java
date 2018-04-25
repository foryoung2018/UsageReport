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
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;

import java.nio.ByteBuffer;

/**
 * <code>class BitRateBox extends Box('btrt') {<br/>
 * unsigned int(32) bufferSizeDB;<br/>
 * // gives the size of the decoding buffer for<br/>
 * // the elementary stream in bytes.<br/>
 * unsigned int(32) maxBitrate;<br/>
 * // gives the maximum rate in bits/second <br/>
 * // over any window of one second.<br/>
 * unsigned int(32) avgBitrate;<br/>
 * // avgBitrate gives the average rate in <br/>
 * // bits/second over the entire presentation.<br/>
 * }</code>
 *  @hide
 * {@exthide}
 */

public final class BitRateBox extends AbstractBox {
	/**
	 *  @hide
	 */
	public static final String TYPE = "btrt";

    private long bufferSizeDb;
    private long maxBitrate;
    private long avgBitrate;

    /**
     *  @hide
     */
    public BitRateBox() {
        super(TYPE);
    }

    /**
     *  @hide
     */
    protected long getContentSize() {
        return 12;
    }

    /**
     *  @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        bufferSizeDb = IsoTypeReader.readUInt32(content);
        maxBitrate = IsoTypeReader.readUInt32(content);
        avgBitrate = IsoTypeReader.readUInt32(content);
    }

    /**
     *  @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt32(byteBuffer, bufferSizeDb);
        IsoTypeWriter.writeUInt32(byteBuffer, maxBitrate);
        IsoTypeWriter.writeUInt32(byteBuffer, avgBitrate);
    }

    /**
     *  @hide
     */
    public long getBufferSizeDb() {
        return bufferSizeDb;
    }

    /**
     *  @hide
     */
    public void setBufferSizeDb(long bufferSizeDb) {
        this.bufferSizeDb = bufferSizeDb;
    }

    /**
     *  @hide
     */
    public long getMaxBitrate() {
        return maxBitrate;
    }

    /**
     *  @hide
     */
    public void setMaxBitrate(long maxBitrate) {
        this.maxBitrate = maxBitrate;
    }

    /**
     *  @hide
     */
    public long getAvgBitrate() {
        return avgBitrate;
    }

    /**
     *  @hide
     */
    public void setAvgBitrate(long avgBitrate) {
        this.avgBitrate = avgBitrate;
    }
}
