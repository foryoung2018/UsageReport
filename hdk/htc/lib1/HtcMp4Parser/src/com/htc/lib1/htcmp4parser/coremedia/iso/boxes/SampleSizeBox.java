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

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * This box containes the sample count and a table giving the size in bytes of each sample.
 * Defined in ISO/IEC 14496-12.
 * @hide
 * {@exthide}
 */
public class SampleSizeBox extends AbstractFullBox {
    private long sampleSize;
    private long[] sampleSizes = new long[0];
    /**
     * @hide
     */
    public static final String TYPE = "stsz";
    /**
     * @hide
     */
    int sampleCount;

    /**
     * @hide
     */
    public SampleSizeBox() {
        super(TYPE);
    }

    /**
     * Returns the field sample size.
     * If sampleSize > 0 every sample has the same size.
     * If sampleSize == 0 the samples have different size as stated in the sampleSizes field.
     *
     * @return the sampleSize field
     * @hide
     */
    public long getSampleSize() {
        return sampleSize;
    }

    /**
     * @hide
     */
    public void setSampleSize(long sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * @hide
     */
    public long getSampleSizeAtIndex(int index) {
        if (sampleSize > 0) {
            return sampleSize;
        } else {
            return sampleSizes[index];
        }
    }

    /**
     * @hide
     */
    public long getSampleCount() {
        if (sampleSize > 0) {
            return sampleCount;
        } else {
            return sampleSizes.length;
        }

    }

    /**
     * @hide
     */
    public long[] getSampleSizes() {
        return sampleSizes;
    }

    /**
     * @hide
     */
    public void setSampleSizes(long[] sampleSizes) {
        this.sampleSizes = sampleSizes;
    }

    /**
     * @hide
     */
    protected long getContentSize() {
        return 12 + (sampleSize == 0 ? sampleSizes.length * 4 : 0);
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        sampleSize = IsoTypeReader.readUInt32(content);
        sampleCount = l2i(IsoTypeReader.readUInt32(content));

        if (sampleSize == 0) {
            sampleSizes = new long[(int) sampleCount];

            for (int i = 0; i < sampleCount; i++) {
                sampleSizes[i] = IsoTypeReader.readUInt32(content);
            }
        }
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, sampleSize);

        if (sampleSize == 0) {
            IsoTypeWriter.writeUInt32(byteBuffer, sampleSizes.length);
            for (long sampleSize1 : sampleSizes) {
                IsoTypeWriter.writeUInt32(byteBuffer, sampleSize1);
            }
        } else {
            IsoTypeWriter.writeUInt32(byteBuffer, sampleCount);
        }

    }

    /**
     * @hide
     */
    public String toString() {
        return "SampleSizeBox[sampleSize=" + getSampleSize() + ";sampleCount=" + getSampleCount() + "]";
    }
}
