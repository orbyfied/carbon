package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.util.StringReader;

public class Executable
        extends AbstractNodeComponent
        implements Selecting, Functional {

    public Executable(Node node) {
        super(node);
    }

    @Override
    public boolean selects(StringReader reader) {
        return false;
    }

    @Override
    public void walked(Invocation ctx, StringReader reader) {

    }

    @Override
    public void execute(Invocation ctx) {

    }

}
