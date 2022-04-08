package com.github.orbyfied.carbon.util;

import java.util.Iterator;
import java.util.function.Function;

public class CollectionUtil {

    public static <I, O> Iterator<O> mappedIterator(Iterator<I> iterator,
                                                    Function<I, O> mapper) {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public O next() {
                return mapper.apply(iterator.next());
            }
        };
    }

    public static <T> Iterator<T> iterate(T[] arr) {
        return new Iterator<>() {
            final int l = arr.length;
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < l - 1;
            }

            @Override
            public T next() {
                return arr[i++];
            }
        };
    }

}
