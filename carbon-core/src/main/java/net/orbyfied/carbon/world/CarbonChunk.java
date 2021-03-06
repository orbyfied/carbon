package net.orbyfied.carbon.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.orbyfied.carbon.block.CarbonBlock;
import net.orbyfied.carbon.block.CarbonBlockState;
import net.orbyfied.carbon.logging.BukkitLogger;
import net.orbyfied.carbon.util.StringReader;
import net.orbyfied.carbon.util.nbt.CompoundObjectTag;
import net.orbyfied.carbon.util.nbt.Nbt;
import org.bukkit.World;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    final CarbonWorld world;

    /**
     * The NMS chunk handle.
     */
    final LevelChunk chunk;

    /**
     * The array of block states.
     * Will be reallocated and resized when needed.
     */
    CarbonBlockState[][] blockStates;

    /**
     * List of block states that should be updated
     * specially every tick.
     */
    final List<CarbonBlockState> tickableStates = new ArrayList<>();

    /**
     * Should this chunk be loaded?
     */
    boolean isLoaded;

    /**
     * Did this chunk finish loading and processing data.
     */
    boolean loadComplete;

    /**
     * The X index of this chunk.
     */
    final int cx;

    /**
     * The Z index of this chunk.
     */
    final int cz;

    /**
     * A cache for speed of saving.
     */
    CompoundTag cacheTag = new CompoundTag();

    /**
     * If it should use safe loading.
     * Features:
     * - Allocate a temporary, thread safe buffer of block states.
     */
    boolean safeLoad = true;

    public CarbonChunk(final CarbonWorld world, int x, int z, LevelChunk nmsChunk) {
        this.world = world;
        this.cx    = x;
        this.cz    = z;
        this.chunk = nmsChunk;

        blockStates = (CarbonBlockState[][]) Array.newInstance(CarbonBlockState[].class, 5);
    }

    /**
     * Is this chunk available for processing.
     * @return True/false.
     */
    public boolean isAvailable() {
        return isLoaded && loadComplete;
    }

    /**
     * Is this chunk supposed to be loaded.
     * @return True/false.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Has this chunk completed loading data.
     * @return True/false.
     */
    public boolean isLoadComplete() {
        return loadComplete;
    }

    /**
     * Get the chunk X of this chunk.
     * @return Chunk X.
     */
    public int getChunkX() {
        return cx;
    }

    /**
     * Get the chunk Z of this chunk.
     * @return Chunk Z.
     */
    public int getChunkZ() {
        return cz;
    }

    /**
     * Get the world X of this chunk.
     * @return World X.
     */
    public int getWorldX() {
        return cx * CHUNK_WIDTH;
    }

    /**
     * Get the world Z of this chunk.
     * @return World Z.
     */
    public int getWorldZ() {
        return cz * CHUNK_WIDTH;
    }

    /**
     * Get the index of a subsection by Y coordinate.
     * @param y The Y coordinate.
     * @return The sub index.
     */
    public int getSubIndex(int y) {
        return y / SUB_SECT_HEIGHT;
    }

    /**
     * Get the Y coordinate from the base of a subsection.
     * @param y The world Y coordinate.
     * @return The section Y coordinate.
     */
    public int getYInSub(int y) {
        return y % SUB_SECT_HEIGHT;
    }

    /**
     * Get the 1D index into a subsection.
     * @param x The X inside the chunk.
     * @param y The Y inside the subsection.
     * @param z The Z inside the chunk.
     * @return The 1D index.
     */
    public int get1dInSub(int x, /* y from the base of the section */ int y, int z) {
        return getYInSub(y) * CHUNK_WIDTH * CHUNK_WIDTH +
                z * CHUNK_WIDTH +
                x;
    }

    /**
     * Get a subsection by index.
     * @param idx The index.
     * @return The subsection.
     */
    public CarbonBlockState[] getSubSection(int idx) {
        return blockStates[idx];
    }

    /**
     * Get a subsection by world Y coordinate.
     * @param y The Y coordinate.
     * @return The subsection.
     * @see CarbonChunk#getSubIndex(int)
     */
    public CarbonBlockState[] getSubSectionByY(int y) {
        return getSubSection(getSubIndex(y));
    }

    /**
     * Get a subsection by index, if it doesn't exist
     * create, store and return it.
     * @param idx The index.
     * @return A subsection.
     */
    public CarbonBlockState[] getOrCreateSubSection(int idx) {
        CarbonBlockState[] sect = getSubSection(idx);
        if (sect == null) {
            sect = new CarbonBlockState[SUB_SECT_SIZE];
            blockStates[idx] = sect;
        }

        return sect;
    }

    /**
     * Get a subsection by Y coordinate, if it doesn't
     * exist create, store and return it.
     * @param y The world Y coordinate.
     * @return The subsection.
     * @see CarbonChunk#getOrCreateSubSection(int)
     * @see CarbonChunk#getSubIndex(int)
     */
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
        if (cacheTag != null)
            cacheTag.put(x + "," + y + "," + z, new CompoundObjectTag<>(state));
        return this;
    }

    public CarbonChunk loadAsync() {
        world.loadChunkAsync(this);
        return this;
    }

    public CarbonChunk saveAsync() {
        world.saveChunkAsync(this);
        return this;
    }

    /* Loading */

    public void unload() {
        isLoaded = false;
    }

    void loadBlockState(String loc, CompoundTag statesTag,
                        CarbonBlockState[][] states) {
        // parse location
        StringReader reader = new StringReader(loc, 0);
        int x = Integer.parseInt(reader.collect(c -> c != ','));
        int y = Integer.parseInt(reader.collect(c -> c != ','));
        int z = Integer.parseInt(reader.collect(             ));

        // load object
        CompoundObjectTag<CarbonBlockState> stateTag = Nbt.getOrLoadObject(statesTag, loc);
        if (stateTag == null)
            return;
        CarbonBlockState state = stateTag.getObject();

        // get subsection index
        int si = getYInSub(y);
        // get or create subsection
        CarbonBlockState[] subsect = states[si];
        if (subsect == null)
            states[si] = (subsect = new CarbonBlockState[CHUNK_WIDTH * CHUNK_WIDTH * SUB_SECT_HEIGHT]);
        // set state
        subsect[get1dInSub(x, y, z)] = state;
    }

    public synchronized void load() {
        // check if it should still load
        if (!isLoaded) return;

        // get manager reference
        // and other references
        final CarbonWorldManager manager = world.manager;
        final BukkitLogger logger = manager.logger;

        // get data file
        Path data = manager.getChunkNBTFile(this);
        // check if the file is available, of if the chunk is new
        if (!Files.exists(data))
            return;

        try {

            // open data file
            InputStream fileIn = Files.newInputStream(data);
            DataInput   dataIn = new DataInputStream(fileIn);

            // load data
            NbtAccounter accounter = new NbtAccounter(Integer.MAX_VALUE);
            CompoundTag dataTag = CompoundTag.TYPE.load(dataIn, 0, accounter);

            fileIn.close();

            // allocate or get state array
            CarbonBlockState[][] states;
            if (safeLoad)
                states = (CarbonBlockState[][]) Array.newInstance(CarbonBlockState[].class, 5);
            else
                states = blockStates;

            // get block states tag
            CompoundTag statesTag = dataTag.getCompound("BlockStates");
            for (Map.Entry<String, Tag> entry : statesTag.tags.entrySet()) {
                // load block state
                loadBlockState(entry.getKey(), statesTag, states);
            }

            // set array
            blockStates = states;

        } catch (IOException e) {
            // handle exception
            logger.errt("Failed to load chunk (" + cx + ", " + cz + ") (async)", e);
        }
    }

    /* Saving */

    public synchronized void save() {
        // get manager reference
        // and other references
        final CarbonWorldManager manager = world.manager;
        final BukkitLogger logger = manager.logger;

        // get data file
        Path data = manager.getChunkNBTFile(this);

        try {

            // create data file
            if (!Files.exists(data))
                Files.createFile(data);

            // open data file
            OutputStream fileOut = Files.newOutputStream(data);
            DataOutput   dataOut = new DataOutputStream(fileOut);

            // save data
            CompoundTag dataTag = new CompoundTag();
            dataTag.put("BlockStates", cacheTag);

            dataTag.write(dataOut);

            fileOut.close();

        } catch (IOException e) {
            // handle exception
            logger.errt("Failed to load chunk (" + cx + ", " + cz + ") (async)", e);
        }
    }

    /* Getters */

    public CarbonWorld getWorld() {
        return world;
    }

    public LevelChunk getChunk() {
        return chunk;
    }

}
