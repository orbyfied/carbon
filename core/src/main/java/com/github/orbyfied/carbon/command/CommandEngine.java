package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.command.parameter.Parameter;
import com.github.orbyfied.carbon.command.parameter.TypeResolver;
import com.github.orbyfied.carbon.util.StringReader;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;

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

    public CommandEngine register(Node command) {
        commands.add(command);
        aliases.put(command.getName(), command);
        for (String alias : command.aliases)
            aliases.put(alias, command);
        return this;
    }

    public CommandEngine unregister(Node command) {
        commands.remove(command);
        aliases.remove(command.getName(), command);
        for (String alias : command.aliases)
            aliases.remove(alias, command);
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
     * Dispatches a suggestion or invocation
     * request for a command.
     * @param sender The command sender.
     * @param str The command string.
     * @param suggestions The suggestion builder.
     *                    If this is null, the request is invocation,
     *                    otherwise the request will be set to suggestion.
     * @return The context for optional further use.
     */
    public Context dispatch(CommandSender sender,
                         String str,
                         Suggestions suggestions) {

        // get mode (execute or suggest)
        boolean isSuggesting    = suggestions != null;
        Context.Destiny destiny = (isSuggesting ? Context.Destiny.SUGGEST : Context.Destiny.EXECUTE);

        // create string reader
        StringReader reader = new StringReader(str, 0);

        // parse alias and get command
        String alias = reader.collect(c -> c != ' ', 0);
        final Node root = aliases.get(alias);
        if (root == null) { // handle if no command exists
            // TODO: better error handling
            return null;
        }

        // create context
        Context context = new Context(this, sender);
        context.setDestiny(destiny);

        // walk root
        Executable lastExecutable = null; // the executable to execute at the end
        Selecting mainc = root.getComponentOf(Selecting.class); // bring out to walk root too
        Suggester suggester;
        Node current = root;
        while (true) {

                // is executable
            if (mainc instanceof Executable exec) {
                lastExecutable = exec;
                exec.walked(context, reader); // execute walked

                // is parameter
            } else if (mainc instanceof Parameter param) {
                param.walked(context, reader);
            }

            // suggest
            if (isSuggesting && (suggester = current.getComponentOf(Suggester.class)) != null)
                suggester.suggest(context, suggestions);

            // skip to next character
            reader.next();

            // get main functional component
            // and set current to new node
            mainc = current.getSubnode(context, reader);

            // break if we ended
            if (reader.current() == StringReader.DONE ||
                    mainc == null)
                break;

            // get current node
            current = mainc.getNode();

        }

        // execute
        if (lastExecutable != null && !isSuggesting)
            lastExecutable.execute(context);

        // return
        return context;

    }

}
