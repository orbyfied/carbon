package net.orbyfied.carbon.world;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * Represents a chunk
 */
public class CarbonChunk {

    // dimension constants
    public static int FULL_MIN_Y = -64;
    public static int FULL_MAX_Y = 314;

    /**
     * The NMS world handle.
     */
    Level level;

    /**
     * The Bukkit world handle.
     */
    World world;

    /**
     * The lowest Y this chunk covers.
     */
    int minY;

    /**
     * The highest Y this chunk covers.
     */
    int maxY;

    /**
     * The size of this chunks full arrays.
     */
    int size;



    /* Getters. */

    public Level getLevel() {
        return level;
    }

    public World getWorld() {
        return world;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getInternalSize() {
        return size;
    }

}
