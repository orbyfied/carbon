package net.orbyfied.carbon.command;

import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.util.StringReader;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Optional;

public class Context {

    public Context(CommandEngine engine,
                   CommandSender sender) {
        this.engine = engine;
        this.sender = sender;
    }

    /**
     * The sender of the command.
     */
    protected final CommandSender sender;

    /**
     * The purspose of this invocation.
     */
    protected Destiny destiny;

    /**
     * The root command node.
     */
    protected Node rootCommand;

    /**
     * The list of arguments, parameters and symbols.
     */
    protected final HashMap<Identifier, Object> symbols = new HashMap<>();

    /**
     * Options usable in parsing.
     */
    protected final HashMap<Identifier, Object> options = new HashMap<>();

    /**
     * The command engine.
     */
    protected final CommandEngine engine;

    /**
     * The intermediate status text.
     */
    protected String intermediateText;

    /**
     * If the text can be formatted.
     */
    protected boolean canFormat = true;

    /**
     * If the invocation was successful.
     */
    protected Boolean successful;

    /**
     * The current string reader used for parsing.
     */
    protected StringReader reader;

    /**
     * The current node we are at.
     */
    protected Node current;

    /**
     * The last/current executable node.
     */
    protected Executable currentExecutable;

    /* ----- Basic Manipulation ----- */

    public Context canFormat(boolean canFormat) {
        this.canFormat = canFormat;
        return this;
    }

    public CommandEngine engine() {
        return engine;
    }

    public CommandSender sender() {
        return sender;
    }

    public Destiny destiny() {
        return destiny;
    }

    public String intermediateText() {
        return intermediateText;
    }

    public Context intermediateText(String text) {
        if (!canFormat)
            text = ChatColor.stripColor(text);
        this.intermediateText = text;
        return this;
    }

    public Context successful(boolean b) {
        this.successful = b;
        return this;
    }

    public Boolean successful() {
        return successful;
    }

    public Context destiny(Destiny destiny) {
        this.destiny = destiny;
        return this;
    }

    public Node rootCommand() {
        return rootCommand;
    }

    public StringReader reader() {
        return reader;
    }

    public Executable currentExecutable() {
        return currentExecutable;
    }

    public Node currentNode() {
        return current;
    }

    /* ----- Symbols ----- */

    public HashMap<Identifier, Object> getSymbols() {
        return symbols;
    }

    @SuppressWarnings("unchecked")
    public <T> T getSymbol(Identifier identifier) {
        return (T) symbols.get(identifier);
    }

    public <T> T getSymbol(String id) {
        return getSymbol(Identifier.of(id));
    }

    @SuppressWarnings("unchecked")
    public <T> T getSymbol(Identifier identifier, Class<T> tClass) {
        return (T) symbols.get(identifier);
    }

    public <T> T getSymbol(String id, Class<T> tClass) {
        return getSymbol(Identifier.of(id), tClass);
    }

    public Context setSymbol(Identifier id, Object o) {
        symbols.put(id, o);
        return this;
    }

    public Context setSymbol(String id, Object o) {
        return setOption(Identifier.of(id), o);
    }

    public Context unsetSymbol(Identifier id) {
        symbols.remove(id);
        return this;
    }

    public Context unsetSymbol(String id) {
        return unsetSymbol(Identifier.of(id));
    }

    /* ----- Options ----- */

    public HashMap<Identifier, Object> getOptions() {
        return symbols;
    }

    public <T> Optional<T> getLocalOption(String identifier, Class<T> tClass) {
        return getOption(new Identifier(current.name, identifier));
    }

    public <T> Optional<T> getLocalOption(String identifier) {
        return getOption(new Identifier(current.name, identifier));
    }

    public Context setLocalOption(String identifier, Object o) {
        return setOption(new Identifier(current.name, identifier), o);
    }

    public Context unsetLocalOption(String identifier) {
        return unsetOption(new Identifier(current.name, identifier));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOption(Identifier identifier) {
        return (Optional<T>) Optional.ofNullable(options.get(identifier));
    }

    public <T> Optional<T> getOption(String identifier) {
        return getOption(Identifier.of(identifier));
    }

    public Context setOption(Identifier identifier, Object o) {
        options.put(identifier, o);
        return this;
    }

    public Context setOption(String id, Object o) {
        return setOption(Identifier.of(id), o);
    }

    public Context unsetOption(Identifier id) {
        options.remove(id);
        return this;
    }

    public Context unsetOption(String id) {
        options.remove(Identifier.of(id));
        return this;
    }

    ///////////////////////////////////

    /**
     * Declares the purposes/destinies
     * of an invocation.
     * @see Context#destiny
     */
    public enum Destiny {

        SUGGEST,
        EXECUTE

    }

}
