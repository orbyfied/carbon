package net.orbyfied.carbon.user;

import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.mc.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A wrapper for the creative inventory
 * allowing for storage of more data and
 * shit.
 * @see CreativeInventoryManager
 */
public class CarbonCreativeInventory {

    /**
     * Factory reference.
     */
    private final CreativeInventoryManager factory;

    /**
     * The Minecraft inventory that the player sees.
     */
    private final Inventory inv;

    /**
     * The player this inventory is assigned to.
     */
    private final Player player;

    /**
     * If it is currently open.
     */
    public boolean isOpen;

    public CarbonCreativeInventory(final CreativeInventoryManager factory,
                                   final Player player) {
        this.factory = factory;
        this.player  = player;
        this.inv = factory.createInventory(player);
    }

    /* Getters. */

    public Inventory getInventory() {
        return inv;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isOpen() {
        return isOpen;
    }

    /* Features. */

    public CarbonCreativeInventory initialize() {
        // populate inventory
        fillItems();

        return this;
    }

    public CarbonCreativeInventory open(Player player) {
        // open inventory
        player.openInventory(inv);

        // set open status
        isOpen = true;
        factory.open.put(player, this);

        return this;
    }

    public void close(boolean forceClosePlayer) {
        // close player inventory
        if (player.getOpenInventory().getTopInventory() == inv && forceClosePlayer) {
            player.closeInventory();
        }

        // set open status
        isOpen = false;
        factory.open.remove(player);
    }

    public void fillItems() {

        // get item list
        Registry<CarbonItem<?>> items =
                factory.environment.main.getRegistries().getByIdentifier("minecraft:items");

        // build top bar
        ItemStack barItem = ItemBuilder.create(Material.GRAY_STAINED_GLASS_PANE)
                .meta(meta -> meta.setDisplayName("")).build();

        int i = 9;
        for (; i < 18; i++) {
            inv.setItem(i, barItem);
        }

        // fill with items
        i = 18;
        final int l = 54 - 18;
        for (CarbonItem<?> item : items) {
            net.minecraft.world.item.ItemStack stack = item.newStack();
            inv.setItem(i, stack.getBukkitStack());
            i++;
            if (i >= l)
                break;
        }

    }

}
