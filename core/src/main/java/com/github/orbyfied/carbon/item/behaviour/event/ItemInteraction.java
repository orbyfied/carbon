package com.github.orbyfied.carbon.item.behaviour.event;

import com.github.orbyfied.carbon.event.BusEvent;
import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.util.TriConsumer;
import com.github.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.function.BiConsumer;

public class ItemInteraction extends BusEvent {

    protected final CarbonItem<CarbonItemState> item;
    protected final CarbonItemState<CarbonItem> state;
    protected final PlayerInteractEvent event;

    public ItemInteraction(CarbonItem<CarbonItemState> item,
                           CarbonItemState<CarbonItem> state,
                           PlayerInteractEvent event) {
        this.item = item;
        this.state = state;
        this.event = event;
    }

    public CarbonItem<CarbonItemState> getItem() {
        return item;
    }

    public CarbonItemState<CarbonItem> getState() {
        return state;
    }

    public PlayerInteractEvent getEvent() {
        return event;
    }

    public static PipelineAccess<BusEvent> getPipeline(EventBus bus) {
        return BusEvent.createMonoPipeline(bus);
    }

}
