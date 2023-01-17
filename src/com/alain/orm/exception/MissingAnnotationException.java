package com.alain.orm.exception;

public class MissingAnnotationException extends Exception {
    public MissingAnnotationException() {
        super("ERROR: This class must be annoted");
    }
}
