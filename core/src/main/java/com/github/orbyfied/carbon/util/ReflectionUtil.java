package com.github.orbyfied.carbon.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ReflectionUtil {

    public static Class<?> getCallerClass(int off) {
        try {
            return Class.forName(getCallerFrame(off).getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static StackTraceElement getCallerFrame(int off) {
        StackTraceElement[] elem;
        try {
            throw new Exception();
        } catch (Exception e) {
            elem = e.getStackTrace();
        }
        return elem[1 + off];
    }

    public static StackTraceElement getCallerFrame(int off, Predicate<StackTraceElement> pred) {
        StackTraceElement[] elems;
        try {
            throw new Exception();
        } catch (Exception e) {
            elems = e.getStackTrace();
        }
        int l = elems.length;
        StackTraceElement element;
        for (int i = 1 + off; i < l; i++)
            if (pred.test(element = elems[i]))
                return element;
        return elems[elems.length - 1];
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

    public static void printParentTree(Class<?> klass) {
        ReflectionUtil.walkParents(klass, null, (d, c) -> {
            System.out.println("@ depth " + d + ": " + "  ".repeat(d) + "|- " + c);
        });
    }

    public static void printCallTree() {
        StackTraceElement[] elements;
        try {
            throw new Exception();
        } catch (Exception e) {
            elements = e.getStackTrace();
        }
        System.out.println("+ CALLED: " + elements[1]);
        for (int i = 2; i < elements.length; i++)
            System.out.println("| " + elements[i]);
    }

    public static Class<?> getClassSafe(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getDeclaredFieldSafe(Class<?> klass, String name) {
        Objects.requireNonNull(klass);
        try {
            Field f = klass.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getDeclaredMethodSafe(Class<?> klass, String name,
                                               Class... argTypes) {
        Objects.requireNonNull(klass);
        try {
            Method m = klass.getDeclaredMethod(name, argTypes);
            m.setAccessible(true);
            return m;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Constructor<?> getDeclaredConstructorSafe(Class<?> klass,
                                                            Class... argTypes) {
        Objects.requireNonNull(klass);
        try {
            Constructor<?> c = klass.getDeclaredConstructor(argTypes);
            c.setAccessible(true);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T queryFieldSafe(Object on, Field f) {
        try {
            return (T) f.get(on);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setFieldSafe(Object on, Field f, Object val) {
        try {
            f.set(on, val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeSafe(Method m, Object on, Object... args) {
        try {
            return (T) m.invoke(on, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Constructor<?> c, Object... args) {
        try {
            return (T) c.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
