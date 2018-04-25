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
import com.htc.lib1.htcmp4parser.coremedia.iso.ChannelHelper;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.List;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * A free box. Just a placeholder to enable editing without rewriting the whole file.
 * @hide
 * {@exthide}
 */
public class FreeBox implements Box {
	/**
	 * @hide
	 */
	public static final String TYPE = "free";
	/**
	 * @hide
	 */
	ByteBuffer data;
	/**
	 * @hide
	 */
	List<Box> replacers = new LinkedList<Box>();
    private ContainerBox parent;

    /**
     * @hide
     */
    public FreeBox() {
    }

    /**
     * @hide
     */
    public FreeBox(int size) {
        this.data = ByteBuffer.allocate(size);
    }

    /**
     * @hide
     */
    public ByteBuffer getData() {
        return data;
    }

    /**
     * @hide
     */
    public void setData(ByteBuffer data) {
        this.data = data;
    }

    /**
     * @hide
     */
    public void getBox(WritableByteChannel os) throws IOException {
        for (Box replacer : replacers) {
            replacer.getBox(os);
        }
        ByteBuffer header = ByteBuffer.allocate(8);
        IsoTypeWriter.writeUInt32(header, 8 + data.limit());
        header.put(TYPE.getBytes());
        header.rewind();
        os.write(header);
        data.rewind();
        os.write(data);

    }

    /**
     * @hide
     */
    public ContainerBox getParent() {
        return parent;
    }

    /**
     * @hide
     */
    public void setParent(ContainerBox parent) {
        this.parent = parent;
    }

    /**
     * @hide
     */
    public long getSize() {
        long size = 8;
        for (Box replacer : replacers) {
            size += replacer.getSize();
        }
        size += data.limit();
        return size;
    }

    /**
     * @hide
     */
    public String getType() {
        return TYPE;
    }

    /**
     * @hide
     */
    public void parse(ReadableByteChannel readableByteChannel, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        
    	if (readableByteChannel instanceof FileChannel && contentSize > 1024 * 1024) {
    		
    		final boolean isFileOverSize = ChannelHelper.isOverSize(readableByteChannel, contentSize);
    		if (isFileOverSize) {
    			data = ChannelHelper.readFullyAndAllocate(readableByteChannel, contentSize);
    		} else {
                // It's quite expensive to map a file into the memory. Just do it when the box is larger than a MB.
                data = ((FileChannel) readableByteChannel).map(FileChannel.MapMode.READ_ONLY, ((FileChannel) readableByteChannel).position(), contentSize);
                ((FileChannel) readableByteChannel).position(((FileChannel) readableByteChannel).position() + contentSize);
    		}
        } else {
            assert contentSize < Integer.MAX_VALUE;
            data = ChannelHelper.readFully(readableByteChannel, contentSize);
        }
    }

    /**
     * @hide
     */
    public void addAndReplace(Box box) {
        data.position(l2i(box.getSize()));
        data = data.slice();
        replacers.add(box);
    }


}