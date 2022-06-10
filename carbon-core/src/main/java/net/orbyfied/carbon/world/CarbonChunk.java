package net.orbyfied.carbon.world;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.orbyfied.carbon.block.CarbonBlock;
import net.orbyfied.carbon.block.CarbonBlockState;
import org.bukkit.World;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Represents a chunk in a Carbon data world.
 */
@SuppressWarnings("rawtypes")
public class CarbonChunk {

    // dimension constants
    public static final int FULL_MIN_Y      = -64;
    public static final int FULL_MAX_Y      = 320;
    public static final int FULL_Y_RAN      = FULL_MAX_Y - FULL_MIN_Y;
    public static final int CHUNK_WIDTH     = 16;
    public static final int SUB_SECT_AMOUNT = 12;
    public static final int SUB_SECT_HEIGHT = FULL_Y_RAN / SUB_SECT_AMOUNT;
    public static final int SUB_SECT_SIZE   = CHUNK_WIDTH * CHUNK_WIDTH * SUB_SECT_HEIGHT;

    /**
     * The world handle.
     */
    CarbonWorld world;

    /**
     * The NMS chunk handle.
     */
    LevelChunk chunk;

    /**
     * The array of block states.
     * Will be reallocated and resized when needed.
     */
    CarbonBlockState[][] blockStates;

    /**
     * List of block states that should be updated
     * specially every tick.
     */
    List<CarbonBlockState> tickableStates;

    public CarbonChunk(final CarbonWorld world, int x, int z) {
        blockStates = (CarbonBlockState[][]) Array.newInstance(CarbonBlockState[].class, 5);
    }

    public int getSubIndex(int y) {
        return y / SUB_SECT_HEIGHT;
    }

    public int getYInSub(int y) {
        return y % SUB_SECT_HEIGHT;
    }

    public int get1dInSub(int x, /* y from the base of the section */ int y, int z) {
        return getYInSub(y) * CHUNK_WIDTH * CHUNK_WIDTH +
                z * CHUNK_WIDTH +
                x;
    }

    public CarbonBlockState[] getSubSection(int idx) {
        return blockStates[idx];
    }

    public CarbonBlockState[] getSubSectionByY(int y) {
        return getSubSection(getSubIndex(y));
    }

    public CarbonBlockState[] getOrCreateSubSection(int idx) {
        CarbonBlockState[] sect = getSubSection(idx);
        if (sect == null) {
            sect = new CarbonBlockState[SUB_SECT_SIZE];
            blockStates[idx] = sect;
        }

        return sect;
    }

    public CarbonBlockState[] getOrCreateSubSectionByY(int y) {
        return getOrCreateSubSection(getSubIndex(y));
    }

    public CarbonBlockState getBlockState(int x, int y, int z) {
        CarbonBlockState[] sect = getSubSectionByY(y);
        if (sect == null)
            return null;
        return sect[get1dInSub(x, getYInSub(y), z)];
    }

    public CarbonChunk setBlockState(int x, int y, int z, CarbonBlockState state) {
        CarbonBlockState[] sect = getOrCreateSubSectionByY(y);
        sect[get1dInSub(x, getYInSub(y), z)] = state;
        return this;
    }

    /* Getters. */

    public CarbonWorld getWorld() {
        return world;
    }

    public LevelChunk getChunk() {
        return chunk;
    }

}
