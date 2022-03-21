package com.github.orbyfied.carbon.item;

import com.github.orbyfied.carbon.element.ModElementRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
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

    public void save(ItemStack stack,
                     ItemMeta meta,
                     CompoundTag tag) {
        tag.putInt("ItemId", item.getId());
    }

    @SuppressWarnings("unchecked")
    public void load(ItemStack stack,
                     ItemMeta meta,
                     CompoundTag tag) {
        item = (I) item.getRegistry().getComponent(ModElementRegistry.class)
                .getLinear(tag.getInt("ItemId"));
    }

}
