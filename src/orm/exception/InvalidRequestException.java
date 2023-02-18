package orm.exception;

public class InvalidRequestException extends Exception {
    
    public InvalidRequestException() {
        super("ERROR: Invalid request ! Please check your syntax");
    }
}
