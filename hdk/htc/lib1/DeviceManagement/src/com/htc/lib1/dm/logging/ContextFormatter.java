package com.htc.lib1.dm.logging;

import android.content.Context;

import com.htc.lib1.dm.logging.LogBuilder.ObjectFormatter;

class ContextFormatter implements ObjectFormatter {

    @Override
    public boolean append(StringBuilder builder, Object object) {
        if (object instanceof Context) {
            builder.append(object.getClass().getSimpleName());
            builder.append("{");
            builder.append(String.format("%h", object.hashCode()));
            builder.append("}");
            return true;
        }
        return false;
    }

}
