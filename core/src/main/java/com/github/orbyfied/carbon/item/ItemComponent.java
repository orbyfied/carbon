package com.github.orbyfied.carbon.item;

import com.github.orbyfied.carbon.element.AbstractElementComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class ItemComponent<S extends CarbonItemState>
        extends AbstractElementComponent<CarbonItem<S>> {

    public ItemComponent(CarbonItem<S> element) {
        super(element);
    }

    public abstract void updateStack(ItemStack stack,
                                     S state,
                                     CompoundTag tag);

}
