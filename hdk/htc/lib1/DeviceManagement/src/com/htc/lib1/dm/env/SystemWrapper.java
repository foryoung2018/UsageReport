package com.htc.lib1.dm.env;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Build;

// Cribbed from com.htc.sphere.internal.SystemWrapper written by Tingwei Lin (tingwei_lin@htc.com).

/**
 * Support for accessing hidden system APIs.
 */
public class SystemWrapper {

  private static final boolean INTERNAL_DEBUG_FLAG = "userdebug".equals(Build.TYPE) || "eng".equals(Build.TYPE);

  /**
   * Utility class for accessing system properties.
   * <p>
   * Note that the key length is limited to 32 characters.
   * If the key length exceeds 32 characters, accessor methods will throw a {@link IllegalArgumentException}
   */
  public static class SystemProperties {

    /**
     * Get the value for the given key.
     * 
     * @param key property key
     * @return the value associated with the specified key or an empty string if the key isn't found
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static String get(String key) {
      // return android.os.SystemProperties.get(key);
      return invokePublicStaticMethod(
          "android.os.SystemProperties",
          "get",
          "",
          new Class<?>[] { String.class },
          new Object[] { key }
          );
    }

    /**
     * Get the value for the given key.
     * <p>
     * Note that if <code>def</code> is <code>null</code> and there is no value associated with
     * the specified key, the EMPTY string is returned (not <code>null</code>).
     * 
     * @param key property key
     * @param def a default value to return
     * @return the value associated with the specified key or the <code>def</code> if the key isn't found
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static String get(String key, String def) {
      // return android.os.SystemProperties.get(key, def);
      return invokePublicStaticMethod(
          "android.os.SystemProperties",
          "get",
          def,
          new Class<?>[] { String.class, String.class },
          new Object[] { key, def }
          );
    }

    /**
     * Get the value for the given key, returned as a boolean.
     * <p>
     * Values 'n', 'no', '0', 'false' or 'off' are considered false.
     * Values 'y', 'yes', '1', 'true' or 'on' are considered true.
     * (case sensitive).
     * <p>
     * If the key does not exist, or has any other value, then the default
     * result is returned.
     * 
     * @param key the key to lookup
     * @param def a default value to return
     * @return the value associated with key parsed as a boolean,
     *  or <code>def</code> if the key isn't found or the associated value
     *  cannot be parsed as a boolean.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static boolean getBoolean(String key, boolean def) {
      // return android.os.SystemProperties.getBoolean(key, def);
      return invokePublicStaticMethod(
          "android.os.SystemProperties",
          "getBoolean",
          def,
          new Class<?>[] { String.class, boolean.class },
          new Object[] { key, def }
          );
    }

    /**
     * Get the value for the given key, and return as an integer.
     * 
     * @param key the key to lookup
     * @param def a default value to return
     * @return the value associated with key parsed as an integer,
     *  or <code>def</code> if the key isn't found or the associated value
     *  cannot be parsed as an integer.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static int getInt(String key, int def) {
      // return android.os.SystemProperties.getInt(key, def);
      return invokePublicStaticMethod(
          "android.os.SystemProperties",
          "getInt",
          def,
          new Class<?>[] { String.class, int.class },
          new Object[] { key, def }
          );
    }

    /**
     * Get the value for the given key, and return as a long.
     * 
     * @param key the key to lookup
     * @param def a default value to return
     * @return the value associated with key parsed as a long,
     *  or <code>def</code> if the key isn't found or the associated value
     *  cannot be parsed as a long.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static long getLong(String key, long def) {
      // return android.os.SystemProperties.getLong(key, def);
      return invokePublicStaticMethod(
          "android.os.SystemProperties",
          "getLong",
          def,
          new Class<?>[] { String.class, long.class },
          new Object[] { key, def }
          );
    }
  }

  private static void printStackTrace(Throwable t) {
    if ( INTERNAL_DEBUG_FLAG ) {
      t.printStackTrace();
    }
  }

//  @SuppressWarnings("unchecked")
//  private static <T> T getPublicStaticField(String fullClassName, String fieldName, T defaultFieldValue) {
//    if ( fullClassName == null || fieldName == null ) {
//      return defaultFieldValue;
//    }
//    try {
//      Class<?> clazz = Class.forName(fullClassName);
//      Field field = clazz.getField(fieldName);
//      return (T) field.get(null);
//    } catch (ClassNotFoundException e) {
//      printStackTrace(e);
//    } catch (NoSuchFieldException e) {
//      printStackTrace(e);
//    } catch (IllegalArgumentException e) {
//      printStackTrace(e);
//    } catch (IllegalAccessException e) {
//      printStackTrace(e);
//    }
//    return defaultFieldValue;
//  }

  @SuppressWarnings("unchecked")
  private static <T> T invokePublicStaticMethod(String fullClassName, String methodName,
      T defaultReturnValue, Class<?>[] parameterTypes, Object[] parameterValues) {
    if ( fullClassName == null || methodName == null ) {
      return defaultReturnValue;
    }
    try {
      Class<?> clazz = Class.forName(fullClassName);
      Method method = clazz.getMethod(methodName, parameterTypes);
      return (T) method.invoke(null, parameterValues);
    } catch (ClassNotFoundException e) {
      printStackTrace(e);
    } catch (NoSuchMethodException e) {
      printStackTrace(e);
    } catch (IllegalArgumentException e) {
      printStackTrace(e);
    } catch (IllegalAccessException e) {
      printStackTrace(e);
    } catch (InvocationTargetException e) {
      printStackTrace(e);
    }
    return defaultReturnValue;
  }
}
