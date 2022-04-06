package com.github.orbyfied.carbon.command.impl;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.github.orbyfied.carbon.command.CommandEngine;
import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.SuggestionAccumulator;
import com.github.orbyfied.carbon.command.minecraft.MinecraftParameterType;
import com.github.orbyfied.carbon.event.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Uses the Bukkit command system to register
 * and execute/tab-complete commands.
 * TODO: find a way to get the message above the textbox
 */
public class BukkitCommandEngine extends CommandEngine
        implements Listener {

    private static final SimpleCommandMap commandMap = (SimpleCommandMap) Bukkit.getCommandMap();

    private final Plugin plugin;

    public BukkitCommandEngine(Plugin plugin) {
        super();

        this.plugin = plugin;

        ((DelegatingNamespacedTypeResolver)getTypeResolver())
                .namespace("minecraft", MinecraftParameterType.typeResolver);
    }

    @Override
    protected void registerPlatform(Node root) {
        commandMap.register(root.getName(), new EnginedCommand(root));
    }

    @Override
    protected void unregisterPlatform(Node root) { }

    @Override
    public void enablePlatform() {
        // register events
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private static SuggestionAccumulator createSuggestions(List<String> list) {
        return new SuggestionAccumulator() {
            @Override
            public SuggestionAccumulator suggest(Object o) {
                if (o != null)
                    list.add(o.toString());
                return this;
            }

            @Override
            public SuggestionAccumulator unsuggest(Object o) {
                if (o != null)
                    list.remove(o.toString());
                return null;
            }
        };
    }

    @EventHandler
    public void onPrepareCommand(AsyncTabCompleteEvent event) {
        // dispatch command
        Context ctx = this.dispatch(
                event.getSender(),
                event.getBuffer(),
                createSuggestions(event.getCompletions()),
                null
        );

        // handle result if the command was found
        if (ctx.getRootCommand() != null) {
            // set handled
            event.setHandled(true);

            // communicate with sender
            // TODO: set text above somehow
        }
    }

    @EventHandler
    public void onExecuteCommand(PlayerCommandPreprocessEvent event) {
        // dispatch command
        Context ctx = this.dispatch(
                event.getPlayer(),
                event.getMessage(),
                null,
                null
        );

        // handle result if the command was found
        if (ctx.getRootCommand() != null) {
            // set handled
            event.setCancelled(true);

            // communicate with sender
            event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Exception in command: " +
                    ctx.getIntermediateText());
        }
    }

    public static class EnginedCommand extends BukkitCommand {

        protected EnginedCommand(Node command) {
            super(
                    command.getName(),
                    /* TODO */ "",
                    /* TODO */ "",
                    command.getAliases()
            );
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
            return false;
        }

    }

}
