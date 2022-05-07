package net.orbyfied.carbon.util.mc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.orbyfied.carbon.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

import static net.orbyfied.carbon.util.ReflectionUtil.getDeclaredFieldSafe;
import static net.orbyfied.carbon.util.ReflectionUtil.queryFieldSafe;

public class NmsHelper {

    private static final String VERSION;
    private static final String CRAFT_BUKKIT_PACKAGE;

    static {

        VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
        CRAFT_BUKKIT_PACKAGE = "org.bukkit.craftbukkit." + VERSION + ".";

    }

    public static String getVersion() {
        return VERSION;
    }

    public static Class<?> getCraftBukkitClass(String relativeName) {
        try {
            return Class.forName(CRAFT_BUKKIT_PACKAGE + relativeName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Field playerHandleField = getDeclaredFieldSafe(
            getCraftBukkitClass("entity.CraftEntity"),
            "entity"
    );

    public static ServerPlayer getPlayerHandle(Player player) {
        return queryFieldSafe(player, playerHandleField);
    }

}
