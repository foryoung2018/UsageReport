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
package com.htc.lib1.exo.utilities;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;

/**
 * get the first few bytes from local content.
 */
public class ContentHelper {
    static public ByteBuffer getByteBuffer(Context context, Uri uri, int length)
    {
        try {
           FileDescriptor fd = getFileDescriptor(context, uri);
           InputStream inputStream = new FileInputStream(fd);
           ByteBuffer buffer = ByteBuffer.allocate(length);
           int size =inputStream.read(buffer.array(), 0, length);
           buffer.limit(size);
           
           return buffer;
        } catch (Exception e) {

        }
        return null;
    }

    private static FileDescriptor getFileDescriptor(Context context, Uri uri) throws IOException {
      try {
        ContentResolver resolver = context.getContentResolver();
        AssetFileDescriptor fd = resolver.openAssetFileDescriptor(uri, "r");
        return fd.getFileDescriptor();
      } catch (IOException e) {
        throw new IOException(e);
      }
    }
}
