package com.github.orbyfied.carbon.item.behaviour;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.core.CarbonJavaAPI;
import com.github.orbyfied.carbon.event.EventBus;
import com.github.orbyfied.carbon.event.EventListener;
import com.github.orbyfied.carbon.event.RegisteredListener;
import com.github.orbyfied.carbon.event.pipeline.Handler;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.behaviour.event.PlayerItemInteraction;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemInteractionAdapter {

    protected final EventBus bus = new EventBus();

    protected final Carbon main = CarbonJavaAPI.get().getMain();

    protected RegisteredListener genericListener;

    static final Registry<CarbonItem> itemRegistry = CarbonJavaAPI.get().getRegistries()
            .getByIdentifier("minecraft:items");

    public EventBus getBus() {
        return bus;
    }

    public ItemInteractionAdapter post(PlayerItemInteraction event) {
        bus.post(event);
        return this;
    }

    public ItemInteractionAdapter tryPost(PlayerInteractEvent event) {
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

    public ItemInteractionAdapter addBehaviour(EventListener o) {
        bus.register(o);
        return this;
    }

    public <E> ItemInteractionAdapter behaviour(Class<E> eClass, Handler<E> handler) {
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
