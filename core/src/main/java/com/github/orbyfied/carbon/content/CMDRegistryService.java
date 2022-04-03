package com.github.orbyfied.carbon.content;

import com.github.orbyfied.carbon.content.pack.PackResource;
import com.github.orbyfied.carbon.content.pack.ResourcePackBuilder;
import com.github.orbyfied.carbon.content.pack.SourcedAsset;
import com.github.orbyfied.carbon.content.pack.asset.BaseItemModelBuilder;
import com.github.orbyfied.carbon.content.pack.service.MinecraftAssetService;
import com.github.orbyfied.carbon.element.RegistrableElement;
import com.github.orbyfied.carbon.element.SpecifiedIdentifier;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.registry.AbstractRegistryService;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.*;

public class CMDRegistryService<T extends RegistrableElement>
        extends AbstractRegistryService<Registry<T>, T>
        implements AssetPreparingService {

    public CMDRegistryService(Registry<T> registry) {
        super(registry);
    }

    private HashMap<Material, ArrayList<ModelHolder<T>>> cmds = new HashMap<>(); // raw custom model data counter

    public HashMap<Material, ArrayList<ModelHolder<T>>> getCMDRegistry() {
        return cmds;
    }

    public int next(Material base, ModelHolder<T> holder) {
        ArrayList<ModelHolder<T>> list = cmds.computeIfAbsent(base, __ -> new ArrayList<>());
        int i = list.size() + 1;
        list.add(holder);
        return i;
    }

    public ModelHolder<T> getHolderOf(Material base, int off) {
        off = off - 1;
        ArrayList<ModelHolder<T>> holders;
        if ((holders = cmds.get(base)) == null) return null;
        if (off >= holders.size() || off < 0) return null;
        return holders.get(off);
    }

    public com.github.orbyfied.carbon.element.SpecifiedIdentifier getModelOf(Material base, int off) {
        ModelHolder<T> h = getHolderOf(base, off);
        return h.getModel(off - 1 - h.getCustomModelDataOffset());
    }

    public BaseEditing edit(Material base) {
        return new BaseEditing(base);
    }

    @Override
    public void prepareAssets(ResourcePackBuilder builder) {
        MinecraftAssetService mcAssetService = builder.getService(MinecraftAssetService.class);

        // map of asset builders per material
        HashMap<Material, BaseItemModelBuilder> baseModelBuilders = new HashMap<>();

        // get all custom model data's per material
        // and loop over them
        Set<Map.Entry<Material, ArrayList<ModelHolder<T>>>> entries = cmds.entrySet();
        for (Map.Entry<Material, ArrayList<ModelHolder<T>>> entry : entries) {
            Material base = entry.getKey();
            NamespacedKey baseKey = base.getKey();

            // make sure to extract item base model
            mcAssetService.addToExtract("assets/minecraft/models/item/" + baseKey.getKey() + ".json");

            // get model builder
            BaseItemModelBuilder baseModelBuilder = baseModelBuilders.computeIfAbsent(base, base1 -> builder.asset(new BaseItemModelBuilder(builder,
                    PackResource.of("minecraft:item/" + baseKey, p -> p
                            .resolve("assets/minecraft/models/item")
                            .resolve(baseKey.getKey() + ".json")
                    ), baseKey
                ))
            );

            // get all model holders
            ArrayList<ModelHolder<T>> modelHolders = entry.getValue();

            // iterate over all model holders
            for (ModelHolder<T> modelHolder : modelHolders) {
                // get model offset
                int off = modelHolder.getCustomModelDataOffset();

                // loop over models and build
                for (SpecifiedIdentifier modelAsset : modelHolder.getModels()) {
                    // add override
                    baseModelBuilder.addOverride(
                            new BaseItemModelBuilder.PredicateOverride()
                                .setModelName(modelAsset.toString())
                                .setCustomModelData(off)
                    );

                    off++;
                }
            }
        }

    }

    public class BaseEditing {

        private final Material base;
        private final ArrayList<ModelHolder<T>> list;

        public BaseEditing(Material base) {
            this.base = base;
            this.list = cmds.computeIfAbsent(base, __ -> new ArrayList<>());
        }

        public int next(ModelHolder<T> holder) {
            int i = list.size() + 1;
            list.add(holder);
            return i;
        }

    }

}
