package com.github.orbyfied.carbon.item.display;

import com.github.orbyfied.carbon.content.CMDRegistryService;
import com.github.orbyfied.carbon.content.ModelHolder;
import com.github.orbyfied.carbon.content.pack.PackResource;
import com.github.orbyfied.carbon.content.pack.ResourcePackBuilder;
import com.github.orbyfied.carbon.content.pack.SourcedAsset;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.ItemDisplayStrategy;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.mc.ItemUtil;
import com.github.orbyfied.carbon.util.resource.ResourceHandle;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;

public class ModelItemDisplayStrategy extends ItemDisplayStrategy implements ModelHolder<CarbonItem<?>> {

    protected String displayName;
    protected boolean hasGlint;

    protected Material baseMaterial;
    protected List<String> modelsIntermediate = new ArrayList<>();
    protected SourcedAsset[] models;
    protected int cmdStart;

    public ModelItemDisplayStrategy(CarbonItem<?> item) {
        super(item);
        baseMaterial = item.getBaseMaterial();
        hasGlint = false;
    }

    @Override
    public void build() {
        // parse models
        Class<?> modClass = item.getMod().getPluginClass();
        Identifier id = item.getIdentifier();
        this.baseMaterial = item.getBaseMaterial();
        this.models = new SourcedAsset[modelsIntermediate.size()];
        for (int i = 0; i < modelsIntermediate.size(); i++) {
            // build asset path
            // assets/<modid>/models/item/<model>
            String model = modelsIntermediate.get(i);
            StringBuilder pathb = new StringBuilder();
            pathb.append("assets/")
                    .append(id.getNamespace())
                    .append("/models/item/")
                    .append(model + ".json");
            String assetPath = pathb.toString();

            models[i] = new SourcedAsset(
                    // get resource <mod jar>/<asset> as source
                    ResourceHandle.ofModuleResource(modClass, "/" + assetPath),
                    // use resource <pack>/<asset> as destination
                    PackResource.of("item." + item.getIdentifier(),path -> path.resolve(assetPath))
            ).setName(model);
        }

        // bake
        prepare();
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
    public String getNamespace() {
        return item.getIdentifier().getNamespace();
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

    public ModelItemDisplayStrategy addModel(String asset) {
        modelsIntermediate.add(asset);
        return this;
    }

}
