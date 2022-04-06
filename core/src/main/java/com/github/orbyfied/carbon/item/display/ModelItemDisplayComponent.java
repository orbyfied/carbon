package com.github.orbyfied.carbon.item.display;

import com.github.orbyfied.carbon.content.CMDRegistryService;
import com.github.orbyfied.carbon.content.ModelHolder;
import com.github.orbyfied.carbon.element.SpecifiedIdentifier;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.item.ItemComponent;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * An item display strategy which utilizes
 * custom models and the custom model data
 * feature of Minecraft to display them.
 * @see ModelHolder
 * @see CMDRegistryService
 */
public class ModelItemDisplayComponent
        extends ItemComponent<CarbonItemState>
        implements ModelHolder<CarbonItem<?>> {

    // TODO: clean this mess of a class up

    /* properties */
    protected String displayName;
    protected boolean hasGlint;

    /* system */
    protected Material baseMaterial;
    protected List<SpecifiedIdentifier> modelsIntermediate = new ArrayList<>();
    protected SpecifiedIdentifier[] models;
    protected int cmdStart;

    public ModelItemDisplayComponent(CarbonItem<CarbonItemState> item) {
        super(item);
        baseMaterial = item.getBaseMaterial();
        hasGlint = false;
    }

    @Override
    public void build() {
        // parse models
        Identifier id = element.getIdentifier();
        this.baseMaterial = element.getBaseMaterial();
        this.models = modelsIntermediate.toArray(new com.github.orbyfied.carbon.element.SpecifiedIdentifier[0]);

        // bake
        bakeModels();
    }

    public ModelItemDisplayComponent setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ModelItemDisplayComponent setGlinting(boolean b) {
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
        CMDRegistryService<CarbonItem<?>> service = element.getRegistry().getService(CMDRegistryService.class);
        cmdStart = registerAllAndGetOffset(baseMaterial, service, models.length);
    }

    private int getModelIdFrom(CarbonItemState<?> state) {
        return 0;
    }

    @Override
    public void update(
            ItemStack stack,
            CarbonItemState state,
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
    public SpecifiedIdentifier getModel(int off) {
        return models[off];
    }

    @Override
    public SpecifiedIdentifier[] getModels() {
        return models;
    }

    public ModelItemDisplayComponent addModel(String asset) {
        modelsIntermediate.add(
                new SpecifiedIdentifier(
                        element.getIdentifier().getNamespace(),
                        "item",
                        asset
                )
        );
        return this;
    }

}
