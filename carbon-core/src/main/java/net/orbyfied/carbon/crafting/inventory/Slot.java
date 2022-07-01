package net.orbyfied.carbon.crafting.inventory;

import net.orbyfied.carbon.item.CompiledStack;
import net.orbyfied.carbon.util.mc.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

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

    ItemStack     getBukkitItem();

    /**
     * Set the item in this slot.
     * @param stack The item.
     */
    void setItem(CompiledStack stack);

    /**
     * If this slot accepts the given
     * item stack. Used for filling.
     * Should check if the item stack is
     * similar (type and nbt same, but not amount),
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

    /**
     * Checks whether this slot is empty.
     * By default it checks if the item
     * provided by {@link Slot#getBukkitItem()}
     * is null or empty.
     * @see ItemUtil#isEmpty(CompiledStack)
     * @return If it is empty.
     */
    default boolean isEmpty() {
        return ItemUtil.isEmpty(getBukkitItem());
    }

    /**
     * To string method for debug purposes.
     * @return A debug string.
     */
    default String toDebugString() {
        return "Slot(virt: " + isVirtual() + ", idx: " + getIndex() + ", isEmpty: " + isEmpty() + ", item: " + getItem() + ")";
    }

    ///////////////////////////////////////////////////

    static Slot in(final Inventory inv,
                   final int index) {
        return new Slot() {

            @Override
            public boolean isVirtual() {
                return false; // real slot
            }

            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public CompiledStack getItem() {
                return new CompiledStack().wrap(inv.getItem(index));
            }

            @Override
            public ItemStack getBukkitItem() {
                return inv.getItem(index);
            }

            @Override
            public void setItem(CompiledStack stack) {
                inv.setItem(index, stack.getStack().getBukkitStack());
            }

            @Override
            public boolean accepts(CompiledStack stack) {
                return Slot.acceptsDefaultImpl(stack, inv.getItem(index));
            }

            @Override
            public CompiledStack add(CompiledStack stack) {
                return Slot.addDefaultImpl(stack, inv.getItem(index), s -> inv.setItem(index, s));
            }
        };
    }

    static Slot in(ItemStack[] arr, int i) {
        return new Slot() {
            @Override
            public boolean isVirtual() {
                return false;
            }

            @Override
            public int getIndex() {
                return i;
            }

            @Override
            public CompiledStack getItem() {
                return new CompiledStack().wrap(arr[i]);
            }

            @Override
            public ItemStack getBukkitItem() {
                return arr[i];
            }

            @Override
            public void setItem(CompiledStack stack) {
                arr[i] = stack.getBukkitStack();
            }

            @Override
            public boolean accepts(CompiledStack stack) {
                return Slot.acceptsDefaultImpl(stack, arr[i]);
            }

            @Override
            public CompiledStack add(CompiledStack stack) {
                return Slot.addDefaultImpl(stack, arr[i], s -> arr[i] = s);
            }
        };
    }

    static Slot getAndSet(final Consumer<ItemStack> setter,
                          final Supplier<ItemStack> getter) {
        return new Slot() {
            @Override
            public boolean isVirtual() {
                return true;
            }

            @Override
            public int getIndex() {
                return -1;
            }

            @Override
            public CompiledStack getItem() {
                return new CompiledStack().wrap(getter.get());
            }

            @Override
            public ItemStack getBukkitItem() {
                return getter.get();
            }

            @Override
            public void setItem(CompiledStack stack) {
                setter.accept(stack.getBukkitStack());
            }

            @Override
            public boolean accepts(CompiledStack stack) {
                return Slot.acceptsDefaultImpl(stack, getter.get());
            }

            @Override
            public CompiledStack add(CompiledStack stack) {
                return Slot.addDefaultImpl(stack, getter.get(), setter);
            }
        };
    }

    static Slot onlyAdd(final Consumer<ItemStack> setter) {
        return new Slot() {
            @Override
            public boolean isVirtual() {
                return true;
            }

            @Override
            public int getIndex() {
                return -1;
            }

            @Override
            public CompiledStack getItem() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ItemStack getBukkitItem() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setItem(CompiledStack stack) {
                setter.accept(stack.getBukkitStack());
            }

            @Override
            public boolean accepts(CompiledStack stack) {
                return true;
            }

            @Override
            public CompiledStack add(CompiledStack stack) {
                setter.accept(stack.getBukkitStack());
                return null;
            }
        };
    }

    ////////////////////////////////////////////////////

    private static boolean acceptsDefaultImpl(CompiledStack stack, ItemStack bss) {
        if (bss == null
                || bss.getAmount() == 0
                || bss.getType() == Material.AIR)
            return true;

        if (bss.getAmount() >= bss.getMaxStackSize())
            return false;
        // TODO: check if simalar better way
        if (!stack.getStack().getBukkitStack().isSimilar(bss))
            return false;
        return true;
    }

    private static CompiledStack addDefaultImpl(CompiledStack stack, ItemStack bss, Consumer<ItemStack> setter) {
        if (bss == null
                || bss.getAmount() == 0
                || bss.getType() == Material.AIR)
            setter.accept(stack.getStack().getBukkitStack());
        else {
            bss.setAmount(bss.getAmount() + stack.getAmount());
        }
        // TODO: calculate and return overflow
        return null;
    }

}
