package net.orbyfied.carbon.util.message.style.color;

import net.md_5.bungee.api.ChatColor;
import net.orbyfied.carbon.util.message.style.TextShader;

import java.awt.*;

public class GradientShader2DH implements TextShader {
    // TODO TODO TODO: var 2DH gradient shader
    boolean dirInverted = false;
    Color[] pointCols;
    float[] pointPos;

    @Override
    public ChatColor getColor(int x, int y, int w, int h, char c) {
        return null;
    }

    /////////////////////////

    public static GradientShader2DH gradient(boolean dirInverted, float[] poses, Color[] cols) {
        GradientShader2DH gs = new GradientShader2DH();
        gs.dirInverted = dirInverted;
        gs.pointCols   = cols;
        gs.pointPos    = poses;
        return gs;
    }

    public static GradientShader2DH gradient(boolean dirInverted, Object... points) {
        float[] poses = new float[points.length / 2];
        Color[] cols  = new Color[points.length / 2];

        float pos = 0;

        int l = points.length;
        for (int i = 0; i < l; i++) {
            int i2 = i / 2;
            Object p = points[i];
            if (i % 2 == 0) {
                pos = (float) p;
            } else {
                poses[i2] = pos;
                cols[i2]  = (Color) points[i];
            }
        }

        return gradient(dirInverted, poses, cols);
    }

}
