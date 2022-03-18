package com.github.orbyfied.carbon.content.pack;

import com.github.orbyfied.carbon.util.json.JsonDocument;
import com.github.orbyfied.carbon.util.json.JsonObject;

public class PackMetaBuilder extends JsonAssetBuilder {

    public static final PackResource MC_META_RESOURCE = PackResource.of(
            "packmeta",
            path -> path.resolve("pack.mcmeta")
    );

    private ResourcePackBuilder parent;
    private String description;
    private int    packFormat;

    public PackMetaBuilder(ResourcePackBuilder parent) {
        super(parent, MC_META_RESOURCE);
        this.parent = parent;
    }

    public PackMetaBuilder setDescription(String desc) {
        this.description = desc;
        return this;
    }

    public PackMetaBuilder setPackFormat(int format) {
        this.packFormat = format;
        return this;
    }

    @Override
    public void writeJson(JsonDocument doc) {
        doc.set("pack", new JsonObject()
                .set("pack_format", packFormat)
                .set("description", description));
    }

    @Override
    public void readJson(JsonDocument doc) {
        JsonObject nest = doc.getObject("pack");
        packFormat  = nest.getInt("pack_format");
        description = nest.getString("description");
    }
}
