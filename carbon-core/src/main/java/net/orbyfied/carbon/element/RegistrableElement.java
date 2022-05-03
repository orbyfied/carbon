package net.orbyfied.carbon.element;

import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.registry.Identifiable;

/**
 * A mod element/piece of content
 * which can be registered with an
 * Identifier. Gets a magic value
 * assigned as well.
 * @see ModElementRegistry
 */
public abstract class RegistrableElement implements Identifiable {

    /**
     * The registry it was registered to.
     */
    private Registry<? extends RegistrableElement> registry;

    /**
     * The numerical ID/"magic value".
     */
    private int id;

    /**
     * Get the numerical ID.
     * @return The ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the registry this was
     * registered to.
     * @return The registry.
     */
    public Registry<? extends RegistrableElement> getRegistry() {
        return registry;
    }

    /* Internal. */

    void setId(int id) { this.id = id; }
    void setRegistry(Registry<? extends RegistrableElement> reg) { this.registry = reg; }

}
