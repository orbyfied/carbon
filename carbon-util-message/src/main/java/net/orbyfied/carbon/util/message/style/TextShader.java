package net.orbyfied.carbon.util.message.style;

import net.md_5.bungee.api.ChatColor;

public interface TextShader {

    ChatColor getColor(int x, int y, int w, int h, char c);

}
