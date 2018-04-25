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
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * The Scheme Type Box identifies the protection scheme. Resides in  a Protection Scheme Information Box or
 * an SRTP Process Box.
 *
 * @see com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SchemeInformationBox
 * @hide
 * {@exthide}
 */
public class SchemeTypeBox extends AbstractFullBox {
	/**
	 * @hide
	 */
    public static final String TYPE = "schm";
    /**
     * @hide
     */
    String schemeType = "    ";
    /**
     * @hide
     */
    long schemeVersion;
    /**
     * @hide
     */
    String schemeUri = null;

    /**
     * @hide
     */
    public SchemeTypeBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public String getSchemeType() {
        return schemeType;
    }

    /**
     * @hide
     */
    public long getSchemeVersion() {
        return schemeVersion;
    }

    /**
     * @hide
     */
    public String getSchemeUri() {
        return schemeUri;
    }

    /**
     * @hide
     */
    public void setSchemeType(String schemeType) {
        assert schemeType != null && schemeType.length() == 4 : "SchemeType may not be null or not 4 bytes long";
        this.schemeType = schemeType;
    }

    /**
     * @hide
     */
    public void setSchemeVersion(int schemeVersion) {
        this.schemeVersion = schemeVersion;
    }

    /**
     * @hide
     */
    public void setSchemeUri(String schemeUri) {
        this.schemeUri = schemeUri;
    }

    /**
     * @hide
     */
    protected long getContentSize() {
        return 12 + (((getFlags() & 1) == 1) ? Utf8.utf8StringLengthInBytes(schemeUri) + 1 : 0);
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        schemeType = IsoTypeReader.read4cc(content);
        schemeVersion = IsoTypeReader.readUInt32(content);
        if ((getFlags() & 1) == 1) {
            schemeUri = IsoTypeReader.readString(content);
        }
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(IsoFile.fourCCtoBytes(schemeType));
        IsoTypeWriter.writeUInt32(byteBuffer, schemeVersion);
        if ((getFlags() & 1) == 1) {
            byteBuffer.put(Utf8.convert(schemeUri));
        }
    }

    /**
     * @hide
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Schema Type Box[");
        buffer.append("schemeUri=").append(schemeUri).append("; ");
        buffer.append("schemeType=").append(schemeType).append("; ");
        buffer.append("schemeVersion=").append(schemeUri).append("; ");
        buffer.append("]");
        return buffer.toString();
    }
}
