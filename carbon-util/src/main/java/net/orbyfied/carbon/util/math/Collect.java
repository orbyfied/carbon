package net.orbyfied.carbon.util.math;

/**
 * Represents a collection of numbers.
 * Has a minimum and a maximum.
 */
public interface Collect<N extends Number> extends Iterable<N> {

    N getMin();

    N getMax();

    boolean has(N num);

}
