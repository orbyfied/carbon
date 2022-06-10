package net.orbyfied.carbon.world;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.orbyfied.carbon.block.CarbonBlockState;
import org.bukkit.World;

import java.util.List;

/**
 * Represents a chunk in a Carbon data world.
 */
public class CarbonChunk {

    // dimension constants
    public static int FULL_MIN_Y  = -64;
    public static int FULL_MAX_Y  = 314;
    public static int CHUNK_WIDTH = 16;

    /**
     * The world handle.
     */
    CarbonWorld world;

    /**
     * The NMS chunk handle.
     */
    LevelChunk chunk;

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

    /**
     * The array of block states.
     * Will be reallocated and resized when needed.
     */
    CarbonBlockState[] blockStates;

    /**
     * List of block states that should be updated
     * specially every tick.
     */
    List<CarbonBlockState> tickableStates;

    /**
     * Resizes and reallocates the active
     * area of the chunk.
     * @param minY The minimum Y.
     * @param maxY The maximum Y.
     * @return This.
     *
     * TODO: allow shrinking of chunks (currently not possible)
     */
    public CarbonChunk reallocate(int minY, int maxY) {
        // store old min Y
        int oldMinY = minY;

        // set and calculate new size
        this.minY = Math.min(minY, FULL_MIN_Y);
        this.maxY = Math.max(maxY, FULL_MAX_Y);
        this.size = CHUNK_WIDTH * CHUNK_WIDTH * (maxY - minY);

        // calculate old block states index
        // and copy the old block states
        int oldBsPos = oldMinY * CHUNK_WIDTH * CHUNK_WIDTH;
        CarbonBlockState[] oldbs = blockStates;
        // allocate new block states array
        // with new size
        blockStates = new CarbonBlockState[size];
        // copy back old data
        System.arraycopy(oldbs, 0, blockStates, oldBsPos, oldbs.length);

        // return
        return this;
    }

    /* Getters. */

    public CarbonWorld getWorld() {
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

    public LevelChunk getChunk() {
        return chunk;
    }

}
