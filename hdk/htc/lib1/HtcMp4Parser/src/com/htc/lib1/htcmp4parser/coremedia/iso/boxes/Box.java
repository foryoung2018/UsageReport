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

import com.htc.lib1.htcmp4parser.coremedia.iso.BoxParser;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ContainerBox;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Defines basic interaction possibilities for any ISO box. Each box has a parent box and a type.
 *  @hide
 * {@exthide}
 */
public interface Box {
	/**
	 *  @hide
	 */
	ContainerBox getParent();

	/**
	 *  @hide
	 */
    void setParent(ContainerBox parent);

    /**
     *  @hide
     */
    long getSize();

    /**
     * The box's 4-cc type.
     * @return the 4 character type of the box
     * @hide
     */
    String getType();

    /**
     * Writes the complete box - size | 4-cc | content - to the given <code>writableByteChannel</code>.
     * @param writableByteChannel the box's sink
     * @throws IOException in case of problems with the <code>Channel</code>
     * @hide
     */
    void getBox(WritableByteChannel writableByteChannel) throws IOException;

    /**
     *  @hide
     */
    void parse(ReadableByteChannel readableByteChannel, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException;
}
