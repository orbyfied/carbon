package net.orbyfied.carbon.util.message;

import net.orbyfied.carbon.util.message.style.Style;
import net.orbyfied.carbon.util.message.style.Styled;
import net.orbyfied.carbon.util.message.writer.*;

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

    default String toString(Object... params) {
        return getString(params);
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

    default <T> T serialize(Context ctx, MessageWriter<T, ?> writer, Class<T> target) {
        // write string
        T it = ComponentWriter.serializeStatic(
                ctx,
                target,
                this,
                writer
        );

        if (it == null)
            return null;

        // apply transform if available
        if (this instanceof Styled styled) {
            Style style = styled.style();
            if (style != null)
                // apply transform
                it = ComponentTransform.transformStatic(
                        ctx, writer,
                        this, style, it
                );
        }

        // return
        return it;
    }

}
