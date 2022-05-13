package net.orbyfied.carbon.item;

import net.orbyfied.carbon.api.CarbonAPI;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Material;

import java.util.Objects;

public class CompiledStack {

    // TODO: this. is. fucking. shit.
    private static Registry<CarbonItem> itemRegistry;

    static void initializeApi(CarbonAPI api) {
        itemRegistry = api.getRegistries().getByIdentifier("minecraft:items");
    }

    static void disableApi(CarbonAPI api) {
        itemRegistry = null;
    }

    /////////////////////////////////////////

    protected ItemStack       stack;
    protected CarbonItemState state;

    public ItemStack getStack() {
        return stack;
    }

    public CarbonItemState getState() {
        return state;
    }

    public int getAmount() {
        if (stack == null)
            return 0;
        return stack.getCount();
    }

    @Deprecated
    public Material getType() {
        return stack.getBukkitStack().getType();
    }

    public Item getItemType() {
        if (stack == null)
            return null;
        return stack.getItem();
    }

    public org.bukkit.inventory.ItemStack getBukkitStack() {
        if (stack == null)
            return null;
        return stack.getBukkitStack();
    }

    public boolean isEmpty() {
        return stack == null ||
                stack.getItem() == Items.AIR ||
                stack.getCount() == 0;
    }

    public CompiledStack fill(Item item) {
        stack = new ItemStack(item);
        return this;
    }

    public CompiledStack fill(Item item, int count) {
        stack = new ItemStack(item, count);
        return this;
    }

    public CompiledStack fill(Material material) {
        stack = new ItemStack(ItemUtil.getItem(material));
        return this;
    }

    public CompiledStack fill(Material material, int count) {
        stack = new ItemStack(ItemUtil.getItem(material), count);
        return this;
    }

    public CompiledStack fill(CarbonItem item) {
        stack = item.newStack();
        return this;
    }

    public CompiledStack fill(CarbonItem item, int amount) {
        stack = item.newStack();
        stack.setCount(amount);
        return this;
    }

    public CompiledStack trust(CarbonItemState state) {
        this.state = state;
        return this;
    }

    public CompiledStack primitive(ItemStack nmsStack) {
        this.stack = nmsStack;
        return this;
    }

    public CompiledStack primitive(org.bukkit.inventory.ItemStack bukkitStack) {
        return primitive(ItemUtil.getHandle(bukkitStack));
    }

    public CompiledStack wrap(org.bukkit.inventory.ItemStack bukkitStack) {
        if (bukkitStack == null)
            return this;
        return wrap(ItemUtil.getHandle(bukkitStack));
    }

    public CompiledStack wrap(ItemStack nmsStack) {
        if (nmsStack == null)
            return this;

        // set stack
        this.stack = nmsStack;

        // get tag
        CompoundTag tag = nmsStack.getTag();
        if (tag == null)
            return this;

        // get and check item type
        String itemId = tag.getString("CarbonItemId");
        CarbonItem item = itemRegistry.getByIdentifier(itemId);
        if (item == null)
            return this;

        try {
            // load state
            state = item.loadState(nmsStack);
        } catch (Exception e) {
            // ignore
            return this;
        }

        // return
        return this;
    }
    
    /* ---- Object ---- */

    @Override
    public String toString() {
        if (stack == null)
            return "EMPTY null stack";
        return stack.getItem() + " x" + stack.getCount() + (state != null ? " { " + state + " }" : "") + " stack";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompiledStack stack1 = (CompiledStack) o;
        return ItemUtil.equalsNmsStack(stack, stack1.stack) && Objects.equals(state, stack1.state);
    }

    @Override
    public int hashCode() {
        int h = Objects.hash(state);
        h = h * 31 + ItemUtil.hashNmsStack(stack);
        return h;
    }

}
