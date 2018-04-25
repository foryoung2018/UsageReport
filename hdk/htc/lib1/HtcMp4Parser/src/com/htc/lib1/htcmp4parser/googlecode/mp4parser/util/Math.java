package com.htc.lib1.htcmp4parser.googlecode.mp4parser.util;

/**
 * @hide
 */
public class Math {
	/**
     * @hide
     */
    public static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    /**
     * @hide
     */
    public static int gcd(int a, int b) {
        while (b > 0) {
            int temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    /**
     * @hide
     */
    public static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    /**
     * @hide
     */
    public static int lcm(int a, int b) {
        return a * (b / gcd(a, b));
    }

}
