package exception;

public class InvalidSheetException extends RuntimeException{
    public InvalidSheetException(String message) {
        super(message);
    }
}
