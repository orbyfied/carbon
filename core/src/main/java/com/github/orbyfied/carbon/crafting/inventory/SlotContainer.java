package com.github.orbyfied.carbon.crafting.inventory;

import com.github.orbyfied.carbon.item.CompiledStack;
import net.minecraft.world.item.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an array of slots which
 * can be filled linearly. Useful for
 * result slots with multiple items.
 * Basically a helper class.
 */
public class SlotContainer implements Iterable<Slot> {

    private List<Slot> slots;

    public SlotContainer(List<Slot> slots) {
        this.slots = new ArrayList<>(slots);
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public Slot getSlot(int i) {
        return slots.get(i);
    }

    public Slot addItem(CompiledStack stack) {
        CompiledStack left = stack;
        int l = slots.size();
        for (int i = 0; i < l; i++) {
            Slot slot = slots.get(i);
            if (slot.accepts(stack)) {
                left = slot.add(stack);
                if (left == null ||
                        left.getAmount() == 0 ||
                        left.getMinecraftItem() == Items.AIR)
                    return slot;
            }
        }

        return null;
    }

    @NotNull
    @Override
    public Iterator<Slot> iterator() {
        return slots.iterator();
    }
}
