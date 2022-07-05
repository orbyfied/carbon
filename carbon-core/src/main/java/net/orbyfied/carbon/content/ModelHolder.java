package net.orbyfied.carbon.content;

import net.orbyfied.carbon.element.RegistrableElement;
import net.orbyfied.carbon.element.SpecifiedIdentifier;
import org.bukkit.Material;

/**
 * Represents an object that can hold
 * custom model data fueled models.
 * @param <T> The mod element type.
 */
public interface ModelHolder<T extends RegistrableElement> {

    /**
     * Get the offset into the custom model
     * data table of the base material.
     * @return The offset.
     */
    int getCustomModelDataOffset();

    /**
     * Get the model at a specified index.
     * @param off The index.
     * @return The model.
     */
    SpecifiedIdentifier getModel(int off);

    /**
     * Get all models this holder holds.
     * @return All the models.
     */
    SpecifiedIdentifier[] getModels();

    // utility to make life easier
    default int registerAllAndGetOffset(
            Material base,
            CMDRegistryService<T> service,
            int amount) {
        // check for amount
        if (amount == 0) return -1;

        // create edit session for the base material
        CMDRegistryService<T>.BaseEditing edit = service.edit(base);

        // create initial
        int start = edit.next(this);
        // create all others
        // start at one offset because we already inserted the first one
        int off = 1;
        for (; off < amount; off++)
            edit.next(this);

        // return
        return start;
    }

}
