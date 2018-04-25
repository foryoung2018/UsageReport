package com.htc.lib1.dm.logging;

import java.util.ArrayList;
import java.util.List;


/**
 * Builder to construct log messages.
 */
class LogBuilder {
    /**
     * Interface to formatters that decide how to format an object an append it
     * to a {@link StringBuilder}.
     */
    public interface ObjectFormatter {
        /**
         * Convert an object into a string.
         * 
         * @param builder Builder to append.
         * @param object Object to format.
         * @return True if it recognized the object and appended it to the
         *         builder, false if the formatter is not designed for given
         *         object.
         */
        public boolean append(StringBuilder builder, Object object);
    }

    public static final int MAX_LINE_LENGTH = 100;
    public static final String NEW_LINE = "\n";
    public static final String INDENT = "  ";
    private static LogBuilder sInstance;
    private List<ObjectFormatter> mFormatters = new ArrayList<ObjectFormatter>(5);

    /**
     * Get the {@link LogBuilder} instance.
     * 
     * @return {@link LogBuilder} instance.
     */
    public static synchronized LogBuilder getBuilder() {
        if (sInstance == null)
            sInstance = new LogBuilder();
        return sInstance;
    }
    
    /**
     * Remove the instance to reset all properties.
     */
    public static synchronized void reset() {
        sInstance = null;
    }

    /**
     * Construct a {@link LogBuilder} instance.
     */
    private LogBuilder() {
        BundleFormatter bundleFmt = null;
        if (LoggingProperties.get().enableThrowableFormatter())
            mFormatters.add(new ThrowableFormatter());
        if (LoggingProperties.get().enableBundleFormatter()) {
            bundleFmt = new BundleFormatter();
            mFormatters.add(bundleFmt);
        }
        mFormatters.add(new IntentFormatter(bundleFmt));
        mFormatters.add(new ContextFormatter());
        mFormatters.add(new SimpleFormatter());
    }

    /**
     * Build log message.
     * 
     * @param prefix Prefix to use.
     * @param objects Objects to log.
     * @return String built to log, or {@code null} if no objects were passed in
     *         and {@link LoggingProperties#enableMethodLog()} is false.
     */
    public String build(String prefix, String hashCode, Object... objects) {
        boolean logMethodName = LoggingProperties.get().enableMethodLog();
        boolean logObjects = objects.length > 0;
        StackTraceElement element = null;

        if (logMethodName || logObjects) {

            // Construct prefix.
            StringBuilder builder = new StringBuilder(prefix);

            // Construct method name.
            if (logMethodName) {
                builder.append(hashCode).append(": ");
                element = getLoggableElement();
                builder.append(element.getMethodName()).append("()");
            }

            if (logObjects)
                builder.append(": ");

            // Convert all objects.
            for (Object obj : objects) {
                for (ObjectFormatter fmt : mFormatters) {
                    if (fmt.append(builder, obj))
                        break;
                }
            }

            // Construct file info.
            if (LoggingProperties.get().enableFileInfoLog()) {
                if (element == null)
                    element = getLoggableElement();
                builder.append(" (").append(element.getFileName()).append(": ")
                        .append(element.getLineNumber()).append(")");
            }
            
            // Replace newlines.
            int index = builder.indexOf(NEW_LINE);
            while (index != -1) {
                int base = index + NEW_LINE.length();
                builder.insert(base, INDENT);
                index = builder.indexOf("\n", base);
            }

            return builder.toString();
        }

        return null;
    }

    /**
     * Get loggable element.
     * 
     * @return Element.
     */
    private StackTraceElement getLoggableElement() {
        boolean sawLogger = false;
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            String current = element.getClassName();
            if (current.startsWith(Logger.class.getName())) {
                sawLogger = true;
            } else if (sawLogger) {
                return element;
            }
        }
        throw new IllegalStateException("Not able to find loggable element.");
    }
}
