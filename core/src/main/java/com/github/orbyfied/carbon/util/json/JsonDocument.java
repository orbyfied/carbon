package com.github.orbyfied.carbon.util.json;

import com.github.orbyfied.carbon.util.StringReader;

public class JsonDocument extends JsonObject {

    public JsonDocument() {
        super();
    }

    public String toJson() {
        return JsonValueSpec.JSON_OBJECT_PRIMITIVE.toJson(this, 0);
    }

    public static JsonDocument fromJson(String s) {
        JsonDocument doc = new JsonDocument();
        doc.map = JsonValueSpec.JSON_OBJECT_PRIMITIVE.fromJson(new StringReader(s, 0)).map;
        return doc;
    }

}
