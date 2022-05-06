package net.orbyfied.carbon.util.security;

public interface AccessValidator {

    boolean check(StackTraceElement[] elements, StackTraceElement accessCaller);

    default boolean test(int off) {
        return Access.checkAccess(off + 1 /* this frame */, this);
    }

    default void require(int off) {
        Access.assertAccess(off + 1 /* this frame */, this);
    }

    //////////////////////////////////////////////////

    static AccessValidator topInPackage(String pkg) {
        final String pref = pkg + ".";
        return (elements, accessCaller) -> elements[0].getClassName().startsWith(pref);
    }

}
