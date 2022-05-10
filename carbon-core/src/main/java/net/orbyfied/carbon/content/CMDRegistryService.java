package net.orbyfied.carbon.content;

import net.orbyfied.carbon.content.pack.PackResource;
import net.orbyfied.carbon.content.pack.ResourcePackBuilder;
import net.orbyfied.carbon.content.pack.asset.BaseItemModelBuilder;
import net.orbyfied.carbon.content.pack.service.MinecraftAssetService;
import net.orbyfied.carbon.element.RegistrableElement;
import net.orbyfied.carbon.element.SpecifiedIdentifier;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.registry.AbstractRegistryService;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.*;

/**
 * Registry service for CustomModelData registration.
 * Provides and builds assets for the resource pack
 * automatically too.
 * @param <T> The element type.
 * @see AssetPreparingService
 * @see net.orbyfied.carbon.registry.RegistryService
 */
public class CMDRegistryService<T extends RegistrableElement>
        extends AbstractRegistryService<Registry<T>, T>
        implements AssetPreparingService {

    public CMDRegistryService(Registry<T> registry) {
        super(registry);
    }

    /**
     * The map of taken custom model data values
     * by base material.
     */
    private HashMap<Material, ArrayList<ModelHolder<T>>> cmds = new HashMap<>(); // raw custom model data counter

    /**
     * How many CMD values have been taken in total.
     */
    private int takenTotal = 0;

    /* Getters. */

    public HashMap<Material, ArrayList<ModelHolder<T>>> getCMDRegistry() {
        return cmds;
    }

    public int getTotalTaken() {
        return takenTotal;
    }

    /**
     * Allocates a new CMD cell from the specified
     * base material for the provided model holder.
     * @param base The base material.
     * @param holder The holder.
     * @return The index of the entry.
     */
    public int next(Material base, ModelHolder<T> holder) {
        // get list of base material
        ArrayList<ModelHolder<T>> list = cmds.computeIfAbsent(base, __ -> new ArrayList<>());

        // calculate index
        int i = list.size() + 1;

        // add value
        list.add(holder);
        takenTotal++;

        // return index
        return i;
    }

    /**
     * Get the holder of a CMD in the specified
     * base material. Returns null if the CMD is out
     * of bounds or if the list hasn't been allocated
     * at all yet.
     * @param base The base material.
     * @param off The CMD.
     * @return The model holder.
     */
    public ModelHolder<T> getHolderOf(Material base, int off) {
        // adjust offset because it always starts at 1
        // but list indices always start at 0
        off = off - 1;

        // get and check list
        ArrayList<ModelHolder<T>> holders;
        if ((holders = cmds.get(base)) == null) return null;
        // check index
        if (off >= holders.size() || off < 0) return null;

        // get model holder
        return holders.get(off);
    }

    /**
     * Get the model at the specified CMD.
     * @param base The base material.
     * @param off The offset.
     * @return The model identifier.
     */
    public SpecifiedIdentifier getModelOf(Material base, int off) {
        // get holder of cell
        ModelHolder<T> h = getHolderOf(base, off);
        // get model from relative offset
        return h.getModel(off - /* start at 0 */ 1 - h.getCustomModelDataOffset());
    }

    /**
     * Creates a new editing session for the
     * base material. Makes allocation faster
     * as it doesn't have to look up by the
     * base material every time.
     * @param base The base material.
     * @return The edit session.
     */
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
            takenTotal++;
            return i;
        }

    }

}
