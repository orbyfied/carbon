package com.github.orbyfied.carbon.crafting.inventory;

import com.github.orbyfied.carbon.item.CompiledStack;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a slot which can
 * hold an item. Used for both
 * input and output.
 */
public interface Slot {

    /**
     * Get if this slot is virtual.
     * If so, it probably won't have a
     * real index or the index will be
     * an ID for virtual identification.
     * @return True/false.
     */
    boolean isVirtual();

    /**
     * Get the index of this slot.
     * Usually in the target inventory.
     * @return The index.
     */
    int getIndex();

    /**
     * Get the item in this slot.
     * @return The item.
     */
    CompiledStack getItem();

    /**
     * Set the item in this slot.
     * @param stack The item.
     */
    void setItem(CompiledStack stack);

    /**
     * If this slot accepts the given
     * item stack. Used for filling.
     * Should check if the item stack is
     * similar (type & nbt same, but not amount),
     * and if the slot hasn't reached it's
     * maximum capacity/stack size.
     * @return True/false.
     */
    boolean accepts(CompiledStack stack);

    /**
     * Should add the given item stack
     * to the contents of this slot. Usually
     * just increments the count by whatever.
     * Should return what couldn't be added.
     * @param stack The item to add.
     * @return What couldn't be added.
     */
    CompiledStack add(CompiledStack stack);

}
