package com.github.orbyfied.carbon.util;

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

}
