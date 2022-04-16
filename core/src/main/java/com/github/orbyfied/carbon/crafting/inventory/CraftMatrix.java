package com.github.orbyfied.carbon.crafting.inventory;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a crafting matrix/inventory.
 * Can be any inventory with input and output
 * slots, like crafting tables and furnaces.
 */
public class CraftMatrix implements Cloneable {

    /**
     * The input matrix.
     */
    protected SlotContainer input;

    /**
     * The output slots.
     */
    protected SlotContainer output;

    /**
     * The slots containing the values for
     * the ingredients by their ingredient tag.
     */
    protected HashMap<Object, Slot> taggedSlots;

    public CraftMatrix input(SlotContainer slots) {
        this.input = slots;
        return this;
    }

    public CraftMatrix input(Slot... slots) {
        this.input = new SlotContainer(Arrays.asList(slots));
        return this;
    }

    public SlotContainer input() {
        return input;
    }

    public SlotContainer output() {
        return output;
    }

    public CraftMatrix output(SlotContainer c) {
        this.output = c;
        return this;
    }

    public CraftMatrix put(Object o, Slot slot) {
        this.taggedSlots.put(o, slot);
        return this;
    }

    public Slot get(Object o) {
        return this.taggedSlots.get(o);
    }

    /**
     * Clean up any free space around
     * the block of items. Used in
     * shaped crafting.
     * NOTE: Affects the current matrix.
     * Clone it if you want to prevent this.
     * @return This.
     */
    public CraftMatrix normalize() {
        // TODO: implement normalization

        // return
        return this;
    }

    ///////////////////////////////////////////

    @Override
    public CraftMatrix clone() {
        try {
            return (CraftMatrix) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CraftMatrix that = (CraftMatrix) o;
        return Objects.equals(input, that.input) && Objects.equals(output, that.output);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(output);
        result = 31 * result + Objects.hash(input);
        return result;
    }

}
