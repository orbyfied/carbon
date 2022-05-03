package net.orbyfied.carbon.bootstrap;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.util.TextFormatting;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.function.BiConsumer;

/**
 * General branding and design for Carbon.
 */
public class CarbonBranding {

    /* Colors */
    public static final Color PRIMARY_COLOR_A = new Color(0x75eeea);
    public static final Color PRIMARY_COLOR_B = new Color(0x74ed7f);

    public static final Color SECONDARY_COLOR_A = new Color(0xEE7575);
    public static final Color SECONDARY_COLOR_B = new Color(0xEDCB74);

    public static final Color ICON_COLOR_A = new Color(0xF3F3F3);
    public static final Color ICON_COLOR_B = new Color(0x282828);

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
            " \\______/   " ;

    public static final String BRAND_ICON_FORMATTED =
            TextFormatting.gradient2d2fTlbr(BRAND_ICON_UNCOLORED,
                    ICON_COLOR_A,
                    ICON_COLOR_B);

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
