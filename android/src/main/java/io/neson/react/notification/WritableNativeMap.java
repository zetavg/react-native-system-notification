package io.neson.react.notification;

import java.util.HashMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

public class WritableNativeMap extends HashMap implements WritableMap {

    @Override
    public boolean hasKey(String name) {
        return (this.get(name) != null);
    }

    @Override
    public boolean isNull(String name) {
        return (this.get(name) == null);
    }

    @Override
    public boolean getBoolean(String name) {
        return (boolean) this.get(name);
    }

    @Override
    public double getDouble(String name) {
        return (double) this.get(name);
    }

    @Override
    public int getInt(String name) {
        Number v = (Number) this.get(name);
        return (int) v.intValue();
    }

    @Override
    public String getString(String name) {
        return (String) this.get(name);
    }

    @Override
    public ReadableArray getArray(String name) {
        return (ReadableArray) this.get(name);
    }

    @Override
    public ReadableMap getMap(String name) {
        return (ReadableMap) this.get(name);
    }

    @Override
    public ReadableType getType(String name) { return null; }

    @Override
    public ReadableMapKeySetIterator keySetIterator() { return null; }

    @Override
    public void putNull(String key) {}

    @Override
    public void putBoolean(String key, boolean value) {
        this.put(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        this.put(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        this.put(key, value);
    }

    @Override
    public void putString(String key, String value) {
        this.put(key, value);
    }

    @Override
    public void putArray(String key, WritableArray value) {
        this.put(key, value);
    }

    @Override
    public void putMap(String key, WritableMap value) {
        this.put(key, value);
    }

    @Override
    public void merge(ReadableMap source) {}
}
