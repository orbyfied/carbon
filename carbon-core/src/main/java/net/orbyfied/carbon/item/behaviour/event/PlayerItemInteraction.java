package net.orbyfied.carbon.item.behaviour.event;

import net.orbyfied.carbon.event.BusEvent;
import net.orbyfied.carbon.event.EventBus;
import net.orbyfied.carbon.event.pipeline.PipelineAccess;
import net.orbyfied.carbon.event.util.Pipelines;
import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerItemInteraction extends BusEvent {

    protected final CarbonItem<CarbonItemState> item;
    protected final CarbonItemState<CarbonItem> state;
    protected final PlayerInteractEvent event;

    public PlayerItemInteraction(CarbonItem<CarbonItemState> item,
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
        return Pipelines.mono(bus);
    }

}
