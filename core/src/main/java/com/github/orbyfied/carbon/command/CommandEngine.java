package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.command.exception.CommandException;
import com.github.orbyfied.carbon.command.exception.NodeExecutionException;
import com.github.orbyfied.carbon.command.impl.DelegatingNamespacedTypeResolver;
import com.github.orbyfied.carbon.command.impl.SystemParameterType;
import com.github.orbyfied.carbon.command.parameter.Parameter;
import com.github.orbyfied.carbon.command.parameter.TypeResolver;
import com.github.orbyfied.carbon.util.StringReader;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * The engine of the command system.
 */
public abstract class CommandEngine {

    /**
     * The parameter type resolver.
     */
    TypeResolver typeResolver;

    /**
     * The commands registered.
     */
    ArrayList<Node> commands = new ArrayList<>();

    /**
     * The aliases registered mapped
     * to the nodes.
     */
    HashMap<String, Node> aliases = new HashMap<>();

    public CommandEngine() {
        typeResolver = new DelegatingNamespacedTypeResolver()
                .namespace("system",    SystemParameterType.typeResolver);
    }

    public CommandEngine register(Node command) {
        commands.add(command);
        aliases.put(command.getName(), command);
        for (String alias : command.aliases)
            aliases.put(alias, command);
        registerPlatform(command);
        return this;
    }

    public CommandEngine unregister(Node command) {
        commands.remove(command);
        aliases.remove(command.getName(), command);
        for (String alias : command.aliases)
            aliases.remove(alias, command);
        unregisterPlatform(command);
        return this;
    }

    /**
     * Set the type resolver.
     * @param resolver It.
     * @return This.
     */
    public CommandEngine setTypeResolver(TypeResolver resolver) {
        this.typeResolver = resolver;
        return this;
    }

    /**
     * Get the type resolver.
     * @return It.
     */
    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    /**
     * Should do whatever it needs to do when
     * a node gets registered to make it work.
     * @param root The command node.
     */
    protected abstract void registerPlatform(Node root);

    /**
     * Should do whatever it needs to do when
     * a node gets unregistered to make it work.
     * @param root The command node.
     * @implNote Optional. Unregistration won't be used much.
     */
    protected abstract void unregisterPlatform(Node root);

    /**
     * Should prepare the command engine for
     * usage. Called whenever the system
     * is ready for operation.
     */
    public abstract void enablePlatform();

    /**
     * Dispatches a suggestion or invocation
     * request for a command.
     * @param sender The command sender.
     * @param str The command string.
     * @param suggestions The suggestion builder.
     *                    If this is null, the request is invocation,
     *                    otherwise the request will be set to suggestion.
     * @param ctxConsumer A consumer for configuring the context.
     * @return The context for optional further use.
     */
    public Context dispatch(CommandSender sender,
                            String str,
                            SuggestionAccumulator suggestions,
                            Consumer<Context> ctxConsumer) {

        // get mode (execute or suggest)
        boolean isSuggesting    = suggestions != null;
        Context.Destiny destiny = (isSuggesting ? Context.Destiny.SUGGEST : Context.Destiny.EXECUTE);

        // create string reader
        StringReader reader = new StringReader(str, 0);

        // create context
        Context context = new Context(this, sender);
        context.setDestiny(destiny);
        if (ctxConsumer != null)
            ctxConsumer.accept(context);
        context.setSuccessful(true);

        // parse alias and get command
        String alias = reader.collect(c -> c != ' ', 0);
        final Node root = aliases.get(alias);
        if (root == null) { // handle if no command exists
            // TODO: better error handling
            return null;
        }

        context.rootCommand = root;

        // error handling
        try {

            // walk root
            Executable lastExecutable = null; // the executable to execute at the end
            Selecting mainc = root.getComponentOf(Selecting.class); // bring out to walk root too
            Suggester suggester = null; // last suggester
            Node current = root;
            while (true) {

                // is executable
                if (mainc instanceof Executable exec) {
                    lastExecutable = exec;
                    try {
                        // execute walked
                        exec.walked(context, reader);
                    } catch (Exception e) {
                        // dont create a massive chain of exceptions
                        if (e instanceof NodeExecutionException nex) {
                            // throw the exception itself
                            throw nex;
                        } else {
                            // throw the execution exception
                            throw new NodeExecutionException(root, current, e);
                        }
                    }

                    // is parameter
                } else if (mainc instanceof Parameter param) {
                    // parse and save parameter
                    param.walked(context, reader);
                }

                // suggest
                Suggester tempSuggester;
                if (isSuggesting && (tempSuggester = current.getComponentOf(Suggester.class)) != null)
                    suggester = tempSuggester;

                // skip to next character
                reader.next();

                // get main functional component
                // and set current to new node
                mainc = current.getSubnode(context, reader);

                // break if we ended
                if (reader.current() == StringReader.DONE ||
                        mainc == null) {
                    current = null;
                    break;
                }

                // get current node
                current = mainc.getNode();

            }

            // suggest
            if (isSuggesting && suggester != null)
                suggester.suggestNext(
                        context,
                        suggestions,
                        reader,
                        current
                );

            // execute
            if (lastExecutable != null && !isSuggesting && context.isSuccessful()) {
                try {
                    // execute final command
                    lastExecutable.execute(context);
                } catch (Exception e) {
                    // dont create a massive chain of exceptions
                    if (e instanceof NodeExecutionException nex) {
                        // throw the exception itself
                        throw nex;
                    } else {
                        // throw the execution exception
                        throw new NodeExecutionException(root, lastExecutable.node, e);
                    }
                }
            }

        } catch (CommandException e) {
            // print stack trace if severe enough
            if (e.isSevere())
                e.printStackTrace();

            // communicate with sender
            context.setIntermediateText(e.getFormattedString());
            context.setSuccessful(false); // fail
        }

        // return
        return context;

    }

}
