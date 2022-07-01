package net.orbyfied.carbon.test;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.logging.BukkitLogger;
import net.orbyfied.carbon.registry.Identifier;

public abstract class CompiledTest {

    final Identifier id;
    final String     name;

    final Object source;

    public CompiledTest(Identifier id, String name, Object source) {
        this.id   = id;
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public Identifier getId() {
        return id;
    }

    public Object getSource() {
        return source;
    }

    public abstract void execute(Carbon main,
                                 TestManager manager,
                                 BukkitLogger logger) throws Exception;

}
