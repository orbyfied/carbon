package com.github.orbyfied.carbon.registry;

/**
 * Indicates an item which can be
 * registered in an {@link Registry}.
 * Provides a method to get the unique
 * identifier of the item.
 * @see Identifiable#getIdentifier()
 * @see Registry
 * @see Identifier
 */
public interface Identifiable {

    /**
     * Gets the unique identifier of
     * this registered/registrable item.
     * This should always be the same.
     * @return The identifier.
     */
    Identifier getIdentifier();

    /**
     * Registers this object to the registry.
     * @return This.
     */
    @SuppressWarnings("unchecked")
    default Identifiable register(Registry registry) {
        registry.register(this);
        return this;
    }

}
