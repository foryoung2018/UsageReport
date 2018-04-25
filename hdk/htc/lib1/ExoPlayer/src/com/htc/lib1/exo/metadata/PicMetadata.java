/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.htc.lib1.exo.metadata;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.exoplayer.util.ParsableBitArray;

import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

/**
 * A metadata that contains parsed ID3 APIC (Picture) frame data associated
 * with time indices.
 */
public class PicMetadata {

  public static final String TYPE = "APIC";
  public static final String TAG = "PicMetadata";

  private final byte[] privateData;
  public final String mimeType;
  public final int pictureType;
  public final String description;

  private final int position;
  private final int length;
  //private Bitmap bitmap;
  WeakReference<Bitmap> bitmap;
  public PicMetadata(byte[] buf) {
    this.privateData = buf;

    ParsableBitArray byteBuffer = new ParsableBitArray(privateData);

    byteBuffer.skipBits(8);

    this.mimeType = readString(byteBuffer);

    this.pictureType = byteBuffer.readBits(8);

    this.description = readString(byteBuffer);
    
    this.position = byteBuffer.getPosition() / 8;

    this.length = privateData.length - position;

    bitmap = null;
  }

  /**
   * We don't parse the bitmap until the application call getBitmap()
   * 
   */
  public Bitmap getBitmap() {	
    if (bitmap == null) {
      ParsableBitArray byteBuffer = new ParsableBitArray(privateData);
      this.bitmap = new WeakReference<Bitmap>(BitmapFactory.decodeByteArray(byteBuffer.data, position, length));
    }
    return bitmap.get();
  }
  
  /**
   * just save the byte array into the storage.
   * 
   */
  public void saveFile(String path) {
	  saveFile(privateData, position, length, path);
  }

  private String readString(ParsableBitArray byteBuffer){
    StringBuilder str = new StringBuilder();
    char c = (char) byteBuffer.readBits(8);
    while (c != 0 ) {
      str.append((char)c);
      c = (char) byteBuffer.readBits(8);
    }
    return str.toString();
  }

  private void saveFile(byte[] buf, int start, int length, String path) {
    try{
      FileOutputStream out = new FileOutputStream(path);
      out.write(buf, start, length - start);
      out.close();
    }catch(Exception e) {

    }
  }
}
