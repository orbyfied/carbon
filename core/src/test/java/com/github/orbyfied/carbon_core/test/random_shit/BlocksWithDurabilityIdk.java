package com.github.orbyfied.carbon_core.test.random_shit;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class BlocksWithDurabilityIdk {

    private static final Map<Material, Short> BLOCK_DURABILITIES = new HashMap<>();

    static {
        BLOCK_DURABILITIES.put(Material.CRAFTING_TABLE, (short)3);
    }

    private static NamespacedKey keyOfPos(Location loc,
                                          String ext) {
        return new NamespacedKey("shit", loc.getX() + "_" + loc.getY() + "_" + loc.getZ() + "_" + ext);
    }

    public void used(Block block) {
        Chunk c = block.getChunk();
        PersistentDataContainer pdc = c.getPersistentDataContainer();
        NamespacedKey key = keyOfPos(block.getLocation(), "durability");
        Short maxDur = BLOCK_DURABILITIES.get(block.getType());
        if (maxDur == null) // not a block w/ durability
            return;

        // check if this block was used already
        if (!pdc.has(key)) {
            pdc.set(key, PersistentDataType.SHORT, (short)(maxDur - 1));
            return;
        }

        // get and decrease durability
        short dur = pdc.get(key, PersistentDataType.SHORT);
        dur--;

        if (dur == 0) { // broken
            pdc.remove(key);
            block.setType(Material.AIR);
            block.getWorld().playSound(block.getLocation(),
                    Sound.BLOCK_ANVIL_BREAK,
                    SoundCategory.BLOCKS,
                    1f,
                    .5f);
        } else {
            pdc.set(key, PersistentDataType.SHORT, dur);
        }
    }

}
