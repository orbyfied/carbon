package net.orbyfied.carbon.util.data;

import java.util.Objects;

public class Pair<A, B> {

    A a;
    B b;

    public Pair() { }

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public Pair<A, B> setFirst(A a) {
        this.a = a;
        return this;
    }

    public Pair<A, B> setSecond(B b) {
        this.b = b;
        return this;
    }

    public A getFirst() {
        return a;
    }

    public B getSecond() {
        return b;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

}
