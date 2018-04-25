package com.htc.lib1.media.zoe;

/**
*{@inheritDoc}
*{@exthide}
*/
public class HtcZoeMetadata extends com.htc.lib0.media.zoe.HtcZoeMetadata{

    protected static int translateKeyToIntLE(String key) throws IllegalArgumentException{
        if( key == null || key.length() != 4)
            throw new IllegalArgumentException("key format is invalid");

        byte b[] = key.getBytes();
        int ile = (int)b[0] & 0xFF;
        ile += ((int)b[1] & 0xFF) << 8;
        ile += ((int)b[2] & 0xFF) << 16;
        ile += ((int)b[3] & 0xFF) << 24;
        
        return ile;
    }
    
    protected static int translateKeyToInt(String key) throws IllegalArgumentException{
        if( key == null || key.length() != 4)
            throw new IllegalArgumentException("key format is invalid");

        byte b[] = key.getBytes();
        int ile = (int)b[3] & 0xFF;
        ile += ((int)b[2] & 0xFF) << 8;
        ile += ((int)b[1] & 0xFF) << 16;
        ile += ((int)b[0] & 0xFF) << 24;
        
        return ile;
    }

    protected static int translateKeyToInt(char[] key) throws IllegalArgumentException {
        final int length = 4; // FOURCC size must be 4.
        if (key == null || key.length != length) {
            throw new IllegalArgumentException("key format is invalid");
        }

        int ile = 0;
        for (int i=0; i<length; i++) {
            ile += (((int) (byte)key[i]) & 0xFF) << (8*(length-(i+1)));
        }

        return ile;
    }
}

