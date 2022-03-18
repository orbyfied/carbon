package com.github.orbyfied.carbon.util.json;

import com.github.orbyfied.carbon.util.StringReader;

import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class JsonArray implements JsonTable<Integer, Object> {

    protected List<Object> list;

    public JsonArray() {
        this.list = new ArrayList<>();
    }

    public JsonArray(List<Object> list) {
        this.list = list;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public JsonArray set(Integer i, Object o) {
        if (i >= list.size())
            list.add(o);
        else
            list.set(i, o);
        return this;
    }

    @Override
    public JsonArray remove(Integer integer) {
        list.remove((int)integer);
        return this;
    }

    @Override
    public JsonArray removeValue(Object o) {
        list.remove(o);
        return this;
    }

    @Override
    public JsonArray add(Object o) {
        list.add(o);
        return this;
    }

    @Override
    public int getInt(Integer integer) {
        return (int)list.get(integer);
    }

    @Override
    public long getLong(Integer integer) {
        return (long)list.get(integer);
    }

    @Override
    public short getShort(Integer integer) {
        return (short)list.get(integer);
    }

    @Override
    public char getChar(Integer integer) {
        return (char)list.get(integer);
    }

    @Override
    public byte getByte(Integer integer) {
        return (byte)list.get(integer);
    }

    @Override
    public double getDouble(Integer integer) {
        return (double)list.get(integer);
    }

    @Override
    public float getFloat(Integer integer) {
        return (float)list.get(integer);
    }

    @Override
    public String getString(Integer integer) {
        return (String)list.get(integer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K1, V1> JsonTable<K1, V1> getTable(Integer integer) {
        return (JsonTable<K1, V1>) list.get(integer);
    }

    @Override
    public JsonObject getObject(Integer integer) {
        return (JsonObject) list.get(integer);
    }

    @Override
    public JsonArray getArray(Integer integer) {
        return (JsonArray) list.get(integer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Integer integer) {
        return (T) list.get(integer);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
