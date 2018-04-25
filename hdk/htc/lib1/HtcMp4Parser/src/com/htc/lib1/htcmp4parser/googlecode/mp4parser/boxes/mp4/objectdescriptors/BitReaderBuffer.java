package com.htc.lib1.htcmp4parser.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.nio.ByteBuffer;
/**
 * @hide
 */
public class BitReaderBuffer {

    private ByteBuffer buffer;
    /**
     * @hide
     */
    int initialPos;
    /**
     * @hide
     */
    int position;
    /**
     * @hide
     */
    public BitReaderBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
        initialPos = buffer.position();
    }
    /**
     * @hide
     */
    public int readBits(int i) {
        byte b = buffer.get(initialPos + position / 8);
        int v = b < 0 ? b + 256 : b;
        int left = 8 - position % 8;
        int rc;
        if (i <= left) {
            rc = (v << (position % 8) & 0xFF) >> ((position % 8) + (left - i));
            position += i;
        } else {
            int now = left;
            int then = i - left;
            rc = readBits(now);
            rc = rc << then;
            rc += readBits(then);
        }
        buffer.position(initialPos + (int) Math.ceil((double) position / 8));
        return rc;
    }
    /**
     * @hide
     */
    public int getPosition() {
        return position;
    }
    /**
     * @hide
     */
    public int byteSync() {
        int left = 8 - position % 8;
        if (left == 8) {
            left = 0;
        }
        readBits(left);
        return left;
    }
    /**
     * @hide
     */
    public int remainingBits() {
        return buffer.limit() * 8 - position;
    }
}
