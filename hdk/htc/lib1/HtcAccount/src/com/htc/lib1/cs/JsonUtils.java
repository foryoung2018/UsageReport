
package com.htc.lib1.cs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Helper class to serialize and deserialize json strings / classes using gson.
 * <p>
 * The naming follows a general rule: if a helper class includes only static
 * methods it's named as {@code Utils}, and if it needs an instance to work
 * (including singleton implementation) it's named as {@code Helper}.
 * </p>
 */
public class JsonUtils {
    /**
     * Parse the provided object into a JSON string.
     * 
     * @param obj Object to parse.
     * @return Converted JSON string.
     * @throws JsonParseException If an error occurs when converting the object
     *             to a json string. Note that it's a runtime exception.
     */
    public static String toJson(Object obj) throws JsonParseException {
        return allocGson().toJson(obj);
    }

    /**
     * @param json JSON string to parse the object from.
     * @param type Type of the object to convert the json string to. In most
     *            cases {@code Clazz.class} should work but if you're operating
     *            on a generate type, use {@code TypeToken}. For example, to
     *            work with {@code List<String>}, use
     *            {@code new TypeToken<List<String>>()}.
     * @return Parsed json object.
     * @throws JsonParseException If an error occurs when parsing the string.
     *             Note that it's a runtime exception.
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Type type) throws JsonParseException {
        return (T) allocGson().fromJson(json, type);
    }

    /**
     * Custom type adapter to serialize / deserialize {@link Enum} to / from
     * integers.
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private static class EnumAsIntegerTypeAdapter implements
            JsonSerializer<Enum>, JsonDeserializer<Enum>, InstanceCreator<Enum> {
        @Override
        public JsonElement serialize(Enum src, Type typeOfSrc,
                JsonSerializationContext context) {
            return new JsonPrimitive(src.ordinal());
        }

        @Override
        public Enum deserialize(JsonElement json, Type classOfT,
                JsonDeserializationContext context) throws JsonParseException {
            Class<Enum> enumClass = (Class<Enum>) classOfT;
            try {
                Method valuesMethod = enumClass.getMethod("values");
                Enum[] enums = (Enum[]) valuesMethod.invoke(null);
                return enums[json.getAsInt()];
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Enum createInstance(Type type) {
            Class<Enum> enumClass = (Class<Enum>) type;
            try {
                Method valuesMethod = enumClass.getMethod("values");
                Enum[] enums = (Enum[]) valuesMethod.invoke(null);
                return enums[0];
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    /**
     * Custom type adapter to serialize / deserialize {@link Date} objects.
     */
    private static class DateTypeAdapter implements JsonSerializer<Date>,
            JsonDeserializer<Date>
    {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc,
                JsonSerializationContext context) {
            return new JsonPrimitive("/Date(" + src.getTime() + ")/");
        }

        @Override
        public Date deserialize(JsonElement json, Type classOfT,
                JsonDeserializationContext context) throws JsonParseException {
            String dateStr = json.getAsString();
            String dateOnlyStr = dateStr.replace("/", "")
                    .replace("\"", "")
                    .replace("Date(", "")
                    .replace(")", "");

            String parts[] = dateOnlyStr.split("[+-]");
            Boolean positiveTimezoneOffset = (dateOnlyStr.indexOf("+") >= 0);

            long dateVal = Long.parseLong(parts[0]);

            if (parts.length > 1 && parts[1].length() == 4)
            {
                long offset = (Long.parseLong(parts[1].substring(0, 2)) * 60 * 60 * 1000) +
                        (Long.parseLong(parts[1].substring(2, 2)) * 60 * 1000);
                if (positiveTimezoneOffset)
                    offset *= -1;
                dateVal += offset;
            }

            return new Date(dateVal);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    /**
     * Get a gson instance with custom type adapters.
     * 
     * @return Gson instance.
     */
    @SuppressWarnings("rawtypes")
    public static Gson allocGson() {
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(Enum.class, new EnumAsIntegerTypeAdapter())
                .registerTypeHierarchyAdapter(Date.class, new DateTypeAdapter())
                .registerTypeHierarchyAdapter(ArrayList.class, new ArrayListTypeAdapter())
                .create();
    }

    /**
     * Custom type adapter to deserialize arrays.
     * 
     * @param <T>
     */
    private static class ArrayListTypeAdapter<T> implements JsonDeserializer<T> {

        @Override
        @SuppressWarnings("unchecked")
        public T deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            ArrayList<T> results = new ArrayList<T>();

            JsonArray array = json.getAsJsonArray();
            if (array.size() <= 0) {
                return null;
            } else {
                for (JsonElement element : array)
                {
                    results.add((T) (element.isJsonObject() ? element.getAsJsonObject() : element
                            .getAsString()));
                }
            }

            return (T) results;
        }

        public String toString() {
            return getClass().getSimpleName();
        }
    }
}
