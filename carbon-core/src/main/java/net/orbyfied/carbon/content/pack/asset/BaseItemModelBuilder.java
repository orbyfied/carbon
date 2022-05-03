package net.orbyfied.carbon.content.pack.asset;

import net.orbyfied.carbon.content.pack.JsonAssetBuilder;
import net.orbyfied.carbon.content.pack.PackResource;
import net.orbyfied.carbon.content.pack.ResourcePackBuilder;
import net.orbyfied.carbon.content.pack.service.MinecraftAssetService;
import net.orbyfied.carbon.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;

public class BaseItemModelBuilder extends JsonAssetBuilder {

    public BaseItemModelBuilder(ResourcePackBuilder parent, PackResource loc, NamespacedKey key) {
        super(parent, loc);
        this.key = key;
    }

    final NamespacedKey key;

    final ArrayList<ModelOverride> overrides = new ArrayList<>();

    @Override
    public JsonObject writeJson(JsonObject doc) {
        // merge with default minecraft assets
        final MinecraftAssetService mcAssetService = parent.getService(MinecraftAssetService.class);
        JsonObject mcModel = JsonUtil.readJsonObjectFile(json,
                mcAssetService.getAssetsPath().resolve("assets/minecraft/models/item/" + key.getKey() + ".json"));
        if (mcModel == null)
            mcModel = new JsonObject();

        // set overrides
        if (overrides.size() != 0) {
            JsonArray array = new JsonArray();

            for (ModelOverride override : overrides) {
                JsonObject oobj = new JsonObject();
                override.writeFull(oobj);
                array.add(oobj);
            }

            mcModel.add("overrides", array);
        }

        return mcModel;
    }

    @Override
    public void readJson(JsonObject doc) {
        throw new UnsupportedOperationException();
    }

    public BaseItemModelBuilder addOverride(ModelOverride override) {
        overrides.add(override);
        return this;
    }

    ///////////////////////////////////////////

    public interface ModelOverride {
        default void writeFull(JsonObject obj) {
            write(obj);
            obj.addProperty("model", getModel());
        }

        void write(JsonObject obj);
        String getModel();
    }

    public static class PredicateOverride implements ModelOverride {

        String modelName;

        Integer customModelData;

        public PredicateOverride setModelName(String name) {
            this.modelName = name;
            return this;
        }

        public PredicateOverride setCustomModelData(Integer i) {
            this.customModelData = i;
            return this;
        }

        @Override
        public void write(JsonObject obj) {
            JsonObject pred = new JsonObject();
            if (customModelData != null)
                pred.addProperty("custom_model_data", customModelData);
            obj.add("predicate", pred);
        }

        @Override
        public String getModel() {
            return modelName;
        }

    }

}
