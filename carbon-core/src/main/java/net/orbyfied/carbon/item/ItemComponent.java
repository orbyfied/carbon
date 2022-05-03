package net.orbyfied.carbon.item;

import net.orbyfied.carbon.element.AbstractElementComponent;
import net.minecraft.nbt.CompoundTag;

public abstract class ItemComponent<S extends CarbonItemState>
        extends AbstractElementComponent<CarbonItem<S>> {

    public ItemComponent(CarbonItem<S> element) {
        super(element);
    }

    public abstract void updateStack(CompiledStack stack,
                                     S state,
                                     CompoundTag tag);

}
