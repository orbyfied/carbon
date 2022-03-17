package com.github.orbyfied.carbon.item;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class CarbonItemState<I extends CarbonItem<?>> {

    protected I item;

    public CarbonItemState(I item) {
        this.item = item;
    }

    public I getItem() {
        return item;
    }

    public void save(ItemMeta meta, PersistentDataContainer tag) {

    }

    public void load(ItemMeta meta, PersistentDataContainer tag) {

    }

}
