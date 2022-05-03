package net.orbyfied.carbon.crafting.match;

public class RecipeDimensions {

    /**
     * The sizes in all dimensions.
     */
    protected int[] sizes;

    /**
     * The dimension
     */
    protected byte dim;

    public RecipeDimensions(int dim) {
        this.dim   = (byte)dim;
        this.sizes = new int[dim];
    }

    public byte dimension() {
        return dim;
    }

    public int[] sizes() {
        return sizes;
    }

    public RecipeDimensions sizes(int off, int... vs) {
        System.arraycopy(vs, off, this.sizes, 0, this.sizes.length - off);
        return this;
    }

    public RecipeDimensions sized(int... vs) {
        return sizes(0, vs);
    }

    public int size(int a) {
        return sizes[a];
    }

    public RecipeDimensions size(int a, short l) {
        sizes[a] = l;
        return this;
    }

    public int flatten(int... pos) {
        int acc = pos[0];
        for (int i = 1, j = 0; i < pos.length && j < sizes.length; i++, j++) {
            acc += pos[i] * sizes[j];
        }

        return acc;
    }

}
