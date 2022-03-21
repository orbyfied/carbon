package com.github.orbyfied.carbon.content;

import com.github.orbyfied.carbon.content.pack.CopyAssetBuilder;
import com.github.orbyfied.carbon.content.pack.PackResource;
import com.github.orbyfied.carbon.content.pack.ResourcePackBuilder;
import com.github.orbyfied.carbon.content.pack.SourcedAsset;
import com.github.orbyfied.carbon.element.RegistrableElement;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.registry.RegistryService;
import com.github.orbyfied.carbon.util.resource.ResourceHandle;
import org.bukkit.Material;

import java.util.*;

public class CMDRegistryService<T extends RegistrableElement>
        extends RegistryService<Registry<T>, T>
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
        int i = list.size();
        list.add(holder);
        return i;
    }

    public ModelHolder<T> getHolderOf(Material base, int off) {
        ArrayList<ModelHolder<T>> holders;
        if ((holders = cmds.get(base)) == null) return null;
        if (off >= holders.size() || off < 0) return null;
        return holders.get(off);
    }

    public SourcedAsset getModelOf(Material base, int off) {
        ModelHolder<T> h = getHolderOf(base, off);
        return h.getModel(off - h.getCustomModelDataOffset());
    }

    public BaseEditing edit(Material base) {
        return new BaseEditing(base);
    }

    @Override
    public void prepareAssets(ResourcePackBuilder builder) {
        Set<Map.Entry<Material, ArrayList<ModelHolder<T>>>> entries = cmds.entrySet();
        for (Map.Entry<Material, ArrayList<ModelHolder<T>>> entry : entries) {
            Material base = entry.getKey();
            ArrayList<ModelHolder<T>> modelHolders = entry.getValue();
            for (ModelHolder<T> modelHolder : modelHolders) {
                int off = modelHolder.getCustomModelDataOffset();
                for (SourcedAsset modelAsset : modelHolder.getModels()) {
                    off++;
                    // build actual model
                    builder.asset((b) ->
                            new CopyAssetBuilder(b, modelAsset.getResource())
                    ).setSource(modelAsset.getSource());
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
            int i = list.size();
            list.add(holder);
            return i;
        }

    }

}
