package net.orbyfied.carbon.util;

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

    @SuppressWarnings("unchecked")
    public static <T> T[] resize(T[] in, int size) {
        T[] r = (T[]) new Object[size];
        System.arraycopy(in, 0, r, 0, in.length);
        return r;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] remove(T[] in, int index) {
        if (index < 0 || index >= in.length)
            return in;
        T[] r = (T[]) new Object[in.length - 1];
        System.arraycopy(in, 0, r, 0, index - 1);
        System.arraycopy(in, index + 1, r, index, in.length - index + 1);
        return r;
    }

}
