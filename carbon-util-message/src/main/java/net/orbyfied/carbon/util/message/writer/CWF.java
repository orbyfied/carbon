package net.orbyfied.carbon.util.message.writer;

import net.orbyfied.carbon.util.message.Context;

public interface CWF<S, T> {

    T serialize(Context ctx, MessageWriter<T, ?> writer, S source);

}
