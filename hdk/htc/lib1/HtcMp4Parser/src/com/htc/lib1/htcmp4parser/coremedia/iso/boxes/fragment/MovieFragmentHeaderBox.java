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
 * aligned(8) class MovieFragmentHeaderBox
 * extends FullBox('mfhd', 0, 0){
 * unsigned int(32) sequence_number;
 * }
 *  @hide
 * {@exthide}
 */

public class MovieFragmentHeaderBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "mfhd";
    private long sequenceNumber;
    /**
     * @hide
     */
    public MovieFragmentHeaderBox() {
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
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, sequenceNumber);
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        sequenceNumber = IsoTypeReader.readUInt32(content);

    }
    /**
     * @hide
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }
    /**
     * @hide
     */
    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    /**
     * @hide
     */
    @Override
    public String toString() {
        return "MovieFragmentHeaderBox{" +
                "sequenceNumber=" + sequenceNumber +
                '}';
    }
}
