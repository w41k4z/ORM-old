package com.alain.orm.exception;

public class InvalidColumnCountException extends Exception {
    public InvalidColumnCountException() {
        super("ERROR: The number of column set and the actual column mismatched");
    }
}
