package com.github.orbyfied.carbon.util.json2;

import java.util.ArrayList;

public abstract class JsonNode<T> {

    protected T value;
    protected ArrayList<JsonNode<?>> nodes = new ArrayList<>();

    public abstract void parse();
    public abstract void write();

}
