package com.github.orbyfied.carbon.command.impl;

import com.github.orbyfied.carbon.command.CommandEngine;
import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.Suggestions;
import com.github.orbyfied.carbon.command.minecraft.MinecraftParameterType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses the Bukkit command system to register
 * and execute/tab-complete commands.
 * TODO: find a way to get the message above the textbox
 */
public class BukkitCommandEngine extends CommandEngine {

    private static final SimpleCommandMap commandMap = (SimpleCommandMap) Bukkit.getCommandMap();

    public BukkitCommandEngine() {
        super();

        ((DelegatingNamespacedTypeResolver)getTypeResolver())
                .namespace("minecraft", MinecraftParameterType.typeResolver);
    }

    @Override
    protected void registerPlatform(Node root) {
        RegisteredBukkitCommand cmd = new RegisteredBukkitCommand(this, root);
        commandMap.register(cmd.getLabel(), cmd);
    }

    @Override
    protected void unregisterPlatform(Node root) {
        throw new UnsupportedOperationException();
    }

    private static String stitchArgs(String label, String[] args) {
        StringBuilder b = new StringBuilder(label);
        for (String s : args)
            b.append(" ").append(s);
        return b.toString();
    }

    private static Suggestions createSuggestions(List<String> list) {
        return new Suggestions() {
            @Override
            public Suggestions suggest(Object o) {
                if (o != null)
                    list.add(o.toString());
                return this;
            }

            @Override
            public Suggestions unsuggest(Object o) {
                if (o != null)
                    list.remove(o.toString());
                return null;
            }
        };
    }

    static class RegisteredBukkitCommand extends BukkitCommand {

        protected final CommandEngine engine;

        protected RegisteredBukkitCommand(CommandEngine engine,
                                          Node node) {
            super(node.getName(),
                    /* TODO */ "TODO",
                    /* TODO */ "TODO",
                    node.getAliases());
            this.engine = engine;
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
            String str = stitchArgs(alias, args);
            Context ctx = engine.dispatch(sender, str, null, null);
            return ctx.isSuccessful();
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, Location location) throws IllegalArgumentException {
            List<String> list = new ArrayList<>();
            String str = stitchArgs(alias, args);
            Context ctx = engine.dispatch(sender, str, createSuggestions(list), null);
            return list;
        }

    }

}
