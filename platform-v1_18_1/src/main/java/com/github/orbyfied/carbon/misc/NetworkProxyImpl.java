package com.github.orbyfied.carbon.misc;

import com.github.orbyfied.carbon.platform.NetworkProxy;
import com.github.orbyfied.carbon.util.mc.NMSHelper;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class NetworkProxyImpl implements NetworkProxy {

    private static final Class<?> craftEntityClass = NMSHelper.getCraftBukkitClass("entity.CraftEntity");
    private static final Field    craftEntityHandleField;

    static {
        Field tempCraftEntityHandleField;
        try {
            tempCraftEntityHandleField = craftEntityClass.getDeclaredField("entity");
            tempCraftEntityHandleField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            tempCraftEntityHandleField = null;
        }
        craftEntityHandleField = tempCraftEntityHandleField;
    }

    private static ServerPlayer getNmsPlayer(Player bukkitPlayer) {
        try {
            return (ServerPlayer) craftEntityHandleField.get(bukkitPlayer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnectionOf(Player bukkitPlayer) {
        return getNmsPlayer(bukkitPlayer).connection.getConnection();
    }

}
