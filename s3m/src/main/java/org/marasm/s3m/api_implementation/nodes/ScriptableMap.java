package org.marasm.s3m.api_implementation.nodes;

import org.mozilla.javascript.Scriptable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ScriptableMap implements Scriptable, Map {
    public final Map map;

    public ScriptableMap(Map map) {
        this.map = map;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Set entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set keySet() {
        return map.keySet();
    }

    @Override
    public Object put(Object key, Object value) {
        return map.put(key, value);
    }

    @Override
    public void putAll(Map m) {
        map.putAll(m);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection values() {
        return map.values();
    }

    @Override
    public void delete(String name) {
        map.remove(name);
    }

    @Override
    public void delete(int index) {
        map.remove(index);
    }

    @Override
    public Object get(String name, Scriptable start) {
        return map.get(name);
    }

    @Override
    public Object get(int index, Scriptable start) {
        return map.get(index);
    }

    @Override
    public String getClassName() {
        return map.getClass().getName();
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        return toString();
    }

    @Override
    public Object[] getIds() {
        Object[] res = new Object[map.size()];
        int i = 0;
        for (Object k : map.keySet()) {
            res[i] = k;
            i++;
        }
        return res;
    }

    @Override
    public Scriptable getParentScope() {
        return null;
    }

    @Override
    public void setParentScope(Scriptable parent) {
    }

    @Override
    public Scriptable getPrototype() {
        return null;
    }

    @Override
    public void setPrototype(Scriptable prototype) {
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return map.containsKey(name);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return map.containsKey(index);
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return false;
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        map.put(name, value);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        map.put(index, value);
    }

    public Map getMap() {
        return map;
    }
}
