package com.github.orbyfied.carbon.item;

import com.github.orbyfied.carbon.api.CarbonAPI;
import com.github.orbyfied.carbon.registry.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;

public class CompiledStack {

    // TODO: this. is. fucking. shit.
    private static Registry<CarbonItem> itemRegistry;
    public static void initialize(CarbonAPI api) {
        itemRegistry = api.getRegistries().getByIdentifier("minecraft:items");
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
        return stack.getCount();
    }

    @Deprecated
    public Material getType() {
        return stack.getBukkitStack().getType();
    }

    public Item getMinecraftItem() {
        return stack.getItem();
    }

    public CompiledStack wrap(ItemStack nmsStack) {
        // get handle
        CompoundTag tag = nmsStack.getTag();
        if (tag == null)
            return null;

        // get and check item type
        String itemId = tag.getString("CarbonItemId");
        CarbonItem item = itemRegistry.getByIdentifier(itemId);
        if (item == null)
            return null;

        try {
            // load state
            state = item.loadState(nmsStack);
        } catch (Exception e) {
            // ignore
            return null;
        }

        // return
        return this;
    }

}
