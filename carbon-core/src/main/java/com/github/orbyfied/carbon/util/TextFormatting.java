package com.github.orbyfied.carbon.util;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Pattern;

public class TextFormatting {

    public static final String NEWLINE_REGEX = Pattern.quote("\n");

    public static String translate(String text, String prefix, String hexprefix) {
        boolean enableHex = true;
        if (hexprefix == null)
            enableHex = false;
        if (prefix == null)
            throw new IllegalArgumentException("colorcode prefix cannot be null");
        if (text == null)
            throw new IllegalArgumentException("text cannot be null");
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while(i < text.length()){
            char current = text.charAt(i);

            int prefixEnd = Math.min(i + prefix.length(), text.length() - 1);
            int hexEnd = Math.min(i + (enableHex ? hexprefix.length() : 0), text.length() - 1);

            String prefixSpace = text.substring(i, prefixEnd);
            String hexSpace = enableHex ? text.substring(i, hexEnd) : "";
            char charAfterPrefix = text.charAt(prefixEnd);
            String x6 = "";
            if (enableHex)
                x6 = text.substring(hexEnd, Math.min(i+6+hexprefix.length(), text.length()));

            if (hexSpace.equals(hexprefix)) {
                if (!enableHex){
                    builder.append(current);
                    continue;
                }
                builder.append(net.md_5.bungee.api.ChatColor.of(new Color(Integer.parseInt(x6, 16))));
                i += hexprefix.length()+6;
            } else if (prefixSpace.equals(prefix)) {
                builder.append(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', "&"+charAfterPrefix));
                i += prefix.length()+1;
            } else {
                builder.append(current);
                i++;
            }
        }
        return builder.toString();
    }

    public static String rgbToHex(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        String out = "#" +
                Integer.toString(r, 16).toUpperCase() +
                Integer.toString(g, 16).toUpperCase() +
                Integer.toString(b, 16).toUpperCase();

        return out;
    }

    /**
     * Creates a 1D gradient from 2 points
     * positioned at the full left and right points.
     * Acronym: GRADIENT 1Dimensional 2Float Left Right
     * @param text The text to color.
     * @param from The color on the left.
     * @param to The color on the right.
     * @param checkForWhitespace If whitespace should be skipped.
     * @param formatting The formatting of each character.
     * @return The final string.
     */
    public static String gradient1d2fLr(String text, Color from, Color to, boolean checkForWhitespace, ChatColor... formatting) {
        // strip color from text
        text = ChatColor.stripColor(text);

        // calculate length and frame increase
        int len = text.length();
        float p = 1f / (len - 1);

        // get individual RGB values
        int r1 = from.getRed();
        int g1 = from.getGreen();
        int b1 = from.getBlue();

        int r2 = to.getRed();
        int g2 = to.getGreen();
        int b2 = to.getBlue();

        // initialize the extra formatting
        StringBuilder b = new StringBuilder();
        for (net.md_5.bungee.api.ChatColor color : formatting)
            b.append(color);
        String ef = b.toString();

        // create StringBuilder
        StringBuilder builder = new StringBuilder();

        // create frame variable and loop
        float frame = 0;
        for (int i = 0; i < len; i++) {
            // get character
            char c = text.charAt(i);

            // check for whitespace
            if (checkForWhitespace) if (c == ' ' || c == '\n' || c == '\t') continue;

            // interpolate
            Color col = colInterpolate(r1, g1, b1, r2, g2, b2, frame);

            // create ChatColor and append it with extra formatting
            net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(col);
            builder.append(color).append(ef).append(c);

            // increment frame
            frame += p;
        }

        // return string
        return builder.toString();
    }

    public static String gradient2d2fTlbr(String text, Color from, Color to, ChatColor... formatting) {
        // strip color from text
        text = ChatColor.stripColor(text);

        // split text
        String[] lines = text.split(NEWLINE_REGEX);

        // calculate length and frame increase
        int lenY = lines.length;
        float py = 1f / (lenY - 1);

        // get individual RGB values
        int r1 = from.getRed();
        int g1 = from.getGreen();
        int b1 = from.getBlue();

        int r2 = to.getRed();
        int g2 = to.getGreen();
        int b2 = to.getBlue();

        // initialize the extra formatting
        StringBuilder b = new StringBuilder();
        for (net.md_5.bungee.api.ChatColor color : formatting)
            b.append(color);
        String ef = b.toString();

        // create StringBuilder
        StringBuilder builder = new StringBuilder();

        // loop
        int ly = lines.length;
        String line;
        float fy = 0f;
        for (int y = 0; y < ly; y++) {
            line = lines[y];
            int ll = line.length();
            float fx = 0;
            float px = 1f / (line.length() - 1);
            for (int i = 0; i < ll; i++) {
                // get character
                char c = line.charAt(i);

                // interpolate
                float f = fx / 2 + fy / 2;
                Color col = colInterpolate(r1, g1, b1, r2, g2, b2, f);

                // create ChatColor and append it with extra formatting
                net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(col);
                builder.append(color).append(ef).append(c);

                // increment frame
                fx += px;
            }
            builder.append("\n");
            fy += py;
        }
                       // remove trailing newline
        return builder.delete(builder.length() - 1, builder.length()).toString();
    }

    // interpolation lets gooo
    private static Color colInterpolate(int r1, int g1, int b1, int r2, int g2, int b2, float frame) {
        float iframe = 1 - frame;

        float r1f = r1 * iframe;
        float g1f = g1 * iframe;
        float b1f = b1 * iframe;

        float r2f = r2 * frame;
        float g2f = g2 * frame;
        float b2f = b2 * frame;

        float red   = r1f + r2f;
        float green = g1f + g2f;
        float blue  = b1f + b2f;

        return new Color (red / 255, green / 255, blue / 255);
    }

}
