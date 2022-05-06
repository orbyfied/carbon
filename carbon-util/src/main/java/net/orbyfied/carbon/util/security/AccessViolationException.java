package net.orbyfied.carbon.util.security;

public class AccessViolationException extends RuntimeException {

    public AccessViolationException() { }

    public AccessViolationException(String message) {
        super(message);
    }

    public AccessViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessViolationException(Throwable cause) {
        super(cause);
    }

    public AccessViolationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
