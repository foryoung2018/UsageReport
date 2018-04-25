package com.htc.lib1.settings.provider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import android.content.ContentResolver;
import android.text.TextUtils;

public class SettingsReflectionUtil {
	public static final Class[] PARAMS_TYPE_VOID = new Class[]{};
	
	private static final String CLASS_NAME = SettingsReflectionUtil.class.getSimpleName();	
	
	private static final boolean STATIC_FINAL_FIELD_CACHE_ENABLED = true;
	private static HashMap<String, StaticFinalFieldCache> mStaticFinalFieldCacheMap = null;
	
	public static Class getClass(String className) { 
	    try {  
	        return Class.forName(className);
	    } catch (ClassNotFoundException e) {
	    	SettingsWrapperLog.logW("ClassNotFoundException: ", className);
	    	return null;
	    } catch (Exception e) {  
	    	SettingsWrapperLog.logW("getClass() Exception: ", e.getMessage());
	    	return null;
	    }  
	}
	
	private static Method getMethod(Class classOfInterest, String methodName, Class... parameterTypes) { 
		if(null==classOfInterest) {
			return null;
		}

		Method methodToAquire = null;
		try {
			methodToAquire = classOfInterest.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
	    	SettingsWrapperLog.logW("NoSuchMethodException: ", methodName);
	    	return null;
		}
		return methodToAquire;
	}
	
	private static Method getMethod(String className, String methodName, Class... parameterTypes) { 
		Class classOfInterest = getClass(className);
		if(null==classOfInterest) {
			return null;
		}
		return getMethod(classOfInterest, methodName, parameterTypes);
	}
	
	private static boolean isMethodStatic(Method method) {
		if(null==method) {
	    	SettingsWrapperLog.logW("isMethodStatic(): method==null");
			return false;
		}
		int modifiers = method.getModifiers();
		return Modifier.isStatic(modifiers);
	}
	
	private static boolean isFieldStaticFinal(Field field) {
		if(null==field) {
	    	SettingsWrapperLog.logW("isFieldStaticFinal(): field==null");
			return false;
		}
		int modifiers = field.getModifiers();
		return (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
	}
	
	public static Object invokeMethod(String className, String methodName, Class[] parameterTypes, Object... parameters) {
		return invokeMethod(null, className, methodName, parameterTypes, parameters);
	}
	
	public static Object invokeMethod(Object instance, String className, String methodName, Class[] parameterTypes, Object... parameters) {
		Class classOfInterest = getClass(className);
		if(null==classOfInterest) {
			return null;
		}
		Method method = getMethod(classOfInterest, methodName, parameterTypes);
		if(null==method) {
			return null;
		}
		
		if(null==instance) {
			try {
				instance = isMethodStatic(method)? null : classOfInterest.newInstance();
			} catch (InstantiationException e) {
		    	SettingsWrapperLog.logW("invokeMethod(): ",className,".",methodName,", InstantiationException: ", e.getMessage());
				return null;
			} catch (IllegalAccessException e) {
		    	SettingsWrapperLog.logW("invokeMethod(): ",className,".",methodName,", IllegalAccessException(when newInstance): ", e.getMessage());
				return null;
			}
		}
		
		Object returnedValue;
		try {
			returnedValue = method.invoke(instance, parameters);
		} catch (IllegalArgumentException e) {
	    	SettingsWrapperLog.logW("invokeMethod(): ",className,".",methodName,", IllegalArgumentException: ", e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
	    	SettingsWrapperLog.logW("invokeMethod(): ",className,".",methodName,", IllegalAccessException: ", e.getMessage());
			return null;
		} catch (InvocationTargetException e) {
	    	SettingsWrapperLog.logW("invokeMethod(): ",className,".",methodName,", InvocationTargetException: ", e.getMessage());
			return null;
		}
		return returnedValue;
	}
	
	private static class StaticFinalFieldCache {
		private boolean mIsFieldFound;
		private Object mValue;
		
		StaticFinalFieldCache(boolean isFieldFound, Object value) {
			mIsFieldFound = isFieldFound;
			mValue = value;
		}
		
		public boolean isFieldFound() {
			return mIsFieldFound;
		}

		public Object getValue() {
			return mValue;
		}
	}
	
	private static StaticFinalFieldCache getStaticFinalFieldCache(String className, String fieldName) {
		if(!STATIC_FINAL_FIELD_CACHE_ENABLED) {
			return null;
		}
		
		if(null==className || null==fieldName) {
			return null;
		}
		
		if(null==mStaticFinalFieldCacheMap) {
			return null;
		}
		
		String key = className+"."+fieldName;
		return mStaticFinalFieldCacheMap.get(key);
	}
	
	private synchronized static void setStaticFinalFieldCache(String className, String fieldName, StaticFinalFieldCache cache) {
		if(!STATIC_FINAL_FIELD_CACHE_ENABLED) {
			return;
		}

		if(null==className || null==fieldName) {
			return;
		}
		
		if(null==mStaticFinalFieldCacheMap) {
			mStaticFinalFieldCacheMap = new HashMap<String, StaticFinalFieldCache>();
		}
		String key = className+"."+fieldName;
		mStaticFinalFieldCacheMap.remove(key);
		mStaticFinalFieldCacheMap.put(key, cache);
	}
	
	public static Object getStaticFinalField(String className, String fieldName) {
		return getStaticFinalField(className, fieldName, null);
	}
	
	public static Object getStaticFinalField(String className, String fieldName, Object defaultValue) {
		// parameters check
		if(null==className || null==fieldName) {
			return defaultValue;
		}
		
		// see if it has cache
		StaticFinalFieldCache cache = getStaticFinalFieldCache(className, fieldName);
		if(cache!=null) {
			if(!cache.isFieldFound()) {
		    	SettingsWrapperLog.logW("getStaticFinalField(): ",className,".",fieldName,", field not found (cache)");
				return null;
			}

			Object value = cache.getValue();
			if(null==value) {
		    	SettingsWrapperLog.logW("getStaticFinalField(): ",className,".",fieldName,", field.get(null)==null");
		    	return defaultValue;
			}
			
			return value;
		}
		
		// no cache, do reflection for it
		Class classOfInterest = getClass(className);
		if(null==classOfInterest) {
			setStaticFinalFieldCache(className, fieldName, new StaticFinalFieldCache(false, null));
			return defaultValue;
		}
		
		Field field = null;
		try {
			field = classOfInterest.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
	    	SettingsWrapperLog.logW("getStaticFinalField(): ",className,".",fieldName,", NoSuchFieldException: ", e.getMessage());
			setStaticFinalFieldCache(className, fieldName, new StaticFinalFieldCache(false, null));
			return defaultValue;
		}
		
		if(null==field) {
			return defaultValue;
		}
		
		if(!isFieldStaticFinal(field)) {
	    	SettingsWrapperLog.logW("getStaticFinalField(): ",className,".",fieldName,", Not static final!");
			setStaticFinalFieldCache(className, fieldName, new StaticFinalFieldCache(false, null));
			return defaultValue;
		}
		
		Object value = null;
		try {
			value = field.get(null);
		} catch (IllegalArgumentException e) {
	    	SettingsWrapperLog.logW("getStaticFinalField(): ",className,".",fieldName,", IllegalArgumentException: ", e.getMessage());
			setStaticFinalFieldCache(className, fieldName, new StaticFinalFieldCache(false, null));
			return defaultValue;
		} catch (IllegalAccessException e) {
	    	SettingsWrapperLog.logW("getStaticFinalField(): ",className,".",fieldName,", IllegalAccessException: ", e.getMessage());
			setStaticFinalFieldCache(className, fieldName, new StaticFinalFieldCache(false, null));
			return defaultValue;
		}
		
		setStaticFinalFieldCache(className, fieldName, new StaticFinalFieldCache(true, value));

		if(null==value) {
	    	SettingsWrapperLog.logW("getStaticFinalField(): ",className,".",fieldName,", field.get(null)==null");
			return defaultValue;
		}

		return value;
	}
	
	public static int myUserId() {
		final String REFL_CLASS_NAME = "android.os.UserHandle";
		final String REFL_METHOD_NAME = "myUserId";
		return (Integer) invokeMethod(REFL_CLASS_NAME, REFL_METHOD_NAME, PARAMS_TYPE_VOID);
	}
	
	public static boolean TextUtils_delimitedStringContains (
            String delimitedString, char delimiter, String item) {
        if (TextUtils.isEmpty(delimitedString) || TextUtils.isEmpty(item)) {
            return false;
        }
        int pos = -1;
        int length = delimitedString.length();
        while ((pos = delimitedString.indexOf(item, pos + 1)) != -1) {
            if (pos > 0 && delimitedString.charAt(pos - 1) != delimiter) {
                continue;
            }
            int expectedDelimiterPos = pos + item.length();
            if (expectedDelimiterPos == length) {
                // Match at end of string.
                return true;
            }
            if (delimitedString.charAt(expectedDelimiterPos) == delimiter) {
                return true;
            }
        }
        return false;
    }

	public static String getPackageNameFromContentResolver(ContentResolver cr) {
		if(null==cr) {
			return null;
		}
		
        return (String) SettingsReflectionUtil.invokeMethod(
        		cr,
    			"android.content.ContentResolver", 
    			"getPackageName", 
    			SettingsReflectionUtil.PARAMS_TYPE_VOID);
	}
}
