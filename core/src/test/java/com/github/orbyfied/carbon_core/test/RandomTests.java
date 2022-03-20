package com.github.orbyfied.carbon_core.test;

import com.github.orbyfied.carbon.util.json.JsonArray;
import com.github.orbyfied.carbon.util.json.JsonDocument;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class RandomTests {

    public void distTest() {

        List<Object> objs = Arrays.asList(1,2,3,4,5,6,7,8,9);
        Map<Integer, List<Object>> dist = new HashMap<>();
        int y = 3;

    }

    @Test
    public void cTest() throws Exception {
        Callable<?> callable = Executors.callable(() ->
                System.out.println(Thread.currentThread().getName()));

        callable.call();

        new Thread(() -> {
            try {
                callable.call();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

//    @Test
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
