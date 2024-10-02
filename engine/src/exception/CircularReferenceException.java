package exception;

public class CircularReferenceException extends RuntimeException{
    public CircularReferenceException(String message){
        super(message);
    }
}
