package com.github.orbyfied.carbon.crafting.inventory;

import com.github.orbyfied.carbon.bootstrap.CarbonBootstrap;
import com.github.orbyfied.carbon.bootstrap.CarbonReport;
import com.github.orbyfied.carbon.logging.BukkitLogger;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

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
    protected HashMap<Object, Slot> taggedSlots = new HashMap<>();

    /**
     * The root matrix.
     * Null if this is the root.
     * Stores the original matrix in
     * the result of any processing
     * function, like {@link CraftMatrix#normalize()}
     */
    protected CraftMatrix root;

    /**
     * The width of the matrix.
     */
    protected int width;

    /**
     * The total size of the matrix.
     */
    protected int size;

    public CraftMatrix dimensions(Integer size,
                                  Integer w) {
        this.size = Objects.requireNonNullElseGet(size, () -> input.getSlots().size());
        this.width = w;
        return this;
    }

    @Nonnull
    public CraftMatrix root() {
        if (root == null)
            return this;
        return root;
    }

    @Nullable
    public CraftMatrix rootRaw() {
        return root;
    }

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

    public int flatten(int x, int y) {
        return y * width + x;
    }

    /**
     * Clean up any free space around
     * the block of items. Used in
     * shaped crafting.Returns a new normalized
     * matrix with a reference to this one.
     * @return The instance.
     */
    public CraftMatrix normalize() {

        List<Slot> slots = input.getSlots();

        // calculate shape
        int tlx = Integer.MAX_VALUE; // top left x (-)
        int tly = Integer.MAX_VALUE; // top left y (-)
        int brx = 0; // bottom l
        int bry = 0;

        int height = size / width;
        for (int x = 0; x < width; x++) { // right
            for (int y = 0; y < height; y++) { // down
                // get
                int im = flatten(x, y);
                Slot slot = slots.get(im);

                // skip if empty
                if (slot.isEmpty())
                    continue;

                // calculate bounds
                tlx = Math.min(x, tlx);
                tly = Math.min(y, tly);
                brx = Math.max(x, brx);
                bry = Math.max(y, bry);
            }
        }

        int dw = brx - tlx;

        // apply changes
        int l = slots.size();
        List<Slot> slotsFinal = new ArrayList<>(l);
        for (int x = tlx; x < brx; x++)
            for (int y = tly; y < tlx; y++)
                slotsFinal.add(slots.get(flatten(x, y)));

        // create matrix
        CraftMatrix matrix = new CraftMatrix();
        matrix.root        = this;
        matrix.output      = this.output;
        matrix.taggedSlots = this.taggedSlots;

        matrix.input = SlotContainer.of(slotsFinal);

        matrix.size  = slotsFinal.size();
        matrix.width = dw;

        // return
        return matrix;
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
