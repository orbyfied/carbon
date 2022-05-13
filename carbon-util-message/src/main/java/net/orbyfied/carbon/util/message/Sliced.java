package net.orbyfied.carbon.util.message;

import net.orbyfied.carbon.util.message.style.Style;
import net.orbyfied.carbon.util.message.style.Styled;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Sliced implements Styled {

    /**
     * The parts that this sliced message
     * is made up of.
     */
    protected List<Slice> slices = new ArrayList<>();

    public Sliced() { }

    public Sliced(List<Slice> slices) {
        this.slices = slices;
    }

    /* ---- Slice ---- */

    @Override
    public void writeRaw(Context ctx, StringBuilder builder) {
        for (Slice slice : slices) {
            slice.write(ctx.ascend(), builder);
            ctx.descend();
        }
    }

    @Override
    public String toString() {
        return getString();
    }

    public String toString(Object... params) {
        return getString(params);
    }

    /* ---- Sliced ---- */

    public <T extends Slice> Sliced append(Supplier<T> supplier, Consumer<T> consumer) {
        T it = supplier.get();
        slices.add(it);
        consumer.accept(it);
        return this;
    }

    /* ---- Styled ---- */

    Style style = Style.EMPTY;

    @Override
    public Style style() {
        return style;
    }

    @Override
    public Sliced styled(Style style) {
        this.style = style;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Sliced style(Consumer<? extends Style> consumer) {
        ((Consumer<Style>)consumer).accept(style);
        return this;
    }

    @Override
    public <T extends Style> Sliced styled(Supplier<T> supplier, Consumer<T> consumer) {
        Styled.super.styled(supplier, consumer);
        return this;
    }

}
