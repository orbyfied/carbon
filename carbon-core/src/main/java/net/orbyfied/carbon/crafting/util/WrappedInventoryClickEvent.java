package net.orbyfied.carbon.crafting.util;

import net.orbyfied.carbon.event.BusEvent;
import net.orbyfied.carbon.event.EventBus;
import net.orbyfied.carbon.event.pipeline.PipelineAccess;
import net.orbyfied.carbon.event.util.Pipelines;
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
