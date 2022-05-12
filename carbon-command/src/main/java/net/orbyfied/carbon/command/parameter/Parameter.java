package net.orbyfied.carbon.command.parameter;

import net.orbyfied.carbon.command.exception.NodeParseException;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.util.StringReader;
import net.orbyfied.carbon.command.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Parameter
        extends AbstractNodeComponent
        implements Functional, Selecting, Completer {

    protected Identifier identifier;

    protected ParameterType<?> type;

    protected LinkedHashMap<String, Supplier<Object>> options = new LinkedHashMap<>();

    public Parameter(Node node) {
        super(node);
        Node parent = node;
        while ((parent = parent.parent()).hasComponentOf(Parameter.class)) { }
        identifier = new Identifier(parent.getName(), node.getName());
    }

    public Parameter setOption(String id, Supplier<Object> supplier) {
        options.put(id, supplier);
        return this;
    }

    public Parameter setOption(String id, Object supplied) {
        options.put(id, () -> supplied);
        return this;
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

    private void putOptions(Context context) {
        for (Map.Entry<String, Supplier<Object>> entry : options.entrySet())
            context.setLocalOption(entry.getKey(), entry.getValue().get());
    }

    private void remOptions(Context context) {
        for (String key : options.keySet())
            context.unsetLocalOption(key);
    }

    @Override
    public void walked(Context ctx, StringReader reader) {
        int startIndex = reader.index();
        Object v;

        putOptions(ctx);

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

        remOptions(ctx);

        ctx.setSymbol(identifier, v);
    }

    @Override
    public void execute(Context ctx) { }

    @Override
    public boolean selects(Context ctx, StringReader reader) {
        putOptions(ctx);
        boolean b = type.accepts(ctx, reader);
        remOptions(ctx);
        return b;
    }

    @Override
    public void completeSelf(Context context, Node from, SuggestionAccumulator suggestions) {
        putOptions(context);
        type.suggest(context, suggestions);
        remOptions(context);
    }

}
