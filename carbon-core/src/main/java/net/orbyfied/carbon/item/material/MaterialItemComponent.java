package net.orbyfied.carbon.item.material;

import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;
import net.orbyfied.carbon.item.CompiledStack;
import net.orbyfied.carbon.item.ItemComponent;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class MaterialItemComponent extends ItemComponent<CarbonItemState> {

    public MaterialItemComponent(CarbonItem<CarbonItemState> element) {
        super(element);
    }

    protected List<MaterialTag> tags = new ArrayList<>();

    public List<MaterialTag> tags() {
        return tags;
    }

    public MaterialItemComponent tag(MaterialTag tag) {
        this.tags.add(tag);
        return this;
    }

    public MaterialItemComponent tag(String tag) {
        return tag(MaterialTag.of(tag));
    }

    public boolean is(MaterialTag tag) {
        int s = tags.size();
        for (int i = 0; i < s; i++)
            if (tags.get(i).is(tag))
                return true;
        return false;
    }

    public boolean is(String tag) {
        return is(MaterialTag.of(tag));
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
