package net.orbyfied.carbon.world;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.Level;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A world for Carbon data corresponding
 * to a Minecraft world.
 */
public class CarbonWorld {

    public static long getPositionCompound(int x, int z) {
        long c = 0;
        c |= z;
        c >>= 32 /* shift 32 bits (one int) to the right */;
        c |= x;
        return c;
    }

    public static IntIntPair getPositionFromCompound(long c) {
        int x = (int)c;
        c <<= 32;
        int z = (int)c;
        return IntIntPair.of(x, z);
    }

    ////////////////////////////////////////

    /**
     * The chunks mapped by position.
     * The position compound long is calculated
     * with a 64 bit number, the lower half 32 being
     * the X coordinate and the upper half being the
     * Z coordinate.
     */
    final Long2ObjectOpenHashMap<CarbonChunk> chunksByPosition = new Long2ObjectOpenHashMap<>(50);

    /**
     * A list of chunks. Order is not related to position
     * but rather to when they were loaded. (Last is latest
     * loaded)
     */
    final ArrayList<CarbonChunk> chunks = new ArrayList<>(50);

    /**
     * The NMS world handle.
     */
    final Level level;

    /**
     * The Bukkit world handle.
     */
    final World world;

    /**
     * The Carbon world manager.
     */
    final CarbonWorldManager manager;

    /**
     * If the world is currently loaded.
     */
    boolean isLoaded;

    public CarbonWorld(
            CarbonWorldManager manager,
            Level level
    ) {
        this.manager = manager;

        this.level = level;
        this.world = level.getWorld();
    }

    public CarbonChunk getChunk(long posc) {
        return chunksByPosition.get(posc);
    }

    public CarbonChunk getChunk(int x, int z) {
        return getChunk(getPositionCompound(x, z));
    }

    /* Getters. */

    public Long2ObjectOpenHashMap<CarbonChunk> getChunksByPosition() {
        return chunksByPosition;
    }

    public List<CarbonChunk> getChunks() {
        return Collections.unmodifiableList(chunks);
    }

    public Level getLevel() {
        return level;
    }

    public World getWorld() {
        return world;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

}
