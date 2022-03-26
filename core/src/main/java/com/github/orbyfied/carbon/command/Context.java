package com.github.orbyfied.carbon.command;

import com.github.orbyfied.carbon.registry.Identifier;
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

    protected final HashMap<Identifier, Object> args = new HashMap<>();

    protected final CommandEngine engine;

    public CommandEngine getEngine() {
        return engine;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Destiny getDestiny() {
        return destiny;
    }

    public Context setDestiny(Destiny destiny) {
        this.destiny = destiny;
        return this;
    }

    public HashMap<Identifier, Object> getArgs() {
        return args;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArg(Identifier identifier, Class<T> tClass) {
        return (T) args.get(identifier);
    }

    public <T> T getArg(String id, Class<T> tClass) {
        return getArg(Identifier.of(id), tClass);
    }

    public enum Destiny {

        SUGGEST,
        EXECUTE

    }

}
