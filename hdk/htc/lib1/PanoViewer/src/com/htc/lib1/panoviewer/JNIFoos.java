//package studio.panoplus;
package com.htc.lib1.panoviewer;

import java.io.FileDescriptor;
import java.nio.IntBuffer;

import android.graphics.Bitmap;

public class JNIFoos
{	
	
	static
	{
		//-- static lib link
		System.loadLibrary("bv_panodata_hdk_v6");
	}
 
	public native int    setFd( FileDescriptor descriptor );	
	public native int    checkPano(FileDescriptor descriptor);		
	public native int    getRoiRect(FileDescriptor descriptor, int[] roiRect ); 
	public native int    getXmpInfo(FileDescriptor descriptor, int[] xmpInfo );
	
	// native android bitmap, Obsolete in 2013-10-21 
	//public native Bitmap getThumbNative(FileDescriptor descriptor, int wid, int hei, int depth, int type, int fromExif );
	
	public native int    getThumb(FileDescriptor descriptor, int type, int fromExif, Bitmap img, int[] sizeArr );
	
	public native void   onCommand( int a, int b, int c );
	
}
