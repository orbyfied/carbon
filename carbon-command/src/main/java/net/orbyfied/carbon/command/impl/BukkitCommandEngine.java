package net.orbyfied.carbon.command.impl;

import net.orbyfied.carbon.command.*;
import net.orbyfied.carbon.command.minecraft.MinecraftParameterType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses the Bukkit command system to register
 * and execute/tab-complete commands.
 * TODO: find a way to get the message above the textbox
 */
public class BukkitCommandEngine extends CommandEngine {

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
        RegisteredBukkitCommand cmd = new RegisteredBukkitCommand(this, root);
        commandMap.register(cmd.getLabel(), cmd);
    }

    @Override
    protected void unregisterPlatform(Node root) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enablePlatform() {

    }

    @Override
    public void disablePlatform() {
        
    }

    private static String stitchArgs(String label, String[] args) {
        StringBuilder b = new StringBuilder(label);
        for (String s : args)
            b.append(" ").append(s);
        return b.toString();
    }

    private static SuggestionAccumulator createSuggestionAccumulator(List<String> list) {
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

    static class RegisteredBukkitCommand extends BukkitCommand {

        protected final CommandEngine engine;
        protected final Node node;

        protected RegisteredBukkitCommand(CommandEngine engine,
                                          Node node) {

            super(node.getName(),
                    "",
                    "",
                    node.getAliases());

            // set fields
            this.engine = engine;
            this.node   = node;

            // set properties
            CommandProperties rcp = node.getComponentOf(CommandProperties.class);
            if (rcp != null) {
                if (rcp.description() != null)
                    this.setDescription(rcp.description());
                if (rcp.label() != null)
                    this.setLabel(rcp.label());
                if (rcp.usage() != null)
                    this.setUsage(rcp.usage());
            }
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
            String str = stitchArgs(alias, args);
            Context ctx = engine.dispatch(sender, str, null, null);
            return ctx.successful();
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, Location location) throws IllegalArgumentException {
            List<String> list = new ArrayList<>();
            String str = stitchArgs(alias, args);
            Context ctx = engine.dispatch(sender, str, createSuggestionAccumulator(list), null);
            return list;
        }

    }

}