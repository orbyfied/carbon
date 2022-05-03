package net.orbyfied.carbon.item;

import net.orbyfied.carbon.core.Service;
import net.orbyfied.carbon.core.ServiceManager;
import net.orbyfied.carbon.util.mc.NmsHelper;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ItemFixer
        extends Service
        implements Listener {

    public ItemFixer(ServiceManager manager) {
        super(manager);
    }

    @Override
    protected void start() {

        // register event listener
        Bukkit.getPluginManager().registerEvents(this, manager.getMain().getPlugin());

    }

    @Override
    public void end() {

    }

    /* ---- Core Fixers ---- */

    public void fixItem(CompiledStack stack) {
        // check if item is custom
        if (stack.getState() == null)
            return;

        // get item
        CarbonItem item = stack.getState().getItem();

        // update
        item.updateStack(stack);
    }

    public void fixItem(ItemStack stack) {
        fixItem(new CompiledStack().wrap(stack));
    }

    /* ---- Player Inventory ---- */

    public ItemFixer fixPlayerNextLogin(OfflinePlayer player) {
        hasLoggedOn.remove(player.getUniqueId());
        return this;
    }

    public boolean hasPlayerBeenFixed(OfflinePlayer player) {
        return hasLoggedOn.contains(player.getUniqueId());
    }

    /**
     * Cache for storing if players have logged on
     * this session. Gets reset on startup. Ensures
     * that a player inventory is not fixed twice in
     * the same session as the item properties should be
     * defined by now. To
     */
    protected final HashSet<UUID> hasLoggedOn = new HashSet<>();

    public ItemFixer forceFixPlayer(org.bukkit.entity.Player player) {
        return forceFixPlayer(NmsHelper.getPlayerHandle(player));
    }

    public ItemFixer forceFixPlayer(net.minecraft.world.entity.player.Player player) {
        // TODO: do like async or some shit
        for (List<ItemStack> compartment : player.getInventory().compartments) {
            int l = compartment.size();
            for (int i = 0; i < l; i++) {
                fixItem(compartment.get(i));
            }
        }

        return this;
    }

    @EventHandler // fix player inventory
    void onPlayerJoin(PlayerJoinEvent event) {
        forceFixPlayer(event.getPlayer());
    }

}
