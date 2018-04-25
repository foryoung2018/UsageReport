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

/**
 * @hide
 * {@exthide}
 */
public class RecordingYearBox extends AbstractFullBox {
	/**
	 * @hide
	 */
	public static final String TYPE = "yrrc";

	/**
	 * @hide
	 */
    int recordingYear;

    /**
     * @hide
     */
    public RecordingYearBox() {
        super(TYPE);
    }


    /**
     * @hide
     */
    protected long getContentSize() {
        return 6;
    }

    /**
     * @hide
     */
    public int getRecordingYear() {
        return recordingYear;
    }

    /**
     * @hide
     */
    public void setRecordingYear(int recordingYear) {
        this.recordingYear = recordingYear;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        recordingYear = IsoTypeReader.readUInt16(content);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt16(byteBuffer, recordingYear);
    }

}
