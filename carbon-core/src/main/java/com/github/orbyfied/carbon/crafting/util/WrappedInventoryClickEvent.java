package com.github.orbyfied.carbon.crafting.util;

import com.github.orbyfied.carbon.event.BusEvent;
import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.pipeline.PipelineAccess;
import com.github.orbyfied.carbon.event.util.Pipelines;
import org.bukkit.event.inventory.InventoryClickEvent;

public class WrappedInventoryClickEvent extends BusEvent {

    public static PipelineAccess<BusEvent> getPipeline(EventBus bus) {
        return Pipelines.mono(bus);
    }

    protected InventoryClickEvent event;

    public WrappedInventoryClickEvent(InventoryClickEvent e) {
        this.event = e;
    }

    public InventoryClickEvent getEvent() {
        return event;
    }

}
