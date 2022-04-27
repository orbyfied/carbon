package com.github.orbyfied.carbon.item.material;

import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.CompiledStack;
import com.github.orbyfied.carbon.item.ItemComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class MaterialItemComponent extends ItemComponent<CarbonItemState> {

    public MaterialItemComponent(CarbonItem<CarbonItemState> element) {
        super(element);
    }

    protected MaterialTag tag;

    public MaterialTag tag() {
        return tag;
    }

    public MaterialItemComponent tag(MaterialTag tag) {
        this.tag = tag;
        return this;
    }

    public MaterialItemComponent tag(String tag) {
        this.tag = MaterialTag.of(tag);
        return this;
    }

    public boolean is(String tag) {
        return this.tag.is(tag);
    }

    public boolean is(MaterialTag tag) {
        return this.tag.is(tag);
    }

    @Override
    public void build() {
        // nothing
    }

    @Override
    public void updateStack(CompiledStack stack, CarbonItemState state, CompoundTag tag) {
        // nothing x2
    }

}
