package com.github.orbyfied.carbon.item.display;

import com.github.orbyfied.carbon.content.CMDRegistryService;
import com.github.orbyfied.carbon.content.ModelHolder;
import com.github.orbyfied.carbon.content.pack.ResourcePackBuilder;
import com.github.orbyfied.carbon.content.pack.SourcedAsset;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.ItemDisplayStrategy;
import com.github.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;

public class ModelItemDisplayStrategy extends ItemDisplayStrategy implements ModelHolder<CarbonItem<?>> {

    protected String displayName;
    protected boolean hasGlint;

    protected Material baseMaterial;
    protected SourcedAsset[] models;
    protected int cmdStart;

    public ModelItemDisplayStrategy(CarbonItem<?> item) {
        super(item);
        baseMaterial = item.getBaseMaterial();
        hasGlint = false;
    }

    @Override
    public void build() {
        this.baseMaterial = item.getBaseMaterial();
    }

    public ModelItemDisplayStrategy setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ModelItemDisplayStrategy setGlinting(boolean b) {
        this.hasGlint = b;
        return this;
    }

    public boolean isGlinting() {
        return hasGlint;
    }

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
        if (hasGlint)
            ItemUtil.setHasGlint(stack.getOrCreateTag(), true);

        MutableComponent displayNameComponent = new TextComponent(displayName);
        displayNameComponent.setStyle(Style.EMPTY.withItalic(false));

        stack.setHoverName(displayNameComponent);
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
