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

import com.htc.lib1.htcmp4parser.coremedia.iso.BoxParser;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ContainerBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.ByteBufferByteChannel;
import com.htc.lib1.htcmp4parser.utils.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Abstract base class suitable for most boxes acting purely as container for other boxes.
 * @hide
 */
public abstract class AbstractContainerBox extends AbstractBox implements ContainerBox {
    private static Logger LOG = Logger.getLogger(AbstractContainerBox.class.getName());

    /**
     * @hide
     */
    protected List<Box> boxes = new LinkedList<Box>();
    // Added by Ken - 20120809
    // protected BoxParser boxParser;

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        long contentSize = 0;
        for (Box boxe : boxes) {
            contentSize += boxe.getSize();
        }
        return contentSize;
    }

    /**
     * @hide
     */
    public AbstractContainerBox(String type) {
        super(type);
    }

    /**
     * @hide
     */
    public List<Box> getBoxes() {
        return Collections.unmodifiableList(boxes);
    }

    /**
     * @hide
     */
    public void setBoxes(List<Box> boxes) {
        this.boxes = new LinkedList<Box>(boxes);
    }

    /**
     * @hide
     */
    @SuppressWarnings("unchecked")
    public <T extends Box> List<T> getBoxes(Class<T> clazz) {
        return getBoxes(clazz, false);
    }

    /**
     * @hide
     */
    @SuppressWarnings("unchecked")
    public <T extends Box> List<T> getBoxes(Class<T> clazz, boolean recursive) {
        List<T> boxesToBeReturned = new ArrayList<T>(2);
        for (Box boxe : boxes) {
            //clazz.isInstance(boxe) / clazz == boxe.getClass()?
            // I hereby finally decide to use isInstance

            if (clazz.isInstance(boxe)) {
                boxesToBeReturned.add((T) boxe);
            }

            if (recursive && boxe instanceof ContainerBox) {
                boxesToBeReturned.addAll(((ContainerBox) boxe).getBoxes(clazz, recursive));
            }
        }
        return boxesToBeReturned;
    }

    /**
     * Add <code>b</code> to the container and sets the parent correctly.
     *
     * @param b will be added to the container
     * @hide
     */
    public void addBox(Box b) {
    	if (b == null) {
    		Log.w(this.getClass().getSimpleName(), "addBox got null");
    		return;
    	}
        b.setParent(this);
        boxes.add(b);
    }

    /**
     * @hide
     */
    public void removeBox(Box b) {
        b.setParent(this);
        boxes.remove(b);
    }

    /**
     * @hide
     */
    @Override
    public void parse(ReadableByteChannel readableByteChannel, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        super.parse(readableByteChannel, header, contentSize, boxParser);
        
        // Added by Ken - 20120809
        // this.boxParser = boxParser;
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseChildBoxes(content);
    }

    /**
     * @hide
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        buffer.append(this.getClass().getSimpleName()).append("[");
        for (int i = 0; i < boxes.size(); i++) {
            if (i > 0) {
                buffer.append(";");
            }
            buffer.append(boxes.get(i).toString());
        }
        buffer.append("]");
        return buffer.toString();
    }

    /**
     * The number of bytes from box start (first length byte) to the
     * first length byte of the first child box
     *
     * @return offset to first child box
     * @hide
     */
    public long getNumOfBytesToFirstChild() {
        return 8;
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeChildBoxes(byteBuffer);
    }

    /**
     * @hide
     */
    protected final void parseChildBoxes(ByteBuffer content) {
        try {
            while (content.remaining() >= 8) { //  8 is the minimal size for a sane box
                boxes.add(boxParser.parseBox(new ByteBufferByteChannel(content), this));
            }

            if (content.remaining() != 0) {
                setDeadBytes(content.slice());
                LOG.warning("Something's wrong with the sizes. There are dead bytes in a container box.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @hide
     */
    protected final void writeChildBoxes(ByteBuffer bb) {
        WritableByteChannel wbc = new ByteBufferByteChannel(bb);
        for (Box box : boxes) {
            try {
                box.getBox(wbc);
            } catch (IOException e) {
                // My WritableByteChannel won't throw any excpetion
                throw new RuntimeException("Cannot happen to me", e);
            }
        }
    }

}
