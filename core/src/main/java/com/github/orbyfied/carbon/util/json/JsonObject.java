package com.github.orbyfied.carbon.util.json;

import com.github.orbyfied.carbon.util.StringReader;

import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.StringJoiner;

public class JsonObject implements JsonTable<String, Object> {

    protected HashMap<String, Object> map;

    public JsonObject() {
        map = new HashMap<>();
    }

    public JsonObject(HashMap<String, Object> map) {
        this.map = map;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    public JsonObject set(String s, Object o) {
        map.put(s, o);
        return this;
    }

    @Override
    public JsonObject remove(String s) {
        map.remove(s);
        return this;
    }

    @Override
    public JsonTable<String, Object> removeValue(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonTable<String, Object> add(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInt(String s) {
        return (int) map.get(s);
    }

    @Override
    public long getLong(String s) {
        return (long) map.get(s);
    }

    @Override
    public short getShort(String s) {
        return (short) map.get(s);
    }

    @Override
    public char getChar(String s) {
        return (char) map.get(s);
    }

    @Override
    public byte getByte(String s) {
        return (byte) map.get(s);
    }

    @Override
    public double getDouble(String s) {
        return (double) map.get(s);
    }

    @Override
    public float getFloat(String s) {
        return (float) map.get(s);
    }

    @Override
    public String getString(String s) {
        return (String) map.get(s);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K1, V1> JsonTable<K1, V1> getTable(String s) {
        return (JsonTable<K1, V1>) map.get(s);
    }

    @Override
    public JsonObject getObject(String s) {
        return (JsonObject) map.get(s);
    }

    @Override
    public JsonArray getArray(String s) {
        return (JsonArray) map.get(s);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String s) {
        return (T) map.get(s);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
