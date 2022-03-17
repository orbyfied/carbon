package com.github.orbyfied.carbon.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

public class ArrayUtil {

    public static <T> ArrayIterator<T> iterator(T[] arr) {
        Objects.requireNonNull(arr);
        return new ArrayIterator<>(arr);
    }

    public static <T> ArrayIterable<T> iterable(T[] arr) {
        Objects.requireNonNull(arr);
        return new ArrayIterable<>(arr);
    }

    public static class ArrayIterable<T> implements Iterable<T> {

        private final T[] arr;

        public ArrayIterable(T[] arr) {
            this.arr = arr;
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new ArrayIterator<>(arr);
        }
    }

    public static class ArrayIterator<T> implements Iterator<T> {
        private final T[] arr;
        private int len;
        private int idx;

        public ArrayIterator(final T[] arr) {
            this.arr = arr;
            this.len = arr.length;
            this.idx = 0;
        }

        @Override
        public boolean hasNext() {
            return idx < len - 1;
        }

        @Override
        public T next() {
            return arr[idx++];
        }
    }

}
