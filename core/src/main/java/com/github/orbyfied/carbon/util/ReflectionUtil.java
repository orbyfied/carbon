package com.github.orbyfied.carbon.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
                                   Predicate<Class<?>> pred,
                                   Consumer<Class<?>> consumer) {
        walkParents(klass, pred, (depth, c) -> consumer.accept(c));
    }

    public static void walkParents(Class<?> klass,
                                   Predicate<Class<?>> pred,
                                   BiConsumer<Integer, Class<?>> consumer) {
        internalWalkParents(klass, pred, consumer, 0);
    }

    public static void internalWalkParents(Class<?> klass,
                                           Predicate<Class<?>> pred,
                                           BiConsumer<Integer, Class<?>> consumer,
                                           int depth) {
        try {
            if (pred != null && !pred.test(klass)) return;
            consumer.accept(depth, klass);
            if (klass.getSuperclass() != Object.class && klass.getSuperclass() != null)
                internalWalkParents(klass.getSuperclass(), pred, consumer, depth + 1);
            for (Class<?> c : klass.getInterfaces())
                internalWalkParents(c, pred, consumer, depth + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
