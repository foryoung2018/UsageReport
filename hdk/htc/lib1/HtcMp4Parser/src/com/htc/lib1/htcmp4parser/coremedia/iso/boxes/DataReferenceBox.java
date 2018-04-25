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


import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.FullContainerBox;

import java.nio.ByteBuffer;

/**
 * The data reference object contains a table of data references (normally URLs) that declare the location(s) of
 * the media data used within the presentation. The data reference index in the sample description ties entries in
 * this table to the samples in the track. A track may be split over several sources in this way.
 * If the flag is set indicating that the data is in the same file as this box, then no string (not even an empty one)
 * shall be supplied in the entry field.
 * The DataEntryBox within the DataReferenceBox shall be either a DataEntryUrnBox or a DataEntryUrlBox.
 *
 * @see com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataEntryUrlBox
 * @see com.htc.lib1.htcmp4parser.coremedia.iso.boxes.DataEntryUrnBox
 * @hide
 * {@exthide}
 */
public class DataReferenceBox extends FullContainerBox {

	/**
	 *  @hide
	 */
    public static final String TYPE = "dref";

    /**
     *  @hide
     */
    public DataReferenceBox() {
        super(TYPE);

    }

    /**
     *  @hide
     */
    @Override
    protected long getContentSize() {
        return super.getContentSize() + 4;
    }

    /**
     *  @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        content.get(new byte[4]); // basically a skip of 4 bytes signaling the number of child boxes
        parseChildBoxes(content);
    }

    /**
     *  @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, getBoxes().size());
        writeChildBoxes(byteBuffer);
    }

}
