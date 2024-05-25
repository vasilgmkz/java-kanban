package exception;

import java.io.IOException;

public class ManagerSaveException extends IOException {
    private IOException e;

    public ManagerSaveException() {
    }

    public ManagerSaveException(String message, IOException e) {
        super(message);
        this.e = e;
    }

    public String getDetailMessage() {
        return getMessage() + e;
    }
}
