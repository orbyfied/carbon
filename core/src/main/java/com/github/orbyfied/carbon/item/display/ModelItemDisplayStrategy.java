package com.github.orbyfied.carbon.item.display;

import com.github.orbyfied.carbon.content.CMDRegistryService;
import com.github.orbyfied.carbon.content.ModelHolder;
import com.github.orbyfied.carbon.content.pack.ResourcePackBuilder;
import com.github.orbyfied.carbon.content.pack.SourcedAsset;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.ItemDisplayStrategy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;

public class ModelItemDisplayStrategy extends ItemDisplayStrategy implements ModelHolder<CarbonItem<?>> {

    public ModelItemDisplayStrategy(CarbonItem<?> item) {
        super(item);

    }

    public Material baseMaterial;
    public SourcedAsset[] models;
    public int cmdStart;

    public void bakeModels() {
        // register custom model data
        if (models.length == 0)
            throw new IllegalArgumentException("item with 0 models, cant bake");
        CMDRegistryService<CarbonItem<?>> service = item.getRegistry().getService(CMDRegistryService.class);
        cmdStart = registerAllAndGetOffset(baseMaterial, service, models.length);
    }

    private int getModelIdFrom(CarbonItemState<?> state) {
        return 0;
    }

    @Override
    public void prepare() {
        bakeModels();
    }

    @Override
    public void makeAssets(ResourcePackBuilder builder) {

    }

    @Override
    public void makeItem(
            ItemStack stack,
            CarbonItemState<?> state,
            CompoundTag tag) {
        tag.putInt("CustomModelData", cmdStart + getModelIdFrom(state));
    }

    @Override
    public int getCustomModelDataOffset() {
        return cmdStart;
    }

    @Override
    public SourcedAsset getModel(int off) {
        return models[off];
    }

    @Override
    public SourcedAsset[] getModels() {
        return models;
    }

}
