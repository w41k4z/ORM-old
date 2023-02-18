package orm.exception;

public class PrimaryKeyCountException extends Exception {
    public PrimaryKeyCountException() {
        super("ERROR: The number of primary key for a table can not be greater than 1");
    }
}
