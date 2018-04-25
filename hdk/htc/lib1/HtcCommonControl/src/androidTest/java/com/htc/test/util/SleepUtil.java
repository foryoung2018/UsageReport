package com.htc.test.util;

/**
 * Created by archermind on 8/12/16.
 */

public class SleepUtil {
    public static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
