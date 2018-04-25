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

package com.htc.lib1.htcmp4parser.googlecode.mp4parser;


import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.FullBox;

import java.nio.ByteBuffer;

/**
 * Base class for all ISO Full boxes.
 * @hide
 */
public abstract class AbstractFullBox extends AbstractBox implements FullBox {
    private int version;
    private int flags;

    /**
     * @hide
     */
    protected AbstractFullBox(String type) {
        super(type);
    }

    /**
     * @hide
     */
    protected AbstractFullBox(String type, byte[] userType) {
        super(type, userType);
    }

    /**
     * @hide
     */
    public int getVersion() {
        return version;
    }

    /**
     * @hide
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @hide
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @hide
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }


    /**
     * Parses the version/flags header and returns the remaining box size.
     *
     * @param content
     * @return number of bytes read
     * @hide
     */
    protected final long parseVersionAndFlags(ByteBuffer content) {
        version = IsoTypeReader.readUInt8(content);
        flags = IsoTypeReader.readUInt24(content);
        return 4;
    }
    /**
     * @hide
     */
    protected final void writeVersionAndFlags(ByteBuffer bb) {
        IsoTypeWriter.writeUInt8(bb, version);
        IsoTypeWriter.writeUInt24(bb, flags);
    }
}
