package com.github.orbyfied.carbon.content.pack;

import com.github.orbyfied.carbon.content.pack.service.JsonPackService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class JsonAssetBuilder extends PackAssetBuilder {

    protected Gson json;

    protected Charset charset = StandardCharsets.UTF_8;

    public JsonAssetBuilder(ResourcePackBuilder parent, PackResource loc) {
        super(parent, loc);
        this.json = parent.getService(JsonPackService.class).getGson();
    }

    public abstract JsonObject writeJson(JsonObject doc);
    public abstract void readJson(JsonObject doc);

    @Override
    public void write(OutputStream out) throws IOException {
        JsonObject doc = new JsonObject();
        doc = writeJson(doc);
        out.write(json.toJson(doc).getBytes(charset));
    }

    @Override
    public void read(InputStream in) throws IOException {
        JsonObject doc = json.fromJson(new InputStreamReader(in), JsonObject.class);
        readJson(doc);
    }

}
