package net.orbyfied.carbon.item.behaviour;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.core.CarbonJavaAPI;
import net.orbyfied.carbon.core.Service;
import net.orbyfied.carbon.core.ServiceManager;
import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;
import net.orbyfied.carbon.item.CompiledStack;
import net.orbyfied.carbon.item.ItemComponent;
import net.orbyfied.carbon.item.behaviour.event.PlayerItemInteraction;
import net.minecraft.nbt.CompoundTag;
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

    protected ItemBehaviourAdapter adapter;

    public ItemBehaviourAdapter adapter() {
        if (adapter == null)
            adapter = new ItemBehaviourAdapter();
        return adapter;
    }

    public EventItemBehaviourComponent adapter(ItemBehaviourAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    @Override
    public void build() {
        if (!main.getServiceManager().hasService(PostService.class))
            main.getServiceManager().addService(new PostService(main.getServiceManager()));
        if (adapter == null)
            adapter = new ItemBehaviourAdapter();
    }

    @Override
    public void updateStack(CompiledStack stack, CarbonItemState state, CompoundTag tag) { }

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

            PlayerItemInteraction interaction = ItemBehaviourAdapter.interactionOf(event);
            if (interaction == null)
                return;

            interaction.getItem()
                    .component(EventItemBehaviourComponent.class)
                    .adapter.post(interaction);

        }

    }

}
