package com.github.orbyfied.carbon.util.json;

public interface JsonTable<K, V> {

    boolean isArray();

    JsonTable<K, V> set(K k, V v);

    JsonTable<K, V> remove(K k);

    JsonTable<K, V> removeValue(V v);

    JsonTable<K, V> add(V v);

    int getInt(K k);

    long getLong(K k);

    short getShort(K k);

    char getChar(K k);

    byte getByte(K k);

    double getDouble(K k);

    float getFloat(K k);

    String getString(K k);

    <K1, V1> JsonTable<K1, V1> getTable(K k);

    JsonObject getObject(K k);

    JsonArray getArray(K k);

    <T extends V> T get(K k);

}
