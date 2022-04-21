package com.github.orbyfied.carbon.crafting.util;

import com.github.orbyfied.carbon.event.BusEvent;
import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public class WrappedPrepareCraftEvent extends BusEvent {

    public static PipelineAccess<BusEvent> getPipeline(EventBus bus) {
        return BusEvent.createMonoPipeline(bus);
    }

    protected PrepareItemCraftEvent event;

    public WrappedPrepareCraftEvent(PrepareItemCraftEvent e) {
        this.event = e;
    }

    public PrepareItemCraftEvent getEvent() {
        return event;
    }
}
