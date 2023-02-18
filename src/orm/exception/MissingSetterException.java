package orm.exception;

public class MissingSetterException extends Exception {
    public MissingSetterException() {
        super("ERROR: Every column field must have a string setter");
    }
}
