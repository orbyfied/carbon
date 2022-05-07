package net.orbyfied.carbon.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.orbyfied.carbon.util.nbt.CompoundTagSerializer;

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
     * @param stack The item stack.
     * @param tag The CarbonItemState NBT tag.
     */
    public void save(ItemStack stack,
                     CompoundTag tag) {

    }

    /**
     * Loads the dat from the NBT
     * tag into this item state.
     * @param stack The item stack.
     * @param tag The CarbonItemState NBT tag.
     */
    public void load(ItemStack stack,
                     CompoundTag tag) {

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

    public static final CompoundTagSerializer<CarbonItemState> COMPOUND_TAG_SERIALIZER = null; // TODO

}
