package net.orbyfied.carbon.command.parameter;

import net.orbyfied.carbon.command.exception.NodeParseException;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.util.StringReader;
import net.orbyfied.carbon.command.*;

public class Parameter
        extends AbstractNodeComponent
        implements Functional, Selecting, Completer {

    protected Identifier identifier;

    protected ParameterType<?> type;

    public Parameter(Node node) {
        super(node);
        Node parent = node;
        while ((parent = parent.getParent()).hasComponentOf(Parameter.class)) { }
        identifier = new Identifier(parent.getName(), node.getName());
    }

    public Parameter setIdentifier(Identifier id) {
        this.identifier = id;
        return this;
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
                    e
            );

        }

        ctx.setArg(identifier, v);
    }

    @Override
    public void execute(Context ctx) { }

    @Override
    public boolean selects(Context ctx, StringReader reader) {
        return type.accepts(ctx, reader);
    }

    @Override
    public void completeSelf(Context context, Node from, SuggestionAccumulator suggestions) {
        type.suggest(context, suggestions);
    }

}
