package orm.exception;

public class InvalidRequestException extends Exception {

    public InvalidRequestException() {
        super("ERROR: Unknown query request ! Please check your syntax !");
    }
}
