package net.orbyfied.carbon.util.functional;

import java.util.function.Function;

public interface Accumulator<T> {

    void add(T item);

    /////////////////////////////////////

    static <S, D> Accumulator<S> mapped(Accumulator<D> destination,
                                        Function<S, D> mapper) {
        return new Accumulator<>() {
            @Override
            public void add(S item) {
                destination.add(mapper.apply(item));
            }
        };
    }

}
