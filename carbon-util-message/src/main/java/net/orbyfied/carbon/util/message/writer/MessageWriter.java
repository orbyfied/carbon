package net.orbyfied.carbon.util.message.writer;

import net.orbyfied.carbon.util.message.Context;

public abstract class MessageWriter<T, R> {

    private final Class<T> type;

    private Context context;

    public MessageWriter(Class<T> type) {
        this.type = type;
    }

    public MessageWriter<T, R> contextual(Context context) {
        this.context = context;
        return this;
    }

    public Context context() {
        return context;
    }

    /////////////////////////////////////////////

    public abstract MessageWriter<T, R> append(T component);

    public abstract R build();

    ////////////////////////////////////////////

    public MessageWriter<T, R> append(MessageWritable component) {
        // serialize component
        T res = ComponentWriter.serializeStatic(context, type, component, this);
        return append(res);
    }

}
