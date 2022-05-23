package net.orbyfied.carbon.util.message.style.color;

import net.md_5.bungee.api.ChatColor;
import net.orbyfied.carbon.util.message.style.TextShader;

import java.awt.*;

public class Shaders {

    private static Color colInterpolate(int r1, int g1, int b1,
                                        int r2, int g2, int b2,
                                        float frame) {
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

    /////////////////////////////////

    public static TextShader oneColor(Color color) {
        final ChatColor cc = ChatColor.of(color);
        return (x, y, w, h, c) -> cc;
    }

    public static TextShader oneColor(ChatColor color) {
        return (x, y, w, h, c) -> color;
    }

    /**
     * Creates a 1D gradient from 2 points
     * positioned at the full left and right points.
     * It 'flows' from left to right.
     * Acronym: GRADIENT 1Dimensional 2Float Left Right
     * @param from The color on the left.
     * @param to The color on the right.
     * @return The gradient shader.
     */
    public static TextShader gradient1d2fLr(final Color from, final Color to) {
        final int r1 = from.getRed();
        final int g1 = from.getGreen();
        final int b1 = from.getBlue();

        final int r2 = from.getRed();
        final int g2 = from.getGreen();
        final int b2 = from.getBlue();

        return (x, y, w, h, c) -> {
            float frame = (float)x / w;
            return ChatColor.of(colInterpolate(
                    r1, g1, b1,
                    r2, g2, b2,
                    frame
            ));
        };
    }

}
