package com.github.orbyfied.carbon.item.interact;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.content.StrategyService;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.ItemInteractStrategy;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventItemInteractStrategyService
        extends StrategyService<ItemInteractStrategy>
        implements Listener {

    public EventItemInteractStrategyService(Carbon main) {
        super(main, ItemInteractStrategy.class);
        this.itemRegistry = main
                .getRegistries()
                .getByIdentifier("minecraft:items");
    }

    protected Registry<CarbonItem> itemRegistry;

    public EventItemInteractStrategyService enable() {
        Bukkit.getPluginManager().registerEvents(this, main.getPlugin());
        return null;
    }

    public CarbonItemState<?> getState(org.bukkit.inventory.ItemStack stack) {
        // get item handle
        if (stack == null)
            return null;
        ItemStack nmsStack = ItemUtil.getHandle(stack);
        if (nmsStack == null)
            return null;

        // get item
        CompoundTag tag = nmsStack.getTag();
        if (tag == null) return null;
        if (!tag.contains(CarbonItem.ITEM_ID_TAG)) // check if it is custom item
            return null;
        int id = tag.getInt(CarbonItem.ITEM_ID_TAG);

        CarbonItem<?> item = (CarbonItem<?>) itemRegistry.getByIndex(id);
        if (item == null) return null;

        // load and return state
        CarbonItemState<?> state = item.loadState(stack);
        return state;
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {

        // get item
        CarbonItemState<?> state = getState(event.getItem());

        // handle interaction
        event.getPlayer().sendMessage(
                ChatColor.GOLD + "state: " + ChatColor.RED + state
        );

    }

}
