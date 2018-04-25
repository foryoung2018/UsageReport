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

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.nio.ByteBuffer;

/**
 * Only used within the DataReferenceBox. Find more information there.
 *
 * @see com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataReferenceBox
 * @hide
 * {@exthide}
 */
public class DataEntryUrlBox extends AbstractFullBox {
	/**
	 *  @hide
	 */
	public static final String TYPE = "url ";

	/**
	 *  @hide
	 */
    public DataEntryUrlBox() {
        super(TYPE);
    }

    /**
     *  @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
    }

    /**
     *  @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
    }

    /**
     *  @hide
     */
    protected long getContentSize() {
        return 4;
    }

    /**
     *  @hide
     */
    public String toString() {
        return "DataEntryUrlBox[]";
    }
}
