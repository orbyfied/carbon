package net.orbyfied.carbon.util.security;

public class Access {

    /**
     * Checks if the frame with off + 1 has
     * permission following the validator
     * supplied.
     * @param off The offset.
     * @param validator The validator.
     * @return If the access check returned true.
     */
    public static boolean checkAccess(int off, AccessValidator validator) {
        // get stack trace
        StackTraceElement[] elements;
        try {
            throw new Exception();
        } catch (Exception e) { elements = e.getStackTrace(); }

        // create new list with offset
        int offw = off + 1 /* for this frame */;
        StackTraceElement caller          = elements[off];
        StackTraceElement[] finalElements = new StackTraceElement[elements.length - offw];
        System.arraycopy(elements, offw, finalElements, 0, finalElements.length);

        // validate
        return validator.check(finalElements, caller);
    }

    public static void assertAccess(int off, AccessValidator validator) {
        if (!checkAccess(off + 1 /* this frame */, validator))
            throw new AccessViolationException("");
    }

}
