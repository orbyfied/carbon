package net.orbyfied.carbon.util.message.slice;

import net.orbyfied.carbon.util.message.Context;
import net.orbyfied.carbon.util.message.Sliced;
import net.orbyfied.carbon.util.message.style.Style;
import net.orbyfied.carbon.util.message.style.Styled;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Placeholder implements Styled {

    /**
     * The key of the value to look up.
     */
    String key;

    /**
     * What to use if the value is null.
     */
    Object or;

    public Placeholder key(String key) {
        this.key = key;
        return this;
    }

    public String key() {
        return key;
    }

    public Placeholder or(Object or) {
        this.or = Objects.toString(or);
        return this;
    }

    public Object or() {
        return or;
    }

    @Override
    public void writeRaw(Context ctx, StringBuilder builder) {
        Object val = ctx.value(key);
        if (val == null)
            val = or;
        builder.append(val);
    }

    @Override
    public String toString() {
        return getString();
    }

    public String toString(Object... params) {
        return getString(params);
    }

    /* ---- Styled ---- */

    Style style = Style.EMPTY;

    @Override
    public Style style() {
        return style;
    }

    @Override
    public Placeholder styled(Style style) {
        this.style = style;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Placeholder style(Consumer<? extends Style> consumer) {
        ((Consumer<Style>)consumer).accept(style);
        return this;
    }

    @Override
    public <T extends Style> Placeholder styled(Supplier<T> supplier, Consumer<T> consumer) {
        Styled.super.styled(supplier, consumer);
        return this;
    }

}
