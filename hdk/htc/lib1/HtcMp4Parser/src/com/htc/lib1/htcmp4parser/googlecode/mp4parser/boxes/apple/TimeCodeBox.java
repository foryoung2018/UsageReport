/*
 * Copyright 2012 Sebastian Annies, Hamburg
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
package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.apple;

import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.sampleentry.SampleEntry;

import java.nio.ByteBuffer;

/**
 * @hide
 * {@exthide}
 *
 */
public class TimeCodeBox extends SampleEntry {
    /**
     * @hide
     */
    byte[] data;

    /**
     * @hide
     */
    public TimeCodeBox() {
        super("tmcd");
    }
    
    /**
     * @hide
     * @return
     */
    @Override
    protected long getContentSize() {
        long size = 26;
        for (Box box : boxes) {
            size += box.getSize();
        }
        return size;
    }

    /**
     * @hide
     * @param content
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        _parseReservedAndDataReferenceIndex(content);
        data = new byte[18];
        content.get(data);
        _parseChildBoxes(content);
    }

    /**
     * @hide
     * @param byteBuffer
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        _writeReservedAndDataReferenceIndex(byteBuffer);
        byteBuffer.put(data);
        _writeChildBoxes(byteBuffer);
    }
}
