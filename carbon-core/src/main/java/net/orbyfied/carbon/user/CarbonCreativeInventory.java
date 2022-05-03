package net.orbyfied.carbon.user;

import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.mc.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CarbonCreativeInventory {

    private final CreativeInventoryFactory factory;

    public final Inventory inv;

    public final Player player;

    public boolean isOpen;

    public CarbonCreativeInventory(final CreativeInventoryFactory factory,
                                   final Player player) {
        this.factory = factory;
        this.player  = player;
        this.inv = factory.createInventory(player);
    }

    public CarbonCreativeInventory initialize() {
        fillItems();
        return this;
    }

    public CarbonCreativeInventory open(Player player) {
        player.openInventory(inv);
        isOpen = true;
        factory.open.put(player, this);
        return this;
    }

    public void close(boolean forceClosePlayer) {
        if (player.getOpenInventory().getTopInventory() == inv && forceClosePlayer) {
            player.closeInventory();
        }
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
