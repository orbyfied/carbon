package net.orbyfied.carbon.content.pack.asset;

import net.orbyfied.carbon.content.pack.JsonAssetBuilder;
import net.orbyfied.carbon.content.pack.PackResource;
import net.orbyfied.carbon.content.pack.ResourcePackBuilder;
import com.google.gson.JsonObject;
import net.orbyfied.carbon.util.IOUtil;

import java.nio.charset.StandardCharsets;

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

        this.charset = StandardCharsets.US_ASCII;
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
        in.addProperty("description", IOUtil.escapeUstr(description));
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
