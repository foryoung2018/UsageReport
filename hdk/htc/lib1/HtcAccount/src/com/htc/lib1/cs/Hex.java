
package com.htc.lib1.cs;

/**
 * Resembles Hex class in apache common code.
 */
public class Hex {

    /**
     * Convert the byte array to a human-readable string.
     * 
     * @param input Input byte array.
     * @return All capital human-readable hex string.
     * @throws IllegalArgumentException If {@code array} is {@code null} or
     *             empty array.
     */
    public static String encodeHexString(byte[] input) {
        if (input == null || input.length == 0)
            throw new IllegalArgumentException("'input' is null or empty.");

        char[] val = new char[2 * input.length];
        String hex = "0123456789ABCDEF";
        for (int i = 0; i < input.length; i++) {
            int b = input[i] & 0xff;
            val[2 * i] = hex.charAt(b >>> 4);
            val[2 * i + 1] = hex.charAt(b & 15);
        }
        return String.valueOf(val);
    }

    /**
     * Add a colon between each byte (2 hex) in a hex-represented byte array.
     * 
     * @param input Input string. Usually comes from
     *            {@link Hex#encodeHexString(byte[])}.
     * @return Output with colons.
     */
    public static String splitWithColon(String input) {
        StringBuilder builder = new StringBuilder(input);
        for (int i = 0; i < input.length() / 2 - 1; i++)
            builder.insert(i * 3 + 2, ':');
        return builder.toString();
    }
}
