package net.orbyfied.carbon.item.behaviour;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.core.CarbonJavaAPI;
import net.orbyfied.carbon.event.BusEvent;
import net.orbyfied.carbon.event.EventBus;
import net.orbyfied.carbon.event.EventListener;
import net.orbyfied.carbon.event.RegisteredListener;
import net.orbyfied.carbon.event.pipeline.Handler;
import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;
import net.orbyfied.carbon.item.behaviour.event.PlayerItemInteraction;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemBehaviourAdapter {

    protected final EventBus bus = new EventBus();

    protected final Carbon main = CarbonJavaAPI.get().getMain();

    protected RegisteredListener genericListener;

    static final Registry<CarbonItem> itemRegistry = CarbonJavaAPI.get().getRegistries()
            .getByIdentifier("minecraft:items");

    public EventBus getBus() {
        return bus;
    }

    public ItemBehaviourAdapter post(BusEvent event) {
        bus.post(event);
        return this;
    }

    public ItemBehaviourAdapter tryPost(PlayerInteractEvent event) {
        PlayerItemInteraction i = interactionOf(event);
        if (i == null)
            return this;
        post(i);
        return this;
    }

    public RegisteredListener getOrCreateGenericListener() {
        if (genericListener == null)
            genericListener = new RegisteredListener(bus, null)
                    .dynamic(true)
                    .register();
        return genericListener;
    }

    public RegisteredListener getGenericListenerOrNull() {
        return genericListener;
    }

    public ItemBehaviourAdapter addBehaviour(EventListener o) {
        bus.register(o);
        return this;
    }

    public <E> ItemBehaviourAdapter behaviour(Class<E> eClass, Handler<E> handler) {
        getOrCreateGenericListener().handle(eClass, handler);
        return this;
    }

    /* Event Stuff */

    public static PlayerItemInteraction interactionOf(PlayerInteractEvent event) {
        // check for item
        if (event.getItem() == null)
            return null;

        // get handle
        ItemStack nmsStack = ItemUtil.getHandle(event.getItem());
        CompoundTag tag = nmsStack.getTag();
        if (tag == null)
            return null;

        // get and check item type
        String itemId = tag.getString("CarbonItemId");
        CarbonItem item = itemRegistry.getByIdentifier(itemId);
        if (item == null)
            return null;

        CarbonItemState state;
        try {
            // load state
            state = item.loadState(nmsStack);
        } catch (Exception e) {
            // ignore
            return null;
        }

        // construct event
        PlayerItemInteraction interaction = new PlayerItemInteraction(
                item,
                state,
                event
        );

        // return
        return interaction;
    }

}
