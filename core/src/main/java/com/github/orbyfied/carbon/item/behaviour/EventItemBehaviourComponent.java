package com.github.orbyfied.carbon.item.behaviour;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.core.CarbonJavaAPI;
import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.ItemComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class EventItemBehaviourComponent
        extends ItemComponent<CarbonItemState> {

    protected final EventBus bus = new EventBus();
    protected final Carbon main;

    public EventItemBehaviourComponent(CarbonItem<CarbonItemState> element) {
        super(element);
        this.main = CarbonJavaAPI.get().getMain();
    }

    @Override
    public void build() {
        if (!main.getServiceManager().hasService(MixinEventBehaviourService.class))
            main.getServiceManager().addService(new MixinEventBehaviourService(main.getServiceManager()));
    }

    @Override
    public void update(ItemStack stack, CarbonItemState state, CompoundTag tag) {

    }

}
