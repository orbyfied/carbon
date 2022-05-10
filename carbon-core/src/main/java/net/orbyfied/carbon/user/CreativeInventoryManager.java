package net.orbyfied.carbon.user;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Management class for the Carbon creative
 * inventory feature.
 * @see CarbonUserEnvironment
 * @see CarbonCreativeInventory
 */
public class CreativeInventoryManager implements Listener {

    /**
     * The user environment reference.
     */
    public final CarbonUserEnvironment environment;

    public CreativeInventoryManager(CarbonUserEnvironment environment) {
        this.environment = environment;
    }

    /* ----- Status Logic ----- */

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, environment.main.getPlugin());
    }

    public void disable() {
        HandlerList.unregisterAll(this);
    }

    /* ----- Inventories ----- */

    /**
     * The currently open creative inventories byplayer.
     */
    public HashMap<Player, CarbonCreativeInventory> open = new HashMap<>();

    /**
     * Creates a new creative inventory for the
     * provided player.
     * @param player The player.
     * @return The inventory wrapper.
     */
    public CarbonCreativeInventory create(Player player) {
        return new CarbonCreativeInventory(this, player);
    }

    /**
     * The title to use for the creative inventory.
     */
    public static final String INVENTORY_TITLE =
            ChatColor.DARK_GRAY + "" + ChatColor.MAGIC + "iiii "
            + ChatColor.DARK_PURPLE + "Creative Inventory "
            + ChatColor.DARK_GRAY + ChatColor.MAGIC + "iiii ";

    /* ---- Internal ----- */

    protected Inventory createInventory(Player player) {
        return Bukkit.createInventory(player, 54 /* double chest */, INVENTORY_TITLE);
    }

    @EventHandler
    // try to close object
    // when the inventory is closed
    void onCloseInventory(InventoryCloseEvent event) {
        CarbonCreativeInventory inv;
        if ((inv = open.get(event.getPlayer())) == null) return;
        if (event.getInventory() != inv.getInventory()) return;

        inv.close(false);
    }

    @EventHandler
    // handle clicks on the inventory
    void onItemClick(InventoryClickEvent event) {
        // check and get object
        CarbonCreativeInventory cinv;
        if ((cinv = open.get(event.getWhoClicked())) == null) return;
        if (event.getClickedInventory() != cinv.getInventory()) return;

        // get inventory
        Inventory inv = cinv.getInventory();

        // check slot index
        int slot = event.getSlot();
            // in top bar
        if (slot < 18) {
            return;

            // in item section
        } else if (slot >= 18) {
            // check action
            InventoryAction action = event.getAction();
            switch (action) {
                // dont allow any manipulation
                // (placement) of the items
                case HOTBAR_SWAP:
                case PLACE_ALL:
                case PLACE_SOME:
                case PLACE_ONE:
                case DROP_ALL_SLOT:
                case DROP_ONE_SLOT:
                    event.setCancelled(true);
                    return;
            }

            // get cursor stack
            ItemStack cursor = event.getCurrentItem();
            if (cursor == null) return;

            // cancel event
            event.setCancelled(true);

            // set amount to 64 if it is a right click
            if (event.getClick().isRightClick()) {
                cursor.setAmount(64);
            }

            // replenish item
            inv.setItem(slot, cursor.asQuantity(1));

            // set new item in cursor
            event.setCursor(cursor);
        }
    }

}
