package com.github.orbyfied.carbon.item;

import net.minecraft.nbt.CompoundTag;

public class CarbonItemState<I extends CarbonItem<?>> {

    protected I item;

    public CarbonItemState(I item) {
        this.item = item;
    }

    public I getItem() {
        return item;
    }

    public void save(CompoundTag tag) {

    }

    public void load(CompoundTag tag) {

    }

}
