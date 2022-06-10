package net.orbyfied.carbon.item;

import net.minecraft.nbt.CompoundTag;
import net.orbyfied.carbon.api.CarbonAPI;
import net.orbyfied.carbon.core.CarbonJavaAPI;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.nbt.CompoundTagSerializer;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents the state and data of
 * an item stack. Can be serialized to
 * and deserialized from NBT.
 * @param <I> The item type.
 */
public class CarbonItemState<I extends CarbonItem> {

    /**
     * The item type of this state.
     */
    protected I item;

    /** Constructor. */
    public CarbonItemState(I item) {
        this.item = item;
    }

    /**
     * Get the item type of
     * the state.
     * @return The item type.
     */
    public I getItem() {
        return item;
    }

    /*
    Core NBT Storage Design
      -  tag  = { CarbonItemState: ... }
      -  item = { CarbonItemId: ... } and { CarbonItemState: { ItemId: ... } }
     */

    /**
     * Saves the current item state
     * into the NBT tag. The tag is
     * { CarbonItemState: ... } (the ...)
     * @param tag The CarbonItemState NBT tag.
     */
    public void save(CompoundTag tag) {

    }

    /**
     * Loads the data from the NBT
     * tag into this item state.
     * @param tag The CarbonItemState NBT tag.
     */
    public void load(CompoundTag tag) {

    }

    ////////////////////////////////////////

    public String propertiesToString() {
        return "";
    }

    @Override
    public String toString() {
        return "state of " + item + ": " + propertiesToString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarbonItemState<?> that = (CarbonItemState<?>) o;
        return Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    ////////////////////////////////////////

    private static Registry<CarbonItem> ITEM_REGISTRY;

    static void initializeApi(CarbonAPI api) {
        ITEM_REGISTRY = api.getRegistries().getByIdentifier("minecraft:items");
    }

    static void disableApi(CarbonAPI api) {
        ITEM_REGISTRY = null;
    }

    public static final CompoundTagSerializer<CarbonItemState> NBT_TAG_SERIALIZER = new CompoundTagSerializer<>() {

        @Override
        public void write(CompoundTag tag, CarbonItemState v) throws IOException {
            tag.putString(CarbonItem.ITEM_ID_TAG, v.item.getIdentifier().toString());
            v.save(tag);
        }

        @Override
        public CarbonItemState read(CompoundTag tag) throws IOException {
            if (ITEM_REGISTRY == null)
                ITEM_REGISTRY = CarbonJavaAPI.get().getMain().getRegistries().getByIdentifier("minecraft:items");

            String itemId = tag.getString(CarbonItem.ITEM_ID_TAG);
            CarbonItem item = ITEM_REGISTRY.getByIdentifier(itemId);

            CarbonItemState state = item.newState();
            state.load(tag);
            return state;
        }

        @Override
        public CarbonItemState copy(CarbonItemState original) {
            return new CarbonItemState(original.item);
        }

    };

}
