package com.github.orbyfied.carbon.content.pack;

import com.google.gson.JsonObject;

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
    public JsonObject writeJson(JsonObject doc) {
        JsonObject in = new JsonObject();
        in.addProperty("pack_format", packFormat);
        in.addProperty("description", description);
        doc.add("pack", in);
        return doc;
    }

    @Override
    public void readJson(JsonObject doc) {
        JsonObject nest = doc.getAsJsonObject("pack");
        packFormat  = nest.get("pack_format").getAsInt();
        description = nest.get("description").getAsString();
    }
}
