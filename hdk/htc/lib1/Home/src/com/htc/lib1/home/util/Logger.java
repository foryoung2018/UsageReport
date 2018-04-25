package com.htc.lib1.home.util;

/**
 * Created by jason on 8/1/16.
 */
public class Logger {
    private static HomeLoggerBase STATIC_LOGGER = HomeLoggerFactory.getLogger(Logger.class.getSimpleName());

    /////////// secure logger +++
    public static void vs(String tag, String format, Object... args) {
        STATIC_LOGGER.vs(tag, format, args);
    }

    public static void vs(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.vs(tag, tr, format, args);
    }

    public static void ds(String tag, String format, Object... args) {
        STATIC_LOGGER.ds(tag, format, args);
    }

    public static void ds(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.ds(tag, tr, format, args);
    }

    public static void is(String tag, String format, Object... args) {
        STATIC_LOGGER.is(tag,format, args);
    }

    public static void is(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.is(tag, tr, format, args);
    }

    public static void ws(String tag, String format, Object... args) {
        STATIC_LOGGER.ws(tag, format, args);
    }

    public static void ws(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.ws(tag, tr, format, args);
    }

    public static void es(String tag, String format, Object... args) {
        STATIC_LOGGER.es(tag, format, args);
    }

    public static void es(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.es(tag, tr, format, args);
    }
    /////////// secure logger ---

    public static void v(String tag, String format, Object... args) {
        STATIC_LOGGER.v(tag, format, args);
    }

    public static void v(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.v(tag, tr, format, args);
    }

    public static void d(String tag, String format, Object... args) {
        STATIC_LOGGER.d(tag, format, args);
    }

    public static void d(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.d(tag, tr, format, args);
    }

    public static void i(String tag, String format, Object... args) {
        STATIC_LOGGER.i(tag,format, args);
    }

    public static void i(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.i(tag, tr, format, args);
    }

    public static void w(String tag, String format, Object... args) {
        STATIC_LOGGER.w(tag, format, args);
    }

    public static void w(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.w(tag, tr, format, args);
    }

    public static void e(String tag, String format, Object... args) {
        STATIC_LOGGER.e(tag, format, args);
    }

    public static void e(String tag, Throwable tr, String format, Object... args) {
        STATIC_LOGGER.e(tag, tr, format, args);
    }

    public static String showStack(String tag, int level) {
        return STATIC_LOGGER.showStack(tag, level);
    }

    public static void traceBegin(String name) {
        STATIC_LOGGER.traceBegin(name);
    }

    public static void traceEnd() {
        STATIC_LOGGER.traceEnd();
    }
}
