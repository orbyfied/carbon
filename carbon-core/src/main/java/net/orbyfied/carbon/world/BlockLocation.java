package net.orbyfied.carbon.world;

import net.orbyfied.carbon.block.CarbonBlockState;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;

/**
 * A block location in a world and chunk.
 */
@SuppressWarnings("rawtypes")
public class BlockLocation {

    /**
     * Weak reference to the Carbon world instance.
     * A weak reference is used to prevent locations
     * keeping a world instance alive.
     */
    WeakReference<CarbonWorld> worldReference;

    /**
     * The position vector.
     * X and Z are horizontal, Y is vertical.
     */
    int x, y, z;

    /**
     * Constructor.
     * @param world The world this position is in.
     * @param x     The positions X coordinate.
     * @param y     The positions Y coordinate.
     * @param z     The positions Z coordinate.
     */
    public BlockLocation(
            CarbonWorld world,
            int x, int y, int z
    ) {
        // set the reference
        this.worldReference = new WeakReference<>(world);

        // set the position
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Get the world this location is in.
     * @return The world.
     */
    public final CarbonWorld getWorld() {
        return worldReference.get();
    }

    /**
     * Get the chunk this location is in.
     * @return The chunk.
     * @throws IllegalStateException If the world has been unloaded from memory.
     */
    public CarbonChunk getChunk() {
        checkWorldAlive();
        return worldReference.get().getChunkAt(x, z);
    }

    /**
     * Get the X coordinate in the world.
     * @return The block X coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Y coordinate in the world.
     * @return The block Y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Get the Z coordinate in the world.
     * @return The block Z coordinate.
     */
    public int getZ() {
        return z;
    }

    /**
     * Creates and returns a new 3D vector
     * with the X, Y and Z components of this
     * position ordered respectively.
     * @return The 3D vector.
     */
    public Vector getXYZ() {
        return new Vector(x, y, z);
    }

    /**
     * Stores the data from the given 3D
     * vector in this object.
     * @param vec The 3D vector.
     * @return This.
     */
    public BlockLocation setXYZ(Vector vec) {
        return setXYZ(
                vec.getBlockX(),
                vec.getBlockY(),
                vec.getBlockZ()
        );
    }

    /**
     * Stores the given components in
     * this object.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return This.
     */
    public BlockLocation setXYZ(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Set the X coordinate in the world.
     * @param x The X coordinate.
     * @return This.
     */
    public BlockLocation setX(int x) {
        this.x = x;
        return this;
    }

    /**
     * Set the Y coordinate in the world.
     * @param y The Y coordinate.
     * @return This.
     */
    public BlockLocation setY(int y) {
        this.y = y;
        return this;
    }

    /**
     * Set the Z coordinate in the world.
     * @param z The Z coordinate.
     * @return This.
     */
    public BlockLocation setZ(int z) {
        this.z = z;
        return this;
    }

    /**
     * Get the block state at the location
     * described by this object.
     * @return The block state.
     * @throws IllegalStateException If the world has been unloaded from memory.
     */
    public CarbonBlockState getBlockState() {
        checkWorldAlive();
        return worldReference.get().getBlockState(x, y, z);
    }

    /**
     * Put the given block state at the
     * location described by this object.
     * @param state The block state to set.
     * @return This.
     * @throws IllegalStateException If the world has been unloaded from memory.
     */
    public BlockLocation setBlockState(CarbonBlockState state) {
        checkWorldAlive();
        worldReference.get().setBlockState(x, y, z, state);
        return this;
    }

    // checks if the world is still loaded and available
    void checkWorldAlive() {
        if (worldReference.get() == null)
            throw new IllegalStateException("World has been unloaded");
    }

    /////////////////////////////

    public static FastBlockLocation fast(CarbonWorld world, int x, int y, int z) {
        return new FastBlockLocation(world, x, y, z);
    }

}
