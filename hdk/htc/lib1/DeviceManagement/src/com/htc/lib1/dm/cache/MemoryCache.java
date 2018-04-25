package com.htc.lib1.dm.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joe_Wu on 8/23/14.
 */
public class MemoryCache {

    private static MemoryCache sInstance;
    private Map<String,Object> cache = new HashMap<String, Object>();
    public static final String TTL_SURFIFX = "_ttl";

    public synchronized static MemoryCache getInstance() {
        if(sInstance==null)
            sInstance = new MemoryCache();
        return sInstance;
    }

    private MemoryCache() {}

    public boolean existCache(String key) {
        return cache.containsKey(key);
    }

    public boolean existCache(String key, Long ttl) {
        Long lastTime = getCache(Long.class, key + TTL_SURFIFX);
        if(lastTime!=null && ( System.currentTimeMillis() - lastTime - ttl ) > 0 ) {
            return true;
        }
        return false;
    }

    public <T> T getCache(Class<T> type, String key) {
        if(existCache(key)) {
            Object value = cache.get(key);
            if(value!=null && type.isAssignableFrom(value.getClass())){
                return (T)value;
            }
        }
        return null;
    }

    public <T> T getCache(Class<T> type, String key, Long ttl) {
        Long lastTime = getCache(Long.class, key + TTL_SURFIFX);
        if(lastTime!=null && ( System.currentTimeMillis() - lastTime - ttl ) > 0 ) {
            return getCache(type, key);
        }
        return null;
    }

    public void setCache(String key, Object value) {
        if(value!=null) {
            cache.put(key, value);
            cache.put(key + TTL_SURFIFX, System.currentTimeMillis());
        }
    }

    public void removeCache(String key) {
        if(cache.containsKey(key))
            cache.remove(key);
    }
}
