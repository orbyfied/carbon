package com.github.orbyfied.carbon.command.parameter;

import com.github.orbyfied.carbon.command.*;
import com.github.orbyfied.carbon.command.exception.NodeParseException;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.StringReader;
import org.bukkit.event.Event;

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
        int startIndex = reader.index();
        Object v;

        try {
            // parse value
            v = type.parse(ctx, reader);
        } catch (Exception e) {
            if (e instanceof NodeParseException) {
                throw e;
            }

            int endIndex = reader.index();
            throw new NodeParseException(
                    node.getRoot(),
                    node,
                    new ErrorLocation(reader, startIndex, endIndex),
                    "internal exception",
                    e
            );

        }

        ctx.setArg(identifier, v);
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
