package com.github.orbyfied.carbon.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public abstract class ItemDisplayStrategy extends ItemStrategy {

    public ItemDisplayStrategy(CarbonItem<?> item) {
        super(item);
    }

    public abstract void makeAssets(/* ... */);

    public abstract void makeItem(ItemStack stack,
                                  CarbonItemState<?> state,
                                  ItemMeta meta,
                                  PersistentDataContainer tag);

}
