package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.command.exception.CommandException;
import com.github.orbyfied.carbon.command.exception.CommandExecutionException;
import com.github.orbyfied.carbon.command.exception.NodeExecutionException;
import com.github.orbyfied.carbon.command.parameter.Parameter;
import com.github.orbyfied.carbon.command.parameter.TypeResolver;
import com.github.orbyfied.carbon.util.StringReader;
import net.md_5.bungee.api.ChatColor;
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
     * @param ctxConsumer A consumer for configuring the context.
     * @return The context for optional further use.
     */
    public Context dispatch(CommandSender sender,
                            String str,
                            Suggestions suggestions,
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
                        exec.walked(context, reader); // execute walked
                    } catch (Exception e) {
                        // throw the execution exception
                        throw new NodeExecutionException(root, current, e);
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
            System.out.println(suggester);
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
                    lastExecutable.execute(context); // execute walked
                } catch (Exception e) {
                    // throw the execution exception
                    throw new NodeExecutionException(root, lastExecutable.node, e);
                }
            }

        } catch (CommandException e) {
            // handle exception
            context.setIntermediateText(ChatColor.DARK_RED + "ERROR: " + e.getFormattedString());
            context.setSuccessful(false); // fail
        }

        // return
        return context;

    }

}
