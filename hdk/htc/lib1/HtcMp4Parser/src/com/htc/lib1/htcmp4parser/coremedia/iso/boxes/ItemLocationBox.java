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
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReaderVariable;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriterVariable;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractFullBox;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * aligned(8) class ItemLocationBox extends FullBox(‘iloc’, version, 0) {
 * unsigned int(4) offset_size;
 * unsigned int(4) length_size;
 * unsigned int(4) base_offset_size;
 * if (version == 1)
 * unsigned int(4) index_size;
 * else
 * unsigned int(4) reserved;
 * unsigned int(16) item_count;
 * for (i=0; i<item_count; i++) {
 * unsigned int(16) item_ID;
 * if (version == 1) {
 * unsigned int(12) reserved = 0;
 * unsigned int(4) construction_method;
 * }
 * unsigned int(16) data_reference_index;
 * unsigned int(base_offset_size*8) base_offset;
 * unsigned int(16) extent_count;
 * for (j=0; j<extent_count; j++) {
 * if ((version == 1) && (index_size > 0)) {
 * unsigned int(index_size*8) extent_index;
 * }
 * unsigned int(offset_size*8) extent_offset;
 * unsigned int(length_size*8) extent_length;
 * }
 * }
 * }
 * @hide
 * {@exthide}
 */
public class ItemLocationBox extends AbstractFullBox {
	/**
	 * @hide
	 */
	public int offsetSize = 8;
	/**
	 * @hide
	 */
	public int lengthSize = 8;
	/**
	 * @hide
	 */
	public int baseOffsetSize = 8;
	/**
	 * @hide
	 */
	public int indexSize = 0;
	/**
	 * @hide
	 */
	public List<Item> items = new LinkedList<Item>();

	/**
	 * @hide
	 */
    public static final String TYPE = "iloc";

    /**
     * @hide
     */
    public ItemLocationBox() {
        super(TYPE);
    }

    /**
     * @hide
     */
    @Override
    protected long getContentSize() {
        long size = 8;
        for (Item item : items) {
            size += item.getSize();
        }
        return size;
    }

    /**
     * @hide
     */
    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt8(byteBuffer, ((offsetSize << 4) | lengthSize));
        if (getVersion() == 1) {
            IsoTypeWriter.writeUInt8(byteBuffer, (baseOffsetSize << 4 | indexSize));
        } else {
            IsoTypeWriter.writeUInt8(byteBuffer, (baseOffsetSize << 4));
        }
        IsoTypeWriter.writeUInt16(byteBuffer, items.size());
        for (Item item : items) {
            item.getContent(byteBuffer);
        }
    }

    /**
     * @hide
     */
    @Override
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        int tmp = IsoTypeReader.readUInt8(content);
        offsetSize = tmp >>> 4;
        lengthSize = tmp & 0xf;
        tmp = IsoTypeReader.readUInt8(content);
        baseOffsetSize = tmp >>> 4;

        if (getVersion() == 1) {
            indexSize = tmp & 0xf;
        }
        int itemCount = IsoTypeReader.readUInt16(content);
        for (int i = 0; i < itemCount; i++) {
            items.add(new Item(content));
        }
    }

    /**
     * @hide
     */
    public int getOffsetSize() {
        return offsetSize;
    }

    /**
     * @hide
     */
    public void setOffsetSize(int offsetSize) {
        this.offsetSize = offsetSize;
    }

    /**
     * @hide
     */
    public int getLengthSize() {
        return lengthSize;
    }

    /**
     * @hide
     */
    public void setLengthSize(int lengthSize) {
        this.lengthSize = lengthSize;
    }

    /**
     * @hide
     */
    public int getBaseOffsetSize() {
        return baseOffsetSize;
    }

    /**
     * @hide
     */
    public void setBaseOffsetSize(int baseOffsetSize) {
        this.baseOffsetSize = baseOffsetSize;
    }

    /**
     * @hide
     */
    public int getIndexSize() {
        return indexSize;
    }

    /**
     * @hide
     */
    public void setIndexSize(int indexSize) {
        this.indexSize = indexSize;
    }

    /**
     * @hide
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @hide
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * @hide
     */
    public Item createItem(int itemId, int constructionMethod, int dataReferenceIndex, long baseOffset, List<Extent> extents) {
        return new Item(itemId, constructionMethod, dataReferenceIndex, baseOffset, extents);
    }

    /**
     * @hide
     */
    Item createItem(ByteBuffer bb) {
        return new Item(bb);
    }

    /**
     * @hide
     * {@exthide}
     */
    public class Item {
    	/**
    	 * @hide
    	 */
    	public int itemId;
    	/**
    	 * @hide
    	 */
    	public int constructionMethod;
    	/**
    	 * @hide
    	 */
    	public int dataReferenceIndex;
    	/**
    	 * @hide
    	 */
    	public long baseOffset;
    	/**
    	 * @hide
    	 */
    	public List<Extent> extents = new LinkedList<Extent>();
    	
    	/**
    	 * @hide
    	 */
        public Item(ByteBuffer in) {
            itemId = IsoTypeReader.readUInt16(in);

            if (getVersion() == 1) {
                int tmp = IsoTypeReader.readUInt16(in);
                constructionMethod = tmp & 0xf;
            }

            dataReferenceIndex = IsoTypeReader.readUInt16(in);
            if (baseOffsetSize > 0) {
                baseOffset = IsoTypeReaderVariable.read(in, baseOffsetSize);
            } else {
                baseOffset = 0;
            }
            int extentCount = IsoTypeReader.readUInt16(in);


            for (int i = 0; i < extentCount; i++) {
                extents.add(new Extent(in));
            }
        }
        
        /**
         * @hide
         */
        public Item(int itemId, int constructionMethod, int dataReferenceIndex, long baseOffset, List<Extent> extents) {
            this.itemId = itemId;
            this.constructionMethod = constructionMethod;
            this.dataReferenceIndex = dataReferenceIndex;
            this.baseOffset = baseOffset;
            this.extents = extents;
        }
        
        /**
         * @hide
         */
        public int getSize() {
            int size = 2;

            if (getVersion() == 1) {
                size += 2;
            }

            size += 2;
            size += baseOffsetSize;
            size += 2;


            for (Extent extent : extents) {
                size += extent.getSize();
            }
            return size;
        }
        
        /**
         * @hide
         */
        public void setBaseOffset(long baseOffset) {
            this.baseOffset = baseOffset;
        }

        /**
         * @hide
         */
        public void getContent(ByteBuffer bb)  {
            IsoTypeWriter.writeUInt16(bb, itemId);

            if (getVersion() == 1) {
                IsoTypeWriter.writeUInt16(bb, constructionMethod);
            }


            IsoTypeWriter.writeUInt16(bb, dataReferenceIndex);
            if (baseOffsetSize > 0) {
                IsoTypeWriterVariable.write(baseOffset, bb, baseOffsetSize);
            }
            IsoTypeWriter.writeUInt16(bb, extents.size());

            for (Extent extent : extents) {
                extent.getContent(bb);
            }
        }

        /**
         * @hide
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (baseOffset != item.baseOffset) return false;
            if (constructionMethod != item.constructionMethod) return false;
            if (dataReferenceIndex != item.dataReferenceIndex) return false;
            if (itemId != item.itemId) return false;
            if (extents != null ? !extents.equals(item.extents) : item.extents != null) return false;

            return true;
        }

        /**
         * @hide
         */
        @Override
        public int hashCode() {
            int result = itemId;
            result = 31 * result + constructionMethod;
            result = 31 * result + dataReferenceIndex;
            result = 31 * result + (int) (baseOffset ^ (baseOffset >>> 32));
            result = 31 * result + (extents != null ? extents.hashCode() : 0);
            return result;
        }

        /**
         * @hide
         */
        @Override
        public String toString() {
            return "Item{" +
                    "baseOffset=" + baseOffset +
                    ", itemId=" + itemId +
                    ", constructionMethod=" + constructionMethod +
                    ", dataReferenceIndex=" + dataReferenceIndex +
                    ", extents=" + extents +
                    '}';
        }
    }

    /**
     * @hide
     */
    public Extent createExtent(long extentOffset, long extentLength, long extentIndex) {
        return new Extent(extentOffset, extentLength, extentIndex);
    }

    /**
     * @hide
     */
    Extent createExtent(ByteBuffer bb) {
        return new Extent(bb);
    }

    /**
     * @hide
     * {@exthide}
     */
    public class Extent {
    	/**
    	 * @hide
    	 */
    	public long extentOffset;
    	/**
    	 * @hide
    	 */
    	public long extentLength;
    	/**
    	 * @hide
    	 */    	
    	public long extentIndex;

    	/**
    	 * @hide
    	 */
        public Extent(long extentOffset, long extentLength, long extentIndex) {
            this.extentOffset = extentOffset;
            this.extentLength = extentLength;
            this.extentIndex = extentIndex;
        }

        /**
         * @hide
         */
        public Extent(ByteBuffer in) {
            if ((getVersion() == 1) && indexSize > 0) {
                extentIndex = IsoTypeReaderVariable.read(in, indexSize);
            }
            extentOffset = IsoTypeReaderVariable.read(in, offsetSize);
            extentLength = IsoTypeReaderVariable.read(in, lengthSize);
        }

        /**
         * @hide
         */
        public void getContent(ByteBuffer os)  {
            if ((getVersion() == 1) && indexSize > 0) {
                IsoTypeWriterVariable.write(extentIndex, os, indexSize);
            }
            IsoTypeWriterVariable.write(extentOffset, os, offsetSize);
            IsoTypeWriterVariable.write(extentLength, os, lengthSize);
        }

        /**
         * @hide
         */
        public int getSize() {
            return (indexSize > 0 ? indexSize : 0) + offsetSize + lengthSize;
        }

        /**
         * @hide
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Extent extent = (Extent) o;

            if (extentIndex != extent.extentIndex) return false;
            if (extentLength != extent.extentLength) return false;
            if (extentOffset != extent.extentOffset) return false;

            return true;
        }

        /**
         * @hide
         */
        @Override
        public int hashCode() {
            int result = (int) (extentOffset ^ (extentOffset >>> 32));
            result = 31 * result + (int) (extentLength ^ (extentLength >>> 32));
            result = 31 * result + (int) (extentIndex ^ (extentIndex >>> 32));
            return result;
        }

        /**
         * @hide
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Extent");
            sb.append("{extentOffset=").append(extentOffset);
            sb.append(", extentLength=").append(extentLength);
            sb.append(", extentIndex=").append(extentIndex);
            sb.append('}');
            return sb.toString();
        }
    }


}
