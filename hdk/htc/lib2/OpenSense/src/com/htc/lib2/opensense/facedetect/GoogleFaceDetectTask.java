package com.htc.lib2.opensense.facedetect;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

public class GoogleFaceDetectTask extends FaceDetectTask {
	public GoogleFaceDetectTask(int max) {
		super(max);
	}
	
	@Override
	protected void face_detect_impl(Entry e, Bitmap bm, int scale) {
		// The bitmap must be in 565 format (for now).
		if(bm.getConfig() != Bitmap.Config.RGB_565)bm = bm.copy(Bitmap.Config.RGB_565, true);
		// Note that the width of the image must be even.
		int width = bm.getWidth();
		int height = bm.getHeight();
		int box[] = {0,0,0,0};

		if(width < 32 || height < 32) {
			// image too small don't anaylze
			e.box = box;
			return;
		}

		Bitmap bm2 = null;
    	if((width & 0x01) != 0){
    		width--;
    		bm2 = Bitmap.createBitmap(bm, 0, 0, width, height);
    	} else {
			bm2 = bm;
		}
    	//
		FaceDetector faceDetector = new FaceDetector(width, height, 5);
		Face[] faceInfo = new Face[5];
		int n = faceDetector.findFaces(bm2, faceInfo);
		if(n > 0)box = new int [4 * n] ;
		FDLog.d(TAG, "Google face:" + n);
        for(int i = 0; i < n; i++){
        	PointF mid = new PointF(0f, 0f);
        	faceInfo[i].getMidPoint(mid);
        	float eyeD = faceInfo[i].eyesDistance();
        	int px = (int)(mid.x - eyeD);
        	int py = (int)(mid.y - eyeD);
        	int sx = (int)(eyeD * 2);
        	int sy = (int)(eyeD * 2);
        	box[i * 4 + 0] = px;
        	box[i * 4 + 1] = py;
        	box[i * 4 + 2] = sx;
        	box[i * 4 + 3] = sy;
        	//Log.d(TAG, "px : "+px+" py : "+py + " width : " + sx + " heigth : " + sy);
        }
    	//return(box);
        if(scale > 1)for(int i = 0; i <  box.length; i++)box[i] = box[i] * scale;
		e.box = box;

		if (bm2 != bm) {
			bm2.recycle();
		}
	}
}
