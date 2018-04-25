//package studio.panoplus;
package com.htc.lib1.panoviewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

//============================================================
//
public class DataRetriever 
{  
	public static final int CODE_SUCCESS = 0;
	public static final int CODE_FAIL = -1;
	 
	public static final String KEY_IS_PANORAMA_PLUS		= "ISPANORAMA";
	public static final String KEY_INTERNAL_RECTANGLE	= "INTERNALRECTANGLE";
	public static final String KEY_XMP_INFO				= "XMPINFO";
	
	public static final int DECODE_TYPE_BEST_FIT		= 1;
	public static final int DECODE_TYPE_CENTER_CROP		= 2;
	
	public static final int DECODE_DETPH_16		        = 16;
	public static final int DECODE_DETPH_32		        = 32;	
	
	public static final String KEY_THUMBNAIL_MAX_SIZE   = "THUMBMAXSIZE";

	ParcelFileDescriptor mDescriptor = null;
	JNIFoos jniFoos = new JNIFoos();
	
	public int setDataSource( String filepath )
	{
		ParcelFileDescriptor descriptor = null;
		try {
			descriptor = ParcelFileDescriptor.open(new File(filepath), ParcelFileDescriptor.MODE_READ_ONLY);			
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		}
		return setDataSource( descriptor );
	}
	
	public int setDataSource(Context context, Uri uri)
	{		
		ParcelFileDescriptor descriptor = null;
		try {
			//descriptor = context.getContentResolver().openFileDescriptor(uri, "r");			
			AssetFileDescriptor ad = context.getContentResolver().openAssetFileDescriptor(uri, "r");
			descriptor = ad.getParcelFileDescriptor();
			
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
		return setDataSource( descriptor );
    }

    public int setDataSource( ParcelFileDescriptor descriptor)
    {
    	if( null == descriptor ){
    		return CODE_FAIL;
    	}
    	
    	mDescriptor = descriptor;
    	int rlt = jniFoos.setFd( mDescriptor.getFileDescriptor() );
    	
    	if( 0 == rlt ){
    		mDescriptor = null;
    	}
    	
    	Log.i("PANO+DR", String.format("SD:%d", rlt));
    	
        return rlt > 0 ? CODE_SUCCESS : CODE_FAIL;
    }
	 
    public int Release()
    {   	
    	int rlt = 0;
    	try {
			if( null != mDescriptor ){
				mDescriptor.close();
				mDescriptor = null;
				rlt = 1;
			}
		} catch (IOException e) {			
			e.printStackTrace();
			rlt = 0;
		}
    	return rlt > 0 ? CODE_SUCCESS : CODE_FAIL;
    }
     
	/**
    * Decode thumbnail with maximum internal rectangle
    * @param width Prefer width.
    * @param height Prefer height.
    * @param colorDepth Color depth. 16 or 32.
    * @param decodeType Decode type.
    * @param fromExifThumbnail True to decode thumbnail from exif thumbnail.
    * @param extras Extras parameter.
    * @return Bitmap.
    */   
    public Bitmap decodeThumbnail(int width, int height, int colorDepth, int decodeType, boolean fromExifThumbnail, Bundle extras)
    {
    	// Sanity checks:
    	//
    	if( null == mDescriptor ){
    		return null;
    	}
    	if( width < 0 || height < 0 ){
    		return null;
    	} 
    	
    	Bitmap.Config config;
    	
    	switch( colorDepth )
    	{
    	case 16:
    		config = Bitmap.Config.RGB_565;
            break;
    	case 32:
    		config = Bitmap.Config.ARGB_8888;
    		break;
    	default:
    		return null;
    	}
    	
    	// create java bitmap
    	Bitmap init_bmp = Bitmap.createBitmap(width, height, config);
    	//
    	int [] bmpRealSize = new int[2];
    	bmpRealSize[0] = bmpRealSize[1] = 0;  
    	int result = jniFoos.getThumb(mDescriptor.getFileDescriptor(), decodeType, fromExifThumbnail?1:0, init_bmp, bmpRealSize);
    	
    	Bitmap result_bmp = null;
    	
    	if( result > 0 && bmpRealSize[0] > 0 && bmpRealSize[1] > 0 ){
    		result_bmp = Bitmap.createBitmap(init_bmp, 0, 0, bmpRealSize[0], bmpRealSize[1]);
    		//Log.i("Leon", String.format("Leon, input size: %d, %d, real size: %d, %d", width, height, bmpRealSize[0], bmpRealSize[1]));
    	}
    	
    	return result_bmp;
    }
   
    /* Using native android bitmap, Obsolete in 2012-10-21 */
    /*
    public Bitmap decodeThumbnail(int width, int height, int colorDepth, int decodeType, boolean fromExifThumbnail, Bundle extras)
    {
    	if( null == mDescriptor ){
    		return null;
    	}
    	
    	Bitmap img =jniFoos.getThumb(mDescriptor.getFileDescriptor(), width, height, colorDepth, decodeType, fromExifThumbnail?1:0); 
    	
    	return img;
    }
    */
    
     
	public Boolean extractMetaDataAsBoolean(String key)
    {		
		if( null == mDescriptor ){
    		return false;
    	}
		
		int rlt = 0;
		
		if( key.equals(KEY_IS_PANORAMA_PLUS) )
		{
			rlt = jniFoos.checkPano(mDescriptor.getFileDescriptor());
			
			Log.i("PANO+DR", String.format("KEY P+: %d", rlt));
		}
		 
        return rlt > 0;        
    }
		 
	public int extractMetaDataAsInt(String key)
	{
		return 0;
	}
	
	public float extractMetaDataAsFloat(String key)
	{
		return 0;
	}
	
	public double extractMetaDataAsDouble(String key)
	{
		return 0;
	}
	
	public String extractMetaDataAsString(String key)
	{
		return "";
	}

	/**
	 * @param String key == XMPINFO
	 * @return xmp info
	 *   arry[0] : CroppedAreaLeftPixels;
	 *   arry[1] : CroppedAreaTopPixels;
	 *   arry[2] : CroppedAreaImageWidthPixels;
     *   arry[3] : CroppedAreaImageHeightPixels;
     *   arry[4] : FullPanoWidthPixels;
	 *   arry[5] : FullPanoHeightPixels;
	 *   arry[6] : NorthPosInX;
     *
     * --------------------------------------------
     * @param String key == KEY_INTERNAL_RECTANGLE
	 * @return maximal interal rectangle information
     *   arry[0] : rectangle left top position, X;
	 *   arry[1] : rectangle left top position, Y;
	 *   arry[2] : rectangle width;
     *   arry[3] : rectangle height;
     * 
	 */ 
	public int[] extractMetaDataAsIntArray(String key)
	{	
		if( null == mDescriptor ){
    		return null;
    	}
		  
		int rlt = 0;
		int [] arry = null;
		
		if( key.equals(KEY_INTERNAL_RECTANGLE) )
		{
			arry = new int[4];
			if( 0!= jniFoos.getRoiRect(mDescriptor.getFileDescriptor(), arry) ){
				if( 0 < arry[2] || 0 < arry[3] ){
					rlt = 1;
					Log.i("PANO+DR", String.format("%d, %d, %d, %d", arry[0], arry[1], arry[2], arry[3]));
				}
			}
			 
		}else
		if( key.equals(KEY_XMP_INFO) )
		{
			arry = new int[7];
			if( 0!=  jniFoos.getXmpInfo(mDescriptor.getFileDescriptor(), arry) ){				
				rlt = 1;
				Log.i("PANO+DR", String.format("%d, %d, %d, %d, %d, %d, %d", arry[0], arry[1], arry[2], arry[3], arry[4], arry[5], arry[6]));				
			}			
		}
		
		if( 0 == rlt ){
			arry = null;
			Log.i("PANO+DR", String.format("NULL result !") );
		}
		
		return arry;
	}
}

 
