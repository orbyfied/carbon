package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.util.StringReader;

public class Parameter
        extends AbstractNodeComponent
        implements Functional, Suggester {

    public Parameter(Node node) {
        super(node);
    }

    @Override
    public void walked(Context ctx, StringReader reader) {

    }

    @Override
    public void execute(Context ctx) {

    }

    @Override
    public void suggest(Context ctx, Suggestions builder) {

    }

}
