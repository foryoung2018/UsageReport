/*
 * Copyright 2009 castLabs GmbH, Berlin
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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.fragment;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * aligned(8) class MovieFragmentRandomAccessOffsetBox
 * extends FullBox('mfro', version, 0) {
 * unsigned int(32) size;
 * }
 *  @hide
 * {@exthide}
 */
public class MovieFragmentRandomAccessOffsetBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "mfro";
    private long mfraSize;
    /**
     * @hide
     */
    public MovieFragmentRandomAccessOffsetBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    protected long getContentSize() {
        return 8;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        mfraSize = IsoTypeReader.readUInt32(content);
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, mfraSize);
    }
    /**
     * @hide
     */
    public long getMfraSize() {
        return mfraSize;
    }
    /**
     * @hide
     */
    public void setMfraSize(long mfraSize) {
        this.mfraSize = mfraSize;
    }
}
