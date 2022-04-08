package com.github.orbyfied.carbon.item.behaviour;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.core.CarbonJavaAPI;
import com.github.orbyfied.carbon.core.Service;
import com.github.orbyfied.carbon.core.ServiceManager;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.ItemComponent;
import com.github.orbyfied.carbon.item.behaviour.event.ItemInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventItemBehaviourComponent
        extends ItemComponent<CarbonItemState> {

    protected final Carbon main;

    public EventItemBehaviourComponent(CarbonItem<CarbonItemState> element) {
        super(element);
        this.main = CarbonJavaAPI.get().getMain();
    }

    protected ItemInteractionAdapter adapter = new ItemInteractionAdapter();

    public ItemInteractionAdapter adapter() {
        return adapter;
    }

    public EventItemBehaviourComponent adapter(ItemInteractionAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    @Override
    public void build() {
        if (!main.getServiceManager().hasService(PostService.class))
            main.getServiceManager().addService(new PostService(main.getServiceManager()));
        if (adapter == null)
            adapter = new ItemInteractionAdapter();
    }

    @Override
    public void updateStack(ItemStack stack, CarbonItemState state, CompoundTag tag) { }

    /**
     * The event listener service to call
     * all interaction events.
     */
    public static class PostService
            extends Service
            implements Listener {

        public PostService(ServiceManager manager) {
            super(manager);
        }

        @Override
        protected void start() {
            // register events
            Bukkit.getPluginManager().registerEvents(
                    this,
                    manager.getMain().getPlugin()
            );
        }

        /* ---- Events ---- */

        @EventHandler
        void onPlayerInteract(PlayerInteractEvent event) {

            ItemInteraction interaction = ItemInteractionAdapter.interactionOf(event);
            if (interaction == null)
                return;

            interaction.getItem()
                    .component(EventItemBehaviourComponent.class)
                    .adapter.post(interaction);

        }

    }

}
