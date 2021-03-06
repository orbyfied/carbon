package net.orbyfied.carbon.crafting.inventory;

import net.orbyfied.carbon.item.CompiledStack;
import net.orbyfied.carbon.util.mc.ItemUtil;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    public int getSize() { return slots.size(); }

    public Slot addItem(CompiledStack stack) {
        CompiledStack left;
        int l = slots.size();
        for (int i = 0; i < l; i++) {
            Slot slot = slots.get(i);
            if (slot == null)
                continue;
            if (slot.accepts(stack)) {
                left = slot.add(stack);
                if (ItemUtil.isEmpty(left))
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

    @Override
    public String toString() {
        return slots.toString();
    }

    /////////////////////////////////////////////

    public static SlotContainer of(final List<Slot> slots) {
        return new SlotContainer(slots);
    }

    public static SlotContainer of(final Slot... slots) {
        return new SlotContainer(Arrays.asList(slots));
    }

    public static SlotContainer ofInventory(final Inventory inventory) {
        ArrayList<Slot> slots = new ArrayList<>();
        int l = inventory.getSize();
        for (int i = 0; i < l; i++)
            slots.add(Slot.in(inventory, i));

        return new SlotContainer(slots);
    }

    public static SlotContainer ofArray(final ItemStack[] arr) {
        ArrayList<Slot> slots = new ArrayList<>();
        int l = arr.length;
        for (int i = 0; i < l; i++)
            slots.add(Slot.in(arr, i));

        return new SlotContainer(slots);
    }

}
