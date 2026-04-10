package exceptions;

public class InvalidCredentialException extends Exception {
    public InvalidCredentialException(String message) {
        super(message);//we pass the message to java's built_in Exception class (the super class)

    }
}
