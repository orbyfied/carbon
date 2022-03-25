package com.github.orbyfied.carbon.util;

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
        try {
            consumer.accept(klass);
            if (klass.getSuperclass() != Object.class)
                walkParents(klass.getSuperclass(), consumer);
            for (Class<?> c : klass.getInterfaces())
                walkParents(c, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
