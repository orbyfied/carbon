package com.github.orbyfied.carbon.item.display;

import com.github.orbyfied.carbon.content.CMDRegistryService;
import com.github.orbyfied.carbon.content.ModelHolder;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.ItemDisplayStrategy;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class ModelItemDisplayStrategy extends ItemDisplayStrategy implements ModelHolder<CarbonItem<?>> {

    public ModelItemDisplayStrategy(CarbonItem<?> item) {
        super(item);

    }

    public Material baseMaterial;
    public Object[] models;
    public int      cmdStart;

    public void bakeModels() {
        // register custom model data
        if (models.length == 0)
            throw new IllegalArgumentException("item with 0 models, cant bake");
        CMDRegistryService<CarbonItem<?>> service = item.getRegistry().getService(CMDRegistryService.class);
        cmdStart = registerAllAndGetOffset(baseMaterial, service, models.length);
    }

    @Override
    public void makeAssets() {
        bakeModels();
    }

    @Override
    public void makeItem(
            ItemStack stack,
            CarbonItemState<?> state,
            ItemMeta meta,
            PersistentDataContainer tag) {

    }

    @Override
    public int getCustomModelDataOffset() {
        return cmdStart;
    }

    @Override
    public Object getModel(int off) {
        return models[off];
    }

}
