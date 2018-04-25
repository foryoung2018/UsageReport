package com.htc.lib1.htcmp4parser.coremedia.iso.boxes.mdat;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A SortedSet that contains just one value.
 *  @hide
 * {@exthide}
 */
public class DummyMap<K, V> implements Map<K, V> {
    /**
     * @hide
     */
    HashSet<K> keys = new HashSet<K>();
    /**
     * @hide
     */
    V value;
    /**
     * @hide
     */
    public DummyMap(V value) {
        this.value = value;
    }
    /**
     * @hide
     */
    public Comparator<? super K> comparator() {
        return null;  // I don't have any
    }
    /**
     * @hide
     */
    public void addKeys(K[] keys) {
        Collections.addAll(this.keys, keys);

    }
    /**
     * @hide
     */
    public int size() {
        return keys.size();
    }
    /**
     * @hide
     */
    public boolean isEmpty() {
        return keys.isEmpty();
    }
    /**
     * @hide
     */
    public boolean containsKey(Object key) {
        return keys.contains(key);
    }
    /**
     * @hide
     */
    public boolean containsValue(Object value) {
        return this.value == value;
    }
    /**
     * @hide
     */
    public V get(Object key) {
        return keys.contains(key) ? value : null;
    }
    /**
     * @hide
     */
    public V put(K key, V value) {
        assert this.value == value;
        keys.add(key);
        return this.value;
    }
    /**
     * @hide
     */
    public V remove(Object key) {
        V v = get(key);
        keys.remove(key);
        return v;
    }
    /**
     * @hide
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K k : m.keySet()) {
            assert m.get(k) == value;
            this.keys.add(k);
        }
    }
    /**
     * @hide
     */
    public void clear() {
        keys.clear();
    }
    /**
     * @hide
     */
    public Set<K> keySet() {
        return keys;
    }
    /**
     * @hide
     */
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }
    /**
     * @hide
     */
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
