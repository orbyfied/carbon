package net.orbyfied.carbon.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class JsonUtil {

    public static JsonObject mergeInto(JsonObject a, JsonObject b) {
        Set<Map.Entry<String, JsonElement>> entries = a.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            b.add(entry.getKey(), entry.getValue());
        }
        return b;
    }

    public static JsonObject readJsonObjectFile(Gson gson, Path file) {
        try {
            InputStream is = Files.newInputStream(file);
            JsonObject obj = gson.fromJson(new InputStreamReader(is), JsonObject.class);
            is.close();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
