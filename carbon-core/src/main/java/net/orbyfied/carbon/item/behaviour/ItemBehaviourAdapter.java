package net.orbyfied.carbon.item.behaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.orbyfied.carbon.api.CarbonAPI;
import net.orbyfied.carbon.content.BehaviourAdapter;
import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;
import net.orbyfied.carbon.item.behaviour.event.PlayerItemInteraction;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.mc.ItemUtil;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemBehaviourAdapter extends BehaviourAdapter<ItemBehaviourAdapter, CarbonItem> {

    private static Registry<CarbonItem> itemRegistry;

    static void initializeApi(CarbonAPI api) {
        itemRegistry = api.getRegistries().getByIdentifier("minecraft:items");
    }

    static void disableApi(CarbonAPI api) {

    }

    /* Event Stuff */

    public ItemBehaviourAdapter tryPost(PlayerInteractEvent event) {
        PlayerItemInteraction i = interactionOf(event);
        if (i == null)
            return this;
        post(i);
        return this;
    }

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
