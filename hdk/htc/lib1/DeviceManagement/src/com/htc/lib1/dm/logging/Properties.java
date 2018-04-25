package com.htc.lib1.dm.logging;

/**
 * Interface for wrappers to read properties from different implementations.
 * Implement your own settings as {@link com.htc.lib1.dm.logging.CustomProperties} to
 * override the default properties.
 * <p>
 * If any of the methods returns {@code null} in
 * {@link com.htc.lib1.dm.logging.CustomProperties}, the default value from
 * {@link DefaultProperties} will be applied.
 */
public interface Properties {

    /**
     * The tag to use when logging messages.
     * 
     * @return Tag to use. The default value is {@link DefaultProperties#TAG}.
     */
    public String tag();

    /**
     * The tag to use when logging sensitive messages.
     * 
     * @return Tag to use. The default value is {@link DefaultProperties#STAG}.
     */
    public String senstiveTag();

    /**
     * True to log method name. Please note this is a heavy operation that
     * impacts performance much. Ensure it's only enabled for debugging.
     * 
     * @return True to enable method name log. Default false.
     */
    public Boolean enableMethodLog();

    /**
     * True to log file info (file name and line number). Please note this is a
     * heavy operation that impacts performance much. Ensure it's only enabled
     * for debugging.
     * 
     * @return True to enable file info log. Default false.
     */
    public Boolean enableFileInfoLog();

    /**
     * True to log the stack trace of throwables.
     * 
     * @return Enable or not. Default false.
     */
    public Boolean enableThrowableFormatter();

    /**
     * True to expand the content of bundles.
     * 
     * @return Enable or not. Default false.
     */
    public Boolean enableBundleFormatter();
}
