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

import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;

import java.nio.ByteBuffer;

/**
 * The contents of a free-space box are irrelevant and may be ignored, or the object deleted, without affecting the
 * presentation. Care should be excercized when deleting the object, as this may invalidate the offsets used in the
 * sample table.
 * @hide
 * {@exthide}
 */
public class FreeSpaceBox extends AbstractBox {
	/**
	 * @hide
	 */
	public static final String TYPE = "skip";

	/**
	 * @hide
	 */
    byte[] data;

    /**
     * @hide
     */
    protected long getContentSize() {
        return data.length;
    }

    /**
     * @hide
     */
    public FreeSpaceBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * @hide
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        data = new byte[content.remaining()];
        content.get(data);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(data);
    }

    /**
     * @hide
     */
    public String toString() {
        return "FreeSpaceBox[size=" + data.length + ";type=" + getType() + "]";
    }
}
