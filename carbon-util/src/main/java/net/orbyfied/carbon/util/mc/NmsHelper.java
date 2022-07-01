package net.orbyfied.carbon.util.mc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

import static net.orbyfied.carbon.util.ReflectionUtil.getDeclaredFieldSafe;
import static net.orbyfied.carbon.util.ReflectionUtil.queryFieldSafe;

/**
 * Utilities for working with NMS.
 */
public class NmsHelper {

    /**
     * The Minecraft/CraftBukkit server version.
     */
    private static final String VERSION;

    /**
     * The CraftBukkit root package with the
     * correct version inlined. Used for
     * reflection.
     */
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

    /* ---- Player Handles ---- */

    private static Field playerHandleField = getDeclaredFieldSafe(
            getCraftBukkitClass("entity.CraftEntity"),
            "entity"
    );

    public static ServerPlayer getPlayerHandle(Player player) {
        return queryFieldSafe(player, playerHandleField);
    }

    /* ---- World Handles ---- */

    private static Field worldHandleField = getDeclaredFieldSafe(
            getCraftBukkitClass("CraftWorld"),
            "world"
    );

    public static ServerLevel getWorldHandle(World world) {
        return queryFieldSafe(world, worldHandleField);
    }

}
