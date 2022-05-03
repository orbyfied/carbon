package net.orbyfied.carbon.util.math;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Predicate;

public interface Range<N extends Number> extends Collect<N> {

    Range<N> redefine(N min, N max);

    ///////////////////////////////////////////

    abstract class NumberIterator<N extends Number> implements Iterator<N> {

        private final Predicate<N> hasNext;

        public N num;

        protected NumberIterator(N init, Predicate<N> hasNext) {
            this.num = init;
            this.hasNext = hasNext;
        }

        @Override
        public boolean hasNext() {
            return hasNext.test(num);
        }

    }

    static DoubleRange doubles(double min, double max) {
        return new DoubleRange().redefine(min, max);
    }

    static DoubleRange doubles(double max) {
        return doubles(0, max);
    }

    final class DoubleRange implements Range<Double> {

        private Double min;
        private Double max;

        @Override
        public Double getMin() {
            return min;
        }

        @Override
        public Double getMax() {
            return max;
        }

        @Override
        public boolean has(Double num) {
            return num >= min && num <= max;
        }

        @Override
        public DoubleRange redefine(Double min, Double max) {
            this.min = min;
            this.max = max;
            return this;
        }

        @NotNull
        @Override
        public Iterator<Double> iterator() {
            return new NumberIterator<>(min, n -> n < max) {
                @Override
                public Double next() {
                    return num++;
                }
            };
        }

    }

}
