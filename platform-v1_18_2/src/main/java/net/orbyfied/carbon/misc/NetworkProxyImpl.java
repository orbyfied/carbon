package net.orbyfied.carbon.misc;

import net.orbyfied.carbon.platform.NetworkProxy;
import net.orbyfied.carbon.util.mc.NmsHelper;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class NetworkProxyImpl implements NetworkProxy {

    private static final Class<?> craftEntityClass = NmsHelper.getCraftBukkitClass("entity.CraftEntity");
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
