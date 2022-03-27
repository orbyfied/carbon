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
        if (fromIndex > toIndex) {
            fromIndex = toIndex;
            toIndex   = fromIndex;
        }
        this.fromIndex = Math.min(reader.getString().length() - 1, fromIndex);
        this.toIndex   = Math.max(reader.getString().length() - 1, toIndex);
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
