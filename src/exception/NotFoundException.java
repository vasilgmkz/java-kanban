package exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(final StringBuilder messages) {
        super(messages.toString());
    }
}
