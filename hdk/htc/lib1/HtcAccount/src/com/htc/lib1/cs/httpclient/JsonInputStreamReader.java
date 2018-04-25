
package com.htc.lib1.cs.httpclient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.htc.lib1.cs.JsonUtils;
import com.htc.lib1.cs.httpclient.HttpConnection.HttpInputStreamReader;

/**
 * Read the input stream and convert it to an object by {@link JsonUtils}. There
 * are two ways to use this class, either you can create a class derives the
 * template class so it can find the template type automatically, or you have to
 * pass the type of the template manually. For example, the following code
 * creates a {@link JsonInputStreamReader} works on {@code MyJsonClass} in the
 * same manner of how {@link TypeToken} works in gson.
 * 
 * <pre>
 * JsonInputStreamReader&lt;MyJsonClass&gt; reader =
 *         new JsonInputStreamReader&lt;MyJsonClass&gt;() {
 *         };
 * </pre>
 * 
 * The reason to create a subclass it that Java doesn't provide a way to get the
 * type of template parameter directly. However, unlike {@link TypeToken},
 * {@link JsonInputStreamReader} doesn't create functionally-equal types for
 * {@link ParameterizedType}, {@link GenericArrayType} and {@link WildcardType}.
 * If you want to use {@link TypeToken} directly in those cases, use
 * 
 * <pre>
 * JsonInputStreamReader&lt;MyJsonClass&gt; reader =
 *         new JsonInputStreamReader&lt;MyJsonClass&gt;(new TypeToken&lt;MyJsonClass&gt;() {
 *         }.getType());
 * </pre>
 * 
 * @author samael_wang@htc.com
 * @param <T>
 */
public class JsonInputStreamReader<T> implements HttpInputStreamReader<T> {
    private StringInputStreamReader mReader = new StringInputStreamReader();
    private Type mType;

    /**
     * Construct an instance which derives represented class from type
     * parameter.
     */
    public JsonInputStreamReader() {
        mType = getSuperclassTypeParameter(getClass());
    }

    /**
     * Construct an instance and set the type manually.
     * 
     * @param type Type to set. Must not be {@code null}.
     */
    public JsonInputStreamReader(Type type) {
        if (type == null)
            throw new IllegalArgumentException("'type' is null.");
        mType = type;
    }

    @Override
    public T readFrom(int statusCode, Map<String, List<String>> responseHeader,
            BufferedInputStream istream) throws IOException {
        try {
            return JsonUtils.fromJson(mReader.readFrom(statusCode, responseHeader, istream), mType);
        } catch (JsonParseException e) {
            throw new ParseResponseException(e.getMessage(), e);
        }
    }

    /**
     * Returns the type from super class's type parameter.
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return (parameterized.getActualTypeArguments()[0]);
    }
}
