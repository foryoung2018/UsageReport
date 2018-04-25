package com.htc.lib1.videohighlights;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;


public class BokehPhoto {

	private static final byte[] magicWords = {'U','F','C','S'};
	private static final int READ_LEN = 16; 
	
	public class OffsetInfo{
		public int offset;
		public int size;

		public OffsetInfo() {
			this.offset = -1;
			this.size = -1;			
		}

		public OffsetInfo(int offset, int size) {
			this.offset = offset;
			this.size = size;			
		}
	}
		
	public static OffsetInfo getSubimageOffset(String path){	
		return getSubimageOffset(new File(path));		
	}	
			
	public static OffsetInfo getSubimageOffset(Context context, Uri uri) {
		String path = getRealPathFromURI(context, uri);
		
		if(path == null)
			return null;
		else return getSubimageOffset(new File(path));
	}	
	
	public static OffsetInfo getSubimageOffset(ParcelFileDescriptor descriptor){		
		ParcelFileDescriptor.AutoCloseInputStream input = new ParcelFileDescriptor.AutoCloseInputStream(descriptor);
		
		try {
			OffsetInfo info = getSubimageOffset(input);
			input.close();
			return info;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static OffsetInfo getSubimageOffset(FileInputStream input){
		BokehPhoto photo = new BokehPhoto();
		int offset = -1;
		int size = -1;			
		
		try {			
			FileChannel ch = input.getChannel();
			long skipBytes = ch.size() - READ_LEN;
			ch.position(skipBytes);

			byte[] buffer = new byte[READ_LEN]; //read 16 byte; 1st 4-byte is "UFCS", 2nd 4-byte is offset, 3rd 4-byte is size, 4th 4-byte is "UFCS"
			
			input.read(buffer, 0, buffer.length);			
			
			if(buffer[0]!=magicWords[0] || buffer[1]!=magicWords[1] || buffer[2]!=magicWords[2] || buffer[3]!=magicWords[3]
					|| buffer[12]!=magicWords[0] || buffer[13]!=magicWords[1] || buffer[14]!=magicWords[2] || buffer[15]!=magicWords[3]){
				return null;
			}

			offset = ( buffer[7]<<24&0xFF000000) |(  buffer[6]<<16&0x00FF0000) | ( buffer[5]<<8&0x0000FF00) | ( buffer[4]&0x000000FF);
			size = ( buffer[11]<<24&0xFF000000) |(  buffer[10]<<16&0x00FF0000) | ( buffer[9]<<8&0x0000FF00) | ( buffer[8]&0x000000FF);

			return photo.new OffsetInfo(offset, size);			
		} catch (IOException e) {
			e.printStackTrace();
		}		
					
		return null;
	}
	
	private static OffsetInfo getSubimageOffset(File file) {
		
		try {
			FileInputStream input = new FileInputStream(file);
			 OffsetInfo info = getSubimageOffset(input);
			 input.close();
			 return info;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}		 	    
	
	private static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if(cursor.moveToFirst())
				return cursor.getString(column_index);
			else return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}	

	private static InputStream getSubimage(FileInputStream input, OffsetInfo offset){
		try {
			
			if(input==null || offset==null)
				return null;
			
			FileChannel ch = input.getChannel();	
			long skipBytes = offset.offset + magicWords.length;
			ch.position(skipBytes);

			byte[] buffer = new byte[offset.size-2*magicWords.length];
			input.read(buffer);			
			
			return new ByteArrayInputStream(buffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;			
	}
	
	public static InputStream getSubImageInputStream(String path){
			
		try {
			FileInputStream input = new FileInputStream(new File(path));
			OffsetInfo info = getSubimageOffset(input);
			InputStream output = getSubimage(input,info);
			input.close();
			return output;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream getSubImageInputStream(ParcelFileDescriptor descriptor){
		ParcelFileDescriptor.AutoCloseInputStream input = new ParcelFileDescriptor.AutoCloseInputStream(descriptor);
		
		try {
			OffsetInfo info = getSubimageOffset(input);
			InputStream output = getSubimage(input,info);			
			input.close();
			return output;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
