
package com.htc.lib1.dm.logging;

import java.util.Iterator;

import android.os.Bundle;

import com.htc.lib1.dm.logging.LogBuilder.ObjectFormatter;

/**
 * Formatter to handle {@link Bundle}.
 */
class BundleFormatter implements ObjectFormatter {

    @Override
    public boolean append(StringBuilder builder, Object object) {
        if (object instanceof Bundle) {
            Bundle bundle = (Bundle) object;
            builder.append("{");
            Iterator<String> iter = bundle.keySet().iterator();
            String key;
            while (iter.hasNext()) {
                key = iter.next();
                builder.append("\"").append(key).append("\":\"").append(bundle.get(key))
                        .append("\"");
                if (iter.hasNext())
                    builder.append(",");
            }
            builder.append("}");
            return true;
        }
        return false;
    }

}
