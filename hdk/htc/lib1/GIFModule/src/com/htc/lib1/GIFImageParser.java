/*
 * Copyright (C) 2007 HTC Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.lib1;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.util.Log;

public class GIFImageParser {
    private final static String TAG = "GIFImageParser";
    
    static {
        System.loadLibrary("gifdecoder2");
    }

    private int mValue = 0;
    private int mNativeContext;
    private Bitmap mCachedBitmap = null;
    
    /**
     * Default constructor
     */
    public GIFImageParser() {
        nativeBegin();
    }

    /**
     * Free all buffers decoded. The GIFImageParser cannot be reused once the method is invoked.
     */
    public void release() {
        nativeRelease();
        mCachedBitmap = null;
    }

    /**
     * Get the bitmap decoded with the specified index.
     *
     * @param index An index of frames. First frame index is 0
     * @return A bitmap decoded from the specified index
     * @throws IllegalAugumentException if index is greater than or equal to frame count ; index is less than 0
     * @throws IllegalStateException release() was called
     * @throws OutOfMemoryError The buffer cannot be created
     */
    public native Bitmap getFrame(int index);

    /**
     * How many frames are in the gif.
     *
     * @return The total count of frames in the gif
     * @throws IllegalStateException release() was called
     */
    public native int frameCount();

    /**
     * Set the gif data source with the specified complete path.
     *
     * @param filepath      complete path name for the file to be decoded
     * @throws IllegalAugumentException filepath is null or invalid
     * @throws IllegalStateException release() was called
     * @throws OutOfMemoryError The buffer cannot be created
     */
    public native void setDataPath(String filepath);

    /**
     * Set the data source with the data stream.
     *
     * @param data      byte array of compressed gif image data
     * @throws IllegalAugumentException data is null
     * @throws IllegalStateException release() was called
     * @throws OutOfMemoryError The buffer cannot be created
     */
    public native void setRawData(byte[] data);

    /**
     * Get the duration of a frame with the specified index.
     *
     * @param index An index of frames. First frame index is 0
     * @return A duration of the specified index of frames
     * @throws IllegalAugumentException if index is greater than or equal to frame count ; index is less than 0.
     * @throws IllegalStateException release() was called.
     */
    public native long frameDurationAtIndex(int index);

    /**
     * Get the dimension of a frame with the specified index.
     *
     * @param index An index of frames. First frame index is 0
     * @return The dimension of a frame with the specified index
     * @throws IllegalAugumentException if index is greater than or equal to frame count ; index is less than 0.
     * @throws IllegalStateException release() was called
     */
    public native Rect frameRectAtIndex(int index);

    protected void finalize() {
        release();
    }

    private native void nativeBegin();

    private native void nativeRelease();

    private Bitmap setBitmapPixels(int[] pixel, int stride, int width, int height) {
        mCachedBitmap.setPixels(pixel, 0, stride, 0, 0, width, height);

        return mCachedBitmap;
    }
    
    private Bitmap preparedBitmap(int width, int height) {
        mCachedBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        return mCachedBitmap;
    }


    static public class JniException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public JniException(String message) {
            super(message);
        }
    }
}
