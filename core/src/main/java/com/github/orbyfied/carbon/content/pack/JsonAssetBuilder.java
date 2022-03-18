package com.github.orbyfied.carbon.content.pack;

import com.github.orbyfied.carbon.util.json.JsonDocument;
import com.github.orbyfied.carbon.util.json.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class JsonAssetBuilder extends PackAssetBuilder {

    public JsonAssetBuilder(ResourcePackBuilder parent, PackResource loc) {
        super(parent, loc);
    }

    public abstract void writeJson(JsonDocument doc);
    public abstract void  readJson(JsonDocument doc);

    @Override
    public void write(OutputStream out) throws IOException {
        JsonDocument doc = new JsonDocument();
        writeJson(doc);
        out.write(doc.toJson().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void read(InputStream in) throws IOException {
        JsonDocument doc = JsonDocument.fromJson(
                new String(in.readAllBytes(), StandardCharsets.UTF_8));
        readJson(doc);
    }

}
