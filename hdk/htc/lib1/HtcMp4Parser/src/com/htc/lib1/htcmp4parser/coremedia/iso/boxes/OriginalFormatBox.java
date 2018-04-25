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

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;

import java.nio.ByteBuffer;

/**
 * The Original Format Box contains the four-character-code of the original untransformed sample description.
 * See ISO/IEC 14496-12 for details.
 *
 * @see ProtectionSchemeInformationBox
 * @hide
 * {@exthide}
 */

public class OriginalFormatBox extends AbstractBox {
	/**
	 * @hide
	 */
	public static final String TYPE = "frma";

    private String dataFormat = "    ";

    /**
     * @hide
     */
    public OriginalFormatBox() {
        super("frma");
    }

    /**
     * @hide
     */
    public String getDataFormat() {
        return dataFormat;
    }

    /**
     * @hide
     */
    public void setDataFormat(String dataFormat) {
        assert dataFormat.length() == 4;
        this.dataFormat = dataFormat;
    }

    /**
     * @hide
     */
    protected long getContentSize() {
        return 4;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        dataFormat = IsoTypeReader.read4cc(content);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(IsoFile.fourCCtoBytes(dataFormat));
    }

    /**
     * @hide
     */
    public String toString() {
        return "OriginalFormatBox[dataFormat=" + getDataFormat() + "]";
    }
}
