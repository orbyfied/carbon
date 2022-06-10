package net.orbyfied.carbon.world;

import net.orbyfied.carbon.block.CarbonBlockState;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;

/**
 * A block location in a world and chunk.
 * Optimized for fast access, may take up
 * a significant amount of memory.
 */
@SuppressWarnings("rawtypes")
public class FastBlockLocation extends BlockLocation {

    /**
     * Reference to the chunk for fast access.
     */
    WeakReference<CarbonChunk> chunkReference;

    /**
     * The index of the sub section of the chunk.
     * For fast access.
     */
    int subIndex;

    /**
     * The 1D index into the sub section.
     * For fast access.
     */
    int si1d;

    /**
     * Positions inside the chunk.
     * For fast access.
     */
    int ix, iz;

    /**
     * Constructor.
     *
     * @param world The world this position is in.
     * @param x     The positions X coordinate.
     * @param y     The positions Y coordinate.
     * @param z     The positions Z coordinate.
     */
    public FastBlockLocation(CarbonWorld world, int x, int y, int z) {
        super(world, x, y, z);
        recalculatePosition();
    }

    /**
     * Get the chunk that this location is in.
     * @return The chunk.
     */
    @Override
    public CarbonChunk getChunk() {
        return chunkReference.get();
    }

    /**
     * Get the chunk subsection this location is in.
     * @return The subsection array.
     */
    public CarbonBlockState[] getSubSection() {
        return chunkReference.get().getSubSection(subIndex);
    }

    /**
     * Get the index of the subsection.
     * @return The index.
     */
    public int getSubIndex() {
        return subIndex;
    }

    /**
     * Get the index inside the subsection.
     * @return The index.
     */
    public int getIndexInSub() {
        return si1d;
    }

    /**
     * Get the X coordinate inside the chunk.
     * @return The X.
     */
    public int getChunkX() {
        return ix;
    }

    /**
     * Get the Z coordinate inside the chunk.
     * @return The Z.
     */
    public int getChunkZ() {
        return iz;
    }

    /**
     * Get the block state at the position stored in this object.
     * @return The block state.
     */
    @Override
    public CarbonBlockState getBlockState() {
        return getSubSection()[si1d];
    }

    /**
     * Set the block state at the position stored in this object.
     * @param state The block state to set.
     * @return This.
     */
    @Override
    public FastBlockLocation setBlockState(CarbonBlockState state) {
        getSubSection()[si1d] = state;
        return this;
    }

    @Override
    public FastBlockLocation setXYZ(Vector vec) {
        super.setXYZ(vec);
        return this;
    }

    @Override
    public FastBlockLocation setXYZ(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        recalculatePosition();
        return this;
    }

    @Override public FastBlockLocation setX(int x) { super.setX(x); recalculatePosition(); return this; }
    @Override public FastBlockLocation setY(int y) { super.setY(y); recalculatePosition(); return this; }
    @Override public FastBlockLocation setZ(int z) { super.setZ(z); recalculatePosition(); return this; }

    // recalculates the position
    void recalculatePosition() {
        // get world
        CarbonWorld world = worldReference.get();

        // get chunk
        CarbonChunk chunk = world.getChunkAt(x, z);
        chunkReference = new WeakReference<>(chunk);

        // calculate coordinates in the chunk
        ix = x % CarbonChunk.CHUNK_WIDTH;
        iz = z % CarbonChunk.CHUNK_WIDTH;

        // get subsection index
        subIndex = chunk.getSubIndex(y);

        // get index into subsection
        si1d = chunk.get1dInSub(x, y % CarbonChunk.SUB_SECT_HEIGHT, z);
    }

    // checks if the chunk is loaded and available
    void checkChunkAlive() {
        if (chunkReference.get() == null) {
            int cx = x / CarbonChunk.CHUNK_WIDTH;
            int cz = z / CarbonChunk.CHUNK_WIDTH;
            throw new IllegalStateException("Chunk " + cx + "," + cz + " is unloaded");
        }
    }

}
