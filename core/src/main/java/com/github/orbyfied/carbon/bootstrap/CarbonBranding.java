package com.github.orbyfied.carbon.bootstrap;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.util.TextFormatting;
import net.md_5.bungee.api.ChatColor;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * General branding and design for Carbon.
 */
public class CarbonBranding {

    /* Colors */
    public static final Color PRIMARY_COLOR_A = new Color(0x75eeea);
    public static final Color PRIMARY_COLOR_B = new Color(0x74ed7f);

    public static final Color SECONDARY_COLOR_A = new Color(0xEE7575);
    public static final Color SECONDARY_COLOR_B = new Color(0xEDCB74);

    public static final Color ICON_COLOR_A = new Color(0xC6C6C6);
    public static final Color ICON_COLOR_B = new Color(0x353535);

    /**
     * Prefix.
     */
    public static final String PREFIX = TextFormatting.gradient1d2fLr("CARBON",
            PRIMARY_COLOR_A,
            PRIMARY_COLOR_B,
            false);

    public static final String BRAND_ICON_UNCOLORED =
            " $$$$$$\\  \n" +
            "$$  __$$\\ \n" +
            "$$ /  \\__|\n" +
            "$$ |       \n" +
            "$$ |       \n" +
            "$$ |  $$\\ \n" +
            "\\$$$$$$  |\n" +
            " \\______/ \n" ;

    public static final String BRAND_ICON_FORMATTED =
            TextFormatting.gradient2d2fTlbr(BRAND_ICON_UNCOLORED,
                    PRIMARY_COLOR_A,
                    PRIMARY_COLOR_B);

    public static final String BRAND_MESSAGE_FORMATTED =
            PREFIX + TextFormatting.gradient1d2fLr("v" + Carbon.VERSION, PRIMARY_COLOR_A.darker(), PRIMARY_COLOR_B.darker(), false) +
                    ChatColor.DARK_GRAY + " by " +
                    TextFormatting.gradient1d2fLr("orbyfied", SECONDARY_COLOR_A, SECONDARY_COLOR_B, false);

    /**
     * Calls the provided consumer with
     * the line number and the line content
     * for each line in a string.
     * @param text The text to go over.
     * @param lineConsumer The function to call.
     */
    public static void applyForEachLine(String text, BiConsumer<Integer, String> lineConsumer) {
        // split lines
        String[] lines = text.split(TextFormatting.NEWLINE_REGEX);

        // iterate over lines
        int l = lines.length;
        for (int i = 0; i < l; i++)
            lineConsumer.accept(i, lines[i]);
    }

}
