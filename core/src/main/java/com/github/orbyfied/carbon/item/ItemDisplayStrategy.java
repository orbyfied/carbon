package com.github.orbyfied.carbon.item;

import com.github.orbyfied.carbon.content.pack.ResourcePackBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class ItemDisplayStrategy extends ItemStrategy {

    public ItemDisplayStrategy(CarbonItem<?> item) {
        super(item);
    }

    public abstract void prepare();

    public abstract void makeAssets(ResourcePackBuilder builder);

    public abstract void makeItem(ItemStack stack,
                                  CarbonItemState<?> state,
                                  CompoundTag tag);

}
