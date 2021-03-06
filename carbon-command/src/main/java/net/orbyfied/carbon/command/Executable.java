package net.orbyfied.carbon.command;

import net.orbyfied.carbon.command.impl.CommandNodeExecutor;
import net.orbyfied.carbon.util.StringReader;

import java.util.Set;

public class Executable
        extends AbstractNodeComponent
        implements Selecting, Functional, Completer {

    public Executable(Node node) {
        super(node);
    }

    private CommandNodeExecutor walkExecutor;
    private CommandNodeExecutor executor;

    private Set<String> aliases = Set.of(node.getAliases().toArray(new String[0]));

    public Executable setWalkExecutor(CommandNodeExecutor e) {
        this.walkExecutor = e;
        return this;
    }

    public Executable setExecutor(CommandNodeExecutor e) {
        this.executor = e;
        return this;
    }

    @Override
    public boolean selects(Context ctx, StringReader reader) {
        String s = reader.collect(c -> c != ' ');
        if (s.equals(node.name))
            return true;
        return aliases.contains(s);
    }

    @Override
    public void walked(Context ctx, StringReader reader) {
        reader.collect(c -> c != ' ');
        if (walkExecutor != null)
            executor.execute(ctx, node);
    }

    @Override
    public void execute(Context ctx) {
        if (executor != null)
            executor.execute(ctx, node);
    }

    @Override
    public void completeSelf(Context context, Node from, SuggestionAccumulator suggestions) {
        suggestions.suggest(node.getName());
    }

}
