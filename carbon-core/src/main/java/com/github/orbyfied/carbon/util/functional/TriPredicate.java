package com.github.orbyfied.carbon.util.functional;

@FunctionalInterface
public interface TriPredicate<A, B, C> {

    boolean test(A a, B b, C c);

}
