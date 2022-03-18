package com.github.orbyfied.carbon_core.test;

import com.github.orbyfied.carbon.util.StringReader;
import com.github.orbyfied.carbon.util.StructStringBuilder;
import com.github.orbyfied.carbon.util.json.JsonArray;
import com.github.orbyfied.carbon.util.json.JsonDocument;
import com.github.orbyfied.carbon.util.json.JsonObject;
import com.github.orbyfied.carbon.util.json.JsonValueSpec;
import com.github.orbyfied.carbon.util.resource.ResourceHandle;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RandomTests {

    @Test
    public void jsonTest() {
        JsonDocument doc1 = new JsonDocument();
        doc1
                .set("numbera", 1)
                .set("str", "hi")
                .set("numberb", 3.8)
                .set("bool", true)
                .set("character", 'a')
                .set("myArray", new JsonArray()
                        .add(1).add(2).add("hello"));

        String doc1str = doc1.toJson();
        System.out.println(doc1str);
        JsonDocument doc1Parsed = JsonDocument.fromJson(doc1str);
        System.out.println(doc1Parsed);

    }

}
