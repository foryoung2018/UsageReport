
package com.htc.lib1.dm.logging;

import android.util.Log;

import com.htc.lib1.dm.logging.LogBuilder.ObjectFormatter;

/**
 * Formatter to handle {@link Throwable}.
 */
class ThrowableFormatter implements ObjectFormatter {

    @Override
    public boolean append(StringBuilder builder, Object object) {
        if (object instanceof Throwable) {
            builder.append(Log.getStackTraceString((Throwable) object));
            return true;
        }
        return false;
    }

}
