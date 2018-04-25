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
import com.htc.lib1.htcmp4parser.coremedia.iso.Utf8;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * Gives a language dependent description of the media contained in the ISO file.
 * @hide
 * {@exthide}
 */
public class DescriptionBox extends AbstractFullBox {
	/**
	 *  @hide
	 */
	public static final String TYPE = "dscp";

    private String language;
    private String description;
    
    /**
     *  @hide
     */
    public DescriptionBox() {
        super(TYPE);
    }

    /**
     *  @hide
     */
    public String getLanguage() {
        return language;
    }

    /**
     *  @hide
     */
    public String getDescription() {
        return description;
    }

    /**
     *  @hide
     */
    protected long getContentSize() {
        return 7 + Utf8.utf8StringLengthInBytes(description);
    }

    /**
     *  @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        language = IsoTypeReader.readIso639(content);
        description = IsoTypeReader.readString(content);
    }

    /**
     *  @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeIso639(byteBuffer, language);
        byteBuffer.put(Utf8.convert(description));
        byteBuffer.put((byte) 0);
    }

    /**
     *  @hide
     */
    public String toString() {
        return "DescriptionBox[language=" + getLanguage() + ";description=" + getDescription() + "]";
    }

    /**
     *  @hide
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *  @hide
     */
    public void setDescription(String description) {
        this.description = description;
    }
}