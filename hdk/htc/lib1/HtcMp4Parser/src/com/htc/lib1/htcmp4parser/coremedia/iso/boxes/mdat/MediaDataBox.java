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

package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat;

import com.htc.lib1.htcmp4parser.coremedia.iso.BoxParser;
import com.htc.lib1.htcmp4parser.coremedia.iso.ChannelHelper;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.ContainerBox;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat.SampleList.SampleOffset;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

/**
 * This box contains the media data. In video tracks, this box would contain video frames. A presentation may
 * contain zero or more Media Data Boxes. The actual media data follows the type field; its structure is described
 * by the metadata (see {@link com.htc.lib1.htcmp4parser.coremedia.iso.boxes.SampleTableBox}).<br>
 * In large presentations, it may be desirable to have more data in this box than a 32-bit size would permit. In this
 * case, the large variant of the size field is used.<br>
 * There may be any number of these boxes in the file (including zero, if all the media data is in other files). The
 * metadata refers to media data by its absolute offset within the file (see {@link com.htc.lib1.htcmp4parser.coremedia.iso.boxes.StaticChunkOffsetBox});
 * so Media Data Box headers and free space may easily be skipped, and files without any box structure may
 * also be referenced and used.
 *  @hide
 * {@exthide}
 */
public final class MediaDataBox implements Box {
    private static Logger LOG = Logger.getLogger(MediaDataBox.class.getName());
    /**
     * @hide
     */
    public static final String TYPE = "mdat";
    /**
     * @hide
     */
    public static final int BUFFER_SIZE = 10 * 1024 * 1024;
    /**
     * @hide
     */
    ContainerBox parent;
    /**
     * @hide
     */
    ByteBuffer header;

    // These fields are for the special case of a FileChannel as input.
    private FileChannel fileChannel;
    private long startPosition;
    private long contentSize;


    private Map<Long, Reference<ByteBuffer>> cache = new HashMap<Long, Reference<ByteBuffer>>();


    /**
     * If the whole content is just in one mapped buffer keep a strong reference to it so it is
     * not evicted from the cache.
     */
    private ByteBuffer content;
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
    public String getType() {
        return TYPE;
    }
    /**
     * @hide
     */
    public void getBox(WritableByteChannel writableByteChannel) throws IOException {
        if (fileChannel != null) {
            assert checkStillOk();
            fileChannel.transferTo(startPosition - header.limit(), contentSize + header.limit(), writableByteChannel);
        } else {
            header.rewind();
            writableByteChannel.write(header);
            writableByteChannel.write(content);
        }
    }

    /**
     * If someone use the same file as source and sink it could the case that
     * inserting a few bytes before the mdat results in overwrting data we still
     * need to write this mdat here. This method just makes sure that we haven't already
     * overwritten the mdat contents.
     * @return true if ok
     */
    private boolean checkStillOk() {
        try {
            fileChannel.position(startPosition - header.limit());
            ByteBuffer h2 = ByteBuffer.allocate(header.limit());
            fileChannel.read(h2);
            header.rewind();
            h2.rewind();
            assert h2.equals(header): "It seems that the content I want to read has already been overwritten.";
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * @hide
     */
    public long getSize() {
        long size = header.limit();
        size += contentSize;
        return size;
    }
    /**
     * @hide
     */    
    public long getContentSize() {
    	return contentSize;
    }
    /**
     * @hide
     */
    public void parse(ReadableByteChannel readableByteChannel, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        this.header = header;
        this.contentSize = contentSize;
        this.fileChannel = ((FileChannel) readableByteChannel);
    	this.startPosition = ((FileChannel) readableByteChannel).position();

        if (readableByteChannel instanceof FileChannel && (contentSize > 100 * 1024)) {
            ((FileChannel) readableByteChannel).position(((FileChannel) readableByteChannel).position() + contentSize);
        } else {        	
        	content = ChannelHelper.readFully(readableByteChannel, l2i(contentSize));
            cache.put(0l, new SoftReference<ByteBuffer>(content));
        }

    }
    /**
     * @hide
     */
    public synchronized SampleOffset getContentOffset(final long offset, final int length) {
    	
    	final SampleOffset ret = new SampleOffset(startPosition + offset, length);
    	return ret;
    }
    
    /**
     * @hide
     */
    public synchronized ByteBuffer getContent(long offset, int length) {

        for (Long chacheEntryOffset : cache.keySet()) {
            if (chacheEntryOffset <= offset && offset <= chacheEntryOffset + BUFFER_SIZE) {
                final ByteBuffer cacheEntry = cache.get(chacheEntryOffset).get();
                if ((cacheEntry != null) && ((chacheEntryOffset + cacheEntry.limit()) >= (offset + length))) {
                    // CACHE HIT
                    cacheEntry.position((int) (offset - chacheEntryOffset));
                    final ByteBuffer cachedSample = cacheEntry.slice();
                    cachedSample.limit(length);
                    return cachedSample;
                }
            }
        }
        // CACHE MISS
        ByteBuffer cacheEntry;
        try {
            // Just mapping 10MB at a time. Seems reasonable.
        	
        	if ( (startPosition+offset) + Math.min(BUFFER_SIZE, contentSize - offset) >= ChannelHelper.FILE_SIZE_CONSTRAINT ) {
        		// we would not use this byteBuffer to store file, read fake map to avoid file over size problem.
        		cacheEntry = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, Math.min(BUFFER_SIZE, contentSize - offset));
        	} else {
        		cacheEntry = fileChannel.map(FileChannel.MapMode.READ_ONLY, startPosition + offset, Math.min(BUFFER_SIZE, contentSize - offset));
        	}
        	
        	
        } catch (IOException e1) {
            LOG.fine("Even mapping just 10MB of the source file into the memory failed. " + e1);
            throw new RuntimeException(
                    "Delayed reading of mdat content failed. Make sure not to close " +
                            "the FileChannel that has been used to create the IsoFile!", e1);
        }
        cache.put(offset, new SoftReference<ByteBuffer>(cacheEntry));
        cacheEntry.position(0);
        final ByteBuffer cachedSample = cacheEntry.slice();
        cachedSample.limit(length);
        return cachedSample;
    }

    /**
     * @hide
     */
    public ByteBuffer getHeader() {
        return header;
    }
    /**
     * @hide
     */    
    public long getMediaDataBoxStartPos() {
//    	if (header == null) {
//    		android.util.Log.e("MediaDataBox", "MediaDataBox header offset error");
//    		return -1;
//    	}
//    	
//    	return startPosition - header.limit();
    	return startPosition;
    }

}
