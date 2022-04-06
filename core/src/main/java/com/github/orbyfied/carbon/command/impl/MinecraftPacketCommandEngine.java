package com.github.orbyfied.carbon.command.impl;

import com.github.orbyfied.carbon.command.CommandEngine;
import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.SuggestionAccumulator;
import com.github.orbyfied.carbon.util.mc.NmsHelper;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MinecraftPacketCommandEngine extends CommandEngine
        implements Listener {

    protected final Plugin plugin;

    public MinecraftPacketCommandEngine(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override protected void registerPlatform(Node root) { }
    @Override protected void unregisterPlatform(Node root) { }

    @Override
    public void enablePlatform() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disablePlatform() {

    }

    /* Behaviour. */

    public void injectCommandPacketHandler(Player player) {
        // get nms player
        final ServerPlayer playerHandle = NmsHelper.getPlayerHandle(player);

        // get connection
        final ServerGamePacketListenerImpl playerConnection = playerHandle.connection;
        final Connection directConnection = playerConnection.getConnection();

        // create listener
        CommandPacketHandler handler = new CommandPacketHandler(
                player,
                playerHandle,
                playerConnection
        );

        // add listener to channel pipeline
        directConnection.channel.pipeline()
                .addBefore("packet_handler", "Carbon_command_packet_interceptor", handler);
    }

    // for registering network handlers
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        injectCommandPacketHandler(event.getPlayer());
    }

    class CommandPacketHandler extends ChannelDuplexHandler {

        private final Player bukkitPlayer;
        private final ServerPlayer player;
        private final ServerGamePacketListenerImpl playerConnection;

        CommandPacketHandler(Player bukkitPlayer, ServerPlayer player, ServerGamePacketListenerImpl playerConnection) {
            this.bukkitPlayer = bukkitPlayer;
            this.player = player;
            this.playerConnection = playerConnection;
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            super.write(ctx, msg, promise);
        }

        @Override
        public void channelRead(ChannelHandlerContext c, Object m) {

            if (m instanceof ServerboundCommandSuggestionPacket packet) {
                String str = packet.getCommand();
                List<Suggestion> suggestionList = new ArrayList<>();
                SuggestionAccumulator acc = new SuggestionAccumulator() {
                    @Override
                    public SuggestionAccumulator suggest(Object o) {
                        if (o == null) return this;
                        String str = o.toString();
                        suggestionList.add(new Suggestion(
                                StringRange.between(0, str.length() - 1),
                                str,
                                new LiteralMessage(str)
                        ));
                        return this;
                    }

                    @Override
                    public SuggestionAccumulator unsuggest(Object o) {
                        throw new UnsupportedOperationException();
                    }
                };

                Suggestions suggestions = new Suggestions(StringRange.between(0, str.length() - 1),
                        suggestionList);

                Context ctx = MinecraftPacketCommandEngine.this.dispatch(
                        bukkitPlayer,
                        packet.getCommand(),
                        acc,
                        null
                );

                if (ctx.getRootCommand() != null) {
                    ClientboundCommandSuggestionsPacket result =
                            new ClientboundCommandSuggestionsPacket(0, suggestions);

                    // cancel
                    c.flush();
                }
            }

        }

    }

}
