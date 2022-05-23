package net.orbyfied.carbon.util.message.writer;

import net.orbyfied.carbon.util.message.Context;

@FunctionalInterface
public interface CTF<C, E, T> {

    T transform(Context context,
                MessageWriter<T, ?> writer,
                C component,
                E effect,
                T serialized);

}
