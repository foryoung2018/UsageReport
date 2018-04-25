
package com.htc.lib1.dm.logging;

import com.htc.lib1.dm.logging.LogBuilder.ObjectFormatter;

/**
 * Simply append all objects to builder but specialize {@code null} and empty
 * string.
 */
class SimpleFormatter implements ObjectFormatter {
    public static final String NULL_STRING = "${NULL}";
    public static final String EMPTY_STRING = "${EMPTY}";

    @Override
    public boolean append(StringBuilder builder, Object object) {
        if (object == null) {
            builder.append(NULL_STRING);
        } else if (object instanceof String) {
            if (((String) object).isEmpty())
                builder.append(EMPTY_STRING);
            else
                builder.append(object);
        } else {
            builder.append(object);
        }
        return true;
    }

}
