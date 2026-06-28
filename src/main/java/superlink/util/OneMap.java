package superlink.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class OneMap<K,V> implements Map {

    public V value;
    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Object get(Object key) {
        return value;
    }

    @Override
    public Object put(Object key, Object value) {
        this.value= (V) value;
        return true;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set keySet() {
        return Collections.emptySet();
    }

    @Override
    public Collection values() {
        return Collections.emptyList();
    }

    @Override
    public Set<Entry> entrySet() {
        return Collections.emptySet();
    }
}
