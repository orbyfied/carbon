package com.github.orbyfied.carbon.logging;

import com.github.orbyfied.carbon.util.ArrayUtil;
import com.mojang.datafixers.types.Func;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.awt.*;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Simple color supporting logger
 * for the Bukkit console.
 */
public class BukkitLogger {

    /**
     * The sender to use.
     */
    private final CommandSender sender;

    /**
     * The tag of the logger.
     */
    private final String tag;

    /**
     * The current stage of the logger.
     */
    private String stage;

    private boolean isDebug;

    private Function<Integer, ChatColor> colorMapper = DEFAULT_LEVEL_COLORS;

    private Function<Integer, String> nameMapper = DEFAULT_LEVEL_NAMES;

    public BukkitLogger(String tag) {
        this.tag    = tag;
        this.sender = Bukkit.getConsoleSender();
    }

    public BukkitLogger setColorMapper(Function<Integer, ChatColor> f) {
        this.colorMapper = f;
        return this;
    }

    public BukkitLogger setNameMapper(Function<Integer, String> f) {
        this.nameMapper = f;
        return this;
    }

    public BukkitLogger stage(String stage) {
        this.stage = stage;
        return this;
    }

    private String createLevelText(int level) {
        String name = nameMapper.apply(level);
        return colorMapper.apply(level) + "(" + name + ")";
    }

    private String createArrayString(Object[] arr) {
        StringBuilder b = new StringBuilder();
        int l = arr.length;
        for (int i = 0; i < l; i++) {
            if (i != 0)
                b.append(" ");
            b.append(arr[i]);
        }
        return b.toString();
    }

    private String createLoggingString(int level, Object... msg) {
        String msgstr;
        if (msg.length == 0)
            msgstr = "";
        else if (msg.length == 1)
            msgstr = Objects.toString(msg[0]);
        else
            msgstr = createArrayString(msg);
        return createLevelText(level) + " " + ChatColor.RESET + ChatColor.GRAY + "[ " + ChatColor.WHITE + tag
                + (stage != null ? ChatColor.DARK_GRAY + "/" + stage : "") + ChatColor.GRAY + " ] " + msgstr;
    }

    public BukkitLogger log(int level, Object msg) {
        sender.sendMessage(createLoggingString(level, msg));
        return this;
    }

    public BukkitLogger log(int level, String stage, Object msg) {
        return stage(stage).log(level, msg);
    }

    public BukkitLogger info(Object msg) {
        return log(-1, msg);
    }

    public BukkitLogger info(String stage, Object msg) {
        return stage(stage).log(-1, msg);
    }

    public BukkitLogger ok(Object msg) {
        return log(0, msg);
    }

    public BukkitLogger ok(String stage, Object msg) {
        return stage(stage).log(0, msg);
    }

    public BukkitLogger warn(Object msg) {
        return log(1, msg);
    }

    public BukkitLogger warn(String stage, Object msg) {
        return stage(stage).log(1, msg);
    }

    public BukkitLogger err(Object msg) {
        return log(2, msg);
    }

    public BukkitLogger err(String stage, Object msg) {
        return stage(stage).log(2, msg);
    }

    public BukkitLogger misc(Object msg) {
        return log(-2, msg);
    }

    public BukkitLogger misc(String stage, Object msg) {
        return stage(stage).log(-2, msg);
    }

    public BukkitLogger debug(Object msg) {
        return log(5, msg);
    }

    public BukkitLogger debug(String stage, Object msg) {
        return stage(stage).log(5, msg);
    }

    public BukkitLogger debugc(Object msg) {
        if (isDebug)
            debug(msg);
        return this;
    }

    public BukkitLogger debugc(Supplier<Object> supplier) {
        if (isDebug)
            debug(supplier.get());
        return this;
    }

    public BukkitLogger debugc(String stage, Object msg) {
        if (isDebug)
            debug(stage, msg);
        return this;
    }

    /* --------- DEFAULT LEVELS --------- */

    /*
       - -1: INFO
       -  0: OK
       -  1: WARN
       -  2: ERROR
     */

    public static final Function<Integer, String> DEFAULT_LEVEL_NAMES = level -> switch (level) {
        case -1 -> " info";
        case  0 -> "   ok";
        case  1 -> " warn";
        case  2 -> "  err";
        case  5 -> "debug";
        default -> " misc";
    };

    public static final ChatColor COLOR_DEBUG = colOf(new Color(0x5261CB));
    public static final ChatColor COLOR_INFO  = colOf(new Color(0x74badc));
    public static final ChatColor COLOR_OK    = colOf(new Color(0x72db84));
    public static final ChatColor COLOR_WARN  = colOf(new Color(0xc5db72));
    public static final ChatColor COLOR_ERR   = colOf(new Color(0xdb7272));
    public static final ChatColor COLOR_MISC  = colOf(new Color(0xC962D4));

    private static ChatColor colOf(Color color) {
        return ChatColor.of(color);
    }

    public static final Function<Integer, ChatColor> DEFAULT_LEVEL_COLORS = level -> switch (level) {
        case -1 -> COLOR_INFO;
        case  0 -> COLOR_OK  ;
        case  1 -> COLOR_WARN;
        case  2 -> COLOR_ERR ;
        case  5 -> COLOR_DEBUG;
        default -> COLOR_MISC;
    };

}
