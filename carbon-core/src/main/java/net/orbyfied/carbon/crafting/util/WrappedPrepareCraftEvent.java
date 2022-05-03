package net.orbyfied.carbon.crafting.util;

import net.orbyfied.carbon.event.BusEvent;
import net.orbyfied.carbon.event.EventBus;
import net.orbyfied.carbon.event.pipeline.PipelineAccess;
import net.orbyfied.carbon.event.util.Pipelines;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public class WrappedPrepareCraftEvent extends BusEvent {

    public static PipelineAccess<BusEvent> getPipeline(EventBus bus) {
        return Pipelines.mono(bus);
    }

    protected PrepareItemCraftEvent event;

    public WrappedPrepareCraftEvent(PrepareItemCraftEvent e) {
        this.event = e;
    }

    public PrepareItemCraftEvent getEvent() {
        return event;
    }
}
