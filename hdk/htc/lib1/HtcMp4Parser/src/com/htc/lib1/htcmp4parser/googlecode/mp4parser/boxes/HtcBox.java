package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes;

import static com.htc.lib1.htcmp4parser.googlecode.mp4parser.util.CastUtils.l2i;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeReader;
import com.htc.lib1.htcmp4parser.coremedia.iso.IsoTypeWriter;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.AbstractBox;

/**
 * @hide
 * {@exthide}
 */
public class HtcBox extends AbstractBox {
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public static final String TYPE = "htcb";

	private int version;
	private int flag; 
	private long offset;
	private int size;
	private boolean applyUint32 = false;
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public void setOffset(final long offset){
		this.offset = offset;
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public long getOffset(){
		return offset;
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public void setTableSize(final int size){
		this.size = size;
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public int getTableSize(){
		return size;
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public HtcBox() {
		super(TYPE);
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	@Override
	protected long getContentSize() {
		return 4 + 4 + (applyUint32 ? 4 : 8 ) + 4;
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	@Override
	protected void getContent(ByteBuffer byteBuffer) {
		IsoTypeWriter.writeInt32(byteBuffer, version);
		IsoTypeWriter.writeInt32(byteBuffer,flag);
		if( applyUint32){
			IsoTypeWriter.writeUInt32(byteBuffer, offset);
		} else {
			IsoTypeWriter.writeUInt64(byteBuffer, offset);
		}
		IsoTypeWriter.writeInt32(byteBuffer, size);
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	@Override
	protected void _parseDetails(ByteBuffer content) {
		version = IsoTypeReader.readInt32(content);
		flag = IsoTypeReader.readInt32(content);
		applyUint32 = (1 == flag);		//0 -> 64 , 1 -> 32
		offset = applyUint32 ? IsoTypeReader.readUInt32(content)  : IsoTypeReader.readUInt64(content)  ;
		size = IsoTypeReader.readInt32(content);
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("version:" + version + "\nflag:" + flag + "\noffset:" + offset + "\nsize:" + size);
		return buffer.toString();
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	public static class HTCMetaDataTable implements Cloneable{
		
		/**
		 * @hide
		 * {@exthide}
		 */
		@Override
		public Object clone(){
			try {
				HTCMetaDataTable table = (HTCMetaDataTable)super.clone();
				table.mEntries = new LinkedList<Entry>();
				table.mEntries.addAll(getEntries());
				return table;
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public static enum KEY{
			HMTT,
			DLen,
			ZCVR,
			ZPTH,
			ZPTW,
			ZSHT,
			ZJPG,
			CamD;
		};
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public static final int INVALID_INDEX  = -1;
		
		/** 
		 * Entry
		 * @hide
		 * {@exthide}
		 */
		public static  class Entry implements Cloneable{
			
			/**
			 * @hide
			 * {@exthide}
			 */
			public final String key;					//4 bytes
			/**
			 * @hide
			 * {@exthide}
			 */
			public final int isData;					//4 bytes , 0 or 1
			/**
			 * @hide
			 * {@exthide}
			 */
			public final int valueSize;				//4 bytes , 4 , 12 or 16

			/**
			 * used when valueSize equals 4
			 * @hide
			 * {@exthide}
			 */
			public int value;							//UINT32

			/**
			 * used when valueSize equals 12 or 16
			 * @hide
			 * {@exthide}
			 */
			public int index;
			/**
			 * @hide
			 * {@exthide}
			 */
			public long offset;							//UINT32 or UINT64
			/**
			 * @hide
			 * {@exthide}
			 */
			public int size;
			
			//used when valueSize is not included in {4,12,16}
			/**
			 * @hide
			 * {@exthide}
			 */
			public byte[] datas = null;
			
			/**
			 * @hide
			 * {@exthide}
			 */
			Entry(String key , int isData , int valueSize){
				this.key = key;
				this.isData = isData;
				this.valueSize = valueSize;
			}
			
			/**
			 * @hide
			 * {@exthide}
			 */
			public int getSize(){
				return 4 + 4 + 4 + valueSize;
			}
			
			/**
			 * @hide
			 * {@exthide}
			 */
			public String toString(){
				return "key:" + key + ",isData:" + isData + ",valueSize:" + valueSize + ",value:" + value + ",index:" + index + ",offset:" + offset + ",size:" + size;
			}
			
			/**
			 * @hide
			 * {@exthide}
			 */
			public void getContent(ByteBuffer byteBuffer){
				byteBuffer.put(IsoFile.fourCCtoBytes(key));
				IsoTypeWriter.writeUInt32(byteBuffer, isData);
				IsoTypeWriter.writeUInt32(byteBuffer, valueSize);
				switch(valueSize){
				case 4:
					IsoTypeWriter.writeUInt32(byteBuffer, value);
					break;
				case 12:
					IsoTypeWriter.writeInt32(byteBuffer, index);
					IsoTypeWriter.writeUInt32(byteBuffer, offset);
					IsoTypeWriter.writeUInt32(byteBuffer, size);
					break;
				case 16:
					IsoTypeWriter.writeInt32(byteBuffer, index);
					IsoTypeWriter.writeUInt64(byteBuffer, offset);
					IsoTypeWriter.writeUInt32(byteBuffer, size);
					break;
				default:
					if(null != datas){
						byteBuffer.put(datas);
					}
					break;
				}
			}
			
			/**
			 * @hide
			 * {@exthide}
			 */
			@Override
			public Object clone(){
				try {
					return super.clone();
				} catch (CloneNotSupportedException e) {
					throw new RuntimeException(e);
				}
			}
			
		}//class Entry
		
		private String mCheckTag;
		private long version;
		private LinkedList<Entry> mEntries = new LinkedList<Entry>();
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public void clearEntries(){
			mEntries.clear();
		}
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public List<Entry> getEntries(){
			return mEntries;
		}
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public List<Entry> cloneEntries(KEY key){
			List<Entry> list = new ArrayList<Entry>();
			for(Entry entry:mEntries){
				if(key.toString().equals(entry.key)){
					list.add((Entry)entry.clone());
				}
			}
			return list;
		}
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public final void getContent(WritableByteChannel os) throws IOException {

			//allocate ByteBuffer
			ByteBuffer bb = ByteBuffer.allocate(l2i(getSize()));		
			
			//write check tag	
			bb.put(IsoFile.fourCCtoBytes(mCheckTag));
			
			//write version
			IsoTypeWriter.writeUInt32(bb, version);
			
			//write entries in order
			for(Entry entry:mEntries){
				entry.getContent(bb);
			}
			
			//write check tag
			bb.put(IsoFile.fourCCtoBytes(mCheckTag));
			
			//flush buffer to channel
			bb.rewind();
			os.write(bb);
		}
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public final int getSize() {
			int ret = 0;
			ret += 4;										//check tag
			ret += 4;										//version
			for( Entry entry : mEntries ){	//the size of all entries
				ret += entry.getSize();
			}
			ret += 4;										//check tag
			return ret;
		}
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public void parse(ByteBuffer content) {
			
			//parse check tag
			final String CHECK_TAG = mCheckTag = IsoTypeReader.read4cc(content);
			
			//parse version
			version = IsoTypeReader.readUInt32(content);
			
			//recursive parse entries until we meet check tag again.
			String tag = null;
			while(!CHECK_TAG.equalsIgnoreCase((tag = IsoTypeReader.read4cc(content)))){
				final String key = tag;
				final int isData = (int)IsoTypeReader.readUInt32(content);
				final int valueSize = (int)IsoTypeReader.readUInt32(content);
				final Entry entry = new Entry(key,isData,valueSize);
				switch(valueSize){
				case 4:
					entry.value = IsoTypeReader.readInt32(content);			//this should only happens while isData equals 0;
					break;
				case 12:
					entry.index = IsoTypeReader.readInt32(content);
					entry.offset = IsoTypeReader.readUInt32(content);			//notice here!!! if valueSize equals 12 , we should consider offset as 4 bytes INT
					entry.size = IsoTypeReader.readInt32(content);
					break;
				case 16:
					entry.index = IsoTypeReader.readInt32(content);
					entry.offset = IsoTypeReader.readUInt64(content);			//notice here!!! if valueSize equals 12 , we should consider offset as 4 bytes LONG
					entry.size = IsoTypeReader.readInt32(content);
					break;
				default:
					final byte[] datas = entry.datas = new byte[valueSize];	//if we can't identify data_info by value-size , just save whole bytes array
					content.get(datas, 0, datas.length);
					break;
				}
				//save parsed entry into list.
				mEntries.add(entry);
				
			}//the end of while
		}

	}//class HTCMetaDataTable
}
