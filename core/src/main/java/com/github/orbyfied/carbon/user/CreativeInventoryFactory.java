package com.github.orbyfied.carbon.user;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CreativeInventoryFactory implements Listener {

    public final CarbonUserEnvironment environment;

    public CreativeInventoryFactory(CarbonUserEnvironment environment) {
        this.environment = environment;
    }

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, environment.main.getPlugin());
    }

    public HashMap<Player, CarbonCreativeInventory> open = new HashMap<>();

    public CarbonCreativeInventory create(Player player) {
        return new CarbonCreativeInventory(this, player);
    }

    public static final String INVENTORY_TITLE =
            ChatColor.DARK_GRAY + "" + ChatColor.MAGIC + "iiii "
            + ChatColor.DARK_PURPLE + "Creative Inventory "
            + ChatColor.DARK_GRAY + ChatColor.MAGIC + "iiii ";

    public Inventory createInventory(Player player) {
        return Bukkit.createInventory(player, 54 /* double chest */, INVENTORY_TITLE);
    }

    @EventHandler
    void onCloseInventory(InventoryCloseEvent event) {
        CarbonCreativeInventory inv;
        if ((inv = open.get(event.getPlayer())) == null) return;
        if (event.getInventory() != inv.inv) return;
        inv.close(false);
    }

    @EventHandler
    void onItemClick(InventoryClickEvent event) {
        CarbonCreativeInventory cinv;
        if ((cinv = open.get(event.getWhoClicked())) == null) return;
        if (event.getClickedInventory() != cinv.inv) return;
        Inventory inv = cinv.inv;

        int slot = event.getSlot();
        if (slot < 18) {
            return;
        } else if (slot >= 18) {
            InventoryAction action = event.getAction();
            switch (action) {
                case HOTBAR_SWAP:
                case PLACE_ALL:
                case PLACE_SOME:
                case PLACE_ONE:
                case DROP_ALL_SLOT:
                case DROP_ONE_SLOT:
                    event.setCancelled(true);
                    return;
            }

            ItemStack cursor = event.getCurrentItem();
            if (cursor == null) return;
            event.setCancelled(true);
            if (event.getClick().isRightClick()) {
                cursor.setAmount(64);
            }
            inv.setItem(slot, cursor.asQuantity(1));
            event.setCursor(cursor);
        }
    }

}
