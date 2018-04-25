package com.htc.lib2.opensense.facedetect;

import com.htc.lib2.opensense.internal.SystemWrapper.HtcBuildFlag;
import com.htc.lib2.opensense.internal.SystemWrapper.SystemProperties;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

/**
 * @author Vincent
 * 
 * @hide
 */
public class FaceRectUtils {
    private static final String TAG = "FaceRectUtils";

	static final boolean DEBUG = HtcBuildFlag.Htc_DEBUG_flag;
	static final boolean DEBUG_SIMPLE = true;
	static final boolean DEBUG_SORT = DEBUG && false;
	static final boolean DEBUG_PERFORMANCE = DEBUG && false;		
	static final boolean DEBUG_DETAIL = SystemProperties.getBoolean("debug.FaceRectUtils", false);//SystemProperties.getBoolean("debug.FaceRectUtils", HtcBuildFlag.Htc_DEBUG_flag);
    
	final static float SUPPERBIGGER=4.5f;
	final static float CENTERFACEWEIGHT=1.3f;
	final static int MARGINX = 20; // ? cm at 1080P screen's pixels
	final static int MARGINY= 20; // ? cm at 1080P screen's pixels
	
	
    /**
     * Prepares the transformation matrix needed for scaling, centering, then cropping an image.
     * <p>
     * Additionally, the "second-4th" cropping rule may be applied if needed. The "second-4th" rule shifts the target
     * window upwards in an attempt to capture the face area of the image.
     * </p>
     * 
     * @param srcWidth
     *            the width of the source image
     * @param srcHeight
     *            the height of the source image
     * @param dstWidth
     *            the desired width of the final image
     * @param dstHeight
     *            the desired height of the final image
     * @param applyCroppingRule
     *            whether to apply the second-4th cropping rule
     * @param result
     *            a {@link Matrix} to store the results; or null
     * @return the resulting transformation matrix
     */
    public static Matrix getCenterCropMatrix(float srcWidth, float srcHeight, float dstWidth, float dstHeight, boolean applyCroppingRule,
            Matrix result) {
        final float fScale;
        final float fTranslateX;
        float fTranslateY;

        // Note: We need to find the scale_factor that satisfies both of these conditions:
        //       (1) scale_factor * source_width >= destination_width
        //       (2) scale_factor * source_height >= destination_height
        //       To satisfy both, we take the larger of the two scale_factors.

        // the minimum scale_factor to satisfy (1)
        final float fMinScaleWidth = dstWidth / srcWidth;
        // the minimum scale_factor to satisfy (2)
        final float fMinScaleHeight = dstHeight / srcHeight;

        if(fMinScaleWidth > fMinScaleHeight) {
            // use the width scale_factor cause it's larger
            fScale = fMinScaleWidth;
            // width is scaled to match destination width, so we don't need to translate X
            fTranslateX = 0;
            // calculate the resulting scaled height
            final float fScaledHeight = srcHeight * fScale;
            // center vertically by translating Y
            fTranslateY = (dstHeight - fScaledHeight) * 0.5f;
            // only check to apply cropping rule when the scaled height is greater than destination height
            if(applyCroppingRule) {
                fTranslateY += Math.min(fScaledHeight / 8, Math.abs(fTranslateY));
            }
        } else {
            // use the height scale_factor cause it's larger
            fScale = fMinScaleHeight;
            // calculate the resulting scaled width
            final float fScaledWidth = srcWidth * fScale;
            // center horizontally by translating X
            fTranslateX = (dstWidth - fScaledWidth) * 0.5f;
            // height is scaled to match destination height, so we don't need to translate Y
            fTranslateY = 0;
        }

        final Matrix transformMatrix = (result == null) ? new Matrix() : result;
        // reset matrix to identity matrix
        transformMatrix.reset();
        // apply scaling
        transformMatrix.setScale(fScale, fScale);
        // apply translate, rounding the values to the nearest integer
        transformMatrix.postTranslate((int) (fTranslateX + 0.5f), (int) (fTranslateY + 0.5f));
        return transformMatrix;
    }


    /**
     * Prepares the transformation matrix needed for scaling, centering, then cropping an image.
     * <p>
     * Additionally, the provided face rectangle is used as a center-point to crop the image.
     * </p>
     * 
     * @param srcWidth
     *            the width of the source image
     * @param srcHeight
     *            the height of the source image
     * @param dstWidth
     *            the desired width of the final image
     * @param dstHeight
     *            the desired height of the final image
     * @param faceRect
     *            a {@link Rect}(left,top,right,bottom) containing the detected face area
     * @param result
     *            a {@link Matrix} to store the results; or null if a new one should be created
     * @return the resulting transformation matrix
     */
    public static Matrix getCenterCropMatrix(float srcWidth, float srcHeight, float dstWidth, float dstHeight, Rect faceRect, Matrix result) {
        final float fScale;
        final float fScaledWidth;
        final float fScaledHeight;
        float fTranslateX;
        float fTranslateY;

        // Note: We need to find the scale_factor that satisfies both of these conditions:
        //       (1) scale_factor * source_width >= destination_width
        //       (2) scale_factor * source_height >= destination_height
        //       To satisfy both, we take the larger of the two scale_factors.

        // the minimum scale_factor to satisfy (1)
        final float fMinScaleWidth = dstWidth / srcWidth;
        // the minimum scale_factor to satisfy (2)
        final float fMinScaleHeight = dstHeight / srcHeight;

        if(fMinScaleWidth > fMinScaleHeight) {
            // use the width scale_factor cause it's larger
            fScale = fMinScaleWidth;
            // calculate the resulting scaled width
            fScaledWidth = srcWidth * fScale;
            // calculate the resulting scaled height
            fScaledHeight = srcHeight * fScale;
            // width is scaled to match destination width, so we don't need to translate X
            fTranslateX = 0;
            // center vertically by translating Y
            fTranslateY = (dstHeight - fScaledHeight) * 0.5f;
        } else {
            // use the height scale_factor cause it's larger
            fScale = fMinScaleHeight;
            // calculate the resulting scaled width
            fScaledWidth = srcWidth * fScale;
            // calculate the resulting scaled height
            fScaledHeight = srcHeight * fScale;
            // center horizontally by translating X
            fTranslateX = (dstWidth - fScaledWidth) * 0.5f;
            // height is scaled to match destination height, so we don't need to translate Y
            fTranslateY = 0;
        }
        final Matrix transformMatrix = (result == null) ? new Matrix() : result;
        // reset matrix to identity matrix
        transformMatrix.reset();
        // apply scaling
        transformMatrix.setScale(fScale, fScale);
        // apply translate, rounding the values to the nearest integer
        transformMatrix.postTranslate((int) (fTranslateX + 0.5f), (int) (fTranslateY + 0.5f));
        if(faceRect != null) {
            final float fScaledFaceCenterX = faceRect.centerX() * fScale;
            final float fScaledFaceCenterY = faceRect.centerY() * fScale;
            // calculate the offset amount from center to face
            final float fFaceOffsetX = (fScaledWidth / 2) - fScaledFaceCenterX;
            final float fFaceOffsetY = (fScaledHeight / 2) - fScaledFaceCenterY;
            // make sure we don't offset too far, exceeding the bounds of the scaled image
            final float fFaceTranslateX = (Math.abs(fFaceOffsetX) <= Math.abs(fTranslateX)) ? fFaceOffsetX : Math.copySign(fTranslateX,
                    fFaceOffsetX);
            final float fFaceTranslateY = (Math.abs(fFaceOffsetY) <= Math.abs(fTranslateY)) ? fFaceOffsetY : Math.copySign(fTranslateY,
                    fFaceOffsetY);
            transformMatrix.postTranslate(fFaceTranslateX, fFaceTranslateY);
        }
        return transformMatrix;
    }
    
    /**
     * @author henjr
     */
	public static int[]  getUnionRectForMultiFace (int srcWidth, int srcHeight, int dstWidth, int dstHeight, int[] faces, int nOrientation, boolean oneFace, boolean scaleToFit,int rule) {
		if (rule ==0)
			return getUnionRectForMultiFace_CRL(srcWidth,srcHeight,dstWidth,dstHeight,faces,nOrientation,oneFace,scaleToFit,rule);
		else 
			return getUnionRectForMultiFace_Most(srcWidth,srcHeight,dstWidth,dstHeight,faces,nOrientation,oneFace,scaleToFit,rule);
	}

	
	/**
	 * oneFace, scaleToFit,int rule:reserve 
	 * @param srcWidth
	 * @param srcHeight
	 * @param dstWidth
	 * @param dstHeight
	 * @param faces left, top, width, height
	 * @param nOrientation it should not use now, 0: face to right  1: face to bottom 2: face to left 3: face to top
	 * @param oneFace
	 * @param scaleToFit
	 * @param rule
	 * @return
	 */
	public static int[]  getUnionRectForMultiFace_CRL (int srcWidth, int srcHeight, int dstWidth, int dstHeight, int[] faces, int nOrientation, boolean oneFace, boolean scaleToFit,int rule)
	{
		final float fScale;
		final float fScaledWidth;
		final float fScaledHeight;
		int nFaces=0;
		int nTypeAlgorithm = 0;
		boolean bLandscape = true;
		int[] nResult = new int[4];
		int n1stIndex = -1, n2ndIndex = -1;
		int nSize=0, n1stSize = -1, n2ndSize = -1;
		int i, j;		
		long nTime =0;

		if (DEBUG_PERFORMANCE)
			nTime = System.currentTimeMillis();		

		if(DEBUG || DEBUG_DETAIL)
			Log.i(TAG, "Do getUnionRectForMultiFace_CRL src=(" + srcWidth + "x" + srcHeight + ")" + " view=(" + dstWidth + "x" + dstHeight + ")" + " faces=" + (faces != null) + " arg=(" + nOrientation + "," + oneFace + "," + scaleToFit + "," + rule + ")");
		
		if ((srcWidth <=0) || (srcHeight <=0)|| (dstWidth <=0) || (dstHeight <=0)) {
			Log.e(TAG, "FD algorithm=Parameter wrong!!!Should not happened: " + srcWidth + ":" + srcHeight+":"+dstWidth+":"+dstHeight);
			return null;
		}

		if (DEBUG_DETAIL) {
			Log.i(TAG, "Faces Img" );
			if (faces != null) {
				for (i=0;i< faces.length/4;i++) {
					Log.i(TAG, i+":" + faces[4*i]+"," + faces[4*i+1]+"," + faces[4*i+2]+"," + faces[4*i+3]);
				}
			}
		}
		// Note: We need to find the scale_factor that satisfies both of these conditions:
		//		 (1) scale_factor * source_width >= destination_width
		//		 (2) scale_factor * source_height >= destination_height
		//		 To satisfy both, we take the larger of the two scale_factors.
		
		// the minimum scale_factor to satisfy (1)
		final float fMinScaleWidth = ((float)dstWidth) / srcWidth;
		// the minimum scale_factor to satisfy (2)
		final float fMinScaleHeight = ((float)dstHeight) / srcHeight;

		//Adjust Input Parameter to fill out left or top is negative
		if (faces != null) {
			nFaces = faces.length/4;		//left, top , width, height
			if (nFaces > 0) {
				for (i=0;i< nFaces;i++) {					
					if ((faces[4*i]<0)||(faces[4*i+1]<0)) {
						if(DEBUG || DEBUG_DETAIL)
							Log.w(TAG, "Input's coordinate had negative i=" +i + "("+ faces[4*i]+","+faces[4*i+1]+","+faces[4*i+2]+","+faces[4*i+3]+")");
						if (faces[4*i]<0){
							faces[4*i+2] = faces[4*i+2] + faces[4*i];
							faces[4*i] = 0;	
						}
						if (faces[4*i+1]<0){
							faces[4*i+3] = faces[4*i+3] + faces[4*i+1];
							faces[4*i+1] = 0;	
						}
						if(DEBUG || DEBUG_DETAIL)
							Log.w(TAG, "Input's new coordinate i=" +i + "("+ faces[4*i]+","+faces[4*i+1]+","+faces[4*i+2]+","+faces[4*i+3]+")");
					}
				}		
			}
		}	
	
		//Check Input Parameter
		if (faces != null) {
			nFaces = faces.length/4;		//left, top , width, height
			if (nFaces > 0) {
				for (i=0;i< nFaces;i++) {					
					if ((faces[4*i+2] <=0) || (faces[4*i+3] <=0)
						||(faces[4*i]<0)||(faces[4*i+1]<0)||(faces[4*i+2]>srcWidth)||(faces[4*i+3]>srcHeight)) {
						Log.e(TAG, "Should not happened!!!Face Array had error! i=" +i + "("+ faces[4*i]+","+faces[4*i+1]+","+faces[4*i+2]+","+faces[4*i+3]+")");
						nFaces =0;
						break;
					}
				}		
			}
		}
		
		if (nFaces == 0) {
			Log.e(TAG, "FD algorithm=No Face.!!! Should not happened");
			return null;
		}		
			
		
		if(fMinScaleWidth > fMinScaleHeight) {
			// use the width scale_factor cause it's larger
			fScale = fMinScaleWidth;
			// calculate the resulting scaled width
			fScaledWidth = srcWidth * fScale;
			// calculate the resulting scaled height
			fScaledHeight = srcHeight * fScale;
			bLandscape = false;
		} else {
			// use the height scale_factor cause it's larger
			fScale = fMinScaleHeight;
			// calculate the resulting scaled width
			fScaledWidth = srcWidth * fScale;
			// calculate the resulting scaled height
			fScaledHeight = srcHeight * fScale;
			bLandscape = true;
		}
	
		//Check the 1st, 2nd Bigger Faces
		for (i=0;i< nFaces;i++) {
			nSize = faces[4*i+2] * faces[4*i+3];
			
			if (nSize >= n1stSize){ 		
				n2ndSize = n1stSize;
				n2ndIndex = n1stIndex;
				n1stSize = nSize;
				n1stIndex = i;
			} else if (nSize >= n2ndSize){
				n2ndSize = nSize;
				n2ndIndex = i;
			}
		}
		
		//Face Algorithm
		Rect rcFocus = new Rect((int)(faces[4*n1stIndex] *fScale), (int)((faces[4*n1stIndex+1]) *fScale), (int)((faces[4*n1stIndex] + faces[4*n1stIndex+2]) *fScale),(int)((faces[4*n1stIndex+1] + faces[4*n1stIndex+3]) *fScale));
		Rect[] rcBound = new Rect[3];//0:center 1:right 2:left	
		Rect[] rcSrcImgBoundUnion = new Rect[3];//0:center 1:right 2:left	
		int[] nAOI = new int[3]; //0:center 1:right 2:left
		int nTmpLeft=0, nTmpTop = 0, nTmpRight = 0, nTmpBottom = 0; 

		for (i= 0;i<3;i++) {
			rcBound[i] = new Rect();
			rcSrcImgBoundUnion[i] = new Rect(faces[4*n1stIndex], faces[4*n1stIndex+1], faces[4*n1stIndex] + faces[4*n1stIndex+2], faces[4*n1stIndex+1]+faces[4*n1stIndex+3]);
			nAOI[i] =0;			
		}
		
		nResult[0] = faces[4*n1stIndex]; //left
		nResult[1] = faces[4*n1stIndex+1];//top
		nResult[2] = faces[4*n1stIndex]+faces[4*n1stIndex+2];//right		
		nResult[3] = faces[4*n1stIndex+1]+faces[4*n1stIndex+3];//bottom

//		Log.i(TAG, "Time="+ (System.currentTimeMillis() - nTime));
		
		if (nFaces == 1) {
			nTypeAlgorithm = 1;

			//Dump rcBounds
			if (DEBUG_SIMPLE)
				Log.i(TAG, "FD algorithm=" + "1 Face horizon=" + bLandscape + " scale=" + fScale + " result=(" + nResult[0] + ","+nResult[1]+","+(nResult[2])+","+(nResult[3])+")");
		} else if (n1stSize >= (((float)SUPPERBIGGER) * n2ndSize)) {
			nTypeAlgorithm = 2;
			if (DEBUG_SIMPLE)
				Log.i(TAG, "FD algorithm=" + "SuperBig horizon=" + bLandscape + " scale=" + fScale + " result=(" + nResult[0] + ","+nResult[1]+","+(nResult[2])+","+(nResult[3])+")");
		} else {
			// check  faces  if rcFocus is at Center
			int nX = (rcFocus.left + rcFocus.right)/2;
			int nY = (rcFocus.top + rcFocus.bottom)/2;
			if (bLandscape) {
				//AOI is at Center
				if ((nX - dstWidth/2) <= 0) {
					rcBound[0].left = 0;
				} else {
					rcBound[0].left = (int)(nX - dstWidth/2+MARGINX);
				}

				if ((nX + dstWidth/2) >= fScaledWidth) {
					rcBound[0].right = (int)fScaledWidth;			
				} else {
					rcBound[0].right = (int)(nX + dstWidth/2-MARGINX);
				}
				
				rcBound[0].top = 0;
				rcBound[0].bottom = (int)(fScaledHeight);
	
				//AOI is at Right
				if ((rcFocus.right + MARGINX - dstWidth) <= 0) {
					rcBound[1].left = 0;
				} else {
					rcBound[1].left = (int)(rcFocus.right + MARGINX - dstWidth + MARGINX);
				}
				
				if ((rcFocus.right + MARGINX) >= fScaledWidth) {
					rcBound[1].right = (int)(fScaledWidth);
				} else {				
					rcBound[1].right = (int)(rcFocus.right + MARGINX - MARGINX);
				}		
				
				rcBound[1].top = 0;
				rcBound[1].bottom = (int)(fScaledHeight);
	
	
				//AOI is at left
				if ((rcFocus.left - MARGINX) <=0) {
					rcBound[2].left = 0;
				} else {
					rcBound[2].left = (int)(rcFocus.left - MARGINX + MARGINX);
				}

				if ((rcFocus.left - MARGINX + dstWidth)>= fScaledWidth) {
					rcBound[2].right = (int)(fScaledWidth);
				} else {
					rcBound[2].right = (int)(rcFocus.left - MARGINX + dstWidth - MARGINX);
				}
				
				rcBound[2].top = 0;
				rcBound[2].bottom = (int)(fScaledHeight);		
			} else {
				//AOI is at Center
				if ((nY - dstHeight/2) <= 0) {
					rcBound[0].top = 0;
				} else {
					rcBound[0].top = (int)(nY - dstHeight/2+MARGINY);
				}
				
				if ((nY + dstHeight/2) >= fScaledHeight) {
					rcBound[0].bottom = (int)(fScaledHeight);
				} else {
					rcBound[0].bottom = (int)(nY + dstHeight/2-MARGINY);
				}
				
				rcBound[0].left = 0;
				rcBound[0].right = (int)(fScaledWidth);
	
				//AOI is at bottom
				if ((rcFocus.bottom + MARGINY - dstHeight) <= 0) {
					rcBound[1].top = 0;
				} else {
					rcBound[1].top = (int)(rcFocus.bottom + MARGINY - dstHeight + MARGINY);
				}
				
				if ((rcFocus.bottom + MARGINY) >= fScaledHeight) {
					rcBound[1].bottom = (int)(fScaledHeight);
				} else {
					rcBound[1].bottom = (int)(rcFocus.bottom + MARGINY - MARGINY);
				}
				
				rcBound[1].left = 0;
				rcBound[1].right = (int)(fScaledWidth);
				//AOI is at top
				if ((rcFocus.top - MARGINY) <=0) {
					rcBound[2].top = 0;
				} else {
					rcBound[2].top = (int)(rcFocus.top - MARGINY + MARGINY);
				}

				if ((rcFocus.top - MARGINY + dstHeight)>= fScaledHeight) {				
					rcBound[2].bottom = (int)(fScaledHeight);		
				} else {
					rcBound[2].bottom = (int)(rcFocus.top - MARGINY + dstHeight - MARGINY);
				}
				rcBound[2].left = 0;
				rcBound[2].right = (int)(fScaledWidth);
			}
//			Log.i(TAG, "Time3="+ (System.currentTimeMillis() - nTime));

			//Calculate How many Rect inside the Center
			for (i=0;i< nFaces;i++) {
				if (i == n1stIndex) {
					for (j=0;j<3;j++)
						nAOI[j]++;
				} else {
					nTmpLeft = (int)((faces[4*i]) * fScale);
					nTmpTop = (int)((faces[4*i+1]) * fScale);
					nTmpRight = (int)((faces[4*i]+faces[4*i+2]) * fScale);
					nTmpBottom= (int)((faces[4*i+1]+faces[4*i+3]) * fScale);
					for (j =0; j<3;j++) {
						if ((nTmpLeft >= rcBound[j].left) && (nTmpRight <= rcBound[j].right) &&
							(nTmpTop >= rcBound[j].top) && (nTmpBottom <= rcBound[j].bottom)){
							nAOI[j]++;
							if (rcSrcImgBoundUnion[j].left > (faces[4*i]))
								rcSrcImgBoundUnion[j].left = (faces[4*i]);
							if (rcSrcImgBoundUnion[j].top > (faces[4*i+1]))
								rcSrcImgBoundUnion[j].top = (faces[4*i+1]);
							if (rcSrcImgBoundUnion[j].right < (faces[4*i]+faces[4*i+2]))
								rcSrcImgBoundUnion[j].right = (faces[4*i]+faces[4*i+2]);							
							if (rcSrcImgBoundUnion[j].bottom < (faces[4*i+1]+faces[4*i+3]))
								rcSrcImgBoundUnion[j].bottom = (faces[4*i+1]+faces[4*i+3]);
						}
					}
				}
			}
	
			//check which AOI had bigger faces
			if (((CENTERFACEWEIGHT * nAOI[0]) >= nAOI[1]) && ((CENTERFACEWEIGHT * nAOI[0]) >= nAOI[2])) {
				nTypeAlgorithm = 3; 		
			} else if (nAOI[1] >= nAOI[2]) {
				nTypeAlgorithm = 4;
			} else {
				nTypeAlgorithm = 5;
			}

			nResult[0] = rcSrcImgBoundUnion[nTypeAlgorithm-3].left;
			nResult[1] = rcSrcImgBoundUnion[nTypeAlgorithm-3].top;
			nResult[2] = rcSrcImgBoundUnion[nTypeAlgorithm-3].right;
			nResult[3] = rcSrcImgBoundUnion[nTypeAlgorithm-3].bottom;
			//Dump rcBounds

			if (DEBUG_DETAIL) {
				Log.i(TAG, "Faces View");
				
				for (i=0;i< nFaces;i++) {
					Log.i(TAG, i+":" + ((int)(faces[4*i]*fScale))+"," + ((int)(faces[4*i+1]*fScale))+"," + 
						((int)((faces[4*i]+faces[4*i+2]) * fScale))+"," + 
						((int)((faces[4*i+1]+faces[4*i+3]) * fScale)));
				}
			}

			if (DEBUG_SIMPLE||DEBUG_DETAIL)
				Log.i(TAG, "FD algorithm=" + nTypeAlgorithm + " focus=" + n1stIndex +  " (" + faces[4*n1stIndex] + ","+faces[4*n1stIndex+1]+","+(faces[4*n1stIndex+2])+","+(faces[4*n1stIndex+3])+")"+
						" horizon=" + bLandscape + " scale=" + fScale + " max=" + fScaledWidth + "x" + fScaledHeight +
						" x=" + nX + " y=" + nY + " view=(" + dstWidth + "x" + dstHeight + ")" +
//						" FocusImg=(" + faces[4*n1stIndex] + ","+faces[4*n1stIndex+1]+","+(faces[4*n1stIndex]+faces[4*n1stIndex+2])+","+(faces[4*n1stIndex+1]+faces[4*n1stIndex+3])+")"+
//						" Focus=(" + rcFocus.left + ","+rcFocus.top+","+(rcFocus.right)+","+(rcFocus.bottom)+")"+
						" Center= " + nAOI[0] + " rc=(" + rcBound[0].left + ","+rcBound[0].top+","+(rcBound[0].right)+","+(rcBound[0].bottom)+")"+ 
						" Right= " + nAOI[1] + " rc=(" + rcBound[1].left + ","+rcBound[1].top+","+(rcBound[1].right)+","+(rcBound[1].bottom)+")"+
						" Left= " + nAOI[2] + " rc=(" + rcBound[2].left + ","+rcBound[2].top+","+(rcBound[2].right)+","+(rcBound[2].bottom)+")"+
						" result=(" + nResult[0] + ","+nResult[1]+","+(nResult[2])+","+(nResult[3])+")");
		}

		if (DEBUG_PERFORMANCE)
			Log.i(TAG, "Union Rect Time="+ (System.currentTimeMillis() - nTime));
		
		return nResult;
	}

	//SelectSort
	/*
	int ij,min,t;
	min = i;
	for (j=i+1;j<nFaces;j++) {
		if (faces[j] < faces[min])
			min = j;
		t = faces[min];
		faces[min] = faces[i];
		faces[i] = t;
	}
	*/
	
	
	//Left , Top, width, height
	public static void InsertSort(int[] faces, int[] NewFaces, boolean bLandscape){
		int i,j,v;
		int nFaces = faces.length/4;		//left, top , width, height			
		long nTime=0;

		if (DEBUG_PERFORMANCE)
			nTime = System.currentTimeMillis();
		
		for (i = 0;i < nFaces;i++)
			NewFaces[i] = i;
		
		if (bLandscape) {
			for (i=1;i< nFaces;i++) {
				v = NewFaces[i];
				j = i;

				while((j>0) && (faces[4*NewFaces[j-1]] > faces[4*v])){
					NewFaces[j] = NewFaces[j-1];
					j--;
				}
				NewFaces[j] = v;
			}
		} else {
			for (i=1;i< nFaces;i++) {
				v = NewFaces[i];
				j = i;

				while((j>0) && (faces[4*NewFaces[j-1]+1] > faces[4*v+1])){
					NewFaces[j] = NewFaces[j-1];
					j--;
				}
				NewFaces[j] = v;
			}
		}

		if (DEBUG_SORT) {
			Log.i(TAG, "Insert Result Landscape="+ bLandscape);
			for (i=0;i< nFaces;i++) {
				Log.i(TAG, i+":("+faces[4*NewFaces[i]] + "," +faces[4*NewFaces[i]+1]+ ")");
			}
		}		

		if (DEBUG_PERFORMANCE)
			Log.i(TAG, "InsertSort Time="+ (System.currentTimeMillis() - nTime));
	}
	//Faces: left, top, width, height
	//oneFace, scaleToFit,int rule:reserve
	//nOrientation:it should not use now. 
	//0: face to right  1: face to bottom 2: face to left 3: face to top
	public static int[]  getUnionRectForMultiFace_Most(int srcWidth, int srcHeight, int dstWidth, int dstHeight, int[] faces, int nOrientation, boolean oneFace, boolean scaleToFit,int rule)
	{
		final float fScale;
		final float fScaledWidth;
		final float fScaledHeight;
		int nFaces=0;
		int nTypeAlgorithm = 0;
		boolean bLandscape = true;
		int[] nResult = new int[4];
		int n1stIndex = -1, n2ndIndex = -1;
		int nSize=0, n1stSize = -1, n2ndSize = -1;
		int i=0, j=0;		
		long nTime =0;

		if (DEBUG_PERFORMANCE)
			nTime = System.currentTimeMillis();		

		if (DEBUG || DEBUG_DETAIL)
			Log.i(TAG, "Do getUnionRectForMultiFace_Most src=(" + srcWidth + "x" + srcHeight + ")" + " view=(" + dstWidth + "x" + dstHeight + ")" + " faces=" + (faces != null) + " arg=(" + nOrientation + "," + oneFace + "," + scaleToFit + "," + rule + ")");
		
		if ((srcWidth <=0) || (srcHeight <=0)|| (dstWidth <=0) || (dstHeight <=0)) {
			Log.e(TAG, "FD algorithm=Parameter wrong!!!Should not happened: " + srcWidth + ":" + srcHeight+":"+dstWidth+":"+dstHeight);
			return null;
		}
		
		if (DEBUG_DETAIL) {
			Log.i(TAG, "Faces Img" );
			if (faces != null) {
				for (i=0;i< faces.length/4;i++) {
					Log.i(TAG, i+":" + faces[4*i]+"," + faces[4*i+1]+"," + faces[4*i+2]+"," + faces[4*i+3]);
				}
			}
		}		
		
		// Note: We need to find the scale_factor that satisfies both of these conditions:
		//		 (1) scale_factor * source_width >= destination_width
		//		 (2) scale_factor * source_height >= destination_height
		//		 To satisfy both, we take the larger of the two scale_factors.
		
		// the minimum scale_factor to satisfy (1)
		final float fMinScaleWidth = ((float)dstWidth) / srcWidth;
		// the minimum scale_factor to satisfy (2)
		final float fMinScaleHeight = ((float)dstHeight) / srcHeight;

		//Adjust Input Parameter to fill out left or top is negative
		if (faces != null) {
			nFaces = faces.length/4;		//left, top , width, height
			if (nFaces > 0) {
				for (i=0;i< nFaces;i++) {					
					if ((faces[4*i]<0)||(faces[4*i+1]<0)) {
						if(DEBUG || DEBUG_DETAIL)
							Log.w(TAG, "Input's coordinate had negative i=" +i + "("+ faces[4*i]+","+faces[4*i+1]+","+faces[4*i+2]+","+faces[4*i+3]+")");
						if (faces[4*i]<0){
							faces[4*i+2] = faces[4*i+2] + faces[4*i];
							faces[4*i] = 0;	
						}
						if (faces[4*i+1]<0){
							faces[4*i+3] = faces[4*i+3] + faces[4*i+1];
							faces[4*i+1] = 0;	
						}
						if(DEBUG || DEBUG_DETAIL)
							Log.w(TAG, "Input's new coordinate i=" +i + "("+ faces[4*i]+","+faces[4*i+1]+","+faces[4*i+2]+","+faces[4*i+3]+")");
					}
				}		
			}
		}	

		//Check Input Parameter
		//Faces must inside srcWidth, srcHeight		
		if (faces != null) {
			nFaces = faces.length/4;		//left, top , width, height
//			if (DEBUG_FACESPARAMETER)
			if (nFaces > 0) {
				for (i=0;i< nFaces;i++) {
					if ((faces[4*i+2] <=0) || (faces[4*i+3] <=0)
						||(faces[4*i]<0)||(faces[4*i+1]<0)||(faces[4*i+2]>srcWidth)||(faces[4*i+3]>srcHeight)) {
						Log.e(TAG, "Should not happened!!!Face Array had error! i=" +i + "("+ faces[4*i]+","+faces[4*i+1]+","+faces[4*i+2]+","+faces[4*i+3]+")");						
						nFaces =0;
						break;
					}
				}		
			}
		}
		
		if (nFaces == 0) {
			Log.e(TAG, "FD algorithm=No Face.!!! Should not happened");
			return null;
		}
		
		
		if(fMinScaleWidth > fMinScaleHeight) {
			// use the width scale_factor cause it's larger
			fScale = fMinScaleWidth;
			// calculate the resulting scaled width
			fScaledWidth = srcWidth * fScale;
			// calculate the resulting scaled height
			fScaledHeight = srcHeight * fScale;
			bLandscape = false;
		} else {
			// use the height scale_factor cause it's larger
			fScale = fMinScaleHeight;
			// calculate the resulting scaled width
			fScaledWidth = srcWidth * fScale;
			// calculate the resulting scaled height
			fScaledHeight = srcHeight * fScale;
			bLandscape = true;
		}
	
		//Check the 1st, 2nd Bigger Faces
		for (i=0;i< nFaces;i++) {
			nSize = faces[4*i+2] * faces[4*i+3];
			
			if (nSize >= n1stSize){ 		
				n2ndSize = n1stSize;
				n2ndIndex = n1stIndex;
				n1stSize = nSize;
				n1stIndex = i;
			} else if (nSize >= n2ndSize){
				n2ndSize = nSize;
				n2ndIndex = i;
			}
		}
		
		//Face Algorithm
		Rect rcFocus = new Rect((int)(faces[4*n1stIndex] *fScale), (int)((faces[4*n1stIndex+1]) *fScale), (int)((faces[4*n1stIndex] + faces[4*n1stIndex+2]) *fScale),(int)((faces[4*n1stIndex+1] + faces[4*n1stIndex+3]) *fScale));
		Rect[] rcBound = new Rect[3];//0:center 1:right 2:left	
//		Rect[] rcSrcImgBoundUnion = new Rect[3];//0:center 1:right 2:left	
//		int[] nAOI = new int[3]; //0:center 1:right 2:left
//		int nTmpLeft=0, nTmpTop = 0, nTmpRight = 0, nTmpBottom = 0; 

		for (i= 0;i<3;i++) {
			rcBound[i] = new Rect();
//			rcSrcImgBoundUnion[i] = new Rect(faces[4*n1stIndex], faces[4*n1stIndex+1], faces[4*n1stIndex] + faces[4*n1stIndex+2], faces[4*n1stIndex+1]+faces[4*n1stIndex+3]);
//			nAOI[i] =0;			
		}
		
		nResult[0] = faces[4*n1stIndex]; //left
		nResult[1] = faces[4*n1stIndex+1];//top
		nResult[2] = faces[4*n1stIndex]+faces[4*n1stIndex+2];//right		
		nResult[3] = faces[4*n1stIndex+1]+faces[4*n1stIndex+3];//bottom

//		Log.i(TAG, "Time="+ (System.currentTimeMillis() - nTime));
		
		if (nFaces == 1) {
			nTypeAlgorithm = 1;

			//Dump rcBounds
			if (DEBUG_SIMPLE)			
				Log.i(TAG, "FD algorithm=" + "1 Face horizon=" + bLandscape + " scale=" + fScale + " result=(" + nResult[0] + ","+nResult[1]+","+(nResult[2])+","+(nResult[3])+")");
		} else if (n1stSize >= (((float)SUPPERBIGGER) * n2ndSize)) {
			nTypeAlgorithm = 2;
			if (DEBUG_SIMPLE)
				Log.i(TAG, "FD algorithm=" + "SuperBig horizon=" + bLandscape + " scale=" + fScale + " result=(" + nResult[0] + ","+nResult[1]+","+(nResult[2])+","+(nResult[3])+")");
			
		} else {	
			// check  faces  if rcFocus is at Center
			int nX = (rcFocus.left + rcFocus.right)/2;
			int nY = (rcFocus.top + rcFocus.bottom)/2;		
			if (bLandscape) {
				//AOI is at Center
				if ((nX - dstWidth/2) <= 0) {
					rcBound[0].left = 0;
				} else {
					rcBound[0].left = (int)(nX - dstWidth/2+MARGINX);
				}

				if ((nX + dstWidth/2) >= fScaledWidth) {
					rcBound[0].right = (int)fScaledWidth;			
				} else {
					rcBound[0].right = (int)(nX + dstWidth/2-MARGINX);
				}
				
				rcBound[0].top = 0;
				rcBound[0].bottom = (int)(fScaledHeight);
	
				//AOI is at Right
				if ((rcFocus.right + MARGINX - dstWidth) <= 0) {
					rcBound[1].left = 0;
				} else {
					rcBound[1].left = (int)(rcFocus.right + MARGINX - dstWidth + MARGINX);
				}
				
				if ((rcFocus.right + MARGINX) >= fScaledWidth) {
					rcBound[1].right = (int)(fScaledWidth);
				} else {				
					rcBound[1].right = (int)(rcFocus.right + MARGINX - MARGINX);
				}
				
				rcBound[1].top = 0;
				rcBound[1].bottom = (int)(fScaledHeight);
	
	
				//AOI is at left
				if ((rcFocus.left - MARGINX) <=0) {
					rcBound[2].left = 0;
				} else {
					rcBound[2].left = (int)(rcFocus.left - MARGINX + MARGINX);
				}

				if ((rcFocus.left - MARGINX + dstWidth)>= fScaledWidth) {
					rcBound[2].right = (int)(fScaledWidth);
				} else {
					rcBound[2].right = (int)(rcFocus.left - MARGINX + dstWidth - MARGINX);
				}
				
				rcBound[2].top = 0;
				rcBound[2].bottom = (int)(fScaledHeight);		
			} else {
				//AOI is at Center
				if ((nY - dstHeight/2) <= 0) {
					rcBound[0].top = 0;
				} else {
					rcBound[0].top = (int)(nY - dstHeight/2+MARGINY);
				}
				
				if ((nY + dstHeight/2) >= fScaledHeight) {
					rcBound[0].bottom = (int)(fScaledHeight);
				} else {
					rcBound[0].bottom = (int)(nY + dstHeight/2-MARGINY);
				}
				
				rcBound[0].left = 0;
				rcBound[0].right = (int)(fScaledWidth);
	
				//AOI is at bottom
				if ((rcFocus.bottom + MARGINY - dstHeight) <= 0) {
					rcBound[1].top = 0;
				} else {
					rcBound[1].top = (int)(rcFocus.bottom + MARGINY - dstHeight + MARGINY);
				}
				
				if ((rcFocus.bottom + MARGINY) >= fScaledHeight) {
					rcBound[1].bottom = (int)(fScaledHeight);
				} else {
					rcBound[1].bottom = (int)(rcFocus.bottom + MARGINY - MARGINY);
				}
				
				rcBound[1].left = 0;
				rcBound[1].right = (int)(fScaledWidth);
				//AOI is at top
				if ((rcFocus.top - MARGINY) <=0) {
					rcBound[2].top = 0;
				} else {
					rcBound[2].top = (int)(rcFocus.top - MARGINY + MARGINY);
				}

				if ((rcFocus.top - MARGINY + dstHeight)>= fScaledHeight) {				
					rcBound[2].bottom = (int)(fScaledHeight);		
				} else {
					rcBound[2].bottom = (int)(rcFocus.top - MARGINY + dstHeight - MARGINY);
				}
				rcBound[2].left = 0;
				rcBound[2].right = (int)(fScaledWidth);
			}
			//			Log.i(TAG, "Time3="+ (System.currentTimeMillis() - nTime));

			//Calculate Max Bound
			Rect rcMaxBound = new Rect(rcBound[1]);
			int[] NewFacesIndex = new int[nFaces]; 
			int[] scaleFaces = new int[nFaces*4]; // left, top, right, bottom
			Rect  rcSrcImgBoundUnion = new Rect();
			int nAOI = 0, nMaxAOI = 0;
			int nLocalBoundLeft, nLocalBoundRight, nLocalBoundTop, nLocalBoundBottom;
			int nLocalSrcImgUnionLeft, nLocalSrcImgUnionRight, nLocalSrcImgUnionTop, nLocalSrcImgUnionBottom;

			//Calculate Max Bound
			if (rcBound[2].left < rcMaxBound.left)
				rcMaxBound.left = rcBound[2].left;
			if (rcBound[2].top < rcMaxBound.top)
				rcMaxBound.top = rcBound[2].top;
			if (rcBound[2].right > rcMaxBound.right)
				rcMaxBound.right = rcBound[2].right;
			if (rcBound[2].bottom > rcMaxBound.bottom)
				rcMaxBound.bottom = rcBound[2].bottom;			
				
			//Sort Face		
			InsertSort(faces, NewFacesIndex, bLandscape);
			//Prepare a new Faces after applying scale 
			for (i=0;i<nFaces;i++){
				scaleFaces[4*i] = (int)(faces[4*NewFacesIndex[i]] *fScale);
				scaleFaces[4*i+1] = (int)(faces[4*NewFacesIndex[i]+1] *fScale);
				scaleFaces[4*i+2] = (int)((faces[4*NewFacesIndex[i]]+ faces[4*NewFacesIndex[i]+2])*fScale);
				scaleFaces[4*i+3] = (int)((faces[4*NewFacesIndex[i]+1]+ faces[4*NewFacesIndex[i]+3])*fScale);
			}
			
			boolean bSkip = false;
			//Calculate How many Rect inside the Center
			for (i=0;i< nFaces;i++) {
				if ((scaleFaces[4*i]>=rcMaxBound.left)&&(scaleFaces[4*i+1]>=rcMaxBound.top)
					&&(scaleFaces[4*i+2]<=rcMaxBound.right)&&(scaleFaces[4*i+3]<=rcMaxBound.bottom)) {
					//Calculate how many AOI inside this Area
					nAOI =1;

					if (bLandscape) {
						nLocalBoundLeft = scaleFaces[4*i];
						nLocalBoundRight = scaleFaces[4*i] + (int)fScaledWidth;
						nLocalBoundTop = 0;						
						nLocalBoundBottom = (int)fScaledHeight;
					} else {
						nLocalBoundTop = scaleFaces[4*i+1];
						nLocalBoundBottom = scaleFaces[4*i+1] + (int)fScaledHeight;
						nLocalBoundLeft = 0;
						nLocalBoundRight = (int)fScaledWidth;					
					}
					
					if (nLocalBoundRight > rcMaxBound.right)
						nLocalBoundRight = rcMaxBound.right;
					if (nLocalBoundBottom > rcMaxBound.bottom)
						nLocalBoundBottom = rcMaxBound.bottom;					
					
					nLocalSrcImgUnionLeft = faces[4*NewFacesIndex[i]];
					nLocalSrcImgUnionTop = faces[4*NewFacesIndex[i]+1];
					nLocalSrcImgUnionRight = faces[4*NewFacesIndex[i]]+faces[4*NewFacesIndex[i]+2];
					nLocalSrcImgUnionBottom = faces[4*NewFacesIndex[i]+1]+faces[4*NewFacesIndex[i]+3];

					if (i != (nFaces-1)) {
						for (j=i+1;j< nFaces;j++) {
							if ((scaleFaces[4*j]>=nLocalBoundLeft)&&(scaleFaces[4*j+1]>=nLocalBoundTop)
								&&(scaleFaces[4*j+2]<=nLocalBoundRight)&&(scaleFaces[4*j+3]<=nLocalBoundBottom)) {
								nAOI++;

								if (nLocalSrcImgUnionLeft > (faces[4*NewFacesIndex[j]]))
									nLocalSrcImgUnionLeft = (faces[4*NewFacesIndex[j]]);
								if (nLocalSrcImgUnionTop > (faces[4*NewFacesIndex[j]+1]))
									nLocalSrcImgUnionTop = (faces[4*NewFacesIndex[j]+1]);
								if (nLocalSrcImgUnionRight < (faces[4*NewFacesIndex[j]]+faces[4*NewFacesIndex[j]+2]))
									nLocalSrcImgUnionRight = (faces[4*NewFacesIndex[j]]+faces[4*NewFacesIndex[j]+2]);							
								if (nLocalSrcImgUnionBottom < (faces[4*NewFacesIndex[j]+1]+faces[4*NewFacesIndex[j]+3]))
									nLocalSrcImgUnionBottom = (faces[4*NewFacesIndex[j]+1]+faces[4*NewFacesIndex[j]+3]);
							}
						}
					}
			
					if (nAOI > nMaxAOI) {
						nMaxAOI = nAOI;

						rcSrcImgBoundUnion.left = nLocalSrcImgUnionLeft;
						rcSrcImgBoundUnion.top = nLocalSrcImgUnionTop;
						rcSrcImgBoundUnion.right = nLocalSrcImgUnionRight;
						rcSrcImgBoundUnion.bottom = nLocalSrcImgUnionBottom; 
					}
				}
			}
	
			nResult[0] = rcSrcImgBoundUnion.left;
			nResult[1] = rcSrcImgBoundUnion.top;
			nResult[2] = rcSrcImgBoundUnion.right;
			nResult[3] = rcSrcImgBoundUnion.bottom;			
			//Dump rcBounds
			if (DEBUG_DETAIL) {
				Log.i(TAG, "Faces View");
				
				for (i=0;i< nFaces;i++) {
					Log.i(TAG, i+":" + ((int)(faces[4*i]*fScale))+"," + ((int)(faces[4*i+1]*fScale))+"," + 
						((int)((faces[4*i]+faces[4*i+2]) * fScale) - (int)(faces[4*i]*fScale))+"," + 
						((int)((faces[4*i+1]+faces[4*i+3]) * fScale) - (int)(faces[4*i+1]*fScale)));
				}
			}
			
			if (DEBUG_SIMPLE||DEBUG_DETAIL)
				Log.i(TAG, "FD algorithm=MostFaces focus=" + n1stIndex +  " (" + faces[4*n1stIndex] + ","+faces[4*n1stIndex+1]+","+(faces[4*n1stIndex+2])+","+(faces[4*n1stIndex+3])+")"+
						" horizon=" + bLandscape + " scale=" + fScale + " max=" + fScaledWidth + "x" + fScaledHeight +
						" x=" + nX + " y=" + nY + " view=(" + dstWidth + "x" + dstHeight + ")" +
						" Focus=(" + rcFocus.left + ","+rcFocus.top+","+(rcFocus.right)+","+(rcFocus.bottom)+")"+
						" faces=" + nMaxAOI + 
						" rcMaxBound=(" + rcMaxBound.left + ","+rcMaxBound.top+","+(rcMaxBound.right)+","+(rcMaxBound.bottom)+")"+
						" result=(" + nResult[0] + ","+nResult[1]+","+(nResult[2])+","+(nResult[3])+")");
		}

		if (DEBUG_PERFORMANCE)
			Log.i(TAG, "Union Rect Time="+ (System.currentTimeMillis() - nTime));
		
		return nResult;
	}
	
}
