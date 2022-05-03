package net.orbyfied.carbon.platform;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.entity.Player;

public interface NetworkProxy {

    Connection getConnectionOf(Player bukkitPlayer);

}
