package exception;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    private IOException e;

    public ManagerSaveException() {
    }

    public ManagerSaveException(String message) {
        super(message);
    }
}
