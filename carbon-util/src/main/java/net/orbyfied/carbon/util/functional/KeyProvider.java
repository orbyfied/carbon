package net.orbyfied.carbon.util.functional;

public interface KeyProvider<K> {

    void provideKeys(Accumulator<K> acc);

}
