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
 * aligned(8) class MovieExtendsHeaderBox extends FullBox('mehd', version, 0) {
 * if (version==1) {
 * unsigned int(64) fragment_duration;
 * } else { // version==0
 * unsigned int(32) fragment_duration;
 * }
 * }
 *  @hide
 * {@exthide}
 */
public class MovieExtendsHeaderBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "mehd";
    private long fragmentDuration;
    /**
     * @hide
     */
    public MovieExtendsHeaderBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        return getVersion() == 1 ? 12 : 8;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        fragmentDuration = getVersion() == 1 ? IsoTypeReader.readUInt64(content) : IsoTypeReader.readUInt32(content);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        if (getVersion() == 1) {
            IsoTypeWriter.writeUInt64(byteBuffer, fragmentDuration);
        } else {
            IsoTypeWriter.writeUInt32(byteBuffer, fragmentDuration);
        }
    }
    /**
     * @hide
     */
    public long getFragmentDuration() {
        return fragmentDuration;
    }
    /**
     * @hide
     */
    public void setFragmentDuration(long fragmentDuration) {
        this.fragmentDuration = fragmentDuration;
    }
}
