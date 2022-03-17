package com.github.orbyfied.carbon.registry;

/**
 * Indicates an item which can be
 * registered in an {@link Registry}.
 * Provides a method to get the unique
 * identifier of the item.
 * @see RegistryItem#getIdentifier()
 * @see Registry
 * @see Identifier
 */
public interface RegistryItem {

    /**
     * Gets the unique identifier of
     * this registered/registrable item.
     * This should always be the same.
     * @return The identifier.
     */
    Identifier getIdentifier();

}
