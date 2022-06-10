package net.orbyfied.carbon.block;

import net.minecraft.nbt.CompoundTag;
import net.orbyfied.carbon.api.CarbonAPI;
import net.orbyfied.carbon.core.CarbonJavaAPI;
import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.nbt.CompoundTagSerializer;
import org.bukkit.block.Block;

import java.io.IOException;
import java.util.Objects;

public class CarbonBlockState<B extends CarbonBlock> {

    // block id tag
    public static final String BLOCK_ID_TAG = "BlockId";

    public CarbonBlockState(B block) {
        this.block = block;
    }

    /**
     * The block type of this block state.
     */
    protected B block;

    public B getBlock() {
        return block;
    }

    /**
     * Saves the current block state
     * into the NBT tag.
     * @param tag The NBT tag.
     */
    public void save(CompoundTag tag) {

    }

    /**
     * Loads the data from the NBT
     * tag into this block state.
     * @param tag The NBT tag.
     */
    public void load(CompoundTag tag) {

    }

    ///////////////////////////////////////

    public String propertiesToString() {
        return "";
    }

    @Override
    public String toString() {
        return "state " + block.getIdentifier() + ": " + propertiesToString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarbonBlockState<?> that = (CarbonBlockState<?>) o;
        return Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block);
    }

    /////////////////////////////////////////

    private static Registry<CarbonBlock> BLOCK_REGISTRY;

    static void initializeApi(CarbonAPI api) {
        BLOCK_REGISTRY = api.getRegistries().getByIdentifier("minecraft:blocks");
    }

    static void disableApi(CarbonAPI api) {
        BLOCK_REGISTRY = null;
    }

    public static final CompoundTagSerializer<CarbonBlockState> NBT_TAG_SERIALIZER = new CompoundTagSerializer<CarbonBlockState>() {

        @Override
        public void write(CompoundTag tag, CarbonBlockState v) throws IOException {
            tag.putString(BLOCK_ID_TAG, v.block.identifier.toString());
            v.save(tag);
        }

        @Override
        public CarbonBlockState read(CompoundTag tag) throws IOException {
            // try to get block id
            Identifier id = Identifier.of(tag.getString(BLOCK_ID_TAG));
            CarbonBlock block = BLOCK_REGISTRY.getByIdentifier(id);

            // allocate state

            return null;
        }

        @Override
        public CarbonBlockState copy(CarbonBlockState original) {
            return null;
        }

    };

}
