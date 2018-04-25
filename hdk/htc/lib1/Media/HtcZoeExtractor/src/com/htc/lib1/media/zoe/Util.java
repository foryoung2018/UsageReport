package com.htc.lib1.media.zoe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @hide
 * @author Winston
 *
 */
public class Util {
    static int FOURCC(char in1, char in2, char in3, char in4){
        char tag[] = {in1, in2, in3, in4};

        int res = HtcZoeMetadata.translateKeyToInt(tag);
        //int res = HtcZoeMetadata.translateKeyToInt(new String(tag));

/*
         Log.d(TAG, new String(tag) + " " + res + " " + Integer.toHexString(res));
         char str[] = new char[4];
         MakeFourCCString(res, str);
         Log.d(TAG, String.valueOf(str));
*/
        return res;
    }

    static int readInt(final ByteArrayInputStream bis, final long offset) throws IOException
    {
        byte x[] = read(bis, offset, 4);
        return ByteBuffer.wrap(x).getInt();
    }

    static long readLong(final ByteArrayInputStream bis, final long offset) throws IOException
    {
        byte x[] = read(bis, offset, 8);
        return ByteBuffer.wrap(x).getLong();
    }

    static byte[] read(final ByteArrayInputStream bis, final long offset, final int length) throws IOException{
        byte x[] = new byte[length];
        bis.reset();
        bis.skip(offset);
        if(bis.read(x) < length)
            throw new IOException("ERROR I/O");
        return x;
    }

    static int readInt(final ByteArrayInputStream bis) throws IOException
    {
        byte x[] = read(bis, 4);
        return ByteBuffer.wrap(x).getInt();
    }
    
    static long readUInt(final ByteArrayInputStream bis) throws IOException
    {
        return ((long) readInt(bis) & 0xffffffffL);
    }

    static long readLong(final ByteArrayInputStream bis) throws IOException
    {
        byte x[] = read(bis, 8);
        return ByteBuffer.wrap(x).getLong();
    }

    static byte[] read(final ByteArrayInputStream bis, final int length) throws IOException{
        byte x[] = new byte[length];
        if(bis.read(x) < length)
            throw new IOException("ERROR I/O");
        return x;
    }

    static int readInt(final FileChannel fc, final long offset) throws IOException
    {
        byte x[] = read(fc, offset, 4);
        return ByteBuffer.wrap(x).getInt();
    }
    
    static long readUInt(final FileChannel fc, final long offset) throws IOException
    {
        return ((long) readInt(fc, offset) & 0xffffffffL);
    }

    static long readLong(final FileChannel fc, final long offset) throws IOException
    {
        byte x[] = read(fc, offset, 8);
        return ByteBuffer.wrap(x).getLong();
    }

    static byte[] read(final FileChannel fc, final long offset, final int length) throws IOException{
        ByteBuffer data = ByteBuffer.allocate(length);
        if(fc.read(data, offset) < length)
            throw new IOException("ERROR I/O");
        return data.array();
    }

    private static long writeOffset = -1;
    
    //set/get current offset
    static void setCurrentOffset(long cur)
    {
        writeOffset = cur;
    }

    static long getCurrentOffset()
    {
        return writeOffset;
    }

    //write int, long, bytes, string, must call set before write
    static int writeInt(final FileChannel fc, final int value) throws IOException
    {
        int rt = fc.write(ByteBuffer.wrap(ByteBuffer.allocate(4).putInt(value).array()), writeOffset);
        writeOffset += 4;
        return rt;
    }

    static int writeLong(final FileChannel fc, final long value) throws IOException
    {
        int rt = fc.write(ByteBuffer.wrap(ByteBuffer.allocate(8).putLong(value).array()), writeOffset);
        writeOffset += 8;
        return rt;
    }

    static int writeString(final FileChannel fc, final String value) throws IOException
    {
        int rt = fc.write(ByteBuffer.wrap(value.getBytes()), writeOffset);
        writeOffset += 4;
        return rt;
    }
    
    static int writeBytes(final FileChannel fc, final byte[]  value) throws IOException
    {
        int rt = fc.write(ByteBuffer.wrap(value), writeOffset);
        writeOffset += value.length;
        return rt;
    }

    static int writeBytes(final FileChannel fc, final byte[]  value, final long offset) throws IOException
    {
        return fc.write(ByteBuffer.wrap(value), offset);
    }

    static void MakeFourCCString(int x, char s[]) {
        if(s.length < 4)
            return;

        s[0] = (char) (x >> 24);
        s[1] = (char) ((x >> 16) & 0xff);
        s[2] = (char) ((x >> 8) & 0xff);
        s[3] = (char) (x & 0xff);
    }
}
