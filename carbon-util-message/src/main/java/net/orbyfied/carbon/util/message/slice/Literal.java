package net.orbyfied.carbon.util.message.slice;

import net.orbyfied.carbon.util.message.Context;
import net.orbyfied.carbon.util.message.Slice;
import net.orbyfied.carbon.util.message.Sliced;
import net.orbyfied.carbon.util.message.style.Style;
import net.orbyfied.carbon.util.message.style.Styled;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Literal implements Slice, Styled {

    /**
     * The text to write.
     */
    String text;

    public Literal text(String text) {
        this.text = text;
        return this;
    }

    public String text() {
        return text;
    }

    @Override
    public void writeRaw(Context ctx, StringBuilder builder) {
        builder.append(text);
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
    public Literal styled(Style style) {
        this.style = style;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Literal style(Consumer<? extends Style> consumer) {
        ((Consumer<Style>)consumer).accept(style);
        return this;
    }

    @Override
    public <T extends Style> Literal styled(Supplier<T> supplier, Consumer<T> consumer) {
        Styled.super.styled(supplier, consumer);
        return this;
    }

}
