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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.vodafone;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * A vodafone specific box.
 *  @hide
 * {@exthide}
 */
public class CoverUriBox extends AbstractFullBox {
    /**
     * @hide
     */
    public static final String TYPE = "cvru";

    private String coverUri;
    /**
     * @hide
     */
    public CoverUriBox() {
        super(TYPE);
    }
    /**
     * @hide
     */
    public String getCoverUri() {
        return coverUri;
    }
    /**
     * @hide
     */
    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }
    /**
     * @hide
     */
    protected long getContentSize() {
        return Utf8.utf8StringLengthInBytes(coverUri) + 5;
    }
    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        coverUri = IsoTypeReader.readString(content);
    }
    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(Utf8.convert(coverUri));
        byteBuffer.put((byte) 0);
    }

    /**
     * @hide
     */
    public String toString() {
        return "CoverUriBox[coverUri=" + getCoverUri() + "]";
    }
}
