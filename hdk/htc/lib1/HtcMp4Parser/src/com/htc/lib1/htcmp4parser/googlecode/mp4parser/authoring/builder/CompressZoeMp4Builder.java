package com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.builder;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import com.htc.lib1.htcmp4parser.coremedia.iso.IsoFile;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.Box;
import com.htc.lib1.htcmp4parser.coremedia.iso.boxes.UserDataBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.authoring.Movie;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.GeoDataBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcBox;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcBox.HTCMetaDataTable;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcBox.HTCMetaDataTable.Entry;
import com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.HtcBox.HTCMetaDataTable.KEY;
import com.htc.lib1.htcmp4parser.utils.Log;

/**
 * @hide
 * {@exthide}
 */
public class CompressZoeMp4Builder extends DefaultMp4Builder {
	
	private boolean mKeepZJPG = true;

	public CompressZoeMp4Builder setKeepZJPG(boolean keep) {
		mKeepZJPG = keep;
		return this;
	}

	/**
	 * @hide
	 * {@exthide}
	 */
    protected InterleaveChunkMdat newInterleaveChunkMdat(final Movie movie){
    	return new MyInterleaveChunkMdat(movie);
    }
	
    private class MyInterleaveChunkMdat extends InterleaveChunkMdat{
    	
    	private BinaryData mZJPGBinary = null;
    	private HTCMetaDataTable mTable = null;
    	
    	/**
    	 * @hide
    	 * {@exthide}
    	 */
		@Override
		public long getSize() {
			//the total size of "mdat" should be original size + htcbinary + htctable 
			return super.getSize() + ((null == mZJPGBinary)?0:mZJPGBinary.getSize()) + ((null == mTable)?0:mTable.getSize());
		}
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public MyInterleaveChunkMdat(Movie movie) {
			super(movie);
		}
		
		/**
		 * @hide
		 * {@exthide}
		 */
		@Override
		public void writeMdat(WritableByteChannel writableByteChannel) throws IOException {
			super.writeMdat(writableByteChannel);
			
			//write binary
			if(null != mZJPGBinary){
				mZJPGBinary.getContent(writableByteChannel);
			}
			
			//write table
			if(null != mTable){
				mTable.getContent(writableByteChannel);
			}
		}//end of writeMdat
    } //end of class MyInterleaveChunkMdat
	
    /**
     * @hide
     * {@exthide}
     */
	@Override
	public IsoFile build(final Movie movie) {
		
		final HtcBox.HTCMetaDataTable table = movie.getTable();
		
		final IsoFile isoFile = super.build(movie);
		
		// convert to pure MP4  if table is not existed
		if(null == table){
			return isoFile;
		}
		
		final MyInterleaveChunkMdat mdat = isoFile.getBoxes(MyInterleaveChunkMdat.class,true).get(0);
	
		//since we have table , so we could consider HtcBox is existed.
		final HtcBox htcBox = isoFile.getBoxes(HtcBox.class, true).get(0);

		//clone the old table , then clear all entries but reserve CHECK_TAG and version 
		final HtcBox.HTCMetaDataTable newTable = (HtcBox.HTCMetaDataTable)table.clone();
		newTable.clearEntries();
		
		//copy MHTT , ZPTH , ZPTW from original table to new table
		final KEY[] copySet = {KEY.DLen,KEY.HMTT,KEY.ZPTH,KEY.ZPTW,KEY.CamD};
		for(final KEY key:copySet){
			final List<Entry> list = table.cloneEntries(key);
			if(null != list && 0 != list.size()){
				newTable.getEntries().add(list.get(0));
				continue;
			}
		}
		
		if (mKeepZJPG) {
			int coverIndex = Integer.MAX_VALUE;
			final KEY[] zeroSet = {KEY.ZCVR,KEY.ZSHT};
			for(final KEY key:zeroSet){
				final List<Entry> list = table.cloneEntries(key);
				if(null != list && 0 != list.size()){
					final Entry z = list.get(0);				//there should exist only one ZCVR and ZSHT , so simply use get(0) here...
					coverIndex = z.value;						//get the index , the index value within ZCVR and ZSHT should be the same in current design.
					z.value = 0;								//since we only left one image here , so we set the value of ZCVR and ZSHT to 0.
					newTable.getEntries().add(z);
					continue;
				}
			}

			Entry coverEntry = null;
			Entry countEntry = null;
			for(Entry entry : table.cloneEntries(KEY.ZJPG)){
				if( coverIndex == entry.index ){				//we keep the ZJPG which it's index is as the same as the one we got from ZCVR
					coverEntry = entry;
				} else if(HTCMetaDataTable.INVALID_INDEX == entry.index){
					countEntry = entry;
				}
			}

			if( null != coverEntry && null != countEntry){
				newTable.getEntries().add(coverEntry);		//add cover entry
				newTable.getEntries().add(countEntry);		//add count entry

				//generate new binary segment
				final BinaryData data = new BinaryData( mSrcFileDescriptor , mFilePath , coverEntry.offset,coverEntry.size );						//add binary into mdat

				//reset the value of coverEntry
				coverEntry.index = 0;
				coverEntry.offset = isoFile.getSize(); 			//set the offset of coverEntry

				//reset the value of countEntry
				countEntry.size = 1;										//set the size of countEntry

				//add binary into mdat
				mdat.mZJPGBinary = data;
			}
		}

		//set the value within "htcb" box
		htcBox.setOffset(isoFile.getSize());
		htcBox.setTableSize(newTable.getSize());
		
		//finally , add newTable into mdat
		mdat.mTable = newTable;

		{
			Log.d("print source entries:" + table.getEntries().size());
			final List<HTCMetaDataTable.Entry> entries = table.getEntries();
			for(final HTCMetaDataTable.Entry entry:entries){
				Log.d("entry:" + entry);
			}
		}

		{
			Log.d("print target entries:" + newTable.getEntries().size());
			final List<HTCMetaDataTable.Entry> entries = newTable.getEntries();
			for(final HTCMetaDataTable.Entry entry:entries){
				Log.d("entry:" + entry);
			}
		}
			
		return isoFile;
	}
	
	/**
	 * @hide
	 * {@exthide}
	 */
	@Override
	protected Box createUdta(Movie movie) {
		
		Box box = null;
		
		//generate an empty "udta"
		final UserDataBox udta = new UserDataBox();
		
		//add HtcBox if necessary
		if( null != movie.getTable()  && null != (box = movie.getBox(HtcBox.class))  ){
			udta.addBox(box);
		}
		
		//add GeoDataBox if necessary
		if(null != (box = movie.getBox(GeoDataBox.class))){
			udta.addBox(box);
		}
		
		if( 0 != udta.getBoxes().size() ){
			return udta;
		}
		
		return null;
	}

	/********************************  class BinaryData *******************************/
	private static class BinaryData{
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public BinaryData(final FileDescriptor fd , final String filePath  , final long offset , final long size){
			mSrcFileDescriptor = fd;
			mFilePath = filePath;
			mOffset = offset;
			mSize = size;
		}

		private final  FileDescriptor mSrcFileDescriptor;
		private final String mFilePath;
		private long mOffset;
		private long mSize;
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public long getSize() {
			return mSize;	// header + content + tailer
		}
		
		/**
		 * @hide
		 * {@exthide}
		 */
		public void getContent(WritableByteChannel writableByteChannel) throws IOException {
			
			if(null == mSrcFileDescriptor && null == mFilePath){
				return;
			}
		
			FileInputStream fis =  null;
			FileChannel channel = null;
			try{
				//open source channel
				channel = (fis = (null != mFilePath) ? new FileInputStream(mFilePath) : new FileInputStream(mSrcFileDescriptor)).getChannel();
				
				//write binary
				writableByteChannel.write( channel.map(MapMode.READ_ONLY, mOffset, mSize));
			} finally{
				//ExtractVideo.close(fis);
				//ExtractVideo.close(channel);
			}
		}
	}
	
}
