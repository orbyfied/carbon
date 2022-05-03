package net.orbyfied.carbon.content.pack.service;

import net.orbyfied.carbon.content.pack.ResourcePackBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonPackService extends PackService {

    public JsonPackService(ResourcePackBuilder builder) {
        super(builder);
    }

    protected final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public Gson getGson() {
        return gson;
    }

}
