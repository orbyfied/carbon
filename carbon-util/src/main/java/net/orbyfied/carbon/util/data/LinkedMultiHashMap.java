package net.orbyfied.carbon.util.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Stores multiple elements of type V in a linked list
 * keyed by a key of type K.
 * @param <K>
 * @param <V>
 */
public class LinkedMultiHashMap<K, V> {

    /**
     * The internal map to store all data.
     */
    private HashMap<K, LinkedList<V>> map = new HashMap<>();

    public int size() {
        return map.size();
    }

    public HashMap<K, LinkedList<V>> getMap() {
        return map;
    }

    public LinkedList<V> getAll(K key) {
        return map.get(key);
    }

    public LinkedList<V> getOrCreateAll(K key) {
        return map.computeIfAbsent(key, __ -> new LinkedList<>());
    }

    public LinkedMultiHashMap<K, V> addLast(K key, V val) {
        getOrCreateAll(key).addLast(val);
        return this;
    }

    public LinkedMultiHashMap<K, V> addFirst(K key, V val) {
        getOrCreateAll(key).addFirst(val);
        return this;
    }

    public LinkedMultiHashMap<K, V> addAt(int index, K key, V val) {
        LinkedList<V> list = getOrCreateAll(key);
        list.add(Math.min(index, list.size() - 1), val);
        return this;
    }

}
