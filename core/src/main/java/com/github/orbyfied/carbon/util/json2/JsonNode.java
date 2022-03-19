package com.github.orbyfied.carbon.util.json2;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public abstract class JsonNode<T> {

    protected T value;
    protected ArrayList<JsonNode<?>> nodes = new ArrayList<>();

}
