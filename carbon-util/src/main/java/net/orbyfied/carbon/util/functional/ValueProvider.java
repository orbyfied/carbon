package net.orbyfied.carbon.util.functional;

public interface ValueProvider<V> {

    void provideValues(Accumulator<V> acc);

}
