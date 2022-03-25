package com.github.orbyfied.carbon.command.parameter;

import com.github.orbyfied.carbon.command.*;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.StringReader;

public class Parameter
        extends AbstractNodeComponent
        implements Functional, Suggester {

    private Identifier identifier;

    public Parameter(Node node) {
        super(node);
        Node parent = node;
        while ((parent = parent.getParent()).hasComponentOf(Parameter.class)) { }
        identifier = new Identifier(parent.getName(), node.getName());
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
