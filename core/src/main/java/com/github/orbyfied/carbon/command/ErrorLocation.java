package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.util.StringReader;

public class ErrorLocation {

    protected int fromIndex;
    protected int toIndex;

    protected StringReader reader;

    public ErrorLocation(StringReader reader,
                         int fromIndex,
                         int toIndex) {
        this.reader = reader;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public int getStartIndex() {
        return fromIndex;
    }

    public int getEndIndex() {
        return toIndex;
    }

    public StringReader getReader() {
        return reader;
    }

}
