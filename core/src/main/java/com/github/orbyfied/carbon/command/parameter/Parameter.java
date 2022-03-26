package com.github.orbyfied.carbon.command.parameter;

import com.github.orbyfied.carbon.command.*;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.StringReader;

public class Parameter
        extends AbstractNodeComponent
        implements Functional, Suggester, Selecting {

    protected Identifier identifier;

    protected ParameterType<?> type;

    public Parameter(Node node) {
        super(node);
        Node parent = node;
        while ((parent = parent.getParent()).hasComponentOf(Parameter.class)) { }
        identifier = new Identifier(parent.getName(), node.getName());
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Parameter setType(ParameterType<?> type) {
        this.type = type;
        return this;
    }

    public ParameterType<?> getType() {
        return type;
    }

    @Override
    public void walked(Context ctx, StringReader reader) {
        ctx.setArg(identifier, type.parse(ctx, reader));
    }

    @Override
    public void suggest(Context ctx, Suggestions builder) {
        type.suggest(ctx, builder);
    }

    @Override
    public void execute(Context ctx) { }

    @Override
    public boolean selects(Context ctx, StringReader reader) {
        return type.accepts(ctx, reader);
    }

}
