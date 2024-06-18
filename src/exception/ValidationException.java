package exception;

public class ValidationException extends RuntimeException {
    public ValidationException(final StringBuilder messages) {
        super(messages.toString());
    }
}
