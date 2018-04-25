package com.htc.studio.bdi.log.codec;

/**
 * The encoder support encoding bytes (8-bit binary data) to an ascii string (7-bit characters).
 * See {@link com.htc.studio.bdi.log.codec.AsciiStringDecoder} for its decoder.
 * @author simon
 * @since 2014/7/15
 */
public class AsciiStringEncoder {
  /**
   * Compute the encoded size (number of characters or string length) given the number of bytes.
   * This function will not throw an exception if the len argument is less than zero.
   *
   * @param len number of bytes
   * @return number of characters
   */
  public static int encodeSize(int len) {
    if (len <= 0) return 0;
    return len + (len + 6) / 7;
  }

  /**
   * Encode byte array <b>bytes</b>, starting from position <b>start</b>, with size <b>length</b>, to the destination buffer <b>buf</b>, starting from position <b>offset</b>.
   * Caller should make sure <b>bytes</b> is not null and its size >= <b>start</b> + <b>length</b>.
   * Caller should make sure <b>buf</b> is not null and its size >= <b>offset</b> + encodeSize(<b>length</b>).
   *
   * @param buf destination array to store encoded output of ascii characters.
   * @param offset the starting position of the encoded output.
   * @param bytes source array to store bytes to be encoded.
   * @param start the starting position of the binary input.
   * @param length the size (number of bytes) of binary input.
   */
  public static void encode(char[] buf, int offset, byte[] bytes, int start, int length) {
    final int end = start + length;
    while (start < end) {
      int res = end - start;
      if (res > 7) res = 7;
      int msb7 = 0;
      for (int k = 0; k < res; k++) {
        int b = bytes[start + k];
        buf[offset++] = (char) (b & 0x07f);
        msb7 |= (b & 0x080) >> k;
      }
      buf[offset++] = (char) (msb7 >> 1);
      start += 7;
    }
  }

  /**
   * Encode the entire byte array <b>bytes</b> to an ascii string.
   *
   * @param bytes source array to store bytes to be encoded
   * @return encoded string of ascii characters
   * @throws IllegalArgumentException when bytes is null
   */
  public static String encode(byte[] bytes) throws IllegalArgumentException {
    if (bytes == null) throw new IllegalArgumentException("bytes");

    int size = encodeSize(bytes.length);
    char[] buf = new char[size];
    encode(buf, 0, bytes, 0, bytes.length);
    return String.valueOf(buf);
  }
}