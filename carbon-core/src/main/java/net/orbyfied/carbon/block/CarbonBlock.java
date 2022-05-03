package net.orbyfied.carbon.block;

import net.orbyfied.carbon.element.RegistrableElement;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Identifier;


public class CarbonBlock extends RegistrableElement {

    /**
     * The identifier of this block.
     * Storing it as a variable makes
     * implementation easier.
     */
    protected final Identifier identifier;

    /**
     * Internal Constructor.
     * It is supposed to be called
     * using {@code super(...)} inside
     * of a block class to supply an identifier.
     * @param id The identifier of this block.
     */
    public CarbonBlock(Identifier id) {
        this.identifier = id;
    }

    /**
     * @see Identifiable#getIdentifier()
     */
    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

}
