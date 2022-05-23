package net.orbyfied.carbon.util.message;

import net.orbyfied.carbon.util.message.writer.MessageWritable;
import net.orbyfied.carbon.util.message.writer.MessageWriter;
import net.orbyfied.carbon.util.message.writer.StringMessageWriter;

public interface Slice extends MessageWritable {

    default String getString(Context context) {
        // create string builder
        StringMessageWriter writer = new StringMessageWriter()
                .contextual(context);

        // write
        writer.append(this);

        // return
        return writer.build();
    }

    default String getString() {
        return getString(new Context());
    }

    default String getString(Object... params) {
        Context context = new Context()
                .fill(params);
        return getString(context);
    }

    default <R> R write(MessageWriter<?, R> writer, Context context) {
        // configure writer
        writer.contextual(context);

        // write
        writer.append(this);

        // return
        return writer.build();
    }

    default <R> R write(MessageWriter<?, R> writer, Object... params) {
        return write(writer, new Context()
                .fill(params)
        );
    }

}
