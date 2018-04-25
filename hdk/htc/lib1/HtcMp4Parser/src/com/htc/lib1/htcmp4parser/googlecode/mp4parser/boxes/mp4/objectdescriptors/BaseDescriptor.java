/*
 * Copyright 2011 castLabs, Berlin
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

package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;

import java.io.IOException;
import java.nio.ByteBuffer;

/*
abstract aligned(8) expandable(228-1) class BaseDescriptor : bit(8) tag=0 {
// empty. To be filled by classes extending this class.
}

int sizeOfInstance = 0;
bit(1) nextByte;
bit(7) sizeOfInstance;
while(nextByte) {
bit(1) nextByte;
bit(7) sizeByte;
sizeOfInstance = sizeOfInstance<<7 | sizeByte;
}
 */
/**
 * @hide
 */
@Descriptor(tags = 0x00)
public abstract class BaseDescriptor {
	/**
	 * @hide
	 */
    int tag;
    /**
     * @hide
     */
    int sizeOfInstance;
    /**
     * @hide
     */
    int sizeBytes;
    /**
     * @hide
     */
    public BaseDescriptor() {
    }
    /**
     * @hide
     */
    public int getTag() {
        return tag;
    }
    /**
     * @hide
     */
    public int getSize() {
        return sizeOfInstance
                + 1//1 for the tag
                + sizeBytes;
    }
    /**
     * @hide
     */
    public int getSizeOfInstance() {
        return sizeOfInstance;
    }
    /**
     * @hide
     */
    public int getSizeBytes() {
        return sizeBytes;
    }
    /**
     * @hide
     */
    public final void parse(int tag, ByteBuffer bb) throws IOException {
        this.tag = tag;

        int i = 0;
        int tmp = IsoTypeReader.readUInt8(bb);
        i++;
        sizeOfInstance = tmp & 0x7f;
        while (tmp >>> 7 == 1) { //nextbyte indicator bit
            tmp = IsoTypeReader.readUInt8(bb);
            i++;
            //sizeOfInstance = sizeOfInstance<<7 | sizeByte;
            sizeOfInstance = sizeOfInstance << 7 | tmp & 0x7f;
        }
        sizeBytes = i;
        ByteBuffer detailSource = bb.slice();
        detailSource.limit(sizeOfInstance);
        parseDetail(detailSource);
        assert detailSource.remaining() == 0: this.getClass().getSimpleName() + " has not been fully parsed";
        bb.position(bb.position() + sizeOfInstance);
    }
    /**
     * @hide
     */
    public abstract void parseDetail(ByteBuffer bb) throws IOException;

    /**
     * @hide
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BaseDescriptor");
        sb.append("{tag=").append(tag);
        sb.append(", sizeOfInstance=").append(sizeOfInstance);
        sb.append('}');
        return sb.toString();
    }
}
