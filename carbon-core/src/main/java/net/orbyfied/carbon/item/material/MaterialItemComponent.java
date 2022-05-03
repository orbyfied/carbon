package net.orbyfied.carbon.item.material;

import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;
import net.orbyfied.carbon.item.CompiledStack;
import net.orbyfied.carbon.item.ItemComponent;
import net.minecraft.nbt.CompoundTag;

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
