package com.htc.lib1.dm.logging;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.htc.lib1.dm.logging.LogBuilder.ObjectFormatter;

/**
 * Formatter for intents.
 */
class IntentFormatter implements ObjectFormatter {
    private static final String EXTRAS_STRING = "(has extras)";
    private BundleFormatter mBundleFmt;

    /**
     * Create an {@link IntentFormatter} instance.
     * 
     * @param bundleFmt If not {@code null}, it will be used to format intent
     *            extras.
     */
    public IntentFormatter(BundleFormatter bundleFmt) {
        mBundleFmt = bundleFmt;
    }

    @Override
    @SuppressLint("HTCAddInfoWhenCatch")
    public boolean append(StringBuilder builder, Object object) {
        if (object instanceof Intent) {
            try {
                /*
                 * Try to use hidden internal method to build intent string.
                 * public void toShortString(StringBuilder b, boolean secure,
                 * boolean comp, boolean extras, boolean clip)
                 */
                StringBuilder intentStringBuilder = new StringBuilder("Intent { ");
                object.getClass().getMethod("toShortString", new Class[] {
                        StringBuilder.class, boolean.class, boolean.class,
                        boolean.class, boolean.class
                }).invoke(object, new Object[] {
                        intentStringBuilder, false, true, true, true
                });
                intentStringBuilder.append(" }");

                /* Format extras. */
                int extrasIndex = intentStringBuilder.indexOf(EXTRAS_STRING);
                if (extrasIndex != -1 && mBundleFmt != null) {
                    StringBuilder bundleStringBuilder = new StringBuilder("extras=");
                    if (mBundleFmt.append(bundleStringBuilder, ((Intent) object).getExtras())) {
                        intentStringBuilder.replace(extrasIndex,
                                extrasIndex + EXTRAS_STRING.length(),
                                bundleStringBuilder.toString());
                    }
                }

                builder.append(intentStringBuilder);

                return true;
            } catch (Exception e) {
                // Fallback to normal way if exception occurs.
                return false;
            }
        }
        return false;
    }
}
