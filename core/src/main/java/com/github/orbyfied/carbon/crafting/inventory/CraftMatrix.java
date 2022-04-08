package com.github.orbyfied.carbon.crafting.inventory;

/**
 * Represents a crafting matrix/inventory.
 * Can be any inventory with input and output
 * slots, like crafting tables and furnaces.
 */
public class CraftMatrix {

    /**
     * The input matrix.
     */
    protected Slot[] input;

    /**
     * The output slots.
     */
    protected SlotContainer output;

    public Slot[] getInput() {
        return input;
    }

    public SlotContainer getOutput() {
        return output;
    }

}
