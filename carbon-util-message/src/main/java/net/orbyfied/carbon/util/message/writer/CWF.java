package net.orbyfied.carbon.util.message.writer;

import net.orbyfied.carbon.util.message.Context;

@FunctionalInterface
public interface CWF<S, T> {

    T serialize(Context ctx, MessageWriter<T, ?> writer, S source);

}
