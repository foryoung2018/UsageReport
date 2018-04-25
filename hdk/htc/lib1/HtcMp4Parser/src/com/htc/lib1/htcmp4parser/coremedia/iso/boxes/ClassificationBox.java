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
 * Classification of the media according to 3GPP 26.244.
 *  @hide
 * {@exthide}
 */
public class ClassificationBox extends AbstractFullBox {
	/**
	 *  @hide
	 */
	public static final String TYPE = "clsf";


    private String classificationEntity;
    private int classificationTableIndex;
    private String language;
    private String classificationInfo;

    /**
     *  @hide
     */
    public ClassificationBox() {
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
    public String getClassificationEntity() {
        return classificationEntity;
    }

    /**
     *  @hide
     */
    public int getClassificationTableIndex() {
        return classificationTableIndex;
    }

    /**
     *  @hide
     */
    public String getClassificationInfo() {
        return classificationInfo;
    }

    /**
     *  @hide
     */
    public void setClassificationEntity(String classificationEntity) {
        this.classificationEntity = classificationEntity;
    }

    /**
     *  @hide
     */
    public void setClassificationTableIndex(int classificationTableIndex) {
        this.classificationTableIndex = classificationTableIndex;
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
    public void setClassificationInfo(String classificationInfo) {
        this.classificationInfo = classificationInfo;
    }

    /**
     *  @hide
     */
    protected long getContentSize() {
        return 4 + 2 + 2 + Utf8.utf8StringLengthInBytes(classificationInfo) + 1;
    }

    /**
     *  @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        byte[] cE = new byte[4];
        content.get(cE);
        classificationEntity = IsoFile.bytesToFourCC(cE);
        classificationTableIndex = IsoTypeReader.readUInt16(content);
        language = IsoTypeReader.readIso639(content);
        classificationInfo = IsoTypeReader.readString(content);
    }

    /**
     *  @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(IsoFile.fourCCtoBytes(classificationEntity));
        IsoTypeWriter.writeUInt16(byteBuffer, classificationTableIndex);
        IsoTypeWriter.writeIso639(byteBuffer, language);
        byteBuffer.put(Utf8.convert(classificationInfo));
        byteBuffer.put((byte) 0);
    }

    /**
     *  @hide
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("ClassificationBox[language=").append(getLanguage());
        buffer.append("classificationEntity=").append(getClassificationEntity());
        buffer.append(";classificationTableIndex=").append(getClassificationTableIndex());
        buffer.append(";language=").append(getLanguage());
        buffer.append(";classificationInfo=").append(getClassificationInfo());
        buffer.append("]");
        return buffer.toString();
    }
}
