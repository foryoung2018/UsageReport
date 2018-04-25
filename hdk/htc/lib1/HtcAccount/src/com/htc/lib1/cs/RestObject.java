
package com.htc.lib1.cs;

import java.io.Serializable;

/**
 * The base class of all REST json objects.
 * 
 * @author samael_wang
 */
@SuppressWarnings("serial")
public abstract class RestObject implements Serializable {

    /**
     * Check if the content of the object is valid. When the object is parsed
     * and constructed from a JSON string, it helps to check if the content is
     * ready to use.
     * 
     * @return {@code true} if the content is valid.
     */
    public abstract boolean isValid();

    /**
     * Check if the content of the object is valid. Throws
     * {@link InvalidRestObjectException} if not.
     * 
     * @return {@code this}
     * @throws InvalidRestObjectException If the content is not consider valid.
     */
    public RestObject isValidOrThrow() throws InvalidRestObjectException {
        if (!isValid()) {
            throw new InvalidRestObjectException("The object " + getClass().getSimpleName() + " "
                    + toJsonString() + " is not valid.");
        }
        return this;
    }

    /**
     * Convert the object to JSON String.
     */
    public String toJsonString() {
        return JsonUtils.toJson(this);
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RestObject)
            return toJsonString().equals(((RestObject) o).toJsonString());
        return false;
    }

}
