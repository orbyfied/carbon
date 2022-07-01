package net.orbyfied.examplemod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.logging.BukkitLogger;
import net.orbyfied.carbon.test.CarbonTest;
import net.orbyfied.carbon.test.TestManager;
import net.orbyfied.carbon.util.mc.NmsHelper;
import net.orbyfied.carbon.util.mc.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class DefaultCarbonTests {

    @CarbonTest
    static void testFastBlockPlace(Carbon main, TestManager manager, BukkitLogger logger) {
        final int size   = 10000;
        final int blocks = size * 2 * 2 * /* y */ 128;
        final BlockState stateSet = Blocks.STONE.defaultBlockState();
        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onInteract(PlayerInteractEvent event) {
                if (event.getItem() == null || event.getItem().getType() != Material.BLUE_ORCHID) return;

                Location block = event.getClickedBlock().getLocation();
                int x1 = block.getBlockX() - size;
                int y1 = block.getBlockY() - 64;
                int z1 = block.getBlockZ() - size;
                int x2 = block.getBlockX() + size;
                int y2 = block.getBlockY() + 64;
                int z2 = block.getBlockZ() + size;

                WorldUtil.modifyBlocksFast(
                        NmsHelper.getWorldHandle(block.getWorld()),
                        x1, y1, z1,
                        x2, y2, z2,
                        (x, y, z, state) -> stateSet
                );

                WorldUtil.updateBlocksAsync(
                        NmsHelper.getWorldHandle(block.getWorld()),
                        x1, y1, z1,
                        x2, y2, z2
                );

                event.getPlayer().sendMessage("Filled " + blocks + " blocks.");
            }

        }, main.getPlugin());
    }

}
