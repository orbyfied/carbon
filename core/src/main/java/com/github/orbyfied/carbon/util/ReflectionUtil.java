package com.github.orbyfied.carbon.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ReflectionUtil {

    public static Class<?> getCallerClass(int off) {
        StackTraceElement[] elem;
        try {
            throw new Exception();
        } catch (Exception e) {
            elem = e.getStackTrace();
        }
        try {
            return Class.forName(elem[1 + off].getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void walkParents(Class<?> klass,
                                   Consumer<Class<?>> consumer) {
        walkParents(klass, (depth, c) -> consumer.accept(c));
    }

    public static void walkParents(Class<?> klass,
                                   BiConsumer<Integer, Class<?>> consumer) {
        internalWalkParents(klass, consumer, 0);
    }

    public static void internalWalkParents(Class<?> klass,
                                           BiConsumer<Integer, Class<?>> consumer,
                                           int depth) {
        try {
            consumer.accept(depth, klass);
            if (klass.getSuperclass() != Object.class && klass.getSuperclass() != null)
                internalWalkParents(klass.getSuperclass(), consumer, depth + 1);
            for (Class<?> c : klass.getInterfaces())
                internalWalkParents(c, consumer, depth + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
