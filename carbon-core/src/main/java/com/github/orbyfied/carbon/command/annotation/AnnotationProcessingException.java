package com.github.orbyfied.carbon.command.annotation;

public class AnnotationProcessingException extends RuntimeException {

    public AnnotationProcessingException() { }

    public AnnotationProcessingException(String message) {
        super(message);
    }

    public AnnotationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnnotationProcessingException(Throwable cause) {
        super(cause);
    }

    public AnnotationProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
