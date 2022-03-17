package com.github.orbyfied.carbon.item;

import com.github.orbyfied.carbon.element.RegistrableElement;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.registry.RegistryItem;

/**
 * A custom item type.
 */
public class CarbonItem<S extends CarbonItemState<?>> extends RegistrableElement {

    /**
     * The identifier of this item.
     * Storing it as a variable makes
     * implementation easier.
     */
    protected final Identifier identifier;

    /**
     * Internal Constructor.
     * It is supposed to be called
     * using {@code super(...)} inside
     * of an item class to supply an identifier.
     * @param id The identifier of this block.
     */
    public CarbonItem(Identifier id) {
        this.identifier = id;
    }

    /**
     * @see RegistryItem#getIdentifier()
     */
    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @SuppressWarnings("unchecked")
    public S newState() {
        return (S) new CarbonItemState<>(this);
    }

}
