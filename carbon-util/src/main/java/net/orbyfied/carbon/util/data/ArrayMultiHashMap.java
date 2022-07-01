package net.orbyfied.carbon.util.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ArrayMultiHashMap<K, V> {

    /**
     * The internal map to store all data.
     */
    private HashMap<K, ArrayList<V>> map = new HashMap<>();

    public int size() {
        return map.size();
    }

    public HashMap<K, ArrayList<V>> getMap() {
        return map;
    }

    public ArrayList<V> getAll(K key) {
        return map.get(key);
    }

    public ArrayList<V> getOrCreateAll(K key) {
        return map.computeIfAbsent(key, __ -> new ArrayList<>());
    }

    public ArrayMultiHashMap<K, V> addLast(K key, V val) {
        getOrCreateAll(key).add(val);
        return this;
    }

    public ArrayMultiHashMap<K, V> addFirst(K key, V val) {
        getOrCreateAll(key).add(0, val);
        return this;
    }

    public ArrayMultiHashMap<K, V> addAt(int index, K key, V val) {
        ArrayList<V> list = getOrCreateAll(key);
        list.add(Math.min(index, list.size() - 1), val);
        return this;
    }

}
