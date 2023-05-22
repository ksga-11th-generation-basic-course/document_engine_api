package kh.com.kshrd.docengine.exceptions;

public class NotOwnerException extends RuntimeException{
    public NotOwnerException(String message) {
        super(message);
    }
}
