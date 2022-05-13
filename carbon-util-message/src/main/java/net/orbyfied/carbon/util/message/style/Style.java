package net.orbyfied.carbon.util.message.style;

import net.md_5.bungee.api.ChatColor;
import net.orbyfied.carbon.util.TextFormatting;

import java.awt.*;
import java.util.function.Consumer;

public interface Style {

    void write(String text, StringBuilder dest);

    default void write(Consumer<StringBuilder> sbc, StringBuilder dest) {
        StringBuilder b = new StringBuilder();
        sbc.accept(b);

        write(b.toString(), dest);
    }

    ////////////////////////////////////

    Style EMPTY = (text, dest) -> dest.append(text);

    static BasicStyle basic() {
        return new BasicStyle();
    }

    static Style oneColor(ChatColor color) {
        return (text, dest) -> dest
                .append(color)
                .append(text);
    }

}
