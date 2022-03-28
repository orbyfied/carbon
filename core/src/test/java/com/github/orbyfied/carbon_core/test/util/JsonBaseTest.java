package com.github.orbyfied.carbon_core.test.util;

import com.github.orbyfied.carbon.util.StringReader;
import com.github.orbyfied.carbon.util.json.JsonArray;
import com.github.orbyfied.carbon.util.json.JsonObject;
import com.github.orbyfied.carbon.util.json.JsonValueSpec;
import org.junit.jupiter.api.Test;

public class JsonBaseTest {

        /* ---- 1 ---- */

    @Test
    public void testJson() {

        JsonObject obj = new JsonObject();

        obj.set("a", 1)
                .set("b", 2.8)
                .set("c", "hello")
                .set("d", new JsonArray()
                        .add(1)
                        .add(2)
                        .add(3)
                )
                .set("e", new JsonObject()
                        .set("sussy", "baka")
                        .set("fucked", "your mom")
                );

        String s = JsonValueSpec.JSON_OBJECT_PRIMITIVE.toJson(obj, 0);

        System.out.println(s);

        JsonObject backParsed =
                JsonValueSpec.JSON_OBJECT_PRIMITIVE.fromJson(new StringReader(s, 0));

        System.out.println(
                JsonValueSpec.JSON_OBJECT_PRIMITIVE.toJson(backParsed, 0)
        );

    }

}
