package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.registry.Identifier;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;

public class Context {

    public Context(CommandEngine engine,
                   CommandSender sender) {
        this.engine = engine;
        this.sender = sender;
    }

    protected final CommandSender sender;

    protected Destiny destiny;

    protected Node rootCommand;

    protected final HashMap<Identifier, Object> args = new HashMap<>();

    protected final CommandEngine engine;

    protected String intermediateText;

    protected boolean canFormat;

    protected boolean successful;

    public Context setCanFormat(boolean canFormat) {
        this.canFormat = canFormat;
        return this;
    }

    public CommandEngine getEngine() {
        return engine;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Destiny getDestiny() {
        return destiny;
    }

    public String getIntermediateText() {
        return intermediateText;
    }

    public Context setIntermediateText(String text) {
        if (!canFormat)
            text = ChatColor.stripColor(text);
        this.intermediateText = text;
        return this;
    }

    public Context setSuccessful(boolean b) {
        this.successful = b;
        return this;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Context setDestiny(Destiny destiny) {
        this.destiny = destiny;
        return this;
    }

    public Node getRootCommand() {
        return rootCommand;
    }

    public HashMap<Identifier, Object> getArgs() {
        return args;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArg(Identifier identifier) {
        return (T) args.get(identifier);
    }

    public <T> T getArg(String id) {
        return getArg(Identifier.of(id));
    }

    @SuppressWarnings("unchecked")
    public <T> T getArg(Identifier identifier, Class<T> tClass) {
        return (T) args.get(identifier);
    }

    public <T> T getArg(String id, Class<T> tClass) {
        return getArg(Identifier.of(id), tClass);
    }

    public Context setArg(Identifier id, Object o) {
        args.put(id, o);
        return this;
    }

    public enum Destiny {

        SUGGEST,
        EXECUTE

    }

}
