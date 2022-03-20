package com.github.orbyfied.carbon.util.json2;

public class JsonContext {

    private int indent = 0;

    public JsonContext indent(int a) {
        this.indent += a;
        return this;
    }

    public JsonContext setIndent(int i) {
        this.indent = i;
        return this;
    }

    public int getIndent() {
        return indent;
    }

}
