package net.orbyfied.carbon.util.message.style;

import net.orbyfied.carbon.util.message.Context;
import net.orbyfied.carbon.util.message.Slice;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Styled extends Slice {

    Style style();

    Styled styled(Style style);

    Styled style(Consumer<? extends Style> consumer);

    default <T extends Style> Styled styled(Supplier<T> supplier, Consumer<T> consumer) {
        styled(supplier.get()).style(consumer);
        return this;
    }

    default void writeRaw(Context ctx, StringBuilder builder) { }

    @Override
    default void write(Context ctx, StringBuilder builder) {
        style().write(b -> writeRaw(ctx, b), builder);
    }

}
