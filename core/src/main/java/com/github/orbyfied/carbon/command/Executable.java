package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.command.impl.CommandNodeExecutor;
import com.github.orbyfied.carbon.util.StringReader;

public class Executable
        extends AbstractNodeComponent
        implements Selecting, Functional, Completer {

    public Executable(Node node) {
        super(node);
    }

    private CommandNodeExecutor walkExecutor;
    private CommandNodeExecutor executor;

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
        return true;
    }

    @Override
    public void walked(Context ctx, StringReader reader) {
        if (walkExecutor != null)
            executor.execute(ctx, node);
    }

    @Override
    public void execute(Context ctx) {
        if (executor != null)
            executor.execute(ctx, node);
    }

    @Override
    public void completeSelf(Context context, Node from, Suggestions suggestions) {
        suggestions.suggest(node.getName());
    }

}
