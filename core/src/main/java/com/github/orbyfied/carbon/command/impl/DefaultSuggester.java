package com.github.orbyfied.carbon.command.impl;

import com.github.orbyfied.carbon.command.*;

public class DefaultSuggester
        extends AbstractNodeComponent
        implements Suggester {

    public DefaultSuggester(Node node) {
        super(node);
    }

    @Override
    public void suggest(Context ctx, Suggestions builder) {
        if (node.hasComponentOf(Parameter.class)) {
            node.getComponent(Parameter.class).suggest(ctx, builder);
            return;
        }

        for (Node n : node.getChildren())
            if (n.hasComponentOf(Executable.class))
                builder.suggest(n.getName());
    }

}
