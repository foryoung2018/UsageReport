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
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.FullContainerBox;

import java.nio.ByteBuffer;

/**
 * The Item Protection Box provides an array of item protection information, for use by the Item Information Box.
 *
 * @see com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ItemProtectionBox
 * @hide
 * {@exthide}
 */
public class ItemProtectionBox extends FullContainerBox {

	/**
	 * @hide
	 */
    public static final String TYPE = "ipro";

    /**
     * @hide
     */
    public ItemProtectionBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    public SchemeInformationBox getItemProtectionScheme() {
        if (!getBoxes(SchemeInformationBox.class).isEmpty()) {
            return getBoxes(SchemeInformationBox.class).get(0);
        } else {
            return null;
        }
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        IsoTypeReader.readUInt16(content);
        parseChildBoxes(content);
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt16(byteBuffer, getBoxes().size());
        writeChildBoxes(byteBuffer);
    }

}
